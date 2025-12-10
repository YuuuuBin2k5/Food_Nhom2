package com.ecommerce.dto; // Nên đặt trong package data

import com.ecommerce.entity.User;
import com.ecommerce.util.DBUtil;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

public class UserDB {

    // --- 1. INSERT (Đã thêm finally để đóng kết nối) ---
    public static void insert(User user) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        trans.begin();
        try {
            em.persist(user);
            trans.commit();
        } catch (Exception e) {
            System.out.println("Lỗi Insert: " + e);
            if (trans.isActive()) {
                trans.rollback();
            }
        } finally {
            em.close(); // BẮT BUỘC PHẢI CÓ
        }
    }

    // --- 2. UPDATE ---
    public static void update(User user) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        trans.begin();
        try {
            em.merge(user);
            trans.commit();
        } catch (Exception e) {
            System.out.println("Lỗi Update: " + e);
            trans.rollback();
        } finally {
            em.close();
        }
    }

    // --- 3. DELETE ---
    public static void delete(User user) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        trans.begin();
        try {
            // Phải merge trước khi remove để đưa object vào trạng thái managed
            em.remove(em.merge(user));
            trans.commit();
        } catch (Exception e) {
            System.out.println("Lỗi Delete: " + e);
            trans.rollback();
        } finally {
            em.close();
        }
    }

    // --- 4. SELECT BY EMAIL (Đã sửa lỗi query sai) ---
    public static User selectUser(String email) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        String qString = "SELECT u FROM User u WHERE u.email = :email"; // Sửa userId thành email
        TypedQuery<User> q = em.createQuery(qString, User.class);
        q.setParameter("email", email);
        try {
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    // --- 5. SELECT ALL ---
    public static List<User> selectUsers() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        String qString = "SELECT u FROM User u";
        TypedQuery<User> q = em.createQuery(qString, User.class);
        try {
            return q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    // --- 6. CHECK LOGIN (Quan trọng cho chức năng đăng nhập) ---
    public static User checkLogin(String email, String password) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        String qString = "SELECT u FROM User u WHERE u.email = :email AND u.password = :password";
        TypedQuery<User> q = em.createQuery(qString, User.class);
        q.setParameter("email", email);
        q.setParameter("password", password);
        try {
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    // --- 7. EMAIL EXISTS (Đã chuyển từ JDBC sang JPA) ---
    public static boolean emailExists(String email) {
        // Tận dụng lại hàm selectUser ở trên cho gọn
        User u = selectUser(email);
        return u != null;
    }
    
    // --- 8. GET BY ID ---
    public static User getUserById(long userId) { // Lưu ý kiểu dữ liệu ID (Long hay String tùy Entity của bạn)
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            return em.find(User.class, userId);
        } finally {
            em.close();
        }
    }
}