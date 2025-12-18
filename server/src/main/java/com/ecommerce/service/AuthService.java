package com.ecommerce.service;

import java.util.Date;
import java.util.UUID;

import com.ecommerce.entity.Admin;
import com.ecommerce.entity.Buyer;
import com.ecommerce.entity.PasswordResetToken;
import com.ecommerce.entity.Seller;
import com.ecommerce.entity.Shipper;
import com.ecommerce.entity.User;
import com.ecommerce.util.DBUtil;
import com.ecommerce.util.MailUtil;
import com.ecommerce.util.PasswordUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

public class AuthService {

    /* =========================
       LOGIN
       ========================= */
    public User login(String email, String password) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            User user = findUserByEmail(em, email);

            if (user == null) {
                throw new Exception("EMAIL_NOT_FOUND");
            }

            // ✅ Use BCrypt to verify password
            if (!PasswordUtil.verify(password, user.getPassword())) {
                throw new Exception("INVALID_PASSWORD");
            }

            if (user.isBanned()) {
                throw new Exception("ACCOUNT_BANNED");
            }

            return user;
        } finally {
            em.close();
        }
    }

    /* =========================
       FORGOT PASSWORD
       ========================= */
    public void requestPasswordReset(String email) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();

        try {
            User user = findUserByEmail(em, email);

            // ⚠️ Không leak thông tin email tồn tại hay không
            if (user == null) return;

            em.getTransaction().begin();

                String tokenValue = UUID.randomUUID().toString();
                Date expiredAt = new Date(System.currentTimeMillis() + 15 * 60 * 1000);

                String userId = user.getUserId();
                String userType = "UNKNOWN";
                if (user instanceof Admin) userType = "ADMIN";
                else if (user instanceof Seller) userType = "SELLER";
                else if (user instanceof Buyer) userType = "BUYER";
                else if (user instanceof Shipper) userType = "SHIPPER";

                PasswordResetToken token = new PasswordResetToken(tokenValue, userId, userType, expiredAt);
                em.persist(token);
            em.getTransaction().commit();

            // 👉 Gửi mail
            String resetLink =
                    "http://localhost:5173/reset-password?token=" + tokenValue;

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
       RESET PASSWORD (BOOLEAN)
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

            // Lookup user by stored userId and userType

            String userId = token.getUserId();
            String userType = token.getUserType();

            User user = null;
            if ("SELLER".equals(userType)) {
                user = em.find(Seller.class, userId);
            } else if ("BUYER".equals(userType)) {
                user = em.find(Buyer.class, userId);
            } else if ("SHIPPER".equals(userType)) {
                user = em.find(Shipper.class, userId);
            } else if ("ADMIN".equals(userType)) {
                user = em.find(Admin.class, userId);
            }

            if (user == null) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                return false;
            }

            // ✅ Hash password before saving
            String hashedPassword = PasswordUtil.hash(newPassword);
            user.setPassword(hashedPassword);

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
       FIND USER BY EMAIL
       ========================= */
    private User findUserByEmail(EntityManager em, String email) {

        try {
            return em.createQuery(
                    "SELECT s FROM Seller s WHERE s.email = :email",
                    Seller.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException ignored) {}

        try {
            return em.createQuery(
                    "SELECT b FROM Buyer b WHERE b.email = :email",
                    Buyer.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException ignored) {}

        try {
            return em.createQuery(
                    "SELECT sh FROM Shipper sh WHERE sh.email = :email",
                    Shipper.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException ignored) {}

        try {
            return em.createQuery(
                    "SELECT a FROM Admin a WHERE a.email = :email",
                    Admin.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException ignored) {}

        return null;
    }
}
