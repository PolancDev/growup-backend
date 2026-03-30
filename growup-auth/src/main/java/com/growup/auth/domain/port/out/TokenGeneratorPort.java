package com.growup.auth.domain.port.out;

import com.growup.auth.domain.model.User;

/**
 * Puerto de Salida para la generación de tokens de seguridad.
 * Abstrae la tecnología de tokens (JWT, etc.) de la lógica de aplicación.
 */
public interface TokenGeneratorPort {
    String generateToken(User user);
}