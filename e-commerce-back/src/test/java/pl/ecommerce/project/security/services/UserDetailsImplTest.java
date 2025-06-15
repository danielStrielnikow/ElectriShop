package pl.ecommerce.project.security.services;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pl.ecommerce.project.model.Role;
import pl.ecommerce.project.model.User;
import pl.ecommerce.project.model.app.AppRole;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsImplTest {

    @Test
    void testBuildFromUser() {
        // given
        User user = new User();
        user.setUserId(1L);
        user.setUserName("john");
        user.setEmail("john@example.com");
        user.setPassword("secret");

        Role userRole = new Role();
        userRole.setRoleName(AppRole.ROLE_USER);
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        // when
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);

        // then
        assertEquals(user.getUserId(), userDetails.getId());
        assertEquals(user.getUserName(), userDetails.getUsername());
        assertEquals(user.getEmail(), userDetails.getEmail());
        assertEquals(user.getPassword(), userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void testGettersAndSetters() {
        Collection<GrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));

        UserDetailsImpl userDetails = new UserDetailsImpl(2L, "anna", "anna@example.com", "pass123", authorities);

        assertEquals(2L, userDetails.getId());
        assertEquals("anna", userDetails.getUsername());
        assertEquals("anna@example.com", userDetails.getEmail());
        assertEquals("pass123", userDetails.getPassword());
        assertEquals(authorities, userDetails.getAuthorities());
    }

    @Test
    void testAccountStatusMethods() {
        UserDetailsImpl userDetails = new UserDetailsImpl();
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void testEqualsAndHashCode() {
        UserDetailsImpl u1 = new UserDetailsImpl(1L, "a", "a@a", "pass", List.of());
        UserDetailsImpl u2 = new UserDetailsImpl(1L, "b", "b@b", "pass2", List.of());
        UserDetailsImpl u3 = new UserDetailsImpl(2L, "a", "a@a", "pass", List.of());

        assertEquals(u1, u2);
        assertNotEquals(u1, u3);
        assertNotEquals(u2, u3);
    }
}