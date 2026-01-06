package com.ecommerce.entity;

public enum ActionType {
    
    // ===== SELLER ACTIONS =====
    // 1. Seller được admin duyệt/từ chối
    SELLER_APPROVED,            // Seller được admin duyệt
    SELLER_REJECTED,            // Seller bị admin từ chối
    
    // 2. Seller đăng sản phẩm
    SELLER_CREATE_PRODUCT,      // Seller đăng sản phẩm mới
    
    // 3. Sản phẩm được admin duyệt/từ chối
    PRODUCT_APPROVED,           // Sản phẩm được admin duyệt
    PRODUCT_REJECTED,           // Sản phẩm bị admin từ chối
    
    // 4. Seller chấp nhận/từ chối đơn hàng
    SELLER_ACCEPT_ORDER,        // Seller chấp nhận đơn hàng
    SELLER_REJECT_ORDER,        // Seller từ chối đơn hàng
    
    // 5. Seller bị admin ban/unban
    SELLER_BANNED,              // Seller bị admin ban
    SELLER_UNBANNED,            // Seller được admin unban
    
    // ===== BUYER ACTIONS =====
    // 1. Buyer mua sản phẩm
    BUYER_PLACE_ORDER,          // Buyer đặt hàng
    
    // 2. Buyer thanh toán hóa đơn
    BUYER_PAY_ORDER,            // Buyer thanh toán hóa đơn
    
    // 3. Buyer hủy hóa đơn
    BUYER_CANCEL_ORDER,         // Buyer hủy hóa đơn
    
    // 4. Buyer bị admin ban/unban
    BUYER_BANNED,               // Buyer bị admin ban
    BUYER_UNBANNED,             // Buyer được admin unban
    
    // ===== SHIPPER ACTIONS =====
    // 1. Shipper nhận đơn hàng
    SHIPPER_ACCEPT_ORDER,       // Shipper nhận đơn hàng
    
    // 2. Shipper giao thành công đơn hàng
    SHIPPER_COMPLETE_ORDER,     // Shipper hoàn thành giao hàng
    
    // 3. Shipper bị admin ban/unban
    SHIPPER_BANNED,             // Shipper bị admin ban
    SHIPPER_UNBANNED            // Shipper được admin unban
}