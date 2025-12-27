package com.mycompany.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.mycompany.service.UserDAO;
import com.mycompany.service.UserLogDAO;
import com.mycompany.entity.Seller;
import com.mycompany.entity.Buyer;
import com.mycompany.entity.Shipper;
import com.mycompany.entity.UserLog;
import com.mycompany.entity.ActionType;
import com.mycompany.entity.Role;

@WebServlet("/admin/manageUser")
public class AdminUserController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final int PAGE_SIZE = 6;
    
    private UserDAO userDAO = new UserDAO();
    private UserLogDAO userLogDAO = new UserLogDAO();

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
            case "viewLog":
                viewUserLog(request, response);
                break;
            case "ban":
                banUser(request, response);
                break;
            case "unban":
                unbanUser(request, response);
                break;
            case "search":
                loadUsers(request, response);
                break;
            default:
                loadUsers(request, response);
                break;
        }
    }


    private void loadUsers(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        String filter = request.getParameter("filter");
        if (filter == null) filter = "all";
        
        int page = 1;
        try {
            String pageStr = request.getParameter("page");
            if (pageStr != null) page = Integer.parseInt(pageStr);
            if (page < 1) page = 1;
        } catch (NumberFormatException e) {
            page = 1;
        }
        
        List<Seller> sellers = new ArrayList<>();
        List<Buyer> buyers = new ArrayList<>();
        List<Shipper> shippers = new ArrayList<>();
        long totalItems = 0;
        int totalPages = 1;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sellers = userDAO.searchSellers(keyword);
            buyers = userDAO.searchBuyers(keyword);
            shippers = userDAO.searchShippers(keyword);
            totalItems = sellers.size() + buyers.size() + shippers.size();
        } else if ("sellers".equals(filter)) {
            sellers = userDAO.getAllSellers(page, PAGE_SIZE);
            totalItems = userDAO.countUsers("sellers");
            totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
        } else if ("buyers".equals(filter)) {
            buyers = userDAO.getAllBuyers(page, PAGE_SIZE);
            totalItems = userDAO.countUsers("buyers");
            totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
        } else if ("shippers".equals(filter)) {
            shippers = userDAO.getAllShippers(page, PAGE_SIZE);
            totalItems = userDAO.countUsers("shippers");
            totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
        } else if ("banned".equals(filter)) {
            sellers = userDAO.getBannedSellers();
            buyers = userDAO.getBannedBuyers();
            shippers = userDAO.getBannedShippers();
            totalItems = sellers.size() + buyers.size() + shippers.size();
        } else {
            sellers = userDAO.getAllSellers();
            buyers = userDAO.getAllBuyers();
            shippers = userDAO.getAllShippers();
            totalItems = sellers.size() + buyers.size() + shippers.size();
        }
        
        request.setAttribute("sellers", sellers);
        request.setAttribute("buyers", buyers);
        request.setAttribute("shippers", shippers);
        request.setAttribute("keyword", keyword);
        request.setAttribute("filter", filter);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalItems", totalItems);
        request.setAttribute("activePage", "user");
        request.getRequestDispatcher("/admin_manages_user.jsp").forward(request, response);
    }

    private void viewUserLog(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String userId = request.getParameter("userId");
        String userName = request.getParameter("userName");
        String userType = request.getParameter("userType");
        
        List<UserLog> userLogs = new ArrayList<>();
        if (userId != null && !userId.trim().isEmpty()) {
            userLogs = userLogDAO.getLogsByUserId(userId, 50);
        }
        
        String keyword = request.getParameter("keyword");
        String filter = request.getParameter("filter");
        if (filter == null) filter = "all";
        
        int page = 1;
        try {
            String pageStr = request.getParameter("page");
            if (pageStr != null) page = Integer.parseInt(pageStr);
            if (page < 1) page = 1;
        } catch (NumberFormatException e) {
            page = 1;
        }
        
        List<Seller> sellers = new ArrayList<>();
        List<Buyer> buyers = new ArrayList<>();
        List<Shipper> shippers = new ArrayList<>();
        long totalItems = 0;
        int totalPages = 1;

        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sellers = userDAO.searchSellers(keyword);
            buyers = userDAO.searchBuyers(keyword);
            shippers = userDAO.searchShippers(keyword);
            totalItems = sellers.size() + buyers.size() + shippers.size();
        } else if ("sellers".equals(filter)) {
            sellers = userDAO.getAllSellers(page, PAGE_SIZE);
            totalItems = userDAO.countUsers("sellers");
            totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
        } else if ("buyers".equals(filter)) {
            buyers = userDAO.getAllBuyers(page, PAGE_SIZE);
            totalItems = userDAO.countUsers("buyers");
            totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
        } else if ("shippers".equals(filter)) {
            shippers = userDAO.getAllShippers(page, PAGE_SIZE);
            totalItems = userDAO.countUsers("shippers");
            totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
        } else if ("banned".equals(filter)) {
            sellers = userDAO.getBannedSellers();
            buyers = userDAO.getBannedBuyers();
            shippers = userDAO.getBannedShippers();
            totalItems = sellers.size() + buyers.size() + shippers.size();
        } else {
            sellers = userDAO.getAllSellers();
            buyers = userDAO.getAllBuyers();
            shippers = userDAO.getAllShippers();
            totalItems = sellers.size() + buyers.size() + shippers.size();
        }
        
        request.setAttribute("userLogs", userLogs);
        request.setAttribute("selectedUserId", userId);
        request.setAttribute("selectedUserName", userName);
        request.setAttribute("selectedUserType", userType);
        request.setAttribute("sellers", sellers);
        request.setAttribute("buyers", buyers);
        request.setAttribute("shippers", shippers);
        request.setAttribute("keyword", keyword);
        request.setAttribute("filter", filter);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalItems", totalItems);
        request.setAttribute("activePage", "user");
        request.getRequestDispatcher("/admin_manages_user.jsp").forward(request, response);
    }

    private void banUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String userId = request.getParameter("userId");
        String userName = request.getParameter("userName");
        String userType = request.getParameter("userType");
        HttpSession session = request.getSession();
        String adminId = (String) session.getAttribute("user");
        
        boolean success = false;
        switch (userType) {
            case "seller": success = userDAO.banSeller(userId); break;
            case "buyer": success = userDAO.banBuyer(userId); break;
            case "shipper": success = userDAO.banShipper(userId); break;
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


    private void unbanUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String userId = request.getParameter("userId");
        String userName = request.getParameter("userName");
        String userType = request.getParameter("userType");
        HttpSession session = request.getSession();
        String adminId = (String) session.getAttribute("user");
        
        boolean success = false;
        switch (userType) {
            case "seller": success = userDAO.unbanSeller(userId); break;
            case "buyer": success = userDAO.unbanBuyer(userId); break;
            case "shipper": success = userDAO.unbanShipper(userId); break;
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
