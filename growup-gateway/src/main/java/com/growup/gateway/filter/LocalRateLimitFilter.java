package com.growup.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class LocalRateLimitFilter implements GlobalFilter, Ordered {

    private static final int MAX_REQUESTS_PER_MINUTE = 100;
    private static final long WINDOW_SIZE_MS = 60_000;

    private final Map<String, RateLimitEntry> rateLimitMap = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String clientIp = getClientIp(exchange);
        String key = clientIp + ":" + (Instant.now().toEpochMilli() / WINDOW_SIZE_MS);
        
        RateLimitEntry entry = rateLimitMap.computeIfAbsent(key, 
            k -> new RateLimitEntry());
        
        if (entry.count.incrementAndGet() > MAX_REQUESTS_PER_MINUTE) {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            exchange.getResponse().getHeaders().add("X-RateLimit-Remaining", "0");
            exchange.getResponse().getHeaders().add("X-RateLimit-Limit", String.valueOf(MAX_REQUESTS_PER_MINUTE));
            return exchange.getResponse().setComplete();
        }
        
        exchange.getResponse().getHeaders().add("X-RateLimit-Remaining", 
            String.valueOf(MAX_REQUESTS_PER_MINUTE - entry.count.get()));
        exchange.getResponse().getHeaders().add("X-RateLimit-Limit", String.valueOf(MAX_REQUESTS_PER_MINUTE));
        
        // Clean up old entries periodically
        cleanupOldEntries();
        
        return chain.filter(exchange);
    }

    private String getClientIp(ServerWebExchange exchange) {
        String xForwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
    }

    private void cleanupOldEntries() {
        long currentWindow = Instant.now().toEpochMilli() / WINDOW_SIZE_MS;
        rateLimitMap.entrySet().removeIf(entry -> {
            String[] parts = entry.getKey().split(":");
            return Long.parseLong(parts[1]) < currentWindow - 2;
        });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private static class RateLimitEntry {
        AtomicInteger count = new AtomicInteger(0);
    }
}