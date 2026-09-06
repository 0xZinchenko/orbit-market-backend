package com.zim4ik.spacecatmarket.order.repository;

import com.zim4ik.spacecatmarket.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
