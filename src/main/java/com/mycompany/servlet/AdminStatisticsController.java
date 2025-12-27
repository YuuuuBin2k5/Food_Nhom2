package com.mycompany.servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.mycompany.service.AdminDAO;

@WebServlet("/admin/statistics")
public class AdminStatisticsController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AdminDAO adminDAO = new AdminDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        // Kiểm tra đăng nhập
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }
        
        loadStatistics(request, response);
    }

    private void loadStatistics(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        long totalBuyers = adminDAO.countBuyers();
        long totalSellers = adminDAO.countSellers();
        long totalShippers = adminDAO.countShippers();
        long totalOrders = adminDAO.countOrders();
        long totalProducts = adminDAO.countProducts();
        
        request.setAttribute("totalBuyers", totalBuyers);
        request.setAttribute("totalSellers", totalSellers);
        request.setAttribute("totalShippers", totalShippers);
        request.setAttribute("totalOrders", totalOrders);
        request.setAttribute("totalProducts", totalProducts);
        request.setAttribute("activePage", "statistics");
        request.getRequestDispatcher("/admin_statistics.jsp").forward(request, response);
    }
}
