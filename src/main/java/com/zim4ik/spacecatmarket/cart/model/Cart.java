package com.zim4ik.spacecatmarket.cart.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@NoArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    public static Cart create(List<CartItem> items) {
        Cart cart = new Cart();
        cart.updateItems(items);
        return cart;
    }

    public void updateItems(List<CartItem> newItems) {
        items.clear();
        for (CartItem item : newItems) {
            item.assignCart(this);
            items.add(item);
        }
    }
}
