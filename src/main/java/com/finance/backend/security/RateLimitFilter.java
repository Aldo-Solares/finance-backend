package com.finance.backend.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

        private static final long CAPACITY = 100;
        private static final long REFILL_TOKENS = 10022;
        private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

        @Override
        protected void doFilterInternal(
                        HttpServletRequest request,
                        HttpServletResponse response,
                        FilterChain filterChain)
                        throws ServletException, IOException {

                String key = getRequestKey(request);

                Bucket bucket = buckets.computeIfAbsent(
                                key,
                                ignored -> createBucket());

                if (!bucket.tryConsume(1)) {

                        response.setStatus(
                                        HttpStatus.TOO_MANY_REQUESTS.value());

                        response.setContentType(
                                        "application/json");

                        response.setCharacterEncoding(
                                        "UTF-8");

                        response.getWriter().write(
                                        """
                                                        {
                                                          "success": false,
                                                          "message": "Too many requests",
                                                          "data": null
                                                        }
                                                        """);

                        return;
                }

                filterChain.doFilter(
                                request,
                                response);
        }

        private Bucket createBucket() {

                Bandwidth limit = Bandwidth.builder()
                                .capacity(CAPACITY)
                                .refillGreedy(
                                                REFILL_TOKENS,
                                                Duration.ofMinutes(1))
                                .build();

                return Bucket.builder()
                                .addLimit(limit)
                                .build();
        }

        private String getRequestKey(
                        HttpServletRequest request) {

                Authentication authentication = SecurityContextHolder
                                .getContext()
                                .getAuthentication();

                if (authentication != null
                                && authentication.isAuthenticated()
                                && !"anonymousUser".equals(
                                                authentication.getName())) {

                        return authentication.getName();
                }

                return request.getRemoteAddr();
        }
}