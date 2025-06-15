package pl.ecommerce.project.model;

import org.junit.jupiter.api.Test;
import pl.ecommerce.project.model.app.AppRole;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void testNoArgsConstructor() {
        Role role = new Role();
        assertNull(role.getRoleId());
        assertNull(role.getRoleName());
    }

    @Test
    void testAllArgsConstructor() {
        Role role = new Role(1L, AppRole.ROLE_USER);
        assertEquals(1L, role.getRoleId());
        assertEquals(AppRole.ROLE_USER, role.getRoleName());
    }

    @Test
    void testRoleNameConstructor() {
        Role role = new Role(AppRole.ROLE_ADMIN);
        assertNull(role.getRoleId());
        assertEquals(AppRole.ROLE_ADMIN, role.getRoleName());
    }

    @Test
    void testSettersAndGetters() {
        Role role = new Role();
        role.setRoleId(5L);
        role.setRoleName(AppRole.ROLE_SELLER);

        assertEquals(5L, role.getRoleId());
        assertEquals(AppRole.ROLE_SELLER, role.getRoleName());
    }
}