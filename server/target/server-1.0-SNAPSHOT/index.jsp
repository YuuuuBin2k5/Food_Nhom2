<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.ecommerce.util.DBUtil"%>

<%@page import="jakarta.persistence.EntityManager"%>

<!DOCTYPE html>
<html>
    <head>
        <title>Server Status Check</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <style>
            body {
                font-family: Arial, sans-serif;
                display: flex;
                justify-content: center;
                align-items: center;
                height: 100vh;
                margin: 0;
                background-color: #f4f4f9;
            }
            .status-card {
                background: white;
                padding: 40px;
                border-radius: 10px;
                box-shadow: 0 4px 15px rgba(0,0,0,0.1);
                text-align: center;
                max-width: 600px;
            }
            .status-icon {
                font-size: 50px;
                margin-bottom: 20px;
            }
            .success { color: #28a745; }
            .error { color: #dc3545; }
            .message { font-size: 24px; font-weight: bold; margin-bottom: 10px;}
            .detail { color: #666; font-size: 14px; word-break: break-word;}
            button {
                margin-top: 20px;
                padding: 10px 20px;
                cursor: pointer;
                background-color: #007bff;
                color: white;
                border: none;
                border-radius: 5px;
                font-size: 16px;
            }
            button:hover { background-color: #0056b3; }
        </style>
    </head>
    <body>

        <%
            // --- PHẦN CODE JAVA KIỂM TRA KẾT NỐI ---
            String statusIcon = "";
            String statusClass = "";
            String message = "";
            String detail = "";
            
            EntityManager em = null;
            
            try {
                // 1. Nếu trang này chạy được -> Server (Tomcat) ĐANG CHẠY.
                
                // 2. Thử tạo EntityManager để kiểm tra DB
                // DBUtil trả về Jakarta EntityManagerFactory, nên biến em phải là Jakarta EntityManager
                em = DBUtil.getEmFactory().createEntityManager();
                
                // Nếu dòng trên không lỗi -> Kết nối thành công
                statusIcon = "✅";
                statusClass = "success";
                message = "Hệ thống hoạt động bình thường!";
                detail = "Kết nối Database thành công via JPA EntityManager.";
                
            } catch (Exception e) {
                // Nếu có lỗi -> Kết nối thất bại
                statusIcon = "❌";
                statusClass = "error";
                message = "Lỗi kết nối Database!";
                detail = "Chi tiết lỗi: " + e.getMessage();
                e.printStackTrace(); // In lỗi ra console server để debug
            } finally {
                if (em != null) {
                    em.close();
                }
            }
        %>

        <div class="status-card">
            <div class="status-icon"><%= statusIcon %></div>
            
            <div class="message <%= statusClass %>">
                <%= message %>
            </div>
            
            <p class="detail">
                <%= detail %>
            </p>

            <div style="margin-top: 20px; border-top: 1px solid #eee; padding-top: 10px;">
                <small>Server Time: <%= new java.util.Date() %></small>
            </div>
            
            <button onclick="window.location.reload();">Kiểm tra lại</button>
        </div>

    </body>
</html>