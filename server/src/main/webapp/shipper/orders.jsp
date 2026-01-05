<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đơn có sẵn - Shipper</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/shipper.css">
</head>
<body class="bg-white shipper-page">
    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="currentPath" value="/shipper/orders"/>
    </jsp:include>

    <main class="shipper-main">
        
        <!-- Header -->
        <div style="margin-bottom: 2rem; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;">
            <div>
                <h2 style="font-size: 1.8rem; color: #1a202c; font-weight: 700; margin: 0;">📦 Đơn hàng có sẵn</h2>
                <p style="color: #718096; margin: 0.5rem 0 0 0;">Các đơn hàng đang chờ shipper nhận</p>
            </div>
            <button onclick="location.reload()" style="background: #3182ce; color: white; border: none; padding: 0.75rem 1.5rem; border-radius: 0.5rem; cursor: pointer; font-weight: 600; display: flex; align-items: center; gap: 0.5rem;">
                🔄 Làm mới
            </button>
        </div>

        <!-- Stats -->
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1.5rem; margin-bottom: 2rem;">
            <div style="background: white; padding: 1.5rem; border-radius: 0.75rem; box-shadow: 0 2px 4px rgba(0,0,0,0.1); border-left: 4px solid #3182ce;">
                <p style="color: #718096; font-size: 0.875rem; margin: 0;">Đơn có sẵn</p>
                <p style="font-size: 2rem; font-weight: 700; color: #1a202c; margin: 0.5rem 0 0 0;">${availableOrders}</p>
            </div>
            <div style="background: white; padding: 1.5rem; border-radius: 0.75rem; box-shadow: 0 2px 4px rgba(0,0,0,0.1); border-left: 4px solid #805ad5;">
                <p style="color: #718096; font-size: 0.875rem; margin: 0;">Đang giao</p>
                <p style="font-size: 2rem; font-weight: 700; color: #1a202c; margin: 0.5rem 0 0 0;">${shippingOrders}</p>
            </div>
            <div style="background: white; padding: 1.5rem; border-radius: 0.75rem; box-shadow: 0 2px 4px rgba(0,0,0,0.1); border-left: 4px solid #38a169;">
                <p style="color: #718096; font-size: 0.875rem; margin: 0;">Đã giao</p>
                <p style="font-size: 2rem; font-weight: 700; color: #1a202c; margin: 0.5rem 0 0 0;">${deliveredOrders}</p>
            </div>
        </div>

        <!-- Orders List -->
        <div style="background: white; border-radius: 0.75rem; box-shadow: 0 2px 4px rgba(0,0,0,0.1); overflow: hidden;">
            <div style="padding: 1rem 1.5rem; background: linear-gradient(to right, #fff7ed, #fef3c7); border-bottom: 1px solid #e2e8f0;">
                <h3 style="margin: 0; font-size: 1.125rem; color: #1a202c;">Danh sách đơn hàng chờ nhận</h3>
            </div>
            
            <c:choose>
                <c:when test="${empty orders}">
                    <div style="text-align: center; padding: 4rem 2rem; color: #718096;">
                        <span style="font-size: 4rem; display: block; margin-bottom: 1rem;">📭</span>
                        <h3 style="margin: 0 0 0.5rem 0; color: #1a202c;">Không có đơn hàng</h3>
                        <p style="margin: 0;">Chưa có đơn hàng nào có sẵn để nhận</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div style="padding: 1.5rem; display: flex; flex-direction: column; gap: 1.5rem;">
                        <c:forEach var="order" items="${orders}">
                            <div style="border: 1px solid #e2e8f0; border-radius: 0.75rem; overflow: hidden; transition: all 0.3s;" 
                                 onmouseover="this.style.boxShadow='0 4px 12px rgba(0,0,0,0.1)'" 
                                 onmouseout="this.style.boxShadow='none'">
                                
                                <!-- Order Header -->
                                <div style="padding: 1rem 1.5rem; background: #f8fafc; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;">
                                    <div>
                                        <span style="font-size: 1.25rem; font-weight: 700; color: #1a202c;">#${order.orderId}</span>
                                        <span style="margin-left: 1rem; padding: 0.25rem 0.75rem; background: #dbeafe; color: #1e40af; border-radius: 9999px; font-size: 0.75rem; font-weight: 600;">
                                            📦 Chờ nhận
                                        </span>
                                    </div>
                                </div>
                                
                                <!-- Order Info -->
                                <div style="padding: 1.5rem;">
                                    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 1.5rem; margin-bottom: 1rem;">
                                        <div>
                                            <p style="color: #718096; font-size: 0.875rem; margin: 0 0 0.25rem 0;">👤 Khách hàng</p>
                                            <p style="font-weight: 600; color: #1a202c; margin: 0;">${order.buyer.fullName}</p>
                                        </div>
                                        <div>
                                            <p style="color: #718096; font-size: 0.875rem; margin: 0 0 0.25rem 0;">📅 Ngày đặt</p>
                                            <p style="font-weight: 600; color: #1a202c; margin: 0;">
                                                <fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm"/>
                                            </p>
                                        </div>
                                    </div>
                                    
                                    <!-- Address -->
                                    <div style="background: #eff6ff; padding: 1rem; border-radius: 0.5rem; margin-bottom: 1rem;">
                                        <p style="color: #1e40af; font-size: 0.875rem; font-weight: 600; margin: 0 0 0.5rem 0;">📍 Địa chỉ giao hàng</p>
                                        <p style="color: #1a202c; margin: 0;">${order.shippingAddress}</p>
                                    </div>
                                    
                                    <!-- Actions -->
                                    <div style="display: flex; gap: 0.75rem; flex-wrap: wrap;">
                                        <button onclick="viewOrderDetail(${order.orderId})" 
                                                style="background: white; color: #3182ce; border: 2px solid #3182ce; padding: 0.625rem 1.25rem; border-radius: 0.5rem; cursor: pointer; font-weight: 600;">
                                            👁️ Xem chi tiết
                                        </button>
                                        <button onclick="acceptOrder(${order.orderId})" 
                                                style="background: linear-gradient(to right, #805ad5, #6b46c1); color: white; border: none; padding: 0.625rem 1.25rem; border-radius: 0.5rem; cursor: pointer; font-weight: 600; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                                            ✋ Nhận đơn này
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </main>

    <jsp:include page="../common/footer.jsp"/>

    <!-- Order Detail Modal -->
    <div id="orderDetailModal" class="modal">
        <div class="modal-backdrop" onclick="closeModal('orderDetailModal')"></div>
        <div class="modal-content modal-lg">
            <div class="modal-header">
                <h3 id="modalOrderId">Chi tiết đơn hàng</h3>
                <button onclick="closeModal('orderDetailModal')" class="btn-close">✕</button>
            </div>
            <div class="modal-body" id="orderDetailContent">
                <div class="loading-spinner">Đang tải...</div>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/main.js"></script>
    <script>
        const API_BASE = '${pageContext.request.contextPath}/api';
        
        async function viewOrderDetail(orderId) {
            try {
                openModal('orderDetailModal');
                document.getElementById('orderDetailContent').innerHTML = '<div style="text-align: center; padding: 2rem;">Đang tải...</div>';
                
                const response = await fetch(API_BASE + '/orders/' + orderId);
                if (!response.ok) throw new Error('Lỗi tải dữ liệu');
                const order = await response.json();
                
                document.getElementById('modalOrderId').textContent = 'Chi tiết đơn hàng #' + order.orderId;
                
                let itemsHtml = '';
                if (order.orderItems) {
                    order.orderItems.forEach(item => {
                        itemsHtml += '<div style="display: flex; justify-content: space-between; padding: 0.75rem; background: #f8fafc; border-radius: 0.5rem; margin-bottom: 0.5rem;">' +
                            '<span>' + item.product.name + '</span>' +
                            '<span>x' + item.quantity + ' - ' + formatPrice(item.price * item.quantity) + '</span>' +
                        '</div>';
                    });
                }
                
                const html = '<div style="display: grid; gap: 1rem;">' +
                    '<div style="background: #fff7ed; padding: 1rem; border-radius: 0.5rem;">' +
                        '<p style="color: #92400e; font-size: 0.75rem; margin: 0 0 0.25rem 0;">KHÁCH HÀNG</p>' +
                        '<p style="font-weight: 700; margin: 0;">👤 ' + order.buyer.fullName + '</p>' +
                    '</div>' +
                    '<div style="background: #eff6ff; padding: 1rem; border-radius: 0.5rem;">' +
                        '<p style="color: #1e40af; font-size: 0.875rem; font-weight: 600; margin: 0 0 0.5rem 0;">📍 Địa chỉ giao hàng</p>' +
                        '<p style="margin: 0;">' + order.shippingAddress + '</p>' +
                    '</div>' +
                    '<div>' +
                        '<p style="font-weight: 600; margin: 0 0 0.75rem 0;">🛍️ Sản phẩm</p>' +
                        itemsHtml +
                    '</div>' +
                    '<div style="display: flex; justify-content: flex-end; gap: 0.75rem; padding-top: 1rem; border-top: 1px solid #e2e8f0;">' +
                        '<button onclick="closeModal(\'orderDetailModal\')" style="background: #e2e8f0; color: #334155; border: none; padding: 0.75rem 1.5rem; border-radius: 0.5rem; cursor: pointer; font-weight: 600;">Đóng</button>' +
                        '<button onclick="acceptOrder(' + order.orderId + '); closeModal(\'orderDetailModal\');" style="background: linear-gradient(to right, #805ad5, #6b46c1); color: white; border: none; padding: 0.75rem 1.5rem; border-radius: 0.5rem; cursor: pointer; font-weight: 600;">✋ Nhận đơn này</button>' +
                    '</div>' +
                '</div>';
                
                document.getElementById('orderDetailContent').innerHTML = html;
                
            } catch (error) {
                document.getElementById('orderDetailContent').innerHTML = '<div style="text-align: center; color: #e53e3e; padding: 2rem;">Lỗi tải chi tiết đơn hàng</div>';
            }
        }
        
        async function acceptOrder(orderId) {
            if (!confirm('Bạn có chắc muốn nhận đơn này?')) return;
            
            try {
                showLoading();
                const response = await fetch(API_BASE + '/shipper/orders/' + orderId + '/accept', {
                    method: 'PATCH',
                    headers: { 'Content-Type': 'application/json' }
                });
                
                if (response.ok) {
                    showToast('Nhận đơn thành công!', 'success');
                    setTimeout(() => window.location.href = '${pageContext.request.contextPath}/shipper/delivering', 1000);
                } else {
                    const error = await response.json();
                    showToast(error.message || 'Lỗi nhận đơn', 'error');
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
