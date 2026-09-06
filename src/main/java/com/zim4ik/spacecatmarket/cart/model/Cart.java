package com.zim4ik.spacecatmarket.cart.model;

import com.zim4ik.spacecatmarket.product.model.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@NoArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    private List<Product> products;

    public static Cart create(List<Product> products) {
        Cart cart = new Cart();
        cart.products = products;
        return cart;
    }

    public void updateProducts(List<Product> products) {
        this.products = products;
    }

}
