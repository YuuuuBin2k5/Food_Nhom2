package com.mycompany.controller;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.mycompany.dao.SellerDAO;
import com.mycompany.dao.ProductDAO;
import com.mycompany.dao.UserDAO;
import com.mycompany.model.Seller;
import com.mycompany.model.Buyer;
import com.mycompany.model.Shipper;
import com.mycompany.model.Product;

@WebServlet("/admin")
public class AdminController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private SellerDAO sellerDAO = new SellerDAO();
    private ProductDAO productDAO = new ProductDAO();
    private UserDAO userDAO = new UserDAO();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        
        if (action == null) {
            action = "viewLogin"; 
        }

        switch (action) {
            // --- ĐĂNG NHẬP / XUẤT ---
            case "viewLogin":
                request.getRequestDispatcher("index.jsp").forward(request, response);
                break;

            case "login":
                checkLogin(request, response);
                break;
                
            case "logout":
                processLogout(request, response);
                break;
                
            // --- MENU DASHBOARD ---
            case "viewLog":
            case "viewRefund":
                request.getRequestDispatcher("admin-dashboard.jsp").forward(request, response);
                break;
            
            // --- QUẢN LÝ BAN USER ---
            case "viewBanUser":
            case "searchUser":
                loadBanUserPage(request, response);
                break;
                
            case "banSeller":
                banSeller(request, response);
                break;
                
            case "unbanSeller":
                unbanSeller(request, response);
                break;
                
            case "banBuyer":
                banBuyer(request, response);
                break;
                
            case "unbanBuyer":
                unbanBuyer(request, response);
                break;
                
            case "banShipper":
                banShipper(request, response);
                break;
                
            case "unbanShipper":
                unbanShipper(request, response);
                break;
            
            // --- QUẢN LÝ PRODUCT ---
            case "viewProduct":
                loadPendingProducts(request, response);
                break;
                
            case "approveProduct":
                approveProduct(request, response);
                break;
                
            case "rejectProduct":
                rejectProduct(request, response);
                break;
            
            // --- QUẢN LÝ SELLER ---
            case "viewSeller":
                loadPendingSellers(request, response);
                break;
                
            case "approveSeller":
                approveSeller(request, response);
                break;
                
            case "rejectSeller":
                rejectSeller(request, response);
                break;
                
            default:
                request.getRequestDispatcher("index.jsp").forward(request, response);
                break;
        }
    }

    private void checkLogin(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String u = request.getParameter("username");
        String p = request.getParameter("password");

        if ("admin".equals(u) && "123".equals(p)) {
            HttpSession session = request.getSession();
            session.setAttribute("user", u);
            response.sendRedirect("admin?action=viewLog");
        } else {
            request.setAttribute("message", "Sai tên đăng nhập hoặc mật khẩu!");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }

    private void processLogout(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.invalidate();
        response.sendRedirect("index.jsp");
    }

    // --- SELLER MANAGEMENT ---
    
    private void loadPendingSellers(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Lấy seller đầu tiên cần duyệt (FIFO)
        Seller seller = sellerDAO.getFirstPendingSeller();
        long pendingCount = sellerDAO.countPendingSellers();
        
        request.setAttribute("seller", seller);
        request.setAttribute("pendingCount", pendingCount);
        request.setAttribute("currentAction", "viewSeller");
        request.getRequestDispatcher("admin-dashboard.jsp").forward(request, response);
    }

    private void approveSeller(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String sellerId = request.getParameter("sellerId");
        String shopName = request.getParameter("shopName");
        
        if (sellerId != null && sellerDAO.approveSeller(sellerId)) {
            request.setAttribute("message", "Đã duyệt seller \"" + shopName + "\" thành công!");
        } else {
            request.setAttribute("error", "Không thể duyệt seller. Vui lòng thử lại.");
        }
        loadPendingSellers(request, response);
    }

    private void rejectSeller(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String sellerId = request.getParameter("sellerId");
        String shopName = request.getParameter("shopName");
        
        if (sellerId != null && sellerDAO.rejectSeller(sellerId)) {
            request.setAttribute("message", "Đã từ chối seller \"" + shopName + "\".");
        } else {
            request.setAttribute("error", "Không thể từ chối seller. Vui lòng thử lại.");
        }
        loadPendingSellers(request, response);
    }

    // --- PRODUCT MANAGEMENT ---
    
    private void loadPendingProducts(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        Product product = productDAO.getFirstPendingProduct();
        long pendingCount = productDAO.countPendingProducts();
        
        request.setAttribute("product", product);
        request.setAttribute("pendingCount", pendingCount);
        request.setAttribute("currentAction", "viewProduct");
        request.getRequestDispatcher("admin-dashboard.jsp").forward(request, response);
    }

    private void approveProduct(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String productIdStr = request.getParameter("productId");
        String productName = request.getParameter("productName");
        
        try {
            Long productId = Long.parseLong(productIdStr);
            if (productDAO.approveProduct(productId)) {
                request.setAttribute("message", "Đã duyệt sản phẩm \"" + productName + "\" thành công!");
            } else {
                request.setAttribute("error", "Không thể duyệt sản phẩm. Vui lòng thử lại.");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID sản phẩm không hợp lệ.");
        }
        loadPendingProducts(request, response);
    }

    private void rejectProduct(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String productIdStr = request.getParameter("productId");
        String productName = request.getParameter("productName");
        
        try {
            Long productId = Long.parseLong(productIdStr);
            if (productDAO.rejectProduct(productId)) {
                request.setAttribute("message", "Đã từ chối sản phẩm \"" + productName + "\".");
            } else {
                request.setAttribute("error", "Không thể từ chối sản phẩm. Vui lòng thử lại.");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID sản phẩm không hợp lệ.");
        }
        loadPendingProducts(request, response);
    }

    // --- BAN USER MANAGEMENT ---
    
    private void loadBanUserPage(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        String filter = request.getParameter("filter"); // all, sellers, buyers, banned
        
        if (filter == null) filter = "all";
        
        List<Seller> sellers = new java.util.ArrayList<>();
        List<Buyer> buyers = new java.util.ArrayList<>();
        List<Shipper> shippers = new java.util.ArrayList<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sellers = userDAO.searchSellers(keyword);
            buyers = userDAO.searchBuyers(keyword);
            shippers = userDAO.searchShippers(keyword);
        } else if ("banned".equals(filter)) {
            sellers = userDAO.getBannedSellers();
            buyers = userDAO.getBannedBuyers();
            shippers = userDAO.getBannedShippers();
        } else if ("sellers".equals(filter)) {
            sellers = userDAO.getAllSellers();
        } else if ("buyers".equals(filter)) {
            buyers = userDAO.getAllBuyers();
        } else if ("shippers".equals(filter)) {
            shippers = userDAO.getAllShippers();
        } else {
            sellers = userDAO.getAllSellers();
            buyers = userDAO.getAllBuyers();
            shippers = userDAO.getAllShippers();
        }
        
        request.setAttribute("sellers", sellers);
        request.setAttribute("buyers", buyers);
        request.setAttribute("shippers", shippers);
        request.setAttribute("keyword", keyword);
        request.setAttribute("filter", filter);
        request.setAttribute("currentAction", "viewBanUser");
        request.getRequestDispatcher("admin-dashboard.jsp").forward(request, response);
    }

    private void banSeller(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String sellerId = request.getParameter("userId");
        String userName = request.getParameter("userName");
        
        if (sellerId != null && userDAO.banSeller(sellerId)) {
            request.setAttribute("message", "Đã ban seller \"" + userName + "\".");
        } else {
            request.setAttribute("error", "Không thể ban seller. Vui lòng thử lại.");
        }
        loadBanUserPage(request, response);
    }

    private void unbanSeller(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String sellerId = request.getParameter("userId");
        String userName = request.getParameter("userName");
        
        if (sellerId != null && userDAO.unbanSeller(sellerId)) {
            request.setAttribute("message", "Đã unban seller \"" + userName + "\".");
        } else {
            request.setAttribute("error", "Không thể unban seller. Vui lòng thử lại.");
        }
        loadBanUserPage(request, response);
    }

    private void banBuyer(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String visitorId = request.getParameter("userId");
        String userName = request.getParameter("userName");
        
        if (visitorId != null && userDAO.banBuyer(visitorId)) {
            request.setAttribute("message", "Đã ban buyer \"" + userName + "\".");
        } else {
            request.setAttribute("error", "Không thể ban buyer. Vui lòng thử lại.");
        }
        loadBanUserPage(request, response);
    }

    private void unbanBuyer(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String visitorId = request.getParameter("userId");
        String userName = request.getParameter("userName");
        
        if (visitorId != null && userDAO.unbanBuyer(visitorId)) {
            request.setAttribute("message", "Đã unban buyer \"" + userName + "\".");
        } else {
            request.setAttribute("error", "Không thể unban buyer. Vui lòng thử lại.");
        }
        loadBanUserPage(request, response);
    }

    private void banShipper(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String visitorId = request.getParameter("userId");
        String userName = request.getParameter("userName");
        
        if (visitorId != null && userDAO.banShipper(visitorId)) {
            request.setAttribute("message", "Đã ban shipper \"" + userName + "\".");
        } else {
            request.setAttribute("error", "Không thể ban shipper. Vui lòng thử lại.");
        }
        loadBanUserPage(request, response);
    }

    private void unbanShipper(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String visitorId = request.getParameter("userId");
        String userName = request.getParameter("userName");
        
        if (visitorId != null && userDAO.unbanShipper(visitorId)) {
            request.setAttribute("message", "Đã unban shipper \"" + userName + "\".");
        } else {
            request.setAttribute("error", "Không thể unban shipper. Vui lòng thử lại.");
        }
        loadBanUserPage(request, response);
    }

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
}
