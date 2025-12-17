package com.mycompany.model;

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
            
            // Tạo 5 Seller với status PENDING để test duyệt
            // Mỗi seller nộp giấy phép cách nhau 1 ngày để test thứ tự FIFO
            java.util.Calendar cal = java.util.Calendar.getInstance();
            
            // Seller 1 - nộp sớm nhất (5 ngày trước)
            Seller s1 = new Seller("Nguyễn Thị Hương", "huong@gmail.com", "123456", 
                    "0909123456", "123 Lê Lợi, Q1, TP.HCM", "Bếp Của Mẹ");
            s1.setBusinessLicenseUrl("img/giayphepkinhdoanh.jpg");
            cal.add(java.util.Calendar.DATE, -5);
            s1.setLicenseSubmittedDate(cal.getTime());
            
            // Seller 2 - nộp 4 ngày trước
            Seller s2 = new Seller("Trần Văn Nam", "nam@gmail.com", "123456", 
                    "0987654321", "456 Cầu Giấy, Hà Nội", "Organic Food");
            s2.setBusinessLicenseUrl("img/giayphepkinhdoanh.jpg");
            cal.add(java.util.Calendar.DATE, 1);
            s2.setLicenseSubmittedDate(cal.getTime());
            
            // Seller 3 - nộp 3 ngày trước
            Seller s3 = new Seller("Lê Thị Mai", "mai@gmail.com", "123456", 
                    "0912345678", "789 Nguyễn Huệ, Đà Nẵng", "Bánh Mì Sài Gòn");
            s3.setBusinessLicenseUrl("img/giayphepkinhdoanh.jpg");
            cal.add(java.util.Calendar.DATE, 1);
            s3.setLicenseSubmittedDate(cal.getTime());
            
            // Seller 4 - nộp 2 ngày trước
            Seller s4 = new Seller("Phạm Minh Tuấn", "tuan@gmail.com", "123456", 
                    "0923456789", "321 Hai Bà Trưng, Q3, TP.HCM", "Cơm Nhà Làm");
            s4.setBusinessLicenseUrl("img/giayphepkinhdoanh.jpg");
            cal.add(java.util.Calendar.DATE, 1);
            s4.setLicenseSubmittedDate(cal.getTime());
            
            // Seller 5 - nộp 1 ngày trước
            Seller s5 = new Seller("Hoàng Thị Lan", "lan@gmail.com", "123456", 
                    "0934567890", "654 Trần Phú, Nha Trang", "Hải Sản Tươi Sống");
            s5.setBusinessLicenseUrl("img/giayphepkinhdoanh.jpg");
            cal.add(java.util.Calendar.DATE, 1);
            s5.setLicenseSubmittedDate(cal.getTime());
            
            em.persist(s1);
            em.persist(s2);
            em.persist(s3);
            em.persist(s4);
            em.persist(s5);
            
            System.out.println("--- ĐÃ TẠO 5 SELLER THÀNH CÔNG ---");
            
            // ========== TẠO PRODUCT TEST ==========
            // Cần seller đã được duyệt để tạo product
            // Set seller 1 thành APPROVED để có thể đăng sản phẩm
            s1.setVerificationStatus(SellerStatus.APPROVED);
            
            cal = java.util.Calendar.getInstance();
            
            // Ngày sản xuất và hạn sử dụng
            java.util.Date today = new java.util.Date();
            cal.add(java.util.Calendar.DATE, -30); // 30 ngày trước
            java.util.Date manufactureDate = cal.getTime();
            cal.add(java.util.Calendar.DATE, 60); // 30 ngày sau (từ hôm nay)
            java.util.Date expirationDate = cal.getTime();
            
            // Product 1
            Product p1 = new Product("Bánh mì thịt nguội", 
                "Bánh mì tươi với thịt nguội, rau sống, nước sốt đặc biệt", 
                35000, 25000, expirationDate, manufactureDate, 50, 
                "img/giayphepkinhdoanh.jpg", s1);
            cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.HOUR, -5);
            p1.setCreatedDate(cal.getTime());
            
            // Product 2
            Product p2 = new Product("Cơm tấm sườn bì chả", 
                "Cơm tấm đặc biệt với sườn nướng, bì, chả trứng", 
                50000, 35000, expirationDate, manufactureDate, 30, 
                "img/giayphepkinhdoanh.jpg", s1);
            cal.add(java.util.Calendar.HOUR, 1);
            p2.setCreatedDate(cal.getTime());
            
            // Product 3
            Product p3 = new Product("Phở bò tái nạm", 
                "Phở bò truyền thống với nước dùng hầm xương 12 tiếng", 
                60000, 45000, expirationDate, manufactureDate, 25, 
                "img/giayphepkinhdoanh.jpg", s1);
            cal.add(java.util.Calendar.HOUR, 1);
            p3.setCreatedDate(cal.getTime());
            
            // Product 4
            Product p4 = new Product("Bún chả Hà Nội", 
                "Bún chả với thịt nướng than hoa, nước mắm pha chua ngọt", 
                55000, 40000, expirationDate, manufactureDate, 20, 
                "img/giayphepkinhdoanh.jpg", s1);
            cal.add(java.util.Calendar.HOUR, 1);
            p4.setCreatedDate(cal.getTime());
            
            // Product 5
            Product p5 = new Product("Gỏi cuốn tôm thịt", 
                "Gỏi cuốn tươi với tôm, thịt heo, bún, rau sống", 
                40000, 30000, expirationDate, manufactureDate, 40, 
                "img/giayphepkinhdoanh.jpg", s1);
            cal.add(java.util.Calendar.HOUR, 1);
            p5.setCreatedDate(cal.getTime());
            
            em.persist(p1);
            em.persist(p2);
            em.persist(p3);
            em.persist(p4);
            em.persist(p5);
            
            System.out.println("--- ĐÃ TẠO 5 PRODUCT THÀNH CÔNG ---");
            
            // ========== TẠO BUYER TEST ==========
            Buyer b1 = new Buyer("Võ Văn An", "an@gmail.com", "123456", 
                    "0901111111", "100 Nguyễn Trãi, Q5, TP.HCM");
            
            Buyer b2 = new Buyer("Đặng Thị Bình", "binh@gmail.com", "123456", 
                    "0902222222", "200 Lý Thường Kiệt, Q10, TP.HCM");
            
            Buyer b3 = new Buyer("Ngô Quốc Cường", "cuong@gmail.com", "123456", 
                    "0903333333", "300 Điện Biên Phủ, Q3, TP.HCM");
            
            // Buyer 4 - đã bị ban để test
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
            
            // ========== THÊM SELLER ĐÃ DUYỆT VÀ BỊ BAN ĐỂ TEST ==========
            // Seller 6 - đã được duyệt
            Seller s6 = new Seller("Đinh Văn Phúc", "phuc@gmail.com", "123456", 
                    "0945678901", "111 Pasteur, Q1, TP.HCM", "Quán Ăn Gia Đình");
            s6.setBusinessLicenseUrl("img/giayphepkinhdoanh.jpg");
            s6.setVerificationStatus(SellerStatus.APPROVED);
            
            // Seller 7 - đã bị ban để test
            Seller s7 = new Seller("Bùi Thị Giang", "giang@gmail.com", "123456", 
                    "0956789012", "222 Nguyễn Đình Chiểu, Q3, TP.HCM", "Đồ Ăn Vặt");
            s7.setBusinessLicenseUrl("img/giayphepkinhdoanh.jpg");
            s7.setVerificationStatus(SellerStatus.APPROVED);
            s7.setBanned(true);
            
            // Seller 8 - bị từ chối
            Seller s8 = new Seller("Cao Minh Hiếu", "hieu@gmail.com", "123456", 
                    "0967890123", "333 Lê Văn Sỹ, Q3, TP.HCM", "Trà Sữa ABC");
            s8.setBusinessLicenseUrl("img/giayphepkinhdoanh.jpg");
            s8.setVerificationStatus(SellerStatus.REJECTED);
            
            em.persist(s6);
            em.persist(s7);
            em.persist(s8);
            
            // ========== THÊM BUYER ==========
            Buyer b6 = new Buyer("Phan Văn Phong", "phong@gmail.com", "123456", 
                    "0906666666", "600 Trường Chinh, Tân Bình, TP.HCM");
            
            Buyer b7 = new Buyer("Huỳnh Thị Quỳnh", "quynh@gmail.com", "123456", 
                    "0907777777", "700 Cộng Hòa, Tân Bình, TP.HCM");
            
            em.persist(b6);
            em.persist(b7);
            
            // ========== TẠO SHIPPER TEST ==========
            Shipper sh1 = new Shipper("Lê Văn Tài", "tai@gmail.com", "123456", 
                    "0911111111", "100 Nguyễn Văn Cừ, Q5, TP.HCM");
            
            Shipper sh2 = new Shipper("Trần Minh Khoa", "khoa@gmail.com", "123456", 
                    "0922222222", "200 Lý Tự Trọng, Q1, TP.HCM");
            
            Shipper sh3 = new Shipper("Nguyễn Thị Linh", "linh@gmail.com", "123456", 
                    "0933333333", "300 Trần Hưng Đạo, Q5, TP.HCM");
            
            // Shipper 4 - đã bị ban để test
            Shipper sh4 = new Shipper("Phạm Văn Mạnh", "manh@gmail.com", "123456", 
                    "0944444444", "400 Nguyễn Thị Minh Khai, Q3, TP.HCM");
            sh4.setBanned(true);
            
            em.persist(sh1);
            em.persist(sh2);
            em.persist(sh3);
            em.persist(sh4);
            
            em.getTransaction().commit();
            
            System.out.println("--- ĐÃ TẠO DỮ LIỆU TEST THÀNH CÔNG ---");
            System.out.println("- 8 Sellers (5 PENDING, 2 APPROVED, 1 REJECTED, 1 BANNED)");
            System.out.println("- 7 Buyers (1 BANNED)");
            System.out.println("- 4 Shippers (1 BANNED)");
            System.out.println("- 5 Products (PENDING_APPROVAL)");
            
            em.close();
            emf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}