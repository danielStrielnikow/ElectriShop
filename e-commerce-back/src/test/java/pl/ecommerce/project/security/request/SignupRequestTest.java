package pl.ecommerce.project.security.request;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SignupRequestTest {

    @Test
    void testSetAndGetUsername() {
        SignupRequest request = new SignupRequest();
        request.setUsername("janek");
        assertEquals("janek", request.getUsername());
    }

    @Test
    void testSetAndGetEmail() {
        SignupRequest request = new SignupRequest();
        request.setEmail("janek@example.com");
        assertEquals("janek@example.com", request.getEmail());
    }

    @Test
    void testSetAndGetPassword() {
        SignupRequest request = new SignupRequest();
        request.setPassword("securePassword123");
        assertEquals("securePassword123", request.getPassword());
    }

    @Test
    void testSetAndGetRole() {
        SignupRequest request = new SignupRequest();
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        roles.add("ADMIN");
        request.setRole(roles);
        assertEquals(roles, request.getRole());
    }

    @Test
    void testDefaultValues() {
        SignupRequest request = new SignupRequest();
        assertNull(request.getUsername());
        assertNull(request.getEmail());
        assertNull(request.getPassword());
        assertNull(request.getRole());
    }
}