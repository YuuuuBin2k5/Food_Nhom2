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
import com.ecommerce.service.AdminSellerService;
import com.ecommerce.service.UserLogService;
import com.ecommerce.entity.Admin;
import com.ecommerce.entity.Seller;
import com.ecommerce.entity.SellerStatus;
import com.ecommerce.entity.UserLog;
import com.ecommerce.entity.ActionType;
import com.ecommerce.entity.Role;

@WebServlet("/admin/approveSeller")
public class AdminSellerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final int PAGE_SIZE = 6;
    
    private AdminSellerService sellerDAO = new AdminSellerService();
    private UserLogService userLogDAO = new UserLogService();

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
        
        // Kiểm tra đăng nhập
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        String action = request.getParameter("action");
        if (action == null) action = "list";
        
        switch (action) {
            case "detail":
                viewSellerDetail(request, response);
                break;
            case "approve":
                approveSeller(request, response);
                break;
            case "reject":
                rejectSeller(request, response);
                break;
            default:
                loadSellers(request, response);
                break;
        }
    }

    private void loadSellers(HttpServletRequest request, HttpServletResponse response) 
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
        
        Seller seller = sellerDAO.getFirstPendingSeller();
        
        long pendingCount = sellerDAO.countByStatus(SellerStatus.PENDING);
        long approvedCount = sellerDAO.countByStatus(SellerStatus.APPROVED);
        long rejectedCount = sellerDAO.countByStatus(SellerStatus.REJECTED);
        long unverifiedCount = sellerDAO.countByStatus(SellerStatus.UNVERIFIED);
        long allCount = sellerDAO.countAll();
        
        List<Seller> sellerList = new ArrayList<>();
        long totalItems = 0;
        
        switch (tab) {
            case "approved":
                sellerList = sellerDAO.getSellersByStatus(SellerStatus.APPROVED, page, PAGE_SIZE);
                totalItems = approvedCount;
                break;
            case "rejected":
                sellerList = sellerDAO.getSellersByStatus(SellerStatus.REJECTED, page, PAGE_SIZE);
                totalItems = rejectedCount;
                break;
            case "unverified":
                sellerList = sellerDAO.getSellersByStatus(SellerStatus.UNVERIFIED, page, PAGE_SIZE);
                totalItems = unverifiedCount;
                break;
            case "all":
                sellerList = sellerDAO.getAllSellers(page, PAGE_SIZE);
                totalItems = allCount;
                break;
            default:
                tab = "pending";
                sellerList = sellerDAO.getSellersByStatus(SellerStatus.PENDING, page, PAGE_SIZE);
                totalItems = pendingCount;
                break;
        }
        
        int totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
        
        setAttributes(request, seller, sellerList, tab, page, totalPages, totalItems,
                     pendingCount, approvedCount, rejectedCount, unverifiedCount, allCount);
        request.getRequestDispatcher("/admin/admin_approves_seller.jsp").forward(request, response);
    }

    private void viewSellerDetail(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String sellerId = request.getParameter("sellerId");
        String tab = request.getParameter("tab");
        String pageStr = request.getParameter("page");
        
        Seller seller = null;
        if (sellerId != null) {
            seller = sellerDAO.findById(sellerId);
        }
        
        if (tab == null) tab = "pending";
        int page = 1;
        try {
            if (pageStr != null) page = Integer.parseInt(pageStr);
        } catch (NumberFormatException e) {
            page = 1;
        }
        
        long pendingCount = sellerDAO.countByStatus(SellerStatus.PENDING);
        long approvedCount = sellerDAO.countByStatus(SellerStatus.APPROVED);
        long rejectedCount = sellerDAO.countByStatus(SellerStatus.REJECTED);
        long unverifiedCount = sellerDAO.countByStatus(SellerStatus.UNVERIFIED);
        long allCount = sellerDAO.countAll();
        
        List<Seller> sellerList = new ArrayList<>();
        long totalItems = 0;
        
        switch (tab) {
            case "approved":
                sellerList = sellerDAO.getSellersByStatus(SellerStatus.APPROVED, page, PAGE_SIZE);
                totalItems = approvedCount;
                break;
            case "rejected":
                sellerList = sellerDAO.getSellersByStatus(SellerStatus.REJECTED, page, PAGE_SIZE);
                totalItems = rejectedCount;
                break;
            case "unverified":
                sellerList = sellerDAO.getSellersByStatus(SellerStatus.UNVERIFIED, page, PAGE_SIZE);
                totalItems = unverifiedCount;
                break;
            case "all":
                sellerList = sellerDAO.getAllSellers(page, PAGE_SIZE);
                totalItems = allCount;
                break;
            default:
                tab = "pending";
                sellerList = sellerDAO.getSellersByStatus(SellerStatus.PENDING, page, PAGE_SIZE);
                totalItems = pendingCount;
                break;
        }
        
        int totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
        
        setAttributes(request, seller, sellerList, tab, page, totalPages, totalItems,
                     pendingCount, approvedCount, rejectedCount, unverifiedCount, allCount);
        request.getRequestDispatcher("/admin/admin_approves_seller.jsp").forward(request, response);
    }

    private void approveSeller(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String sellerId = request.getParameter("sellerId");
        String shopName = request.getParameter("shopName");
        HttpSession session = request.getSession();
        Admin admin = (Admin) session.getAttribute("user");
        String adminId = admin != null ? admin.getUserId() : "unknown";
        
        if (sellerId != null) {
            int result = sellerDAO.approveSeller(sellerId);
            switch (result) {
                case 0: // Thành công
                    UserLog log = new UserLog(sellerId, Role.SELLER, ActionType.SELLER_APPROVED,
                        "Seller \"" + shopName + "\" được duyệt bởi admin " + adminId, null, null, adminId);
                    userLogDAO.save(log);
                    request.setAttribute("message", "Đã duyệt seller \"" + shopName + "\" thành công!");
                    break;
                case 2: // Đã được xử lý bởi admin khác
                    request.setAttribute("error", "Seller \"" + shopName + "\" đã được xử lý bởi admin khác.");
                    break;
                default: // Lỗi khác
                    request.setAttribute("error", "Không thể duyệt seller. Vui lòng thử lại.");
            }
        } else {
            request.setAttribute("error", "Không thể duyệt seller. Vui lòng thử lại.");
        }
        loadSellers(request, response);
    }

    private void rejectSeller(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String sellerId = request.getParameter("sellerId");
        String shopName = request.getParameter("shopName");
        HttpSession session = request.getSession();
        Admin admin = (Admin) session.getAttribute("user");
        String adminId = admin != null ? admin.getUserId() : "unknown";
        
        if (sellerId != null) {
            int result = sellerDAO.rejectSeller(sellerId);
            switch (result) {
                case 0: // Thành công
                    UserLog log = new UserLog(sellerId, Role.SELLER, ActionType.SELLER_REJECTED,
                        "Seller \"" + shopName + "\" bị từ chối bởi admin " + adminId, null, null, adminId);
                    userLogDAO.save(log);
                    request.setAttribute("message", "Đã từ chối seller \"" + shopName + "\".");
                    break;
                case 2: // Đã được xử lý bởi admin khác
                    request.setAttribute("error", "Seller \"" + shopName + "\" đã được xử lý bởi admin khác.");
                    break;
                default: // Lỗi khác
                    request.setAttribute("error", "Không thể từ chối seller. Vui lòng thử lại.");
            }
        } else {
            request.setAttribute("error", "Không thể từ chối seller. Vui lòng thử lại.");
        }
        loadSellers(request, response);
    }

    private void setAttributes(HttpServletRequest request, Seller seller, List<Seller> sellerList,
            String tab, int page, int totalPages, long totalItems,
            long pendingCount, long approvedCount, long rejectedCount, long unverifiedCount, long allCount) {
        request.setAttribute("seller", seller);
        request.setAttribute("sellerList", sellerList);
        request.setAttribute("currentTab", tab);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalItems", totalItems);
        request.setAttribute("pendingCount", pendingCount);
        request.setAttribute("approvedCount", approvedCount);
        request.setAttribute("rejectedCount", rejectedCount);
        request.setAttribute("unverifiedCount", unverifiedCount);
        request.setAttribute("allCount", allCount);
        request.setAttribute("activePage", "seller");
    }
}
