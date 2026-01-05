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
import com.ecommerce.service.AdminUserService;
import com.ecommerce.service.UserLogService;
import com.ecommerce.util.MenuHelper;
import com.ecommerce.entity.Admin;
import com.ecommerce.entity.Seller;
import com.ecommerce.entity.Buyer;
import com.ecommerce.entity.Shipper;
import com.ecommerce.entity.UserLog;
import com.ecommerce.entity.ActionType;
import com.ecommerce.entity.Role;

@WebServlet("/admin/manageUser")
public class AdminUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private AdminUserService userDAO = new AdminUserService();
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
        if ("viewLog".equals(action)) {
            viewUserLog(request, response);
        } else {
            loadUsers(request, response);
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
        if ("ban".equals(action)) {
            banUser(request, response);
        } else if ("unban".equals(action)) {
            unbanUser(request, response);
        } else {
            loadUsers(request, response);
        }
    }

    /**
     * Load danh sách users với filter và sort
     */
    private void loadUsers(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        MenuHelper.setMenuItems(request, "ADMIN", "/admin/manageUser");
        HttpSession session = request.getSession();
        
        String keyword = request.getParameter("keyword");
        String filter = request.getParameter("filter");
        String sort = request.getParameter("sort");
        
        // Đọc từ session nếu không có param
        if (filter == null) {
            filter = (String) session.getAttribute("adminUserFilter");
        }
        if (sort == null) {
            sort = (String) session.getAttribute("adminUserSort");
        }
        if (filter == null) filter = "all";
        if (sort == null) sort = "name";
        
        // Lưu vào session
        session.setAttribute("adminUserFilter", filter);
        session.setAttribute("adminUserSort", sort);
        
        // Đọc lastViewedUser từ session nếu có
        String lastUserId = (String) session.getAttribute("lastViewedUserId");
        String lastUserName = (String) session.getAttribute("lastViewedUserName");
        String lastUserType = (String) session.getAttribute("lastViewedUserType");
        
        List<UserLog> userLogs = new ArrayList<>();
        if (lastUserId != null && !lastUserId.trim().isEmpty()) {
            userLogs = userLogDAO.getLogsByUserId(lastUserId, 50);
            request.setAttribute("selectedUserId", lastUserId);
            request.setAttribute("selectedUserName", lastUserName);
            request.setAttribute("selectedUserType", lastUserType);
            request.setAttribute("userLogs", userLogs);
        }
        
        // Load users theo filter và sort
        List<Seller> sellers = new ArrayList<>();
        List<Buyer> buyers = new ArrayList<>();
        List<Shipper> shippers = new ArrayList<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            // Search mode
            sellers = userDAO.searchSellers(keyword);
            buyers = userDAO.searchBuyers(keyword);
            shippers = userDAO.searchShippers(keyword);
        } else {
            // Filter mode với sort
            switch (filter) {
                case "sellers":
                    sellers = getSellerListWithSort(sort);
                    break;
                case "buyers":
                    buyers = getBuyerListWithSort(sort);
                    break;
                case "shippers":
                    shippers = getShipperListWithSort(sort);
                    break;
                case "banned":
                    sellers = userDAO.getBannedSellers();
                    buyers = userDAO.getBannedBuyers();
                    shippers = userDAO.getBannedShippers();
                    break;
                default: // all
                    sellers = getSellerListWithSort(sort);
                    buyers = getBuyerListWithSort(sort);
                    shippers = getShipperListWithSort(sort);
                    break;
            }
        }
        
        request.setAttribute("sellers", sellers);
        request.setAttribute("buyers", buyers);
        request.setAttribute("shippers", shippers);
        request.setAttribute("keyword", keyword);
        request.setAttribute("filter", filter);
        request.setAttribute("sort", sort);
        request.getRequestDispatcher("/admin/admin_manages_user.jsp").forward(request, response);
    }
    
    /**
     * Lấy danh sách seller với sort từ service
     */
    private List<Seller> getSellerListWithSort(String sort) {
        switch (sort) {
            case "email":
                return userDAO.getAllSellersSortByEmail();
            case "shop":
                return userDAO.getAllSellersSortByShop();
            default: // name
                return userDAO.getAllSellers();
        }
    }

    /**
     * Lấy danh sách buyer với sort từ service
     */
    private List<Buyer> getBuyerListWithSort(String sort) {
        switch (sort) {
            case "email":
                return userDAO.getAllBuyersSortByEmail();
            default: // name
                return userDAO.getAllBuyers();
        }
    }

    /**
     * Lấy danh sách shipper với sort từ service
     */
    private List<Shipper> getShipperListWithSort(String sort) {
        switch (sort) {
            case "email":
                return userDAO.getAllShippersSortByEmail();
            default: // name
                return userDAO.getAllShippers();
        }
    }

    /**
     * Xem log của user được chọn
     */
    private void viewUserLog(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        MenuHelper.setMenuItems(request, "ADMIN", "/admin/manageUser");
        HttpSession session = request.getSession();
        
        String userId = request.getParameter("userId");
        String userName = request.getParameter("userName");
        String userType = request.getParameter("userType");
        String filter = request.getParameter("filter");
        
        // Đọc từ session nếu không có param
        if (filter == null) {
            filter = (String) session.getAttribute("adminUserFilter");
        }
        if (filter == null) filter = "all";
        
        // Lưu vào session
        session.setAttribute("adminUserFilter", filter);
        
        List<UserLog> userLogs = new ArrayList<>();
        if (userId != null && !userId.trim().isEmpty()) {
            userLogs = userLogDAO.getLogsByUserId(userId, 50);
        }
        
        List<Seller> sellers = new ArrayList<>();
        List<Buyer> buyers = new ArrayList<>();
        List<Shipper> shippers = new ArrayList<>();
        
        switch (filter) {
            case "sellers":
                sellers = userDAO.getAllSellers();
                break;
            case "buyers":
                buyers = userDAO.getAllBuyers();
                break;
            case "shippers":
                shippers = userDAO.getAllShippers();
                break;
            case "banned":
                sellers = userDAO.getBannedSellers();
                buyers = userDAO.getBannedBuyers();
                shippers = userDAO.getBannedShippers();
                break;
            default: // all
                sellers = userDAO.getAllSellers();
                buyers = userDAO.getAllBuyers();
                shippers = userDAO.getAllShippers();
                break;
        }
        
        request.setAttribute("userLogs", userLogs);
        request.setAttribute("selectedUserId", userId);
        request.setAttribute("selectedUserName", userName);
        request.setAttribute("selectedUserType", userType);
        request.setAttribute("sellers", sellers);
        request.setAttribute("buyers", buyers);
        request.setAttribute("shippers", shippers);
        request.setAttribute("filter", filter);
        request.getRequestDispatcher("/admin/admin_manages_user.jsp").forward(request, response);
    }

    /**
     * Ban user
     */
    private void banUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String userId = request.getParameter("userId");
        String userName = request.getParameter("userName");
        String userType = request.getParameter("userType");
        HttpSession session = request.getSession();
        Admin admin = (Admin) session.getAttribute("user");
        String adminId = admin.getUserId();
        
        boolean success = false;
        if ("seller".equals(userType)) {
            success = userDAO.banSeller(userId);
        } else if ("buyer".equals(userType)) {
            success = userDAO.banBuyer(userId);
        } else if ("shipper".equals(userType)) {
            success = userDAO.banShipper(userId);
        }
        
        if (success) {
            Role userRole = "seller".equals(userType) ? Role.SELLER 
                          : "buyer".equals(userType) ? Role.BUYER : Role.SHIPPER;
            ActionType actionType = "seller".equals(userType) ? ActionType.SELLER_BANNED 
                                  : "buyer".equals(userType) ? ActionType.BUYER_BANNED 
                                  : ActionType.SHIPPER_BANNED;
            
            UserLog log = new UserLog(userId, userRole, actionType,
                userType.substring(0, 1).toUpperCase() + userType.substring(1) + " \"" + userName + "\" bị ban bởi admin " + adminId,
                null, null, adminId);
            userLogDAO.save(log);
            request.setAttribute("message", "Đã ban " + userType + " \"" + userName + "\".");
        } else {
            request.setAttribute("error", "Không thể ban " + userType + ". Vui lòng thử lại.");
        }
        loadUsers(request, response);
    }

    /**
     * Unban user
     */
    private void unbanUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String userId = request.getParameter("userId");
        String userName = request.getParameter("userName");
        String userType = request.getParameter("userType");
        HttpSession session = request.getSession();
        Admin admin = (Admin) session.getAttribute("user");
        String adminId = admin.getUserId();
        
        boolean success = false;
        if ("seller".equals(userType)) {
            success = userDAO.unbanSeller(userId);
        } else if ("buyer".equals(userType)) {
            success = userDAO.unbanBuyer(userId);
        } else if ("shipper".equals(userType)) {
            success = userDAO.unbanShipper(userId);
        }
        
        if (success) {
            Role userRole = "seller".equals(userType) ? Role.SELLER 
                          : "buyer".equals(userType) ? Role.BUYER : Role.SHIPPER;
            ActionType actionType = "seller".equals(userType) ? ActionType.SELLER_UNBANNED 
                                  : "buyer".equals(userType) ? ActionType.BUYER_UNBANNED 
                                  : ActionType.SHIPPER_UNBANNED;
            
            UserLog log = new UserLog(userId, userRole, actionType,
                userType.substring(0, 1).toUpperCase() + userType.substring(1) + " \"" + userName + "\" được unban bởi admin " + adminId,
                null, null, adminId);
            userLogDAO.save(log);
            request.setAttribute("message", "Đã unban " + userType + " \"" + userName + "\".");
        } else {
            request.setAttribute("error", "Không thể unban " + userType + ". Vui lòng thử lại.");
        }
        loadUsers(request, response);
    }
}