package com.growup.auth.infrastructure.adapter.web;

import com.growup.auth.domain.model.User;
import com.growup.auth.domain.port.in.UserInPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adaptador Web para Administración de Usuarios.
 * Endpoints REST para gestión de usuarios por administradores.
 * Ruta alineada con OpenAPI spec: /api/v1/admin/users
 */
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Slf4j
public class AdminUserWebAdapter {

    private final UserInPort userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        log.info("GrowUp-Log: AdminUserWebAdapter - Listando todos los usuarios");
        List<User> users = userService.getAllUsers();
        
        List<Map<String, Object>> response = users.stream()
                .map(this::mapUserToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable UUID id) {
        log.info("GrowUp-Log: AdminUserWebAdapter - Obteniendo usuario por ID: {}", id);
        User user = userService.getUserById(id);
        return ResponseEntity.ok(mapUserToDto(user));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable UUID id,
            @RequestBody Map<String, String> updateRequest) {
        log.info("GrowUp-Log: AdminUserWebAdapter - Actualizando usuario: {}", id);
        
        User user = userService.getUserById(id);
        
        if (updateRequest.containsKey("name") && updateRequest.get("name") != null) {
            user.setName(updateRequest.get("name"));
        }
        if (updateRequest.containsKey("email") && updateRequest.get("email") != null) {
            user.setEmail(updateRequest.get("email"));
        }
        if (updateRequest.containsKey("bio") && updateRequest.get("bio") != null) {
            user.setBio(updateRequest.get("bio"));
        }
        if (updateRequest.containsKey("avatar") && updateRequest.get("avatar") != null) {
            user.setAvatar(updateRequest.get("avatar"));
        }
        
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(mapUserToDto(updatedUser));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> toggleUserStatus(@PathVariable UUID id) {
        log.info("GrowUp-Log: AdminUserWebAdapter - Cambiando estado del usuario: {}", id);
        User user = userService.toggleUserStatus(id);
        return ResponseEntity.ok(mapUserToDto(user));
    }

    private Map<String, Object> mapUserToDto(User user) {
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