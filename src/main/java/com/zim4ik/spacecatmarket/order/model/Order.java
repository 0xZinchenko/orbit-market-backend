package com.zim4ik.spacecatmarket.order.model;

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
@Table(name = "orders")
@Getter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    private List<Product> products;

    public static Order create(List<Product> products) {
        Order order = new Order();
        order.products = products;
        return order;
    }

    public void updateProducts(List<Product> products) {
        this.products = products;
    }

}
