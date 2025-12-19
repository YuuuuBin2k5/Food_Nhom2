package com.ecommerce.service;

import com.ecommerce.entity.*;
import com.ecommerce.util.DBUtil;
import com.ecommerce.util.PasswordUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

public class RegisterService {
    
    private final NotificationService notificationService = new NotificationService();

    public void registerUser(String fullName, String email, String password, String phone, String role, String shopName) throws Exception {
        
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();

        try {
            // Kiểm tra tài khoản có tồn tại hay không (biến count để so sánh)
            long count = 0;

            count += em.createQuery("SELECT COUNT(s) FROM Seller s WHERE s.email = :email", Long.class)
                       .setParameter("email", email)
                       .getSingleResult();

            count += em.createQuery("SELECT COUNT(b) FROM Buyer b WHERE b.email = :email", Long.class)
                       .setParameter("email", email)
                       .getSingleResult();

            count += em.createQuery("SELECT COUNT(sh) FROM Shipper sh WHERE sh.email = :email", Long.class)
                        .setParameter("email", email)
                        .getSingleResult();

            if (count > 0) {
                throw new Exception("Email này đã được sử dụng!");
            }

            User newUser = null;
            
            //Nếu role == null thì là gán BUYER
            String userRoleStr = (role != null) ? role.toUpperCase() : "BUYER";
            Role enumRole;
            
            // Duyệt xem vai trò j
            switch (userRoleStr) {
                case "SELLER":
                    if (shopName == null || shopName.trim().isEmpty()) {
                        throw new Exception("Người bán phải có tên cửa hàng!");
                    }
                    Seller seller = new Seller();
                    seller.setShopName(shopName);
                    seller.setRevenue(0.0);
                    newUser = seller;
                    enumRole = Role.SELLER;
                    break;
                    
                case "SHIPPER":
                    newUser = new Shipper();
                    enumRole = Role.SHIPPER;
                    break;
                    
                case "BUYER":
                default:
                    newUser = new Buyer();
                    enumRole = Role.BUYER;
                    break;
            }

            newUser.setFullName(fullName);
            newUser.setEmail(email);
            // ✅ Hash password before saving
            String hashedPassword = PasswordUtil.hash(password);
            newUser.setPassword(hashedPassword);
            newUser.setPhoneNumber(phone);
            newUser.setCreatedDate(new Date());
            newUser.setRole(enumRole);

            trans.begin();
            em.persist(newUser);
            
            // Flush để lấy userId
            em.flush();
            
            // Nếu là seller mới đăng ký, gửi notification cho tất cả admin
            if (enumRole == Role.SELLER) {
                System.out.println("=== [RegisterService] Seller registered, creating notifications for admins");
                
                // Lấy danh sách admin
                TypedQuery<Admin> adminQuery = em.createQuery("SELECT a FROM Admin a", Admin.class);
                List<Admin> admins = adminQuery.getResultList();
                
                System.out.println("=== [RegisterService] Found " + admins.size() + " admins");
                
                for (Admin admin : admins) {
                    try {
                        System.out.println("=== [RegisterService] Creating notification for admin: " + admin.getEmail());
                        notificationService.createNotification(
                            em,
                            admin.getUserId(),
                            NotificationType.NEW_SELLER_REGISTRATION,
                            "Seller mới đăng ký",
                            "Seller '" + shopName + "' vừa đăng ký và chờ duyệt.",
                            null
                        );
                        System.out.println("=== [RegisterService] ✅ Notification created successfully");
                    } catch (Exception e) {
                        System.err.println("=== [RegisterService] ❌ Failed to create notification: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
            
            trans.commit();

        } catch (Exception e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}