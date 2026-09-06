package com.zim4ik.spacecatmarket.cart.repository;

import com.zim4ik.spacecatmarket.cart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
