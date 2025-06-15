package pl.ecommerce.project.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CartTest {

    @Test
    void testNoArgsConstructor() {
        Cart cart = new Cart();
        assertNull(cart.getCartId());
    }

    @Test
    void testSettersAndGetters() {
        Cart cart = new Cart();
        cart.setCartId(1L);
        assertEquals(1L, cart.getCartId());
    }
}