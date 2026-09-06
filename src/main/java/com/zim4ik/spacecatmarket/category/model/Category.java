package com.zim4ik.spacecatmarket.category.model;


import com.zim4ik.spacecatmarket.category.exception.InvalidCategoryException;
import com.zim4ik.spacecatmarket.product.exception.InvalidProductException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    public static Category create (String name, String description) {
       Category category = new Category();

       category.updateName(name);
       category.updateDescription(description);
       return category;
    }

    public void updateName(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new InvalidCategoryException(
                    "Name is required"
            );
        }

        if(newName.length() >= 256) {
            throw new InvalidCategoryException(
                    "Name is too long"
            );
        }
        this.name = newName;
    }

    public void updateDescription(String newDescription) {
        this.description = newDescription;
    }
}
