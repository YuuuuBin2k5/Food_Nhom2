<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html>

    <head>
        <meta charset="UTF-8">
        <title>Test Dashboard Links</title>
        <style>
            body {
                font-family: Arial, sans-serif;
                margin: 40px;
            }

            .card {
                background: white;
                border: 1px solid #ddd;
                border-radius: 8px;
                padding: 20px;
                margin: 20px 0;
            }

            .btn {
                display: inline-block;
                padding: 10px 20px;
                background: #007bff;
                color: white;
                text-decoration: none;
                border-radius: 4px;
                margin: 5px;
            }

            .btn:hover {
                background: #0056b3;
            }

            .info {
                background: #e7f3ff;
                padding: 15px;
                border-radius: 4px;
                margin: 10px 0;
            }
        </style>
    </head>

    <body>
        <h1>🧪 Test Dashboard Links</h1>

        <div class="info">
            <strong>Thông tin session hiện tại:</strong><br>
            User: ${sessionScope.user.fullName} (${sessionScope.user.userId})<br>
            Role: ${sessionScope.role}<br>
            Logged in: ${sessionScope.user != null ? 'Yes' : 'No'}
        </div>

        <div class="card">
            <h3>🏠 Home & Dashboard Links</h3>
            <a href="${pageContext.request.contextPath}/" class="btn">Home (Auto-redirect)</a>
            <a href="${pageContext.request.contextPath}/buyer/dashboard" class="btn">Buyer Dashboard</a>
            <a href="${pageContext.request.contextPath}/seller/dashboard" class="btn">Seller Dashboard</a>
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn">Admin Dashboard</a>
        </div>

        <div class="card">
            <h3>🛍️ Buyer Pages</h3>
            <a href="${pageContext.request.contextPath}/products" class="btn">Products</a>
            <a href="${pageContext.request.contextPath}/cart" class="btn">Cart</a>
            <a href="${pageContext.request.contextPath}/orders" class="btn">Orders</a>
        </div>

        <div class="card">
            <h3>🏪 Seller Pages</h3>
            <a href="${pageContext.request.contextPath}/seller/products" class="btn">Seller Products</a>
            <a href="${pageContext.request.contextPath}/seller/orders" class="btn">Seller Orders</a>
            <a href="${pageContext.request.contextPath}/seller/settings" class="btn">Seller Settings</a>
        </div>

        <div class="card">
            <h3>🛡️ Admin Pages</h3>
            <a href="${pageContext.request.contextPath}/admin/approveSeller" class="btn">Approve Sellers</a>
            <a href="${pageContext.request.contextPath}/admin/approveProduct" class="btn">Approve Products</a>
            <a href="${pageContext.request.contextPath}/admin/statistics" class="btn">Statistics</a>
        </div>

        <div class="card">
            <h3>🔧 Test & Debug</h3>
            <a href="${pageContext.request.contextPath}/test-product.jsp" class="btn">Test Product Creation</a>
            <a href="${pageContext.request.contextPath}/test/product" class="btn">Test Product Servlet</a>
        </div>

        <div class="card">
            <h3>🔐 Auth</h3>
            <a href="${pageContext.request.contextPath}/login" class="btn">Login</a>
            <a href="${pageContext.request.contextPath}/register" class="btn">Register</a>
            <a href="${pageContext.request.contextPath}/logout" class="btn">Logout</a>
        </div>

        <script>
            // Auto-refresh session info every 5 seconds
            setTimeout(() => {
                location.reload();
            }, 10000);
        </script>
    </body>

    </html>