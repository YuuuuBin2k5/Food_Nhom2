<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lịch sử giao hàng - Shipper</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/shipper.css">
</head>
<body class="bg-white shipper-page">
    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="currentPath" value="/shipper/history"/>
    </jsp:include>

    <main class="shipper-main">
        
        <!-- Header -->
        <div style="margin-bottom: 2rem;">
            <h2 style="font-size: 1.8rem; color: #1a202c; font-weight: 700; margin: 0;">📋 Lịch sử giao hàng</h2>
            <p style="color: #718096; margin: 0.5rem 0 0 0;">Các đơn hàng bạn đã giao thành công</p>
        </div>

        <!-- History List -->
        <div style="background: white; border-radius: 0.75rem; box-shadow: 0 2px 4px rgba(0,0,0,0.1); overflow: hidden;">
            <div style="padding: 1rem 1.5rem; background: linear-gradient(to right, #d1fae5, #a7f3d0); border-bottom: 1px solid #e2e8f0;">
                <h3 style="margin: 0; font-size: 1.125rem; color: #065f46;">✅ Đơn đã hoàn thành</h3>
            </div>
            
            <c:choose>
                <c:when test="${empty deliveredOrders}">
                    <div style="text-align: center; padding: 4rem 2rem; color: #718096;">
                        <span style="font-size: 4rem; display: block; margin-bottom: 1rem;">📭</span>
                        <h3 style="margin: 0 0 0.5rem 0; color: #1a202c;">Chưa có lịch sử</h3>
                        <p style="margin: 0 0 1.5rem 0;">Bạn chưa hoàn thành đơn hàng nào</p>
                        <a href="${pageContext.request.contextPath}/shipper/orders" 
                           style="display: inline-block; background: #805ad5; color: white; padding: 0.75rem 1.5rem; border-radius: 0.5rem; text-decoration: none; font-weight: 600;">
                            📦 Nhận đơn ngay
                        </a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div style="overflow-x: auto;">
                        <table style="width: 100%; border-collapse: collapse;">
                            <thead>
                                <tr style="background: #f8fafc;">
                                    <th style="padding: 1rem; text-align: left; font-weight: 600; color: #334155; border-bottom: 1px solid #e2e8f0;">Mã đơn</th>
                                    <th style="padding: 1rem; text-align: left; font-weight: 600; color: #334155; border-bottom: 1px solid #e2e8f0;">Khách hàng</th>
                                    <th style="padding: 1rem; text-align: left; font-weight: 600; color: #334155; border-bottom: 1px solid #e2e8f0;">Địa chỉ</th>
                                    <th style="padding: 1rem; text-align: left; font-weight: 600; color: #334155; border-bottom: 1px solid #e2e8f0;">Ngày giao</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="order" items="${deliveredOrders}">
                                    <tr style="transition: background 0.2s;" onmouseover="this.style.background='#f8fafc'" onmouseout="this.style.background='white'">
                                        <td style="padding: 1rem; border-bottom: 1px solid #e2e8f0;">
                                            <span style="font-weight: 700; color: #1a202c;">#${order.orderId}</span>
                                        </td>
                                        <td style="padding: 1rem; border-bottom: 1px solid #e2e8f0;">
                                            <span style="color: #334155;">${order.buyer.fullName}</span>
                                        </td>
                                        <td style="padding: 1rem; border-bottom: 1px solid #e2e8f0; max-width: 300px;">
                                            <span style="color: #718096; font-size: 0.875rem; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;">
                                                ${order.shippingAddress}
                                            </span>
                                        </td>
                                        <td style="padding: 1rem; border-bottom: 1px solid #e2e8f0;">
                                            <span style="color: #334155; font-size: 0.875rem;">
                                                <fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm"/>
                                            </span>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </main>

    <jsp:include page="../common/footer.jsp"/>
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
