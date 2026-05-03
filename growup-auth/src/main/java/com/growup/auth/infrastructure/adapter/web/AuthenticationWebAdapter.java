package com.growup.auth.infrastructure.adapter.web;

import com.growup.auth.application.dto.auth.LoginRequest;
import com.growup.auth.application.dto.auth.LoginResponse;
import com.growup.auth.application.dto.auth.RegisterRequest;
import com.growup.auth.domain.model.User;
import com.growup.auth.domain.port.in.AuthInPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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

    private final AuthInPort authService;

    /**
     * Endpoint de login para obtener JWT.
     * Este endpoint es público (permitAll en SecurityConfig).
     * Para la demo, acepta cualquier credencial válida y devuelve un JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        log.info("GrowUp-Log: AuthenticationWebAdapter - Intento de login para: {}", loginRequest.getEmail());
        
        try {
            // Autenticar usuario
            log.debug("GrowUp-Log: Llamando a authService.login()");
            var user = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
            log.debug("GrowUp-Log: Usuario autenticado correctamente: {}", user.getEmail());
            
            // Generar token JWT
            log.debug("GrowUp-Log: Generando token JWT");
            String token = authService.generateToken(user);
            log.debug("GrowUp-Log: Token generado exitosamente");
            
            // Construir respuesta
            LoginResponse response = LoginResponse.builder()
                    .token(token)
                    .tokenType("Bearer")
                    .expiresIn(86400000L) // 24 horas en milisegundos
                    .userId(user.getId().toString())
                    .email(user.getEmail())
                    .name(user.getName())
                    .role(user.getRole() != null ? user.getRole().name() : "STUDENT")
                    .build();
             
            log.info("GrowUp-Log: AuthenticationWebAdapter - Login exitoso para: {}", loginRequest.getEmail());
            return ResponseEntity.ok(response);
             
        } catch (com.growup.auth.domain.exception.InvalidCredentialsException e) {
            log.warn("GrowUp-Log: AuthenticationWebAdapter - Credenciales inválidas para: {}", loginRequest.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales inválidas: " + e.getMessage());
        } catch (Exception e) {
            log.error("GrowUp-Log: AuthenticationWebAdapter - Error inesperado en login para: {}", loginRequest.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno: " + e.getMessage());
        }
    }

    /**
     * Endpoint de registro de nuevos usuarios.
     * Público (permitAll en SecurityConfig).
     */
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody RegisterRequest registerRequest) {
        log.info("GrowUp-Log: AuthenticationWebAdapter - Intento de registro para: {}", registerRequest.getEmail());
        
        try {
        // Crear usuario del dominio (leer rol del request o usar STUDENT por defecto)
        com.growup.common.domain.model.enums.Role userRole = com.growup.common.domain.model.enums.Role.STUDENT; // Valor por defecto
        if (registerRequest.getRole() != null) {
            try {
                userRole = com.growup.common.domain.model.enums.Role.valueOf(registerRequest.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("GrowUp-Log: AuthenticationWebAdapter - Rol inválido: {}, usando STUDENT", registerRequest.getRole());
            }
        }
        
        User newUser = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .role(userRole) // Usar el rol del request o el por defecto
                .build();
            
            // Registrar usuario (el servicio hashea la contraseña)
            User savedUser = authService.register(newUser, registerRequest.getPassword());
            
            // Generar token para login automático
            String token = authService.generateToken(savedUser);
            
            LoginResponse response = LoginResponse.builder()
                    .token(token)
                    .tokenType("Bearer")
                    .expiresIn(86400000L)
                    .userId(savedUser.getId().toString())
                    .email(savedUser.getEmail())
                    .name(savedUser.getName())
                    .role(savedUser.getRole().name())
                    .build();
                    
            log.info("GrowUp-Log: AuthenticationWebAdapter - Registro exitoso para: {}", registerRequest.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("GrowUp-Log: AuthenticationWebAdapter - Email ya registrado: {}", registerRequest.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            log.error("GrowUp-Log: AuthenticationWebAdapter - Error en registro para: {}", registerRequest.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint para obtener información del usuario actual desde el token JWT.
     * El token JWT es generado por nuestro JwtTokenGeneratorAdapter.
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        // Obtener información del token JWT personalizado
        String subject = jwt.getSubject(); // userId
        String email = jwt.getClaimAsString("email");
        String name = jwt.getClaimAsString("name");
        String role = jwt.getClaimAsString("role");
        
        // Buscar usuario en BD para obtener información adicional
        var userOpt = authService.getUserById(java.util.UUID.fromString(subject));
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", subject);
        response.put("email", email);
        response.put("name", name != null ? name : userOpt.map(u -> u.getName()).orElse(""));
        response.put("role", role != null ? role : "STUDENT");
        
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