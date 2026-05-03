# 🎯 GrowUp Backend - Demo para Cliente

## ✅ Estado Actual (2 Mayo 2026)

### Arquitectura Implementada
- ✅ **Microservicios completos** corriendo
- ✅ **Service Discovery** con Eureka (puerto 8761)
- ✅ **API Gateway** con Spring Cloud Gateway (puerto 8080)
- ✅ **Autenticación JWT** funcionando en Auth Service (puerto 8081)
- ✅ **Módulo de Estudiantes** implementado
- ✅ **Módulo de Cursos** implementado
- ✅ **Módulo de Matriculación** implementado

### Servicios Activos
| Servicio | Puerto | Estado |
|----------|--------|--------|
| Eureka Discovery | 8761 | ✅ Running |
| API Gateway | 8080 | ✅ Running |
| Auth Service | 8081 | ✅ Running |
| Course Service | 8082 | ✅ Running |
| Enrollment Service | - | ✅ Running |
| Notification Service | - | ✅ Running |

## 🚀 Demo Rápida (5 minutos)

### 1. Service Discovery (Eureka Dashboard)
```
http://localhost:8761
```
✨ **Mostrar**: Todos los microservicios registrados y comunicándose.

### 2. Registro de Usuario (Auth Service)
```bash
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"demo@growup.com","password":"demo123","name":"Demo User"}'
```
✨ **Resultado**: Devuelve JWT Token + datos del usuario

### 3. Login
```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"demo@growup.com","password":"demo123"}'
```
✨ **Resultado**: JWT Token válido para acceder a endpoints protegidos

### 4. Arquitectura Hexagonal
Los servicios están construidos con:
- **Domain**: Lógica de negocio pura
- **Application**: Casos de uso
- **Infrastructure**: Adaptadores (Spring, JPA, REST)

## 📊 Lo que sigue (Próximas 2 semanas)
1. ⚙️ Ajustar validación JWT en Gateway (configuración de secret)
2. 🎨 Frontend Angular 21 con Micro-frontends
3. 📱 Integración con módulo de pagos
4. 🔔 Notificaciones en tiempo real

## 🛠️ Stack Tecnológico
- **Backend**: Java 17 + Spring Boot 3.x + Maven
- **Seguridad**: JWT + Spring Security
- **Base de Datos**: PostgreSQL + Flyway
- **Arquitectura**: Microservicios + Hexagonal
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway

---
**Preparado para demo** ✅
DEMO_CLIENTE.md