package com.ecommerce.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {

    private static final EntityManagerFactory emFactory;

    static {
        try {
            emFactory = Persistence.createEntityManagerFactory("FoodRescuePU");
        } catch (Throwable ex) {
            System.err.println("EntityManagerFactory creation failed: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static EntityManager getEntityManager() {
        return emFactory.createEntityManager(); // Trả về jakarta.persistence.EntityManager
    }

    public static void close() {
        if (emFactory != null && emFactory.isOpen()) {
            emFactory.close();
        }
    }
}
