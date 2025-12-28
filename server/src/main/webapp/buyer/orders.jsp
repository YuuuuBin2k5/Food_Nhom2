<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lịch sử đơn hàng - FreshSave</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <style>
        body { margin: 0; padding-top: 96px; background: #f8fafc; min-height: 100vh; }
        .container { max-width: 1000px; margin: 0 auto; padding: 2rem 1rem; }
        
        .page-title { font-size: 1.5rem; font-weight: 700; color: #1e293b; margin-bottom: 1.5rem; }
        
        .order-item { background: white; border-radius: 1rem; border: 1px solid #e2e8f0; margin-bottom: 1.5rem; overflow: hidden; transition: box-shadow 0.2s; }
        .order-item:hover { box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
        
        .order-header { background: #f1f5f9; padding: 1rem 1.5rem; display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #e2e8f0; }
        .order-meta { display: flex; gap: 2rem; font-size: 0.875rem; color: #64748b; }
        .meta-label { display: block; font-size: 0.75rem; text-transform: uppercase; margin-bottom: 0.25rem; }
        .meta-value { color: #0f172a; font-weight: 600; }
        
        .order-status { font-weight: 600; font-size: 0.875rem; padding: 0.25rem 0.75rem; border-radius: 99px; }
        .status-PENDING { background: #fef3c7; color: #d97706; }
        .status-CONFIRMED { background: #dbeafe; color: #2563eb; }
        .status-SHIPPING { background: #f3e8ff; color: #9333ea; }
        .status-DELIVERED { background: #dcfce7; color: #16a34a; }
        .status-CANCELLED { background: #fee2e2; color: #dc2626; }
        
        .order-content { padding: 1.5rem; }
        .product-list { display: flex; flex-direction: column; gap: 1rem; }
        
        .product-item { display: flex; gap: 1rem; }
        .product-img { width: 80px; height: 80px; border-radius: 0.5rem; object-fit: cover; background: #eee; }
        .product-details { flex: 1; }
        .product-name { font-weight: 600; color: #334155; margin-bottom: 0.25rem; }
        .product-shop { font-size: 0.875rem; color: #64748b; }
        .product-price { font-weight: 600; color: #ef4444; }
        
        .order-actions { padding: 1rem 1.5rem; border-top: 1px solid #f1f5f9; display: flex; justify-content: flex-end; gap: 1rem; align-items: center; }
        
        .btn-cancel { padding: 0.5rem 1rem; border: 1px solid #cbd5e1; background: white; color: #64748b; border-radius: 0.5rem; font-weight: 600; cursor: pointer; transition: all 0.2s; }
        .btn-cancel:hover { border-color: #ef4444; color: #ef4444; background: #fef2f2; }
        
        .total-label { font-size: 0.875rem; color: #64748b; margin-right: 0.5rem; }
        .total-amount { font-size: 1.25rem; font-weight: 700; color: #0f172a; }
    </style>
</head>
<body>
    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="currentPath" value="/orders" />
    </jsp:include>

    <div class="container">
        <h1 class="page-title">📦 Lịch sử mua hàng</h1>
        
        <c:if test="${not empty param.message}">
            <div style="background: #dcfce7; color: #166534; padding: 1rem; border-radius: 0.5rem; margin-bottom: 1.5rem; border: 1px solid #bbf7d0;">
                ${param.message}
            </div>
        </c:if>
        <c:if test="${not empty param.error}">
            <div style="background: #fee2e2; color: #991b1b; padding: 1rem; border-radius: 0.5rem; margin-bottom: 1.5rem; border: 1px solid #fecaca;">
                ${param.error}
            </div>
        </c:if>

        <c:choose>
            <c:when test="${empty orders}">
                <div style="text-align: center; padding: 4rem 0;">
                    <div style="font-size: 3rem; margin-bottom: 1rem;">🛒</div>
                    <h3>Bạn chưa có đơn hàng nào</h3>
                    <a href="${pageContext.request.contextPath}/products" style="color: #ea580c; font-weight: 600; text-decoration: none;">Đi chợ ngay &rarr;</a>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach var="order" items="${orders}">
                    <div class="order-item">
                        <div class="order-header">
                            <div class="order-meta">
                                <div>
                                    <span class="meta-label">Mã đơn hàng</span>
                                    <span class="meta-value">#${order.orderId}</span>
                                </div>
                                <div>
                                    <span class="meta-label">Ngày đặt</span>
                                    <span class="meta-value"><fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy"/></span>
                                </div>
                            </div>
                            <span class="order-status status-${order.status}">
                                <c:choose>
                                    <c:when test="${order.status == 'PENDING'}">Chờ xác nhận</c:when>
                                    <c:when test="${order.status == 'CONFIRMED'}">Đã xác nhận</c:when>
                                    <c:when test="${order.status == 'SHIPPING'}">Đang giao</c:when>
                                    <c:when test="${order.status == 'DELIVERED'}">Giao thành công</c:when>
                                    <c:when test="${order.status == 'CANCELLED'}">Đã hủy</c:when>
                                </c:choose>
                            </span>
                        </div>
                        
                        <div class="order-content">
                            <div class="product-list">
                                <c:forEach var="detail" items="${order.orderDetails}">
                                    <div class="product-item">
                                        <img src="${detail.product.imageUrl != null ? detail.product.imageUrl : 'https://placehold.co/80'}" class="product-img">
                                        <div class="product-details">
                                            <div class="product-name">${detail.product.name}</div>
                                            <div class="product-shop">🏪 ${detail.product.seller.shopName}</div>
                                            <div style="margin-top: 0.25rem;">
                                                <span class="product-price">
                                                    <fmt:formatNumber value="${detail.priceAtPurchase}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                                </span>
                                                <span style="color: #64748b; font-size: 0.875rem;"> x ${detail.quantity}</span>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                        
                        <div class="order-actions">
                            <div>
                                <span class="total-label">Tổng tiền:</span>
                                <span class="total-amount">
                                    <fmt:formatNumber value="${order.payment.amount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                </span>
                            </div>
                            
                            <c:if test="${order.status == 'PENDING'}">
                                <form action="${pageContext.request.contextPath}/order-cancel" method="POST" style="margin:0;" onsubmit="return confirm('Bạn có chắc chắn muốn hủy đơn hàng này?');">
                                    <input type="hidden" name="orderId" value="${order.orderId}">
                                    <button type="submit" class="btn-cancel">Hủy đơn hàng</button>
                                </form>
                            </c:if>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
    
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>