package com.zim4ik.spacecatmarket.order.repository;

import com.zim4ik.spacecatmarket.order.model.OrderItem;
import com.zim4ik.spacecatmarket.order.repository.projection.ProductPurchaseCountProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("""
            SELECT oi.product.id AS productId,
                   oi.product.name AS productName,
                   SUM(oi.quantity) AS totalQuantity
            FROM OrderItem oi
            GROUP BY oi.product.id, oi.product.name
            ORDER BY SUM(oi.quantity) DESC
            """)
    List<ProductPurchaseCountProjection> findMostPurchasedProducts();

    @Query("""
            SELECT oi.product.id AS productId,
                   oi.product.name AS productName,
                   SUM(oi.quantity) AS totalQuantity
            FROM OrderItem oi
            WHERE oi.product.category.id = :categoryId
            GROUP BY oi.product.id, oi.product.name
            ORDER BY SUM(oi.quantity) DESC
            """)
    List<ProductPurchaseCountProjection> findMostPurchasedProductsByCategory(@Param("categoryId") Long categoryId);
}
