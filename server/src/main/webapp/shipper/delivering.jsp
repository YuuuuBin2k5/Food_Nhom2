<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <title>Đang giao - Shipper</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/shipper-modern.css">
    <jsp:include page="../common/pwa-head.jsp"/>
</head>
<body class="shipper-modern">
    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="currentPath" value="/shipper/delivering"/>
    </jsp:include>

    <div class="shipper-container">
        <!-- Toast Messages -->
        <c:if test="${not empty sessionScope.successMessage}">
            <div class="toast-modern show success" id="successToast">
                <span class="toast-icon">✓</span>
                <span class="toast-message">${sessionScope.successMessage}</span>
            </div>
            <c:remove var="successMessage" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.errorMessage}">
            <div class="toast-modern show error" id="errorToast">
                <span class="toast-icon">✕</span>
                <span class="toast-message">${sessionScope.errorMessage}</span>
            </div>
            <c:remove var="errorMessage" scope="session"/>
        </c:if>

        <!-- Header -->
        <header class="shipper-header">
            <div class="shipper-header-content">
                <div>
                    <h1 class="shipper-title">Đang giao hàng</h1>
                    <div class="shipper-subtitle">
                        <span class="status-dot"></span>
                        <span class="status-text">Đơn hàng đang thực hiện</span>
                    </div>
                </div>
            </div>
        </header>

        <c:choose>
            <c:when test="${not empty currentOrder}">
                <!-- Active Order Card -->
                <div class="active-order-card">
                    <div class="active-order-header">
                        <div class="active-order-header-content">
                            <h2 class="active-order-id">#${currentOrder.orderId}</h2>
                            <span class="active-order-badge">
                                🚚 Đang giao
                            </span>
                        </div>
                    </div>
                    
                    <div class="active-order-body">
                        <!-- Customer Info -->
                        <div class="customer-info-grid">
                            <div class="customer-info-item">
                                <p class="customer-info-label">👤 Khách hàng</p>
                                <p class="customer-info-value">${currentOrder.buyer.fullName}</p>
                            </div>
                            <div class="customer-info-item">
                                <p class="customer-info-label">📞 Điện thoại</p>
                                <p class="customer-info-value">
                                    <c:choose>
                                        <c:when test="${not empty currentOrder.buyer.phoneNumber}">${currentOrder.buyer.phoneNumber}</c:when>
                                        <c:otherwise>Không có</c:otherwise>
                                    </c:choose>
                                </p>
                            </div>
                            <div class="customer-info-item">
                                <p class="customer-info-label">📅 Ngày đặt</p>
                                <p class="customer-info-value">
                                    <fmt:formatDate value="${currentOrder.orderDate}" pattern="dd/MM HH:mm"/>
                                </p>
                            </div>
                        </div>

                        <!-- Address -->
                        <div class="address-box-modern">
                            <p class="address-box-label">📍 Địa chỉ giao hàng</p>
                            <p class="address-box-value">${currentOrder.shippingAddress}</p>
                        </div>
                        
                        <!-- Products -->
                        <div class="products-section">
                            <p class="products-title">🛍️ Sản phẩm (${currentOrder.orderDetails.size()} món)</p>
                            <div class="products-list">
                                <c:forEach var="item" items="${currentOrder.orderDetails}">
                                    <div class="product-item">
                                        <span class="product-name">${item.product.name}</span>
                                        <span class="product-qty">x${item.quantity}</span>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                        
                        <!-- Actions -->
                        <div class="active-order-actions">
                            <a href="tel:${currentOrder.buyer.phoneNumber}" class="btn-action-modern btn-call-modern">
                                📞 Gọi khách
                            </a>
                            <a href="https://www.google.com/maps/dir/?api=1&destination=${currentOrder.shippingAddress}" 
                               target="_blank" class="btn-action-modern btn-map-modern">
                                🗺️ Chỉ đường
                            </a>
                            
                            <!-- Complete Order Form -->
                            <form action="${pageContext.request.contextPath}/shipper/action" method="post"
                                  onsubmit="return confirm('Xác nhận đã giao hàng thành công cho khách?');"
                                  style="grid-column: 1 / -1;">
                                <input type="hidden" name="action" value="complete">
                                <input type="hidden" name="orderId" value="${currentOrder.orderId}">
                                <button type="submit" class="btn-action-modern btn-complete-modern" style="width: 100%;">
                                    ✅ Đã giao xong
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <!-- Empty State -->
                <div class="empty-state-modern">
                    <div class="empty-icon-wrapper">
                        <div class="empty-icon-bg"></div>
                        <div class="empty-icon-circle">🚚</div>
                    </div>
                    <h3 class="empty-title">Chưa có đơn đang giao</h3>
                    <p class="empty-description">
                        Nhận đơn mới từ trang "Đơn có sẵn" để bắt đầu giao hàng.
                    </p>
                    <a href="${pageContext.request.contextPath}/shipper/orders" class="btn-scan-modern">
                        📦 Xem đơn có sẵn
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- Bottom Navigation (Mobile) -->
    <nav class="bottom-nav-modern">
        <a href="${pageContext.request.contextPath}/shipper/orders" class="nav-item-modern">
            <span class="nav-icon">📦</span>
            <span class="nav-label">Đơn hàng</span>
        </a>
        <a href="${pageContext.request.contextPath}/shipper/delivering" class="nav-item-modern active">
            <span class="nav-icon">🚚</span>
            <span class="nav-label">Đang giao</span>
        </a>
        <a href="${pageContext.request.contextPath}/shipper/history" class="nav-item-modern">
            <span class="nav-icon">📋</span>
            <span class="nav-label">Lịch sử</span>
        </a>
    </nav>

    <jsp:include page="../common/footer.jsp"/>
    <jsp:include page="../common/pwa-script.jsp"/>
    
    <script>
        // Auto hide toast after 3 seconds
        setTimeout(function() {
            var toasts = document.querySelectorAll('.toast-modern');
            toasts.forEach(function(toast) {
                toast.classList.remove('show');
            });
        }, 3000);
        
        // Sync container with sidebar state
        function syncSidebarState() {
            var sidebar = document.getElementById('sidebar');
            var container = document.querySelector('.shipper-container');
            if (sidebar && container) {
                if (sidebar.classList.contains('scrolled')) {
                    container.classList.add('sidebar-scrolled');
                } else {
                    container.classList.remove('sidebar-scrolled');
                }
            }
        }
        
        var sidebar = document.getElementById('sidebar');
        if (sidebar) {
            var observer = new MutationObserver(syncSidebarState);
            observer.observe(sidebar, { attributes: true, attributeFilter: ['class'] });
            syncSidebarState();
        }
    </script>
</body>
</html>
