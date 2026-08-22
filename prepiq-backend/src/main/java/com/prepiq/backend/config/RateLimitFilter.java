package com.prepiq.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Order(1)
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_WINDOW = 20;
    private static final long WINDOW_MS = 60_000;

    private final ConcurrentHashMap<String, RequestCounter> counters = new ConcurrentHashMap<>();

    private static class RequestCounter {
        AtomicInteger count = new AtomicInteger(0);
        long windowStart = System.currentTimeMillis();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        boolean isAuthEndpoint = path.startsWith("/api/auth/");

        if (isAuthEndpoint) {
            String clientIp = getClientIp(request);
            RequestCounter counter = counters.computeIfAbsent(clientIp, k -> new RequestCounter());

            long now = System.currentTimeMillis();
            if (now - counter.windowStart > WINDOW_MS) {
                counter.count.set(0);
                counter.windowStart = now;
            }

            if (counter.count.incrementAndGet() > MAX_REQUESTS_PER_WINDOW) {
                response.setStatus(429);
                response.setContentType("application/json");
                response.getWriter().write("{\"status\":429,\"error\":\"Too Many Requests\",\"message\":\"Too many attempts. Please wait a minute and try again.\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}