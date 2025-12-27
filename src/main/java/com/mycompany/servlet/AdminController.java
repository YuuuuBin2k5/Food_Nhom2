package com.mycompany.servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin")
public class AdminController extends HttpServlet {
    private static final long serialVersionUID = 1L;

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
        String action = request.getParameter("action");
        
        if (action == null) {
            action = "viewLogin";
        }

        switch (action) {
            case "login":
                checkLogin(request, response);
                break;
            case "logout":
                processLogout(request, response);
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
            response.sendRedirect(request.getContextPath() + "/admin/statistics");
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
}
