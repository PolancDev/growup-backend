package com.growup.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka Service Discovery Server for GrowUp Microservices.
 * 
 * This server provides:
 * - Service registration (all microservices register here)
 * - Service discovery (microservices find each other via this server)
 * - Health monitoring dashboard
 */
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscoveryApplication.class, args);
    }
}
