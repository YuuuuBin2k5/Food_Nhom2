<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đang giao - Shipper</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/shipper.css">
</head>
<body class="bg-white shipper-page">
    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="currentPath" value="/shipper/delivering"/>
    </jsp:include>

    <main class="shipper-main">
        
        <!-- Header -->
        <div style="margin-bottom: 2rem;">
            <h2 style="font-size: 1.8rem; color: #1a202c; font-weight: 700; margin: 0;">🚚 Đơn đang giao</h2>
            <p style="color: #718096; margin: 0.5rem 0 0 0;">Đơn hàng bạn đang thực hiện giao</p>
        </div>

        <c:choose>
            <c:when test="${not empty currentOrder}">
                <!-- Current Order Card -->
                <div style="background: white; border-radius: 0.75rem; box-shadow: 0 4px 12px rgba(0,0,0,0.1); overflow: hidden; border: 2px solid #805ad5;">
                    
                    <!-- Header -->
                    <div style="padding: 1.5rem; background: linear-gradient(to right, #805ad5, #6b46c1); color: white;">
                        <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;">
                            <div>
                                <span style="font-size: 1.5rem; font-weight: 700;">#${currentOrder.orderId}</span>
                                <span style="margin-left: 1rem; padding: 0.25rem 0.75rem; background: rgba(255,255,255,0.2); border-radius: 9999px; font-size: 0.875rem;">
                                    🚚 Đang giao
                                </span>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Content -->
                    <div style="padding: 1.5rem;">
                        <!-- Customer Info -->
                        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1.5rem; margin-bottom: 1.5rem;">
                            <div style="background: #f8fafc; padding: 1rem; border-radius: 0.5rem;">
                                <p style="color: #718096; font-size: 0.875rem; margin: 0 0 0.25rem 0;">👤 Khách hàng</p>
                                <p style="font-weight: 700; color: #1a202c; margin: 0; font-size: 1.125rem;">${currentOrder.buyer.fullName}</p>
                            </div>
                            <div style="background: #f8fafc; padding: 1rem; border-radius: 0.5rem;">
                                <p style="color: #718096; font-size: 0.875rem; margin: 0 0 0.25rem 0;">📞 Số điện thoại</p>
                                <p style="font-weight: 700; color: #1a202c; margin: 0; font-size: 1.125rem;">
                                    <c:choose>
                                        <c:when test="${not empty currentOrder.buyer.phoneNumber}">${currentOrder.buyer.phoneNumber}</c:when>
                                        <c:otherwise>Không có</c:otherwise>
                                    </c:choose>
                                </p>
                            </div>
                            <div style="background: #f8fafc; padding: 1rem; border-radius: 0.5rem;">
                                <p style="color: #718096; font-size: 0.875rem; margin: 0 0 0.25rem 0;">📅 Ngày đặt</p>
                                <p style="font-weight: 700; color: #1a202c; margin: 0; font-size: 1.125rem;">
                                    <fmt:formatDate value="${currentOrder.orderDate}" pattern="dd/MM/yyyy HH:mm"/>
                                </p>
                            </div>
                        </div>

                        <!-- Address -->
                        <div style="background: #eff6ff; padding: 1.25rem; border-radius: 0.5rem; margin-bottom: 1.5rem; border: 1px solid #bfdbfe;">
                            <p style="color: #1e40af; font-size: 0.875rem; font-weight: 700; margin: 0 0 0.5rem 0;">📍 Địa chỉ giao hàng</p>
                            <p style="color: #1a202c; margin: 0; font-size: 1.125rem;">${currentOrder.shippingAddress}</p>
                        </div>
                        
                        <!-- Products -->
                        <div style="margin-bottom: 1.5rem;">
                            <p style="font-weight: 700; color: #1a202c; margin: 0 0 0.75rem 0;">🛍️ Sản phẩm (${currentOrder.orderDetails.size()} món)</p>
                            <div style="display: flex; flex-direction: column; gap: 0.5rem;">
                                <c:forEach var="item" items="${currentOrder.orderDetails}">
                                    <div style="display: flex; justify-content: space-between; align-items: center; padding: 0.75rem; background: #f8fafc; border-radius: 0.5rem;">
                                        <span style="color: #1a202c;">${item.product.name}</span>
                                        <span style="color: #718096;">x${item.quantity}</span>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                        
                        <!-- Actions -->
                        <div class="delivering-actions" style="display: flex; gap: 0.75rem; flex-wrap: wrap;">
                            <a href="tel:${currentOrder.buyer.phoneNumber}" 
                               style="flex: 1; min-width: 120px; display: flex; align-items: center; justify-content: center; gap: 0.5rem; background: white; color: #3182ce; border: 2px solid #3182ce; padding: 1rem; border-radius: 0.5rem; text-decoration: none; font-weight: 600;">
                                📞 Gọi khách
                            </a>
                            <a href="https://www.google.com/maps/dir/?api=1&destination=${currentOrder.shippingAddress}" target="_blank"
                               style="flex: 1; min-width: 120px; display: flex; align-items: center; justify-content: center; gap: 0.5rem; background: white; color: #38a169; border: 2px solid #38a169; padding: 1rem; border-radius: 0.5rem; text-decoration: none; font-weight: 600;">
                                🗺️ Chỉ đường
                            </a>
                            <button onclick="completeOrder(${currentOrder.orderId})" 
                                    style="flex: 1; min-width: 100%; display: flex; align-items: center; justify-content: center; gap: 0.5rem; background: linear-gradient(to right, #38a169, #2f855a); color: white; border: none; padding: 1rem; border-radius: 0.5rem; cursor: pointer; font-weight: 700; font-size: 1rem; box-shadow: 0 4px 6px rgba(0,0,0,0.1);">
                                ✅ Đã giao xong
                            </button>
                        </div>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <!-- Empty State -->
                <div style="background: white; border-radius: 0.75rem; box-shadow: 0 2px 4px rgba(0,0,0,0.1); text-align: center; padding: 4rem 2rem;">
                    <span style="font-size: 5rem; display: block; margin-bottom: 1.5rem;">🚚</span>
                    <h3 style="margin: 0 0 0.5rem 0; color: #1a202c; font-size: 1.5rem;">Chưa có đơn đang giao</h3>
                    <p style="color: #718096; margin: 0 0 2rem 0;">Nhận đơn mới từ trang "Đơn có sẵn"</p>
                    <a href="${pageContext.request.contextPath}/shipper/orders" 
                       style="display: inline-block; background: linear-gradient(to right, #805ad5, #6b46c1); color: white; padding: 0.875rem 2rem; border-radius: 0.5rem; text-decoration: none; font-weight: 600;">
                        📦 Xem đơn có sẵn
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </main>

    <jsp:include page="../common/footer.jsp"/>
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
    <script>
        const API_BASE = '${pageContext.request.contextPath}/api';
        
        async function completeOrder(orderId) {
            if (!confirm('Xác nhận đã giao hàng thành công cho khách?')) return;
            
            try {
                showLoading();
                const response = await fetch(API_BASE + '/shipper/orders/' + orderId + '/complete', {
                    method: 'PATCH',
                    headers: { 'Content-Type': 'application/json' }
                });
                
                if (response.ok) {
                    showToast('Giao hàng thành công! 🎉', 'success');
                    setTimeout(() => window.location.href = '${pageContext.request.contextPath}/shipper/orders', 1500);
                } else {
                    const error = await response.json();
                    showToast(error.message || 'Lỗi xác nhận giao hàng', 'error');
                }
            } catch (error) {
                showToast('Lỗi kết nối server', 'error');
            } finally {
                hideLoading();
            }
        }
    </script>
</body>
</html>
