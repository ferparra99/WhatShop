package com.whatshop.marketplace.products.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.whatshop.marketplace.products.dto.CreateCategoryRequest;
import com.whatshop.marketplace.products.entity.Category;
import com.whatshop.marketplace.products.repository.CategoryRepository;
import com.whatshop.marketplace.shared.exception.BadRequestException;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryServiceImpl")
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryServiceImpl(categoryRepository);
    }

    private CreateCategoryRequest request(String name, String imageUrl) {
        var req = new CreateCategoryRequest();
        req.setName(name);
        req.setImageUrl(imageUrl);
        return req;
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("debe crear una categoria con slug generado correctamente")
        void shouldCreateCategory() {
            when(categoryRepository.existsByName("Electrónica")).thenReturn(false);
            when(categoryRepository.existsBySlug("electronica")).thenReturn(false);
            when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
                Category c = invocation.getArgument(0);
                c.setId(UUID.randomUUID());
                return c;
            });

            var result = categoryService.create(request("Electrónica", "https://example.com/img.jpg"));

            assertNotNull(result);
            assertEquals("Electrónica", result.getName());
            assertEquals("electronica", result.getSlug());
            assertEquals("https://example.com/img.jpg", result.getImageUrl());
        }

        @Test
        @DisplayName("debe normalizar tildes en el slug")
        void shouldNormalizeAccentsInSlug() {
            when(categoryRepository.existsByName("Tecnología y Más")).thenReturn(false);
            when(categoryRepository.existsBySlug("tecnologia-y-mas")).thenReturn(false);
            when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

            var result = categoryService.create(request("Tecnología y Más", null));

            assertEquals("tecnologia-y-mas", result.getSlug());
        }

        @Test
        @DisplayName("debe normalizar la enie en el slug")
        void shouldNormalizeNInSlug() {
            when(categoryRepository.existsByName("Café y Ñoños")).thenReturn(false);
            when(categoryRepository.existsBySlug("cafe-y-nonos")).thenReturn(false);
            when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

            var result = categoryService.create(request("Café y Ñoños", null));

            assertEquals("cafe-y-nonos", result.getSlug());
        }

        @Test
        @DisplayName("debe limpiar caracteres especiales del slug")
        void shouldCleanSpecialCharactersInSlug() {
            when(categoryRepository.existsByName("Ropa & Accesorios 2024!")).thenReturn(false);
            when(categoryRepository.existsBySlug("ropa-accesorios-2024")).thenReturn(false);
            when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

            var result = categoryService.create(request("Ropa & Accesorios 2024!", null));

            assertEquals("ropa-accesorios-2024", result.getSlug());
        }

        @Test
        @DisplayName("debe lanzar excepcion si el nombre de categoria ya existe")
        void shouldThrowWhenNameAlreadyExists() {
            when(categoryRepository.existsByName("Existent")).thenReturn(true);

            assertThrows(BadRequestException.class, () -> categoryService.create(request("Existent", null)));
            verify(categoryRepository, never()).save(any());
        }

        @Test
        @DisplayName("debe lanzar excepcion si el slug ya existe")
        void shouldThrowWhenSlugAlreadyExists() {
            when(categoryRepository.existsByName("Duplicate Slug")).thenReturn(false);
            when(categoryRepository.existsBySlug("duplicate-slug")).thenReturn(true);

            assertThrows(BadRequestException.class, () -> categoryService.create(request("Duplicate Slug", null)));
            verify(categoryRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("listAll")
    class ListAll {

        @Test
        @DisplayName("debe retornar todas las categorias como DTO")
        void shouldReturnAllCategories() {
            var categories = List.of(
                    Category.builder().id(UUID.randomUUID()).name("Cat1").slug("cat1").build(),
                    Category.builder().id(UUID.randomUUID()).name("Cat2").slug("cat2").build());
            when(categoryRepository.findAll()).thenReturn(categories);

            var result = categoryService.listAll();

            assertEquals(2, result.size());
            assertEquals("Cat1", result.get(0).getName());
            assertEquals("cat1", result.get(0).getSlug());
            assertNotNull(result.get(0).getId());
        }
    }
}