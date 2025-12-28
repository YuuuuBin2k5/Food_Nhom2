package com.ecommerce.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.ecommerce.service.AdminProductService;
import com.ecommerce.service.UserLogService;
import com.ecommerce.entity.Admin;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.ProductStatus;
import com.ecommerce.entity.UserLog;
import com.ecommerce.entity.ActionType;
import com.ecommerce.entity.Role;

@WebServlet("/admin/approveProduct")
public class AdminProductServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final int PAGE_SIZE = 6;
    
    private AdminProductService productService = new AdminProductService();
    private UserLogService userLogService = new UserLogService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }
        
        String action = request.getParameter("action");
        if (action == null) action = "list";
        
        switch (action) {
            case "detail":
                viewProductDetail(request, response);
                break;
            case "approve":
                approveProduct(request, response);
                break;
            case "reject":
                rejectProduct(request, response);
                break;
            default:
                loadProducts(request, response);
                break;
        }
    }


    private void loadProducts(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String tab = request.getParameter("tab");
        if (tab == null) tab = "pending";
        
        int page = 1;
        try {
            String pageStr = request.getParameter("page");
            if (pageStr != null) page = Integer.parseInt(pageStr);
            if (page < 1) page = 1;
        } catch (NumberFormatException e) {
            page = 1;
        }
        
        Product product = productService.getFirstPendingProduct();
        
        long pendingCount = productService.countByStatus(ProductStatus.PENDING_APPROVAL);
        long activeCount = productService.countByStatus(ProductStatus.ACTIVE);
        long rejectedCount = productService.countByStatus(ProductStatus.REJECTED);
        long allCount = productService.countAll();
        
        List<Product> productList = new ArrayList<>();
        long totalItems = 0;
        
        switch (tab) {
            case "active":
                productList = productService.getProductsByStatus(ProductStatus.ACTIVE, page, PAGE_SIZE);
                totalItems = activeCount;
                break;
            case "rejected":
                productList = productService.getProductsByStatus(ProductStatus.REJECTED, page, PAGE_SIZE);
                totalItems = rejectedCount;
                break;
            case "all":
                productList = productService.getAllProducts(page, PAGE_SIZE);
                totalItems = allCount;
                break;
            default:
                tab = "pending";
                productList = productService.getProductsByStatus(ProductStatus.PENDING_APPROVAL, page, PAGE_SIZE);
                totalItems = pendingCount;
                break;
        }
        
        int totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
        
        setAttributes(request, product, productList, tab, page, totalPages, totalItems,
                     pendingCount, activeCount, rejectedCount, allCount);
        request.getRequestDispatcher("/admin/admin_approves_product.jsp").forward(request, response);
    }

    private void viewProductDetail(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String productIdStr = request.getParameter("productId");
        String tab = request.getParameter("tab");
        String pageStr = request.getParameter("page");
        
        Product product = null;
        if (productIdStr != null) {
            try {
                Long productId = Long.parseLong(productIdStr);
                product = productService.findById(productId);
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        
        if (tab == null) tab = "pending";
        int page = 1;
        try {
            if (pageStr != null) page = Integer.parseInt(pageStr);
        } catch (NumberFormatException e) {
            page = 1;
        }
        
        long pendingCount = productService.countByStatus(ProductStatus.PENDING_APPROVAL);
        long activeCount = productService.countByStatus(ProductStatus.ACTIVE);
        long rejectedCount = productService.countByStatus(ProductStatus.REJECTED);
        long allCount = productService.countAll();
        
        List<Product> productList = new ArrayList<>();
        long totalItems = 0;
        
        switch (tab) {
            case "active":
                productList = productService.getProductsByStatus(ProductStatus.ACTIVE, page, PAGE_SIZE);
                totalItems = activeCount;
                break;
            case "rejected":
                productList = productService.getProductsByStatus(ProductStatus.REJECTED, page, PAGE_SIZE);
                totalItems = rejectedCount;
                break;
            case "all":
                productList = productService.getAllProducts(page, PAGE_SIZE);
                totalItems = allCount;
                break;
            default:
                tab = "pending";
                productList = productService.getProductsByStatus(ProductStatus.PENDING_APPROVAL, page, PAGE_SIZE);
                totalItems = pendingCount;
                break;
        }
        
        int totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
        
        setAttributes(request, product, productList, tab, page, totalPages, totalItems,
                     pendingCount, activeCount, rejectedCount, allCount);
        request.getRequestDispatcher("/admin/admin_approves_product.jsp").forward(request, response);
    }


    private void approveProduct(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String productIdStr = request.getParameter("productId");
        String productName = request.getParameter("productName");
        HttpSession session = request.getSession();
        Admin admin = (Admin) session.getAttribute("user");
        String adminId = admin != null ? admin.getUserId() : "unknown";
        
        try {
            Long productId = Long.parseLong(productIdStr);
            Product product = productService.findById(productId);
            
            int result = productService.approveProduct(productId);
            switch (result) {
                case 0: // Thành công
                    String sellerId = (product != null && product.getSeller() != null) 
                        ? product.getSeller().getUserId() : null;
                    String sellerName = (product != null && product.getSeller() != null) 
                        ? product.getSeller().getShopName() : "Unknown";
                    
                    UserLog log = new UserLog(sellerId, Role.SELLER, ActionType.PRODUCT_APPROVED,
                        "Sản phẩm \"" + productName + "\" của seller \"" + sellerName + "\" được duyệt bởi admin " + adminId,
                        productIdStr, "PRODUCT", adminId);
                    userLogService.save(log);
                    request.setAttribute("message", "Đã duyệt sản phẩm \"" + productName + "\" thành công!");
                    break;
                case 2: // Đã được xử lý bởi admin khác
                    request.setAttribute("error", "Sản phẩm \"" + productName + "\" đã được xử lý bởi admin khác.");
                    break;
                default: // Lỗi khác
                    request.setAttribute("error", "Không thể duyệt sản phẩm. Vui lòng thử lại.");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID sản phẩm không hợp lệ.");
        }
        loadProducts(request, response);
    }

    private void rejectProduct(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String productIdStr = request.getParameter("productId");
        String productName = request.getParameter("productName");
        HttpSession session = request.getSession();
        Admin admin = (Admin) session.getAttribute("user");
        String adminId = admin != null ? admin.getUserId() : "unknown";
        
        try {
            Long productId = Long.parseLong(productIdStr);
            Product product = productService.findById(productId);
            
            int result = productService.rejectProduct(productId);
            switch (result) {
                case 0: // Thành công
                    String sellerId = (product != null && product.getSeller() != null) 
                        ? product.getSeller().getUserId() : null;
                    String sellerName = (product != null && product.getSeller() != null) 
                        ? product.getSeller().getShopName() : "Unknown";
                    
                    UserLog log = new UserLog(sellerId, Role.SELLER, ActionType.PRODUCT_REJECTED,
                        "Sản phẩm \"" + productName + "\" của seller \"" + sellerName + "\" bị từ chối bởi admin " + adminId,
                        productIdStr, "PRODUCT", adminId);
                    userLogService.save(log);
                    request.setAttribute("message", "Đã từ chối sản phẩm \"" + productName + "\".");
                    break;
                case 2: // Đã được xử lý bởi admin khác
                    request.setAttribute("error", "Sản phẩm \"" + productName + "\" đã được xử lý bởi admin khác.");
                    break;
                default: // Lỗi khác
                    request.setAttribute("error", "Không thể từ chối sản phẩm. Vui lòng thử lại.");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID sản phẩm không hợp lệ.");
        }
        loadProducts(request, response);
    }

    private void setAttributes(HttpServletRequest request, Product product, List<Product> productList,
            String tab, int page, int totalPages, long totalItems,
            long pendingCount, long activeCount, long rejectedCount, long allCount) {
        request.setAttribute("product", product);
        request.setAttribute("productList", productList);
        request.setAttribute("currentTab", tab);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalItems", totalItems);
        request.setAttribute("pendingCount", pendingCount);
        request.setAttribute("activeCount", activeCount);
        request.setAttribute("rejectedCount", rejectedCount);
        request.setAttribute("allCount", allCount);
        request.setAttribute("activePage", "product");
    }
}
