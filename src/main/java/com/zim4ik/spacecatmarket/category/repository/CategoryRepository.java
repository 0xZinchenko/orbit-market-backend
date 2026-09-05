package com.zim4ik.spacecatmarket.category.repository;

import com.zim4ik.spacecatmarket.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
