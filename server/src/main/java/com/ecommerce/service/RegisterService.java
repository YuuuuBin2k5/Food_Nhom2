package com.ecommerce.service;

import com.ecommerce.entity.*;
import com.ecommerce.util.DBUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.Date;

public class RegisterService {

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
            newUser.setPassword(password);
            newUser.setPhoneNumber(phone);
            newUser.setCreatedDate(new Date());
            newUser.setRole(enumRole);

            trans.begin();
            em.persist(newUser);
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