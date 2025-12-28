package com.ecommerce.service;

import com.ecommerce.dto.NotificationDTO;
import com.ecommerce.entity.Notification;
import com.ecommerce.entity.NotificationType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationService {
    
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("FoodRescuePU");
    
    /**
     * Create and send a notification (using provided EntityManager - no separate transaction)
     */
    public NotificationDTO createNotification(EntityManager em, String userId, NotificationType type, String title, String message, Long referenceId) {
        try {
            // Create notification entity
            Notification notification = new Notification(userId, type, title, message, referenceId);
            em.persist(notification);
            
            // Flush to get the ID
            em.flush();
            
            // Convert to DTO
            NotificationDTO dto = convertToDTO(notification);
            
            // WebSocket removed - notifications stored in DB only
            // Users can check notifications via polling or page refresh
            
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create notification", e);
        }
    }
    
    
    /**
     * Create and send a notification (convenience method - creates own EntityManager)
     */
    public NotificationDTO createNotification(String userId, NotificationType type, String title, String message, Long referenceId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            NotificationDTO dto = createNotification(em, userId, type, title, message, referenceId);
            em.getTransaction().commit();
            return dto;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Failed to create notification", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Create and send a notification (overload without referenceId)
     */
    public NotificationDTO createNotification(String userId, NotificationType type, String title, String message) {
        return createNotification(userId, type, title, message, null);
    }
    
    /**
     * Get all notifications for a user
     */
    public List<NotificationDTO> getNotificationsByUserId(String userId, int limit, int offset) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Notification> query = em.createQuery(
                "SELECT n FROM Notification n WHERE n.userId = :userId ORDER BY n.createdAt DESC",
                Notification.class
            );
            query.setParameter("userId", userId);
            query.setMaxResults(limit);
            query.setFirstResult(offset);
            
            List<Notification> notifications = query.getResultList();
            return notifications.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }
    
    /**
     * Get unread notifications count
     */
    public long getUnreadCount(String userId) {
        EntityManager em = emf.createEntityManager();
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
     * Mark notification as read
     */
    public void markAsRead(Long notificationId, String userId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            
            Notification notification = em.find(Notification.class, notificationId);
            if (notification != null && notification.getUserId().equals(userId)) {
                notification.setIsRead(true);
                em.merge(notification);
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Failed to mark notification as read", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Mark all notifications as read for a user
     */
    public void markAllAsRead(String userId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            
            em.createQuery("UPDATE Notification n SET n.isRead = true WHERE n.userId = :userId AND n.isRead = false")
                .setParameter("userId", userId)
                .executeUpdate();
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Failed to mark all notifications as read", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Delete a notification
     */
    public void deleteNotification(Long notificationId, String userId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            
            Notification notification = em.find(Notification.class, notificationId);
            if (notification != null && notification.getUserId().equals(userId)) {
                em.remove(notification);
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Failed to delete notification", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Convert entity to DTO
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
