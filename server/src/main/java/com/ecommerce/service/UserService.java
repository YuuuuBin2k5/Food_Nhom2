package com.ecommerce.service;

import java.util.ArrayList;
import java.util.List;

import com.ecommerce.entity.Admin;
import com.ecommerce.entity.Buyer;
import com.ecommerce.entity.Seller;
import com.ecommerce.entity.Shipper;
import com.ecommerce.entity.User;
import com.ecommerce.util.DBUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

public class UserService {
    
    /**
     * Get all users in the system (from all role tables)
     */
    public List<User> getAllUsers() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            List<User> allUsers = new ArrayList<>();
            
            // Get all buyers
            TypedQuery<Buyer> buyerQuery = em.createQuery("SELECT b FROM Buyer b", Buyer.class);
            allUsers.addAll(buyerQuery.getResultList());
            
            // Get all sellers
            TypedQuery<Seller> sellerQuery = em.createQuery("SELECT s FROM Seller s", Seller.class);
            allUsers.addAll(sellerQuery.getResultList());
            
            // Get all shippers
            TypedQuery<Shipper> shipperQuery = em.createQuery("SELECT s FROM Shipper s", Shipper.class);
            allUsers.addAll(shipperQuery.getResultList());
            
            // Get all admins
            TypedQuery<Admin> adminQuery = em.createQuery("SELECT a FROM Admin a", Admin.class);
            allUsers.addAll(adminQuery.getResultList());
            
            return allUsers;
        } finally {
            em.close();
        }
    }
    
    /**
     * Get user by ID (search in all role tables)
     */
    public User getUserById(String userId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            // Try Buyer first
            Buyer buyer = em.find(Buyer.class, userId);
            if (buyer != null) return buyer;
            
            // Try Seller
            Seller seller = em.find(Seller.class, userId);
            if (seller != null) return seller;
            
            // Try Shipper
            Shipper shipper = em.find(Shipper.class, userId);
            if (shipper != null) return shipper;
            
            // Try Admin
            Admin admin = em.find(Admin.class, userId);
            return admin;
        } finally {
            em.close();
        }
    }
    
    /**
     * Get users by role
     */
    public List<User> getUsersByRole(String role) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            List<User> users = new ArrayList<>();
            
            switch (role.toUpperCase()) {
                case "BUYER":
                    TypedQuery<Buyer> buyerQuery = em.createQuery("SELECT b FROM Buyer b", Buyer.class);
                    users.addAll(buyerQuery.getResultList());
                    break;
                case "SELLER":
                    TypedQuery<Seller> sellerQuery = em.createQuery("SELECT s FROM Seller s", Seller.class);
                    users.addAll(sellerQuery.getResultList());
                    break;
                case "SHIPPER":
                    TypedQuery<Shipper> shipperQuery = em.createQuery("SELECT s FROM Shipper s", Shipper.class);
                    users.addAll(shipperQuery.getResultList());
                    break;
                case "ADMIN":
                    TypedQuery<Admin> adminQuery = em.createQuery("SELECT a FROM Admin a", Admin.class);
                    users.addAll(adminQuery.getResultList());
                    break;
            }
            
            return users;
        } finally {
            em.close();
        }
    }
    
    /**
     * Update user (must know the role to update correct table)
     */
    public void updateUser(User user) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.merge(user);
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) trans.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    
    /**
     * Ban/Unban user
     */
    public void toggleBanUser(String userId, boolean banned) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            
            // Find user in appropriate table
            User user = getUserByIdInTransaction(em, userId);
            if (user == null) {
                throw new Exception("User not found: " + userId);
            }
            
            user.setBanned(banned);
            em.merge(user);
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) trans.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    
    /**
     * Helper method to find user within a transaction
     */
    private User getUserByIdInTransaction(EntityManager em, String userId) {
        Buyer buyer = em.find(Buyer.class, userId);
        if (buyer != null) return buyer;
        
        Seller seller = em.find(Seller.class, userId);
        if (seller != null) return seller;
        
        Shipper shipper = em.find(Shipper.class, userId);
        if (shipper != null) return shipper;
        
        Admin admin = em.find(Admin.class, userId);
        return admin;
    }
    
    /**
     * Delete user
     */
    public void deleteUser(String userId) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            User user = getUserByIdInTransaction(em, userId);
            if (user != null) {
                em.remove(user);
            }
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) trans.rollback();
            throw new Exception("Cannot delete user (may have related data)");
        } finally {
            em.close();
        }
    }
    
    /**
     * Search users by keyword (ID, name, email)
     */
    public List<User> searchUsers(String keyword) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            List<User> users = new ArrayList<>();
            String searchPattern = "%" + keyword.toLowerCase() + "%";
            
            // Search in Buyer
            TypedQuery<Buyer> buyerQuery = em.createQuery(
                "SELECT b FROM Buyer b WHERE " +
                "LOWER(b.userId) LIKE :keyword OR " +
                "LOWER(b.fullName) LIKE :keyword OR " +
                "LOWER(b.email) LIKE :keyword", Buyer.class);
            buyerQuery.setParameter("keyword", searchPattern);
            users.addAll(buyerQuery.getResultList());
            
            // Search in Seller
            TypedQuery<Seller> sellerQuery = em.createQuery(
                "SELECT s FROM Seller s WHERE " +
                "LOWER(s.userId) LIKE :keyword OR " +
                "LOWER(s.fullName) LIKE :keyword OR " +
                "LOWER(s.email) LIKE :keyword", Seller.class);
            sellerQuery.setParameter("keyword", searchPattern);
            users.addAll(sellerQuery.getResultList());
            
            // Search in Shipper
            TypedQuery<Shipper> shipperQuery = em.createQuery(
                "SELECT s FROM Shipper s WHERE " +
                "LOWER(s.userId) LIKE :keyword OR " +
                "LOWER(s.fullName) LIKE :keyword OR " +
                "LOWER(s.email) LIKE :keyword", Shipper.class);
            shipperQuery.setParameter("keyword", searchPattern);
            users.addAll(shipperQuery.getResultList());
            
            // Search in Admin
            TypedQuery<Admin> adminQuery = em.createQuery(
                "SELECT a FROM Admin a WHERE " +
                "LOWER(a.userId) LIKE :keyword OR " +
                "LOWER(a.fullName) LIKE :keyword OR " +
                "LOWER(a.email) LIKE :keyword", Admin.class);
            adminQuery.setParameter("keyword", searchPattern);
            users.addAll(adminQuery.getResultList());
            
            return users;
        } finally {
            em.close();
        }
    }
    
    /**
     * Count users by role
     */
    public long countUsersByRole(String role) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            TypedQuery<Long> query = null;
            
            switch (role.toUpperCase()) {
                case "BUYER":
                    query = em.createQuery("SELECT COUNT(b) FROM Buyer b", Long.class);
                    break;
                case "SELLER":
                    query = em.createQuery("SELECT COUNT(s) FROM Seller s", Long.class);
                    break;
                case "SHIPPER":
                    query = em.createQuery("SELECT COUNT(s) FROM Shipper s", Long.class);
                    break;
                case "ADMIN":
                    query = em.createQuery("SELECT COUNT(a) FROM Admin a", Long.class);
                    break;
                default:
                    return 0;
            }
            
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }
    
    /**
     * Count banned users (across all roles)
     */
    public long countBannedUsers() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            long count = 0;
            
            TypedQuery<Long> buyerQuery = em.createQuery(
                "SELECT COUNT(b) FROM Buyer b WHERE b.isBanned = true", Long.class);
            count += buyerQuery.getSingleResult();
            
            TypedQuery<Long> sellerQuery = em.createQuery(
                "SELECT COUNT(s) FROM Seller s WHERE s.isBanned = true", Long.class);
            count += sellerQuery.getSingleResult();
            
            TypedQuery<Long> shipperQuery = em.createQuery(
                "SELECT COUNT(s) FROM Shipper s WHERE s.isBanned = true", Long.class);
            count += shipperQuery.getSingleResult();
            
            TypedQuery<Long> adminQuery = em.createQuery(
                "SELECT COUNT(a) FROM Admin a WHERE a.isBanned = true", Long.class);
            count += adminQuery.getSingleResult();
            
            return count;
        } finally {
            em.close();
        }
    }
    
    /**
     * Get pending sellers (waiting for approval)
     */
    public List<Seller> getPendingSellers() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            TypedQuery<Seller> query = em.createQuery(
                "SELECT s FROM Seller s WHERE s.isVerified = false AND s.businessLicenseUrl IS NOT NULL ORDER BY s.licenseSubmittedDate ASC", 
                Seller.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
