<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặt hàng thành công - FreshSave</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <style>
        body {
            margin: 0;
            padding: 0;
            background: linear-gradient(to bottom right, #fff7ed, #fef3c7, #fef9c3);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        
        .success-container {
            max-width: 600px;
            width: 100%;
            margin: 2rem;
        }
        
        .success-card {
            background: white;
            border-radius: 1.5rem;
            box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
            overflow: hidden;
        }
        
        .success-header {
            background: linear-gradient(to right, #10b981, #059669);
            padding: 2rem;
            text-align: center;
        }
        
        .success-icon {
            width: 80px;
            height: 80px;
            background: white;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 1rem;
            font-size: 3rem;
            animation: scaleIn 0.5s ease;
        }
        
        @keyframes scaleIn {
            from {
                transform: scale(0);
            }
            to {
                transform: scale(1);
            }
        }
        
        .success-title {
            color: white;
            font-size: 1.875rem;
            font-weight: 700;
            margin-bottom: 0.5rem;
        }
        
        .success-subtitle {
            color: rgba(255, 255, 255, 0.9);
            font-size: 1rem;
        }
        
        .success-body {
            padding: 2rem;
        }
        
        .info-box {
            background: #f0fdf4;
            border: 1px solid #bbf7d0;
            border-radius: 0.75rem;
            padding: 1rem;
            margin-bottom: 1.5rem;
        }
        
        .info-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 0.75rem 0;
            border-bottom: 1px solid #e5e7eb;
        }
        
        .info-row:last-child {
            border-bottom: none;
        }
        
        .info-label {
            color: #64748b;
            font-size: 0.875rem;
        }
        
        .info-value {
            font-weight: 600;
            color: #0f172a;
            text-align: right;
        }
        
        .order-id {
            font-size: 1.5rem;
            font-weight: 700;
            color: #10b981;
        }
        
        .total-amount {
            font-size: 1.875rem;
            font-weight: 700;
            color: #FF6B6B;
        }
        
        .notice-box {
            background: #dbeafe;
            border: 1px solid #93c5fd;
            border-radius: 0.75rem;
            padding: 1rem;
            margin-bottom: 1.5rem;
            display: flex;
            gap: 0.75rem;
        }
        
        .notice-icon {
            font-size: 1.5rem;
            flex-shrink: 0;
        }
        
        .notice-content {
            flex: 1;
        }
        
        .notice-title {
            font-weight: 600;
            color: #1e40af;
            margin-bottom: 0.25rem;
        }
        
        .notice-text {
            font-size: 0.875rem;
            color: #1e3a8a;
            line-height: 1.5;
        }
        
        .action-buttons {
            display: grid;
            grid-template-columns: 1fr;
            gap: 0.75rem;
        }
        
        @media (min-width: 640px) {
            .action-buttons {
                grid-template-columns: 1fr 1fr;
            }
        }
        
        .btn {
            padding: 0.875rem 1.5rem;
            border-radius: 0.75rem;
            font-weight: 600;
            text-align: center;
            text-decoration: none;
            transition: all 0.2s;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 0.5rem;
        }
        
        .btn-primary {
            background: linear-gradient(to right, #FF6B6B, #FF8E53);
            color: white;
            box-shadow: 0 4px 6px rgba(255, 107, 107, 0.3);
        }
        
        .btn-primary:hover {
            box-shadow: 0 10px 15px rgba(255, 107, 107, 0.4);
            transform: translateY(-2px);
        }
        
        .btn-secondary {
            background: white;
            color: #FF6B6B;
            border: 2px solid #fed7aa;
        }
        
        .btn-secondary:hover {
            background: #fff7ed;
        }
        
        .multi-order-notice {
            background: #fef3c7;
            border: 1px solid #fde68a;
            border-radius: 0.75rem;
            padding: 1rem;
            margin-bottom: 1.5rem;
            display: flex;
            gap: 0.75rem;
        }
        
        .order-ids {
            display: flex;
            flex-wrap: wrap;
            gap: 0.5rem;
            margin-top: 0.5rem;
        }
        
        .order-id-badge {
            background: white;
            color: #10b981;
            padding: 0.25rem 0.75rem;
            border-radius: 0.5rem;
            font-weight: 700;
            font-size: 0.875rem;
            border: 1px solid #bbf7d0;
        }
    </style>
</head>
<body>
    <div class="success-container">
        <div class="success-card">
            <!-- Header -->
            <div class="success-header">
                <div class="success-icon">✅</div>
                <h1 class="success-title">Đặt hàng thành công!</h1>
                <p class="success-subtitle">Cảm ơn bạn đã tin tưởng FreshSave</p>
            </div>
            
            <!-- Body -->
            <div class="success-body">
                <!-- Multi-order notice -->
                <c:if test="${not empty orderIds && fn:length(orderIds) > 1}">
                    <div class="multi-order-notice">
                        <span class="notice-icon">ℹ️</span>
                        <div style="flex: 1;">
                            <div class="notice-title">Đơn hàng được tách theo cửa hàng</div>
                            <p class="notice-text">
                                Sản phẩm từ các cửa hàng khác nhau sẽ được giao riêng để đảm bảo chất lượng tốt nhất.
                            </p>
                            <div class="order-ids">
                                <c:forEach items="${orderIds}" var="id">
                                    <span class="order-id-badge">#${id}</span>
                                </c:forEach>
                            </div>
                        </div>
                    </div>
                </c:if>
                
                <!-- Order Info -->
                <div class="info-box">
                    <div class="info-row">
                        <span class="info-label">Mã đơn hàng</span>
                        <span class="info-value order-id">
                            <c:choose>
                                <c:when test="${not empty orderIds && fn:length(orderIds) > 1}">
                                    ${fn:length(orderIds)} đơn hàng
                                </c:when>
                                <c:otherwise>
                                    #${orderId}
                                </c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Tổng tiền</span>
                        <span class="info-value total-amount">
                            <fmt:formatNumber value="${total}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                        </span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Phương thức thanh toán</span>
                        <span class="info-value">
                            <c:choose>
                                <c:when test="${paymentMethod == 'COD'}">
                                    💵 Thanh toán khi nhận hàng
                                </c:when>
                                <c:otherwise>
                                    🏦 Chuyển khoản ngân hàng
                                </c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                </div>
                
                <!-- Notice -->
                <div class="notice-box">
                    <span class="notice-icon">📦</span>
                    <div class="notice-content">
                        <div class="notice-title">Lưu ý quan trọng</div>
                        <p class="notice-text">
                            • Đơn hàng sẽ được xác nhận trong vòng 5-10 phút<br>
                            • Bạn có 30 phút để đến lấy hàng sau khi đơn được xác nhận<br>
                            • Vui lòng kiểm tra email để biết thêm chi tiết
                        </p>
                    </div>
                </div>
                
                <!-- Actions -->
                <div class="action-buttons">
                    <a href="${pageContext.request.contextPath}/orders" class="btn btn-primary">
                        <svg style="width: 1.25rem; height: 1.25rem;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
                        </svg>
                        Xem đơn hàng
                    </a>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary">
                        <svg style="width: 1.25rem; height: 1.25rem;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z"/>
                        </svg>
                        Tiếp tục mua sắm
                    </a>
                </div>
            </div>
        </div>
    </div>
    
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
    <script>
        // Show success toast
        window.addEventListener('DOMContentLoaded', () => {
            Toast.success('Đặt hàng thành công! Cảm ơn bạn đã mua hàng.');
        });
    </script>
</body>
</html>
