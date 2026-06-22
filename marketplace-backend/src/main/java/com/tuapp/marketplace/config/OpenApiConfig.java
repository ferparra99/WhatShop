package com.tuapp.marketplace.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        var securitySchemeName = "bearer-jwt";

        return new OpenAPI()
                .info(new Info()
                        .title("WhatShop — Marketplace API")
                        .version("1.0.2")
                        .description(
                                """
                                API REST del marketplace multi-vendedor **WhatShop**.
                                Backend construido con Java 21 + Spring Boot 3.3.x.
                                
                                ---
                                ### 📦 Módulos disponibles
                                
                                | Módulo | Tag en Swagger | Descripción |
                                |--------|---------------|-------------|
                                | Autenticación | `Auth` | Registro, login, perfil y renovación de JWT |
                                | Usuarios | `Users` | Gestión del perfil del comprador |
                                | Vendedores | `Sellers` | Perfil público y gestión de tiendas |
                                | Productos | `Products` | CRUD, filtros, búsqueda y paginación |
                                | Categorías | `Categories` | Listado y creación de categorías |
                                | Administración | `Admin` | Listar y gestionar usuarios del sistema |
                                
                                ---
                                ### 🔐 Autenticación
                                1. Usá el endpoint `POST /api/v1/auth/register` para crear una cuenta.
                                2. Usá `POST /api/v1/auth/login` para obtener un token JWT.
                                3. Hacé clic en **Authorize** (arriba a la derecha) y pegá tu token.
                                4. ¡Listo! Ya podés probar los endpoints protegidos.
                                
                                ### 🧪 Roles de prueba
                                - `BUYER` — Comprador (navegar, comprar)
                                - `SELLER` — Vendedor (gestionar productos y tienda)
                                - `ADMIN` — Administrador (gestión de usuarios)
                                """
                        )
                        .contact(new Contact()
                                .name("Equipo WhatShop")
                                .email("soporte@whatshop.com"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .externalDocs(new ExternalDocumentation()
                        .description("Documentación del proyecto")
                        .url("https://github.com/tuapp/whatshop"))
                .tags(List.of(
                        new Tag().name("Auth").description("🔐 Registro, inicio de sesión y gestión de tokens JWT"),
                        new Tag().name("Users").description("👤 Perfil del comprador — consultar y actualizar datos personales"),
                        new Tag().name("Sellers").description("🏪 Perfil del vendedor — gestión de tienda y productos propios"),
                        new Tag().name("Products").description("📦 Catálogo de productos — listar, filtrar, crear, editar y eliminar"),
                        new Tag().name("Categories").description("🏷️ Categorías de productos — listar y crear"),
                        new Tag().name("Admin").description("⚙️ Administración del sistema — usuarios y configuración")
                ))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Ingresá tu token JWT obtenido en `POST /api/v1/auth/login`. Ejemplo: `eyJhbGciOi...`")));
    }
}
