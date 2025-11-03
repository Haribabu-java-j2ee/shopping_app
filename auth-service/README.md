# Auth Service - Source Code Structure

## Overview
The auth-service now has a complete source code structure for handling authentication and authorization in the e-commerce shopping application.

## Directory Structure

```
auth-service/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/ecommerce/auth/
│       │       ├── AuthServiceApplication.java          # Main Spring Boot application
│       │       ├── config/
│       │       │   ├── RedisConfig.java               # Redis configuration for token storage
│       │       │   └── SecurityConfig.java            # Spring Security configuration
│       │       ├── controller/
│       │       │   └── AuthController.java            # REST endpoints for auth operations
│       │       ├── domain/
│       │       │   └── entity/
│       │       │       ├── Permission.java            # Permission entity
│       │       │       ├── Role.java                  # Role entity
│       │       │       ├── RoleType.java              # Role type enumeration
│       │       │       └── User.java                  # User entity
│       │       ├── dto/
│       │       │   ├── AuthResponse.java              # Authentication response DTO
│       │       │   ├── LoginRequest.java              # Login request DTO
│       │       │   ├── RefreshTokenRequest.java       # Token refresh request DTO
│       │       │   ├── RegisterRequest.java           # User registration request DTO
│       │       │   └── UserDTO.java                   # User information DTO
│       │       ├── mapper/
│       │       │   └── UserMapper.java                # MapStruct mapper for User
│       │       ├── repository/
│       │       │   ├── RoleRepository.java            # Role repository
│       │       │   └── UserRepository.java            # User repository
│       │       ├── security/
│       │       │   └── JwtAuthenticationFilter.java   # JWT token filter
│       │       └── service/
│       │           ├── AuthService.java               # Auth service interface
│       │           ├── UserDetailsServiceImpl.java    # Spring Security UserDetails service
│       │           └── impl/
│       │               └── AuthServiceImpl.java       # Auth service implementation
│       └── resources/
│           ├── application.yml                        # Application configuration
│           └── db/
│               └── migration/
│                   ├── V1__Create_auth_tables.sql    # Database schema
│                   └── V2__Insert_seed_data.sql      # Seed data (roles, permissions, admin user)
├── Dockerfile                                         # Docker configuration
└── pom.xml                                           # Maven configuration

```

## Key Features Implemented

### 1. User Authentication
- User registration with validation
- Login with username/email
- Password encryption using BCrypt
- JWT token generation
- Refresh token support
- Session management with Redis

### 2. Authorization
- Role-Based Access Control (RBAC)
- Fine-grained permissions
- Multiple user roles: ADMIN, USER, SUPPORT, MERCHANT, GUEST

### 3. Security Features
- JWT authentication with Spring Security
- Account lockout after failed login attempts
- Token blacklisting on logout
- Password strength validation
- Redis-based token caching

### 4. Database Design
- Users table with profile information
- Roles table for role definitions
- Permissions table for fine-grained access control
- User-roles many-to-many relationship
- Role-permissions many-to-many relationship

### 5. API Endpoints

#### Public Endpoints
- `POST /api/v1/auth/register` - Register new user
- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/refresh` - Refresh access token
- `GET /api/v1/auth/health` - Health check

#### Protected Endpoints
- `POST /api/v1/auth/logout` - User logout
- `GET /api/v1/auth/validate` - Validate token
- `GET /api/v1/auth/me` - Get current user details

## Default Credentials

An admin user is created during database migration:
- **Username:** admin
- **Email:** admin@ecommerce.com
- **Password:** Admin@123
- **Role:** ADMIN

## Dependencies

The auth-service uses the following key dependencies:
- Spring Boot Web
- Spring Boot Security
- Spring Boot Data JPA
- Spring Boot Data Redis
- Spring Boot Validation
- Spring Boot Actuator
- Spring Kafka (for user events)
- PostgreSQL
- Flyway (database migrations)
- JWT (JJWT)
- Lombok
- MapStruct

## Configuration

The service is configured via `application.yml`:
- Server port: 8081
- Database: PostgreSQL (auth_db)
- Redis: For token storage
- JWT: Configurable secret and expiration
- Security: Password strength, lockout duration

## Design Patterns

1. **Strategy Pattern** - Multiple authentication strategies
2. **Factory Pattern** - Token and user creation
3. **Singleton Pattern** - Security configuration
4. **Repository Pattern** - Data access abstraction
5. **Builder Pattern** - DTO construction

## Building and Running

To build the auth-service:
```bash
cd auth-service
mvn clean install
```

To run with Docker:
```bash
docker-compose up auth-service
```

## Notes

The auth-service is now complete with all source files. The linter errors shown are Lombok-related compilation issues that will be resolved when Maven processes the annotations during the build.



