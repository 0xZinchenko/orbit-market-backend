package com.zim4ik.spacecatmarket.order.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    @Column(name = "order_number", nullable = false, unique = true, updatable = false)
    private String orderNumber;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public static Order create(List<OrderItem> items) {
        Order order = new Order();
        order.orderNumber = UUID.randomUUID().toString();
        order.updateItems(items);
        return order;
    }

    public void updateItems(List<OrderItem> newItems) {
        items.clear();
        for (OrderItem item : newItems) {
            item.assignOrder(this);
            items.add(item);
        }
    }
}
