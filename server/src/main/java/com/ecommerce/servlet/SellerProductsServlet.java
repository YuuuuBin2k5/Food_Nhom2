package com.ecommerce.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.ecommerce.dto.ProductDTO;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.entity.UserLog;
import com.ecommerce.entity.ActionType;
import com.ecommerce.entity.Role;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.UserLogService;
import com.ecommerce.util.MenuHelper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/seller/products")
public class SellerProductsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private ProductService productService = new ProductService();
    private UserLogService userLogService = new UserLogService();

    // --- XỬ LÝ GET: HIỂN THỊ DANH SÁCH ---
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String role = (String) session.getAttribute("role");
        if (!"SELLER".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        User user = (User) session.getAttribute("user");

        // Set menu items
        MenuHelper.setMenuItems(request, "SELLER", "/seller/products");

        try {
            String sellerId = user.getUserId();
            // Lấy tham số status để lọc theo ProductStatus
            String status = request.getParameter("status");

            // Lấy tất cả sản phẩm của seller (tự động kiểm tra hết hạn)
            List<Product> products = productService.getProductsBySeller(sellerId);

            // Lọc theo status nếu có
            if (status == null || status.trim().isEmpty()) {
                // Mặc định hiển thị PENDING_APPROVAL
                status = "PENDING_APPROVAL";
            }

            if (!"ALL".equals(status)) {
                String finalStatus = status; // Biến final để dùng trong lambda
                products = products.stream()
                        .filter(p -> finalStatus.equals(p.getStatus().toString()))
                        .toList();
            }

            request.setAttribute("products", products);

            // Thêm thông tin sản phẩm sắp hết hạn để hiển thị cảnh báo
            try {
                List<Product> expiringSoonProducts = productService.getExpiringSoonProducts(sellerId);
                request.setAttribute("expiringSoonProducts", expiringSoonProducts);
            } catch (Exception e) {
                System.err.println("Error getting expiring soon products: " + e.getMessage());
            }

            request.getRequestDispatcher("/seller/products.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi tải danh sách sản phẩm");
        }
    }

    // --- XỬ LÝ POST: TẠO MỚI / ẨN / HIỆN ---
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8"); // Để nhận tiếng Việt

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        String action = request.getParameter("action");

        System.out.println("[DEBUG] POST request - Action: " + action + ", User: " + user.getUserId());

        try {
            if ("create".equals(action)) {
                // Kiểm tra xem user có phải là Seller được approved không
                if (user instanceof com.ecommerce.entity.Seller) {
                    com.ecommerce.entity.Seller seller = (com.ecommerce.entity.Seller) user;
                    if (seller.getVerificationStatus() != com.ecommerce.entity.SellerStatus.APPROVED) {
                        request.setAttribute("error",
                                "Bạn cần được admin duyệt trước khi có thể đăng sản phẩm. Trạng thái hiện tại: "
                                        + seller.getVerificationStatus());
                        doGet(request, response);
                        return;
                    }
                }

                createProduct(request, user.getUserId());
                
                // Tạo log cho SELLER_CREATE_PRODUCT
                UserLog log = new UserLog(user.getUserId(), Role.SELLER, ActionType.SELLER_CREATE_PRODUCT,
                    "Seller đăng sản phẩm mới: \"" + request.getParameter("name") + "\"", 
                    null, "PRODUCT", null);
                userLogService.save(log);
                
                response.sendRedirect(
                        request.getContextPath() + "/seller/products?status=PENDING_APPROVAL&message=created");

            } else if ("update".equals(action)) {
                updateProduct(request, user.getUserId());
                response.sendRedirect(
                        request.getContextPath() + "/seller/products?status=PENDING_APPROVAL&message=updated");

            } else if ("hide".equals(action) || "show".equals(action)) {
                toggleProductStatus(request, user.getUserId(), action);
                // Redirect lại đúng status
                String redirectStatus = "show".equals(action) ? "PENDING_APPROVAL" : "ACTIVE";
                if ("hide".equals(action))
                    redirectStatus = "HIDDEN"; // Sau khi ẩn -> chuyển đến tab HIDDEN

                response.sendRedirect(request.getContextPath() + "/seller/products?status=" + redirectStatus);
            } else if ("restock".equals(action)) {
                // Xử lý nhập thêm hàng (có thể redirect đến trang chỉnh sửa)
                Long productId = Long.parseLong(request.getParameter("productId"));
                response.sendRedirect(
                        request.getContextPath() + "/seller/products/edit?id=" + productId + "&action=restock");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[ERROR] Exception in doPost: " + e.getMessage());
            request.setAttribute("error", "Lỗi xử lý: " + e.getMessage());
            doGet(request, response); // Load lại trang kèm thông báo lỗi
        }
    }

    // Hàm phụ: Xử lý tạo sản phẩm
    private void createProduct(HttpServletRequest request, String sellerId) throws Exception {
        System.out.println("[DEBUG] Starting createProduct for seller: " + sellerId);

        String name = request.getParameter("name");
        String desc = request.getParameter("description");
        String originalPriceStr = request.getParameter("originalPrice");
        String priceStr = request.getParameter("price");
        String quantityStr = request.getParameter("quantity");
        String imageUrl = request.getParameter("imageUrl");
        String dateStr = request.getParameter("expirationDate");
        String categoryStr = request.getParameter("category");

        System.out.println("[DEBUG] Product data - Name: " + name + ", OriginalPrice: " + originalPriceStr + ", Price: "
                + priceStr + ", Quantity: "
                + quantityStr + ", Date: " + dateStr + ", Category: " + categoryStr);

        if (name == null || name.trim().isEmpty()) {
            throw new Exception("Tên sản phẩm không được để trống");
        }
        if (originalPriceStr == null || originalPriceStr.trim().isEmpty()) {
            throw new Exception("Giá gốc không được để trống");
        }
        if (priceStr == null || priceStr.trim().isEmpty()) {
            throw new Exception("Giá bán không được để trống");
        }
        if (quantityStr == null || quantityStr.trim().isEmpty()) {
            throw new Exception("Số lượng không được để trống");
        }
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new Exception("Ngày hết hạn không được để trống");
        }

        double originalPrice = Double.parseDouble(originalPriceStr);
        double price = Double.parseDouble(priceStr);
        int quantity = Integer.parseInt(quantityStr);

        // Validate giá bán không được lớn hơn giá gốc
        if (price > originalPrice) {
            throw new Exception("Giá bán không được lớn hơn giá gốc");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date expDate = sdf.parse(dateStr);

        // Xử lý category - mặc định OTHER nếu không chọn hoặc không hợp lệ
        com.ecommerce.entity.ProductCategory category = com.ecommerce.entity.ProductCategory.OTHER;
        if (categoryStr != null && !categoryStr.trim().isEmpty()) {
            try {
                category = com.ecommerce.entity.ProductCategory.valueOf(categoryStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("[DEBUG] Invalid category: " + categoryStr + ", using OTHER as default");
                category = com.ecommerce.entity.ProductCategory.OTHER;
            }
        }

        // Tạo DTO (Data Transfer Object)
        ProductDTO dto = new ProductDTO();
        dto.setName(name);
        dto.setDescription(desc);
        dto.setOriginalPrice(originalPrice);
        dto.setSalePrice(price);
        dto.setQuantity(quantity);
        dto.setImageUrl(imageUrl);
        dto.setExpirationDate(expDate);
        dto.setCategory(category);

        System.out.println("[DEBUG] Calling productService.addProduct...");
        // Gọi Service (Lưu ý: Bạn cần chắc chắn ProductDTO có các setter này)
        productService.addProduct(sellerId, dto);
        System.out.println("[DEBUG] Product created successfully!");
    }

    // Hàm phụ: Xử lý Ẩn/Hiện
    private void toggleProductStatus(HttpServletRequest request, String sellerId, String action) throws Exception {
        Long productId = Long.parseLong(request.getParameter("productId"));
        productService.toggleProductStatus(sellerId, productId, action.toUpperCase());
    }

    // Hàm phụ: Xử lý cập nhật sản phẩm
    private void updateProduct(HttpServletRequest request, String sellerId) throws Exception {
        System.out.println("[DEBUG] Starting updateProduct for seller: " + sellerId);

        String productIdStr = request.getParameter("productId");
        String name = request.getParameter("name");
        String desc = request.getParameter("description");
        String originalPriceStr = request.getParameter("originalPrice");
        String priceStr = request.getParameter("price");
        String quantityStr = request.getParameter("quantity");
        String imageUrl = request.getParameter("imageUrl");
        String dateStr = request.getParameter("expirationDate");
        String categoryStr = request.getParameter("category");

        System.out.println("[DEBUG] Update product data - ID: " + productIdStr + ", Name: " + name + ", OriginalPrice: "
                + originalPriceStr + ", Price: " + priceStr);

        if (productIdStr == null || productIdStr.trim().isEmpty()) {
            throw new Exception("ID sản phẩm không hợp lệ");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new Exception("Tên sản phẩm không được để trống");
        }
        if (originalPriceStr == null || originalPriceStr.trim().isEmpty()) {
            throw new Exception("Giá gốc không được để trống");
        }
        if (priceStr == null || priceStr.trim().isEmpty()) {
            throw new Exception("Giá bán không được để trống");
        }
        if (quantityStr == null || quantityStr.trim().isEmpty()) {
            throw new Exception("Số lượng không được để trống");
        }
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new Exception("Ngày hết hạn không được để trống");
        }

        Long productId = Long.parseLong(productIdStr);
        double originalPrice = Double.parseDouble(originalPriceStr);
        double price = Double.parseDouble(priceStr);
        int quantity = Integer.parseInt(quantityStr);

        // Validate giá bán không được lớn hơn giá gốc
        if (price > originalPrice) {
            throw new Exception("Giá bán không được lớn hơn giá gốc");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date expDate = sdf.parse(dateStr);

        // Xử lý category
        com.ecommerce.entity.ProductCategory category = com.ecommerce.entity.ProductCategory.OTHER;
        if (categoryStr != null && !categoryStr.trim().isEmpty()) {
            try {
                category = com.ecommerce.entity.ProductCategory.valueOf(categoryStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("[DEBUG] Invalid category: " + categoryStr + ", using OTHER as default");
                category = com.ecommerce.entity.ProductCategory.OTHER;
            }
        }

        // Tạo DTO
        ProductDTO dto = new ProductDTO();
        dto.setProductId(productId);
        dto.setName(name);
        dto.setDescription(desc);
        dto.setOriginalPrice(originalPrice);
        dto.setSalePrice(price);
        dto.setQuantity(quantity);
        dto.setImageUrl(imageUrl);
        dto.setExpirationDate(expDate);
        dto.setCategory(category);

        System.out.println("[DEBUG] Calling productService.updateProduct...");
        // Gọi Service để cập nhật sản phẩm
        productService.updateProduct(sellerId, dto);
        System.out.println("[DEBUG] Product updated successfully!");
    }
}