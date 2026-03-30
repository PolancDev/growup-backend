package com.growup.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Gateway Configuration for CORS and global settings.
 * 
 * This configuration provides:
 * - CORS (Cross-Origin Resource Sharing) configuration for frontend integration
 * - Global filter ordering
 * - Gateway-specific settings
 * 
 * @author GrowUp Team
 * @version 1.0.0
 */
@Configuration
public class GatewayConfig {

    /**
     * Configures CORS for the API Gateway.
     * 
     * Allows cross-origin requests from the Angular frontend (localhost:4200)
     * and React frontend (localhost:3000).
     * 
     * @return CorsWebFilter bean
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Allowed origins (frontend applications)
        config.setAllowedOriginPatterns(List.of(
            "http://localhost:4200",
            "http://localhost:3000",
            "http://localhost:*",
            "http://127.0.0.1:*"
        ));
        
        // Allowed HTTP methods
        config.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        
        // Allowed headers
        config.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "X-User-Id",
            "X-User-Email",
            "X-User-Roles",
            "X-User-Name"
        ));
        
        // Headers exposed to clients
        config.setExposedHeaders(Arrays.asList(
            "Authorization",
            "X-User-Id",
            "X-User-Email",
            "X-User-Roles",
            "X-User-Name"
        ));
        
        // Allow credentials (cookies, authorization headers)
        config.setAllowCredentials(true);
        
        // Cache preflight response for 1 hour
        config.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsWebFilter(source);
    }
}