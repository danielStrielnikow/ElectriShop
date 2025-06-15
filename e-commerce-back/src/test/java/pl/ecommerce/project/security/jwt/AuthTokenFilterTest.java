package pl.ecommerce.project.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import pl.ecommerce.project.security.services.UserDetailsServiceImpl;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthTokenFilterTest {

    private AuthTokenFilter filter;
    private JwtUtils jwtUtils;
    private UserDetailsServiceImpl userDetailsService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain chain;

    @BeforeEach
    void setUp() {
        filter = new AuthTokenFilter();
        jwtUtils = mock(JwtUtils.class);
        userDetailsService = mock(UserDetailsServiceImpl.class);

        // Use reflection to set @Autowired fields
        try {
            var jwtUtilsField = AuthTokenFilter.class.getDeclaredField("jwtUtils");
            jwtUtilsField.setAccessible(true);
            jwtUtilsField.set(filter, jwtUtils);

            var udsField = AuthTokenFilter.class.getDeclaredField("userDetailsService");
            udsField.setAccessible(true);
            udsField.set(filter, userDetailsService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternal_withValidJwt_setsAuthentication() throws ServletException, IOException {
        String jwt = "valid.jwt.token";
        String username = "user";
        UserDetails userDetails = mock(UserDetails.class);
        when(jwtUtils.getJwtFromCookies(request)).thenReturn(jwt);
        when(jwtUtils.validateJwtToken(jwt)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(jwt)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn(java.util.Collections.emptyList());

        filter.doFilterInternal(request, response, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertTrue(SecurityContextHolder.getContext().getAuthentication() instanceof UsernamePasswordAuthenticationToken);
        verify(chain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_withInvalidJwt_doesNotSetAuthentication() throws ServletException, IOException {
        String jwt = "invalid.jwt.token";
        when(jwtUtils.getJwtFromCookies(request)).thenReturn(jwt);
        when(jwtUtils.validateJwtToken(jwt)).thenReturn(false);

        filter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_withNoJwt_doesNotSetAuthentication() throws ServletException, IOException {
        when(jwtUtils.getJwtFromCookies(request)).thenReturn(null);

        filter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_exceptionHandling() throws ServletException, IOException {
        when(jwtUtils.getJwtFromCookies(request)).thenThrow(new RuntimeException("error"));

        filter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }
}