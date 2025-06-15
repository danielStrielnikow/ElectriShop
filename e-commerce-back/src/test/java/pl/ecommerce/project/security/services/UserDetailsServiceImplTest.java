package pl.ecommerce.project.security.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.ecommerce.project.model.Role;
import pl.ecommerce.project.model.User;
import pl.ecommerce.project.model.app.AppRole;
import pl.ecommerce.project.repo.UserRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUserByUsername_UserFound() {
        // given
        User user = new User();
        user.setUserId(1L);
        user.setUserName("john");
        user.setEmail("john@example.com");
        user.setPassword("test123");

        Role role = new Role();
        role.setRoleName(AppRole.ROLE_USER);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        when(userRepository.findByUserName("john")).thenReturn(Optional.of(user));

        // when
        UserDetails userDetails = userDetailsService.loadUserByUsername("john");

        // then
        assertNotNull(userDetails);
        assertEquals("john", userDetails.getUsername());
        assertEquals("test123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // given
        when(userRepository.findByUserName("notfound")).thenReturn(Optional.empty());

        // when & then
        UsernameNotFoundException thrown = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("notfound")
        );
        assertEquals("User Not Found with username: notfound", thrown.getMessage());
    }
}