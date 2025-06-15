package pl.ecommerce.project.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.ecommerce.project.security.jwt.AuthEntryPointJwt;
import pl.ecommerce.project.security.services.UserDetailsServiceImpl;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WebSecurityConfigTest {

    @Test
    void allBeansArePresent() throws Exception {
        // Arrange
        UserDetailsServiceImpl userDetailsService = mock(UserDetailsServiceImpl.class);
        AuthEntryPointJwt authEntryPointJwt = mock(AuthEntryPointJwt.class);

        WebSecurityConfig config = new WebSecurityConfig();
        // Wstrzyknij zależności
        config.userDetailsService = userDetailsService;
        config.unauthorizedHandler = authEntryPointJwt;

        // Sprawdź AuthTokenFilter
        assertNotNull(config.authenticationJwtTokenFilter());

        // Sprawdź PasswordEncoder
        PasswordEncoder encoder = config.passwordEncoder();
        assertNotNull(encoder);
        assertTrue(encoder.matches("password", encoder.encode("password")));

        // Sprawdź DaoAuthenticationProvider
        DaoAuthenticationProvider provider = config.authenticationProvider();
        assertNotNull(provider);

        // Odczytaj pole protected przez refleksję:
        Field field = DaoAuthenticationProvider.class.getDeclaredField("userDetailsService");
        field.setAccessible(true);
        Object injectedService = field.get(provider);
        assertSame(userDetailsService, injectedService);

        // Sprawdź AuthenticationManager (z mockiem configu)
        AuthenticationConfiguration authConfig = mock(AuthenticationConfiguration.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        when(authConfig.getAuthenticationManager()).thenReturn(authenticationManager);
        assertSame(authenticationManager, config.authenticationManager(authConfig));

        // Sprawdź WebSecurityCustomizer
        WebSecurityCustomizer customizer = config.webSecurityCustomizer();
        assertNotNull(customizer);
    }

    @Test
    void securityFilterChainReturnsFilterChain() throws Exception {
        // Arrange
        UserDetailsServiceImpl userDetailsService = mock(UserDetailsServiceImpl.class);
        AuthEntryPointJwt authEntryPointJwt = mock(AuthEntryPointJwt.class);
        WebSecurityConfig config = new WebSecurityConfig();
        config.userDetailsService = userDetailsService;
        config.unauthorizedHandler = authEntryPointJwt;

        // Mock HttpSecurity (RETURNS_DEEP_STUBS by default)
        var http = mock(org.springframework.security.config.annotation.web.builders.HttpSecurity.class, RETURNS_DEEP_STUBS);

        // Weryfikacja, że metoda nie rzuca wyjątków
        assertDoesNotThrow(() -> config.filterChain(http));
    }
}