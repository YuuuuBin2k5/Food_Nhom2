<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Shipper Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>
    <jsp:include page="../common/sidebar.jsp" />
    
    <div class="main-content">
        <!-- Header Banner -->
        <div class="header-banner">
            <div class="container">
                <div>
                    <h1 class="page-title">
                        <span class="icon">🚚</span>
                        Shipper Dashboard
                    </h1>
                    <p class="page-subtitle">Quản lý và giao các đơn hàng</p>
                </div>
            </div>
        </div>

        <div class="container py-4">
            <!-- Stats Cards -->
            <div class="shipper-stats-grid mb-4">
                <div class="shipper-stat-card" onclick="filterOrders('AVAILABLE')">
                    <div class="shipper-stat-content">
                        <p class="shipper-stat-label">Đơn có sẵn</p>
                        <p class="shipper-stat-value">${availableOrders}</p>
                        <p class="shipper-stat-subtext text-blue">Có thể nhận ngay</p>
                    </div>
                    <div class="shipper-stat-icon bg-gradient-blue">
                        📦
                    </div>
                </div>
                
                <div class="shipper-stat-card" onclick="filterOrders('SHIPPING')">
                    <div class="shipper-stat-content">
                        <p class="shipper-stat-label">Đang giao</p>
                        <p class="shipper-stat-value">${shippingOrders}</p>
                        <p class="shipper-stat-subtext text-purple">Đơn của tôi</p>
                    </div>
                    <div class="shipper-stat-icon bg-gradient-purple">
                        🚚
                    </div>
                </div>
                
                <div class="shipper-stat-card" onclick="filterOrders('DELIVERED')">
                    <div class="shipper-stat-content">
                        <p class="shipper-stat-label">Đã giao</p>
                        <p class="shipper-stat-value">${deliveredOrders}</p>
                        <p class="shipper-stat-subtext text-green">Hoàn thành</p>
                    </div>
                    <div class="shipper-stat-icon bg-gradient-green">
                        ✅
                    </div>
                </div>
                
                <div class="shipper-stat-card">
                    <div class="shipper-stat-content">
                        <p class="shipper-stat-label">Thu nhập</p>
                        <p class="shipper-stat-value text-primary">
                            <fmt:formatNumber value="${totalEarnings}" type="number" maxFractionDigits="0" />₫
                        </p>
                    </div>
                    <div class="shipper-stat-icon bg-gradient-orange">
                        💰
                    </div>
                </div>
            </div>

            <!-- Status Filter -->
            <div class="status-filter mb-4">
                <button class="filter-btn active" data-status="AVAILABLE" onclick="filterOrders('AVAILABLE')">
                    Đơn có sẵn (${availableOrders})
                </button>
                <button class="filter-btn" data-status="SHIPPING" onclick="filterOrders('SHIPPING')">
                    Đang giao (${shippingOrders})
                </button>
                <button class="filter-btn" data-status="DELIVERED" onclick="filterOrders('DELIVERED')">
                    Đã giao (${deliveredOrders})
                </button>
            </div>

            <!-- Orders List -->
            <div id="ordersList">
                <c:choose>
                    <c:when test="${empty orders}">
                        <div class="empty-state">
                            <span class="empty-icon">📭</span>
                            <h3>Không có đơn hàng nào</h3>
                            <p>Chưa có đơn hàng nào có sẵn để nhận</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="space-y-4">
                            <c:forEach var="order" items="${orders}">
                                <div class="shipper-order-card" data-status="${order.status}">
                                    <!-- Order Header -->
                                    <div class="shipper-order-header">
                                        <div class="shipper-order-header-left">
                                            <div class="flex-between" style="gap: 1rem; flex-wrap: wrap;">
                                                <span class="shipper-order-id">#${order.orderId}</span>
                                                <c:choose>
                                                    <c:when test="${order.status == 'CONFIRMED'}">
                                                        <span class="shipper-badge shipper-badge-blue">
                                                            <span>📦</span> Chờ giao
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${order.status == 'SHIPPING'}">
                                                        <span class="shipper-badge shipper-badge-purple">
                                                            <span>🚚</span> Đang giao
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${order.status == 'DELIVERED'}">
                                                        <span class="shipper-badge shipper-badge-green">
                                                            <span>✅</span> Đã giao
                                                        </span>
                                                    </c:when>
                                                </c:choose>
                                            </div>
                                            <div class="shipper-order-meta">
                                                <span>📅 <fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm" /></span>
                                                <span>👤 ${order.buyer.fullName}</span>
                                            </div>
                                        </div>
                                        <div class="shipper-order-header-right">
                                            <span class="shipper-fee-label">Phí giao hàng</span>
                                            <span class="shipper-fee-value">15,000₫</span>
                                        </div>
                                    </div>
                                    
                                    <!-- Shipping Address -->
                                    <div class="shipper-address-section">
                                        <h4 class="shipper-address-title">
                                            <span>📍</span>
                                            Địa chỉ giao hàng
                                        </h4>
                                        <p class="shipper-address-text">${order.shippingAddress}</p>
                                    </div>
                                    
                                    <!-- Actions Footer -->
                                    <div class="shipper-order-footer">
                                        <button onclick="viewOrderDetail(${order.orderId})" class="shipper-btn-link">
                                            Xem chi tiết
                                            <svg style="width: 1rem; height: 1rem;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
                                            </svg>
                                        </button>
                                        
                                        <div style="display: flex; gap: 0.5rem;">
                                            <c:choose>
                                                <c:when test="${order.status == 'CONFIRMED'}">
                                                    <button onclick="acceptOrder(${order.orderId})" class="shipper-btn-accept">
                                                        ✋ Nhận đơn này
                                                    </button>
                                                </c:when>
                                                <c:when test="${order.status == 'SHIPPING'}">
                                                    <button onclick="completeOrder(${order.orderId})" class="shipper-btn-complete">
                                                        ✅ Đã giao
                                                    </button>
                                                </c:when>
                                            </c:choose>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- Order Detail Modal -->
    <div id="orderDetailModal" class="modal">
        <div class="modal-backdrop" onclick="closeModal('orderDetailModal')"></div>
        <div class="modal-content modal-lg shipper-modal">
            <div class="shipper-modal-header">
                <div>
                    <h3 class="shipper-modal-title" id="modalOrderId">Chi tiết đơn hàng</h3>
                    <p class="shipper-modal-date" id="modalOrderDate"></p>
                </div>
                <button onclick="closeModal('orderDetailModal')" class="shipper-modal-close">✕</button>
            </div>
            <div class="shipper-modal-body" id="orderDetailContent">
                <div class="loading-spinner">Đang tải...</div>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/main.js"></script>
    <script>
        const API_BASE = '${pageContext.request.contextPath}/api';
        let currentFilter = 'AVAILABLE';
        
        function filterOrders(status) {
            currentFilter = status;
            
            // Update active button
            document.querySelectorAll('.filter-btn').forEach(btn => {
                btn.classList.remove('active');
                if (btn.dataset.status === status) {
                    btn.classList.add('active');
                }
            });
            
            // Filter orders
            const orders = document.querySelectorAll('.shipper-order-card');
            let visibleCount = 0;
            
            orders.forEach(order => {
                const orderStatus = order.dataset.status;
                const shouldShow = (status === 'AVAILABLE' && orderStatus === 'CONFIRMED') ||
                                 (status === 'SHIPPING' && orderStatus === 'SHIPPING') ||
                                 (status === 'DELIVERED' && orderStatus === 'DELIVERED');
                
                if (shouldShow) {
                    order.style.display = 'block';
                    visibleCount++;
                } else {
                    order.style.display = 'none';
                }
            });
        }
        
        async function viewOrderDetail(orderId) {
            try {
                openModal('orderDetailModal');
                document.getElementById('orderDetailContent').innerHTML = '<div class="loading-spinner">Đang tải...</div>';
                
                const order = await apiRequest(API_BASE + '/orders/' + orderId);
                
                document.getElementById('modalOrderId').textContent = 'Chi tiết đơn hàng #' + order.orderId;
                document.getElementById('modalOrderDate').textContent = '📅 ' + formatDateTime(order.orderDate);
                
                let statusBadge = '';
                if (order.status === 'CONFIRMED') {
                    statusBadge = '<span class="shipper-badge shipper-badge-blue"><span>📦</span> Chờ giao</span>';
                } else if (order.status === 'SHIPPING') {
                    statusBadge = '<span class="shipper-badge shipper-badge-purple"><span>🚚</span> Đang giao</span>';
                } else if (order.status === 'DELIVERED') {
                    statusBadge = '<span class="shipper-badge shipper-badge-green"><span>✅</span> Đã giao</span>';
                }
                
                let itemsHtml = '';
                order.orderItems.forEach(item => {
                    itemsHtml += `
                        <div class="shipper-modal-item">
                            <div class="shipper-modal-item-icon">🍲</div>
                            <div class="shipper-modal-item-content">
                                <div class="shipper-modal-item-header">
                                    <h5 class="shipper-modal-item-name">\${item.product.name}</h5>
                                    <span class="shipper-modal-item-price">\${formatPrice(item.price * item.quantity)}</span>
                                </div>
                                <div class="shipper-modal-item-detail">
                                    Số lượng: <span class="fw-semibold">\${item.quantity}</span> x \${formatPrice(item.price)}
                                </div>
                            </div>
                        </div>
                    `;
                });
                
                const html = `
                    <div class="shipper-modal-grid">
                        <div class="shipper-modal-info-card">
                            <p class="shipper-modal-info-label">Khách hàng</p>
                            <p class="shipper-modal-info-value">👤 \${order.buyer.fullName}</p>
                        </div>
                        <div class="shipper-modal-info-card">
                            <p class="shipper-modal-info-label">Trạng thái</p>
                            \${statusBadge}
                        </div>
                    </div>
                    
                    <div class="shipper-modal-address">
                        <h4 class="shipper-modal-section-title">
                            <span>📍</span>
                            Địa chỉ giao hàng
                        </h4>
                        <p class="shipper-modal-address-text">\${order.shippingAddress}</p>
                    </div>
                    
                    <div class="shipper-modal-section">
                        <h4 class="shipper-modal-section-title">
                            <span>🛍️</span>
                            Danh sách sản phẩm
                        </h4>
                        <div class="shipper-modal-items">
                            \${itemsHtml}
                        </div>
                    </div>
                    
                    <div class="shipper-modal-total">
                        <div class="shipper-modal-total-row">
                            <span>Tổng tiền hàng</span>
                            <span class="shipper-modal-total-value">\${formatPrice(order.totalAmount)}</span>
                        </div>
                        <div class="shipper-modal-total-row">
                            <span>Phí giao hàng</span>
                            <span class="shipper-modal-total-value text-success">\${formatPrice(15000)}</span>
                        </div>
                    </div>
                    
                    <div class="shipper-modal-actions">
                        <button onclick="closeModal('orderDetailModal')" class="shipper-modal-btn-secondary">
                            Đóng
                        </button>
                        \${order.status === 'CONFIRMED' ? `
                            <button onclick="acceptOrder(\${order.orderId}); closeModal('orderDetailModal');" class="shipper-modal-btn-accept">
                                ✋ Nhận đơn này ngay
                            </button>
                        ` : ''}
                        \${order.status === 'SHIPPING' ? `
                            <button onclick="completeOrder(\${order.orderId}); closeModal('orderDetailModal');" class="shipper-modal-btn-complete">
                                ✅ Xác nhận đã giao
                            </button>
                        ` : ''}
                    </div>
                `;
                
                document.getElementById('orderDetailContent').innerHTML = html;
                
            } catch (error) {
                document.getElementById('orderDetailContent').innerHTML = 
                    '<div class="text-center text-danger">Lỗi tải chi tiết đơn hàng</div>';
            }
        }
        
        async function acceptOrder(orderId) {
            if (!confirm('Bạn có chắc muốn nhận đơn này và bắt đầu giao hàng?')) return;
            
            try {
                showLoading();
                await apiRequest(API_BASE + '/shipper/orders/' + orderId + '/accept', {
                    method: 'PATCH'
                });
                showToast('Nhận đơn thành công!', 'success');
                setTimeout(() => window.location.reload(), 1000);
            } catch (error) {
                showToast(error.message || 'Lỗi nhận đơn', 'error');
            } finally {
                hideLoading();
            }
        }
        
        async function completeOrder(orderId) {
            if (!confirm('Bạn có chắc đã giao hàng thành công?')) return;
            
            try {
                showLoading();
                await apiRequest(API_BASE + '/shipper/orders/' + orderId + '/complete', {
                    method: 'PATCH'
                });
                showToast('Xác nhận giao hàng thành công!', 'success');
                setTimeout(() => window.location.reload(), 1000);
            } catch (error) {
                showToast(error.message || 'Lỗi xác nhận giao hàng', 'error');
            } finally {
                hideLoading();
            }
        }
        
        function formatDateTime(dateStr) {
            const date = new Date(dateStr);
            const day = String(date.getDate()).padStart(2, '0');
            const month = String(date.getMonth() + 1).padStart(2, '0');
            const year = date.getFullYear();
            const hours = String(date.getHours()).padStart(2, '0');
            const minutes = String(date.getMinutes()).padStart(2, '0');
            return `\${day}/\${month}/\${year} \${hours}:\${minutes}`;
        }
        
        // Initialize filter on page load
        filterOrders('AVAILABLE');
    </script>
</body>
</html>
