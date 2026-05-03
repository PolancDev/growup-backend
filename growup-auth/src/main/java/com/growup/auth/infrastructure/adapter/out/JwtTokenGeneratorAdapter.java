package com.growup.auth.infrastructure.adapter.out;

import com.growup.auth.domain.model.User;
import com.growup.auth.domain.port.out.TokenGeneratorPort;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

/**
 * Adaptador de Infraestructura para la generación de Tokens JWT.
 * Implementa el puerto de salida TokenGeneratorPort.
 */
@Component
@Slf4j
public class JwtTokenGeneratorAdapter implements TokenGeneratorPort {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}") // 24 horas por defecto
    private long jwtExpirationMs;

    @Override
    public String generateToken(User user) {
        try {
            // Asegurar que el secreto tenga al menos 256 bits (32 bytes)
            // Si es más corto, JJWT rellenará o dará error
            byte[] apiKeySecretBytes = jwtSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            Key signingKey = io.jsonwebtoken.security.Keys.hmacShaKeyFor(apiKeySecretBytes);

            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

            String token = Jwts.builder()
                    .setSubject(user.getId().toString())
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .claim("email", user.getEmail())
                    .claim("name", user.getName())
                    .claim("role", user.getRole() != null ? user.getRole().name() : com.growup.common.domain.model.enums.Role.STUDENT.name())
                    .signWith(signingKey, SignatureAlgorithm.HS256)
                    .compact();

            log.info("GrowUp-Log: JwtTokenGeneratorAdapter - Token JWT generado para usuario: {}", user.getEmail());
            return token;

        } catch (Exception e) {
            log.error("GrowUp-Log: JwtTokenGeneratorAdapter - Error al generar token JWT", e);
            throw new RuntimeException("Error al generar token de autenticación", e);
        }
    }
}
