package com.ecommerce.servlet;

import com.ecommerce.dto.NotificationDTO;
import com.ecommerce.service.NotificationService;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "NotificationServlet", urlPatterns = {"/api/notifications/*"})
public class NotificationServlet extends HttpServlet {

    private final NotificationService notificationService = new NotificationService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String userId = (String) session.getAttribute("userId");
        String pathInfo = request.getPathInfo();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/notifications - Get all notifications
                handleGetNotifications(userId, request, response);
            } else if (pathInfo.equals("/unread-count")) {
                // GET /api/notifications/unread-count
                handleGetUnreadCount(userId, response);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            response.getWriter().write(gson.toJson(error));
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String userId = (String) session.getAttribute("userId");
        String pathInfo = request.getPathInfo();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if (pathInfo != null && pathInfo.matches("/\\d+/read")) {
                // PUT /api/notifications/{id}/read
                String[] parts = pathInfo.split("/");
                Long notificationId = Long.parseLong(parts[1]);
                notificationService.markAsRead(notificationId, userId);
                
                Map<String, String> result = new HashMap<>();
                result.put("success", "true");
                response.getWriter().write(gson.toJson(result));
            } else if (pathInfo != null && pathInfo.equals("/read-all")) {
                // PUT /api/notifications/read-all
                notificationService.markAllAsRead(userId);
                
                Map<String, String> result = new HashMap<>();
                result.put("success", "true");
                response.getWriter().write(gson.toJson(result));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            response.getWriter().write(gson.toJson(error));
        }
    }

    // Note: Delete functionality not implemented in NotificationService
    // Users can only mark as read, not delete notifications

    private void handleGetNotifications(String userId, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int page = 0;
        int size = 20;

        String pageParam = request.getParameter("page");
        String sizeParam = request.getParameter("size");

        if (pageParam != null) {
            page = Integer.parseInt(pageParam);
        }
        if (sizeParam != null) {
            size = Integer.parseInt(sizeParam);
        }

        List<NotificationDTO> notifications = notificationService.getNotificationsByUserId(userId, page, size);
        response.getWriter().write(gson.toJson(notifications));
    }

    private void handleGetUnreadCount(String userId, HttpServletResponse response) throws IOException {
        long count = notificationService.getUnreadCount(userId);
        Map<String, Long> result = new HashMap<>();
        result.put("count", count);
        response.getWriter().write(gson.toJson(result));
    }
}
