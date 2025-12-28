package com.ecommerce.util;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuHelper {
    
    public static void setMenuItems(HttpServletRequest request, String role, String currentPath) {
        List<Map<String, String>> menuItems = new ArrayList<>();
        
        switch (role) {
            case "BUYER":
                menuItems.add(createMenuItem("/home", "Trang chủ", "🏠"));
                menuItems.add(createMenuItem("/products", "Sản phẩm", "🛍️"));
                menuItems.add(createMenuItem("/orders", "Đơn hàng", "📦"));
                break;
                
            case "SELLER":
                menuItems.add(createMenuItem("/seller/dashboard", "Tổng quan", "📊"));
                menuItems.add(createMenuItem("/seller/products", "Kho hàng", "📦"));
                menuItems.add(createMenuItem("/seller/orders", "Đơn hàng", "📋"));
                menuItems.add(createMenuItem("/seller/settings", "Cài đặt", "⚙️"));
                break;
                
            case "SHIPPER":
                menuItems.add(createMenuItem("/shipper/orders", "Đơn giao hàng", "🚚"));
                break;
                
            case "ADMIN":
                menuItems.add(createMenuItem("/admin/statistics", "Thống kê", "📊"));
                menuItems.add(createMenuItem("/admin/manageUser", "Quản lí người dùng", "👥"));
                menuItems.add(createMenuItem("/admin/approveSeller", "Duyệt người bán", "🏪"));
                menuItems.add(createMenuItem("/admin/approveProduct", "Duyệt sản phẩm", "📦"));
                break;
                
            default:
                menuItems.add(createMenuItem("/home", "Trang chủ", "🏠"));
                menuItems.add(createMenuItem("/products", "Sản phẩm", "🛍️"));
                break;
        }
        
        request.setAttribute("menuItems", menuItems);
        request.setAttribute("currentPath", currentPath);
    }
    
    private static Map<String, String> createMenuItem(String path, String label, String icon) {
        Map<String, String> item = new HashMap<>();
        item.put("path", path);
        item.put("label", label);
        item.put("icon", icon);
        return item;
    }
}
