package pl.ecommerce.project.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.http.ResponseCookie;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.util.WebUtils;
import pl.ecommerce.project.security.services.UserDetailsImpl;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private final String jwtSecret = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"; // 64 base64 chars = 48 bytes
    private final int jwtExpirationMs = 1000 * 60 * 60;
    private final String jwtCookie = "jwt-cookie";

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", jwtExpirationMs);
        ReflectionTestUtils.setField(jwtUtils, "jwtCookie", jwtCookie);
    }

    @Test
    void testGetJwtFromCookies_returnsToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie cookie = new Cookie(jwtCookie, "token123");
        try (MockedStatic<WebUtils> mockedWebUtils = Mockito.mockStatic(WebUtils.class)) {
            mockedWebUtils.when(() -> WebUtils.getCookie(request, jwtCookie)).thenReturn(cookie);

            String result = jwtUtils.getJwtFromCookies(request);
            assertEquals("token123", result);
        }
    }

    @Test
    void testGetJwtFromCookies_returnsNullIfNoCookie() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        try (MockedStatic<WebUtils> mockedWebUtils = Mockito.mockStatic(WebUtils.class)) {
            mockedWebUtils.when(() -> WebUtils.getCookie(request, jwtCookie)).thenReturn(null);

            String result = jwtUtils.getJwtFromCookies(request);
            assertNull(result);
        }
    }

    @Test
    void testGenerateJwtCookie_containsToken() {
        UserDetailsImpl user = new UserDetailsImpl(1L, "user", "email", "pass", Collections.emptyList());
        ResponseCookie cookie = jwtUtils.generateJwtCookie(user);

        assertEquals(jwtCookie, cookie.getName());
        assertNotNull(cookie.getValue());
        assertEquals("/api", cookie.getPath());
        assertTrue(cookie.isHttpOnly());
    }

    @Test
    void testGetCleanJwtCookie() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        assertEquals(jwtCookie, cookie.getName());
        assertEquals("", cookie.getValue());
        assertEquals("/api", cookie.getPath());
    }

    @Test
    void testGenerateTokenFromUsername_andGetUserNameFromJwtToken() {
        String username = "user";
        String token = jwtUtils.generateTokenFromUsername(username);
        String extracted = jwtUtils.getUserNameFromJwtToken(token);
        assertEquals(username, extracted);
    }

    @Test
    void testValidateJwtToken_validToken() {
        String token = jwtUtils.generateTokenFromUsername("user");
        assertTrue(jwtUtils.validateJwtToken(token));
    }

    @Test
    void testValidateJwtToken_invalidToken() {
        assertFalse(jwtUtils.validateJwtToken("invalid.token.string"));
    }

    @Test
    void testValidateJwtToken_expiredToken() {
        String token = io.jsonwebtoken.Jwts.builder()
                .subject("user")
                .issuedAt(new Date(System.currentTimeMillis() - 2000))
                .expiration(new Date(System.currentTimeMillis() - 1000))
                .signWith((javax.crypto.SecretKey) ReflectionTestUtils.invokeMethod(jwtUtils, "key"))
                .compact();
        assertFalse(jwtUtils.validateJwtToken(token));
    }
}