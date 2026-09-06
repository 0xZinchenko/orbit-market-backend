package com.zim4ik.spacecatmarket.cart.service;

import com.zim4ik.spacecatmarket.cart.dto.CartDTO;
import com.zim4ik.spacecatmarket.cart.exception.CartNotFoundException;
import com.zim4ik.spacecatmarket.cart.mapper.CartMapper;
import com.zim4ik.spacecatmarket.cart.model.Cart;
import com.zim4ik.spacecatmarket.cart.repository.CartRepository;
import com.zim4ik.spacecatmarket.product.model.Product;
import com.zim4ik.spacecatmarket.product.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;

    public CartDTO createCart(CartDTO cartDTO) {
        List<Product> products = productRepository.findAllById(cartDTO.productIds());
        Cart cart = Cart.create(products);
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

    public CartDTO updateCart(CartDTO cartDTO) {
        Cart cart = cartRepository.findById(cartDTO.id())
                .orElseThrow(() -> new CartNotFoundException(cartDTO.id()));

        List<Product> products = productRepository.findAllById(cartDTO.productIds());
        cart.updateProducts(products);

        Cart updatedCart = cartRepository.save(cart);

        return cartMapper.cartToCartDto(updatedCart);
    }

    public void deleteCart(Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new CartNotFoundException(id));

        cartRepository.delete(cart);
    }
}
