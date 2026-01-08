package com.ecommerce.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class InsertDB {

    private static final Random random = new Random();

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("FoodRescuePU");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            // Insert Admins
            List<Admin> admins = createAdmins();
            for (Admin admin : admins) {
                em.persist(admin);
            }
            System.out.println("Inserted " + admins.size() + " admins");

            // Insert Sellers
            List<Seller> sellers = createSellers();
            for (Seller seller : sellers) {
                em.persist(seller);
            }
            System.out.println("Inserted " + sellers.size() + " sellers");

            // Insert Buyers
            List<Buyer> buyers = createBuyers();
            for (Buyer buyer : buyers) {
                em.persist(buyer);
            }
            System.out.println("Inserted " + buyers.size() + " buyers");

            // Insert Shippers
            List<Shipper> shippers = createShippers();
            for (Shipper shipper : shippers) {
                em.persist(shipper);
            }
            System.out.println("Inserted " + shippers.size() + " shippers");

            // Insert Products (chỉ cho sellers đã được approved)
            List<Seller> approvedSellers = sellers.stream()
                    .filter(s -> s.getVerificationStatus() == SellerStatus.APPROVED)
                    .toList();
            List<Product> products = createProducts(approvedSellers);
            for (Product product : products) {
                em.persist(product);
            }
            System.out.println("Inserted " + products.size() + " products");

            // Flush để có ID cho products
            em.flush();

            // Lọc products đã ACTIVE
            List<Product> activeProducts = products.stream()
                    .filter(p -> p.getStatus() == ProductStatus.ACTIVE)
                    .toList();

            // Insert Orders với OrderDetails và Payments
            List<Order> orders = createOrders(buyers, shippers, activeProducts);
            for (Order order : orders) {
                em.persist(order);
            }
            System.out.println("Inserted " + orders.size() + " orders");

            // Insert UserLogs
            List<UserLog> logs = createUserLogs(admins, sellers, buyers, shippers, orders);
            for (UserLog log : logs) {
                em.persist(log);
            }
            System.out.println("Inserted " + logs.size() + " user logs");

            em.getTransaction().commit();
            System.out.println("=== Data insertion completed successfully! ===");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }

    // ==================== ADMINS ====================
    private static List<Admin> createAdmins() {
        List<Admin> admins = new ArrayList<>();
        String[][] adminData = {
            {"Nguyễn Văn Admin", "admin1@ecommerce.com", "0901000001", "123 Đường Admin, Q1, TP.HCM"},
            {"Trần Thị Quản Lý", "admin2@ecommerce.com", "0901000002", "456 Đường Quản Lý, Q3, TP.HCM"},
            {"Lê Hoàng Điều Hành", "admin3@ecommerce.com", "0901000003", "789 Đường Điều Hành, Q7, TP.HCM"}
        };
        for (String[] data : adminData) {
            admins.add(new Admin(data[0], data[1], hashPassword("123456"), data[2], data[3]));
        }
        return admins;
    }

    // ==================== SELLERS ====================
    private static List<Seller> createSellers() {
        List<Seller> sellers = new ArrayList<>();
        String[][] sellerData = {
            {"Nguyễn Văn Hùng", "seller1@shop.com", "0902000001", "10 Chợ Lớn, Q5, TP.HCM", "Cửa hàng Phạm Gia"},
            {"Trần Thị Mai", "seller2@shop.com", "0902000002", "20 Bến Thành, Q1, TP.HCM", "Shop Hàng Việt"},
            {"Lê Minh Tuấn", "seller3@shop.com", "0902000003", "30 Tân Bình, Q.Tân Bình, TP.HCM", "Thực Phẩm Sạch Minh"},
            {"Phạm Thu Thủy", "seller4@shop.com", "0902000004", "40 Gò Vấp, Q.Gò Vấp, TP.HCM", "Đồ Hộp Lê Gia"},
            {"Hoàng Quốc Bảo", "seller5@shop.com", "0902000005", "50 Phú Nhuận, Q.Phú Nhuận, TP.HCM", "Fast Food Hoàng"},
            {"Huỳnh Gia Hân", "seller6@shop.com", "0902000006", "60 Bình Thạnh, Q.Bình Thạnh, TP.HCM", "Tiện Lợi 24h"},
            {"Phan Văn Đức", "seller7@shop.com", "0902000007", "70 Thủ Đức, TP.Thủ Đức, TP.HCM", "Mini Mart Đặng"},
            {"Vũ Thị Ngọc Ánh", "seller8@shop.com", "0902000008", "80 Q2, TP.Thủ Đức, TP.HCM", "Organic Bùi"},
            {"Đặng Thành Đạt", "seller9@shop.com", "0902000009", "90 Q7, TP.HCM", "Hàng Nhập Khẩu Ngô"},
            {"Bùi Phương Thảo", "seller10@shop.com", "0902000010", "100 Q10, TP.HCM", "Đặc Sản Việt Nam"},
            {"Đỗ Quang Minh", "seller11@shop.com", "0902000011", "110 Q11, TP.HCM", "Ẩm Thực Châu Á"},
            {"Hồ Thị Lan Anh", "seller12@shop.com", "0902000012", "120 Q.Tân Phú, TP.HCM", "K-Food Store"},
            {"Ngô Văn Long", "seller13@shop.com", "0902000013", "130 Q.Bình Tân, TP.HCM", "Japan Mart"},
            {"Dương Thúy Vy", "seller14@shop.com", "0902000014", "140 Q4, TP.HCM", "Euro Food"},
            {"Lý Văn Kiệt", "seller15@shop.com", "0902000015", "150 Q6, TP.HCM", "American Taste"},
            {"Trương Ngọc Ánh", "seller16@shop.com", "0902000016", "160 Q8, TP.HCM", "Healthy Choice"},
            {"Đinh Văn Toàn", "seller17@shop.com", "0902000017", "170 Q12, TP.HCM", "Diet Food Store"},
            {"Lâm Bảo Ngọc", "seller18@shop.com", "0902000018", "180 Q9, TP.HCM", "Gym Nutrition"},
            {"Hà Minh Tâm", "seller19@shop.com", "0902000019", "190 Hóc Môn, TP.HCM", "New Seller Shop"},
            {"Nguyễn Thùy Linh", "seller20@shop.com", "0902000020", "200 Củ Chi, TP.HCM", "Pending Store"}
        };

        // URLs ảnh giấy phép kinh doanh khác nhau cho mỗi seller
        String[] licenseUrls = {
            "https://images.unsplash.com/photo-1554224155-6726b3ff858f?w=600",
            "https://images.unsplash.com/photo-1450101499163-c8848c66ca85?w=600",
            "https://images.unsplash.com/photo-1586281380349-632531db7ed4?w=600",
            "https://images.unsplash.com/photo-1554224154-26032ffc0d07?w=600",
            "https://images.unsplash.com/photo-1568234928966-359c35dd8327?w=600",
            "https://images.unsplash.com/photo-1507679799987-c73779587ccf?w=600",
            "https://images.unsplash.com/photo-1521791136064-7986c2920216?w=600",
            "https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?w=600",
            "https://images.unsplash.com/photo-1542744173-8e7e53415bb0?w=600",
            "https://images.unsplash.com/photo-1556761175-5973dc0f32e7?w=600",
            "https://images.unsplash.com/photo-1553484771-371a605b060b?w=600",
            "https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=600",
            "https://images.unsplash.com/photo-1573497019940-1c28c88b4f3e?w=600",
            "https://images.unsplash.com/photo-1551836022-d5d88e9218df?w=600",
            "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=600",
            "https://images.unsplash.com/photo-1560250097-0b93528c311a?w=600",
            "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=600",
            "https://images.unsplash.com/photo-1573497019236-17f8177b81e8?w=600"
        };

        for (int i = 0; i < sellerData.length; i++) {
            String[] data = sellerData[i];
            Seller seller = new Seller(data[0], data[1], hashPassword("123456"), data[2], data[3], data[4]);
            
            if (i < 8) {
                // 8 seller đầu: đã được APPROVED
                seller.setBusinessLicenseUrl(licenseUrls[i]);
                seller.setLicenseSubmittedDate(getRandomPastDate(60));
                seller.setLicenseApprovedDate(getRandomPastDate(30));
                seller.setVerificationStatus(SellerStatus.APPROVED);
                seller.setRating(3.5f + random.nextFloat() * 1.5f);
                seller.setRevenue(random.nextDouble() * 100000000);
            } else if (i < 18) {
                // 10 seller tiếp theo: PENDING (chờ duyệt)
                seller.setBusinessLicenseUrl(licenseUrls[i % licenseUrls.length]);
                seller.setLicenseSubmittedDate(getRandomPastDate(14));
                seller.setVerificationStatus(SellerStatus.PENDING);
            } else {
                // 2 seller cuối: UNVERIFIED (chưa nộp giấy phép)
                seller.setVerificationStatus(SellerStatus.UNVERIFIED);
            }
            sellers.add(seller);
        }
        return sellers;
    }

    // ==================== BUYERS ====================
    private static List<Buyer> createBuyers() {
        List<Buyer> buyers = new ArrayList<>();
        String[] firstNames = {"Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Huỳnh", "Phan", "Vũ", "Võ", "Đặng"};
        String[] middleNames = {"Văn", "Thị", "Minh", "Hoàng", "Thanh", "Ngọc", "Quốc", "Hữu", "Đức", "Anh"};
        String[] lastNames = {"An", "Bình", "Cường", "Dũng", "Em", "Phúc", "Giang", "Hải", "Khang", "Linh"};
        String[] districts = {"Q1", "Q2", "Q3", "Q4", "Q5", "Q6", "Q7", "Q8", "Q9", "Q10", "Q11", "Q12", 
                              "Bình Thạnh", "Gò Vấp", "Phú Nhuận", "Tân Bình", "Tân Phú", "Thủ Đức"};

        for (int i = 1; i <= 50; i++) {
            String fullName = firstNames[random.nextInt(firstNames.length)] + " " +
                              middleNames[random.nextInt(middleNames.length)] + " " +
                              lastNames[random.nextInt(lastNames.length)];
            String email = "buyer" + i + "@gmail.com";
            String phone = "090300" + String.format("%04d", i);
            String address = (random.nextInt(200) + 1) + " Đường " + (random.nextInt(50) + 1) + ", " +
                             districts[random.nextInt(districts.length)] + ", TP.HCM";
            
            buyers.add(new Buyer(fullName, email, hashPassword("123456"), phone, address));
        }
        return buyers;
    }

    // ==================== SHIPPERS ====================
    private static List<Shipper> createShippers() {
        List<Shipper> shippers = new ArrayList<>();
        String[] firstNames = {"Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Huỳnh", "Phan", "Vũ", "Võ", "Đặng"};
        String[] middleNames = {"Văn", "Minh", "Hoàng", "Thanh", "Quốc", "Hữu", "Đức", "Anh", "Công", "Tuấn"};
        String[] lastNames = {"Tài", "Lộc", "Phát", "Thành", "Đạt", "Hùng", "Mạnh", "Duy", "Nam", "Bảo"};
        String[] districts = {"Q1", "Q3", "Q5", "Q7", "Q10", "Bình Thạnh", "Gò Vấp", "Tân Bình", "Phú Nhuận", "Thủ Đức"};

        for (int i = 1; i <= 30; i++) {
            String fullName = firstNames[random.nextInt(firstNames.length)] + " " +
                              middleNames[random.nextInt(middleNames.length)] + " " +
                              lastNames[random.nextInt(lastNames.length)];
            String email = "shipper" + i + "@delivery.com";
            String phone = "090400" + String.format("%04d", i);
            String address = districts[random.nextInt(districts.length)] + ", TP.HCM";
            
            Shipper shipper = new Shipper(fullName, email, hashPassword("123456"), phone, address);
            shipper.setAvailable(random.nextBoolean());
            shippers.add(shipper);
        }
        return shippers;
    }

    // ==================== PRODUCTS ====================
    private static List<Product> createProducts(List<Seller> approvedSellers) {
        List<Product> products = new ArrayList<>();
        
        // Đồ hộp (50 sản phẩm)
        String[][] cannedProducts = {
            {"Cá ngừ đóng hộp", "Cá ngừ ngâm dầu thực vật, giàu protein và omega-3", "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400"},
            {"Đậu đỏ đóng hộp", "Đậu đỏ nấu chín, tiện lợi cho các món chè và súp", "https://images.unsplash.com/photo-1515543904323-87f0b4f7e429?w=400"},
            {"Ngô ngọt đóng hộp", "Ngô ngọt hạt vàng, giòn ngọt tự nhiên", "https://images.unsplash.com/photo-1551754655-cd27e38d2076?w=400"},
            {"Đào đóng hộp", "Đào ngâm syrup, thơm ngọt mát lạnh", "https://images.unsplash.com/photo-1595743825637-cdafc8ad4173?w=400"},
            {"Cá mòi đóng hộp", "Cá mòi sốt cà chua, giàu canxi và vitamin D", "https://images.unsplash.com/photo-1599599810769-bcde5a160d32?w=400"},
            {"Đậu xanh đóng hộp", "Đậu xanh hạt mềm, tiện lợi nấu chè", "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=400"},
            {"Nấm đóng hộp", "Nấm rơm nguyên hạt, thơm ngon bổ dưỡng", "https://images.unsplash.com/photo-1504545102780-26774c1bb073?w=400"},
            {"Dứa đóng hộp", "Dứa khoanh ngâm syrup, chua ngọt hài hòa", "https://images.unsplash.com/photo-1550258987-190a2d41a8ba?w=400"},
            {"Thịt heo đóng hộp", "Thịt heo hầm mềm, tiện lợi cho bữa ăn nhanh", "https://images.unsplash.com/photo-1529692236671-f1f6cf9683ba?w=400"},
            {"Cà chua đóng hộp", "Cà chua xay nhuyễn, nguyên liệu nấu ăn tiện lợi", "https://images.unsplash.com/photo-1546470427-227c7369a9b8?w=400"},
            {"Đậu Hà Lan đóng hộp", "Đậu Hà Lan xanh mướt, giàu chất xơ", "https://images.unsplash.com/photo-1587735243615-c03f25aaff15?w=400"},
            {"Xoài đóng hộp", "Xoài chín ngâm syrup, thơm ngọt đậm đà", "https://images.unsplash.com/photo-1553279768-865429fa0078?w=400"},
            {"Cá hồi đóng hộp", "Cá hồi Alaska cao cấp, giàu omega-3", "https://images.unsplash.com/photo-1574781330855-d0db8cc6a79c?w=400"},
            {"Măng đóng hộp", "Măng tươi luộc chín, giòn ngon", "https://images.unsplash.com/photo-1590165482129-1b8b27698780?w=400"},
            {"Vải đóng hộp", "Vải thiều ngâm syrup, ngọt mát", "https://images.unsplash.com/photo-1577003833619-76bbd7f82948?w=400"},
            {"Đậu đen đóng hộp", "Đậu đen nấu chín, bổ thận tráng dương", "https://images.unsplash.com/photo-1551326844-4df70f78d0e9?w=400"},
            {"Ớt đóng hộp", "Ớt jalapeno ngâm giấm, cay nồng", "https://images.unsplash.com/photo-1583119022894-919a68a3d0e3?w=400"},
            {"Súp gà đóng hộp", "Súp gà nấm, ấm bụng bổ dưỡng", "https://images.unsplash.com/photo-1547592166-23ac45744acd?w=400"},
            {"Chôm chôm đóng hộp", "Chôm chôm ngâm syrup, ngọt thanh", "https://images.unsplash.com/photo-1609245340409-cad2474ab1d5?w=400"},
            {"Cá thu đóng hộp", "Cá thu sốt cà, thơm ngon đậm vị", "https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=400"},
            {"Bắp non đóng hộp", "Bắp non giòn ngọt, ăn kèm salad", "https://images.unsplash.com/photo-1606567595334-d39972c85dfd?w=400"},
            {"Nhãn đóng hộp", "Nhãn lồng ngâm syrup, ngọt thanh mát", "https://images.unsplash.com/photo-1597714026720-8f74c62310ba?w=400"},
            {"Đậu nành đóng hộp", "Đậu nành luộc chín, giàu protein thực vật", "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=400"},
            {"Ổi đóng hộp", "Ổi ngâm syrup, thơm ngọt tự nhiên", "https://images.unsplash.com/photo-1536511132770-e5058c7e8c46?w=400"},
            {"Pate gan đóng hộp", "Pate gan heo Pháp, béo ngậy thơm ngon", "https://images.unsplash.com/photo-1626200419199-391ae4be7a41?w=400"},
            {"Cà ri gà đóng hộp", "Cà ri gà Ấn Độ, đậm đà hương vị", "https://images.unsplash.com/photo-1565557623262-b51c2513a641?w=400"},
            {"Dưa chuột đóng hộp", "Dưa chuột muối chua, giòn tan", "https://images.unsplash.com/photo-1604503468506-a8da13d82791?w=400"},
            {"Mít đóng hộp", "Mít chín ngâm syrup, thơm ngọt", "https://images.unsplash.com/photo-1528825871115-3581a5387919?w=400"},
            {"Cá trích đóng hộp", "Cá trích sốt cà chua, giàu DHA", "https://images.unsplash.com/photo-1534604973900-c43ab4c2e0ab?w=400"},
            {"Đậu lăng đóng hộp", "Đậu lăng đỏ nấu chín, giàu sắt", "https://images.unsplash.com/photo-1515543904323-87f0b4f7e429?w=400"},
            {"Cocktail trái cây", "Hỗn hợp trái cây nhiệt đới ngâm syrup", "https://images.unsplash.com/photo-1490474418585-ba9bad8fd0ea?w=400"},
            {"Thịt bò đóng hộp", "Thịt bò hầm mềm, đậm đà", "https://images.unsplash.com/photo-1588168333986-5078d3ae3976?w=400"},
            {"Đu đủ đóng hộp", "Đu đủ chín ngâm syrup, mềm ngọt", "https://images.unsplash.com/photo-1517282009859-f000ec3b26fe?w=400"},
            {"Cá ngừ sốt ớt", "Cá ngừ đóng hộp vị cay, kích thích vị giác", "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400"},
            {"Hạt sen đóng hộp", "Hạt sen tươi luộc chín, bùi ngọt", "https://images.unsplash.com/photo-1606567595334-d39972c85dfd?w=400"},
            {"Sữa đặc có đường", "Sữa đặc Ông Thọ, ngọt béo", "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=400"},
            {"Cháo gà đóng hộp", "Cháo gà ăn liền, tiện lợi bổ dưỡng", "https://images.unsplash.com/photo-1547592166-23ac45744acd?w=400"},
            {"Nho đóng hộp", "Nho xanh không hạt ngâm syrup", "https://images.unsplash.com/photo-1537640538966-79f369143f8f?w=400"},
            {"Cá basa đóng hộp", "Cá basa phi lê sốt cà", "https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=400"},
            {"Đậu cô ve đóng hộp", "Đậu cô ve xanh giòn, giàu vitamin", "https://images.unsplash.com/photo-1567375698348-5d9d5ae99de0?w=400"},
            {"Táo đóng hộp", "Táo ngâm syrup, giòn ngọt", "https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=400"},
            {"Cá cơm đóng hộp", "Cá cơm chiên giòn đóng hộp", "https://images.unsplash.com/photo-1534604973900-c43ab4c2e0ab?w=400"},
            {"Khoai môn đóng hộp", "Khoai môn nấu chín, bùi thơm", "https://images.unsplash.com/photo-1590165482129-1b8b27698780?w=400"},
            {"Lê đóng hộp", "Lê ngâm syrup, mát lành", "https://images.unsplash.com/photo-1514756331096-242fdeb70d4a?w=400"},
            {"Thịt gà đóng hộp", "Thịt gà xé sợi đóng hộp", "https://images.unsplash.com/photo-1598103442097-8b74394b95c6?w=400"},
            {"Cà rốt đóng hộp", "Cà rốt baby luộc chín, ngọt bùi", "https://images.unsplash.com/photo-1598170845058-32b9d6a5da37?w=400"},
            {"Mận đóng hộp", "Mận ngâm syrup, chua ngọt", "https://images.unsplash.com/photo-1596363505729-4190a9506133?w=400"},
            {"Cá nục đóng hộp", "Cá nục sốt cà chua, thơm ngon", "https://images.unsplash.com/photo-1534604973900-c43ab4c2e0ab?w=400"},
            {"Khoai lang đóng hộp", "Khoai lang nghiền mịn, ngọt tự nhiên", "https://images.unsplash.com/photo-1590165482129-1b8b27698780?w=400"},
            {"Cherry đóng hộp", "Cherry đỏ ngâm syrup, ngọt thanh", "https://images.unsplash.com/photo-1528821128474-27f963b062bf?w=400"}
        };

        // Thức ăn nhanh (50 sản phẩm)
        String[][] fastFoodProducts = {
            {"Mì gói Hảo Hảo", "Mì ăn liền vị tôm chua cay, thương hiệu quốc dân", "https://images.unsplash.com/photo-1612929633738-8fe44f7ec841?w=400"},
            {"Phở ăn liền", "Phở bò ăn liền, hương vị truyền thống", "https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?w=400"},
            {"Bánh mì sandwich", "Bánh mì sandwich lát mềm mịn", "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=400"},
            {"Xúc xích tiệt trùng", "Xúc xích heo Đức Việt, tiện lợi", "https://images.unsplash.com/photo-1587536849024-daaa4a417b16?w=400"},
            {"Cơm cháy chà bông", "Cơm cháy giòn rụm kèm chà bông", "https://images.unsplash.com/photo-1536304993881-ff6e9eefa2a6?w=400"},
            {"Bánh tráng trộn", "Bánh tráng trộn đủ vị, ăn vặt số 1", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400"},
            {"Bún bò ăn liền", "Bún bò Huế ăn liền, cay nồng đậm đà", "https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?w=400"},
            {"Bánh gạo Hàn Quốc", "Bánh gạo Topokki, dẻo dai thơm ngon", "https://images.unsplash.com/photo-1635363638580-c2809d049eee?w=400"},
            {"Mì Ý sốt bò bằm", "Mì Ý ăn liền với sốt bò bằm", "https://images.unsplash.com/photo-1621996346565-e3dbc646d9a9?w=400"},
            {"Cháo gói ăn liền", "Cháo thịt bằm ăn liền, tiện lợi", "https://images.unsplash.com/photo-1547592166-23ac45744acd?w=400"},
            {"Bánh bao nhân thịt", "Bánh bao nhân thịt hấp nóng", "https://images.unsplash.com/photo-1563245372-f21724e3856d?w=400"},
            {"Pizza đông lạnh", "Pizza phô mai 4 loại đông lạnh", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400"},
            {"Há cảo đông lạnh", "Há cảo tôm thịt đông lạnh", "https://images.unsplash.com/photo-1496116218417-1a781b1c416c?w=400"},
            {"Burger bò đông lạnh", "Burger bò Mỹ đông lạnh", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400"},
            {"Gà rán đông lạnh", "Gà rán tẩm bột đông lạnh", "https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58?w=400"},
            {"Khoai tây chiên đông lạnh", "Khoai tây McCain đông lạnh", "https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=400"},
            {"Nem rán đông lạnh", "Nem rán Việt Nam đông lạnh", "https://images.unsplash.com/photo-1544025162-d76694265947?w=400"},
            {"Sủi cảo đông lạnh", "Sủi cảo nhân tôm thịt", "https://images.unsplash.com/photo-1496116218417-1a781b1c416c?w=400"},
            {"Hot dog đông lạnh", "Hot dog xúc xích Mỹ", "https://images.unsplash.com/photo-1612392062126-2f1d66380bb5?w=400"},
            {"Bánh xèo ăn liền", "Bột bánh xèo pha sẵn", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400"},
            {"Mì udon ăn liền", "Mì udon Nhật Bản ăn liền", "https://images.unsplash.com/photo-1618841557871-b4664fbf0cb3?w=400"},
            {"Bánh cuốn ăn liền", "Bánh cuốn nhân thịt ăn liền", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400"},
            {"Cơm chiên đông lạnh", "Cơm chiên Dương Châu đông lạnh", "https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=400"},
            {"Chả giò đông lạnh", "Chả giò rế đông lạnh", "https://images.unsplash.com/photo-1544025162-d76694265947?w=400"},
            {"Bánh flan ăn liền", "Bột bánh flan pha sẵn", "https://images.unsplash.com/photo-1528975604071-b4dc52a2d18c?w=400"},
            {"Mì ramen Nhật", "Mì ramen tonkotsu ăn liền", "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=400"},
            {"Bánh tráng cuốn", "Bánh tráng cuốn gỏi cuốn", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400"},
            {"Dimsum đông lạnh", "Dimsum tổng hợp đông lạnh", "https://images.unsplash.com/photo-1563245372-f21724e3856d?w=400"},
            {"Bánh mì que", "Bánh mì que pate đông lạnh", "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=400"},
            {"Mì xào đông lạnh", "Mì xào hải sản đông lạnh", "https://images.unsplash.com/photo-1612929633738-8fe44f7ec841?w=400"},
            {"Bánh bột lọc", "Bánh bột lọc nhân tôm đông lạnh", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400"},
            {"Cá viên chiên", "Cá viên chiên đông lạnh", "https://images.unsplash.com/photo-1529692236671-f1f6cf9683ba?w=400"},
            {"Bò viên đông lạnh", "Bò viên Vissan đông lạnh", "https://images.unsplash.com/photo-1529692236671-f1f6cf9683ba?w=400"},
            {"Tôm viên đông lạnh", "Tôm viên chiên giòn", "https://images.unsplash.com/photo-1529692236671-f1f6cf9683ba?w=400"},
            {"Bánh canh ăn liền", "Bánh canh cua ăn liền", "https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?w=400"},
            {"Miến ăn liền", "Miến gà ăn liền", "https://images.unsplash.com/photo-1612929633738-8fe44f7ec841?w=400"},
            {"Hủ tiếu ăn liền", "Hủ tiếu Nam Vang ăn liền", "https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?w=400"},
            {"Bánh ướt đông lạnh", "Bánh ướt cuốn chả lụa", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400"},
            {"Chả lụa đông lạnh", "Chả lụa Vissan đông lạnh", "https://images.unsplash.com/photo-1529692236671-f1f6cf9683ba?w=400"},
            {"Bánh đa cua", "Bánh đa cua Hải Phòng ăn liền", "https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?w=400"},
            {"Mì Quảng ăn liền", "Mì Quảng ăn liền đặc sản", "https://images.unsplash.com/photo-1612929633738-8fe44f7ec841?w=400"},
            {"Bánh tằm đông lạnh", "Bánh tằm bì đông lạnh", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400"},
            {"Gỏi cuốn đông lạnh", "Gỏi cuốn tôm thịt đông lạnh", "https://images.unsplash.com/photo-1544025162-d76694265947?w=400"},
            {"Bánh khọt đông lạnh", "Bánh khọt Vũng Tàu đông lạnh", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400"},
            {"Bún chả đông lạnh", "Bún chả Hà Nội đông lạnh", "https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?w=400"},
            {"Bánh căn đông lạnh", "Bánh căn Nha Trang đông lạnh", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400"},
            {"Cơm tấm đông lạnh", "Cơm tấm sườn bì đông lạnh", "https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=400"},
            {"Bún riêu ăn liền", "Bún riêu cua ăn liền", "https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?w=400"},
            {"Bánh đúc đông lạnh", "Bánh đúc lạc đông lạnh", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400"}
        };

        // Tạo 50 sản phẩm đồ hộp
        for (int i = 0; i < cannedProducts.length; i++) {
            String[] data = cannedProducts[i];
            Seller seller = approvedSellers.get(i % approvedSellers.size());
            
            double originalPrice = 20000 + random.nextInt(80000);
            double salePrice = originalPrice * (0.7 + random.nextDouble() * 0.25);
            
            // 5 sản phẩm đầu sắp hết hạn (1-3 ngày) để test cảnh báo
            Date expirationDate = (i < 5) 
                ? getFutureDate(random.nextInt(3) + 1) 
                : getFutureDate(random.nextInt(365) + 180);
            
            Product product = new Product(
                data[0], data[1], originalPrice, salePrice,
                expirationDate,
                getRandomPastDate(90),
                50 + random.nextInt(200),
                data[2], seller
            );
            product.setCategory(ProductCategory.CANNED);
            
            // Phân bổ trạng thái: 25 ACTIVE, 5 REJECTED, 20 PENDING
            if (i < 25) {
                product.setStatus(ProductStatus.ACTIVE);
                product.setApprovedDate(getRandomPastDate(30));
            } else if (i < 30) {
                product.setStatus(ProductStatus.REJECTED);
            } else {
                product.setStatus(ProductStatus.PENDING_APPROVAL);
            }
            products.add(product);
        }

        // Tạo 50 sản phẩm thức ăn nhanh
        for (int i = 0; i < fastFoodProducts.length; i++) {
            String[] data = fastFoodProducts[i];
            Seller seller = approvedSellers.get((i + 5) % approvedSellers.size());
            
            double originalPrice = 15000 + random.nextInt(85000);
            double salePrice = originalPrice * (0.75 + random.nextDouble() * 0.2);
            
            Product product = new Product(
                data[0], data[1], originalPrice, salePrice,
                getFutureDate(random.nextInt(180) + 30),
                getRandomPastDate(60),
                30 + random.nextInt(170),
                data[2], seller
            );
            
            // Phân loại category
            if (data[0].contains("đông lạnh")) {
                product.setCategory(ProductCategory.FROZEN);
            } else if (data[0].contains("ăn liền") || data[0].contains("Mì")) {
                product.setCategory(ProductCategory.SNACKS);
            } else {
                product.setCategory(ProductCategory.OTHER);
            }
            
            // Phân bổ trạng thái: 25 ACTIVE, 5 REJECTED, 20 PENDING
            if (i < 25) {
                product.setStatus(ProductStatus.ACTIVE);
                product.setApprovedDate(getRandomPastDate(30));
            } else if (i < 30) {
                product.setStatus(ProductStatus.REJECTED);
            } else {
                product.setStatus(ProductStatus.PENDING_APPROVAL);
            }
            products.add(product);
        }

        return products;
    }

    // ==================== HELPER METHODS ====================
    
    private static String hashPassword(String password) {
        return org.mindrot.jbcrypt.BCrypt.hashpw(password, org.mindrot.jbcrypt.BCrypt.gensalt());
    }

    private static Date getRandomPastDate(int maxDaysAgo) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -random.nextInt(maxDaysAgo));
        return cal.getTime();
    }

    private static Date getFutureDate(int daysFromNow) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, daysFromNow);
        return cal.getTime();
    }

    // ==================== ORDERS ====================
    private static List<Order> createOrders(List<Buyer> buyers, List<Shipper> shippers, List<Product> activeProducts) {
        List<Order> orders = new ArrayList<>();
        String[] districts = {"Q1", "Q3", "Q5", "Q7", "Q10", "Bình Thạnh", "Gò Vấp", "Tân Bình", "Phú Nhuận", "Thủ Đức"};

        // Tạo 100 đơn hàng với các trạng thái khác nhau
        for (int i = 0; i < 100; i++) {
            Buyer buyer = buyers.get(random.nextInt(buyers.size()));
            String address = (random.nextInt(200) + 1) + " Đường " + (random.nextInt(50) + 1) + ", " +
                             districts[random.nextInt(districts.length)] + ", TP.HCM";

            Order order = new Order(buyer, address);
            order.setOrderDate(getRandomPastDate(60));

            // Phân bổ trạng thái: 20 PENDING, 15 CONFIRMED, 25 SHIPPING, 30 DELIVERED, 10 CANCELLED
            if (i < 20) {
                order.setStatus(OrderStatus.PENDING);
            } else if (i < 35) {
                order.setStatus(OrderStatus.CONFIRMED);
            } else if (i < 60) {
                order.setStatus(OrderStatus.SHIPPING);
                // Gán shipper cho đơn đang giao
                Shipper shipper = shippers.get(random.nextInt(shippers.size()));
                order.setShipper(shipper);
            } else if (i < 90) {
                order.setStatus(OrderStatus.DELIVERED);
                // Gán shipper cho đơn đã giao
                Shipper shipper = shippers.get(random.nextInt(shippers.size()));
                order.setShipper(shipper);
            } else {
                order.setStatus(OrderStatus.CANCELLED);
            }

            // Tạo OrderDetails (1-5 sản phẩm mỗi đơn)
            List<OrderDetail> orderDetails = new ArrayList<>();
            int numProducts = 1 + random.nextInt(5);
            double totalAmount = 0;

            for (int j = 0; j < numProducts; j++) {
                Product product = activeProducts.get(random.nextInt(activeProducts.size()));
                int quantity = 1 + random.nextInt(3);
                double price = product.getSalePrice();

                OrderDetail detail = new OrderDetail(order, product, quantity, price);
                orderDetails.add(detail);
                totalAmount += price * quantity;
            }
            order.setOrderDetails(orderDetails);

            // Tạo Payment
            PaymentMethod method = random.nextBoolean() ? PaymentMethod.COD : PaymentMethod.BANKING;
            Payment payment = new Payment(totalAmount, method);
            order.setPayment(payment);

            orders.add(order);
        }
        return orders;
    }

    // ==================== USER LOGS ====================
    private static List<UserLog> createUserLogs(List<Admin> admins, List<Seller> sellers, 
                                                 List<Buyer> buyers, List<Shipper> shippers,
                                                 List<Order> orders) {
        List<UserLog> logs = new ArrayList<>();

        // Logs đặt hàng của buyers
        for (int i = 0; i < Math.min(50, orders.size()); i++) {
            Order order = orders.get(i);
            UserLog log = new UserLog(
                order.getBuyer().getUserId().toString(),
                Role.BUYER,
                ActionType.BUYER_PLACE_ORDER,
                "Đặt đơn hàng #" + (i + 1),
                String.valueOf(i + 1),
                "Order"
            );
            log.setCreatedAt(order.getOrderDate());
            logs.add(log);
        }

        // Logs thanh toán
        for (int i = 0; i < 30; i++) {
            Order order = orders.get(random.nextInt(orders.size()));
            if (order.getPayment() != null && order.getPayment().getMethod() == PaymentMethod.BANKING) {
                UserLog log = new UserLog(
                    order.getBuyer().getUserId().toString(),
                    Role.BUYER,
                    ActionType.BUYER_PAY_ORDER,
                    "Thanh toán đơn hàng qua Banking",
                    String.valueOf(order.getOrderId()),
                    "Order"
                );
                log.setCreatedAt(getRandomPastDate(30));
                logs.add(log);
            }
        }

        // Logs hủy đơn
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.CANCELLED) {
                UserLog log = new UserLog(
                    order.getBuyer().getUserId().toString(),
                    Role.BUYER,
                    ActionType.BUYER_CANCEL_ORDER,
                    "Hủy đơn hàng #" + order.getOrderId(),
                    String.valueOf(order.getOrderId()),
                    "Order"
                );
                log.setCreatedAt(getRandomPastDate(20));
                logs.add(log);
            }
        }

        // Logs duyệt seller
        Admin admin = admins.get(0);
        for (Seller seller : sellers) {
            if (seller.getVerificationStatus() == SellerStatus.APPROVED) {
                UserLog log = new UserLog(
                    seller.getUserId().toString(),
                    Role.SELLER,
                    ActionType.SELLER_APPROVED,
                    "Seller " + seller.getShopName() + " được duyệt",
                    seller.getUserId().toString(),
                    "Seller",
                    admin.getUserId().toString()
                );
                log.setCreatedAt(seller.getLicenseApprovedDate());
                logs.add(log);
            }
        }

        // Logs seller tạo sản phẩm
        for (int i = 0; i < 30; i++) {
            Seller seller = sellers.stream()
                    .filter(s -> s.getVerificationStatus() == SellerStatus.APPROVED)
                    .toList().get(random.nextInt(8));
            UserLog log = new UserLog(
                seller.getUserId().toString(),
                Role.SELLER,
                ActionType.SELLER_CREATE_PRODUCT,
                "Seller " + seller.getShopName() + " đăng sản phẩm mới",
                String.valueOf(random.nextInt(100) + 1),
                "Product"
            );
            log.setCreatedAt(getRandomPastDate(45));
            logs.add(log);
        }

        // Logs duyệt sản phẩm
        for (int i = 0; i < 25; i++) {
            UserLog log = new UserLog(
                admin.getUserId().toString(),
                Role.ADMIN,
                ActionType.PRODUCT_APPROVED,
                "Admin duyệt sản phẩm #" + (i + 1),
                String.valueOf(i + 1),
                "Product",
                admin.getUserId().toString()
            );
            log.setCreatedAt(getRandomPastDate(30));
            logs.add(log);
        }

        // Logs shipper nhận đơn
        for (Order order : orders) {
            if (order.getShipper() != null && 
                (order.getStatus() == OrderStatus.SHIPPING || order.getStatus() == OrderStatus.DELIVERED)) {
                UserLog log = new UserLog(
                    order.getShipper().getUserId().toString(),
                    Role.SHIPPER,
                    ActionType.SHIPPER_ACCEPT_ORDER,
                    "Shipper nhận đơn hàng #" + order.getOrderId(),
                    String.valueOf(order.getOrderId()),
                    "Order"
                );
                log.setCreatedAt(getRandomPastDate(20));
                logs.add(log);
            }
        }

        // Logs giao hàng thành công
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.DELIVERED && order.getShipper() != null) {
                UserLog log = new UserLog(
                    order.getShipper().getUserId().toString(),
                    Role.SHIPPER,
                    ActionType.SHIPPER_COMPLETE_ORDER,
                    "Giao hàng thành công đơn #" + order.getOrderId(),
                    String.valueOf(order.getOrderId()),
                    "Order"
                );
                log.setCreatedAt(getRandomPastDate(15));
                logs.add(log);
            }
        }

        // Logs seller chấp nhận đơn
        for (int i = 0; i < 40; i++) {
            Seller seller = sellers.stream()
                    .filter(s -> s.getVerificationStatus() == SellerStatus.APPROVED)
                    .toList().get(random.nextInt(8));
            UserLog log = new UserLog(
                seller.getUserId().toString(),
                Role.SELLER,
                ActionType.SELLER_ACCEPT_ORDER,
                "Seller chấp nhận đơn hàng",
                String.valueOf(random.nextInt(100) + 1),
                "Order"
            );
            log.setCreatedAt(getRandomPastDate(25));
            logs.add(log);
        }

        return logs;
    }
}