package pl.ecommerce.project.model;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void testNoArgsConstructor() {
        Product product = new Product();
        assertNull(product.getProductId());
        assertNotNull(product.getProducts());
        assertTrue(product.getProducts().isEmpty());
    }

    @Test
    void testAllArgsConstructor() {
        Category category = new Category();
        User user = new User();
        Product product = new Product(1L, "Nazwa", "img.png", "Opis produktu", 10, 99.99, 10.0, 89.99, category, user, new ArrayList<>());
        assertEquals(1L, product.getProductId());
        assertEquals("Nazwa", product.getProductName());
        assertEquals("img.png", product.getImage());
        assertEquals("Opis produktu", product.getDescription());
        assertEquals(10, product.getQuantity());
        assertEquals(99.99, product.getPrice());
        assertEquals(10.0, product.getDiscount());
        assertEquals(89.99, product.getSpecialPrice());
        assertEquals(category, product.getCategory());
        assertEquals(user, product.getUser());
        assertNotNull(product.getProducts());
    }

    @Test
    void testSettersAndGetters() {
        Product product = new Product();
        product.setProductId(2L);
        product.setProductName("Nowy");
        product.setImage("img.jpg");
        product.setDescription("Opis");
        product.setQuantity(5);
        product.setPrice(10.0);
        product.setDiscount(2.5);
        product.setSpecialPrice(7.5);

        assertEquals(2L, product.getProductId());
        assertEquals("Nowy", product.getProductName());
        assertEquals("img.jpg", product.getImage());
        assertEquals("Opis", product.getDescription());
        assertEquals(5, product.getQuantity());
        assertEquals(10.0, product.getPrice());
        assertEquals(2.5, product.getDiscount());
        assertEquals(7.5, product.getSpecialPrice());
    }
}