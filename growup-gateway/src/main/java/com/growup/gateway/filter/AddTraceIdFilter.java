package com.growup.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Filtro global para añadir un Trace ID a las respuestas.
 */
//@Component  // DESHABILITADO PARA DIAGNÓSTICO
public final class AddTraceIdFilter implements GlobalFilter, Ordered {

    /**
     * Anade un Trace ID unico a la respuesta.
     *
     * @param exchange el intercambio del servidor
     * @param chain la cadena de filtros
     * @return Mono vacio cuando se completa el filtro
     */
    @Override
    public Mono<Void> filter(final ServerWebExchange exchange,
                             final GatewayFilterChain chain) {
        String traceId = UUID.randomUUID().toString();

        exchange.getResponse().getHeaders().add("X-Trace-Id", traceId);
        exchange.getAttributes().put("traceId", traceId);

        return chain.filter(exchange);
    }

    /**
     * Devuelve el orden del filtro.
     *
     * @return el orden del filtro
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
