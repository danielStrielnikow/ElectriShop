package pl.ecommerce.project.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class APIExceptionTest {

    @Test
    void testDefaultConstructor() {
        APIException ex = new APIException();
        assertNull(ex.getMessage());
    }

    @Test
    void testConstructorWithMessage() {
        APIException ex = new APIException("Test error!");
        assertEquals("Test error!", ex.getMessage());
    }
}