package com.growup.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación para el GrowUp API Gateway.
 * 
 * Este gateway actúa como punto de entrada para todas las peticiones a microservicios,
 * proporcionando:
 * - Enrutamiento de peticiones a servicios backend
 * - Validación de tokens JWT y extracción de información de usuario
 * - Configuración CORS para integración con frontend
 * - Endpoints de monitoreo de salud
 * 
 * @author Equipo GrowUp
 * @version 1.0.0
 */
@SpringBootApplication
public class GatewayApplication {

    /**
     * Método principal de la aplicación.
     *
     * @param args argumentos de línea de comandos
     */
    public static void main(final String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
