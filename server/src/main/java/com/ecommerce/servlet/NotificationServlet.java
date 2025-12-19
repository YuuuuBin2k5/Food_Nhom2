package com.ecommerce.servlet;

import com.ecommerce.dto.NotificationDTO;
import com.ecommerce.service.NotificationService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/notifications/*")
public class NotificationServlet extends HttpServlet {
    
    private final NotificationService notificationService = new NotificationService();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new TypeAdapter<LocalDateTime>() {
                private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                
                @Override
                public void write(JsonWriter out, LocalDateTime value) throws IOException {
                    if (value == null) {
                        out.nullValue();
                    } else {
                        out.value(value.format(formatter));
                    }
                }
                
                @Override
                public LocalDateTime read(JsonReader in) throws IOException {
                    return LocalDateTime.parse(in.nextString(), formatter);
                }
            })
            .create();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // Get user ID from session/token
            String userId = getUserIdFromRequest(request);
            if (userId == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"Unauthorized\"}");
                return;
            }
            
            String pathInfo = request.getPathInfo();
            
            // GET /api/notifications/unread-count
            if ("/unread-count".equals(pathInfo)) {
                long count = notificationService.getUnreadCount(userId);
                Map<String, Object> result = new HashMap<>();
                result.put("count", count);
                response.getWriter().write(gson.toJson(result));
                return;
            }
            
            // GET /api/notifications - Get all notifications
            int limit = getIntParameter(request, "limit", 20);
            int offset = getIntParameter(request, "offset", 0);
            
            List<NotificationDTO> notifications = notificationService.getNotificationsByUserId(userId, limit, offset);
            response.getWriter().write(gson.toJson(notifications));
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            String userId = getUserIdFromRequest(request);
            if (userId == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"Unauthorized\"}");
                return;
            }
            
            String pathInfo = request.getPathInfo();
            
            // PUT /api/notifications/read-all
            if ("/read-all".equals(pathInfo)) {
                notificationService.markAllAsRead(userId);
                response.getWriter().write("{\"success\":true}");
                return;
            }
            
            // PUT /api/notifications/{id}/read
            if (pathInfo != null && pathInfo.endsWith("/read")) {
                String idStr = pathInfo.substring(1, pathInfo.indexOf("/read"));
                Long notificationId = Long.parseLong(idStr);
                
                notificationService.markAsRead(notificationId, userId);
                response.getWriter().write("{\"success\":true}");
                return;
            }
            
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Invalid request\"}");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            String userId = getUserIdFromRequest(request);
            if (userId == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"Unauthorized\"}");
                return;
            }
            
            String pathInfo = request.getPathInfo();
            if (pathInfo != null && pathInfo.length() > 1) {
                Long notificationId = Long.parseLong(pathInfo.substring(1));
                notificationService.deleteNotification(notificationId, userId);
                response.getWriter().write("{\"success\":true}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Notification ID required\"}");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
    
    /**
     * Get user ID from request (from JWT filter or session)
     */
    private String getUserIdFromRequest(HttpServletRequest request) {
        // Try to get from request attribute (set by JwtAuthFilter)
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj != null) {
            return userIdObj.toString();
        }
        
        // Fallback: try to get from session
        Object sessionUserId = request.getSession().getAttribute("userId");
        if (sessionUserId != null) {
            return sessionUserId.toString();
        }
        
        return null;
    }
    
    /**
     * Get integer parameter with default value
     */
    private int getIntParameter(HttpServletRequest request, String name, int defaultValue) {
        String value = request.getParameter(name);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
}
