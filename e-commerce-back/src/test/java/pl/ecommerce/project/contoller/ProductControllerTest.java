package pl.ecommerce.project.contoller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import pl.ecommerce.project.controller.ProductController;
import pl.ecommerce.project.exception.APIException;
import pl.ecommerce.project.payload.ProductResponse;
import pl.ecommerce.project.payload.dto.ProductDTO;
import pl.ecommerce.project.service.CloudinaryService;
import pl.ecommerce.project.service.ProductService;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductControllerTest {

    @Mock
    private ProductService productService;
    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private ProductController productController;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productController = new ProductController(productService, cloudinaryService);
    }

    @Test
    void getAllProducts_returnsProducts() {
        ProductResponse response = new ProductResponse(Collections.emptyList(), 0, 10, 0L, 1, true);
        when(productService.getAllProducts(0, 10, "productId", "asc", null, null)).thenReturn(response);

        ResponseEntity<ProductResponse> result = productController.getAllProducts(null, null, 0, 10, "productId", "asc");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(productService).getAllProducts(0, 10, "productId", "asc", null, null);
    }

    @Test
    void getProductByCategory_returnsProductResponse() {
        ProductResponse response = new ProductResponse(Collections.emptyList(), 0, 10, 0L, 1, true);
        when(productService.searchByCategory(1L, 0, 10, "productId", "asc")).thenReturn(response);

        ResponseEntity<ProductResponse> result = productController.getProductByCategory(1L, 0, 10, "productId", "asc");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(productService).searchByCategory(1L, 0, 10, "productId", "asc");
    }

    @Test
    void getProductsByKeyWord_returnsProductResponse() {
        ProductResponse response = new ProductResponse(Collections.emptyList(), 0, 10, 0L, 1, true);
        when(productService.searchProductByKeyWord("lap", 0, 10, "productId", "asc")).thenReturn(response);

        ResponseEntity<ProductResponse> result = productController.getProductsByKeyWord("lap", 0, 10, "productId", "asc");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(productService).searchProductByKeyWord("lap", 0, 10, "productId", "asc");
    }

    @Test
    void addProduct_returnsCreated() {
        ProductDTO dto = new ProductDTO();
        when(productService.addProduct(2L, dto)).thenReturn(dto);

        ResponseEntity<ProductDTO> result = productController.addProduct(dto, 2L);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(dto, result.getBody());
        verify(productService).addProduct(2L, dto);
    }

    @Test
    void updateProduct_returnsOk() {
        ProductDTO dto = new ProductDTO();
        when(productService.updateProduct(3L, dto)).thenReturn(dto);

        ResponseEntity<ProductDTO> result = productController.updateProduct(dto, 3L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(dto, result.getBody());
        verify(productService).updateProduct(3L, dto);
    }

    @Test
    void updateProductImage_returnsOk() throws IOException {
        MockMultipartFile file = new MockMultipartFile("image", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "abc".getBytes());
        ProductDTO dto = new ProductDTO();
        when(productService.updateProductImage(4L, file)).thenReturn(dto);

        ResponseEntity<ProductDTO> result = productController.updateProductImage(4L, file);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(dto, result.getBody());
        verify(productService).updateProductImage(4L, file);
    }

    @Test
    void getProductImageUrl_returnsOk() {
        when(productService.getProductImageUrl(5L)).thenReturn("url.jpg");

        ResponseEntity<String> result = productController.getProductImageUrl(5L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("url.jpg", result.getBody());
        verify(productService).getProductImageUrl(5L);
    }

    @Test
    void uploadProductImage_returnsOk() throws IOException {
        MockMultipartFile file = new MockMultipartFile("image", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "abc".getBytes());
        when(cloudinaryService.uploadImage(file, "products")).thenReturn("http://cloud/image.jpg");

        ResponseEntity<String> result = productController.uploadProductImage(6L, file);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("http://cloud/image.jpg", result.getBody());
        verify(cloudinaryService).uploadImage(file, "products");
    }

    @Test
    void uploadProductImage_returnsErrorOnException() throws IOException {
        MockMultipartFile file = new MockMultipartFile("image", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "abc".getBytes());
        when(cloudinaryService.uploadImage(file, "products")).thenThrow(new IOException("err"));

        ResponseEntity<String> result = productController.uploadProductImage(7L, file);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertTrue(result.getBody().contains("Error uploading image"));
    }

    @Test
    void deleteProductById_returnsOk() {
        ProductDTO dto = new ProductDTO();
        when(productService.deleteProductById(8L)).thenReturn(dto);

        ResponseEntity<ProductDTO> result = productController.deleteProductById(8L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(dto, result.getBody());
        verify(productService).deleteProductById(8L);
    }

    @Test
    void getAllProducts_passesNullsAndDefaults() {
        // Sprawdzenie czy metoda przechodzi z nullami i domyślnymi wartościami
        ProductResponse emptyResponse = new ProductResponse(Collections.emptyList(), 0, 10, 0L, 0, true);
        when(productService.getAllProducts(any(), any(), any(), any(), any(), any())).thenReturn(emptyResponse);

        ResponseEntity<ProductResponse> resp = productController.getAllProducts(null, null, null, null, null, null);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertSame(emptyResponse, resp.getBody());
    }

    @Test
    void getAllProducts_handlesNullInputGracefully() {
        // Sprawdza czy kontroler poradzi sobie z nullami (zamiast domyślnych wartości)
        ProductResponse response = new ProductResponse(Collections.emptyList(), 0, 10, 0L, 1, true);
        when(productService.getAllProducts(null, null, null, null, null, null)).thenReturn(response);

        ResponseEntity<ProductResponse> result = productController.getAllProducts(null, null, null, null, null, null);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void addProduct_throwsAPIException_propagatesToCaller() {
        ProductDTO dto = new ProductDTO();
        when(productService.addProduct(eq(1L), eq(dto))).thenThrow(new APIException("Add error"));

        APIException ex = assertThrows(APIException.class, () -> productController.addProduct(dto, 1L));
        assertEquals("Add error", ex.getMessage());
    }

    @Test
    void updateProduct_throwsAPIException_propagatesToCaller() {
        ProductDTO dto = new ProductDTO();
        when(productService.updateProduct(eq(2L), eq(dto))).thenThrow(new APIException("Update error"));

        APIException ex = assertThrows(APIException.class, () -> productController.updateProduct(dto, 2L));
        assertEquals("Update error", ex.getMessage());
    }

    @Test
    void updateProductImage_throwsIOException_propagates() throws IOException {
        MockMultipartFile file = new MockMultipartFile("image", "photo.jpg", MediaType.IMAGE_JPEG_VALUE, "abc".getBytes());
        when(productService.updateProductImage(eq(3L), eq(file))).thenThrow(new IOException("Upload fail"));

        IOException ex = assertThrows(IOException.class, () -> productController.updateProductImage(3L, file));
        assertEquals("Upload fail", ex.getMessage());
    }

    @Test
    void getProductImageUrl_throwsAPIException_propagates() {
        when(productService.getProductImageUrl(eq(4L))).thenThrow(new APIException("No image"));
        APIException ex = assertThrows(APIException.class, () -> productController.getProductImageUrl(4L));
        assertEquals("No image", ex.getMessage());
    }

    @Test
    void uploadProductImage_handlesNullResponse() throws IOException {
        MockMultipartFile file = new MockMultipartFile("image", "empty.jpg", MediaType.IMAGE_JPEG_VALUE, "xyz".getBytes());
        when(cloudinaryService.uploadImage(file, "products")).thenReturn(null);

        ResponseEntity<String> resp = productController.uploadProductImage(5L, file);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNull(resp.getBody());
    }

    @Test
    void uploadProductImage_handlesIOExceptionAndReturnsError() throws IOException {
        MockMultipartFile file = new MockMultipartFile("image", "fail.jpg", MediaType.IMAGE_JPEG_VALUE, "qwe".getBytes());
        when(cloudinaryService.uploadImage(file, "products")).thenThrow(new IOException("Cloud error"));

        ResponseEntity<String> resp = productController.uploadProductImage(6L, file);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        assertTrue(resp.getBody().contains("Error uploading image: Cloud error"));
    }

    @Test
    void deleteProductById_throwsAPIException_propagates() {
        when(productService.deleteProductById(eq(7L))).thenThrow(new APIException("Delete error"));
        APIException ex = assertThrows(APIException.class, () -> productController.deleteProductById(7L));
        assertEquals("Delete error", ex.getMessage());
    }

    @Test
    void getProductsByKeyWord_nullKeyword() {
        ProductResponse response = new ProductResponse(Collections.emptyList(), 0, 10, 0L, 1, true);
        when(productService.searchProductByKeyWord(null, 0, 10, "productId", "asc")).thenReturn(response);

        ResponseEntity<ProductResponse> result = productController.getProductsByKeyWord(null, 0, 10, "productId", "asc");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void getProductByCategory_throwsAPIException() {
        when(productService.searchByCategory(eq(123L), anyInt(), anyInt(), anyString(), anyString()))
                .thenThrow(new APIException("Category not found"));

        APIException ex = assertThrows(APIException.class,
                () -> productController.getProductByCategory(123L, 0, 10, "productId", "asc"));
        assertEquals("Category not found", ex.getMessage());
    }

    @Test
    void uploadProductImage_handlesIOExceptionBody() throws IOException {
        MockMultipartFile file = new MockMultipartFile("image", "abc.jpg", "image/jpeg", "ab".getBytes());
        when(cloudinaryService.uploadImage(file, "products")).thenThrow(new IOException("msg"));

        ResponseEntity<String> resp = productController.uploadProductImage(2L, file);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        assertTrue(resp.getBody().contains("Error uploading image: msg"));
    }

}