package com.ecommerce.service;

import com.ecommerce.entity.Buyer;
import com.ecommerce.entity.Seller;
import com.ecommerce.entity.Shipper;
import com.ecommerce.entity.User;
import com.ecommerce.util.DBUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

public class LoginService {

    public User login(String email, String password) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();

        try {
            User user = null;  

            // Kiểm tra Seller
            try {
                user = em.createQuery("SELECT s FROM Seller s WHERE s.email = :email", Seller.class)
                         .setParameter("email", email)
                         .getSingleResult();
            } catch (NoResultException ignored) {}

            // Kiểm tra Buyer
            if (user == null) {
                try {
                    user = em.createQuery("SELECT b FROM Buyer b WHERE b.email = :email", Buyer.class)
                             .setParameter("email", email)
                             .getSingleResult();
                } catch (NoResultException ignored) {}
            }

            // Kiểm tra Shipper
            if (user == null) {
                try {
                    user = em.createQuery("SELECT sh FROM Shipper sh WHERE sh.email = :email", Shipper.class)
                             .setParameter("email", email)
                             .getSingleResult();
                } catch (NoResultException ignored) {}
            }

            if (user == null) {
                throw new Exception("Email không tồn tại");
            }
            
            // Kiểm tra mật khẩu tài khoảnn
            if (!user.getPassword().equals(password)) {
                throw new Exception("Sai mật khẩu");
            }
            
            // Kiểm tra tài khoản có bị banned ko
            if (user.isBanned()) {
                throw new Exception("Tài khoản của bạn đã bị khóa");
            }

            return user;

        } finally {
            em.close();
        }
    }
}
