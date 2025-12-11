package com.ecommerce.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAUtil {
    
    private static final EntityManagerFactory emFactory;
    
    static {
        try {
            emFactory = Persistence.createEntityManagerFactory("EcommercePU");
        } catch (Throwable ex) {
            System.err.println("EntityManagerFactory creation failed: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    public static EntityManager getEntityManager() {
        return emFactory.createEntityManager();
    }
    
    public static void close() {
        if (emFactory != null && emFactory.isOpen()) {
            emFactory.close();
        }
    }
}
