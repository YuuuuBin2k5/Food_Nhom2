<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đơn hàng - Seller</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/seller_style.css">
</head>
<body class="bg-white">

    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="currentPath" value="/seller/orders" />
    </jsp:include>

    <main style="margin-top: 96px; min-height: 80vh; padding: 2rem; max-width: 1400px; margin-left: auto; margin-right: auto;">
        
        <div style="margin-bottom: 2rem;">
            <h2 style="font-size: 1.5rem; color: #1a202c; margin-bottom: 1rem;">📦 Quản lý đơn hàng</h2>
            
            <div class="filter-bar" style="border-bottom: 2px solid #e2e8f0;">
                <a href="?status=PENDING" 
                   style="padding: 0.75rem 1.5rem; display: inline-block; text-decoration: none; color: ${param.status == 'PENDING' || empty param.status ? '#ea580c' : '#718096'}; border-bottom: 2px solid ${param.status == 'PENDING' || empty param.status ? '#ea580c' : 'transparent'}; font-weight: 600;">
                   Chờ duyệt (Pending)
                </a>
                <a href="?status=CONFIRMED" 
                   style="padding: 0.75rem 1.5rem; display: inline-block; text-decoration: none; color: ${param.status == 'CONFIRMED' ? '#ea580c' : '#718096'}; border-bottom: 2px solid ${param.status == 'CONFIRMED' ? '#ea580c' : 'transparent'}; font-weight: 600;">
                   Đã duyệt / Đang giao
                </a>
                <a href="?status=ALL" 
                   style="padding: 0.75rem 1.5rem; display: inline-block; text-decoration: none; color: ${param.status == 'ALL' ? '#ea580c' : '#718096'}; border-bottom: 2px solid ${param.status == 'ALL' ? '#ea580c' : 'transparent'}; font-weight: 600;">
                   Tất cả
                </a>
            </div>
        </div>

        <c:choose>
            <c:when test="${not empty orders}">
                <table style="width: 100%; border-collapse: collapse; background: white; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
                    <thead style="background: #f7fafc;">
                        <tr>
                            <th style="padding: 1rem; text-align: left;">Mã đơn</th>
                            <th style="padding: 1rem; text-align: left;">Khách hàng</th>
                            <th style="padding: 1rem; text-align: left;">Địa chỉ</th>
                            <th style="padding: 1rem; text-align: left;">Tổng tiền</th>
                            <th style="padding: 1rem; text-align: left;">Ngày đặt</th>
                            <th style="padding: 1rem; text-align: left;">Trạng thái</th>
                            <th style="padding: 1rem; text-align: left;">Hành động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="o" items="${orders}">
                            <tr style="border-top: 1px solid #e2e8f0;">
                                <td style="padding: 1rem; font-weight: bold;">#${o.orderId}</td>
                                <td style="padding: 1rem;">${o.buyer.fullName}</td>
                                <td style="padding: 1rem;">${o.shippingAddress}</td>
                                <td style="padding: 1rem; font-weight: 600; color: #2d3748;">
                                    <fmt:formatNumber value="${o.payment.amount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                </td>
                                <td style="padding: 1rem; color: #718096;">
                                    <fmt:formatDate value="${o.orderDate}" pattern="dd/MM/yyyy HH:mm"/>
                                </td>
                                <td style="padding: 1rem;">
                                    <span class="status-badge status-${o.status}">${o.status}</span>
                                </td>
                                <td style="padding: 1rem;">
                                    <c:if test="${o.status == 'PENDING'}">
                                        <form action="${pageContext.request.contextPath}/seller/orders" method="post">
                                            <input type="hidden" name="action" value="approve">
                                            <input type="hidden" name="orderId" value="${o.orderId}">
                                            <button type="submit" style="background: #38a169; color: white; border: none; padding: 0.5rem 1rem; border-radius: 4px; cursor: pointer; font-weight: 600;">✅ Duyệt đơn</button>
                                        </form>
                                    </c:if>
                                    <c:if test="${o.status == 'CONFIRMED'}">
                                        <span style="color: #718096; font-size: 0.9em; font-style: italic;">⏳ Đang chờ Shipper</span>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <div style="text-align: center; padding: 3rem; background: #f7fafc; border-radius: 8px; color: #718096;">
                    <p>Chưa có đơn hàng nào trong mục này.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </main>

    <jsp:include page="../common/footer.jsp" />
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>