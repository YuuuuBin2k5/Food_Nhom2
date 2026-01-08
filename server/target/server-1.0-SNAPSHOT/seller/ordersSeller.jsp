<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Đơn hàng - Seller</title>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/seller/seller_css.css">
            </head>

            <body class="bg-white">

                <jsp:include page="../common/sidebar.jsp">
                    <jsp:param name="currentPath" value="/seller/orders" />
                </jsp:include>

                <main class="seller-main">

                    <div class="orders-header">
                        <h2>📦 Quản lý đơn hàng</h2>

                        <div class="filter-bar">
                            <a href="?status=PENDING"
                                class="${param.status == 'PENDING' || empty param.status ? 'text-orange-600' : 'text-gray-500'}"
                                style="border-bottom: 2px solid ${param.status == 'PENDING' || empty param.status ? '#ea580c' : 'transparent'};">
                                🕐 Chờ duyệt
                            </a>
                            <a href="?status=CONFIRMED"
                                class="${param.status == 'CONFIRMED' ? 'text-orange-600' : 'text-gray-500'}"
                                style="border-bottom: 2px solid ${param.status == 'CONFIRMED' ? '#ea580c' : 'transparent'};">
                                ✅ Đã duyệt
                            </a>
                            <a href="?status=SHIPPING"
                                class="${param.status == 'SHIPPING' ? 'text-orange-600' : 'text-gray-500'}"
                                style="border-bottom: 2px solid ${param.status == 'SHIPPING' ? '#ea580c' : 'transparent'};">
                                🚚 Đang giao
                            </a>
                            <a href="?status=DELIVERED"
                                class="${param.status == 'DELIVERED' ? 'text-orange-600' : 'text-gray-500'}"
                                style="border-bottom: 2px solid ${param.status == 'DELIVERED' ? '#ea580c' : 'transparent'};">
                                📦 Đã giao
                            </a>
                            <a href="?status=CANCELLED"
                                class="${param.status == 'CANCELLED' ? 'text-orange-600' : 'text-gray-500'}"
                                style="border-bottom: 2px solid ${param.status == 'CANCELLED' ? '#ea580c' : 'transparent'};">
                                ❌ Đã hủy
                            </a>
                            <a href="?status=ALL" class="${param.status == 'ALL' ? 'text-orange-600' : 'text-gray-500'}"
                                style="border-bottom: 2px solid ${param.status == 'ALL' ? '#ea580c' : 'transparent'};">
                                📋 Tất cả
                            </a>
                        </div>
                    </div>

                    <c:choose>
                        <c:when test="${not empty orders}">
                            <table class="orders-table">
                                <thead>
                                    <tr>
                                        <th>Mã đơn</th>
                                        <th>Khách hàng</th>
                                        <th>Địa chỉ</th>
                                        <th>Tổng tiền</th>
                                        <th>Ngày đặt</th>
                                        <th>Trạng thái</th>
                                        <th>Hành động</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="o" items="${orders}">
                                        <tr>
                                            <td class="order-id">#${o.orderId}</td>
                                            <td>${o.buyer.fullName}</td>
                                            <td>${o.shippingAddress}</td>
                                            <td class="order-total">
                                                <fmt:formatNumber value="${o.payment.amount}" type="currency"
                                                    currencySymbol="₫" maxFractionDigits="0" />
                                            </td>
                                            <td class="order-date">
                                                <fmt:formatDate value="${o.orderDate}" pattern="dd/MM/yyyy HH:mm" />
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${o.status == 'PENDING'}">
                                                        <span class="order-status-pending">🕐 Chờ duyệt</span>
                                                    </c:when>
                                                    <c:when test="${o.status == 'CONFIRMED'}">
                                                        <span class="order-status-confirmed">✅ Đã duyệt</span>
                                                    </c:when>
                                                    <c:when test="${o.status == 'SHIPPING'}">
                                                        <span class="order-status-shipping">🚚 Đang giao</span>
                                                    </c:when>
                                                    <c:when test="${o.status == 'DELIVERED'}">
                                                        <span class="order-status-delivered">📦 Đã giao</span>
                                                    </c:when>
                                                    <c:when test="${o.status == 'CANCELLED'}">
                                                        <span class="order-status-cancelled">❌ Đã hủy</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="status-badge">${o.status}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${o.status == 'PENDING'}">
                                                        <div class="action-buttons">
                                                            <form
                                                                action="${pageContext.request.contextPath}/seller/orders"
                                                                method="post" style="display: inline;">
                                                                <input type="hidden" name="action" value="CONFIRM">
                                                                <input type="hidden" name="orderId"
                                                                    value="${o.orderId}">
                                                                <button type="submit" class="btn-approve">✅ Duyệt
                                                                    đơn</button>
                                                            </form>
                                                            <form
                                                                action="${pageContext.request.contextPath}/seller/orders"
                                                                method="post" style="display: inline;">
                                                                <input type="hidden" name="action" value="CANCEL">
                                                                <input type="hidden" name="orderId"
                                                                    value="${o.orderId}">
                                                                <button type="submit" class="btn-cancel-order"
                                                                    onclick="return confirm('Bạn có chắc muốn hủy đơn hàng này?')">❌
                                                                    Hủy đơn</button>
                                                            </form>
                                                        </div>
                                                    </c:when>
                                                    <c:when test="${o.status == 'CONFIRMED'}">
                                                        <span class="order-action-text order-action-success">✅ Đã duyệt
                                                            - Chờ shipper nhận đơn</span>
                                                    </c:when>
                                                    <c:when test="${o.status == 'SHIPPING'}">
                                                        <span class="order-action-text order-action-info">🚚 Đang được
                                                            giao bởi shipper</span>
                                                    </c:when>
                                                    <c:when test="${o.status == 'DELIVERED'}">
                                                        <span class="order-action-text order-action-success">✅ Đã giao
                                                            thành công</span>
                                                    </c:when>
                                                    <c:when test="${o.status == 'CANCELLED'}">
                                                        <span class="order-action-text order-action-error">❌ Đơn hàng đã
                                                            bị hủy</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="order-action-text order-action-muted">Không có hành
                                                            động</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state">
                                <p>Chưa có đơn hàng nào trong mục này.</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </main>

                <jsp:include page="../common/footer.jsp" />
                <script src="${pageContext.request.contextPath}/js/main.js"></script>
            </body>

            </html>