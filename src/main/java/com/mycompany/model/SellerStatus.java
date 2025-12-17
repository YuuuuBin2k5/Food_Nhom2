package com.mycompany.model;

public enum SellerStatus {
    UNVERIFIED,   // Mới tạo acc, chưa upload giấy phép kinh doanh (Admin chưa cần quan tâm)
    PENDING,      // Đã upload ảnh, đang nằm trong hàng chờ Admin duyệt
    APPROVED,     // Admin đã duyệt -> Được phép bán
    REJECTED      // Admin từ chối (Ảnh mờ, sai thông tin...) -> Yêu cầu up lại
}