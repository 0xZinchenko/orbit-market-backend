package com.zim4ik.spacecatmarket.cart.service;

import com.zim4ik.spacecatmarket.cart.dto.CartDTO;
import com.zim4ik.spacecatmarket.cart.dto.CartItemDTO;
import com.zim4ik.spacecatmarket.cart.exception.CartNotFoundException;
import com.zim4ik.spacecatmarket.cart.mapper.CartMapper;
import com.zim4ik.spacecatmarket.cart.model.Cart;
import com.zim4ik.spacecatmarket.cart.model.CartItem;
import com.zim4ik.spacecatmarket.cart.repository.CartRepository;
import com.zim4ik.spacecatmarket.product.exception.ProductNotFoundException;
import com.zim4ik.spacecatmarket.product.model.Product;
import com.zim4ik.spacecatmarket.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private CartService cartService;

    private Product product;
    private Cart cart;
    private CartDTO cartDTO;

    @BeforeEach
    void setUp() {
        product = Product.create("Galaxy Cat", BigDecimal.valueOf(100));
        cart = Cart.create(List.of(CartItem.create(product, 2)));
        cartDTO = new CartDTO(1L, List.of(new CartItemDTO(1L, 2)));
    }

    @Test
    void createCart_savesCartWithResolvedItems() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartMapper.cartToCartDto(cart)).thenReturn(cartDTO);

        CartDTO result = cartService.createCart(cartDTO);

        assertThat(result).isEqualTo(cartDTO);
    }

    @Test
    void createCart_whenProductMissing_throwsProductNotFoundException() {
        CartDTO dto = new CartDTO(null, List.of(new CartItemDTO(404L, 1)));
        when(productRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.createCart(dto))
                .isInstanceOf(ProductNotFoundException.class);

        verify(cartRepository, never()).save(any());
    }

    @Test
    void getAllCarts_returnsMappedList() {
        when(cartRepository.findAll()).thenReturn(List.of(cart));
        when(cartMapper.cartToCartDto(cart)).thenReturn(cartDTO);

        List<CartDTO> result = cartService.getAllCarts();

        assertThat(result).containsExactly(cartDTO);
    }

    @Test
    void getCartById_whenFound_returnsMappedCart() {
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(cartMapper.cartToCartDto(cart)).thenReturn(cartDTO);

        CartDTO result = cartService.getCartById(1L);

        assertThat(result).isEqualTo(cartDTO);
    }

    @Test
    void getCartById_whenNotFound_throwsCartNotFoundException() {
        when(cartRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.getCartById(99L))
                .isInstanceOf(CartNotFoundException.class);
    }

    @Test
    void updateCart_whenFound_updatesItems() {
        Product newProduct = Product.create("Comet Cat", BigDecimal.valueOf(50));
        CartDTO updateDTO = new CartDTO(1L, List.of(new CartItemDTO(2L, 3)));
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(2L)).thenReturn(Optional.of(newProduct));
        when(cartRepository.save(cart)).thenReturn(cart);
        when(cartMapper.cartToCartDto(cart)).thenReturn(updateDTO);

        CartDTO result = cartService.updateCart(updateDTO);

        assertThat(result).isEqualTo(updateDTO);
        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).getProduct()).isEqualTo(newProduct);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(3);
    }

    @Test
    void updateCart_whenNotFound_throwsCartNotFoundException() {
        when(cartRepository.findById(99L)).thenReturn(Optional.empty());
        CartDTO missingDTO = new CartDTO(99L, List.of(new CartItemDTO(1L, 1)));

        assertThatThrownBy(() -> cartService.updateCart(missingDTO))
                .isInstanceOf(CartNotFoundException.class);

        verify(cartRepository, never()).save(any());
    }

    @Test
    void deleteCart_whenFound_deletesCart() {
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        cartService.deleteCart(1L);

        verify(cartRepository).delete(cart);
    }

    @Test
    void deleteCart_whenNotFound_throwsCartNotFoundException() {
        when(cartRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.deleteCart(99L))
                .isInstanceOf(CartNotFoundException.class);

        verify(cartRepository, never()).delete(any());
    }
}
