package com.growup.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the GrowUp API Gateway.
 * 
 * This gateway acts as the entry point for all microservice requests,
 * providing:
 * - Request routing to backend services
 * - JWT token validation and user information extraction
 * - CORS configuration for frontend integration
 * - Health monitoring endpoints
 * 
 * @author GrowUp Team
 * @version 1.0.0
 */
@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}