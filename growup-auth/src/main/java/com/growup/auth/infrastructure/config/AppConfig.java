package com.growup.auth.infrastructure.config;

import com.growup.auth.application.service.AuthService;
import com.growup.auth.application.service.UserService;
import com.growup.auth.domain.port.out.TokenGeneratorPort;
import com.growup.auth.domain.port.out.UserPersistencePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuración manual de beans de la capa de aplicación.
 * Sigue la Arquitectura Hexagonal: la infraestructura declara los beans,
 * la aplicación solo define la lógica.
 */
@Configuration
public class AppConfig {

    @Bean
    public AuthService authService(
            UserPersistencePort userPersistencePort,
            TokenGeneratorPort tokenGeneratorPort,
            PasswordEncoder passwordEncoder) {
        return new AuthService(userPersistencePort, tokenGeneratorPort, passwordEncoder);
    }

    @Bean
    public UserService userService(UserPersistencePort userPersistencePort) {
        return new UserService(userPersistencePort);
    }
}
