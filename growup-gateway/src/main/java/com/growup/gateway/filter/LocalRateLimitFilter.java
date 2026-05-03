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

/**
 * Filtro global para limitar la tasa de peticiones por IP.
 */
//@Component  // DESHABILITADO PARA DIAGNÓSTICO
public final class LocalRateLimitFilter implements GlobalFilter, Ordered {

    /** Maximo de peticiones por minuto. */
    private static final int MAX_PER_MINUTE = 100;

    /** Tamano de ventana en milisegundos. */
    private static final long WINDOW_MS = 60_000L;

    /** Mapa para almacenar el estado del rate limit. */
    private final Map<String, RateLimitEntry> rateLimitMap =
        new ConcurrentHashMap<>();

    /**
     * Filtra las peticiones aplicando rate limiting.
     *
     * @param exchange el intercambio del servidor
     * @param chain la cadena de filtros
     * @return Mono vacio cuando se completa el filtro
     */
    @Override
    public Mono<Void> filter(final ServerWebExchange exchange,
                               final GatewayFilterChain chain) {
        String clientIp = getClientIp(exchange);
        long window = Instant.now().toEpochMilli() / WINDOW_MS;
        String key = clientIp + ":" + window;

        RateLimitEntry entry = rateLimitMap.computeIfAbsent(key,
            k -> new RateLimitEntry());

        if (entry.getCount().incrementAndGet() > MAX_PER_MINUTE) {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            exchange.getResponse().getHeaders().add(
                "X-RateLimit-Remaining", "0");
            exchange.getResponse().getHeaders().add(
                "X-RateLimit-Limit",
                String.valueOf(MAX_PER_MINUTE));
            return exchange.getResponse().setComplete();
        }

        exchange.getResponse().getHeaders().add(
            "X-RateLimit-Remaining",
            String.valueOf(MAX_PER_MINUTE - entry.getCount().get()));
        exchange.getResponse().getHeaders().add(
            "X-RateLimit-Limit",
            String.valueOf(MAX_PER_MINUTE));

        cleanupOldEntries();
        return chain.filter(exchange);
    }

    /**
     * Obtiene la direccion IP del cliente.
     *
     * @param exchange el intercambio del servidor
     * @return direccion IP del cliente
     */
    private String getClientIp(final ServerWebExchange exchange) {
        String xForwardedFor = exchange.getRequest().getHeaders()
            .getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return exchange.getRequest().getRemoteAddress()
            .getAddress().getHostAddress();
    }

    /**
     * Limpia las entradas antiguas del mapa.
     */
    private void cleanupOldEntries() {
        long currentWindow = Instant.now().toEpochMilli() / WINDOW_MS;
        rateLimitMap.entrySet().removeIf(entry -> {
            String[] parts = entry.getKey().split(":");
            return Long.parseLong(parts[1]) < currentWindow - 2;
        });
    }

    /**
     * Devuelve el orden del filtro.
     *
     * @return el orden del filtro
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    /**
     * Entrada para el rate limit.
     */
    private static final class RateLimitEntry {
        /** Contador atomico. */
        private final AtomicInteger count = new AtomicInteger(0);

        /**
         * Obtiene el contador.
         *
         * @return el contador
         */
        public AtomicInteger getCount() {
            return count;
        }
    }
}
