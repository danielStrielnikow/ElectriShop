package pl.ecommerce.project.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CartItemTest {

    @Test
    void testNoArgsConstructor() {
        CartItem item = new CartItem();
        assertNull(item.getCartItemId());
    }

    @Test
    void testSettersAndGetters() {
        CartItem item = new CartItem();
        item.setCartItemId(1L);
        item.setQuantity(3);
        assertEquals(1L, item.getCartItemId());
        assertEquals(3, item.getQuantity());
    }
}