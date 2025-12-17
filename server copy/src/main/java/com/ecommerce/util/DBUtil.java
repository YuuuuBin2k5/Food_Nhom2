package com.ecommerce.util;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DBUtil {

    private static final EntityManagerFactory emf;

    static {
        try {
            // "FoodSharePU" phải khớp với tên trong persistence.xml
            emf = Persistence.createEntityManagerFactory("FoodRescuePU");
        } catch (Throwable ex) {
            System.err.println("Lỗi khởi tạo EntityManagerFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static EntityManagerFactory getEmFactory() {
        return emf;
    }

    public static void closeFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}