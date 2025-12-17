package com.ecommerce.servlet;

import com.ecommerce.entity.Seller;
import com.ecommerce.entity.SellerStatus;
import com.ecommerce.util.DBUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "DebugServlet", urlPatterns = {"/api/debug/sellers"})
public class DebugServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setAccessControlHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try (PrintWriter out = response.getWriter()) {
            List<Seller> allSellers = em.createQuery("SELECT s FROM Seller s", Seller.class).getResultList();
            
            JsonObject result = new JsonObject();
            result.addProperty("totalSellers", allSellers.size());
            
            JsonArray sellersArray = new JsonArray();
            for (Seller s : allSellers) {
                JsonObject sellerJson = new JsonObject();
                sellerJson.addProperty("userId", s.getUserId());
                sellerJson.addProperty("shopName", s.getShopName());
                sellerJson.addProperty("email", s.getEmail());
                sellerJson.addProperty("verificationStatus", s.getVerificationStatus() != null ? s.getVerificationStatus().toString() : "NULL");
                sellerJson.addProperty("businessLicenseUrl", s.getBusinessLicenseUrl());
                sellerJson.addProperty("licenseSubmittedDate", s.getLicenseSubmittedDate() != null ? s.getLicenseSubmittedDate().toString() : "NULL");
                sellersArray.add(sellerJson);
            }
            
            result.add("sellers", sellersArray);
            
            // Count by status
            JsonObject statusCount = new JsonObject();
            for (SellerStatus status : SellerStatus.values()) {
                long count = allSellers.stream()
                    .filter(s -> s.getVerificationStatus() == status)
                    .count();
                statusCount.addProperty(status.toString(), count);
            }
            long nullCount = allSellers.stream()
                .filter(s -> s.getVerificationStatus() == null)
                .count();
            statusCount.addProperty("NULL", nullCount);
            
            result.add("statusCount", statusCount);
            
            out.print(gson.toJson(result));
        } finally {
            em.close();
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setAccessControlHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void setAccessControlHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        resp.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
    }
}
