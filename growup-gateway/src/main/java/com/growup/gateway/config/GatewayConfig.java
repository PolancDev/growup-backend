package com.growup.gateway.config;

import org.springframework.context.annotation.Configuration;

/**
 * Configuración del Gateway para ajustes globales.
 *
 * El CORS se configura en application.yml mediante
 * spring.cloud.gateway.globalcors para evitar duplicación
 * de beans CorsWebFilter.
 *
 * @author Equipo GrowUp
 * @version 2.0.0
 */
@Configuration
public class GatewayConfig {
    // Configuración eliminada: El CORS ahora se maneja en
    // application.yml mediante spring.cloud.gateway.globalcors
    // para evitar conflictos
}
