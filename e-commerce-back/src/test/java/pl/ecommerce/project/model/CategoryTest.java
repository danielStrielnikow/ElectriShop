package pl.ecommerce.project.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    void testNoArgsConstructor() {
        Category category = new Category();
        assertNull(category.getCategoryId());
    }

    @Test
    void testSettersAndGetters() {
        Category category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("RTV");
        assertEquals(1L, category.getCategoryId());
        assertEquals("RTV", category.getCategoryName());
    }
}