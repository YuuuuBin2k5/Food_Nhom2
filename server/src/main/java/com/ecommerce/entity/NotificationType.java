package com.ecommerce.entity;

public enum NotificationType {
    // Buyer notifications
    ORDER_CONFIRMED("Đơn hàng đã được xác nhận"),
    ORDER_SHIPPING("Đơn hàng đang được giao"),
    ORDER_DELIVERED("Đơn hàng đã được giao"),
    ORDER_CANCELLED("Đơn hàng đã bị hủy"),
    
    // Seller notifications
    NEW_ORDER("Đơn hàng mới"),
    PRODUCT_APPROVED("Sản phẩm đã được duyệt"),
    PRODUCT_REJECTED("Sản phẩm bị từ chối"),
    SELLER_APPROVED("Tài khoản seller đã được duyệt"),
    SELLER_REJECTED("Tài khoản seller bị từ chối"),
    
    // Shipper notifications
    NEW_DELIVERY("Đơn hàng mới cần giao"),
    
    // Admin notifications
    NEW_SELLER_REGISTRATION("Seller mới đăng ký"),
    NEW_PRODUCT_PENDING("Sản phẩm mới chờ duyệt");
    
    private final String description;
    
    NotificationType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
