package com.ecommerce.util;

import jakarta.servlet.http.HttpServletRequest;

public class AppConfig {
    
    // Base URL cho production (có thể config từ environment variable)
    private static final String BASE_URL = System.getenv("APP_BASE_URL");
    
    /**
     * Lấy base URL của application
     * Ưu tiên: Environment Variable > Request > Default localhost
     */
    public static String getBaseUrl(HttpServletRequest request) {
        // Nếu có config từ environment variable
        if (BASE_URL != null && !BASE_URL.isEmpty()) {
            return BASE_URL;
        }
        
        // Nếu có request, build từ request
        if (request != null) {
            String scheme = request.getScheme();
            String serverName = request.getServerName();
            int serverPort = request.getServerPort();
            String contextPath = request.getContextPath();
            
            StringBuilder url = new StringBuilder();
            url.append(scheme).append("://").append(serverName);
            
            // Chỉ thêm port nếu không phải port mặc định
            if ((scheme.equals("http") && serverPort != 80) || 
                (scheme.equals("https") && serverPort != 443)) {
                url.append(":").append(serverPort);
            }
            
            url.append(contextPath);
            return url.toString();
        }
        
        // Fallback về localhost (chỉ dùng cho development)
        return "http://localhost:8080/server";
    }
    
    /**
     * Lấy base URL từ environment variable hoặc default
     * Dùng khi không có HttpServletRequest
     */
    public static String getBaseUrl() {
        return getBaseUrl(null);
    }
}
