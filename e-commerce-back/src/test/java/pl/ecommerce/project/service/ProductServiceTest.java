package pl.ecommerce.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;
import pl.ecommerce.project.config.AppErrors;
import pl.ecommerce.project.exception.APIException;
import pl.ecommerce.project.model.Category;
import pl.ecommerce.project.model.Product;
import pl.ecommerce.project.payload.ProductResponse;
import pl.ecommerce.project.payload.dto.DTOMapper;
import pl.ecommerce.project.payload.dto.ProductDTO;
import pl.ecommerce.project.repo.CartRepository;
import pl.ecommerce.project.repo.CategoryRepository;
import pl.ecommerce.project.repo.ProductRepository;
import pl.ecommerce.project.service.fileService.FileServiceImpl;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private FileServiceImpl fileService;
    @Mock private CartRepository cartRepository;
    @Mock private CartService cartService;
    @Mock private DTOMapper dtoMapper;
    @Mock private CloudinaryService cloudinaryService;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set Cloudinary property values via reflection (since @Value fields are not set for unit tests)
        productService = new ProductService(productRepository, categoryRepository, fileService, cartRepository,
                cartService, dtoMapper, cloudinaryService);

        // Manually set @Value fields
        TestUtils.setField(productService, "imagePath", "imgPath");
        TestUtils.setField(productService, "imageBaseUrl", "http://images/");
        TestUtils.setField(productService, "defaultImage", "default.jpg");
    }

    @Test
    void getAllProducts_returnsProductResponse() {
        Product product = new Product();
        product.setProductName("Iphone 13");
        Page<Product> productPage = new PageImpl<>(List.of(product));
        when(productRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(productPage);
        when(dtoMapper.mapToProductDTO(any(Product.class))).thenReturn(new ProductDTO());

        ProductResponse response = productService.getAllProducts(0, 10, "price", "asc", null, null);

        assertEquals(1, response.getContent().size());
        verify(productRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getAllProducts_throwsExceptionOnEmpty() {
        Page<Product> productPage = new PageImpl<>(Collections.emptyList());
        when(productRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(productPage);

        APIException ex = assertThrows(APIException.class, () ->
                productService.getAllProducts(0, 10, "price", "asc", null, null));
        assertEquals(AppErrors.ERROR_NO_PRODUCTS, ex.getMessage());
    }

    @Test
    void searchByCategory_returnsProductResponse() {
        Category category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("laptop");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        Product product = new Product();
        product.setCategory(category);
        Page<Product> productPage = new PageImpl<>(List.of(product));
        when(productRepository.findByCategoryOrderByPriceAsc(eq(category), any(Pageable.class))).thenReturn(productPage);
        when(dtoMapper.mapToProductDTO(any(Product.class))).thenReturn(new ProductDTO());

        ProductResponse response = productService.searchByCategory(1L, 0, 10, "price", "asc");
        assertEquals(1, response.getContent().size());
    }

    @Test
    void searchByCategory_throwsAPIExceptionIfEmpty() {
        Category category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("laptop");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        Page<Product> productPage = new PageImpl<>(Collections.emptyList());
        when(productRepository.findByCategoryOrderByPriceAsc(eq(category), any(Pageable.class))).thenReturn(productPage);

        APIException ex = assertThrows(APIException.class, () ->
                productService.searchByCategory(1L, 0, 10, "price", "asc"));
        assertTrue(ex.getMessage().contains(AppErrors.ERROR_CATEGORY_NO_PRODUCTS));
    }

    @Test
    void addProduct_savesProduct() {
        Category category = new Category();
        category.setCategoryId(2L);
        category.setProducts(new ArrayList<>());
        when(categoryRepository.findById(eq(2L))).thenReturn(Optional.of(category));

        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductName("Iphone 13");
        productDTO.setPrice(100.0);
        productDTO.setDiscount(10.0);

        Product mappedProduct = new Product();
        mappedProduct.setProductName("Iphone 13");
        mappedProduct.setPrice(100.0);
        mappedProduct.setDiscount(10.0);
        when(dtoMapper.mapProductToEntity(any(ProductDTO.class))).thenReturn(mappedProduct);
        when(productRepository.save(any(Product.class))).thenReturn(mappedProduct);
        when(dtoMapper.mapToProductDTO(any(Product.class))).thenReturn(productDTO);

        ProductDTO result = productService.addProduct(2L, productDTO);
        assertEquals("Iphone 13", result.getProductName());
    }

    @Test
    void addProduct_throwsAPIExceptionIfExists() {
        Category category = new Category();
        category.setCategoryId(2L);
        Product existing = new Product();
        existing.setProductName("Iphone 13");
        category.setProducts(List.of(existing));
        when(categoryRepository.findById(eq(2L))).thenReturn(Optional.of(category));

        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductName("Iphone 13");

        APIException ex = assertThrows(APIException.class, () -> productService.addProduct(2L, productDTO));
        assertEquals(AppErrors.ERROR_PRODUCT_EXISTS, ex.getMessage());
    }

    @Test
    void updateProduct_updatesAndSaves() {
        Product product = new Product();
        product.setProductId(5L);
        product.setPrice(20.0);
        product.setDiscount(10.0);
        when(productRepository.findById(5L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductName("Iphone 13");
        productDTO.setPrice(20.0);
        productDTO.setDiscount(10.0);

        when(dtoMapper.mapToProductDTO(any(Product.class))).thenReturn(productDTO);
        when(cartRepository.findCartByProductId(eq(5L))).thenReturn(Collections.emptyList());

        ProductDTO result = productService.updateProduct(5L, productDTO);
        assertEquals("Iphone 13", result.getProductName());
    }

    @Test
    void updateProductImage_uploadsAndSetsImage() throws IOException {
        Product product = new Product();
        product.setProductId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cloudinaryService.uploadImage(any(MultipartFile.class), anyString())).thenReturn("http://images/test.jpg");
        when(productRepository.save(any(Product.class))).thenReturn(product);
        ProductDTO productDTO = new ProductDTO();
        when(dtoMapper.mapToProductDTO(any(Product.class))).thenReturn(productDTO);

        MultipartFile file = mock(MultipartFile.class);

        ProductDTO result = productService.updateProductImage(1L, file);
        assertNotNull(result);
        verify(productRepository).save(product);
        assertEquals(productDTO, result);
    }

    @Test
    void getProductImageUrl_returnsImageOrDefault() {
        Product product = new Product();
        product.setProductId(1L);
        product.setImage("img.jpg");
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        String url = productService.getProductImageUrl(1L);
        assertEquals("img.jpg", url);
    }

    @Test
    void getProductImageUrl_returnsDefaultIfEmpty() {
        Product product = new Product();
        product.setProductId(1L);
        product.setImage("");
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        String url = productService.getProductImageUrl(1L);
        assertEquals("default.jpg", url);
    }

    @Test
    void deleteProductById_deletesAndUpdatesCarts() {
        Product product = new Product();
        product.setProductId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.findCartByProductId(1L)).thenReturn(Collections.emptyList());
        when(dtoMapper.mapToProductDTO(any(Product.class))).thenReturn(new ProductDTO());

        ProductDTO res = productService.deleteProductById(1L);
        verify(productRepository).delete(product);
        assertNotNull(res);
    }

    @Test
    void fetchProductById_throwsIfNotFound() {
        when(productRepository.findById(77L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> {
            TestUtils.callPrivateMethod(productService, "fetchProductById", 77L);
        });
    }

    @Test
    void calculateSpecialPrice_throwsOnBadDiscount() {
        assertThrows(RuntimeException.class, () -> {
            TestUtils.callPrivateMethod(productService, "calculateSpecialPrice", 100.0, -1.0);
        });
        assertThrows(RuntimeException.class, () -> {
            TestUtils.callPrivateMethod(productService, "calculateSpecialPrice", 100.0, 101.0);
        });
    }

    @Test
    void getAllProducts_throwsIllegalArgumentExceptionOnBadPageParams() {
        assertThrows(IllegalArgumentException.class, () ->
                productService.getAllProducts(-1, 10, "price", "asc", null, null));
        assertThrows(IllegalArgumentException.class, () ->
                productService.getAllProducts(0, 0, "price", "asc", null, null));
    }

    

    // Helper test utils for reflection
    static class TestUtils {
        static void setField(Object target, String fieldName, Object value) {
            try {
                var field = target.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(target, value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        static void callPrivateMethod(Object target, String methodName, Object... args) {
            try {
                var paramTypes = Arrays.stream(args).map(Object::getClass).toArray(Class[]::new);
                // Handle autoboxing
                for (int i = 0; i < paramTypes.length; i++) {
                    if(paramTypes[i] == Double.class) paramTypes[i] = double.class;
                    if(paramTypes[i] == Long.class) paramTypes[i] = long.class;
                }
                var method = target.getClass().getDeclaredMethod(methodName, paramTypes);
                method.setAccessible(true);
                method.invoke(target, args);
            } catch (Exception e) {
                if (e.getCause() != null) throw new RuntimeException(e.getCause());
                throw new RuntimeException(e);
            }
        }
    }


}