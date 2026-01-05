package com.ecommerce.util;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DBUtil {
    private static EntityManagerFactory emf;
    private static Throwable initError;

    static {
        try {
            System.out.println("=== DBUtil: Đang khởi tạo kết nối database... ===");
            emf = Persistence.createEntityManagerFactory("FoodRescuePU");
            System.out.println("=== DBUtil: Kết nối database THÀNH CÔNG! ===");
        } catch (Throwable ex) {
            initError = ex;
            System.err.println("=== DBUtil: LỖI KẾT NỐI DATABASE ===");
            System.err.println("Loại lỗi: " + ex.getClass().getName());
            System.err.println("Message: " + ex.getMessage());
            
            // In ra nguyên nhân gốc (root cause)
            Throwable cause = ex;
            while (cause.getCause() != null) {
                cause = cause.getCause();
                System.err.println("Caused by: " + cause.getClass().getName() + " - " + cause.getMessage());
            }
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static EntityManagerFactory getEmFactory() {
        if (emf == null) {
            throw new IllegalStateException("EntityManagerFactory chưa được khởi tạo. Lỗi gốc: " + initError);
        }
        return emf;
    }

    public static void closeFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}