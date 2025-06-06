package pl.ecommerce.project.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pl.ecommerce.project.config.AppErrors;
import pl.ecommerce.project.exception.APIException;
import pl.ecommerce.project.exception.ResourceNotFoundException;
import pl.ecommerce.project.model.Cart;
import pl.ecommerce.project.model.Category;
import pl.ecommerce.project.model.Product;
import pl.ecommerce.project.payload.ProductResponse;
import pl.ecommerce.project.payload.dto.CartDTO;
import pl.ecommerce.project.payload.dto.DTOMapper;
import pl.ecommerce.project.payload.dto.ProductDTO;
import pl.ecommerce.project.repo.CartRepository;
import pl.ecommerce.project.repo.CategoryRepository;
import pl.ecommerce.project.repo.ProductRepository;
import pl.ecommerce.project.service.fileService.FileServiceImpl;

import java.io.IOException;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FileServiceImpl fileService;
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final DTOMapper dtoMapper;
    private final CloudinaryService cloudinaryService;

    @Value("${cloudinary.path}")
    private String imagePath;

    @Value("${cloudinary.url}")
    private String imageBaseUrl;

    @Value("${cloudinary.default_image}")
    private String defaultImage;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          FileServiceImpl fileService,
                          CartRepository cartRepository,
                          CartService cartService,
                          DTOMapper dtoMapper, CloudinaryService cloudinaryService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.fileService = fileService;
        this.cartRepository = cartRepository;
        this.cartService = cartService;
        this.dtoMapper = dtoMapper;
        this.cloudinaryService = cloudinaryService;
    }

    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy,
                                          String sortOrder, String keyword, String category) {
        Pageable pageDetails = getPageDetails(pageNumber, pageSize, sortBy, sortOrder);

        Specification<Product> spec = buildProductSpecification(keyword, category);
        Page<Product> pageProducts = productRepository.findAll(spec, pageDetails);

        return mapToProductResponse(pageProducts);
    }

    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber,
                                            Integer pageSize, String sortBy, String sortOrder) {
        Category category = fetchCategoryById(categoryId);

        Pageable pageDetails = getPageDetails(pageNumber, pageSize, sortBy, sortOrder);
        Page<Product> productPage = productRepository.findByCategoryOrderByPriceAsc(category, pageDetails);

        List<Product> product = productPage.getContent();
        if (product.isEmpty()) {
            throw new APIException(category.getCategoryName()+ " " + AppErrors.ERROR_CATEGORY_NO_PRODUCTS);
        } else {
            return mapToProductResponse(productPage);
        }

    }

    public ProductResponse searchProductByKeyWord(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Pageable pageDetails = getPageDetails(pageNumber, pageSize, sortBy, sortOrder);
        Page<Product> productPage = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%', pageDetails);

        return mapToProductResponse(productPage);
    }

    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
        Category category = fetchCategoryById(categoryId);

        boolean productExists = category.getProducts().stream()
                .anyMatch(product -> product.getProductName().equalsIgnoreCase(productDTO.getProductName()));
        if (productExists) throw new APIException(AppErrors.ERROR_PRODUCT_EXISTS);

        Product product = dtoMapper.mapProductToEntity(productDTO);
        product.setImage(productDTO.getImage() != null ? productDTO.getImage() : defaultImage);
        product.setCategory(category);
        product.setSpecialPrice(calculateSpecialPrice(product.getPrice(), product.getDiscount()));

        Product savedProduct = productRepository.save(product);
        return dtoMapper.mapToProductDTO(savedProduct);
    }

    @Transactional
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product product = fetchProductById(productId);
        product.setSpecialPrice(calculateSpecialPrice(product.getPrice(), product.getDiscount()));

        updateProductFromDTO(productDTO, product);
        Product updatedProduct = productRepository.save(product);

        updateCartsWithProduct(productId);

        return dtoMapper.mapToProductDTO(updatedProduct);
    }

    @Transactional
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product product = fetchProductById(productId);

        // Upload image to Cloudinary
        String imageUrl = cloudinaryService.uploadImage(image, imagePath);

        // Update product with the new image URL
        product.setImage(imageUrl);
        Product updatedProduct = productRepository.save(product);
        return dtoMapper.mapToProductDTO(updatedProduct);
    }

    public String getProductImageUrl(Long productId) {
        Product product = fetchProductById(productId);

        // Return default image if no image is associated
        if (product.getImage() == null || product.getImage().isEmpty()) {
            return defaultImage;
        }

        return product.getImage(); // Return the saved image URL
    }



    @Transactional
    public ProductDTO deleteProductById(Long productId) {
        Product product = fetchProductById(productId);

        List<Cart> carts = cartRepository.findCartByProductId(productId);
        carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(), productId));

        productRepository.delete(product);
        return dtoMapper.mapToProductDTO(product);
    }

    private void updateProductFromDTO(ProductDTO productDTO, Product product) {
        product.setProductName(productDTO.getProductName());
        product.setDescription(productDTO.getDescription());
        product.setQuantity(productDTO.getQuantity());
        product.setDiscount(productDTO.getDiscount());
        product.setPrice(productDTO.getPrice());
        product.setSpecialPrice(productDTO.getSpecialPrice());
    }


    // Helper methods
    private ProductResponse mapToProductResponse(Page<Product> productPage) {
        if (productPage.isEmpty()) throw new APIException(AppErrors.ERROR_NO_PRODUCTS);

        List<ProductDTO> productDTOS = productPage.getContent().stream()
                .map(product -> {
                    ProductDTO productDTO = dtoMapper.mapToProductDTO(product);
                    productDTO.setImage(constructImageUrl(product.getImage()));
                    return productDTO;
                })
                .toList();
        return new ProductResponse(
                productDTOS,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast());
    }

    private Product fetchProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
    }

    private Category fetchCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
    }

    private double calculateSpecialPrice(double price, double discount) {
        if (discount < 0 || discount > 100) {
            throw new IllegalArgumentException("Discount must be between 0 and 100");
        }
        return Math.max(0, price - (discount / 100 * price));
    }

    private  Pageable getPageDetails(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        if (pageNumber < 0 || pageSize <= 0) {
            throw new IllegalArgumentException("Page number must be non-negative and page size must be greater than 0");
        }

        // Ustawienie domyślnego pola do sortowania, jeśli nie podano
        if (sortBy == null || sortBy.isEmpty()) {
            sortBy = "price";
        }

        // Ustawienie domyślnego kierunku sortowania, jeśli nie podano lub jest niepoprawny
        if (sortOrder == null || (!sortOrder.equalsIgnoreCase("asc")
                && !sortOrder.equalsIgnoreCase("desc"))) {
            sortOrder = "asc"; // Domyślnie sortujemy rosnąco
        }

        Sort sortByAndOrder = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        return PageRequest.of(pageNumber, pageSize, sortByAndOrder);
    }

    private void updateCartsWithProduct(Long productId) {
        List<Cart> carts = cartRepository.findCartByProductId(productId);

        List<CartDTO> cartDTOS = carts.stream()
                .map(cart -> {
                    CartDTO cartDTO = dtoMapper.mapToCartDTO(cart);

                    List<ProductDTO> products = cart.getCartItems().stream()
                            .map(p -> dtoMapper.mapToProductDTO(p.getProduct()))
                            .toList();
                    cartDTO.setProducts(products);

                    return cartDTO;
                }).toList();

        cartDTOS.forEach(cart -> cartService.updateProductInCart(cart.getCartId(), productId));
    }

    private String constructImageUrl(String imageName) {
        // Jeśli imageName jest null lub pusty, zwróć URL do domyślnego obrazu
        if (imageName == null || imageName.isEmpty()) {
            return imageBaseUrl.endsWith("/") ? imageBaseUrl + "default-image.jpg" : imageBaseUrl + "/default-image.jpg";
        }

        // Jeśli imageName jest już pełnym URL-em, zwróć go bez zmian
        if (imageName.startsWith("http://") || imageName.startsWith("https://")) {
            return imageName;
        }

        // W przeciwnym razie dodaj base URL do imageName
        return imageBaseUrl.endsWith("/") ? imageBaseUrl + imageName : imageBaseUrl + "/" + imageName;
    }

    private Specification<Product> buildProductSpecification(String keyword, String category) {
        Specification<Product> spec = Specification.where(null);
        if (keyword != null && !keyword.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("productName")),
                            "%" + keyword.toLowerCase() + "%"));
        }

        if (category != null && !category.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("category").get("categoryName"), category));
        }
        return spec;
    }
}
