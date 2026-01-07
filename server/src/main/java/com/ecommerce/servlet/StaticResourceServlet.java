package com.ecommerce.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet để serve các file static (manifest.json, sw.js) 
 * mà không bị redirect bởi security filter
 */
@WebServlet(name = "StaticResourceServlet", urlPatterns = {"/manifest.json", "/sw.js"})
public class StaticResourceServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String path = request.getServletPath();
        
        if ("/manifest.json".equals(path)) {
            response.setContentType("application/manifest+json");
        } else if ("/sw.js".equals(path)) {
            response.setContentType("application/javascript");
            response.setHeader("Service-Worker-Allowed", "/");
        }
        
        response.setCharacterEncoding("UTF-8");
        
        try (InputStream is = getServletContext().getResourceAsStream(path);
             OutputStream os = response.getOutputStream()) {
            
            if (is == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }
}
