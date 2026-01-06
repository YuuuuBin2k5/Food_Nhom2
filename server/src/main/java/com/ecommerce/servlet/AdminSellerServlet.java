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
import com.ecommerce.util.MenuHelper;
import com.ecommerce.entity.Admin;
import com.ecommerce.entity.Seller;
import com.ecommerce.entity.SellerStatus;
import com.ecommerce.entity.UserLog;
import com.ecommerce.entity.ActionType;
import com.ecommerce.entity.Role;

@WebServlet("/admin/approveSeller")
public class AdminSellerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private AdminSellerService sellerDAO = new AdminSellerService();
    private UserLogService userLogDAO = new UserLogService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        String action = request.getParameter("action");
        if ("detail".equals(action)) {
            viewSellerDetail(request, response);
        } else {
            loadFirstPendingSeller(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        if ("approve".equals(action)) {
            approveSeller(request, response);
        } else if ("reject".equals(action)) {
            rejectSeller(request, response);
        } else {
            loadFirstPendingSeller(request, response);
        }
    }

    /**
     * Load trang với seller pending đầu tiên (ai nộp giấy phép trước sẽ được duyệt trước)
     */
    private void loadFirstPendingSeller(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Lấy seller đầu tiên cần duyệt (FIFO - First In First Out)
        Seller seller = sellerDAO.getFirstPendingSeller();
        processSellerRequest(request, response, seller);
    }

    /**
     * Xem chi tiết seller được chọn từ danh sách
     */
    private void viewSellerDetail(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Lấy seller theo ID được chọn từ table
        String sellerId = request.getParameter("sellerId");
        Seller seller = (sellerId != null) ? sellerDAO.findById(sellerId) : null;
        processSellerRequest(request, response, seller);
    }

    /**
     * Xử lý chung cho cả loadFirstPendingSeller và viewSellerDetail
     */
    private void processSellerRequest(HttpServletRequest request, HttpServletResponse response, Seller seller) 
            throws ServletException, IOException {
        MenuHelper.setMenuItems(request, "ADMIN", "/admin/approveSeller");
        HttpSession session = request.getSession();
        
        String tab = request.getParameter("tab");
        String sort = request.getParameter("sort");
        
        // Đọc từ session nếu không có param
        if (tab == null) {
            tab = (String) session.getAttribute("adminSellerTab");
        }
        if (sort == null) {
            sort = (String) session.getAttribute("adminSellerSort");
        }
        if (tab == null) tab = "pending";
        if (sort == null) sort = "newest";
        
        // Lưu vào session
        session.setAttribute("adminSellerTab", tab);
        session.setAttribute("adminSellerSort", sort);
        
        long pendingCount = sellerDAO.countByStatus(SellerStatus.PENDING);
        long approvedCount = sellerDAO.countByStatus(SellerStatus.APPROVED);
        long rejectedCount = sellerDAO.countByStatus(SellerStatus.REJECTED);
        long unverifiedCount = sellerDAO.countByStatus(SellerStatus.UNVERIFIED);
        long allCount = sellerDAO.countAll();
        
        List<Seller> sellerList;
        switch (tab) {
            case "approved":
                sellerList = getSellerListWithSort(SellerStatus.APPROVED, sort);
                break;
            case "rejected":
                sellerList = getSellerListWithSort(SellerStatus.REJECTED, sort);
                break;
            case "unverified":
                sellerList = getSellerListWithSort(SellerStatus.UNVERIFIED, sort);
                break;
            case "all":
                sellerList = getAllSellersWithSort(sort);
                break;
            default:
                tab = "pending";
                sellerList = getSellerListWithSort(SellerStatus.PENDING, sort);
                break;
        }
        
        // Không cần sort ở đây nữa - đã sort trong service!
        
        setAttributes(request, seller, sellerList, tab, sort, pendingCount, approvedCount, rejectedCount, unverifiedCount, allCount);
        request.getRequestDispatcher("/admin/admin_approves_seller.jsp").forward(request, response);
    }

    /**
     * Lấy danh sách seller theo status với sort từ service
     */
    private List<Seller> getSellerListWithSort(SellerStatus status, String sort) {
        switch (sort) {
            case "oldest":
                return sellerDAO.getAllSellersByStatusSortOldest(status);
            case "name":
                return sellerDAO.getAllSellersByStatusSortByName(status);
            case "shop":
                return sellerDAO.getAllSellersByStatusSortByShop(status);
            default: // newest
                return sellerDAO.getAllSellersByStatusSortNewest(status);
        }
    }

    /**
     * Lấy tất cả seller với sort từ service
     */
    private List<Seller> getAllSellersWithSort(String sort) {
        switch (sort) {
            case "oldest":
                return sellerDAO.getAllSellersSortOldest();
            case "name":
                return sellerDAO.getAllSellersSortByName();
            case "shop":
                return sellerDAO.getAllSellersSortByShop();
            default: // newest
                return sellerDAO.getAllSellersSortNewest();
        }
    }

    private void approveSeller(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String sellerId = request.getParameter("sellerId");
        String shopName = request.getParameter("shopName");
        HttpSession session = request.getSession();
        Admin admin = (Admin) session.getAttribute("user");
        String adminId = admin.getUserId();
        
        // Lưu sellerId vào session để xem log sau
        if (sellerId != null) {
            Seller seller = sellerDAO.findById(sellerId);
            session.setAttribute("lastViewedUserId", sellerId);
            session.setAttribute("lastViewedUserName", seller != null ? seller.getFullName() : shopName);
            session.setAttribute("lastViewedUserType", "seller");
        }
        
        if (sellerId != null) {
            int result = sellerDAO.approveSeller(sellerId);
            if (result == 0) {
                UserLog log = new UserLog(sellerId, Role.SELLER, ActionType.SELLER_APPROVED,
                    "Seller \"" + shopName + "\" được duyệt bởi admin " + adminId, sellerId, "SELLER", adminId);
                userLogDAO.save(log);
                request.setAttribute("message", "Đã duyệt seller \"" + shopName + "\" thành công!");
            } else if (result == 2) {
                request.setAttribute("error", "Seller \"" + shopName + "\" đã được xử lý bởi admin khác.");
            } else {
                request.setAttribute("error", "Không thể duyệt seller. Vui lòng thử lại.");
            }
        } else {
            request.setAttribute("error", "Không thể duyệt seller. Vui lòng thử lại.");
        }
        loadFirstPendingSeller(request, response);
    }

    private void rejectSeller(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String sellerId = request.getParameter("sellerId");
        String shopName = request.getParameter("shopName");
        HttpSession session = request.getSession();
        Admin admin = (Admin) session.getAttribute("user");
        String adminId = admin.getUserId();
        
        // Lưu sellerId vào session để xem log sau
        if (sellerId != null) {
            Seller seller = sellerDAO.findById(sellerId);
            session.setAttribute("lastViewedUserId", sellerId);
            session.setAttribute("lastViewedUserName", seller != null ? seller.getFullName() : shopName);
            session.setAttribute("lastViewedUserType", "seller");
        }
        
        if (sellerId != null) {
            int result = sellerDAO.rejectSeller(sellerId);
            if (result == 0) {
                UserLog log = new UserLog(sellerId, Role.SELLER, ActionType.SELLER_REJECTED,
                    "Seller \"" + shopName + "\" bị từ chối bởi admin " + adminId, null, null, adminId);
                userLogDAO.save(log);
                request.setAttribute("message", "Đã từ chối seller \"" + shopName + "\".");
            } else if (result == 2) {
                request.setAttribute("error", "Seller \"" + shopName + "\" đã được xử lý bởi admin khác.");
            } else {
                request.setAttribute("error", "Không thể từ chối seller. Vui lòng thử lại.");
            }
        } else {
            request.setAttribute("error", "Không thể từ chối seller. Vui lòng thử lại.");
        }
        loadFirstPendingSeller(request, response);
    }

    private void setAttributes(HttpServletRequest request, Seller seller, List<Seller> sellerList,
            String tab, String sort, long pendingCount, long approvedCount, long rejectedCount, long unverifiedCount, long allCount) {
        request.setAttribute("seller", seller);
        request.setAttribute("sellerList", sellerList);
        request.setAttribute("currentTab", tab);
        request.setAttribute("currentSort", sort);
        request.setAttribute("pendingCount", pendingCount);
        request.setAttribute("approvedCount", approvedCount);
        request.setAttribute("rejectedCount", rejectedCount);
        request.setAttribute("unverifiedCount", unverifiedCount);
        request.setAttribute("allCount", allCount);
    }
}