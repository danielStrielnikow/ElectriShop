package pl.ecommerce.project.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private User user;

    @BeforeEach
    void setUp() {
        user = new User("login", "mail@mail.com", "haslo");
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals("login", user.getUserName());
        assertEquals("mail@mail.com", user.getEmail());
        assertEquals("haslo", user.getPassword());
        assertNotNull(user.getRoles());
        assertNotNull(user.getAddresses());
    }

    @Test
    void testSetters() {
        user.setUserId(2L);
        assertEquals(2L, user.getUserId());
        user.setUserName("nowy");
        assertEquals("nowy", user.getUserName());
        user.setEmail("nowy@mail.com");
        assertEquals("nowy@mail.com", user.getEmail());
        user.setPassword("nowehaslo");
        assertEquals("nowehaslo", user.getPassword());
    }

    @Test
    void testRoles() {
        Role role = new Role();
        role.setRoleId(1L);
        user.getRoles().add(role);
        assertTrue(user.getRoles().contains(role));
    }

    @Test
    void testAddresses() {
        Address address = new Address();
        user.getAddresses().add(address);
        assertEquals(1, user.getAddresses().size());
    }

    @Test
    void testProducts() {
        Product product = new Product();
        user.setProducts(new HashSet<>());
        user.getProducts().add(product);
        assertTrue(user.getProducts().contains(product));
    }

    @Test
    void testCart() {
        Cart cart = new Cart();
        user.setCart(cart);
        assertEquals(cart, user.getCart());
    }
}