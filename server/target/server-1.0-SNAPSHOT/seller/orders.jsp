<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý đơn hàng - FreshSave Seller</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin_main.css">
    <style>
        body { background: #f8fafc; padding-top: 0; }
        .main-content { margin-left: 250px; padding: 2rem; }
        .product-list { list-style: none; padding: 0; margin: 0; }
        .product-list li { font-size: 0.9rem; margin-bottom: 4px; color: #475569; }
        .product-list li strong { color: #0f172a; }
        .action-group { display: flex; gap: 8px; }
        .tabs { display: flex; gap: 1rem; border-bottom: 2px solid #e2e8f0; margin-bottom: 1.5rem; }
        .tab-link { padding: 0.75rem 1.5rem; text-decoration: none; color: #64748b; font-weight: 600; border-bottom: 2px solid transparent; margin-bottom: -2px; transition: all 0.2s; }
        .tab-link:hover { color: #f97316; }
        .tab-link.active { color: #f97316; border-bottom-color: #f97316; }
    </style>
</head>
<body>

    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="currentPath" value="/seller/orders" />
    </jsp:include>

    <main class="main-content">
        <header style="margin-bottom: 2rem;">
            <h1 style="font-size: 1.8rem; color: #1e293b;">📋 Quản lý đơn hàng</h1>
        </header>

        <nav class="tabs">
            <a href="orders?status=PENDING" class="tab-link ${param.status == 'PENDING' || param.status == null ? 'active' : ''}">Chờ duyệt</a>
            <a href="orders?status=CONFIRMED" class="tab-link ${param.status == 'CONFIRMED' ? 'active' : ''}">Đã duyệt</a>
            <a href="orders?status=SHIPPING" class="tab-link ${param.status == 'SHIPPING' ? 'active' : ''}">Đang giao</a>
            <a href="orders?status=ALL" class="tab-link ${param.status == 'ALL' ? 'active' : ''}">Tất cả</a>
        </nav>

        <section class="table-container" style="background: white; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1); overflow: hidden;">
            <c:choose>
                <c:when test="${not empty orders}">
                    <table style="width: 100%; border-collapse: collapse;">
                        <thead style="background: #f1f5f9; text-align: left;">
                            <tr>
                                <th style="padding: 1rem;">Mã đơn</th>
                                <th style="padding: 1rem;">Ngày đặt</th>
                                <th style="padding: 1rem;">Sản phẩm</th>
                                <th style="padding: 1rem;">Tổng tiền</th>
                                <th style="padding: 1rem;">Trạng thái</th>
                                <th style="padding: 1rem;">Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="o" items="${orders}">
                                <tr style="border-bottom: 1px solid #e2e8f0;">
                                    <td style="padding: 1rem; color: #64748b;">#${o.orderId}</td>
                                    <td style="padding: 1rem;">
                                        <fmt:formatDate value="${o.orderDate}" pattern="dd/MM/yyyy"/><br>
                                        <small style="color: #94a3b8;"><fmt:formatDate value="${o.orderDate}" pattern="HH:mm"/></small>
                                    </td>
                                    
                                    <td style="padding: 1rem;">
                                        <ul class="product-list">
                                            <c:forEach var="d" items="${o.orderDetails}">
                                                <li>• <strong>${d.product.name}</strong> x${d.quantity}</li>
                                            </c:forEach>
                                        </ul>
                                    </td>
                                    
                                    <td style="padding: 1rem; font-weight: 700; color: #0f172a;">
                                        <fmt:formatNumber value="${o.payment.amount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                    </td>

                                    <td style="padding: 1rem;">
                                        <span class="status-badge 
                                            ${o.status == 'PENDING' ? 'badge-orange' : 
                                              o.status == 'CONFIRMED' ? 'badge-blue' : 
                                              o.status == 'DELIVERED' ? 'badge-green' : 
                                              o.status == 'CANCELLED' ? 'badge-red' : 'badge-gray'}">
                                            <c:choose>
                                                <c:when test="${o.status == 'PENDING'}">Chờ duyệt</c:when>
                                                <c:when test="${o.status == 'CONFIRMED'}">Đã xác nhận</c:when>
                                                <c:when test="${o.status == 'SHIPPING'}">Đang giao</c:when>
                                                <c:when test="${o.status == 'DELIVERED'}">Hoàn tất</c:when>
                                                <c:when test="${o.status == 'CANCELLED'}">Đã hủy</c:when>
                                                <c:otherwise>${o.status}</c:otherwise>
                                            </c:choose>
                                        </span>
                                    </td>

                                    <td style="padding: 1rem;">
                                        <c:if test="${o.status == 'PENDING'}">
                                            <div class="action-group">
                                                <form action="${pageContext.request.contextPath}/seller/orders" method="post">
                                                    <input type="hidden" name="action" value="CONFIRM">
                                                    <input type="hidden" name="orderId" value="${o.orderId}">
                                                    <button type="submit" class="btn btn-primary" style="padding: 6px 12px; font-size: 0.8rem; background: #10b981; border: none;">Duyệt</button>
                                                </form>

                                                <form action="${pageContext.request.contextPath}/seller/orders" method="post">
                                                    <input type="hidden" name="action" value="CANCEL">
                                                    <input type="hidden" name="orderId" value="${o.orderId}">
                                                    <button type="submit" class="btn" style="padding: 6px 12px; font-size: 0.8rem; background: #fee2e2; color: #ef4444; border: 1px solid #fecaca;" onclick="return confirm('Bạn có chắc chắn muốn từ chối đơn hàng này?')">Hủy</button>
                                                </form>
                                            </div>
                                        </c:if>
                                        <c:if test="${o.status == 'CONFIRMED'}">
                                            <span style="color: #64748b; font-size: 0.9rem;">⏳ Chờ Shipper</span>
                                        </c:if>
                                        <c:if test="${o.status == 'SHIPPING' || o.status == 'DELIVERED' || o.status == 'CANCELLED'}">
                                            <span style="color: #cbd5e1; font-size: 0.9rem;">---</span>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <div style="padding: 3rem; text-align: center; color: #64748b;">
                        <p style="font-size: 1.2rem;">📭 Không có đơn hàng nào trong mục này</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>
    </main>
</body>
</html>