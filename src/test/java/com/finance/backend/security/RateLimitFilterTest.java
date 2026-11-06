package com.finance.backend.security;

import com.github.benmanes.caffeine.cache.Ticker;

import io.github.bucket4j.TimeMeter;

import jakarta.servlet.FilterChain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RateLimitFilterTest {

        private final MutableTime time = new MutableTime();
        private final RateLimitFilter filter = new RateLimitFilter(
                        10,
                        Duration.ofMinutes(10),
                        time,
                        time);

        @AfterEach
        void clearSecurityContext() {
                SecurityContextHolder.clearContext();
        }

        @Test
        void sameInternalUserIdSharesTheSameUserBucket() throws Exception {
                for (int request = 0; request < 60; request++) {
                        assertEquals(200, request(filter, user(25L, "first@example.com", "USER")).status());
                }

                assertEquals(429, request(filter, user(25L, "renamed@example.com", "USER")).status());
                assertEquals(1, filter.estimatedBucketCount());
        }

        @Test
        void concurrentFirstRequestsStillCreateOneStableBucket() throws Exception {
                RateLimitFilter concurrentFilter = new RateLimitFilter(
                                100,
                                Duration.ofMinutes(10),
                                time,
                                time);
                var authentication = user(26L, "parallel@example.com", "USER");
                ExecutorService executor = Executors.newFixedThreadPool(64);
                CountDownLatch startRequests = new CountDownLatch(1);

                try {
                        List<Future<Integer>> requests = java.util.stream.IntStream.range(0, 64)
                                        .mapToObj(index -> executor.submit(() -> {
                                                if (!startRequests.await(3, TimeUnit.SECONDS)) {
                                                        throw new IllegalStateException("Concurrent test did not start");
                                                }
                                                return request(concurrentFilter, authentication).status();
                                        }))
                                        .toList();
                        startRequests.countDown();

                        int accepted = 0;
                        int limited = 0;
                        for (Future<Integer> request : requests) {
                                int status = request.get(5, TimeUnit.SECONDS);
                                accepted += status == 200 ? 1 : 0;
                                limited += status == 429 ? 1 : 0;
                        }

                        assertEquals(60, accepted);
                        assertEquals(4, limited);
                        assertEquals(1, concurrentFilter.estimatedBucketCount());
                } finally {
                        executor.shutdownNow();
                        assertTrue(executor.awaitTermination(3, TimeUnit.SECONDS));
                }
        }

        @Test
        void normalUserCanBurstSixtyAndRecoversTwoTokensPerSecond() throws Exception {
                var authentication = user(7L, "user@example.com", "USER");
                for (int request = 0; request < 60; request++) {
                        assertEquals(200, request(filter, authentication).status());
                }
                assertEquals(429, request(filter, authentication).status());

                time.advance(Duration.ofMillis(500));
                assertEquals(200, request(filter, authentication).status());
        }

        @Test
        void administratorCanBurstOneHundredTwentyButRemainsLimited() throws Exception {
                var adminAuthentication = user(8L, "etl-admin@example.com", "ADMIN");
                for (int request = 0; request < 120; request++) {
                        assertEquals(200, request(filter, adminAuthentication).status());
                }
                assertEquals(429, request(filter, adminAuthentication).status());

                time.advance(Duration.ofMillis(50));
                assertEquals(200, request(filter, adminAuthentication).status());
        }

        @Test
        void localEtlPolicyIsLimitedToEntryPostsAndSupportsLargeImports() throws Exception {
                RateLimitFilter localFilter = new RateLimitFilter(
                                10,
                                Duration.ofMinutes(10),
                                time,
                                time,
                                true);
                var authentication = user(80L, "local-etl@example.com", "ADMIN");

                for (int request = 0; request < 1_000; request++) {
                        assertEquals(200, request(
                                        localFilter,
                                        authentication,
                                        "POST",
                                        "/api/statement-entries").status());
                }
                assertEquals(429, request(
                                localFilter,
                                authentication,
                                "POST",
                                "/api/statement-entries").status());

                time.advance(Duration.ofMillis(2));
                assertEquals(200, request(
                                localFilter,
                                authentication,
                                "POST",
                                "/api/statement-entries").status());

                for (int request = 0; request < 120; request++) {
                        assertEquals(200, request(localFilter, authentication).status());
                }
                assertEquals(429, request(localFilter, authentication).status());
        }

        @Test
        void expiredInactiveBucketsAreRemovedAfterTenMinutesSinceLastAccess() throws Exception {
                var authentication = user(9L, "idle@example.com", "USER");
                request(filter, authentication);
                assertEquals(1, filter.estimatedBucketCount());

                time.advance(Duration.ofMinutes(9));
                request(filter, authentication);
                time.advance(Duration.ofMinutes(9).plusSeconds(59));
                assertEquals(1, filter.estimatedBucketCount());

                time.advance(Duration.ofSeconds(2));
                assertEquals(0, filter.estimatedBucketCount());
        }

        @Test
        void cacheMaximumUsesSharedOverflowBudgetInsteadOfResettingBucketsForNewKeys() throws Exception {
                RateLimitFilter smallCache = new RateLimitFilter(2, Duration.ofMinutes(10), time, time);

                assertEquals(200, request(smallCache, user(1L, "a@example.com", "USER")).status());
                assertEquals(200, request(smallCache, user(2L, "b@example.com", "USER")).status());
                assertEquals(2, smallCache.estimatedBucketCount());

                for (long id = 3; id <= 62; id++) {
                        assertEquals(200, request(smallCache, user(id, "u" + id + "@example.com", "USER")).status());
                }
                assertEquals(429, request(smallCache, user(63L, "new@example.com", "USER")).status());
                assertEquals(2, smallCache.estimatedBucketCount());
        }

        @Test
        void limitsAnonymousAddressNormalizationAndIgnoresForwardedHeaders() throws Exception {
                AtomicInteger chainInvocations = new AtomicInteger();

                SecurityContextHolder.clearContext();
                var mappedAddressRequest = new MockHttpServletRequest("GET", "/api/public");
                mappedAddressRequest.setRemoteAddr("::ffff:192.0.2.1");
                mappedAddressRequest.addHeader("X-Forwarded-For", "203.0.113.99");
                var mappedAddressResponse = new MockHttpServletResponse();
                filter.doFilter(
                                mappedAddressRequest,
                                mappedAddressResponse,
                                incrementingChain(chainInvocations));

                var ipv4Request = new MockHttpServletRequest("GET", "/api/public");
                ipv4Request.setRemoteAddr("192.0.2.1");
                ipv4Request.addHeader("X-Forwarded-For", "198.51.100.2");
                var ipv4Response = new MockHttpServletResponse();
                filter.doFilter(ipv4Request, ipv4Response, incrementingChain(chainInvocations));

                assertEquals(200, mappedAddressResponse.getStatus());
                assertEquals(200, ipv4Response.getStatus());
                assertEquals(2, chainInvocations.get());
                assertEquals(1, filter.estimatedBucketCount());
        }

        @Test
        void nestedRegistrationDoesNotRunTheOncePerRequestFilterTwice() throws Exception {
                AtomicInteger chainInvocations = new AtomicInteger();
                var authentication = user(10L, "once@example.com", "USER");
                SecurityContextHolder.setContext(context(authentication));
                var request = new MockHttpServletRequest("GET", "/api/cards");
                var response = new MockHttpServletResponse();
                FilterChain nestedRegistration = (nestedRequest, nestedResponse) -> filter.doFilter(
                                nestedRequest,
                                nestedResponse,
                                incrementingChain(chainInvocations));

                filter.doFilter(request, response, nestedRegistration);

                assertEquals(1, chainInvocations.get());
        }

        @Test
        void servletContainerRegistrationIsDisabledWhileSecurityChainOwnsTheFilter() {
                FilterRegistrationBean<RateLimitFilter> registration = new SecurityConfig()
                                .rateLimitFilterServletRegistration(filter);

                assertFalse(registration.isEnabled());
        }

        @Test
        void rateLimitResponseKeepsHttp429AndRetryInformation() throws Exception {
                var authentication = user(11L, "limited@example.com", "USER");
                for (int request = 0; request < 60; request++) {
                        request(filter, authentication);
                }

                RequestResult limited = request(filter, authentication);
                assertEquals(429, limited.status());
                assertTrue(limited.body().contains("Too many requests"));
                assertEquals("1", limited.retryAfter());
        }

        @Test
        void localEtlConcurrentRequestsAreCappedEvenWhenTokenBudgetRemains() throws Exception {
                RateLimitFilter adminFilter = new RateLimitFilter(
                                10,
                                Duration.ofMinutes(10),
                                time,
                                time,
                                true);
                ExecutorService executor = Executors.newFixedThreadPool(4);
                CountDownLatch requestsEntered = new CountDownLatch(4);
                CountDownLatch releaseRequests = new CountDownLatch(1);

                try {
                        List<Future<Integer>> activeRequests = java.util.stream.LongStream.range(0, 4)
                                        .mapToObj(id -> executor.submit(() -> {
                                                SecurityContextHolder.setContext(context(
                                                                user(id + 100, "etl" + id + "@example.com", "ADMIN")));
                                                try {
                                                        var request = new MockHttpServletRequest("POST", "/api/statement-entries");
                                                        request.setServletPath("/api/statement-entries");
                                                        var response = new MockHttpServletResponse();
                                                        adminFilter.doFilter(request, response, (req, res) -> {
                                                                requestsEntered.countDown();
                                                                try {
                                                                        if (!releaseRequests.await(3, TimeUnit.SECONDS)) {
                                                                                throw new IOException("Test did not release held request");
                                                                        }
                                                                } catch (InterruptedException exception) {
                                                                        Thread.currentThread().interrupt();
                                                                        throw new IOException("Test request was interrupted", exception);
                                                                }
                                                        });
                                                        return response.getStatus();
                                                } finally {
                                                        SecurityContextHolder.clearContext();
                                                }
                                        }))
                                        .toList();

                        assertTrue(requestsEntered.await(3, TimeUnit.SECONDS));
                        assertEquals(429, request(
                                        adminFilter,
                                        user(999L, "extra-etl@example.com", "ADMIN"),
                                        "POST",
                                        "/api/statement-entries").status());

                        releaseRequests.countDown();
                        for (Future<Integer> activeRequest : activeRequests) {
                                assertEquals(200, activeRequest.get(3, TimeUnit.SECONDS));
                        }
                } finally {
                        releaseRequests.countDown();
                        executor.shutdownNow();
                        assertTrue(executor.awaitTermination(3, TimeUnit.SECONDS));
                }
        }

        private RequestResult request(RateLimitFilter targetFilter, UsernamePasswordAuthenticationToken authentication)
                        throws Exception {
                return request(targetFilter, authentication, "GET", "/api/test");
        }

        private RequestResult request(
                        RateLimitFilter targetFilter,
                        UsernamePasswordAuthenticationToken authentication,
                        String method,
                        String path)
                        throws Exception {
                SecurityContextHolder.setContext(context(authentication));
                var request = new MockHttpServletRequest(method, path);
                request.setServletPath(path);
                request.setRemoteAddr("192.0.2.10");
                var response = new MockHttpServletResponse();
                AtomicInteger chainInvocations = new AtomicInteger();

                try {
                        targetFilter.doFilter(request, response, incrementingChain(chainInvocations));
                        return new RequestResult(
                                        response.getStatus(),
                                        chainInvocations.get(),
                                        response.getContentAsString(),
                                        response.getHeader("Retry-After"));
                } finally {
                        SecurityContextHolder.clearContext();
                }
        }

        private FilterChain incrementingChain(AtomicInteger invocations) {
                return (request, response) -> invocations.incrementAndGet();
        }

        private SecurityContext context(UsernamePasswordAuthenticationToken authentication) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                return context;
        }

        private UsernamePasswordAuthenticationToken user(Long id, String email, String role) {
                var delegate = User.withUsername(email)
                                .password("not-used")
                                .authorities(new SimpleGrantedAuthority("ROLE_" + role))
                                .build();
                var principal = new FinanceUserDetails(id, delegate);
                return new UsernamePasswordAuthenticationToken(
                                principal,
                                null,
                                principal.getAuthorities());
        }

        private record RequestResult(int status, int chainInvocations, String body, String retryAfter) {
        }

        private static final class MutableTime implements Ticker, TimeMeter {
                private final AtomicLong nanos = new AtomicLong();

                @Override
                public long read() {
                        return nanos.get();
                }

                @Override
                public long currentTimeNanos() {
                        return nanos.get();
                }

                @Override
                public boolean isWallClockBased() {
                        return false;
                }

                void advance(Duration duration) {
                        nanos.addAndGet(duration.toNanos());
                }
        }
}
