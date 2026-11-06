package com.finance.backend.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;
import com.github.benmanes.caffeine.cache.Ticker;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.TimeMeter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.EnumMap;
import java.util.HexFormat;
import java.util.Map;
import java.util.concurrent.Semaphore;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

        static final int DEFAULT_MAX_ENTRIES = 10_000;
        static final Duration DEFAULT_IDLE_EXPIRATION = Duration.ofMinutes(10);

        private final Cache<String, Bucket> buckets;
        private final int maximumEntries;
        private final Object bucketCreationLock = new Object();
        private final TimeMeter timeMeter;
        private final boolean localEtlPolicyEnabled;
        private final Map<Policy, PolicyLimiters> policyLimiters = new EnumMap<>(Policy.class);

        @Autowired
        public RateLimitFilter(
                        @Value("${app.rate-limit.local-etl.enabled:false}") boolean localEtlPolicyEnabled) {
                this(
                                DEFAULT_MAX_ENTRIES,
                                DEFAULT_IDLE_EXPIRATION,
                                Ticker.systemTicker(),
                                TimeMeter.SYSTEM_NANOTIME,
                                localEtlPolicyEnabled);
        }

        RateLimitFilter(
                        int maximumEntries,
                        Duration idleExpiration,
                        Ticker ticker,
                        TimeMeter timeMeter) {
                this(maximumEntries, idleExpiration, ticker, timeMeter, false);
        }

        RateLimitFilter(
                        int maximumEntries,
                        Duration idleExpiration,
                        Ticker ticker,
                        TimeMeter timeMeter,
                        boolean localEtlPolicyEnabled) {

                if (maximumEntries < 1) {
                        throw new IllegalArgumentException("maximumEntries must be positive");
                }

                this.maximumEntries = maximumEntries;
                this.timeMeter = timeMeter;
                this.localEtlPolicyEnabled = localEtlPolicyEnabled;
                this.buckets = Caffeine.newBuilder()
                                .maximumSize(maximumEntries)
                                .expireAfterAccess(idleExpiration)
                                .scheduler(Scheduler.systemScheduler())
                                .ticker(ticker)
                                .build();

                for (Policy policy : Policy.values()) {
                        policyLimiters.put(policy, createPolicyLimiters(policy));
                }
        }

        @Override
        protected void doFilterInternal(
                        HttpServletRequest request,
                        HttpServletResponse response,
                        FilterChain filterChain)
                        throws ServletException, IOException {

                Authentication authentication = SecurityContextHolder
                                .getContext()
                                .getAuthentication();
                Policy policy = resolvePolicy(authentication, request);
                PolicyLimiters limiters = policyLimiters.get(policy);

                if (!limiters.concurrencyLimit().tryAcquire()) {
                        writeRateLimitResponse(response);
                        return;
                }

                try {
                        String key = resolveIdentityKey(request, authentication, policy);
                        Bucket identityBucket = getOrCreateBucket(key, policy, limiters);

                        if (!identityBucket.tryConsume(1)
                                        || !limiters.aggregateBucket().tryConsume(1)) {
                                writeRateLimitResponse(response);
                                return;
                        }

                        filterChain.doFilter(request, response);
                } finally {
                        limiters.concurrencyLimit().release();
                }
        }

        private Bucket getOrCreateBucket(String key, Policy policy, PolicyLimiters limiters) {
                Bucket bucket = buckets.getIfPresent(key);
                if (bucket != null) {
                        return bucket;
                }

                synchronized (bucketCreationLock) {
                        bucket = buckets.getIfPresent(key);
                        if (bucket != null) {
                                return bucket;
                        }

                        buckets.cleanUp();
                        if (buckets.estimatedSize() >= maximumEntries) {
                                return limiters.overflowBucket();
                        }

                        return buckets.get(key, ignored -> createBucket(
                                        policy.capacity,
                                        policy.refillPerMinute));
                }
        }

        private PolicyLimiters createPolicyLimiters(Policy policy) {
                return new PolicyLimiters(
                                createBucket(policy.capacity, policy.refillPerMinute),
                                createBucket(policy.aggregateCapacity, policy.aggregateRefillPerMinute),
                                new Semaphore(policy.maxConcurrentRequests));
        }

        private Bucket createBucket(long capacity, long refillPerMinute) {
                Bandwidth bandwidth = Bandwidth.builder()
                                .capacity(capacity)
                                .refillGreedy(refillPerMinute, Duration.ofMinutes(1))
                                .build();

                return Bucket.builder()
                                .withCustomTimePrecision(timeMeter)
                                .addLimit(bandwidth)
                                .build();
        }

        private record PolicyLimiters(
                        Bucket overflowBucket,
                        Bucket aggregateBucket,
                        Semaphore concurrencyLimit) {
        }

        private Policy resolvePolicy(Authentication authentication, HttpServletRequest request) {
                if (authentication == null
                                || !authentication.isAuthenticated()
                                || authentication instanceof AnonymousAuthenticationToken) {
                        return Policy.ANONYMOUS;
                }

                boolean isAdministrator = false;
                for (GrantedAuthority authority : authentication.getAuthorities()) {
                        if ("ROLE_ADMIN".equals(authority.getAuthority())) {
                                isAdministrator = true;
                                break;
                        }
                }

                if (!isAdministrator) {
                        return Policy.USER;
                }

                if (localEtlPolicyEnabled
                                && "POST".equalsIgnoreCase(request.getMethod())
                                && ("/api/statement-entries".equals(request.getServletPath())
                                                || "/api/etl/statement-entries/import"
                                                                .equals(request.getServletPath()))) {
                        return Policy.LOCAL_ETL;
                }

                return Policy.ADMIN;
        }

        private String resolveIdentityKey(
                        HttpServletRequest request,
                        Authentication authentication,
                        Policy policy) {

                if (policy == Policy.ANONYMOUS) {
                        return "anonymous:" + normalizeRemoteAddress(request.getRemoteAddr());
                }

                Object principal = authentication.getPrincipal();
                if (principal instanceof FinanceUserDetails userDetails
                                && userDetails.getUserId() != null) {
                        return "user:" + policy.name() + ":" + userDetails.getUserId();
                }

                String name = authentication.getName();
                return "principal:" + policy.name() + ":"
                                + (name == null || name.isBlank() ? "unknown" : name);
        }

        private String normalizeRemoteAddress(String remoteAddress) {
                if (remoteAddress == null || remoteAddress.isBlank()) {
                        return "unknown";
                }

                String candidate = remoteAddress.trim();
                if (!candidate.matches("(?i)[0-9a-f:.]+")) {
                        return "unknown";
                }

                try {
                        byte[] address = InetAddress.getByName(candidate).getAddress();
                        if (isIpv4MappedIpv6(address)) {
                                address = java.util.Arrays.copyOfRange(address, 12, 16);
                        }
                        return HexFormat.of().formatHex(address);
                } catch (UnknownHostException exception) {
                        return "unknown";
                }
        }

        private boolean isIpv4MappedIpv6(byte[] address) {
                if (address.length != 16) {
                        return false;
                }
                for (int index = 0; index < 10; index++) {
                        if (address[index] != 0) {
                                return false;
                        }
                }
                return address[10] == (byte) 0xff && address[11] == (byte) 0xff;
        }

        private void writeRateLimitResponse(HttpServletResponse response) throws IOException {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setHeader("Retry-After", "1");
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(
                                "{\"success\":false,\"message\":\"Too many requests\",\"data\":null}");
        }

        long estimatedBucketCount() {
                buckets.cleanUp();
                return buckets.estimatedSize();
        }

        private enum Policy {
                ANONYMOUS(60, 120, 300, 600, 16),
                USER(60, 120, 300, 3_000, 64),
                ADMIN(120, 1_200, 240, 2_400, 16),
                LOCAL_ETL(1_000, 30_000, 2_000, 60_000, 4);

                private final long capacity;
                private final long refillPerMinute;
                private final long aggregateCapacity;
                private final long aggregateRefillPerMinute;
                private final int maxConcurrentRequests;

                Policy(
                                long capacity,
                                long refillPerMinute,
                                long aggregateCapacity,
                                long aggregateRefillPerMinute,
                                int maxConcurrentRequests) {
                        this.capacity = capacity;
                        this.refillPerMinute = refillPerMinute;
                        this.aggregateCapacity = aggregateCapacity;
                        this.aggregateRefillPerMinute = aggregateRefillPerMinute;
                        this.maxConcurrentRequests = maxConcurrentRequests;
                }
        }
}
