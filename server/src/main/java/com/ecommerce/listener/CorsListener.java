package com.ecommerce.listener;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebListener
public class CorsListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("=== CORS Listener Initialized ===");
        
        ServletContext context = sce.getServletContext();
        context.addFilter("SimpleCorsFilter", new Filter() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws java.io.IOException, ServletException {
                
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                HttpServletRequest httpRequest = (HttpServletRequest) request;
                
                System.out.println("CORS Filter: " + httpRequest.getMethod() + " " + httpRequest.getRequestURI());
                
                httpResponse.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
                httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
                httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
                httpResponse.setHeader("Access-Control-Max-Age", "3600");
                
                if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
                    httpResponse.setStatus(HttpServletResponse.SC_OK);
                    return;
                }
                
                chain.doFilter(request, response);
            }
        }).addMappingForUrlPatterns(null, false, "/*");
    }
}
