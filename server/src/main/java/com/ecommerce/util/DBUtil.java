package com.ecommerce.util;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DBUtil {
    private static final EntityManagerFactory emf = 
            Persistence.createEntityManagerFactory("FoodSharePU");

    public static EntityManagerFactory getEmFactory() {
        return emf;
    }
    
    // Hàm đóng Factory khi tắt ứng dụng (ít dùng trong Servlet nhưng nên có)
    public static void closeFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}