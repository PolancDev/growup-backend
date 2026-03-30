package com.growup.auth.infrastructure.adapter.web;

import com.growup.auth.application.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Adaptador Web para Autenticación.
 * Endpoints REST para gestión de perfil de usuario autenticado via OAuth2/Keycloak.
 * Los endpoints /login y /register ahora son manejados por Keycloak.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationWebAdapter {

    private final AuthService authService;

    /**
     * Endpoint para obtener información del usuario actual desde el token JWT.
     * El token es proporcionado por Keycloak después del login OAuth2.
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        // Obtener información del token JWT de Keycloak
        String subject = jwt.getSubject(); // userId
        String email = jwt.getClaimAsString("email");
        String name = jwt.getClaimAsString("name");
        
        // Obtener roles del token
        var roles = jwt.getClaimAsStringList("realm_access.roles");
        String role = (roles != null && !roles.isEmpty()) ? roles.get(0) : "STUDENT";
        
        // Buscar usuario en BD para obtener información adicional
        var userOpt = authService.getUserById(java.util.UUID.fromString(subject));
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", subject);
        response.put("email", email);
        response.put("name", name != null ? name : userOpt.map(u -> u.getName()).orElse(""));
        response.put("role", role);
        
        userOpt.ifPresent(user -> {
            response.put("bio", user.getBio());
            response.put("avatar", user.getAvatar());
            response.put("isActive", user.getIsActive());
        });
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para actualizar el perfil del usuario.
     * Solo accesible si está autenticado.
     */
    @PutMapping("/me")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Map<String, String> updateRequest) {
        
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String userId = jwt.getSubject();
        log.info("GrowUp-Log: AuthenticationWebAdapter - Actualizando perfil para usuario: {}", userId);
        
        var user = authService.updateProfile(
            userId,
            updateRequest.get("name"),
            updateRequest.get("email"),
            null, // Password no se actualiza aquí
            updateRequest.get("avatar"),
            updateRequest.get("bio")
        );
        
        return ResponseEntity.ok(mapUserToDto(user));
    }

    private Map<String, Object> mapUserToDto(com.growup.auth.domain.model.User user) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", user.getId());
        dto.put("name", user.getName());
        dto.put("email", user.getEmail());
        dto.put("isActive", user.getIsActive());
        dto.put("role", user.getRole() != null ? user.getRole().name() : null);
        dto.put("bio", user.getBio());
        dto.put("avatar", user.getAvatar());
        dto.put("joinDate", user.getJoinDate());
        dto.put("version", user.getVersion());
        return dto;
    }
}