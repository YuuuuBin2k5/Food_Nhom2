<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tổng quan - Seller Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <style>
        body {
            margin: 0;
            padding-top: 96px;
            background: linear-gradient(to bottom right, #fff7ed, #fef3c7, #fef9c3);
            min-height: 100vh;
            padding-bottom: 2.5rem;
        }
        
        .dashboard-header {
            background: linear-gradient(to right, #FF6B6B, #FF8E53, #FFC75F);
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            margin-bottom: 2rem;
        }
        
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 1.5rem;
            margin-bottom: 2rem;
        }
        
        .stat-card {
            background: white;
            border-radius: 1rem;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
            border: 1px solid #f1f5f9;
            padding: 1.5rem;
            transition: all 0.3s;
            cursor: pointer;
        }
        
        .stat-card:hover {
            box-shadow: 0 10px 15px rgba(0, 0, 0, 0.1);
            transform: translateY(-4px);
        }
        
        .stat-icon {
            width: 56px;
            height: 56px;
            border-radius: 9999px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.875rem;
            margin-bottom: 1rem;
        }
        
        .stat-value {
            font-size: 2.25rem;
            font-weight: 700;
            color: #0f172a;
        }
        
        .stat-label {
            color: #334155;
            font-size: 0.875rem;
            font-weight: 500;
            margin-bottom: 0.25rem;
        }
        
        .stat-detail {
            font-size: 0.75rem;
            color: #10b981;
            margin-top: 0.5rem;
            display: flex;
            align-items: center;
            gap: 0.25rem;
        }
        
        .revenue-card {
            background: linear-gradient(to right, #FF6B6B, #FF8E53, #FFC75F);
            border-radius: 1rem;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            padding: 2rem;
            color: white;
            margin-bottom: 2rem;
        }
        
        .content-grid {
            display: grid;
            grid-template-columns: 1fr;
            gap: 2rem;
        }
        
        @media (min-width: 1024px) {
            .content-grid {
                grid-template-columns: 1fr 1fr;
            }
        }
        
        .content-card {
            background: white;
            border-radius: 1rem;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
            border: 1px solid #f1f5f9;
            overflow: hidden;
        }
        
        .content-header {
            padding: 1rem 1.5rem;
            background: linear-gradient(to right, #fff7ed, #fef3c7);
            border-bottom: 1px solid #f1f5f9;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .content-body {
            padding: 1.5rem;
        }
        
        .item-list {
            display: flex;
            flex-direction: column;
            gap: 0.75rem;
        }
        
        .item {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0.75rem;
            border-radius: 0.75rem;
            background: #f8fafc;
            transition: background 0.2s;
            cursor: pointer;
        }
        
        .item:hover {
            background: #fff7ed;
        }
        
        .status-badge {
            padding: 0.25rem 0.5rem;
            border-radius: 9999px;
            font-size: 0.75rem;
            font-weight: 700;
        }
        
        .status-active { background: #d1fae5; color: #065f46; }
        .status-pending { background: #fef3c7; color: #92400e; }
        .status-confirmed { background: #dbeafe; color: #1e40af; }
        
        .quick-actions {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1rem;
            margin-top: 2rem;
        }
        
        .action-btn {
            padding: 1rem;
            border-radius: 0.75rem;
            border: 2px solid;
            text-align: left;
            cursor: pointer;
            transition: all 0.2s;
            text-decoration: none;
            display: block;
        }
        
        .action-btn:hover {
            transform: scale(1.02);
        }
        
        .empty-state {
            text-align: center;
            padding: 2rem;
            color: #334155;
        }
    </style>
</head>
<body>
    <!-- Include Sidebar -->
    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="currentPath" value="/seller/dashboard" />
    </jsp:include>
    
    <!-- Header -->
    <div class="dashboard-header">
        <div style="max-width: 1280px; margin: 0 auto; padding: 2rem 1rem;">
            <div style="display: flex; justify-content: space-between; align-items: flex-start; gap: 1rem; flex-wrap: wrap;">
                <div>
                    <h1 style="font-size: 1.875rem; font-weight: 700; color: white; display: flex; align-items: center; gap: 0.75rem;">
                        <span style="font-size: 2.25rem;">📊</span>
                        Tổng quan kinh doanh
                    </h1>
                    <p style="color: rgba(255, 255, 255, 0.9); font-size: 1rem; margin-top: 0.5rem;">
                        Xin chào, <strong>${sessionScope.user.fullName}</strong>! 👋
                    </p>
                </div>
                <div style="display: flex; gap: 0.75rem;">
                    <a href="${pageContext.request.contextPath}/seller/products" style="padding: 0.625rem 1.25rem; background: white; color: #FF6B6B; border-radius: 0.75rem; font-weight: 600; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); transition: all 0.2s; display: flex; align-items: center; gap: 0.5rem; text-decoration: none;">
                        <span>📦</span>
                        Quản lý kho
                    </a>
                    <a href="${pageContext.request.contextPath}/seller/orders" style="padding: 0.625rem 1.25rem; background: rgba(255, 255, 255, 0.1); backdrop-filter: blur(4px); color: white; border: 2px solid rgba(255, 255, 255, 0.3); border-radius: 0.75rem; font-weight: 600; transition: all 0.2s; display: flex; align-items: center; gap: 0.5rem; text-decoration: none;">
                        <span>📄</span>
                        Xem đơn hàng
                    </a>
                </div>
            </div>
        </div>
    </div>
    
    <main style="max-width: 1280px; margin: 0 auto; padding: 0 1rem;">
        <!-- Stats Grid -->
        <div class="stats-grid">
            <div class="stat-card" onclick="location.href='${pageContext.request.contextPath}/seller/products'">
                <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 1rem;">
                    <div class="stat-icon" style="background: linear-gradient(to bottom right, #dbeafe, #bfdbfe);">
                        📦
                    </div>
                    <span style="font-size: 0.75rem; font-weight: 700; color: #2563eb; background: #dbeafe; padding: 0.25rem 0.75rem; border-radius: 9999px;">
                        Tổng số
                    </span>
                </div>
                <p class="stat-label">Sản phẩm</p>
                <p class="stat-value">${totalProducts}</p>
                <p class="stat-detail">
                    <span>✅</span> ${activeProducts} đang bán
                </p>
            </div>
            
            <div class="stat-card" onclick="location.href='${pageContext.request.contextPath}/seller/orders'">
                <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 1rem;">
                    <div class="stat-icon" style="background: linear-gradient(to bottom right, #fef3c7, #fde68a);">
                        ⏳
                    </div>
                    <c:if test="${pendingOrders > 0}">
                        <span style="font-size: 0.75rem; font-weight: 700; color: white; background: #ef4444; padding: 0.25rem 0.75rem; border-radius: 9999px; animation: pulse 2s infinite;">
                            Cần xử lý!
                        </span>
                    </c:if>
                </div>
                <p class="stat-label">Đơn chờ duyệt</p>
                <p class="stat-value">${pendingOrders}</p>
                <p class="stat-detail" style="color: #334155;">
                    <span>📅</span> ${todayOrders} đơn hôm nay
                </p>
            </div>
            
            <div class="stat-card">
                <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 1rem;">
                    <div class="stat-icon" style="background: linear-gradient(to bottom right, #fed7aa, #fecaca);">
                        ⚠️
                    </div>
                    <c:if test="${expiringSoon > 0}">
                        <span style="font-size: 0.75rem; font-weight: 700; color: #ea580c; background: #ffedd5; padding: 0.25rem 0.75rem; border-radius: 9999px;">
                            Chú ý!
                        </span>
                    </c:if>
                </div>
                <p class="stat-label">Sắp hết hạn</p>
                <p class="stat-value">${expiringSoon}</p>
                <p class="stat-detail" style="color: #ea580c;">
                    <span>⏰</span> Còn ≤ 3 ngày
                </p>
            </div>
        </div>
        
        <!-- Revenue Card -->
        <div class="revenue-card">
            <div style="display: flex; align-items: center; justify-content: space-between;">
                <div>
                    <p style="color: rgba(255, 255, 255, 0.8); font-size: 0.875rem; font-weight: 500; margin-bottom: 0.5rem; display: flex; align-items: center; gap: 0.5rem;">
                        <span style="font-size: 1.5rem;">💰</span>
                        Tổng doanh thu
                    </p>
                    <p style="font-size: 3rem; font-weight: 700;">
                        <fmt:formatNumber value="${totalRevenue}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                    </p>
                    <p style="color: rgba(255, 255, 255, 0.8); font-size: 0.875rem; margin-top: 0.75rem;">
                        Từ các đơn hàng đã giao thành công
                    </p>
                </div>
                <div style="width: 128px; height: 128px; background: rgba(255, 255, 255, 0.1); backdrop-filter: blur(4px); border-radius: 9999px; display: none; align-items: center; justify-content: center;" class="md-flex">
                    <span style="font-size: 4rem;">📈</span>
                </div>
            </div>
        </div>
        
        <!-- Content Grid -->
        <div class="content-grid">
            <!-- Recent Products -->
            <div class="content-card">
                <div class="content-header">
                    <h3 style="font-size: 1.125rem; font-weight: 700; color: #0f172a; display: flex; align-items: center; gap: 0.5rem;">
                        <span style="font-size: 1.25rem;">📦</span>
                        Sản phẩm gần đây
                    </h3>
                    <a href="${pageContext.request.contextPath}/seller/products" style="font-size: 0.875rem; color: #FF6B6B; font-weight: 600; text-decoration: none;">
                        Xem tất cả →
                    </a>
                </div>
                <div class="content-body">
                    <c:choose>
                        <c:when test="${empty recentProducts}">
                            <div class="empty-state">
                                <span style="font-size: 2.25rem; display: block; margin-bottom: 0.5rem;">📭</span>
                                Chưa có sản phẩm nào
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="item-list">
                                <c:forEach items="${recentProducts}" var="product">
                                    <div class="item" onclick="location.href='${pageContext.request.contextPath}/seller/products'">
                                        <div style="flex: 1;">
                                            <p style="font-weight: 600; color: #0f172a; font-size: 0.875rem;">${product.name}</p>
                                            <div style="display: flex; align-items: center; gap: 0.5rem; margin-top: 0.25rem;">
                                                <span style="font-size: 0.75rem; color: #334155;">SL: ${product.quantity}</span>
                                                <span style="font-size: 0.75rem; color: #334155;">•</span>
                                                <span style="font-size: 0.75rem; color: #FF6B6B; font-weight: 700;">
                                                    <fmt:formatNumber value="${product.salePrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                                </span>
                                            </div>
                                        </div>
                                        <span class="status-badge status-${fn:toLowerCase(product.status)}">
                                            <c:choose>
                                                <c:when test="${product.status == 'ACTIVE'}">✅ Đang bán</c:when>
                                                <c:when test="${product.status == 'PENDING_APPROVAL'}">⏳ Chờ duyệt</c:when>
                                                <c:otherwise>${product.status}</c:otherwise>
                                            </c:choose>
                                        </span>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            
            <!-- Recent Orders -->
            <div class="content-card">
                <div class="content-header">
                    <h3 style="font-size: 1.125rem; font-weight: 700; color: #0f172a; display: flex; align-items: center; gap: 0.5rem;">
                        <span style="font-size: 1.25rem;">📄</span>
                        Đơn hàng gần đây
                    </h3>
                    <a href="${pageContext.request.contextPath}/seller/orders" style="font-size: 0.875rem; color: #FF6B6B; font-weight: 600; text-decoration: none;">
                        Xem tất cả →
                    </a>
                </div>
                <div class="content-body">
                    <c:choose>
                        <c:when test="${empty recentOrders}">
                            <div class="empty-state">
                                <span style="font-size: 2.25rem; display: block; margin-bottom: 0.5rem;">📭</span>
                                Chưa có đơn hàng nào
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="item-list">
                                <c:forEach items="${recentOrders}" var="order">
                                    <div class="item" onclick="location.href='${pageContext.request.contextPath}/seller/orders'">
                                        <div style="flex: 1;">
                                            <p style="font-weight: 600; color: #0f172a; font-size: 0.875rem;">Đơn #${order.orderId}</p>
                                            <div style="display: flex; align-items: center; gap: 0.5rem; margin-top: 0.25rem;">
                                                <span style="font-size: 0.75rem; color: #334155;">${order.buyer.fullName}</span>
                                                <span style="font-size: 0.75rem; color: #334155;">•</span>
                                                <span style="font-size: 0.75rem; color: #FF6B6B; font-weight: 700;">
                                                    <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                                </span>
                                            </div>
                                        </div>
                                        <span class="status-badge status-${fn:toLowerCase(order.status)}">
                                            <c:choose>
                                                <c:when test="${order.status == 'PENDING'}">⏳ Chờ xác nhận</c:when>
                                                <c:when test="${order.status == 'CONFIRMED'}">👨‍🍳 Đã xác nhận</c:when>
                                                <c:otherwise>${order.status}</c:otherwise>
                                            </c:choose>
                                        </span>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
        
        <!-- Quick Actions -->
        <div style="margin-top: 2rem; background: white; border-radius: 1rem; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1); border: 1px solid #f1f5f9; padding: 1.5rem;">
            <h3 style="font-size: 1.125rem; font-weight: 700; color: #0f172a; margin-bottom: 1rem; display: flex; align-items: center; gap: 0.5rem;">
                <span style="font-size: 1.25rem;">⚡</span>
                Thao tác nhanh
            </h3>
            <div class="quick-actions">
                <a href="${pageContext.request.contextPath}/seller/products" class="action-btn" style="background: linear-gradient(to bottom right, #dbeafe, #bfdbfe); border-color: #93c5fd; color: #0f172a;">
                    <span style="font-size: 1.875rem; display: block; margin-bottom: 0.5rem;">➕</span>
                    <p style="font-weight: 700; font-size: 0.875rem;">Thêm sản phẩm</p>
                    <p style="font-size: 0.75rem; color: #334155; margin-top: 0.25rem;">Đăng bán thực phẩm mới</p>
                </a>
                <a href="${pageContext.request.contextPath}/seller/orders" class="action-btn" style="background: linear-gradient(to bottom right, #fef3c7, #fde68a); border-color: #fde047; color: #0f172a;">
                    <span style="font-size: 1.875rem; display: block; margin-bottom: 0.5rem;">📋</span>
                    <p style="font-weight: 700; font-size: 0.875rem;">Xem đơn hàng</p>
                    <p style="font-size: 0.75rem; color: #334155; margin-top: 0.25rem;">Quản lý đơn đặt hàng</p>
                </a>
                <a href="${pageContext.request.contextPath}/seller/settings" class="action-btn" style="background: linear-gradient(to bottom right, #e9d5ff, #d8b4fe); border-color: #c084fc; color: #0f172a;">
                    <span style="font-size: 1.875rem; display: block; margin-bottom: 0.5rem;">⚙️</span>
                    <p style="font-weight: 700; font-size: 0.875rem;">Cài đặt shop</p>
                    <p style="font-size: 0.75rem; color: #334155; margin-top: 0.25rem;">Cập nhật thông tin</p>
                </a>
                <button onclick="window.location.reload()" class="action-btn" style="background: linear-gradient(to bottom right, #d1fae5, #a7f3d0); border-color: #6ee7b7; color: #0f172a;">
                    <span style="font-size: 1.875rem; display: block; margin-bottom: 0.5rem;">🔄</span>
                    <p style="font-weight: 700; font-size: 0.875rem;">Làm mới</p>
                    <p style="font-size: 0.75rem; color: #334155; margin-top: 0.25rem;">Cập nhật dữ liệu mới</p>
                </button>
            </div>
        </div>
    </main>
    
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
    <style>
        @keyframes pulse {
            0%, 100% { opacity: 1; }
            50% { opacity: 0.5; }
        }
    </style>
</body>
</html>
