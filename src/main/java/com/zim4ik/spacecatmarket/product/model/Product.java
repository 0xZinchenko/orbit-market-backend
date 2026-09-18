package com.zim4ik.spacecatmarket.product.model;

import com.zim4ik.spacecatmarket.category.model.Category;
import com.zim4ik.spacecatmarket.product.exception.InvalidProductException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    public static Product create(String name, BigDecimal price) {
        Product product = new Product();

        product.updateName(name);
        product.changePrice(price);
        return product;
    }

    public void changePrice(BigDecimal newPrice) {
        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidProductException(
                    "Price must not be null and cannot be negative"
            );
        }

        this.price = newPrice;
    }

    public void updateName(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new InvalidProductException(
                    "Name is required"
            );
        }

        if(newName.length() >= 256) {
            throw new InvalidProductException(
                    "Name is too long"
            );
        }
        this.name = newName;
    }

    public void assignCategory(Category category) {
        this.category = category;
    }
}
