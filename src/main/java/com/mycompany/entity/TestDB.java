package com.mycompany.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class TestDB {
    public static void main(String[] args) {
        System.out.println("--- BẮT ĐẦU TẠO DỮ LIỆU TEST ---");
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("adminfoodPU");
            EntityManager em = emf.createEntityManager();
            
            em.getTransaction().begin();
            
            // ========== TẠO SELLER ==========
            java.util.Calendar cal = java.util.Calendar.getInstance();
            
            // Seller 1 - PENDING (nộp sớm nhất - 5 ngày trước)
            Seller s1 = new Seller("Nguyễn Thị Hương", "huong@gmail.com", "123456", 
                    "0909123456", "123 Lê Lợi, Q1, TP.HCM", "Bếp Của Mẹ");
            s1.setBusinessLicenseUrl("img/giayphepkinhdoanh.jpg");
            cal.add(java.util.Calendar.DATE, -5);
            s1.setLicenseSubmittedDate(cal.getTime());
            
            // Seller 2 - PENDING (4 ngày trước)
            Seller s2 = new Seller("Trần Văn Nam", "nam@gmail.com", "123456", 
                    "0987654321", "456 Cầu Giấy, Hà Nội", "Organic Food");
            s2.setBusinessLicenseUrl("img/giayphepkinhdoanh.jpg");
            cal.add(java.util.Calendar.DATE, 1);
            s2.setLicenseSubmittedDate(cal.getTime());
            
            // Seller 3 - PENDING (3 ngày trước)
            Seller s3 = new Seller("Lê Thị Mai", "mai@gmail.com", "123456", 
                    "0912345678", "789 Nguyễn Huệ, Đà Nẵng", "Bánh Mì Sài Gòn");
            s3.setBusinessLicenseUrl("img/giayphepkinhdoanh.jpg");
            cal.add(java.util.Calendar.DATE, 1);
            s3.setLicenseSubmittedDate(cal.getTime());
            
            // Seller 4 - APPROVED (đã duyệt để đăng sản phẩm)
            Seller s4 = new Seller("Phạm Minh Tuấn", "tuan@gmail.com", "123456", 
                    "0923456789", "321 Hai Bà Trưng, Q3, TP.HCM", "Cơm Nhà Làm");
            s4.setBusinessLicenseUrl("img/giayphepkinhdoanh.jpg");
            s4.setVerificationStatus(SellerStatus.APPROVED);
            cal.add(java.util.Calendar.DATE, -10);
            s4.setLicenseSubmittedDate(cal.getTime());
            s4.setLicenseApprovedDate(new java.util.Date());
            
            // Seller 5 - APPROVED (đã duyệt)
            Seller s5 = new Seller("Hoàng Thị Lan", "lan@gmail.com", "123456", 
                    "0934567890", "654 Trần Phú, Nha Trang", "Hải Sản Tươi Sống");
            s5.setBusinessLicenseUrl("img/giayphepkinhdoanh.jpg");
            s5.setVerificationStatus(SellerStatus.APPROVED);
            s5.setLicenseSubmittedDate(cal.getTime());
            s5.setLicenseApprovedDate(new java.util.Date());
            
            // Seller 6 - REJECTED
            Seller s6 = new Seller("Đinh Văn Phúc", "phuc@gmail.com", "123456", 
                    "0945678901", "111 Pasteur, Q1, TP.HCM", "Quán Ăn Gia Đình");
            s6.setBusinessLicenseUrl("img/giayphepkinhdoanh.jpg");
            s6.setVerificationStatus(SellerStatus.REJECTED);
            s6.setLicenseSubmittedDate(cal.getTime());
            
            // Seller 7 - BANNED
            Seller s7 = new Seller("Bùi Thị Giang", "giang@gmail.com", "123456", 
                    "0956789012", "222 Nguyễn Đình Chiểu, Q3, TP.HCM", "Đồ Ăn Vặt");
            s7.setBusinessLicenseUrl("img/giayphepkinhdoanh.jpg");
            s7.setVerificationStatus(SellerStatus.APPROVED);
            s7.setBanned(true);
            
            em.persist(s1);
            em.persist(s2);
            em.persist(s3);
            em.persist(s4);
            em.persist(s5);
            em.persist(s6);
            em.persist(s7);
            
            System.out.println("--- ĐÃ TẠO 7 SELLER ---");
            
            // ========== TẠO PRODUCT ==========
            cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.DATE, -30);
            java.util.Date manufactureDate = cal.getTime();
            cal.add(java.util.Calendar.DATE, 60);
            java.util.Date expirationDate = cal.getTime();
            
            // Products PENDING_APPROVAL (chờ duyệt)
            cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.HOUR, -10);
            
            Product p1 = new Product("Bánh mì thịt nguội", 
                "Bánh mì tươi với thịt nguội, rau sống, nước sốt đặc biệt", 
                35000, 25000, expirationDate, manufactureDate, 50, 
                "img/product.jpg", s4);
            p1.setCreatedDate(cal.getTime());
            
            cal.add(java.util.Calendar.HOUR, 1);
            Product p2 = new Product("Cơm tấm sườn bì chả", 
                "Cơm tấm đặc biệt với sườn nướng, bì, chả trứng", 
                50000, 35000, expirationDate, manufactureDate, 30, 
                "img/product.jpg", s4);
            p2.setCreatedDate(cal.getTime());
            
            cal.add(java.util.Calendar.HOUR, 1);
            Product p3 = new Product("Phở bò tái nạm", 
                "Phở bò truyền thống với nước dùng hầm xương 12 tiếng", 
                60000, 45000, expirationDate, manufactureDate, 25, 
                "img/product.jpg", s5);
            p3.setCreatedDate(cal.getTime());
            
            cal.add(java.util.Calendar.HOUR, 1);
            Product p4 = new Product("Bún chả Hà Nội", 
                "Bún chả với thịt nướng than hoa, nước mắm pha chua ngọt", 
                55000, 40000, expirationDate, manufactureDate, 20, 
                "img/product.jpg", s5);
            p4.setCreatedDate(cal.getTime());
            
            cal.add(java.util.Calendar.HOUR, 1);
            Product p5 = new Product("Gỏi cuốn tôm thịt", 
                "Gỏi cuốn tươi với tôm, thịt heo, bún, rau sống", 
                40000, 30000, expirationDate, manufactureDate, 40, 
                "img/product.jpg", s4);
            p5.setCreatedDate(cal.getTime());
            
            // Products ACTIVE (đã duyệt)
            Product p6 = new Product("Bún bò Huế", 
                "Bún bò Huế cay nồng với giò heo, chả cua", 
                65000, 50000, expirationDate, manufactureDate, 35, 
                "img/product.jpg", s4);
            p6.setStatus(ProductStatus.ACTIVE);
            p6.setApprovedDate(new java.util.Date());
            
            Product p7 = new Product("Mì Quảng", 
                "Mì Quảng đặc sản miền Trung với tôm, thịt, trứng", 
                55000, 42000, expirationDate, manufactureDate, 28, 
                "img/product.jpg", s5);
            p7.setStatus(ProductStatus.ACTIVE);
            p7.setApprovedDate(new java.util.Date());
            
            Product p8 = new Product("Bánh cuốn nóng", 
                "Bánh cuốn nóng hổi với nhân thịt, mộc nhĩ", 
                45000, 35000, expirationDate, manufactureDate, 45, 
                "img/product.jpg", s4);
            p8.setStatus(ProductStatus.ACTIVE);
            p8.setApprovedDate(new java.util.Date());
            
            // Products REJECTED (bị từ chối)
            Product p9 = new Product("Sản phẩm không rõ nguồn gốc", 
                "Mô tả không đầy đủ", 
                100000, 50000, expirationDate, manufactureDate, 10, 
                "img/product.jpg", s4);
            p9.setStatus(ProductStatus.REJECTED);
            
            Product p10 = new Product("Đồ ăn hết hạn", 
                "Sản phẩm đã quá hạn sử dụng", 
                30000, 15000, manufactureDate, manufactureDate, 5, 
                "img/product.jpg", s5);
            p10.setStatus(ProductStatus.REJECTED);
            
            em.persist(p1);
            em.persist(p2);
            em.persist(p3);
            em.persist(p4);
            em.persist(p5);
            em.persist(p6);
            em.persist(p7);
            em.persist(p8);
            em.persist(p9);
            em.persist(p10);
            
            System.out.println("--- ĐÃ TẠO 10 PRODUCT ---");
            
            // ========== TẠO BUYER ==========
            Buyer b1 = new Buyer("Võ Văn An", "an@gmail.com", "123456", 
                    "0901111111", "100 Nguyễn Trãi, Q5, TP.HCM");
            Buyer b2 = new Buyer("Đặng Thị Bình", "binh@gmail.com", "123456", 
                    "0902222222", "200 Lý Thường Kiệt, Q10, TP.HCM");
            Buyer b3 = new Buyer("Ngô Quốc Cường", "cuong@gmail.com", "123456", 
                    "0903333333", "300 Điện Biên Phủ, Q3, TP.HCM");
            Buyer b4 = new Buyer("Lý Minh Đức", "duc@gmail.com", "123456", 
                    "0904444444", "400 Võ Văn Tần, Q3, TP.HCM");
            b4.setBanned(true);
            Buyer b5 = new Buyer("Trương Thị Em", "em@gmail.com", "123456", 
                    "0905555555", "500 Cách Mạng Tháng 8, Q10, TP.HCM");
            
            em.persist(b1);
            em.persist(b2);
            em.persist(b3);
            em.persist(b4);
            em.persist(b5);
            
            System.out.println("--- ĐÃ TẠO 5 BUYER ---");
            
            // ========== TẠO SHIPPER ==========
            Shipper sh1 = new Shipper("Lê Văn Tài", "tai@gmail.com", "123456", 
                    "0911111111", "100 Nguyễn Văn Cừ, Q5, TP.HCM");
            Shipper sh2 = new Shipper("Trần Minh Khoa", "khoa@gmail.com", "123456", 
                    "0922222222", "200 Lý Tự Trọng, Q1, TP.HCM");
            Shipper sh3 = new Shipper("Nguyễn Thị Linh", "linh@gmail.com", "123456", 
                    "0933333333", "300 Trần Hưng Đạo, Q5, TP.HCM");
            Shipper sh4 = new Shipper("Phạm Văn Mạnh", "manh@gmail.com", "123456", 
                    "0944444444", "400 Nguyễn Thị Minh Khai, Q3, TP.HCM");
            sh4.setBanned(true);
            
            em.persist(sh1);
            em.persist(sh2);
            em.persist(sh3);
            em.persist(sh4);
            
            System.out.println("--- ĐÃ TẠO 4 SHIPPER ---");
            
            em.getTransaction().commit();
            
            System.out.println("\n========== HOÀN TẤT ==========");
            System.out.println("- 7 Sellers (3 PENDING, 2 APPROVED, 1 REJECTED, 1 BANNED)");
            System.out.println("- 10 Products (5 PENDING, 3 ACTIVE, 2 REJECTED)");
            System.out.println("- 5 Buyers (1 BANNED)");
            System.out.println("- 4 Shippers (1 BANNED)");
            
            em.close();
            emf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
