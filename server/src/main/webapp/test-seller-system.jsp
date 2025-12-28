<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html>

    <head>
        <meta charset="UTF-8">
        <title>Test Seller & Product System</title>
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

            .btn.seller {
                background: #28a745;
            }

            .btn.admin {
                background: #dc3545;
            }

            .info {
                background: #e7f3ff;
                padding: 15px;
                border-radius: 4px;
                margin: 10px 0;
            }

            .debug {
                background: #f8f9fa;
                padding: 10px;
                border-radius: 4px;
                font-family: monospace;
                font-size: 12px;
            }
        </style>
    </head>

    <body>
        <h1>🧪 Test Seller & Product System</h1>

        <div class="info">
            <strong>Thông tin session hiện tại:</strong><br>
            User: ${sessionScope.user.fullName} (${sessionScope.user.userId})<br>
            Role: ${sessionScope.role}<br>
            Logged in: ${sessionScope.user != null ? 'Yes' : 'No'}
        </div>

        <div class="card">
            <h3>🏠 Main Navigation</h3>
            <a href="${pageContext.request.contextPath}/" class="btn">Home (Auto-redirect)</a>
            <a href="${pageContext.request.contextPath}/login" class="btn">Login</a>
            <a href="${pageContext.request.contextPath}/logout" class="btn">Logout</a>
        </div>

        <div class="card">
            <h3>🏪 Seller System</h3>
            <a href="${pageContext.request.contextPath}/seller/dashboard" class="btn seller">Seller Dashboard</a>
            <a href="${pageContext.request.contextPath}/seller/products" class="btn seller">Seller Products</a>
            <a href="${pageContext.request.contextPath}/test-product.jsp" class="btn seller">Test Product Creation</a>
        </div>

        <div class="card">
            <h3>🛡️ Admin System</h3>
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn admin">Admin Dashboard</a>
            <a href="${pageContext.request.contextPath}/admin/approveSeller" class="btn admin">Approve Sellers</a>
            <a href="${pageContext.request.contextPath}/admin/approveProduct" class="btn admin">Approve Products</a>
        </div>

        <div class="card">
            <h3>🔧 Debug & Test</h3>
            <div class="debug">
                <strong>Debug Info:</strong><br>
                Context Path: ${pageContext.request.contextPath}<br>
                Server Info: ${pageContext.servletContext.serverInfo}<br>
                Session ID: ${pageContext.session.id}<br>
                Request URI: ${pageContext.request.requestURI}<br>
                Current Time: <%= new java.util.Date() %>
            </div>
        </div>

        <div class="card">
            <h3>📋 Test Checklist</h3>
            <ul>
                <li>✅ Seller có thể đăng nhập và truy cập dashboard</li>
                <li>✅ Seller có thể tạo sản phẩm (nếu đã được approve)</li>
                <li>✅ Admin có thể duyệt seller</li>
                <li>✅ Admin có thể duyệt sản phẩm</li>
                <li>✅ Dashboard hiển thị thống kê đúng</li>
                <li>✅ Không có lỗi compile</li>
            </ul>
        </div>

        <script>
            console.log('Test page loaded successfully');
            console.log('User role:', '${sessionScope.role}');
            console.log('User ID:', '${sessionScope.user.userId}');
        </script>
    </body>

    </html>