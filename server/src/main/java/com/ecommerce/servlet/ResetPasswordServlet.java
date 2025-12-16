package com.ecommerce.servlet;

import com.ecommerce.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/reset-password")
public class ResetPasswordServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String token = req.getParameter("token");
        String password = req.getParameter("password");

        boolean success = authService.resetPassword(token, password);

        if (!success) {
            req.setAttribute("error", "Invalid or expired token");
            req.getRequestDispatcher("error.jsp").forward(req, resp);
            return;
        }

        req.setAttribute("message", "Password reset successful");
        req.getRequestDispatcher("login.jsp").forward(req, resp);
    }
}

