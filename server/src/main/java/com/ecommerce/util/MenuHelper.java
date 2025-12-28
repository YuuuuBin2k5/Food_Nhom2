package com.ecommerce.util;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuHelper {
    
    public static class MenuItem {
        private String path;
        private String label;
        private String icon;
        
        public MenuItem(String path, String label, String icon) {
            this.path = path;
            this.label = label;
            this.icon = icon;
        }
        
        public String getPath() { return path; }
        public String getLabel() { return label; }
        public String getIcon() { return icon; }
    }
    
    public static void setMenuItems(HttpServletRequest request, String role, String currentPath) {
        List<MenuItem> menuItems = new ArrayList<>();
        
        switch (role) {
            case "BUYER":
                menuItems.add(new MenuItem("/home", "Trang chủ", "🏠"));
                menuItems.add(new MenuItem("/products", "Sản phẩm", "🛍️"));
                menuItems.add(new MenuItem("/orders", "Đơn hàng", "📦"));
                break;
                
            case "SELLER":
                menuItems.add(new MenuItem("/seller/dashboard", "Tổng quan", "📊"));
                menuItems.add(new MenuItem("/seller/products", "Kho hàng", "📦"));
                menuItems.add(new MenuItem("/seller/orders", "Đơn hàng", "📋"));
                menuItems.add(new MenuItem("/seller/settings", "Cài đặt", "⚙️"));
                break;
                
            case "SHIPPER":
                menuItems.add(new MenuItem("/shipper/orders", "Đơn giao hàng", "🚚"));
                break;
                
            case "ADMIN":
                menuItems.add(new MenuItem("/admin/statistics", "Dashboard", "📊"));
                menuItems.add(new MenuItem("/admin/manageUser", "Người dùng", "👥"));
                menuItems.add(new MenuItem("/admin/approveSeller", "Duyệt Seller", "🏪"));
                menuItems.add(new MenuItem("/admin/approveProduct", "Duyệt SP", "📦"));
                break;
                
            default:
                menuItems.add(new MenuItem("/home", "Trang chủ", "🏠"));
                menuItems.add(new MenuItem("/products", "Sản phẩm", "🛍️"));
                break;
        }
        
        request.setAttribute("menuItems", menuItems);
        request.setAttribute("currentPath", currentPath);
    }
}
