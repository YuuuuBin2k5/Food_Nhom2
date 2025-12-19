package com.ecommerce.servlet;

import com.ecommerce.entity.Admin;
import com.ecommerce.entity.NotificationType;
import com.ecommerce.entity.Shipper;
import com.ecommerce.service.NotificationService;
import com.ecommerce.util.DBUtil;
import com.google.gson.Gson;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/test-notifications")
public class TestNotificationServlet extends HttpServlet {
    
    private final NotificationService notificationService = new NotificationService();
    private final Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        try {
            Map<String, Object> result = new HashMap<>();
            EntityManager em = DBUtil.getEmFactory().createEntityManager();
            
            try {
                // Get all admins
                TypedQuery<Admin> adminQuery = em.createQuery("SELECT a FROM Admin a", Admin.class);
                List<Admin> admins = adminQuery.getResultList();
                result.put("admins", admins.stream().map(a -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("userId", a.getUserId());
                    map.put("email", a.getEmail());
                    map.put("fullName", a.getFullName());
                    return map;
                }).toList());
                
                // Get all shippers
                TypedQuery<Shipper> shipperQuery = em.createQuery("SELECT s FROM Shipper s", Shipper.class);
                List<Shipper> shippers = shipperQuery.getResultList();
                result.put("shippers", shippers.stream().map(s -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("userId", s.getUserId());
                    map.put("email", s.getEmail());
                    map.put("fullName", s.getFullName());
                    return map;
                }).toList());
                
                // Count notifications
                Long notificationCount = em.createQuery("SELECT COUNT(n) FROM Notification n", Long.class)
                    .getSingleResult();
                result.put("totalNotifications", notificationCount);
                
                result.put("success", true);
                result.put("message", "Found " + admins.size() + " admins, " + shippers.size() + " shippers, " + notificationCount + " notifications");
                
            } finally {
                em.close();
            }
            
            resp.getWriter().write(gson.toJson(result));
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        try {
            Map<String, Object> result = new HashMap<>();
            EntityManager em = DBUtil.getEmFactory().createEntityManager();
            
            try {
                // Get first admin
                TypedQuery<Admin> adminQuery = em.createQuery("SELECT a FROM Admin a", Admin.class);
                adminQuery.setMaxResults(1);
                List<Admin> admins = adminQuery.getResultList();
                
                if (admins.isEmpty()) {
                    result.put("success", false);
                    result.put("error", "No admin found");
                    resp.getWriter().write(gson.toJson(result));
                    return;
                }
                
                Admin admin = admins.get(0);
                
                // Create test notification
                notificationService.createNotification(
                    admin.getUserId(),
                    NotificationType.NEW_SELLER_REGISTRATION,
                    "Test Notification",
                    "This is a test notification for admin: " + admin.getFullName(),
                    null
                );
                
                result.put("success", true);
                result.put("message", "Test notification created for admin: " + admin.getEmail());
                result.put("adminId", admin.getUserId());
                
            } finally {
                em.close();
            }
            
            resp.getWriter().write(gson.toJson(result));
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            error.put("stackTrace", e.getStackTrace()[0].toString());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
        }
    }
}
