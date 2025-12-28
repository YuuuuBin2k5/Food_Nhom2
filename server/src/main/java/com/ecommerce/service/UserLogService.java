package com.ecommerce.service;

import com.ecommerce.entity.ActionType;
import com.ecommerce.entity.Role;
import com.ecommerce.entity.UserLog;
import com.ecommerce.util.DBUtil;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

public class UserLogService {

    private EntityManager getEntityManager() {
        return DBUtil.getEmFactory().createEntityManager();
    }

    /**
     * Lưu log mới
     */
    public boolean save(UserLog log) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(log);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Lấy log theo userId (mới nhất trước)
     */
    public List<UserLog> getLogsByUserId(String userId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<UserLog> query = em.createQuery(
                "SELECT l FROM UserLog l WHERE l.userId = :userId ORDER BY l.createdAt DESC",
                UserLog.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy log theo userId với giới hạn số lượng
     */
    public List<UserLog> getLogsByUserId(String userId, int limit) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<UserLog> query = em.createQuery(
                "SELECT l FROM UserLog l WHERE l.userId = :userId ORDER BY l.createdAt DESC",
                UserLog.class);
            query.setParameter("userId", userId);
            query.setMaxResults(limit);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy tất cả log (mới nhất trước)
     */
    public List<UserLog> getAllLogs(int limit) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<UserLog> query = em.createQuery(
                "SELECT l FROM UserLog l ORDER BY l.createdAt DESC",
                UserLog.class);
            query.setMaxResults(limit);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy log theo loại action
     */
    public List<UserLog> getLogsByAction(ActionType action, int limit) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<UserLog> query = em.createQuery(
                "SELECT l FROM UserLog l WHERE l.action = :action ORDER BY l.createdAt DESC",
                UserLog.class);
            query.setParameter("action", action);
            query.setMaxResults(limit);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy log theo role của user
     */
    public List<UserLog> getLogsByRole(Role role, int limit) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<UserLog> query = em.createQuery(
                "SELECT l FROM UserLog l WHERE l.userRole = :role ORDER BY l.createdAt DESC",
                UserLog.class);
            query.setParameter("role", role);
            query.setMaxResults(limit);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy log theo adminId (các action do admin thực hiện)
     */
    public List<UserLog> getLogsByAdminId(String adminId, int limit) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<UserLog> query = em.createQuery(
                "SELECT l FROM UserLog l WHERE l.adminId = :adminId ORDER BY l.createdAt DESC",
                UserLog.class);
            query.setParameter("adminId", adminId);
            query.setMaxResults(limit);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy log theo targetId (đối tượng bị tác động)
     */
    public List<UserLog> getLogsByTargetId(String targetId, int limit) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<UserLog> query = em.createQuery(
                "SELECT l FROM UserLog l WHERE l.targetId = :targetId ORDER BY l.createdAt DESC",
                UserLog.class);
            query.setParameter("targetId", targetId);
            query.setMaxResults(limit);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }
}
