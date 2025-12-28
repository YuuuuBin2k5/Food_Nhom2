package com.ecommerce.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.ecommerce.dto.ProductDTO;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.service.ProductService;
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
            // Lấy tham số tab để lọc (active / pending / sold)
            String tab = request.getParameter("tab");

            // Logic lọc sản phẩm theo tab (Bạn cần update ProductService để hỗ trợ lọc này,
            // hiện tại mình lấy tất cả trước)
            List<Product> products = productService.getProductsBySeller(sellerId);

            // Lọc tạm thời bằng Java Stream nếu Service chưa hỗ trợ
            if (tab == null || "active".equals(tab)) {
                products = products.stream()
                        .filter(p -> "ACTIVE".equals(p.getStatus().toString()))
                        .toList();
            } else if ("pending".equals(tab)) {
                products = products.stream()
                        .filter(p -> "PENDING_APPROVAL".equals(p.getStatus().toString())
                                || "HIDDEN".equals(p.getStatus().toString()))
                        .toList();
            } else if ("sold".equals(tab)) {
                products = products.stream()
                        .filter(p -> "SOLD_OUT".equals(p.getStatus().toString())
                                || "EXPIRED".equals(p.getStatus().toString()))
                        .toList();
            }

            request.setAttribute("products", products);
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
                response.sendRedirect(request.getContextPath() + "/seller/products?tab=pending&message=created");

            } else if ("hide".equals(action) || "show".equals(action)) {
                toggleProductStatus(request, user.getUserId(), action);
                // Redirect lại đúng tab
                String redirectTab = "show".equals(action) ? "pending" : "active"; // Nếu hiện lại -> về pending, Ẩn ->
                                                                                   // vẫn ở list active (nhưng biến mất)
                if ("hide".equals(action))
                    redirectTab = "active"; // Đứng ở active bấm ẩn

                response.sendRedirect(request.getContextPath() + "/seller/products?tab=" + redirectTab);
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
}