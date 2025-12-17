package com.ecommerce.entity;

public enum SellerStatus {
    UNVERIFIED,   // Mới tạo acc, chưa upload giấy phép kinh doanh
    PENDING,      // Đã upload ảnh, đang chờ Admin duyệt
    APPROVED,     // Admin đã duyệt -> Được phép bán
    REJECTED      // Admin từ chối -> Yêu cầu up lại
}
