<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <title>Đơn có sẵn - Shipper</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/shipper-modern.css">
</head>
<body class="shipper-modern">
    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="currentPath" value="/shipper/orders"/>
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
                    <h1 class="shipper-title">Sẵn sàng giao hàng</h1>
                    <div class="shipper-subtitle">
                        <span class="status-dot"></span>
                        <span class="status-text">Online • Đang hoạt động</span>
                    </div>
                </div>
                <a href="${pageContext.request.contextPath}/shipper/orders" class="btn-refresh-modern">🔄</a>
            </div>
        </header>

        <!-- Stats Cards -->
        <div class="stats-grid">
            <div class="stat-card">
                <span class="stat-card-icon">📦</span>
                <p class="stat-card-label">Đơn có sẵn</p>
                <p class="stat-card-value">${availableOrders}<span class="stat-card-unit">đơn</span></p>
            </div>
            <div class="stat-card accent">
                <span class="stat-card-icon">🔥</span>
                <p class="stat-card-label">Đang giao</p>
                <p class="stat-card-value">${shippingOrders}<span class="stat-card-unit">đơn</span></p>
            </div>
            <div class="stat-card">
                <span class="stat-card-icon">✅</span>
                <p class="stat-card-label">Đã hoàn thành</p>
                <p class="stat-card-value">${deliveredOrders}<span class="stat-card-unit">đơn</span></p>
                <p class="stat-card-trend">Hôm nay</p>
            </div>
        </div>

        <!-- Orders Section -->
        <div class="orders-section-title">
            <h3>Đơn hàng chờ nhận (${orders.size()})</h3>
            <span>Sắp xếp: Mới nhất</span>
        </div>

        <c:choose>
            <c:when test="${empty orders}">
                <div class="empty-state-modern">
                    <div class="empty-icon-wrapper">
                        <div class="empty-icon-bg"></div>
                        <div class="empty-icon-circle">📭</div>
                    </div>
                    <h3 class="empty-title">Đã xử lý hết!</h3>
                    <p class="empty-description">
                        Chúng tôi đang quét khu vực của bạn để tìm đơn hàng mới. 
                        Giữ ứng dụng mở để nhận thông báo.
                    </p>
                    <a href="${pageContext.request.contextPath}/shipper/orders" class="btn-scan-modern">
                        Quét lại khu vực
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="orders-grid">
                    <c:forEach var="order" items="${orders}">
                        <div class="order-card-modern">
                            <!-- Map Visual -->
                            <div class="order-card-map">
                                <div class="map-route-line"></div>
                                <div class="map-badge left">
                                    <span class="map-badge-icon">📍</span>
                                    <span>~2 km</span>
                                </div>
                                <div class="map-badge right">
                                    <span class="map-badge-icon">🛍️</span>
                                    <span>${order.orderDetails.size()} món</span>
                                </div>
                            </div>
                            
                            <!-- Content -->
                            <div class="order-card-content">
                                <!-- Time Estimate -->
                                <div class="time-estimate-box">
                                    <div class="time-icon">⏱️</div>
                                    <div>
                                        <p class="time-info-label">Thời gian ước tính</p>
                                        <p class="time-info-value">15-20 phút</p>
                                    </div>
                                </div>
                                
                                <!-- Route Details -->
                                <div class="route-details">
                                    <div class="route-line-vertical"></div>
                                    
                                    <div class="route-point">
                                        <div class="route-point-icon pickup">🏪</div>
                                        <h4 class="route-point-title">Cửa hàng</h4>
                                        <p class="route-point-address">Đơn hàng #${order.orderId}</p>
                                    </div>
                                    
                                    <div class="route-point">
                                        <div class="route-point-icon dropoff">📍</div>
                                        <h4 class="route-point-title">${order.buyer.fullName}</h4>
                                        <p class="route-point-address">${order.shippingAddress}</p>
                                    </div>
                                </div>

                                <!-- Actions - Form based -->
                                <div class="order-card-actions">
                                    <form action="${pageContext.request.contextPath}/shipper/action" method="post" 
                                          onsubmit="return confirm('Bạn có chắc muốn nhận đơn này?');">
                                        <input type="hidden" name="action" value="accept">
                                        <input type="hidden" name="orderId" value="${order.orderId}">
                                        <button type="submit" class="btn-accept-modern">
                                            <div class="btn-accept-inner">
                                                <span class="btn-accept-text">Nhận đơn này</span>
                                                <div class="btn-accept-hint">
                                                    <span>Bấm để nhận</span>
                                                    <div class="btn-accept-arrow">→</div>
                                                </div>
                                            </div>
                                        </button>
                                    </form>
                                    
                                    <div class="order-secondary-actions">
                                        <button type="button" class="btn-secondary-modern" 
                                                onclick="showOrderDetail(${order.orderId})">
                                            👁️ Xem chi tiết
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- Bottom Navigation (Mobile) -->
    <nav class="bottom-nav-modern">
        <a href="${pageContext.request.contextPath}/shipper/orders" class="nav-item-modern active">
            <span class="nav-icon">📦</span>
            <span class="nav-label">Đơn hàng</span>
        </a>
        <a href="${pageContext.request.contextPath}/shipper/delivering" class="nav-item-modern">
            <span class="nav-icon">🚚</span>
            <span class="nav-label">Đang giao</span>
        </a>
        <a href="${pageContext.request.contextPath}/shipper/history" class="nav-item-modern">
            <span class="nav-icon">📋</span>
            <span class="nav-label">Lịch sử</span>
        </a>
    </nav>

    <jsp:include page="../common/footer.jsp"/>
    
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
        
        // Simple order detail modal (optional - can be removed if not needed)
        function showOrderDetail(orderId) {
            alert('Chi tiết đơn hàng #' + orderId + '\n\nĐể xem chi tiết, vui lòng nhận đơn và xem tại trang "Đang giao".');
        }
    </script>
</body>
</html>
