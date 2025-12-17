package com.ecommerce.entity;

public enum ProductStatus {
    PENDING_APPROVAL, // Mới tạo, chờ admin duyệt
    REJECTED,         // Bị admin từ chối (không được đăng)
    ACTIVE,           // Đã duyệt, đang hiển thị cho khách hàng mua
    SOLD_OUT,         // Đã duyệt nhưng hết hàng
    EXPIRED,          // Đã duyệt nhưng hết hạn (ví dụ: đồ ăn hết date)
    HIDDEN            // (Tùy chọn) Người bán tự ẩn đi (tạm ngưng bán)
}