package pl.ecommerce.project.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    @Test
    void testNoArgsConstructor() {
        Address address = new Address();
        assertNull(address.getAddressId());
    }

    @Test
    void testSettersAndGetters() {
        Address address = new Address();
        address.setAddressId(1L);
        address.setStreet("ulica");
        address.setCity("miasto");
        address.setPincode("00-000");

        assertEquals(1L, address.getAddressId());
        assertEquals("ulica", address.getStreet());
        assertEquals("miasto", address.getCity());
        assertEquals("00-000", address.getPincode());
    }
}