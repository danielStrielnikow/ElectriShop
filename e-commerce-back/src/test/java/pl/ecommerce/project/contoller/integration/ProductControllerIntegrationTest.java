package pl.ecommerce.project.contoller.integration;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.ecommerce.project.payload.dto.ProductDTO;
import pl.ecommerce.project.service.ProductService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev-test")
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;


    @Test
    @WithMockUser(roles = "ADMIN")
    void addProduct_withJson_returns201_andProductDto() throws Exception {
        // Przygotuj dane produktu
        ProductDTO savedDto = new ProductDTO();
        savedDto.setProductId(1L);
        savedDto.setProductName("Test Produkt");
        savedDto.setDescription("Opis testowy");
        savedDto.setQuantity(5);
        savedDto.setPrice(123.45);
        savedDto.setDiscount(10.0);
        savedDto.setSpecialPrice(100.0);

        // Mockowanie serwisu
        Mockito.when(productService.addProduct(eq(1L), any(ProductDTO.class))).thenReturn(savedDto);

        mockMvc.perform(post("/api/admin/categories/{categoryId}/product", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "productName": "Test Produkt",
                    "description": "Opis testowy",
                    "quantity": 5,
                    "price": 123.45,
                    "discount": 10.0,
                    "specialPrice": 100.0
                }
            """)
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productName").value("Test Produkt"))
                .andExpect(jsonPath("$.description").value("Opis testowy"))
                .andExpect(jsonPath("$.quantity").value(5))
                .andExpect(jsonPath("$.price").value(123.45))
                .andExpect(jsonPath("$.discount").value(10.0))
                .andExpect(jsonPath("$.specialPrice").value(100.0));
    }


}