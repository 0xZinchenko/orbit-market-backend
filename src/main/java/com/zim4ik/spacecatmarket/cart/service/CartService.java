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
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;

    @Transactional
    public CartDTO createCart(CartDTO cartDTO) {
        List<CartItem> items = cartDTO.items().stream()
                .map(this::toCartItem)
                .toList();
        Cart cart = Cart.create(items);
        Cart savedCart = cartRepository.save(cart);
        return cartMapper.cartToCartDto(savedCart);
    }

    public List<CartDTO> getAllCarts() {
        return cartRepository.findAll()
                .stream()
                .map(cartMapper::cartToCartDto)
                .toList();
    }

    public CartDTO getCartById(Long id) {
        return cartRepository.findById(id)
                .map(cartMapper::cartToCartDto)
                .orElseThrow(() -> new CartNotFoundException(id));
    }

    @Transactional
    public CartDTO updateCart(CartDTO cartDTO) {
        Cart cart = cartRepository.findById(cartDTO.id())
                .orElseThrow(() -> new CartNotFoundException(cartDTO.id()));

        List<CartItem> items = cartDTO.items().stream()
                .map(this::toCartItem)
                .toList();
        cart.updateItems(items);

        Cart updatedCart = cartRepository.save(cart);

        return cartMapper.cartToCartDto(updatedCart);
    }

    @Transactional
    public void deleteCart(Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new CartNotFoundException(id));

        cartRepository.delete(cart);
    }

    private CartItem toCartItem(CartItemDTO dto) {
        Product product = productRepository.findById(dto.productId())
                .orElseThrow(() -> new ProductNotFoundException(dto.productId()));
        return CartItem.create(product, dto.quantity());
    }
}
