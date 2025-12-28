<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Đơn hàng - Seller</title>
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
                        <span class="icon">🏪</span>
                        Quản lý Đơn hàng
                    </h1>
                    <p class="page-subtitle">Theo dõi và xử lý các đơn hàng từ khách hàng</p>
                </div>
            </div>
        </div>

        <div class="container py-4">
            <!-- Status Filter -->
            <div class="status-filter mb-4">
                <button class="filter-btn active" data-status="ALL" onclick="filterOrders('ALL')">
                    Tất cả (${orders.size()})
                </button>
                <button class="filter-btn" data-status="PENDING" onclick="filterOrders('PENDING')">
                    ⏳ Chờ xác nhận
                </button>
                <button class="filter-btn" data-status="CONFIRMED" onclick="filterOrders('CONFIRMED')">
                    👨‍🍳 Đã xác nhận
                </button>
                <button class="filter-btn" data-status="SHIPPING" onclick="filterOrders('SHIPPING')">
                    🚚 Đang giao
                </button>
                <button class="filter-btn" data-status="DELIVERED" onclick="filterOrders('DELIVERED')">
                    ✅ Đã giao
                </button>
                <button class="filter-btn" data-status="CANCELLED" onclick="filterOrders('CANCELLED')">
                    ❌ Đã hủy
                </button>
            </div>

            <c:choose>
                <c:when test="${empty orders}">
                    <div class="empty-state">
                        <span class="empty-icon">📭</span>
                        <h3>Không có đơn hàng nào</h3>
                        <p>Chưa có đơn hàng nào trong hệ thống</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <!-- Orders List -->
                    <div id="ordersList" class="space-y-4">
                        <c:forEach var="order" items="${orders}">
                            <div class="order-card" data-status="${order.status}">
                                <div class="order-header">
                                    <div class="flex-between">
                                        <div>
                                            <h3 class="order-id">Đơn hàng #${order.orderId}</h3>
                                            <p class="order-date">
                                                <fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm" />
                                            </p>
                                        </div>
                                        <c:choose>
                                            <c:when test="${order.status == 'PENDING'}">
                                                <span class="badge badge-warning">⏳ Chờ xác nhận</span>
                                            </c:when>
                                            <c:when test="${order.status == 'CONFIRMED'}">
                                                <span class="badge badge-info">👨‍🍳 Đã xác nhận</span>
                                            </c:when>
                                            <c:when test="${order.status == 'SHIPPING'}">
                                                <span class="badge badge-primary">🚚 Đang giao</span>
                                            </c:when>
                                            <c:when test="${order.status == 'DELIVERED'}">
                                                <span class="badge badge-success">✅ Đã giao</span>
                                            </c:when>
                                            <c:when test="${order.status == 'CANCELLED'}">
                                                <span class="badge badge-danger">❌ Đã hủy</span>
                                            </c:when>
                                        </c:choose>
                                    </div>
                                </div>
                                
                                <div class="order-body">
                                    <div class="order-info">
                                        <div class="info-item">
                                            <span class="info-label">👤 Khách hàng:</span>
                                            <span class="info-value">${order.buyer.fullName}</span>
                                        </div>
                                        <div class="info-item">
                                            <span class="info-label">📞 SĐT:</span>
                                            <span class="info-value">${order.shippingPhone}</span>
                                        </div>
                                        <div class="info-item">
                                            <span class="info-label">📍 Địa chỉ:</span>
                                            <span class="info-value">${order.shippingAddress}</span>
                                        </div>
                                        <div class="info-item">
                                            <span class="info-label">💰 Tổng tiền:</span>
                                            <span class="info-value text-primary fw-bold">
                                                <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="₫" />
                                            </span>
                                        </div>
                                    </div>
                                    
                                    <!-- Order Items -->
                                    <div class="order-items mt-3">
                                        <h4 class="text-sm fw-bold mb-2">Sản phẩm:</h4>
                                        <c:forEach var="item" items="${order.orderItems}">
                                            <div class="order-item">
                                                <span>${item.product.name}</span>
                                                <span class="text-muted">x${item.quantity}</span>
                                                <span class="text-primary">
                                                    <fmt:formatNumber value="${item.price}" type="currency" currencySymbol="₫" />
                                                </span>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>
                                
                                <div class="order-footer">
                                    <button onclick="viewOrderDetail(${order.orderId})" class="btn btn-outline">
                                        👁️ Xem chi tiết
                                    </button>
                                    
                                    <c:if test="${order.status == 'PENDING'}">
                                        <button onclick="updateOrderStatus(${order.orderId}, 'CONFIRMED')" 
                                                class="btn btn-success">
                                            ✅ Xác nhận đơn
                                        </button>
                                        <button onclick="updateOrderStatus(${order.orderId}, 'CANCELLED')" 
                                                class="btn btn-danger">
                                            ❌ Hủy đơn
                                        </button>
                                    </c:if>
                                    
                                    <c:if test="${order.status == 'CONFIRMED'}">
                                        <button onclick="updateOrderStatus(${order.orderId}, 'SHIPPING')" 
                                                class="btn btn-primary">
                                            🚚 Bắt đầu giao
                                        </button>
                                    </c:if>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- Order Detail Modal -->
    <div id="orderDetailModal" class="modal">
        <div class="modal-content modal-lg">
            <div class="modal-header">
                <h3>Chi tiết đơn hàng</h3>
                <button onclick="closeModal('orderDetailModal')" class="btn-close">&times;</button>
            </div>
            <div class="modal-body" id="orderDetailContent">
                <div class="loading-spinner">Đang tải...</div>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/main.js"></script>
    <script>
        const API_BASE = '${pageContext.request.contextPath}/api';
        let currentFilter = 'ALL';
        
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
            const orders = document.querySelectorAll('.order-card');
            let visibleCount = 0;
            
            orders.forEach(order => {
                if (status === 'ALL' || order.dataset.status === status) {
                    order.style.display = 'block';
                    visibleCount++;
                } else {
                    order.style.display = 'none';
                }
            });
            
            // Show empty state if no orders
            const emptyState = document.querySelector('.empty-state');
            if (visibleCount === 0 && !emptyState) {
                const ordersList = document.getElementById('ordersList');
                ordersList.innerHTML = '<div class="empty-state"><span class="empty-icon">📭</span><h3>Không có đơn hàng nào</h3></div>';
            }
        }
        
        async function viewOrderDetail(orderId) {
            try {
                openModal('orderDetailModal');
                document.getElementById('orderDetailContent').innerHTML = '<div class="loading-spinner">Đang tải...</div>';
                
                const order = await apiRequest(API_BASE + '/orders/' + orderId);
                
                let itemsHtml = '';
                order.orderItems.forEach(item => {
                    itemsHtml += `
                        <div class="order-item">
                            <div>
                                <div class="fw-bold">\${item.product.name}</div>
                                <div class="text-muted text-sm">Số lượng: \${item.quantity}</div>
                            </div>
                            <div class="text-primary fw-bold">\${formatPrice(item.price)}</div>
                        </div>
                    `;
                });
                
                const html = `
                    <div class="order-detail">
                        <div class="detail-section">
                            <h4>Thông tin đơn hàng</h4>
                            <div class="info-grid">
                                <div class="info-item">
                                    <span class="info-label">Mã đơn:</span>
                                    <span class="info-value">#\${order.orderId}</span>
                                </div>
                                <div class="info-item">
                                    <span class="info-label">Ngày đặt:</span>
                                    <span class="info-value">\${formatDateTime(order.orderDate)}</span>
                                </div>
                                <div class="info-item">
                                    <span class="info-label">Trạng thái:</span>
                                    <span class="info-value">\${getStatusBadge(order.status)}</span>
                                </div>
                            </div>
                        </div>
                        
                        <div class="detail-section">
                            <h4>Thông tin khách hàng</h4>
                            <div class="info-grid">
                                <div class="info-item">
                                    <span class="info-label">Họ tên:</span>
                                    <span class="info-value">\${order.buyer.fullName}</span>
                                </div>
                                <div class="info-item">
                                    <span class="info-label">SĐT:</span>
                                    <span class="info-value">\${order.shippingPhone}</span>
                                </div>
                                <div class="info-item">
                                    <span class="info-label">Địa chỉ:</span>
                                    <span class="info-value">\${order.shippingAddress}</span>
                                </div>
                            </div>
                        </div>
                        
                        <div class="detail-section">
                            <h4>Sản phẩm</h4>
                            <div class="order-items">
                                \${itemsHtml}
                            </div>
                        </div>
                        
                        <div class="detail-section">
                            <div class="total-amount">
                                <span>Tổng cộng:</span>
                                <span class="text-primary fw-bold">\${formatPrice(order.totalAmount)}</span>
                            </div>
                        </div>
                    </div>
                `;
                
                document.getElementById('orderDetailContent').innerHTML = html;
                
            } catch (error) {
                document.getElementById('orderDetailContent').innerHTML = 
                    '<div class="text-center text-danger">Lỗi tải chi tiết đơn hàng</div>';
            }
        }
        
        async function updateOrderStatus(orderId, newStatus) {
            const actionText = newStatus === 'CONFIRMED' ? 'xác nhận' : 
                             newStatus === 'SHIPPING' ? 'bắt đầu giao' : 'hủy';
            
            if (!confirm(`Bạn có chắc muốn ${actionText} đơn hàng #${orderId}?`)) return;
            
            try {
                showLoading();
                await apiRequest(API_BASE + '/seller/orders/' + orderId + '/status', {
                    method: 'PATCH',
                    body: JSON.stringify({ status: newStatus })
                });
                showToast('Cập nhật trạng thái thành công!', 'success');
                setTimeout(() => window.location.reload(), 1000);
            } catch (error) {
                showToast(error.message || 'Lỗi cập nhật trạng thái', 'error');
            } finally {
                hideLoading();
            }
        }
        
        function getStatusBadge(status) {
            const badges = {
                'PENDING': '<span class="badge badge-warning">⏳ Chờ xác nhận</span>',
                'CONFIRMED': '<span class="badge badge-info">👨‍🍳 Đã xác nhận</span>',
                'SHIPPING': '<span class="badge badge-primary">🚚 Đang giao</span>',
                'DELIVERED': '<span class="badge badge-success">✅ Đã giao</span>',
                'CANCELLED': '<span class="badge badge-danger">❌ Đã hủy</span>'
            };
            return badges[status] || status;
        }
    </script>
</body>
</html>
