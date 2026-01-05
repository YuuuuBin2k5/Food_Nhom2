package com.ecommerce.service;

import com.ecommerce.dto.NotificationDTO;
import com.ecommerce.entity.Notification;
import com.ecommerce.entity.NotificationType;
import com.ecommerce.util.DBUtil; // Import công cụ kết nối của em
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationService {

    /**
     * 1. Tạo thông báo (Dùng chung EntityManager - Thường dùng khi đặt hàng)
     * Servlet/Service khác sẽ mở Transaction và truyền EntityManager vào đây.
     */
    public NotificationDTO createNotification(EntityManager em, String userId, NotificationType type, String title, String message, Long referenceId) {
        try {
            Notification notification = new Notification(userId, type, title, message, referenceId);
            em.persist(notification);
            
            // Đẩy dữ liệu xuống DB ngay để lấy ID tự động cho DTO
            em.flush(); 
            
            return convertToDTO(notification);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo thông báo: " + e.getMessage(), e);
        }
    }

    /**
     * 2. Tạo thông báo đơn lẻ (Tự quản lý Transaction - Dùng cho các vụ việc riêng lẻ)
     */
    public NotificationDTO createNotification(String userId, NotificationType type, String title, String message, Long referenceId) {
        // Lấy EntityManager từ kho tổng DBUtil
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            NotificationDTO dto = createNotification(em, userId, type, title, message, referenceId);
            em.getTransaction().commit();
            return dto;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Lỗi Transaction thông báo", e);
        } finally {
            em.close(); // Luôn luôn đóng để tránh rò rỉ bộ nhớ
        }
    }

    /**
     * 3. Lấy danh sách thông báo của User (Có phân trang)
     */
    public List<NotificationDTO> getNotificationsByUserId(String userId, int limit, int offset) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            TypedQuery<Notification> query = em.createQuery(
                "SELECT n FROM Notification n WHERE n.userId = :userId ORDER BY n.createdAt DESC",
                Notification.class
            );
            query.setParameter("userId", userId);
            query.setMaxResults(limit);
            query.setFirstResult(offset);
            
            return query.getResultList().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    /**
     * 4. Đếm số thông báo chưa đọc (Để hiện số trên icon quả chuông 🔔)
     */
    public long getUnreadCount(String userId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.isRead = false",
                Long.class
            );
            query.setParameter("userId", userId);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * 5. Đánh dấu đã đọc một thông báo
     */
    public void markAsRead(Long notificationId, String userId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Notification notification = em.find(Notification.class, notificationId);
            
            // Bảo mật: Kiểm tra xem thông báo có đúng của User này không
            if (notification != null && notification.getUserId().equals(userId)) {
                notification.setIsRead(true);
                em.merge(notification);
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    /**
     * 6. Đánh dấu tất cả là đã đọc
     */
    public void markAllAsRead(String userId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("UPDATE Notification n SET n.isRead = true WHERE n.userId = :userId AND n.isRead = false")
                .setParameter("userId", userId)
                .executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    /**
     * Chuyển đổi từ Entity sang DTO (Bảo mật & Tối ưu dữ liệu)
     */
    private NotificationDTO convertToDTO(Notification notification) {
        return new NotificationDTO(
            notification.getId(),
            notification.getUserId(),
            notification.getType(),
            notification.getTitle(),
            notification.getMessage(),
            notification.getReferenceId(),
            notification.getIsRead(),
            notification.getCreatedAt()
        );
    }
}