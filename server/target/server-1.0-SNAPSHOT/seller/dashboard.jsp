<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Tổng quan - Seller Dashboard</title>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/seller_style.css">
                <style>
                    .dashboard-card {
                        background: white;
                        padding: 1.5rem;
                        border-radius: 0.5rem;
                        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
                        border: 1px solid #e2e8f0;
                        margin-bottom: 1rem;
                    }

                    .stat-grid {
                        display: grid;
                        grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
                        gap: 1.5rem;
                        margin-bottom: 2rem;
                    }

                    .stat-card {
                        background: white;
                        padding: 1.5rem;
                        border-radius: 0.5rem;
                        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
                        border: 1px solid #e2e8f0;
                        text-align: center;
                    }

                    .stat-number {
                        font-size: 2rem;
                        font-weight: bold;
                        color: #2d3748;
                    }

                    .stat-label {
                        color: #718096;
                        font-size: 0.875rem;
                        margin-top: 0.5rem;
                    }

                    .debug-info {
                        background: #f7fafc;
                        border: 1px solid #e2e8f0;
                        padding: 1rem;
                        border-radius: 0.375rem;
                        margin-bottom: 1rem;
                        font-family: monospace;
                        font-size: 0.875rem;
                    }
                </style>
            </head>

            <body class="bg-white">

                <jsp:include page="../common/sidebar.jsp">
                    <jsp:param name="currentPath" value="/seller/dashboard" />
                </jsp:include>

                <main
                    style="margin-top: 96px; min-height: 80vh; padding: 2rem; max-width: 1400px; margin-left: auto; margin-right: auto;">

                    <div class="dashboard-header" style="margin-bottom: 2rem;">
                        <h2 style="font-size: 1.8rem; color: #1a202c; font-weight: 700;">👋 Chào mừng,
                            ${sessionScope.user.fullName}</h2>
                        <p style="color: #718096;">Đây là tình hình kinh doanh hôm nay của bạn.</p>
                    </div>

                    <!-- Error Message -->
                    <c:if test="${not empty error}">
                        <div
                            style="background-color: #fed7d7; border-left: 4px solid #e53e3e; padding: 1rem; margin-bottom: 2rem; border-radius: 4px;">
                            <h3 style="color: #c53030; margin: 0 0 0.5rem 0; font-size: 1.1rem;">⚠️ Lỗi tải dữ liệu</h3>
                            <p style="margin: 0; color: #742a2a;">${error}</p>
                        </div>
                    </c:if>

                    <!-- Debug Info -->
                    <div class="debug-info">
                        <strong>Debug Info:</strong><br>
                        User ID: ${sessionScope.user.userId}<br>
                        User Name: ${sessionScope.user.fullName}<br>
                        Role: ${sessionScope.role}<br>
                        Total Products: ${totalProducts}<br>
                        Active Products: ${activeProducts}<br>
                        Recent Products Count: ${recentProducts.size()}<br>
                        Recent Orders Count: ${recentOrders.size()}
                    </div>

                    <!-- Stats Grid -->
                    <div class="stat-grid">
                        <div class="stat-card">
                            <div class="stat-number">${totalProducts}</div>
                            <div class="stat-label">Tổng sản phẩm</div>
                        </div>

                        <div class="stat-card">
                            <div class="stat-number">${activeProducts}</div>
                            <div class="stat-label">Đang bán</div>
                        </div>

                        <div class="stat-card">
                            <div class="stat-number">
                                <fmt:formatNumber value="${totalRevenue}" type="currency" currencySymbol="₫"
                                    maxFractionDigits="0" />
                            </div>
                            <div class="stat-label">Tổng doanh thu</div>
                        </div>
                    </div>

                    <!-- Warning for expiring products -->
                    <c:if test="${expiringSoon > 0}">
                        <div
                            style="background-color: #fffaf0; border-left: 4px solid #ed8936; padding: 1rem; margin-bottom: 2rem; border-radius: 4px;">
                            <h3 style="color: #c05621; margin: 0 0 0.5rem 0; font-size: 1.1rem;">⚠️ Cảnh báo: Có
                                ${expiringSoon} sản phẩm sắp hết hạn!</h3>
                            <p style="margin: 0; color: #744210;">Hãy kiểm tra và xử lý các sản phẩm sắp hết hạn.</p>
                        </div>
                    </c:if>

                    <!-- Recent Products -->
                    <div class="dashboard-card">
                        <h3 style="margin-top: 0; color: #2d3748;">📦 Sản phẩm gần đây</h3>
                        <c:choose>
                            <c:when test="${not empty recentProducts}">
                                <div style="overflow-x: auto;">
                                    <table style="width: 100%; border-collapse: collapse;">
                                        <thead>
                                            <tr style="background-color: #f8f9fa;">
                                                <th
                                                    style="padding: 0.75rem; text-align: left; border-bottom: 1px solid #e2e8f0;">
                                                    Tên sản phẩm</th>
                                                <th
                                                    style="padding: 0.75rem; text-align: left; border-bottom: 1px solid #e2e8f0;">
                                                    Giá</th>
                                                <th
                                                    style="padding: 0.75rem; text-align: left; border-bottom: 1px solid #e2e8f0;">
                                                    Số lượng</th>
                                                <th
                                                    style="padding: 0.75rem; text-align: left; border-bottom: 1px solid #e2e8f0;">
                                                    Trạng thái</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="p" items="${recentProducts}">
                                                <tr>
                                                    <td style="padding: 0.75rem; border-bottom: 1px solid #edf2f7;">
                                                        ${p.name}</td>
                                                    <td style="padding: 0.75rem; border-bottom: 1px solid #edf2f7;">
                                                        <fmt:formatNumber value="${p.salePrice}" type="currency"
                                                            currencySymbol="₫" maxFractionDigits="0" />
                                                    </td>
                                                    <td style="padding: 0.75rem; border-bottom: 1px solid #edf2f7;">
                                                        ${p.quantity}</td>
                                                    <td style="padding: 0.75rem; border-bottom: 1px solid #edf2f7;">
                                                        <span style="padding: 0.25rem 0.5rem; border-radius: 0.25rem; font-size: 0.75rem; 
                                                background: ${p.status == 'ACTIVE' ? '#d4edda' : '#f8d7da'}; 
                                                color: ${p.status == 'ACTIVE' ? '#155724' : '#721c24'};">
                                                            ${p.status}
                                                        </span>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                                <div style="margin-top: 1rem; text-align: center;">
                                    <a href="${pageContext.request.contextPath}/seller/products"
                                        style="color: #3182ce; text-decoration: none; font-weight: 500;">
                                        Xem tất cả sản phẩm →
                                    </a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div style="text-align: center; padding: 2rem; color: #a0aec0;">
                                    <p>Chưa có sản phẩm nào. <a
                                            href="${pageContext.request.contextPath}/seller/products"
                                            style="color: #3182ce;">Đăng sản phẩm đầu tiên</a></p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Recent Orders -->
                    <div class="dashboard-card">
                        <h3 style="margin-top: 0; color: #2d3748;">📋 Đơn hàng gần đây</h3>
                        <c:choose>
                            <c:when test="${not empty recentOrders}">
                                <div style="overflow-x: auto;">
                                    <table style="width: 100%; border-collapse: collapse;">
                                        <thead>
                                            <tr style="background-color: #f8f9fa;">
                                                <th
                                                    style="padding: 0.75rem; text-align: left; border-bottom: 1px solid #e2e8f0;">
                                                    Mã đơn</th>
                                                <th
                                                    style="padding: 0.75rem; text-align: left; border-bottom: 1px solid #e2e8f0;">
                                                    Khách hàng</th>
                                                <th
                                                    style="padding: 0.75rem; text-align: left; border-bottom: 1px solid #e2e8f0;">
                                                    Tổng tiền</th>
                                                <th
                                                    style="padding: 0.75rem; text-align: left; border-bottom: 1px solid #e2e8f0;">
                                                    Trạng thái</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="o" items="${recentOrders}">
                                                <tr>
                                                    <td style="padding: 0.75rem; border-bottom: 1px solid #edf2f7;">
                                                        #${o.orderId}</td>
                                                    <td style="padding: 0.75rem; border-bottom: 1px solid #edf2f7;">
                                                        ${o.buyer.fullName}</td>
                                                    <td style="padding: 0.75rem; border-bottom: 1px solid #edf2f7;">
                                                        <fmt:formatNumber value="${o.payment.amount}" type="currency"
                                                            currencySymbol="₫" maxFractionDigits="0" />
                                                    </td>
                                                    <td style="padding: 0.75rem; border-bottom: 1px solid #edf2f7;">
                                                        <span style="padding: 0.25rem 0.5rem; border-radius: 0.25rem; font-size: 0.75rem; 
                                                background: ${o.status == 'DELIVERED' ? '#d4edda' : '#fff3cd'}; 
                                                color: ${o.status == 'DELIVERED' ? '#155724' : '#856404'};">
                                                            ${o.status}
                                                        </span>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                                <div style="margin-top: 1rem; text-align: center;">
                                    <a href="${pageContext.request.contextPath}/seller/orders"
                                        style="color: #3182ce; text-decoration: none; font-weight: 500;">
                                        Xem tất cả đơn hàng →
                                    </a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div style="text-align: center; padding: 2rem; color: #a0aec0;">
                                    <p>Chưa có đơn hàng nào.</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Quick Actions -->
                    <div class="dashboard-card">
                        <h3 style="margin-top: 0; color: #2d3748;">🚀 Hành động nhanh</h3>
                        <div
                            style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem;">
                            <a href="${pageContext.request.contextPath}/seller/products"
                                style="display: block; padding: 1rem; background: #3182ce; color: white; text-decoration: none; border-radius: 0.375rem; text-align: center; font-weight: 500;">
                                📦 Quản lý sản phẩm
                            </a>
                            <a href="${pageContext.request.contextPath}/seller/orders"
                                style="display: block; padding: 1rem; background: #38a169; color: white; text-decoration: none; border-radius: 0.375rem; text-align: center; font-weight: 500;">
                                📋 Xem đơn hàng
                            </a>
                            <a href="${pageContext.request.contextPath}/seller/settings"
                                style="display: block; padding: 1rem; background: #805ad5; color: white; text-decoration: none; border-radius: 0.375rem; text-align: center; font-weight: 500;">
                                ⚙️ Cài đặt shop
                            </a>
                        </div>
                    </div>

                </main>

                <jsp:include page="../common/footer.jsp" />
                <script src="${pageContext.request.contextPath}/js/main.js"></script>
            </body>

            </html>