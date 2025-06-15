package pl.ecommerce.project.security.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    @Test
    void testSetAndGetUsername() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testUser");
        assertEquals("testUser", request.getUsername());
    }

    @Test
    void testSetAndGetPassword() {
        LoginRequest request = new LoginRequest();
        request.setPassword("secretPass");
        assertEquals("secretPass", request.getPassword());
    }

    @Test
    void testSetAndGetUsernameAndPassword() {
        LoginRequest request = new LoginRequest();
        request.setUsername("user123");
        request.setPassword("pass123");
        assertEquals("user123", request.getUsername());
        assertEquals("pass123", request.getPassword());
    }

    @Test
    void testDefaultValues() {
        LoginRequest request = new LoginRequest();
        assertNull(request.getUsername());
        assertNull(request.getPassword());
    }
}