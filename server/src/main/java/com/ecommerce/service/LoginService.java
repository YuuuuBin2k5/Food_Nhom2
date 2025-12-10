package com.ecommerce.service;

import com.ecommerce.entity.User;
import com.ecommerce.util.DBUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

public class LoginService {

    public User login(String email, String password) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();

        try {
            User user;
            try {
                String qString = "SELECT u FROM User u WHERE u.email = :email";
                TypedQuery<User> q = em.createQuery(qString, User.class);
                q.setParameter("email", email);
                user = q.getSingleResult();
            } catch (NoResultException e) {
                throw new Exception("Email không tồn tại");
            }

            if (!user.getPassword().equals(password)) {
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
}