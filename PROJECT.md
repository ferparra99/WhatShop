# MarketApp — Documento de Proyecto
> **Versión:** 1.0.4 | **Última actualización:** 2026-06-22
> **Para agentes IA:** Este archivo es la fuente de verdad del proyecto. Cada módulo tiene su estado, dependencias, entidades y endpoints definidos. Antes de generar código, consultá este archivo para respetar la arquitectura acordada.

---

## Contexto general

| Campo | Valor |
|---|---|
| Tipo de proyecto | Marketplace eCommerce multi-vendedor |
| Integración | WhatsApp Cloud API (notificaciones + chatbot) |
| Backend | Java 21 + Spring Boot 3.3.x (monolito modular) |
| Frontend | Angular 18 + PrimeNG + Capacitor (Android) |
| Base de datos | PostgreSQL 15 (Docker en desarrollo) |
| Arquitectura | Monolito modular — paquetes por dominio |
| Nivel del equipo | Junior (explicaciones detalladas requeridas) |

---

## Leyenda de estados

| Ícono | Estado | Significado |
|---|---|---|
| ⬜ | PENDIENTE | No iniciado |
| 🟡 | EN PROGRESO | En desarrollo activo |
| ✅ | COMPLETO | Implementado y probado |
| 🔴 | BLOQUEADO | Requiere que otro módulo esté completo |

---

## Estructura de carpetas del proyecto

```
marketplace/
├── docker-compose.yml                        ✅ Creado
├── PROJECT.md                                ✅ Este archivo
│
├── marketplace-backend/                      ✅ Creado (v1.0.1)
│   ├── pom.xml                               ✅
│   └── src/main/java/com/tuapp/marketplace/
│       ├── MarketplaceApplication.java       ✅
│       ├── config/                           ← Configuración global
│       │   ├── AppConfig.java                ✅ (CORS + JPA Auditing)
│       │   ├── SecurityConfig.java           ✅ (Spring Security)
│       │   ├── OpenApiConfig.java            ✅ (Swagger/OpenAPI + JWT Auth)
│       │   └── LoggingAspect.java            ✅ (AOP - Logs por consola)
│       ├── shared/                           ← Utilidades compartidas
│       │   ├── exception/                    ✅ (4 clases)
│       │   └── response/                     ✅ (ApiResponse)
│       ├── auth/                             ← MÓDULO 1 ✅
│       │   ├── entity/                       ✅ User, Role
│       │   ├── repository/                   ✅ UserRepository
│       │   ├── service/                      ✅ AuthService, JwtUtil, UserDetails
│       │   ├── controller/                   ✅ AuthController, AdminController
│       │   ├── controller/AuthAPI.java       ✅ (interface Swagger)
│       │   ├── controller/AdminAPI.java      ✅ (interface Swagger)
│       │   ├── filter/                       ✅ JwtAuthFilter
│       │   └── dto/                          ✅ RegisterRequest, LoginRequest, AuthResponse
│       ├── users/                            ← MÓDULO 2 (A) ✅
│       │   ├── service/                      ✅ UserService
│       │   ├── controller/                   ✅ UserController
│       │   ├── controller/UserAPI.java       ✅ (interface Swagger)
│       │   └── dto/                          ✅ UserProfileDTO, UpdateUserRequest
│       ├── sellers/                          ← MÓDULO 2 (B) ✅
│       │   ├── entity/                       ✅ Seller
│       │   ├── repository/                   ✅ SellerRepository
│       │   ├── service/                      ✅ SellerService
│       │   ├── controller/                   ✅ SellerController
│       │   ├── controller/SellerAPI.java     ✅ (interface Swagger)
│       │   └── dto/                          ✅ SellerProfileDTO, UpdateSellerRequest
│       └── products/                         ← MÓDULO 3 ✅
│           ├── entity/                       ✅ Category, Product, ProductStatus
│           ├── repository/                   ✅ CategoryRepository, ProductRepository
│           ├── service/                      ✅ CategoryService, ProductService
│           ├── controller/                   ✅ CategoryController, ProductController
│           ├── controller/ProductAPI.java    ✅ (interface Swagger)
│           ├── controller/CategoryAPI.java   ✅ (interface Swagger)
│           └── dto/                          ✅ ProductDTO, CreateProductRequest,
│                                                UpdateProductRequest, ProductPageResponse
│
└── marketplace-frontend/                     ⬜ Por crear
    └── src/app/
        ├── core/
        ├── shared/
        ├── auth/
        ├── products/
        └── seller/
```

---

## MÓDULO 1 — Autenticación

**Estado general:** ✅ COMPLETO
**Prioridad:** 🔴 CRÍTICA — todos los demás módulos dependen de este
**Paquete:** `com.tuapp.marketplace.auth`

### Descripción
Gestión completa de autenticación con JWT. Registro, login y control de acceso por roles.

### Roles del sistema
| Rol | Descripción |
|---|---|
| `ROLE_BUYER` | Comprador — puede navegar, comprar y ver sus pedidos |
| `ROLE_SELLER` | Vendedor — puede gestionar sus productos y ver sus ventas |
| `ROLE_ADMIN` | Administrador — acceso total al sistema |

### Entidades

#### `User` (tabla: `users`)
| Campo | Tipo | Restricciones |
|---|---|---|
| id | UUID | PK, auto-generado |
| email | String | único, not null |
| password | String | bcrypt, not null |
| fullName | String | not null |
| phone | String | formato +57XXXXXXXXXX |
| role | Enum | BUYER / SELLER / ADMIN |
| enabled | Boolean | default true |
| createdAt | LocalDateTime | auto |
| updatedAt | LocalDateTime | auto |

### Endpoints

| Método | Ruta | Acceso | Descripción | Estado |
|---|---|---|---|---|
| POST | `/api/v1/auth/register` | Público | Registrar nuevo usuario | ✅ |
| POST | `/api/v1/auth/login` | Público | Login → retorna JWT | ✅ |
| GET | `/api/v1/auth/me` | Autenticado | Datos del usuario actual | ✅ |
| POST | `/api/v1/auth/refresh` | Autenticado | Renovar token JWT | ✅ |

### Dependencias externas
- `spring-boot-starter-security`
- `jjwt-api` + `jjwt-impl` + `jjwt-jackson` (versión 0.12.x)
- `spring-boot-starter-validation`

### Tareas de implementación

- [x] Crear entidad `User` con JPA
- [x] Crear `UserRepository` (JpaRepository)
- [x] Crear `AuthService` con registro y login
- [x] Configurar `JwtUtil` (generar y validar tokens)
- [x] Configurar `SecurityConfig` (rutas públicas y protegidas)
- [x] Crear `JwtAuthFilter` (intercepta cada request)
- [x] Crear `AuthController` con los 4 endpoints
- [x] Crear DTOs: `RegisterRequest`, `LoginRequest`, `AuthResponse`
- [x] Manejo de errores: `GlobalExceptionHandler`
- [ ] Probar con Postman (colección incluida)

---

## MÓDULO 2 — Usuarios y Vendedores

**Estado general:** ✅ COMPLETO
**Prioridad:** ALTA — depende del Módulo 1 (auth)
**Paquetes:** `com.tuapp.marketplace.users` / `com.tuapp.marketplace.sellers`

### Descripción
Gestión del perfil del comprador y el perfil extendido del vendedor (tienda, descripción, métricas).

### Entidades

#### `Seller` (tabla: `sellers`)
| Campo | Tipo | Restricciones |
|---|---|---|
| id | UUID | PK, auto-generado |
| user | User | FK → users.id, one-to-one |
| storeName | String | not null, único |
| description | String | nullable |
| nit | String | nullable |
| logoUrl | String | nullable |
| rating | Double | default 0.0 |
| totalSales | Integer | default 0 |
| active | Boolean | default true |
| createdAt | LocalDateTime | auto |

### Endpoints

| Método | Ruta | Acceso | Descripción | Estado |
|---|---|---|---|---|
| GET | `/api/v1/users/me` | BUYER / SELLER | Perfil propio | ✅ |
| PUT | `/api/v1/users/me` | BUYER / SELLER | Actualizar perfil | ✅ |
| GET | `/api/v1/sellers` | Público | Listar vendedores activos | ✅ |
| GET | `/api/v1/sellers/{id}` | Público | Perfil público del vendedor | ✅ |
| PUT | `/api/v1/sellers/me` | SELLER | Actualizar su tienda | ✅ |
| GET | `/api/v1/admin/users` | ADMIN | Listar todos los usuarios | ✅ |
| PUT | `/api/v1/admin/users/{id}/enable` | ADMIN | Activar / desactivar usuario | ✅ |

### Tareas de implementación

- [x] Crear entidad `Seller`
- [x] Crear `SellerRepository`
- [x] Crear `UserService` y `SellerService`
- [x] Crear `UserController` y `SellerController`
- [x] DTOs: `UserProfileDTO`, `SellerProfileDTO`, `UpdateUserRequest`, `UpdateSellerRequest`
- [x] Validar que solo SELLER puede editar su tienda (`@PreAuthorize`)
- [ ] Probar con Postman

---

## MÓDULO 3 — Productos y Catálogo

**Estado general:** ✅ COMPLETO
**Prioridad:** ALTA — depende del Módulo 2 (sellers)
**Paquete:** `com.tuapp.marketplace.products`

### Descripción
CRUD completo de productos. Los vendedores crean y gestionan sus productos. Los compradores pueden navegar, filtrar y buscar.

### Entidades

#### `Category` (tabla: `categories`)
| Campo | Tipo | Restricciones |
|---|---|---|
| id | UUID | PK |
| name | String | not null, único |
| slug | String | not null, único |
| imageUrl | String | nullable |

#### `Product` (tabla: `products`)
| Campo | Tipo | Restricciones |
|---|---|---|
| id | UUID | PK |
| seller | Seller | FK → sellers.id |
| category | Category | FK → categories.id |
| name | String | not null |
| description | String | nullable |
| price | BigDecimal | min 0, not null |
| stock | Integer | min 0, not null |
| imageUrl | String | nullable |
| status | Enum | ACTIVE / PAUSED / DELETED |
| rating | Double | default 0.0 |
| createdAt | LocalDateTime | auto |
| updatedAt | LocalDateTime | auto |

### Endpoints

| Método | Ruta | Acceso | Descripción | Estado |
|---|---|---|---|---|
| GET | `/api/v1/products` | Público | Listar con filtros y paginación | ✅ |
| GET | `/api/v1/products/{id}` | Público | Detalle de producto | ✅ |
| GET | `/api/v1/products/search` | Público | Búsqueda por nombre | ✅ |
| POST | `/api/v1/products` | SELLER | Crear producto | ✅ |
| PUT | `/api/v1/products/{id}` | SELLER | Editar producto | ✅ |
| DELETE | `/api/v1/products/{id}` | SELLER | Eliminar (soft delete) | ✅ |
| GET | `/api/v1/sellers/me/products` | SELLER | Mis productos | ✅ |
| GET | `/api/v1/categories` | Público | Listar categorías | ✅ |

### Tareas de implementación

- [x] Crear entidades `Category` y `Product`
- [x] Crear `CategoryRepository` y `ProductRepository`
- [x] Crear `ProductService` con lógica de filtros
- [x] Crear `CategoryService`
- [x] Crear `ProductController` y `CategoryController`
- [x] Paginación con `Pageable` en Spring Data
- [x] Filtros: por categoría, precio mínimo/máximo, vendedor, estado
- [x] DTOs: `ProductDTO`, `CreateProductRequest`, `UpdateProductRequest`, `ProductPageResponse`
- [x] Validar que el SELLER solo edita sus propios productos
- [ ] Probar con Postman

---

## Módulos planificados (próximas versiones)

| Módulo | Descripción | Versión estimada |
|---|---|---|
| `orders` | Carrito y gestión de pedidos | v1.1 |
| `payments` | Integración MercadoPago / Stripe | v1.2 |
| `whatsapp` | Notificaciones y chatbot básico | v1.2 |
| `admin` | Panel de administración completo (AdminController básico ya creado en v1.0.1) | v1.3 |

---

## Configuración del entorno

### Variables de entorno requeridas
```bash
# Base de datos
DB_URL=jdbc:postgresql://localhost:5432/marketplace_db
DB_USER=marketplace_user
DB_PASS=marketplace_pass

# JWT
JWT_SECRET=tu-clave-secreta-de-minimo-256-bits
JWT_EXPIRATION=86400000

# WhatsApp (Módulo 3+)
WA_TOKEN=
WA_PHONE_ID=
WA_VERIFY_TOKEN=
```

### Comandos útiles
```bash
# Levantar base de datos
docker-compose up -d

# Correr backend
./mvnw spring-boot:run

# Correr frontend
ng serve

# Build Angular para Capacitor
ng build && npx cap sync

# Documentación Swagger UI (requiere backend corriendo)
# Abrir en el navegador:
# http://localhost:8080/swagger-ui.html
```

---

## Convenciones del proyecto

### Nomenclatura
- **Entidades:** PascalCase singular (`User`, `Product`)
- **Tablas:** snake_case plural (`users`, `products`)
- **Endpoints:** kebab-case plural (`/api/v1/products`)
- **DTOs:** sufijo `DTO`, `Request`, `Response` según uso
- **Servicios:** sufijo `Service` (`ProductService`)

### Reglas de arquitectura
1. Los `Controller` solo reciben y devuelven DTOs — nunca entidades JPA
2. Los `Service` contienen toda la lógica de negocio
3. Los `Repository` solo hacen queries — sin lógica
4. Cada módulo tiene su propio `Exception` personalizada
5. Todas las rutas privadas requieren JWT válido
6. Un SELLER solo puede modificar sus propios recursos

---

## Historial de cambios

| Fecha | Versión | Cambio |
|---|---|---|
| 2026-06-22 | 1.0.0 | Documento inicial — Módulos 1, 2 y 3 definidos |
| 2026-06-22 | 1.0.1 | Implementación completa del backend (43 clases Java). Módulos 1, 2 y 3 compilados. AdminController creado (parcial del módulo admin) |
| 2026-06-22 | 1.0.2 | Refactorización MVC completa: carpetas separadas por capa (entity/repository/service/controller/dto/filter/config). Principios SOLID y Clean Code aplicados |
| 2026-06-22 | 1.0.3 | Swagger/OpenAPI agregado. Config global con esquema JWT, 6 controllers documentados con @Tag + @Operation + @ApiResponse, 10 DTOs con @Schema |
| 2026-06-22 | 1.0.4 | Swagger movido a 6 interfaces API separadas. Controllers limpios con JavaDoc. Sistema de logs AOP (consola coloreada). Dependencia spring-boot-starter-aop |

