# MarketApp — Documento de Proyecto
> **Versión:** 1.0.0 | **Última actualización:** 2026-06-22
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
├── marketplace-backend/                      ⬜ Por crear
│   ├── pom.xml
│   └── src/main/java/com/tuapp/marketplace/
│       ├── MarketplaceApplication.java
│       ├── shared/                           ← utilidades compartidas
│       │   ├── exception/
│       │   ├── response/
│       │   └── config/
│       ├── auth/                             ← MÓDULO 1
│       ├── users/                            ← MÓDULO 2 (parte A)
│       ├── sellers/                          ← MÓDULO 2 (parte B)
│       └── products/                         ← MÓDULO 3
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

**Estado general:** ⬜ PENDIENTE
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
| POST | `/api/v1/auth/register` | Público | Registrar nuevo usuario | ⬜ |
| POST | `/api/v1/auth/login` | Público | Login → retorna JWT | ⬜ |
| GET | `/api/v1/auth/me` | Autenticado | Datos del usuario actual | ⬜ |
| POST | `/api/v1/auth/refresh` | Autenticado | Renovar token JWT | ⬜ |

### Dependencias externas
- `spring-boot-starter-security`
- `jjwt-api` + `jjwt-impl` + `jjwt-jackson` (versión 0.12.x)
- `spring-boot-starter-validation`

### Tareas de implementación

- [ ] Crear entidad `User` con JPA
- [ ] Crear `UserRepository` (JpaRepository)
- [ ] Crear `AuthService` con registro y login
- [ ] Configurar `JwtUtil` (generar y validar tokens)
- [ ] Configurar `SecurityConfig` (rutas públicas y protegidas)
- [ ] Crear `JwtAuthFilter` (intercepta cada request)
- [ ] Crear `AuthController` con los 4 endpoints
- [ ] Crear DTOs: `RegisterRequest`, `LoginRequest`, `AuthResponse`
- [ ] Manejo de errores: `GlobalExceptionHandler`
- [ ] Probar con Postman (colección incluida)

---

## MÓDULO 2 — Usuarios y Vendedores

**Estado general:** ⬜ PENDIENTE
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
| GET | `/api/v1/users/me` | BUYER / SELLER | Perfil propio | ⬜ |
| PUT | `/api/v1/users/me` | BUYER / SELLER | Actualizar perfil | ⬜ |
| GET | `/api/v1/sellers` | Público | Listar vendedores activos | ⬜ |
| GET | `/api/v1/sellers/{id}` | Público | Perfil público del vendedor | ⬜ |
| PUT | `/api/v1/sellers/me` | SELLER | Actualizar su tienda | ⬜ |
| GET | `/api/v1/admin/users` | ADMIN | Listar todos los usuarios | ⬜ |
| PUT | `/api/v1/admin/users/{id}/enable` | ADMIN | Activar / desactivar usuario | ⬜ |

### Tareas de implementación

- [ ] Crear entidad `Seller`
- [ ] Crear `SellerRepository`
- [ ] Crear `UserService` y `SellerService`
- [ ] Crear `UserController` y `SellerController`
- [ ] DTOs: `UserProfileDTO`, `SellerProfileDTO`, `UpdateUserRequest`, `UpdateSellerRequest`
- [ ] Validar que solo SELLER puede editar su tienda (`@PreAuthorize`)
- [ ] Probar con Postman

---

## MÓDULO 3 — Productos y Catálogo

**Estado general:** ⬜ PENDIENTE
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
| GET | `/api/v1/products` | Público | Listar con filtros y paginación | ⬜ |
| GET | `/api/v1/products/{id}` | Público | Detalle de producto | ⬜ |
| GET | `/api/v1/products/search` | Público | Búsqueda por nombre | ⬜ |
| POST | `/api/v1/products` | SELLER | Crear producto | ⬜ |
| PUT | `/api/v1/products/{id}` | SELLER | Editar producto | ⬜ |
| DELETE | `/api/v1/products/{id}` | SELLER | Eliminar (soft delete) | ⬜ |
| GET | `/api/v1/sellers/me/products` | SELLER | Mis productos | ⬜ |
| GET | `/api/v1/categories` | Público | Listar categorías | ⬜ |

### Tareas de implementación

- [ ] Crear entidades `Category` y `Product`
- [ ] Crear `CategoryRepository` y `ProductRepository`
- [ ] Crear `ProductService` con lógica de filtros
- [ ] Crear `CategoryService`
- [ ] Crear `ProductController` y `CategoryController`
- [ ] Paginación con `Pageable` en Spring Data
- [ ] Filtros: por categoría, precio mínimo/máximo, vendedor, estado
- [ ] DTOs: `ProductDTO`, `CreateProductRequest`, `UpdateProductRequest`, `ProductPageResponse`
- [ ] Validar que el SELLER solo edita sus propios productos
- [ ] Probar con Postman

---

## Módulos planificados (próximas versiones)

| Módulo | Descripción | Versión estimada |
|---|---|---|
| `orders` | Carrito y gestión de pedidos | v1.1 |
| `payments` | Integración MercadoPago / Stripe | v1.2 |
| `whatsapp` | Notificaciones y chatbot básico | v1.2 |
| `admin` | Panel de administración completo | v1.3 |

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

