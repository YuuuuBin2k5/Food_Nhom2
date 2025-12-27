package com.mycompany.entity;

public enum ActionType {
    // ===== SELLER ACTIONS =====
    // Seller được duyệt/từ chối bởi admin
    SELLER_APPROVED,        // Seller được duyệt bởi admin
    SELLER_REJECTED,        // Seller bị từ chối bởi admin
    
    // Seller quản lý sản phẩm
    SELLER_CREATE_PRODUCT,  // Seller đăng sản phẩm mới
    SELLER_HIDE_PRODUCT,    // Seller ẩn sản phẩm
    SELLER_SHOW_PRODUCT,    // Seller hiện sản phẩm
    SELLER_UPDATE_PRODUCT,  // Seller sửa thông tin sản phẩm
    
    // Seller bị ban/unban
    SELLER_BANNED,          // Seller bị ban bởi admin
    SELLER_UNBANNED,        // Seller được unban bởi admin
    
    // ===== PRODUCT ACTIONS (by Admin) =====
    PRODUCT_APPROVED,       // Sản phẩm được duyệt bởi admin
    PRODUCT_REJECTED,       // Sản phẩm bị từ chối bởi admin
    
    // ===== BUYER ACTIONS =====
    BUYER_PLACE_ORDER,      // Buyer đặt sản phẩm
    BUYER_PAY_ORDER,        // Buyer thanh toán hóa đơn
    BUYER_CANCEL_ORDER,     // Buyer hủy hóa đơn
    BUYER_REVIEW_PRODUCT,   // Buyer đánh giá sản phẩm
    
    // Buyer bị ban/unban
    BUYER_BANNED,           // Buyer bị ban bởi admin
    BUYER_UNBANNED,         // Buyer được unban bởi admin
    
    // ===== SHIPPER ACTIONS =====
    SHIPPER_ACCEPT_ORDER,   // Shipper nhận hóa đơn
    SHIPPER_CANCEL_ORDER,   // Shipper hủy đơn
    SHIPPER_COMPLETE_ORDER, // Shipper hoàn thành đơn
    
    // Shipper bị ban/unban
    SHIPPER_BANNED,         // Shipper bị ban bởi admin
    SHIPPER_UNBANNED,       // Shipper được unban bởi admin
    
    // ===== GENERAL ACTIONS =====
    LOGIN,
    LOGOUT,
    UPDATE_PROFILE
}
