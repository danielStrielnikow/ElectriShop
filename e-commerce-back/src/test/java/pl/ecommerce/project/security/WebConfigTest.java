package pl.ecommerce.project.security;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistration;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class WebConfigTest {

    @Test
    void testAddCorsMappings() {
        // Given
        WebConfig webConfig = new WebConfig();
        String testUrl = "http://localhost:3000";
        ReflectionTestUtils.setField(webConfig, "frontEndUrl", testUrl);

        CorsRegistry registry = mock(CorsRegistry.class);
        CorsRegistration registration = mock(CorsRegistration.class);

        when(registry.addMapping("/**")).thenReturn(registration);
        when(registration.allowedOrigins(anyString())).thenReturn(registration);
        when(registration.allowedMethods(any(String[].class))).thenReturn(registration);
        when(registration.allowedHeaders(anyString())).thenReturn(registration);
        when(registration.allowCredentials(anyBoolean())).thenReturn(registration);

        // When
        webConfig.addCorsMappings(registry);

        // Then
        verify(registry, times(1)).addMapping("/**");
        verify(registration, times(1)).allowedOrigins(testUrl);
        verify(registration, times(1)).allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
        verify(registration, times(1)).allowedHeaders("*");
        verify(registration, times(1)).allowCredentials(true);
    }
}