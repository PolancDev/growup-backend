package com.growup.gateway;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
//import org.springframework.test.context.TestPropertySource;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * Utility class for generating test JWT tokens.
 * Provides methods to create tokens with different configurations
 * for testing JWT authentication in the gateway.
 */
public class JwtTestUtils {

    private static final String TEST_SECRET = "GrowUpSecretKeyForJWTTokenValidationMinimum256BitsRequired!";

    private JwtTestUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Returns the test JWT secret used in test configuration.
     */
    public static String getTestSecret() {
        return TEST_SECRET;
    }

    /**
     * Creates a SecretKey for signing test tokens.
     */
    public static SecretKey getSigningKey() {
        byte[] keyBytes = TEST_SECRET.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a valid JWT token with default claims.
     * 
     * @return A valid JWT token string
     */
    public static String generateValidToken() {
        return generateToken(
            UUID.randomUUID().toString(),  // subject (userId)
            "test@example.com",            // email
            "STUDENT",                      // role
            "Test User"                     // name
        );
    }

    /**
     * Generates a valid JWT token with custom claims.
     * 
     * @param userId The user ID (subject)
     * @param email The user's email
     * @param role The user's role
     * @param name The user's name
     * @return A valid JWT token string
     */
    public static String generateToken(String userId, String email, String role, String name) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("email", email)
                .claim("role", role)
                .claim("name", name)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Generates an expired JWT token.
     * 
     * @return An expired JWT token string
     */
    public static String generateExpiredToken() {
        return Jwts.builder()
                .setSubject(UUID.randomUUID().toString())
                .claim("email", "test@example.com")
                .claim("role", "STUDENT")
                .claim("name", "Test User")
                .setIssuedAt(new Date(System.currentTimeMillis() - 7200000))
                .setExpiration(new Date(System.currentTimeMillis() - 3600000)) // Expired 1 hour ago
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Generates a JWT token with invalid signature.
     * 
     * @return A JWT token with invalid signature
     */
    public static String generateTokenWithInvalidSignature() {
        SecretKey invalidKey = Keys.hmacShaKeyFor("InvalidSecretKeyForTestingPurposesOnly123!".getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
                .setSubject(UUID.randomUUID().toString())
                .claim("email", "test@example.com")
                .claim("role", "STUDENT")
                .claim("name", "Test User")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(invalidKey)
                .compact();
    }

    /**
     * Generates a malformed JWT token.
     * 
     * @return A malformed JWT token string
     */
    public static String generateMalformedToken() {
        return "malformed.token.string";
    }

    /**
     * Generates a token without required claims.
     * 
     * @return A JWT token missing claims
     */
    public static String generateTokenWithoutClaims() {
        return Jwts.builder()
                .setSubject(UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Parses and returns claims from a valid token.
     * 
     * @param token The JWT token to parse
     * @return The claims from the token
     */
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Generates a token with custom expiration time.
     * 
     * @param userId The user ID
     * @param email The user's email
     * @param role The user's role
     * @param name The user's name
     * @param expirationMillis Time in milliseconds until expiration
     * @return A JWT token string
     */
    public static String generateTokenWithExpiration(String userId, String email, String role, String name, long expirationMillis) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("email", email)
                .claim("role", role)
                .claim("name", name)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Generates a token with multiple roles.
     * 
     * @param userId The user ID
     * @param email The user's email
     * @param roles Comma-separated roles
     * @param name The user's name
     * @return A JWT token string with multiple roles
     */
    public static String generateTokenWithRoles(String userId, String email, String roles, String name) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("email", email)
                .claim("role", roles)
                .claim("name", name)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(getSigningKey())
                .compact();
    }
}
