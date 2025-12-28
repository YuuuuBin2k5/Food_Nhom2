<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý đơn hàng - Seller Portal</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <style>
        body { margin: 0; padding-top: 96px; background: #f8fafc; min-height: 100vh; }
        .main-content { max-width: 1400px; margin: 0 auto; padding: 2rem; }
        
        .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem; }
        .page-title { font-size: 1.875rem; font-weight: 700; color: #1e293b; }
        
        .order-card { background: white; border-radius: 1rem; box-shadow: 0 1px 3px rgba(0,0,0,0.1); margin-bottom: 1.5rem; overflow: hidden; border: 1px solid #e2e8f0; }
        .order-header { background: #f8fafc; padding: 1rem 1.5rem; border-bottom: 1px solid #e2e8f0; display: flex; justify-content: space-between; align-items: center; }
        .order-id { font-weight: 700; color: #0f172a; }
        .order-date { font-size: 0.875rem; color: #64748b; }
        
        .order-body { padding: 1.5rem; }
        .item-row { display: flex; align-items: center; gap: 1rem; padding: 0.75rem 0; border-bottom: 1px solid #f1f5f9; }
        .item-row:last-child { border-bottom: none; }
        .item-img { width: 50px; height: 50px; border-radius: 0.5rem; object-fit: cover; background: #eee; }
        .item-info { flex: 1; }
        .item-name { font-weight: 600; color: #334155; }
        .item-meta { font-size: 0.875rem; color: #64748b; }
        
        .order-footer { padding: 1rem 1.5rem; background: #fff; border-top: 1px solid #e2e8f0; display: flex; justify-content: space-between; align-items: center; }
        .total-price { font-size: 1.25rem; font-weight: 700; color: #ef4444; }
        
        .badge { padding: 0.25rem 0.75rem; border-radius: 9999px; font-size: 0.875rem; font-weight: 600; }
        .badge-pending { background: #fef3c7; color: #d97706; }
        .badge-confirmed { background: #dbeafe; color: #2563eb; }
        .badge-shipping { background: #f3e8ff; color: #9333ea; }
        .badge-delivered { background: #dcfce7; color: #16a34a; }
        .badge-cancelled { background: #fee2e2; color: #dc2626; }
        
        .btn-action { padding: 0.5rem 1rem; border-radius: 0.5rem; font-weight: 600; cursor: pointer; border: none; transition: all 0.2s; display: inline-flex; align-items: center; gap: 0.5rem; }
        .btn-approve { background: #10b981; color: white; }
        .btn-approve:hover { background: #059669; }
        .btn-reject { background: white; border: 1px solid #ef4444; color: #ef4444; }
        .btn-reject:hover { background: #fee2e2; }
        .btn-ship { background: #8b5cf6; color: white; }
        .btn-ship:hover { background: #7c3aed; }
        
        .empty-state { text-align: center; padding: 4rem 0; color: #64748b; }
    </style>
</head>
<body>
    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="currentPath" value="/seller/orders" />
    </jsp:include>

    <div class="main-content">
        <div class="page-header">
            <h1 class="page-title">📋 Quản lý Đơn hàng</h1>
        </div>

        <c:choose>
            <c:when test="${empty orders}">
                <div class="empty-state">
                    <div style="font-size: 4rem; margin-bottom: 1rem;">📭</div>
                    <h3>Chưa có đơn hàng nào</h3>
                    <p>Các đơn hàng mới sẽ xuất hiện tại đây</p>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach var="order" items="${orders}">
                    <div class="order-card" id="order-${order.orderId}">
                        <div class="order-header">
                            <div>
                                <span class="order-id">#${order.orderId}</span>
                                <span class="order-date"> • <fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm"/></span>
                            </div>
                            <span class="badge badge-${order.status.toString().toLowerCase()}">
                                ${order.status}
                            </span>
                        </div>
                        
                        <div class="order-body">
                            <div style="margin-bottom: 1rem; font-size: 0.9rem; color: #475569;">
                                <strong>Khách hàng:</strong> ${order.buyer.fullName} <br>
                                <strong>Địa chỉ:</strong> ${order.shippingAddress}
                            </div>
                            
                            <c:forEach var="detail" items="${order.orderDetails}">
                                <c:if test="${detail.product.seller.userId == sessionScope.user.userId}">
                                    <div class="item-row">
                                        <img src="${detail.product.imageUrl != null ? detail.product.imageUrl : 'https://placehold.co/50'}" class="item-img">
                                        <div class="item-info">
                                            <div class="item-name">${detail.product.name}</div>
                                            <div class="item-meta">
                                                <fmt:formatNumber value="${detail.priceAtPurchase}" type="currency" currencySymbol="₫" maxFractionDigits="0"/> 
                                                x ${detail.quantity}
                                            </div>
                                        </div>
                                        <div style="font-weight: 600;">
                                            <fmt:formatNumber value="${detail.priceAtPurchase * detail.quantity}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                        </div>
                                    </div>
                                </c:if>
                            </c:forEach>
                        </div>
                        
                        <div class="order-footer">
                            <div class="total-price">
                                Tổng: <fmt:formatNumber value="${order.payment.amount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                            </div>
                            
                            <div class="actions">
                                <c:if test="${order.status == 'PENDING'}">
                                    <button class="btn-action btn-reject" onclick="updateStatus(${order.orderId}, 'CANCELLED')">❌ Từ chối</button>
                                    <button class="btn-action btn-approve" onclick="updateStatus(${order.orderId}, 'CONFIRMED')">✅ Duyệt đơn</button>
                                </c:if>
                                <c:if test="${order.status == 'CONFIRMED'}">
                                    <button class="btn-action btn-ship" onclick="updateStatus(${order.orderId}, 'SHIPPING')">🚚 Giao hàng</button>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>

    <script src="${pageContext.request.contextPath}/js/main.js"></script>
    <script>
        async function updateStatus(orderId, status) {
            if (!confirm('Bạn có chắc chắn muốn cập nhật trạng thái đơn hàng này?')) return;
            
            try {
                // Gọi API PUT được định nghĩa trong SellerOrderServlet
                const response = await fetch('${pageContext.request.contextPath}/api/seller/orders/' + orderId, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ status: status })
                });
                
                const data = await response.json();
                
                if (data.success) {
                    // Reload lại trang để cập nhật giao diện
                    window.location.reload();
                } else {
                    alert('Lỗi: ' + data.message);
                }
            } catch (error) {
                console.error('Error:', error);
                alert('Có lỗi xảy ra khi cập nhật đơn hàng');
            }
        }
    </script>
</body>
</html>