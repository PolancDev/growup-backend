package com.growup.course.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Configuración de seguridad para Course Service.
 * Valida JWT tokens HMAC (HS256) generados por el auth-service interno de GrowUp.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${jwt.secret:GrowUpJWTSecret2026!@#$%^&*()_+SecureKey32Min}")
    private String jwtSecret;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Swagger/OpenAPI endpoints
                .requestMatchers("/swagger-ui/**", "/api-docs/**", "/swagger-ui.html").permitAll()
                // Health check
                .requestMatchers("/actuator/**").permitAll()
                // Course endpoints - GET publico para catalogo
                .requestMatchers(HttpMethod.GET, "/api/v1/courses", "/api/v1/courses/**").permitAll()
                // Escrituras requieren rol TEACHER o ADMIN
                .requestMatchers(HttpMethod.POST, "/api/v1/courses").hasAnyRole("TEACHER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/courses/**").hasAnyRole("TEACHER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/courses/**").hasAnyRole("TEACHER", "ADMIN")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .decoder(jwtDecoder())
                    .jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );

        return http.build();
    }

    /**
     * JwtDecoder que usa HMAC-SHA256 con el mismo secreto que el auth-service.
     * Esto permite validar tokens firmados internamente sin depender de Keycloak.
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] secretBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec secretKey = new SecretKeySpec(secretBytes, "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }

    /**
     * JWT Authentication Converter custom para tokens del auth-service de GrowUp.
     * El claim 'role' viene como String simple (ej: "ADMIN"), no como array.
     * Se debe convertir a GrantedAuthority con prefijo ROLE_.
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setPrincipalClaimName("sub");
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Object roleClaim = jwt.getClaim("role");
            if (roleClaim == null) {
                return Collections.emptyList();
            }
            // Maneja tanto String simple como lista de strings
            List<String> roles;
            if (roleClaim instanceof String) {
                roles = Collections.singletonList((String) roleClaim);
            } else if (roleClaim instanceof List) {
                roles = ((List<?>) roleClaim).stream()
                        .map(Object::toString)
                        .collect(Collectors.toList());
            } else {
                return Collections.emptyList();
            }
            return roles.stream()
                    .map(role -> "ROLE_" + role)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        });
        return jwtAuthenticationConverter;
    }
}
