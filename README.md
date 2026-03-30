# GrowUp Backend - Arquitectura de Microservicios

## Descripción

Backend de la plataforma GrowUp basado en arquitectura de microservicios con Spring Boot 3.x y Java 17.

## Arquitectura del Sistema

```
┌─────────────────────────────────────────────────────────────────┐
│                         Cliente (Frontend)                      │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                    API Gateway (Puerto 8080)                    │
│                  (Spring Cloud Gateway)                          │
└─────────────────────────────────────────────────────────────────┘
                                │
        ┌───────────────────────┼───────────────────────┐
        ▼                       ▼                       ▼
┌───────────────┐     ┌───────────────┐     ┌───────────────┐
│   Auth Service│     │  Course Service│     │Enrollment Svc │
│  (Puerto 8081)│     │  (Puerto 8082) │     │ (Puerto 8083)  │
└───────────────┘     └───────────────┘     └───────────────┘
        │                       │                       │
        └───────────────────────┼───────────────────────┘
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│              Eureka Discovery (Puerto 8761)                    │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│              Keycloak (Puerto 8180) - OAuth2                    │
└─────────────────────────────────────────────────────────────────┘
```

## Microservicios

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| API Gateway | 8080 | Punto de entrada, enrutamiento, autenticación |
| Discovery | 8761 | Service Registry con Eureka |
| Auth Service | 8081 | Autenticación y gestión de usuarios |
| Course Service | 8082 | Gestión de cursos y lecciones |
| Enrollment Service | 8083 | Matrículas y progreso de estudiantes |
| Notification Service | 8084 | Notificaciones y emails |

## Requisitos

- **Java**: JDK 17
- **Maven**: 3.8+
- **Docker**: Para Keycloak y servicios externos
- **Docker Compose**: Para levantar toda la infraestructura

## Cómo Levantar el Proyecto

### 1. Clonar y preparar

```bash
git clone https://github.com/AbiPol/growup-backend.git
cd growup-backend
```

### 2. Levantar infraestructura con Docker Compose

```bash
docker-compose up -d
```

Esto levantará:
- PostgreSQL (puerto 5432)
- Keycloak (puerto 8180)
- RabbitMQ (puerto 5672)

### 3. Compilar todos los microservicios

```bash
./mvnw clean install -DskipTests
```

### 4. Arrancar servicios

**Opción A: Arrancar todos manualmente (en orden)**

```bash
# 1. Discovery (debe arrancar primero)
cd growup-discovery
./mvnw spring-boot:run

# 2. Auth Service
cd growup-auth
./mvnw spring-boot:run

# 3. Course Service
cd growup-course
./mvnw spring-boot:run

# 4. Enrollment Service
cd growup-enrollment
./mvnw spring-boot:run

# 5. Notification Service
cd growup-notification
./mvnw spring-boot:run

# 6. API Gateway (último)
cd growup-gateway
./mvnw spring-boot:run
```

**Opción B: Usar scripts de startup**

```bash
# Ejecutar script de démarrage
./scripts/start-all.sh
```

### 5. Verificar servicios

- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **Keycloak Admin**: http://localhost:8180 (admin/admin)

## Endpoints Principales

### Auth Service (8081)
- `POST /api/auth/login` - Iniciar sesión
- `POST /api/auth/register` - Registrar usuario
- `GET /api/auth/me` - Obtener usuario actual

### Course Service (8082)
- `GET /api/courses` - Listar cursos
- `GET /api/courses/{id}` - Obtener curso
- `POST /api/courses` - Crear curso
- `PUT /api/courses/{id}` - Actualizar curso

### Enrollment Service (8083)
- `GET /api/enrollments` - Listar matrículas
- `POST /api/enrollments` - Matricularse en curso
- `GET /api/enrollments/student/{id}` - Matrículas de estudiante

### Notification Service (8084)
- `POST /api/notifications/send` - Enviar notificación
- `GET /api/notifications/user/{id}` - Notificaciones de usuario

## Autenticación

El sistema usa **Keycloak** como proveedor de OAuth2:

1. Accede a http://localhost:8180
2. Inicia sesión como admin (admin/admin)
3. Crea un realm llamado "growup"
4. Configura los clients para cada microservicio
5. Los tokens JWT se validan en el API Gateway

### Flujo de Autenticación

```
1. Cliente → API Gateway: Solicita recurso
2. Gateway → Keycloak: Valida token JWT
3. Keycloak → Gateway: Token válido
4. Gateway → Microservicio: Pide recurso
5. Microservicio → Cliente: Respuesta
```

## Estructura de Directorios

```
growup-backend/
├── growup-common/          # Código compartido (DTOs, utilities)
├── growup-discovery/      # Eureka Server
├── growup-gateway/        # API Gateway
├── growup-auth/           # Servicio de autenticación
├── growup-course/         # Servicio de cursos
├── growup-enrollment/     # Servicio de matrículas
├── growup-notification/   # Servicio de notificaciones
├── pom.xml                # Parent POM
└── README.md              # Este archivo
```

## Variables de Entorno

### growup-auth
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/growup_auth
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
KEYCLOAK_URL=http://localhost:8180
```

### growup-course
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/growup_courses
```

### growup-enrollment
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/growup_enrollments
```

## Cómo Contribuir

1. Crea una rama desde `develop`:
   ```bash
   git checkout -b feature/nombre 功能
   ```

2. Sigue Conventional Commits:
   ```bash
   git commit -m "feat(auth): add password reset"
   ```

3. Push y crea PR a `develop`:
   ```bash
   git push origin feature/nombre 功能
   ```

## Rama Histórica

El código del antiguo monolito está preservado en:
- Rama: `historic/monolith`
- Ruta: `src/main/java/com/growup/monolith/`

## Testing

```bash
# Ejecutar tests de un servicio específico
cd growup-auth
./mvnw test

# Ejecutar tests de integración
./mvnw verify -Pintegration-tests
```

## Puertos常用

| Servicio | Puerto |
|----------|--------|
| API Gateway | 8080 |
| Auth Service | 8081 |
| Course Service | 8082 |
| Enrollment Service | 8083 |
| Notification Service | 8084 |
| Discovery (Eureka) | 8761 |
| Keycloak | 8180 |
| PostgreSQL | 5432 |
| RabbitMQ | 5672 |

---

**Versión**: 2.0.0-SNAPSHOT  
**Última Actualización**: 2026-03-30
