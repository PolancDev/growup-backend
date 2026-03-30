package com.growup.auth.domain.port.in;

import com.growup.auth.domain.model.User;

/**
 * Puerto de Entrada para los casos de uso de Autenticación.
 */
public interface AuthInPort {

    /**
     * Autentica a un usuario y devuelve sus datos si las credenciales son válidas.
     */
    User login(String email, String password);

    /**
     * Registra un nuevo usuario en el sistema.
     */
    User register(User user, String password);

    /**
     * Recoge informacion de un usuario.
     */
    User getUser(String email);

    /**
     * Actualiza el perfil de un usuario.
     */
    User updateProfile(String email, String name, String emailNew, String password, String avatar, String bio);

    /**
     * Genera un token de acceso para el usuario.
     */
    String generateToken(User user);
}