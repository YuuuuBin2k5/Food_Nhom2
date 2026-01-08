package com.ecommerce.servlet;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.ecommerce.service.AdminProductService;
import com.ecommerce.service.UserLogService;
import com.ecommerce.util.MenuHelper;
import com.ecommerce.entity.Admin;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.ProductStatus;
import com.ecommerce.entity.UserLog;
import com.ecommerce.entity.ActionType;
import com.ecommerce.entity.Role;

@WebServlet("/admin/approveProduct")
public class AdminProductServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private AdminProductService productService = new AdminProductService();
    private UserLogService userLogService = new UserLogService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Kiểm tra role ADMIN
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        
        String action = request.getParameter("action");
        if ("detail".equals(action)) {
            viewProductDetail(request, response);
        } else {
            loadFirstPendingProduct(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Kiểm tra role ADMIN
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        String action = request.getParameter("action");
        if ("approve".equals(action)) {
            approveProduct(request, response);
        } else if ("reject".equals(action)) {
            rejectProduct(request, response);
        } else {
            loadFirstPendingProduct(request, response);
        }
    }

    /**
     * Load trang với product pending đầu tiên (ai đăng trước sẽ được duyệt trước)
     */
    private void loadFirstPendingProduct(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Lấy product đầu tiên cần duyệt (FIFO - First In First Out)
        Product product = productService.getFirstPendingProduct();
        processProductRequest(request, response, product);
    }

    /**
     * Xem chi tiết product được chọn từ danh sách
     */
    private void viewProductDetail(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Lấy product theo ID được chọn từ table
        String productIdStr = request.getParameter("productId");
        Product product = null;
        if (productIdStr != null) {
            try {
                Long productId = Long.parseLong(productIdStr);
                product = productService.findById(productId);
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        processProductRequest(request, response, product);
    }

    /**
     * Xử lý chung cho cả loadFirstPendingProduct và viewProductDetail
     */
    private void processProductRequest(HttpServletRequest request, HttpServletResponse response, Product product) 
            throws ServletException, IOException {
        MenuHelper.setMenuItems(request, "ADMIN", "/admin/approveProduct");
        HttpSession session = request.getSession();
        
        String tab = request.getParameter("tab");
        String sort = request.getParameter("sort");
        
        // Đọc từ session nếu không có param
        if (tab == null) {
            tab = (String) session.getAttribute("adminProductTab");
        }
        if (sort == null) {
            sort = (String) session.getAttribute("adminProductSort");
        }
        if (tab == null) tab = "pending";
        if (sort == null) sort = "newest";
        
        // Lưu vào session
        session.setAttribute("adminProductTab", tab);
        session.setAttribute("adminProductSort", sort);
        
        long pendingCount = productService.countByStatus(ProductStatus.PENDING_APPROVAL);
        long activeCount = productService.countByStatus(ProductStatus.ACTIVE);
        long rejectedCount = productService.countByStatus(ProductStatus.REJECTED);
        long hiddenCount = productService.countByStatus(ProductStatus.HIDDEN);
        long allCount = productService.countAll();
        
        List<Product> productList;
        switch (tab) {
            case "active":
                productList = getProductListWithSort(ProductStatus.ACTIVE, sort);
                break;
            case "rejected":
                productList = getProductListWithSort(ProductStatus.REJECTED, sort);
                break;
            case "hidden":
                productList = getProductListWithSort(ProductStatus.HIDDEN, sort);
                break;
            case "all":
                productList = getAllProductsWithSort(sort);
                break;
            default:
                tab = "pending";
                productList = getProductListWithSort(ProductStatus.PENDING_APPROVAL, sort);
                break;
        }
        
        setAttributes(request, product, productList, tab, sort, pendingCount, activeCount, rejectedCount, hiddenCount, allCount);
        request.getRequestDispatcher("/admin/admin_approves_product.jsp").forward(request, response);
    }

    /**
     * Lấy danh sách product theo status với sort từ service
     */
    private List<Product> getProductListWithSort(ProductStatus status, String sort) {
        switch (sort) {
            case "oldest":
                return productService.getProductsByStatusSortOldest(status);
            case "name":
                return productService.getProductsByStatusSortByName(status);
            case "shop":
                return productService.getProductsByStatusSortByShop(status);
            default: // newest
                return productService.getProductsByStatusSortNewest(status);
        }
    }

    /**
     * Lấy tất cả product với sort từ service
     */
    private List<Product> getAllProductsWithSort(String sort) {
        switch (sort) {
            case "oldest":
                return productService.getAllProductsSortOldest();
            case "name":
                return productService.getAllProductsSortByName();
            case "shop":
                return productService.getAllProductsSortByShop();
            default: // newest
                return productService.getAllProductsSortNewest();
        }
    }

    private void approveProduct(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String productIdStr = request.getParameter("productId");
        String productName = request.getParameter("productName");
        HttpSession session = request.getSession();
        Admin admin = (Admin) session.getAttribute("user");
        String adminId = admin.getUserId();
        
        try {
            Long productId = Long.parseLong(productIdStr);
            Product product = productService.findById(productId);
            
            // Lưu sellerId vào session để xem log sau
            if (product != null && product.getSeller() != null) {
                session.setAttribute("lastViewedUserId", product.getSeller().getUserId());
                session.setAttribute("lastViewedUserName", product.getSeller().getFullName());
                session.setAttribute("lastViewedUserType", "seller");
            }
            
            int result = productService.approveProduct(productId);
            if (result == 0) {
                String sellerId = (product != null && product.getSeller() != null) 
                    ? product.getSeller().getUserId() : null;
                String sellerName = (product != null && product.getSeller() != null) 
                    ? product.getSeller().getShopName() : "Unknown";
                
                UserLog log = new UserLog(sellerId, Role.SELLER, ActionType.PRODUCT_APPROVED,
                    "Sản phẩm \"" + productName + "\" của seller \"" + sellerName + "\" được duyệt bởi admin " + admin.getFullName(),
                    productIdStr, "PRODUCT", adminId);
                userLogService.save(log);
                request.setAttribute("message", "Đã duyệt sản phẩm \"" + productName + "\" thành công!");
            } else if (result == 2) {
                request.setAttribute("error", "Sản phẩm \"" + productName + "\" đã được xử lý bởi admin khác.");
            } else {
                request.setAttribute("error", "Không thể duyệt sản phẩm. Vui lòng thử lại.");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID sản phẩm không hợp lệ.");
        }
        loadFirstPendingProduct(request, response);
    }

    private void rejectProduct(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String productIdStr = request.getParameter("productId");
        String productName = request.getParameter("productName");
        HttpSession session = request.getSession();
        Admin admin = (Admin) session.getAttribute("user");
        String adminId = admin.getUserId();
        
        try {
            Long productId = Long.parseLong(productIdStr);
            Product product = productService.findById(productId);
            
            // Lưu sellerId vào session để xem log sau
            if (product != null && product.getSeller() != null) {
                session.setAttribute("lastViewedUserId", product.getSeller().getUserId());
                session.setAttribute("lastViewedUserName", product.getSeller().getFullName());
                session.setAttribute("lastViewedUserType", "seller");
            }
            
            int result = productService.rejectProduct(productId);
            if (result == 0) {
                String sellerId = (product != null && product.getSeller() != null) 
                    ? product.getSeller().getUserId() : null;
                String sellerName = (product != null && product.getSeller() != null) 
                    ? product.getSeller().getShopName() : "Unknown";
                
                UserLog log = new UserLog(sellerId, Role.SELLER, ActionType.PRODUCT_REJECTED,
                    "Sản phẩm \"" + productName + "\" của seller \"" + sellerName + "\" bị từ chối bởi admin " + admin.getFullName(),
                    productIdStr, "PRODUCT", adminId);
                userLogService.save(log);
                request.setAttribute("message", "Đã từ chối sản phẩm \"" + productName + "\".");
            } else if (result == 2) {
                request.setAttribute("error", "Sản phẩm \"" + productName + "\" đã được xử lý bởi admin khác.");
            } else {
                request.setAttribute("error", "Không thể từ chối sản phẩm. Vui lòng thử lại.");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID sản phẩm không hợp lệ.");
        }
        loadFirstPendingProduct(request, response);
    }

    private void setAttributes(HttpServletRequest request, Product product, List<Product> productList,
            String tab, String sort, long pendingCount, long activeCount, long rejectedCount, long hiddenCount, long allCount) {
        request.setAttribute("product", product);
        request.setAttribute("productList", productList);
        request.setAttribute("currentTab", tab);
        request.setAttribute("currentSort", sort);
        request.setAttribute("pendingCount", pendingCount);
        request.setAttribute("activeCount", activeCount);
        request.setAttribute("rejectedCount", rejectedCount);
        request.setAttribute("hiddenCount", hiddenCount);
        request.setAttribute("allCount", allCount);
        request.setAttribute("activePage", "product");
    }
}