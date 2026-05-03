package com.growup.gateway.config;

import com.growup.gateway.security.JwtAuthenticationConverter;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * Configuración de Seguridad para API Gateway.
 *
 * Configura autenticación JWT con endpoints públicos para rutas de autenticación.
 * Utiliza validación local de JWT con clave secreta (no JWK Set URI).
 *
 * SOLUCIÓN PARA ENDPOINTS PÚBLICOS:
 * Creamos DOS cadenas de filtros de seguridad:
 * 1. Cadena pública: Maneja /api/v1/auth/** sin validación JWT
 * 2. Cadena protegida: Maneja /api/v1/** con validación JWT
 *
 * Esto asegura que las peticiones a endpoints de autenticación NUNCA pasen por
 * el filtro JWT, evitando así errores 401.
 *
 * @author Equipo GrowUp
 * @version 3.0.0
 */
@Configuration
@EnableWebFluxSecurity
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {

    /** Convertidor JWT para extraer roles de los tokens. */
    private final JwtAuthenticationConverter jwtAuthConverter;

    /** Clave secreta JWT para validación de tokens. */
    @Value("${spring.security.oauth2.resourceserver.jwt.secret}")
    private String jwtSecret;

    /**
     * Construye SecurityConfig con las dependencias requeridas.
     *
     * @param converter convertidor para autenticación JWT
     */
    // public SecurityConfig(
    //     final JwtAuthenticationConverter converter) {
    //     this.jwtAuthConverter = converter;
    // }

    /**
     * CADENA DE SEGURIDAD PÚBLICA - Sin validación JWT.
     * Maneja: /api/v1/auth/** y /actuator/health
     *
     * @param http ServerHttpSecurity a configurar
     * @return SecurityWebFilterChain configurado para endpoints públicos
     */
    @Bean
    public SecurityWebFilterChain publicSecurityWebFilterChain(
        final ServerHttpSecurity http) {
        log.info("Configurando SecurityWebFilterChain PÚBLICO (sin JWT)");

        return http
            .securityMatcher(ServerWebExchangeMatchers.pathMatchers(
                "/api/v1/auth/**",
                "/actuator/health"
            ))
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchanges -> exchanges
                // Todas las rutas coincidentes se permiten sin autenticación
                .anyExchange().permitAll()
            )
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .build();
    }

    /**
     * CADENA DE SEGURIDAD PROTEGIDA - Con validación JWT.
     * Maneja: Todos los demás endpoints /api/v1/**
     *
     * @param http ServerHttpSecurity a configurar
     * @return SecurityWebFilterChain configurado para endpoints protegidos
     */
    @Bean
    public SecurityWebFilterChain protectedSecurityWebFilterChain(
        final ServerHttpSecurity http) {
        log.info("Configurando SecurityWebFilterChain PROTEGIDO (con JWT)");

        return http
            .securityMatcher(ServerWebExchangeMatchers.pathMatchers("/api/v1/**"))
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchanges -> exchanges
                // Todas las rutas coincidentes requieren autenticación
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtDecoder(jwtDecoder()) // ✅ CORRECTO: jwtDecoder() para WebFlux
                    .jwtAuthenticationConverter(jwtAuthConverter))
            )
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .build();
    }

    /**
     * Crea Reactive JWT Decoder usando clave secreta para validación local.
     * Debe coincidir con el secreto usado en auth-service para la
     * generación de tokens.
     *
     * @return ReactiveJwtDecoder configurado
     */
    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        log.info("Creando Reactive JWT Decoder con clave secreta");
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec secretKeySpec = new SecretKeySpec(
            keyBytes, "HmacSHA256");
        return NimbusReactiveJwtDecoder.withSecretKey(secretKeySpec).build();
    }
}
