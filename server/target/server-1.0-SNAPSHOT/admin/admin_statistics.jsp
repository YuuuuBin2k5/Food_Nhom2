<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Thống kê - Admin</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin_main.css">
</head>
<body>

<!-- Include Sidebar -->
    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="currentPath" value="/admin/statistics" />
    </jsp:include>

<div class="main-content">
    <div class="content-box">
        <h2>Thống Kê Hệ Thống</h2>
        
        <div class="stats-grid">
            <div class="stat-card stat-buyers">
                <div class="stat-icon">👥</div>
                <div class="stat-info">
                    <span class="stat-number">${totalBuyers}</span>
                    <span class="stat-label">Người mua</span>
                </div>
            </div>
            
            <div class="stat-card stat-sellers">
                <div class="stat-icon">🏪</div>
                <div class="stat-info">
                    <span class="stat-number">${totalSellers}</span>
                    <span class="stat-label">Người bán</span>
                </div>
            </div>
            
            <div class="stat-card stat-shippers">
                <div class="stat-icon">🚚</div>
                <div class="stat-info">
                    <span class="stat-number">${totalShippers}</span>
                    <span class="stat-label">Shipper</span>
                </div>
            </div>
            
            <div class="stat-card stat-orders">
                <div class="stat-icon">📦</div>
                <div class="stat-info">
                    <span class="stat-number">${totalOrders}</span>
                    <span class="stat-label">Đơn hàng</span>
                </div>
            </div>
            
            <div class="stat-card stat-products">
                <div class="stat-icon">🍔</div>
                <div class="stat-info">
                    <span class="stat-number">${totalProducts}</span>
                    <span class="stat-label">Sản phẩm</span>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>
