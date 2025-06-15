package pl.ecommerce.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import pl.ecommerce.project.config.AppErrors;
import pl.ecommerce.project.exception.APIException;
import pl.ecommerce.project.exception.ResourceNotFoundException;
import pl.ecommerce.project.model.Category;
import pl.ecommerce.project.payload.CategoryResponse;
import pl.ecommerce.project.payload.dto.CategoryDTO;
import pl.ecommerce.project.payload.dto.DTOMapper;
import pl.ecommerce.project.repo.CategoryRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @Mock private CategoryRepository categoryRepository;
    @Mock private DTOMapper dtoMapper;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        categoryService = new CategoryService(categoryRepository, dtoMapper);
    }

    // ---------- getAllCategories ----------
    @Test
    void getAllCategories_returnsCategoryResponse() {
        Category category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("RTV");
        Page<Category> categoryPage = new PageImpl<>(
                List.of(category),
                PageRequest.of(0, 10, Sort.by("categoryName").ascending()),
                1
        );

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryName("RTV");

        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(categoryPage);
        when(dtoMapper.mapToCategoryDTO(category)).thenReturn(categoryDTO);

        CategoryResponse response = categoryService.getAllCategories(0, 10, "categoryName", "ASC");
        assertEquals(1, response.getContent().size());
        assertEquals("RTV", response.getContent().get(0).getCategoryName());
        assertEquals(0, response.getPageNumber());
        assertEquals(10, response.getPageSize());
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getAllCategories_throwsAPIExceptionOnEmpty() {
        Page<Category> emptyPage = new PageImpl<>(Collections.emptyList());
        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        APIException ex = assertThrows(APIException.class, () ->
                categoryService.getAllCategories(0, 10, "categoryName", "ASC"));
        assertEquals(AppErrors.ERROR_CATEGORY_NOT_FOUND, ex.getMessage());
    }

    // ---------- createCategory ----------
    @Test
    void createCategory_savesAndReturnsDTO() {
        CategoryDTO dto = new CategoryDTO();
        dto.setCategoryName("TV");

        Category entity = new Category();
        entity.setCategoryName("TV");

        when(categoryRepository.findByCategoryName("TV")).thenReturn(null);
        when(dtoMapper.mapCategoryToEntity(dto)).thenReturn(entity);
        when(categoryRepository.save(entity)).thenReturn(entity);
        when(dtoMapper.mapToCategoryDTO(entity)).thenReturn(dto);

        CategoryDTO result = categoryService.createCategory(dto);
        assertEquals("TV", result.getCategoryName());
    }

    @Test
    void createCategory_throwsAPIExceptionIfExists() {
        CategoryDTO dto = new CategoryDTO();
        dto.setCategoryName("TV");
        Category entity = new Category();
        entity.setCategoryName("TV");
        when(categoryRepository.findByCategoryName("TV")).thenReturn(entity);

        APIException ex = assertThrows(APIException.class, () -> categoryService.createCategory(dto));
        assertEquals(AppErrors.ERROR_CATEGORY_EXISTS, ex.getMessage());
    }

    // ---------- updateCategory ----------
    @Test
    void updateCategory_updatesAndReturnsDTO() {
        Long id = 5L;
        CategoryDTO dto = new CategoryDTO();
        dto.setCategoryName("Home");

        Category existing = new Category();
        existing.setCategoryId(id);
        existing.setCategoryName("Old");

        Category updated = new Category();
        updated.setCategoryId(id);
        updated.setCategoryName("Home");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(existing)).thenReturn(updated);
        when(dtoMapper.mapToCategoryDTO(updated)).thenReturn(dto);

        CategoryDTO result = categoryService.updateCategory(dto, id);
        assertEquals("Home", result.getCategoryName());
    }

    @Test
    void updateCategory_throwsResourceNotFoundIfMissing() {
        when(categoryRepository.findById(22L)).thenReturn(Optional.empty());
        CategoryDTO dto = new CategoryDTO();
        assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory(dto, 22L));
    }

    // ---------- deleteCategoryById ----------
    @Test
    void deleteCategoryById_deletesAndReturnsDTO() {
        Long id = 9L;
        Category entity = new Category();
        entity.setCategoryId(id);
        entity.setCategoryName("ToDelete");

        CategoryDTO dto = new CategoryDTO();
        dto.setCategoryName("ToDelete");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(entity));
        doNothing().when(categoryRepository).delete(entity);
        when(dtoMapper.mapToCategoryDTO(entity)).thenReturn(dto);

        CategoryDTO result = categoryService.deleteCategoryById(id);
        verify(categoryRepository).delete(entity);
        assertEquals("ToDelete", result.getCategoryName());
    }

    @Test
    void deleteCategoryById_throwsResourceNotFoundIfMissing() {
        when(categoryRepository.findById(555L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategoryById(555L));
    }

    // ---------- getCategoryResponse (private) ----------
    @Test
    void getCategoryResponse_returnsResponse() throws Exception {
        // Przygotowanie jednej kategorii na pierwszą stronę
        Category category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("Test");

        Page<Category> categoryPage = new PageImpl<>(List.of(category), PageRequest.of(0, 20), 1);

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryName("Test");

        when(dtoMapper.mapToCategoryDTO(category)).thenReturn(categoryDTO);

        // Dostęp do prywatnej metody przez refleksję
        var method = CategoryService.class.getDeclaredMethod("getCategoryResponse", Page.class);
        method.setAccessible(true);

        CategoryResponse response = (CategoryResponse) method.invoke(categoryService, categoryPage);
        assertEquals(1, response.getContent().size());
        assertEquals("Test", response.getContent().get(0).getCategoryName());
        assertEquals(0, response.getPageNumber());
        assertEquals(20, response.getPageSize());
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getCategoryResponse_throwsAPIExceptionOnEmptyPage() throws Exception {
        Page<Category> emptyPage = new PageImpl<>(Collections.emptyList());
        var method = CategoryService.class.getDeclaredMethod("getCategoryResponse", Page.class);
        method.setAccessible(true);

        Exception ex = assertThrows(Exception.class, () -> method.invoke(categoryService, emptyPage));
        Throwable cause = ex.getCause();
        assertNotNull(cause, "Brak przyczyny wyjątku");
        assertInstanceOf(APIException.class, cause, "Powinien być APIException");
        assertEquals(AppErrors.ERROR_CATEGORY_NOT_FOUND, cause.getMessage());
    }

    // ---------- fetchCategoryById (private) ----------
    @Test
    void fetchCategoryById_returnsCategory() throws Exception {
        Category entity = new Category();
        entity.setCategoryId(123L);
        when(categoryRepository.findById(123L)).thenReturn(Optional.of(entity));
        var method = CategoryService.class.getDeclaredMethod("fetchCategoryById", Long.class);
        method.setAccessible(true);
        Category result = (Category) method.invoke(categoryService, 123L);
        assertEquals(123L, result.getCategoryId());
    }

    @Test
    void fetchCategoryById_throwsResourceNotFoundIfMissing() throws Exception {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());
        var method = CategoryService.class.getDeclaredMethod("fetchCategoryById", Long.class);
        method.setAccessible(true);

        Exception ex = assertThrows(Exception.class, () -> method.invoke(categoryService, 999L));
        Throwable cause = ex.getCause();
        assertNotNull(cause, "Brak przyczyny wyjątku");
        assertInstanceOf(ResourceNotFoundException.class, cause, "Powinien być ResourceNotFoundException");
    }
}