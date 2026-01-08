<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <title>Lịch sử giao hàng - Shipper</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/shipper-modern.css">
    <jsp:include page="../common/pwa-head.jsp"/>
</head>
<body class="shipper-modern">
    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="currentPath" value="/shipper/history"/>
    </jsp:include>

    <div class="shipper-container">
        <!-- Header -->
        <header class="shipper-header">
            <div class="shipper-header-content">
                <div>
                    <h1 class="shipper-title">Lịch sử giao hàng</h1>
                    <div class="shipper-subtitle">
                        <span class="status-text" style="color: var(--text-secondary);">Các đơn đã hoàn thành</span>
                    </div>
                </div>
            </div>
        </header>

        <!-- History Card -->
        <div class="history-card">
            <div class="history-header">
                <h3>✅ Đơn đã hoàn thành</h3>
            </div>
            
            <c:choose>
                <c:when test="${empty deliveredOrders}">
                    <div class="empty-state-modern" style="padding: 60px 24px;">
                        <div class="empty-icon-wrapper" style="width: 80px; height: 80px;">
                            <div class="empty-icon-bg"></div>
                            <div class="empty-icon-circle" style="font-size: 2rem;">📭</div>
                        </div>
                        <h3 class="empty-title">Chưa có lịch sử</h3>
                        <p class="empty-description">Bạn chưa hoàn thành đơn hàng nào</p>
                        <a href="${pageContext.request.contextPath}/shipper/orders" class="btn-scan-modern">
                            📦 Nhận đơn ngay
                        </a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div style="overflow-x: auto;">
                        <table class="history-table">
                            <thead>
                                <tr>
                                    <th>Mã đơn</th>
                                    <th>Khách hàng</th>
                                    <th>Địa chỉ</th>
                                    <th>Ngày giao</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="order" items="${deliveredOrders}">
                                    <tr>
                                        <td><span class="order-id-badge">#${order.orderId}</span></td>
                                        <td><span class="customer-name">${order.buyer.fullName}</span></td>
                                        <td><span class="address-truncate">${order.shippingAddress}</span></td>
                                        <td><span class="date-text"><fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm"/></span></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- Bottom Navigation (Mobile) -->
    <nav class="bottom-nav-modern">
        <a href="${pageContext.request.contextPath}/shipper/orders" class="nav-item-modern">
            <span class="nav-icon">📦</span>
            <span class="nav-label">Đơn hàng</span>
        </a>
        <a href="${pageContext.request.contextPath}/shipper/delivering" class="nav-item-modern">
            <span class="nav-icon">🚚</span>
            <span class="nav-label">Đang giao</span>
        </a>
        <a href="${pageContext.request.contextPath}/shipper/history" class="nav-item-modern active">
            <span class="nav-icon">📋</span>
            <span class="nav-label">Lịch sử</span>
        </a>
    </nav>

    <jsp:include page="../common/footer.jsp"/>
    <jsp:include page="../common/pwa-script.jsp"/>
    
    <script>
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
