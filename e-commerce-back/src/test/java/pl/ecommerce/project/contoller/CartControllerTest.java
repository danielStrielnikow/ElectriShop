package pl.ecommerce.project.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.ecommerce.project.model.Cart;
import pl.ecommerce.project.payload.dto.CartDTO;
import pl.ecommerce.project.payload.dto.CartItemDTO;
import pl.ecommerce.project.repo.CartRepository;
import pl.ecommerce.project.service.CartService;
import pl.ecommerce.project.util.AuthUtil;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CartControllerTest {

    @InjectMocks
    private CartController cartController;

    @Mock
    private CartService cartService;

    @Mock
    private AuthUtil authUtil;

    @Mock
    private CartRepository cartRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrUpdateCart() {
        List<CartItemDTO> cartItems = Arrays.asList(new CartItemDTO());
        when(cartService.createOrUpdateCartWithItems(cartItems)).thenReturn("Cart created");

        ResponseEntity<String> response = cartController.createOrUpdateCart(cartItems);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Cart created", response.getBody());
        verify(cartService, times(1)).createOrUpdateCartWithItems(cartItems);
    }

    @Test
    void testAddProductToCart() {
        Long productId = 1L;
        Integer quantity = 2;
        CartDTO cartDTO = new CartDTO();
        when(cartService.addProductToCart(productId, quantity)).thenReturn(cartDTO);

        ResponseEntity<CartDTO> response = cartController.addProductToCart(productId, quantity);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(cartDTO, response.getBody());
        verify(cartService, times(1)).addProductToCart(productId, quantity);
    }

    @Test
    void testGetCarts() {
        List<CartDTO> cartDTOS = Arrays.asList(new CartDTO());
        when(cartService.getAllCarts()).thenReturn(cartDTOS);

        ResponseEntity<List<CartDTO>> response = cartController.getCarts();

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(cartDTOS, response.getBody());
        verify(cartService, times(1)).getAllCarts();
    }

    @Test
    void testGetCartById() {
        String email = "test@example.com";
        Cart cart = new Cart();
        cart.setCartId(1L);
        CartDTO cartDTO = new CartDTO();

        when(authUtil.loggedInEmail()).thenReturn(email);
        when(cartRepository.findCartByEmail(email)).thenReturn(cart);
        when(cartService.getCart(email, cart.getCartId())).thenReturn(cartDTO);

        ResponseEntity<CartDTO> response = cartController.getCartById();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cartDTO, response.getBody());
        verify(authUtil, times(1)).loggedInEmail();
        verify(cartRepository, times(1)).findCartByEmail(email);
        verify(cartService, times(1)).getCart(email, cart.getCartId());
    }

    @Test
    void testUpdateCartProduct() {
        Long productId = 1L;
        String operation = "add";
        CartDTO cartDTO = new CartDTO();
        when(cartService.updateProductQuantityInCart(productId, 1)).thenReturn(cartDTO);

        ResponseEntity<CartDTO> response = cartController.updateCartProduct(productId, operation);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cartDTO, response.getBody());
        verify(cartService, times(1)).updateProductQuantityInCart(productId, 1);
    }

    @Test
    void testDeleteProductFromCart() {
        Long cartId = 1L;
        Long productId = 2L;
        when(cartService.deleteProductFromCart(cartId, productId)).thenReturn("Product deleted");

        ResponseEntity<String> response = cartController.deleteProductFromCart(cartId, productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product deleted", response.getBody());
        verify(cartService, times(1)).deleteProductFromCart(cartId, productId);
    }
}