package com.zim4ik.spacecatmarket.order.repository.projection;

public interface ProductPurchaseCountProjection {

    Long getProductId();

    String getProductName();

    Long getTotalQuantity();
}
