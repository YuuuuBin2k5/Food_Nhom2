package com.ecommerce.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.ecommerce.entity.*;
import com.ecommerce.util.DBUtil;
import com.ecommerce.util.MailUtil;
import com.ecommerce.util.PasswordUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

/**
 * Service xử lý authentication & authorization
 * - Login
 * - Register
 * - Forgot/Reset Password
 */
public class AuthService {

    private final NotificationService notificationService = new NotificationService();

    /* =========================
       LOGIN
       ========================= */
    public User login(String email, String password) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            User user = findUserByEmail(em, email);

            if (user == null) {
                throw new Exception("Email không tồn tại");
            }

            if (!PasswordUtil.verify(password, user.getPassword())) {
                throw new Exception("Sai mật khẩu");
            }

            if (user.isBanned()) {
                throw new Exception("Tài khoản của bạn đã bị khóa");
            }

            return user;
        } finally {
            em.close();
        }
    }
    
    /* =========================
       REGISTER
       ========================= */
    public void register(String fullName, String email, String password, String phone, String role, String shopName) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();

        try {
            // Kiểm tra email đã tồn tại
            if (findUserByEmail(em, email) != null) {
                throw new Exception("Email này đã được sử dụng!");
            }

            User newUser = null;
            String userRoleStr = (role != null) ? role.toUpperCase() : "BUYER";
            Role enumRole;
            
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
            newUser.setPassword(PasswordUtil.hash(password));
            newUser.setPhoneNumber(phone);
            newUser.setCreatedDate(new Date());
            newUser.setRole(enumRole);

            trans.begin();
            em.persist(newUser);
            em.flush();
            
            // Gửi notification cho admin nếu là seller mới
            if (enumRole == Role.SELLER) {
                TypedQuery<Admin> adminQuery = em.createQuery("SELECT a FROM Admin a", Admin.class);
                List<Admin> admins = adminQuery.getResultList();
                
                for (Admin admin : admins) {
                    try {
                        notificationService.createNotification(
                            em,
                            admin.getUserId(),
                            NotificationType.NEW_SELLER_REGISTRATION,
                            "Seller mới đăng ký",
                            "Seller '" + shopName + "' vừa đăng ký và chờ duyệt.",
                            null
                        );
                    } catch (Exception e) {
                        System.err.println("Lỗi gửi notification: " + e.getMessage());
                    }
                }
            }
            
            trans.commit();

        } catch (Exception e) {
            if (trans.isActive()) trans.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /* =========================
       FORGOT PASSWORD
       ========================= */
    public void forgotPassword(String email) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();

        try {
            User user = findUserByEmail(em, email);

            // Không leak thông tin email có tồn tại hay không
            if (user == null) return;

            em.getTransaction().begin();

            String tokenValue = UUID.randomUUID().toString();
            Date expiredAt = new Date(System.currentTimeMillis() + 15 * 60 * 1000);

            String userId = user.getUserId();
            String userType = getUserType(user);

            PasswordResetToken token = new PasswordResetToken(tokenValue, userId, userType, expiredAt);
            em.persist(token);
            em.getTransaction().commit();

            // Gửi mail
            String resetLink = "http://localhost:5173/reset-password?token=" + tokenValue;
            MailUtil.send(
                user.getEmail(),
                "Reset your password",
                "Click the link to reset your password:\n\n" + resetLink
            );

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /* =========================
       RESET PASSWORD
       ========================= */
    public boolean resetPassword(String tokenValue, String newPassword) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();

        try {
            PasswordResetToken token = em.createQuery(
                    "SELECT t FROM PasswordResetToken t WHERE t.token = :token",
                    PasswordResetToken.class)
                    .setParameter("token", tokenValue)
                    .getSingleResult();

            if (token.isExpired() || token.isUsed()) {
                return false;
            }

            em.getTransaction().begin();

            User user = findUserByIdAndType(em, token.getUserId(), token.getUserType());
            if (user == null) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                return false;
            }

            user.setPassword(PasswordUtil.hash(newPassword));
            token.markUsed();

            em.merge(user);
            em.merge(token);

            em.getTransaction().commit();
            return true;

        } catch (NoResultException e) {
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return false;
        } finally {
            em.close();
        }
    }

    /* =========================
       HELPER METHODS
       ========================= */
    private User findUserByEmail(EntityManager em, String email) {
        try {
            return em.createQuery("SELECT s FROM Seller s WHERE s.email = :email", Seller.class)
                    .setParameter("email", email).getSingleResult();
        } catch (NoResultException ignored) {}

        try {
            return em.createQuery("SELECT b FROM Buyer b WHERE b.email = :email", Buyer.class)
                    .setParameter("email", email).getSingleResult();
        } catch (NoResultException ignored) {}

        try {
            return em.createQuery("SELECT sh FROM Shipper sh WHERE sh.email = :email", Shipper.class)
                    .setParameter("email", email).getSingleResult();
        } catch (NoResultException ignored) {}

        try {
            return em.createQuery("SELECT a FROM Admin a WHERE a.email = :email", Admin.class)
                    .setParameter("email", email).getSingleResult();
        } catch (NoResultException ignored) {}

        return null;
    }
    
    private User findUserByIdAndType(EntityManager em, String userId, String userType) {
        switch (userType) {
            case "SELLER": return em.find(Seller.class, userId);
            case "BUYER": return em.find(Buyer.class, userId);
            case "SHIPPER": return em.find(Shipper.class, userId);
            case "ADMIN": return em.find(Admin.class, userId);
            default: return null;
        }
    }
    
    private String getUserType(User user) {
        if (user instanceof Admin) return "ADMIN";
        if (user instanceof Seller) return "SELLER";
        if (user instanceof Buyer) return "BUYER";
        if (user instanceof Shipper) return "SHIPPER";
        return "UNKNOWN";
    }
}
