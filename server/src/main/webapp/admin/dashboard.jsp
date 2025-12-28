<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Admin Dashboard - Tổng quan hệ thống</title>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin_main.css">
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

                    .urgent-card {
                        border-left: 4px solid #e53e3e;
                        background: #fed7d7;
                    }

                    .warning-card {
                        border-left: 4px solid #dd6b20;
                        background: #feebc8;
                    }
                </style>
            </head>

            <body class="bg-white">

                <jsp:include page="../common/sidebar.jsp">
                    <jsp:param name="currentPath" value="/admin/dashboard" />
                </jsp:include>

                <main
                    style="margin-top: 96px; min-height: 80vh; padding: 2rem; max-width: 1400px; margin-left: auto; margin-right: auto;">

                    <div class="dashboard-header" style="margin-bottom: 2rem;">
                        <h2 style="font-size: 1.8rem; color: #1a202c; font-weight: 700;">🛡️ Admin Dashboard</h2>
                        <p style="color: #718096;">Tổng quan hệ thống và các tác vụ cần xử lý.</p>
                    </div>

                    <!-- Error Message -->
                    <c:if test="${not empty error}">
                        <div
                            style="background-color: #fed7d7; border-left: 4px solid #e53e3e; padding: 1rem; margin-bottom: 2rem; border-radius: 4px;">
                            <h3 style="color: #c53030; margin: 0 0 0.5rem 0; font-size: 1.1rem;">⚠️ Lỗi tải dữ liệu</h3>
                            <p style="margin: 0; color: #742a2a;">${error}</p>
                        </div>
                    </c:if>

                    <!-- Urgent Actions -->
                    <c:if test="${pendingSellers > 0 || pendingProducts > 0}">
                        <div class="urgent-card" style="padding: 1rem; margin-bottom: 2rem; border-radius: 4px;">
                            <h3 style="color: #c53030; margin: 0 0 0.5rem 0; font-size: 1.1rem;">🚨 Cần xử lý ngay</h3>
                            <div style="display: flex; gap: 2rem; flex-wrap: wrap;">
                                <c:if test="${pendingSellers > 0}">
                                    <div>
                                        <strong>${pendingSellers}</strong> seller chờ duyệt
                                        <a href="${pageContext.request.contextPath}/admin/approveSeller"
                                            style="margin-left: 0.5rem; color: #c53030; text-decoration: underline;">
                                            Duyệt ngay →
                                        </a>
                                    </div>
                                </c:if>
                                <c:if test="${pendingProducts > 0}">
                                    <div>
                                        <strong>${pendingProducts}</strong> sản phẩm chờ duyệt
                                        <a href="${pageContext.request.contextPath}/admin/approveProduct"
                                            style="margin-left: 0.5rem; color: #c53030; text-decoration: underline;">
                                            Duyệt ngay →
                                        </a>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </c:if>

                    <!-- Stats Grid -->
                    <div class="stat-grid">
                        <div class="stat-card">
                            <div class="stat-number" style="color: #38a169;">${totalOrders}</div>
                            <div class="stat-label">Tổng đơn hàng</div>
                        </div>

                        <div class="stat-card">
                            <div class="stat-number" style="color: #3182ce;">${pendingOrders}</div>
                            <div class="stat-label">Đơn chờ xử lý</div>
                        </div>

                        <div class="stat-card">
                            <div class="stat-number" style="color: #805ad5;">
                                <fmt:formatNumber value="${totalRevenue}" type="currency" currencySymbol="₫"
                                    maxFractionDigits="0" />
                            </div>
                            <div class="stat-label">Tổng doanh thu</div>
                        </div>

                        <div class="stat-card">
                            <div class="stat-number" style="color: #d69e2e;">${pendingSellers + pendingProducts}</div>
                            <div class="stat-label">Chờ duyệt</div>
                        </div>
                    </div>

                    <!-- Next Items to Review -->
                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem; margin-bottom: 2rem;">
                        <!-- Next Seller -->
                        <div class="dashboard-card">
                            <h3 style="margin-top: 0; color: #2d3748;">👤 Seller tiếp theo cần duyệt</h3>
                            <c:choose>
                                <c:when test="${not empty nextSeller}">
                                    <div style="border: 1px solid #e2e8f0; border-radius: 0.375rem; padding: 1rem;">
                                        <h4 style="margin: 0 0 0.5rem 0; color: #2d3748;">${nextSeller.shopName}</h4>
                                        <p style="margin: 0; color: #718096; font-size: 0.875rem;">
                                            Chủ shop: ${nextSeller.fullName}<br>
                                            Email: ${nextSeller.email}<br>
                                            Nộp hồ sơ:
                                            <fmt:formatDate value="${nextSeller.licenseSubmittedDate}"
                                                pattern="dd/MM/yyyy HH:mm" />
                                        </p>
                                        <div style="margin-top: 1rem;">
                                            <a href="${pageContext.request.contextPath}/admin/approveSeller?action=detail&sellerId=${nextSeller.userId}"
                                                style="background: #3182ce; color: white; padding: 0.5rem 1rem; text-decoration: none; border-radius: 0.25rem; font-size: 0.875rem;">
                                                Xem chi tiết
                                            </a>
                                        </div>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div style="text-align: center; padding: 2rem; color: #a0aec0;">
                                        <p>✅ Không có seller nào chờ duyệt</p>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <!-- Next Product -->
                        <div class="dashboard-card">
                            <h3 style="margin-top: 0; color: #2d3748;">📦 Sản phẩm tiếp theo cần duyệt</h3>
                            <c:choose>
                                <c:when test="${not empty nextProduct}">
                                    <div style="border: 1px solid #e2e8f0; border-radius: 0.375rem; padding: 1rem;">
                                        <h4 style="margin: 0 0 0.5rem 0; color: #2d3748;">${nextProduct.name}</h4>
                                        <p style="margin: 0; color: #718096; font-size: 0.875rem;">
                                            Shop: ${nextProduct.seller.shopName}<br>
                                            Giá:
                                            <fmt:formatNumber value="${nextProduct.salePrice}" type="currency"
                                                currencySymbol="₫" maxFractionDigits="0" /><br>
                                            Đăng:
                                            <fmt:formatDate value="${nextProduct.createdDate}"
                                                pattern="dd/MM/yyyy HH:mm" />
                                        </p>
                                        <div style="margin-top: 1rem;">
                                            <a href="${pageContext.request.contextPath}/admin/approveProduct?action=detail&productId=${nextProduct.productId}"
                                                style="background: #38a169; color: white; padding: 0.5rem 1rem; text-decoration: none; border-radius: 0.25rem; font-size: 0.875rem;">
                                                Xem chi tiết
                                            </a>
                                        </div>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div style="text-align: center; padding: 2rem; color: #a0aec0;">
                                        <p>✅ Không có sản phẩm nào chờ duyệt</p>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <!-- Quick Actions -->
                    <div class="dashboard-card">
                        <h3 style="margin-top: 0; color: #2d3748;">🚀 Hành động nhanh</h3>
                        <div
                            style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem;">
                            <a href="${pageContext.request.contextPath}/admin/approveSeller"
                                style="display: block; padding: 1rem; background: #3182ce; color: white; text-decoration: none; border-radius: 0.375rem; text-align: center; font-weight: 500;">
                                👤 Duyệt Seller
                            </a>
                            <a href="${pageContext.request.contextPath}/admin/approveProduct"
                                style="display: block; padding: 1rem; background: #38a169; color: white; text-decoration: none; border-radius: 0.375rem; text-align: center; font-weight: 500;">
                                📦 Duyệt Sản phẩm
                            </a>
                            <a href="${pageContext.request.contextPath}/admin/statistics"
                                style="display: block; padding: 1rem; background: #805ad5; color: white; text-decoration: none; border-radius: 0.375rem; text-align: center; font-weight: 500;">
                                📊 Thống kê
                            </a>
                            <a href="${pageContext.request.contextPath}/admin/users"
                                style="display: block; padding: 1rem; background: #d69e2e; color: white; text-decoration: none; border-radius: 0.375rem; text-align: center; font-weight: 500;">
                                👥 Quản lý User
                            </a>
                        </div>
                    </div>

                </main>

                <jsp:include page="../common/footer.jsp" />
                <script src="${pageContext.request.contextPath}/js/main.js"></script>
            </body>

            </html>