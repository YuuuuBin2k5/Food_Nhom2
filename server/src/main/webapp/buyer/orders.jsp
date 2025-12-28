<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lịch sử đơn hàng - FreshSave</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <style>
        body {
            margin: 0;
            padding-top: 96px;
            background: linear-gradient(to bottom right, #fff7ed, #fef3c7, #fef9c3);
            min-height: 100vh;
            padding-bottom: 2.5rem;
        }
        
        .orders-header {
            background: linear-gradient(to right, #FF6B6B, #FF8E53, #FFC75F);
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            margin-bottom: 2rem;
        }
        
        .orders-container {
            max-width: 1280px;
            margin: 0 auto;
            padding: 0 1rem;
        }
        
        .info-notice {
            background: #dbeafe;
            border: 1px solid #93c5fd;
            border-radius: 0.75rem;
            padding: 1rem;
            margin-bottom: 1.5rem;
            display: flex;
            gap: 0.75rem;
        }
        
        .order-card {
            background: white;
            border-radius: 1rem;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
            border: 1px solid #f1f5f9;
            overflow: hidden;
            transition: all 0.3s;
            cursor: pointer;
            margin-bottom: 1.5rem;
        }
        
        .order-card:hover {
            box-shadow: 0 10px 15px rgba(0, 0, 0, 0.1);
            transform: translateY(-4px);
        }
        
        .order-header {
            padding: 1.5rem;
            background: linear-gradient(to right, #fff7ed, #fef3c7);
            border-bottom: 1px solid #f1f5f9;
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            gap: 1rem;
            flex-wrap: wrap;
        }
        
        .order-info {
            display: flex;
            flex-direction: column;
            gap: 0.5rem;
        }
        
        .order-id {
            font-weight: 700;
            color: #0f172a;
            font-size: 1.25rem;
            display: flex;
            align-items: center;
            gap: 0.75rem;
        }
        
        .shop-badge {
            padding: 0.25rem 0.75rem;
            background: white;
            border-radius: 9999px;
            font-size: 0.875rem;
            font-weight: 600;
            color: #FF6B6B;
            border: 1px solid #fed7aa;
            box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
        }
        
        .order-date {
            font-size: 0.875rem;
            color: #334155;
        }
        
        .order-total {
            display: flex;
            flex-direction: column;
            align-items: flex-end;
            gap: 0.25rem;
        }
        
        .total-label {
            font-size: 0.875rem;
            color: #334155;
        }
        
        .total-value {
            font-size: 1.5rem;
            font-weight: 700;
            color: #FF6B6B;
        }
        
        .order-body {
            padding: 1.5rem;
        }
        
        .order-items {
            display: flex;
            flex-direction: column;
            gap: 0.75rem;
        }
        
        .order-item {
            display: flex;
            gap: 1rem;
            padding: 0.75rem;
            border-radius: 0.75rem;
            background: #f8fafc;
            transition: background 0.2s;
        }
        
        .order-item:hover {
            background: #f1f5f9;
        }
        
        .item-image {
            width: 64px;
            height: 64px;
            border-radius: 0.75rem;
            object-fit: cover;
            background: #e2e8f0;
            border: 2px solid white;
            box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
        }
        
        .item-details {
            flex: 1;
            min-width: 0;
        }
        
        .item-name {
            font-weight: 600;
            color: #0f172a;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }
        
        .item-quantity {
            font-size: 0.875rem;
            color: #334155;
            margin-top: 0.25rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
        
        .item-price {
            font-weight: 600;
            color: #FF6B6B;
        }
        
        .more-items {
            font-size: 0.875rem;
            color: #334155;
            text-align: center;
            font-style: italic;
            margin-top: 0.75rem;
        }
        
        .order-footer {
            padding: 1rem 1.5rem;
            background: #f8fafc;
            border-top: 1px solid #f1f5f9;
            display: flex;
            justify-content: flex-end;
        }
        
        .view-detail-btn {
            color: #FF6B6B;
            font-weight: 600;
            font-size: 0.875rem;
            display: flex;
            align-items: center;
            gap: 0.25rem;
            background: none;
            border: none;
            cursor: pointer;
            transition: all 0.2s;
        }
        
        .view-detail-btn:hover {
            text-decoration: underline;
        }
        
        .status-badge {
            padding: 0.375rem 0.75rem;
            border-radius: 9999px;
            font-size: 0.875rem;
            font-weight: 600;
        }
        
        .status-pending { background: #fef3c7; color: #92400e; }
        .status-confirmed { background: #dbeafe; color: #1e40af; }
        .status-shipping { background: #e0e7ff; color: #4338ca; }
        .status-delivered { background: #d1fae5; color: #065f46; }
        .status-cancelled { background: #fee2e2; color: #991b1b; }
        
        .empty-state {
            background: white;
            border-radius: 1.5rem;
            box-shadow: 0 10px 15px rgba(0, 0, 0, 0.1);
            padding: 3rem;
            text-align: center;
            max-width: 32rem;
            margin: 0 auto;
        }
        
        .empty-icon {
            width: 8rem;
            height: 8rem;
            margin: 0 auto 1.5rem;
            background: linear-gradient(to bottom right, #fed7aa, #fde68a);
            border-radius: 9999px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 4rem;
        }
        
        /* Modal */
        .modal-overlay {
            position: fixed;
            inset: 0;
            z-index: 1000;
            background: rgba(0, 0, 0, 0.6);
            backdrop-filter: blur(4px);
            display: none;
            align-items: center;
            justify-content: center;
            padding: 1rem;
        }
        
        .modal-overlay.show {
            display: flex;
        }
        
        .modal-content {
            background: white;
            border-radius: 1rem;
            box-shadow: 0 20px 25px rgba(0, 0, 0, 0.2);
            width: 100%;
            max-width: 48rem;
            max-height: 90vh;
            display: flex;
            flex-direction: column;
            animation: modalIn 0.2s ease;
        }
        
        @keyframes modalIn {
            from {
                opacity: 0;
                transform: scale(0.95);
            }
            to {
                opacity: 1;
                transform: scale(1);
            }
        }
        
        .modal-header {
            padding: 1.5rem;
            border-bottom: 1px solid #f1f5f9;
            background: linear-gradient(to right, #fff7ed, #fef3c7);
            border-radius: 1rem 1rem 0 0;
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
        }
        
        .modal-body {
            padding: 1.5rem;
            overflow-y: auto;
        }
        
        .modal-footer {
            padding: 1.5rem;
            border-top: 1px solid #f1f5f9;
            background: #f8fafc;
            border-radius: 0 0 1rem 1rem;
            display: flex;
            justify-content: flex-end;
            gap: 0.75rem;
        }
        
        .btn {
            padding: 0.75rem 1.5rem;
            border-radius: 0.75rem;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.2s;
            border: none;
        }
        
        .btn-close {
            background: white;
            color: #334155;
            border: 2px solid #e2e8f0;
        }
        
        .btn-close:hover {
            background: #f8fafc;
        }
        
        .btn-cancel {
            background: #fef2f2;
            color: #dc2626;
            border: 2px solid #fecaca;
        }
        
        .btn-cancel:hover {
            background: #fee2e2;
        }
    </style>
</head>
<body>
    <!-- Include Sidebar -->
    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="currentPath" value="/orders" />
    </jsp:include>
    
    <!-- Header -->
    <div class="orders-header">
        <div style="max-width: 1280px; margin: 0 auto; padding: 2rem 1rem;">
            <h1 style="font-size: 1.875rem; font-weight: 700; color: white; display: flex; align-items: center; gap: 0.75rem;">
                <span style="font-size: 2.25rem;">📦</span>
                Lịch sử mua hàng
            </h1>
            <p style="color: rgba(255, 255, 255, 0.9); font-size: 1rem; margin-top: 0.5rem;">
                Quản lý và theo dõi các đơn hàng của bạn
            </p>
        </div>
    </div>
    
    <main class="orders-container">
        <c:choose>
            <c:when test="${empty orders}">
                <!-- Empty State -->
                <div class="empty-state">
                    <div class="empty-icon">🛒</div>
                    <h2 style="font-size: 1.5rem; font-weight: 700; color: #0f172a; margin-bottom: 0.5rem;">
                        Bạn chưa có đơn hàng nào
                    </h2>
                    <p style="color: #334155; margin-bottom: 2rem;">
                        Hãy dạo một vòng chợ và giải cứu những món ngon nhé!
                    </p>
                    <a href="${pageContext.request.contextPath}/products" style="display: inline-block; padding: 1rem 2rem; background: linear-gradient(to right, #FF6B6B, #FF8E53, #FFC75F); color: white; font-weight: 600; border-radius: 0.75rem; text-decoration: none; box-shadow: 0 4px 6px rgba(255, 107, 107, 0.3);">
                        🛍️ Khám phá ngay
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <!-- Info Notice -->
                <div class="info-notice">
                    <span style="font-size: 1.5rem;">ℹ️</span>
                    <div style="flex: 1;">
                        <p style="font-size: 0.875rem; color: #1e40af; font-weight: 500;">
                            Mỗi đơn hàng chỉ chứa sản phẩm từ một cửa hàng
                        </p>
                        <p style="font-size: 0.75rem; color: #1e3a8a; margin-top: 0.25rem;">
                            Nếu bạn mua sản phẩm từ nhiều cửa hàng khác nhau, chúng sẽ được tách thành các đơn hàng riêng biệt để giao hàng nhanh hơn.
                        </p>
                    </div>
                </div>
                
                <!-- Orders List -->
                <div>
                    <c:forEach items="${orders}" var="order">
                        <div class="order-card" onclick="showOrderDetail(${order.orderId})">
                            <!-- Order Header -->
                            <div class="order-header">
                                <div class="order-info">
                                    <div class="order-id">
                                        <span>#${order.orderId}</span>
                                        <span class="status-badge status-${fn:toLowerCase(order.status)}">
                                            <c:choose>
                                                <c:when test="${order.status == 'PENDING'}">Chờ xác nhận</c:when>
                                                <c:when test="${order.status == 'CONFIRMED'}">Đã xác nhận</c:when>
                                                <c:when test="${order.status == 'SHIPPING'}">Đang giao</c:when>
                                                <c:when test="${order.status == 'DELIVERED'}">Đã giao</c:when>
                                                <c:when test="${order.status == 'CANCELLED'}">Đã hủy</c:when>
                                                <c:otherwise>${order.status}</c:otherwise>
                                            </c:choose>
                                        </span>
                                    </div>
                                    <div class="order-date">
                                        📅 <fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm"/>
                                    </div>
                                    <c:if test="${not empty order.items && fn:length(order.items) > 0}">
                                        <div class="shop-badge">
                                            🏪 ${order.items[0].shopName}
                                        </div>
                                    </c:if>
                                </div>
                                <div class="order-total">
                                    <span class="total-label">Tổng tiền</span>
                                    <span class="total-value">
                                        <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                    </span>
                                </div>
                            </div>
                            
                            <!-- Order Body -->
                            <div class="order-body">
                                <div class="order-items">
                                    <c:forEach items="${order.items}" var="item" begin="0" end="1">
                                        <div class="order-item">
                                            <img 
                                                src="${item.imageUrl != null ? item.imageUrl : 'https://placehold.co/64x64/FF6B6B/FFFFFF?text=Food'}" 
                                                alt="${item.name}"
                                                class="item-image"
                                            >
                                            <div class="item-details">
                                                <div class="item-name">${item.name}</div>
                                                <div class="item-quantity">
                                                    <span style="font-weight: 500;">x${item.quantity}</span>
                                                    <span>•</span>
                                                    <span class="item-price">
                                                        <fmt:formatNumber value="${item.price}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                                    </span>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                                <c:if test="${fn:length(order.items) > 2}">
                                    <p class="more-items">
                                        ...và ${fn:length(order.items) - 2} sản phẩm khác từ ${order.items[0].shopName}
                                    </p>
                                </c:if>
                            </div>
                            
                            <!-- Order Footer -->
                            <div class="order-footer">
                                <button class="view-detail-btn" onclick="event.stopPropagation(); showOrderDetail(${order.orderId})">
                                    Xem chi tiết
                                    <svg style="width: 1rem; height: 1rem;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
                                    </svg>
                                </button>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </main>
    
    <!-- Order Detail Modal -->
    <div class="modal-overlay" id="orderModal" onclick="closeModal(event)">
        <div class="modal-content" onclick="event.stopPropagation()">
            <div class="modal-header">
                <div>
                    <h3 style="font-size: 1.5rem; font-weight: 700; color: #0f172a;" id="modalTitle">
                        Chi tiết đơn hàng
                    </h3>
                    <p style="font-size: 0.875rem; color: #334155; margin-top: 0.25rem;" id="modalDate"></p>
                </div>
                <button onclick="closeModal()" style="width: 2.5rem; height: 2.5rem; display: flex; align-items: center; justify-content: center; border-radius: 9999px; background: white; border: none; color: #6b7280; cursor: pointer; transition: background 0.2s;">
                    ✕
                </button>
            </div>
            <div class="modal-body" id="modalBody">
                <!-- Content will be loaded dynamically -->
            </div>
            <div class="modal-footer">
                <button class="btn btn-close" onclick="closeModal()">Đóng</button>
                <button class="btn btn-cancel" id="cancelBtn" style="display: none;" onclick="cancelOrder()">
                    ❌ Hủy đơn hàng
                </button>
            </div>
        </div>
    </div>
    
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
    <script>
        let currentOrderId = null;
        
        function showOrderDetail(orderId) {
            currentOrderId = orderId;
            Loading.show();
            
            API.get('${pageContext.request.contextPath}/api/buyer/orders/' + orderId)
            .then(order => {
                Loading.hide();
                displayOrderDetail(order);
                document.getElementById('orderModal').classList.add('show');
            })
            .catch(error => {
                Loading.hide();
                Toast.error('Không thể tải chi tiết đơn hàng');
            });
        }
        
        function displayOrderDetail(order) {
            document.getElementById('modalTitle').textContent = 'Chi tiết đơn hàng #' + order.orderId;
            document.getElementById('modalDate').textContent = '📅 ' + formatDate(order.orderDate);
            
            const shopName = order.items && order.items.length > 0 ? order.items[0].shopName : 'Cửa hàng';
            
            let html = '<div style="display: flex; flex-direction: column; gap: 1.5rem;">';
            
            // Status
            html += '<div style="display: flex; justify-content: space-between; align-items: center; padding: 1.25rem; background: linear-gradient(to right, #fff7ed, #fef3c7); border-radius: 0.75rem; border: 1px solid #fed7aa;">';
            html += '<span style="font-size: 0.875rem; font-weight: 700; color: #0f172a; text-transform: uppercase; letter-spacing: 0.05em;">Trạng thái đơn hàng</span>';
            html += '<span class="status-badge status-' + order.status.toLowerCase() + '">' + getStatusText(order.status) + '</span>';
            html += '</div>';
            
            // Shop & Address
            html += '<div style="display: grid; grid-template-columns: 1fr; gap: 1.5rem;">';
            html += '<div><h4 style="font-size: 0.875rem; font-weight: 700; color: #0f172a; margin-bottom: 0.75rem; display: flex; align-items: center; gap: 0.5rem;"><span>🏪</span>Cửa hàng</h4>';
            html += '<p style="color: #334155; font-size: 0.875rem; line-height: 1.6; background: #f8fafc; padding: 1rem; border-radius: 0.75rem; border: 1px solid #f1f5f9;">' + shopName + '</p></div>';
            html += '<div><h4 style="font-size: 0.875rem; font-weight: 700; color: #0f172a; margin-bottom: 0.75rem; display: flex; align-items: center; gap: 0.5rem;"><span>📍</span>Địa chỉ nhận hàng</h4>';
            html += '<p style="color: #334155; font-size: 0.875rem; line-height: 1.6; background: #f8fafc; padding: 1rem; border-radius: 0.75rem; border: 1px solid #f1f5f9;">' + order.shippingAddress + '</p></div>';
            html += '</div>';
            
            // Items
            html += '<div><h4 style="font-size: 0.875rem; font-weight: 700; color: #0f172a; margin-bottom: 1rem; display: flex; align-items: center; gap: 0.5rem;"><span>🛍️</span>Danh sách sản phẩm từ ' + shopName + '</h4>';
            html += '<div style="display: flex; flex-direction: column; gap: 0.75rem;">';
            
            order.items.forEach(item => {
                html += '<div style="display: flex; gap: 1rem; padding: 1rem; border: 1px solid #f1f5f9; border-radius: 0.75rem; background: white; box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05); transition: border-color 0.2s;">';
                html += '<img src="' + (item.imageUrl || 'https://placehold.co/64x64/FF6B6B/FFFFFF?text=Food') + '" style="width: 80px; height: 80px; border-radius: 0.75rem; object-fit: cover; background: #f1f5f9; border: 2px solid white; box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);">';
                html += '<div style="flex: 1;"><div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 0.5rem;">';
                html += '<h5 style="font-weight: 700; color: #0f172a; font-size: 1rem;">' + item.name + '</h5>';
                html += '<span style="font-weight: 700; color: #FF6B6B; font-size: 1.125rem;">' + formatPrice(item.price * item.quantity) + '</span>';
                html += '</div><div style="font-size: 0.875rem; color: #334155;">Số lượng: <span style="font-weight: 600;">' + item.quantity + '</span> x ' + formatPrice(item.price) + '</div></div>';
                html += '</div>';
            });
            
            html += '</div></div>';
            
            // Total
            html += '<div style="padding-top: 1.5rem; border-top: 2px solid #f1f5f9; display: flex; justify-content: space-between; align-items: center;">';
            html += '<span style="font-size: 1.25rem; font-weight: 700; color: #0f172a;">Tổng cộng</span>';
            html += '<span style="font-size: 1.875rem; font-weight: 700; color: #FF6B6B;">' + formatPrice(order.totalAmount) + '</span>';
            html += '</div>';
            
            html += '</div>';
            
            document.getElementById('modalBody').innerHTML = html;
            
            // Show cancel button if pending
            const cancelBtn = document.getElementById('cancelBtn');
            if (order.status === 'PENDING') {
                cancelBtn.style.display = 'block';
            } else {
                cancelBtn.style.display = 'none';
            }
        }
        
        function getStatusText(status) {
            const statusMap = {
                'PENDING': 'Chờ xác nhận',
                'CONFIRMED': 'Đã xác nhận',
                'SHIPPING': 'Đang giao',
                'DELIVERED': 'Đã giao',
                'CANCELLED': 'Đã hủy'
            };
            return statusMap[status] || status;
        }
        
        function closeModal(event) {
            if (!event || event.target.id === 'orderModal') {
                document.getElementById('orderModal').classList.remove('show');
                currentOrderId = null;
            }
        }
        
        function cancelOrder() {
            if (!currentOrderId) return;
            
            if (!confirm('Bạn có chắc chắn muốn hủy đơn hàng này?')) {
                return;
            }
            
            Loading.show();
            
            API.put('${pageContext.request.contextPath}/api/buyer/orders/' + currentOrderId + '/cancel')
            .then(data => {
                Loading.hide();
                Toast.success('Đã hủy đơn hàng thành công');
                closeModal();
                setTimeout(() => {
                    window.location.reload();
                }, 1000);
            })
            .catch(error => {
                Loading.hide();
                Toast.error('Không thể hủy đơn hàng');
            });
        }
    </script>
</body>
</html>
