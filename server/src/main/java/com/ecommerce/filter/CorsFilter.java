package com.ecommerce.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter("/*")
public class CorsFilter implements Filter {

        // Các origin frontend cho môi trường dev (thêm 5174 vì Vite dev server có thể chạy ở 5174)
        private static final String[] ALLOWED_ORIGINS = new String[] {
            "http://localhost:5173",
            "http://localhost:5174",
            "http://127.0.0.1:5173",
            "http://127.0.0.1:5174"
        };

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Không cần init gì
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String origin = httpRequest.getHeader("Origin");

        // Nếu origin được cung cấp và nằm trong danh sách cho phép thì phản hồi lại
        if (origin != null) {
            for (String allowed : ALLOWED_ORIGINS) {
                if (allowed.equals(origin)) {
                    httpResponse.setHeader("Access-Control-Allow-Origin", origin);
                    httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
                    break;
                }
            }
        }

        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        httpResponse.setHeader("Access-Control-Max-Age", "3600");

        // Nếu là preflight request thì trả ngay 200
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // Chuyển tiếp request xuống filter/servlet tiếp theo
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Không cần destroy
    }
}
    