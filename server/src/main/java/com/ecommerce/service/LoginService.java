package com.ecommerce.service;

import com.ecommerce.entity.Admin;
import com.ecommerce.entity.Buyer;
import com.ecommerce.entity.Seller;
import com.ecommerce.entity.Shipper;
import com.ecommerce.entity.User;
import com.ecommerce.util.DBUtil;
import com.ecommerce.util.PasswordUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

public class LoginService {

    public User login(String email, String password) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();

        try {
            User user = null;
            
            // Tối ưu: Dùng native query với UNION để query 1 lần
            String sql = 
                "SELECT user_id, full_name, email, password, phone_number, address, role, is_banned, created_date, 'ADMIN' as user_type " +
                "FROM admins WHERE email = ? " +
                "UNION ALL " +
                "SELECT user_id, full_name, email, password, phone_number, address, role, is_banned, created_date, 'SELLER' as user_type " +
                "FROM sellers WHERE email = ? " +
                "UNION ALL " +
                "SELECT user_id, full_name, email, password, phone_number, address, role, is_banned, created_date, 'BUYER' as user_type " +
                "FROM buyers WHERE email = ? " +
                "UNION ALL " +
                "SELECT user_id, full_name, email, password, phone_number, address, role, is_banned, created_date, 'SHIPPER' as user_type " +
                "FROM shippers WHERE email = ? " +
                "LIMIT 1";
            
            Object[] result = (Object[]) em.createNativeQuery(sql)
                .setParameter(1, email)
                .setParameter(2, email)
                .setParameter(3, email)
                .setParameter(4, email)
                .getSingleResult();
            
            String userType = (String) result[9];
            String userId = (String) result[0];
            
            // Load entity đúng type
            switch (userType) {
                case "ADMIN":
                    user = em.find(Admin.class, userId);
                    break;
                case "SELLER":
                    user = em.find(Seller.class, userId);
                    break;
                case "BUYER":
                    user = em.find(Buyer.class, userId);
                    break;
                case "SHIPPER":
                    user = em.find(Shipper.class, userId);
                    break;
            }
            
            if (user == null) {
                throw new Exception("Email không tồn tại");
            }
            
            // Kiểm tra mật khẩu đã hash
            if (!PasswordUtil.verify(password, user.getPassword())) {
                throw new Exception("Sai mật khẩu");
            }
            
            // Kiểm tra tài khoản có bị banned ko
            if (user.isBanned()) {
                throw new Exception("Tài khoản của bạn đã bị khóa");
            }

            return user;

        } catch (jakarta.persistence.NoResultException e) {
            throw new Exception("Email không tồn tại");
        } finally {
            em.close();
        }
    }
}
