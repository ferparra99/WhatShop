# WhatShop — Marketplace Multi-Vendedor

Marketplace eCommerce donde vendedores publican productos y compradores los navegan. Backend monolítico modular con autenticación JWT, roles y catálogo con filtros.

## Stack Tecnológico

| Capa | Tecnología |
|---|---|
| Backend | Java 21, Spring Boot 3.3.x, Maven |
| Base de datos | PostgreSQL 15 (Docker) |
| Frontend | Angular 18 + PrimeNG + Capacitor (Android) |
| Autenticación | JWT (jjwt 0.12.x) + Spring Security |
| Documentación API | Swagger UI / OpenAPI 3 (springdoc) |

## Requisitos Previos

- **JDK 21** (obligatorio — JDK 26 + Lombok son incompatibles)
- **Docker Desktop** (para PostgreSQL)
- **Maven 3.9+**
- **Node.js 20+** (para frontend)

## Estructura del Proyecto

```
WhatShop/
├── docker-compose.yml         # PostgreSQL 15 en puerto 5433
├── init.sql                   # DDL inicial de la base de datos
├── PROJECT.md                 # Documento fuente de verdad (agentes IA)
├── README.md                  # Este archivo
│
├── marketplace-backend/       # Backend Spring Boot
│   └── src/main/java/com/whatshop/marketplace/
│       ├── config/            # Seguridad, Swagger, CORS, Logging AOP
│       ├── shared/            # Excepciones globales, ApiResponse
│       ├── auth/              # Registro, login, JWT, roles
│       ├── users/             # Perfil de usuario
│       ├── sellers/           # Perfil de vendedor (tienda)
│       └── products/          # Productos, categorías, catálogo
│
└── marketplace-frontend/      # Frontend Angular (próximamente)
```

## Cómo Empezar

```bash
# 1. Clonar el repositorio
git clone <repo-url>
cd WhatShop

# 2. Configurar JAVA_HOME a JDK 21
set JAVA_HOME=C:\Users\ferpa\java\jdk-21

# 3. Levantar PostgreSQL
docker-compose up -d

# 4. Iniciar backend
cd marketplace-backend
mvn spring-boot:run

# 5. (Opcional) Iniciar frontend
cd ../marketplace-frontend
npm install
ng serve
```

El backend arranca en `http://localhost:8080`.

## Variables de Entorno

| Variable | Default | Descripción |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5433/marketplace_db` | Conexión a PostgreSQL |
| `DB_USER` | `marketplace_user` | Usuario de base de datos |
| `DB_PASS` | `marketplace_pass` | Contraseña de base de datos |
| `JWT_SECRET` | `tu-clave-secreta-de-minimo-256-bits-para-desarrollo-local` | Clave para firmar JWT |
| `JWT_EXPIRATION` | `86400000` | Expiración del token (24h en ms) |

## Roles del Sistema

| Rol | Permisos |
|---|---|
| `BUYER` | Navegar catálogo, ver productos |
| `SELLER` | Todo lo de BUYER + gestionar tienda y productos propios |
| `ADMIN` | Acceso total: gestionar usuarios, productos, categorías |

## Autenticación

### Registro

```json
POST /api/v1/auth/register

// BUYER
{ "email": "comprador@mail.com", "password": "Pass123", "fullName": "Carlos", "phone": "+573001234567", "role": "BUYER" }

// SELLER (requiere store.storeName)
{ "email": "vendedor@mail.com", "password": "Pass123", "fullName": "Maria", "phone": "+573109876543", "role": "SELLER", "store": { "storeName": "Tienda de Maria", "description": "Productos artesanales", "nit": "900123456-7", "logoUrl": "https://ejemplo.com/logo.png" } }

// ADMIN (store opcional)
{ "email": "admin@mail.com", "password": "Admin123", "fullName": "Admin", "phone": "+573000000000", "role": "ADMIN" }
```

### Login

```json
POST /api/v1/auth/login
{ "email": "vendedor@mail.com", "password": "Pass123" }
// Respuesta: { "token": "eyJhbGci...", "userId": "...", "role": "ROLE_SELLER" }
```

Usar el token en todas las requests protegidas:

```
Authorization: Bearer eyJhbGci...
```

## Endpoints

### Auth (Público + Autenticado)

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| POST | `/api/v1/auth/register` | Público | Registrar usuario |
| POST | `/api/v1/auth/login` | Público | Iniciar sesión |
| GET | `/api/v1/auth/me` | Autenticado | Perfil propio |
| POST | `/api/v1/auth/refresh` | Autenticado | Renovar token |

### Users (Autenticado)

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| GET | `/api/v1/users/me` | Autenticado | Ver perfil |
| PUT | `/api/v1/users/me` | Autenticado | Actualizar perfil |

### Sellers (Público + SELLER/ADMIN)

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| GET | `/api/v1/sellers` | Público | Listar vendedores activos |
| GET | `/api/v1/sellers/{id}` | Público | Perfil público de vendedor |
| GET | `/api/v1/sellers/me` | SELLER o ADMIN | Mi perfil de tienda |
| PUT | `/api/v1/sellers/me` | SELLER o ADMIN | Actualizar mi tienda |
| GET | `/api/v1/sellers/me/products` | SELLER o ADMIN | Mis productos |

### Products (Público + SELLER/ADMIN)

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| GET | `/api/v1/products` | Público | Listar productos (filtros + paginación) |
| GET | `/api/v1/products/{id}` | Público | Detalle de producto |
| GET | `/api/v1/products/search` | Público | Buscar por nombre |
| POST | `/api/v1/products` | SELLER o ADMIN | Crear producto |
| PUT | `/api/v1/products/{id}` | SELLER o ADMIN | Editar producto |
| DELETE | `/api/v1/products/{id}` | SELLER o ADMIN | Eliminar producto (soft delete) |

### Categories (Público + ADMIN)

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| GET | `/api/v1/categories` | Público | Listar categorías |
| POST | `/api/v1/categories` | ADMIN | Crear categoría |

### Admin (solo ADMIN)

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| GET | `/api/v1/admin/users` | ADMIN | Listar usuarios |
| PUT | `/api/v1/admin/users/{id}/enable` | ADMIN | Activar/desactivar usuario |

## Filtros y Paginación

### Productos

```
GET /api/v1/products?categoryId=...&minPrice=...&maxPrice=...&sellerId=...&search=...&page=0&size=20&sort=price,asc
```

| Parámetro | Tipo | Descripción |
|---|---|---|
| `categoryId` | UUID | Filtrar por categoría |
| `minPrice` | BigDecimal | Precio mínimo |
| `maxPrice` | BigDecimal | Precio máximo |
| `sellerId` | UUID | Filtrar por vendedor |
| `search` | String | Búsqueda por nombre |
| `page` | int | Número de página (default 0) |
| `size` | int | Tamaño de página (default 20) |
| `sort` | String | Ordenación (ej: `price,asc`) |

## Documentación Swagger

Con el backend corriendo:

```
http://localhost:8080/swagger-ui.html
```

## Docker

```bash
# Iniciar PostgreSQL
docker-compose up -d

# Detener
docker-compose down

# Ver logs
docker-compose logs -f
```

Configuración: PostgreSQL 15 en puerto `5433`, usuario `marketplace_user`, base de datos `marketplace_db`.

## Licencia

MIT
