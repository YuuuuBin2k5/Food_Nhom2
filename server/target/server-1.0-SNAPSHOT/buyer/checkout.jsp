<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thanh toán - FoodRescue</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/buyer/checkout.css">
</head>
<body>
    <!-- Include Sidebar -->
    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="currentPath" value="/checkout" />
    </jsp:include>
    
    <!-- Header -->
    <header class="checkout-header">
        <div style="max-width: 1280px; margin: 0 auto; padding: 0 1rem;">
            <div style="display: flex; align-items: center; justify-content: space-between; height: 4rem;">
                <a href="${pageContext.request.contextPath}/cart" style="display: flex; align-items: center; gap: 0.5rem; color: white; text-decoration: none; transition: opacity 0.2s;">
                    <svg style="width: 1.25rem; height: 1.25rem;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
                    </svg>
                    Quay lại
                </a>
                <h1 style="font-size: 1.25rem; font-weight: 700; color: white;">💳 Thanh toán</h1>
                <div style="width: 5rem;"></div>
            </div>
        </div>
    </header>
    
    <main class="checkout-container">
        <c:choose>
            <c:when test="${empty cartItems}">
                <!-- Empty Cart -->
                <div style="background: white; border-radius: 1.5rem; box-shadow: 0 10px 15px rgba(0, 0, 0, 0.1); padding: 3rem; text-align: center; max-width: 32rem; margin: 0 auto;">
                    <div style="font-size: 4rem; margin-bottom: 1rem;">⚠️</div>
                    <h2 style="font-size: 1.5rem; font-weight: 700; color: #0f172a; margin-bottom: 0.5rem;">Giỏ hàng trống</h2>
                    <p style="color: #334155; margin-bottom: 2rem;">
                        Bạn cần thêm sản phẩm vào giỏ hàng trước khi thanh toán
                    </p>
                    <a href="${pageContext.request.contextPath}/products" style="display: inline-block; padding: 1rem 2rem; background: linear-gradient(to right, #FF6B6B, #FF8E53, #FFC75F); color: white; font-weight: 600; border-radius: 0.75rem; text-decoration: none; box-shadow: 0 4px 6px rgba(255, 107, 107, 0.3);">
                        🛍️ Khám phá sản phẩm
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <form method="POST" action="${pageContext.request.contextPath}/checkout" onsubmit="return handleSubmit(event)">
                    <div class="checkout-grid">
                        <!-- Left: Checkout Form -->
                        <div class="form-card">
                            <!-- Shipping Info -->
                            <div class="form-section">
                                <h2 class="section-title">
                                    <span>📦</span>
                                    Thông tin giao hàng
                                </h2>
                                
                                <div class="form-row two-cols">
                                    <div class="form-group">
                                        <label class="form-label required" for="recipientName">Họ và tên</label>
                                        <input 
                                            type="text" 
                                            id="recipientName" 
                                            name="recipientName" 
                                            class="form-input ${not empty fieldErrors.recipientName ? 'error' : ''}"
                                            value="${param.recipientName != null ? param.recipientName : sessionScope.user.fullName}"
                                            required
                                        >
                                        <c:if test="${not empty fieldErrors.recipientName}">
                                            <div class="error-message">${fieldErrors.recipientName}</div>
                                        </c:if>
                                    </div>
                                    
                                    <div class="form-group">
                                        <label class="form-label required" for="recipientPhone">Số điện thoại</label>
                                        <input 
                                            type="tel" 
                                            id="recipientPhone" 
                                            name="recipientPhone" 
                                            class="form-input ${not empty fieldErrors.recipientPhone ? 'error' : ''}"
                                            value="${param.recipientPhone != null ? param.recipientPhone : sessionScope.user.phoneNumber}"
                                            required
                                        >
                                        <c:if test="${not empty fieldErrors.recipientPhone}">
                                            <div class="error-message">${fieldErrors.recipientPhone}</div>
                                        </c:if>
                                    </div>
                                </div>
                                
                                <div class="form-group">
                                    <label class="form-label required" for="shippingAddress">Địa chỉ giao hàng</label>
                                    <textarea 
                                        id="shippingAddress" 
                                        name="address" 
                                        class="form-textarea ${not empty fieldErrors.shippingAddress ? 'error' : ''}"
                                        placeholder="Số nhà, tên đường, phường/xã, quận/huyện, tỉnh/thành phố"
                                        required
                                    >${param.shippingAddress != null ? param.shippingAddress : sessionScope.user.address}</textarea>
                                    <c:if test="${not empty fieldErrors.shippingAddress}">
                                        <div class="error-message">${fieldErrors.shippingAddress}</div>
                                    </c:if>
                                </div>
                            </div>
                            
                            <!-- Payment Method -->
                            <div class="form-section">
                                <h2 class="section-title">
                                    <span>💳</span>
                                    Phương thức thanh toán
                                </h2>
                                
                                <div class="payment-options">
                                    <label class="payment-option ${param.paymentMethod == null || param.paymentMethod == 'COD' ? 'selected' : ''}">
                                        <input 
                                            type="radio" 
                                            name="paymentMethod" 
                                            value="COD" 
                                            ${param.paymentMethod == null || param.paymentMethod == 'COD' ? 'checked' : ''}
                                            onchange="updatePaymentSelection(this)"
                                        >
                                        <span class="payment-label">
                                            <span style="font-size: 1.5rem;">💵</span>
                                            <span>Thanh toán khi nhận hàng (COD)</span>
                                        </span>
                                    </label>
                                    
                                    <label class="payment-option ${param.paymentMethod == 'BANKING' ? 'selected' : ''}">
                                        <input 
                                            type="radio" 
                                            name="paymentMethod" 
                                            value="BANKING"
                                            ${param.paymentMethod == 'BANKING' ? 'checked' : ''}
                                            onchange="updatePaymentSelection(this)"
                                        >
                                        <span class="payment-label">
                                            <span style="font-size: 1.5rem;">💳</span>
                                            <span>Thanh toán VNPay</span>
                                        </span>
                                    </label>
                                </div>
                            </div>
                            
                            <!-- Note -->
                            <div class="form-section">
                                <h2 class="section-title">
                                    <span>📝</span>
                                    Ghi chú (không bắt buộc)
                                </h2>
                                
                                <div class="form-group">
                                    <textarea 
                                        id="note" 
                                        name="note" 
                                        class="form-textarea"
                                        placeholder="Ghi chú cho người bán..."
                                    >${param.note}</textarea>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Right: Order Summary -->
                        <div>
                            <div class="summary-card">
                                <h2 class="summary-title">Đơn hàng của bạn</h2>
                                
                                <!-- Items List -->
                                <div class="summary-items">
                                    <c:forEach items="${cartItems}" var="item">
                                        <div class="summary-item">
                                            <img 
                                                src="${item.product.imageUrl != null ? item.product.imageUrl : 'https://placehold.co/60x60/FF6B6B/FFFFFF?text=Food'}" 
                                                alt="${item.product.name}"
                                                class="item-image"
                                            >
                                            <div class="item-details">
                                                <div class="item-name">${item.product.name}</div>
                                                <div class="item-quantity">x${item.quantity}</div>
                                            </div>
                                            <div class="item-price">
                                                <fmt:formatNumber value="${item.product.salePrice * item.quantity}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                                
                                <!-- Summary -->
                                <div class="summary-row">
                                    <span>Tạm tính</span>
                                    <span style="font-weight: 500;">
                                        <fmt:formatNumber value="${subtotal}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                    </span>
                                </div>
                                
                                <div class="summary-row">
                                    <span>Phí vận chuyển</span>
                                    <span style="font-weight: 500;">
                                        <fmt:formatNumber value="${shippingFee}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                    </span>
                                </div>
                                
                                <div class="summary-total">
                                    <span class="total-label">Tổng cộng</span>
                                    <span class="total-value">
                                        <fmt:formatNumber value="${total}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                    </span>
                                </div>
                                
                                <c:if test="${not empty error}">
                                    <div class="alert-error">
                                        <span>⚠️</span>
                                        <span>${error}</span>
                                    </div>
                                </c:if>
                                
                                <button type="submit" class="submit-btn" id="submitBtn">
                                    Đặt hàng
                                </button>
                                
                                <p style="text-align: center; font-size: 0.75rem; color: #9ca3af; margin-top: 1rem;">
                                    Bằng cách đặt hàng, bạn đồng ý với điều khoản sử dụng của chúng tôi
                                </p>
                            </div>
                        </div>
                    </div>
                </form>
            </c:otherwise>
        </c:choose>
    </main>
    
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
    <script>
        function updatePaymentSelection(radio) {
            document.querySelectorAll('.payment-option').forEach(option => {
                option.classList.remove('selected');
            });
            radio.closest('.payment-option').classList.add('selected');
        }
        
        function handleSubmit(event) {
            // Form validation is handled by HTML5 required attributes
            
            const btn = document.getElementById('submitBtn');
            btn.disabled = true;
            btn.textContent = 'Đang xử lý...';
            
            // Show loading if available
            if (typeof Loading !== 'undefined') {
                Loading.show();
            }
            
            return true;
        }
        
        // Show error toast if exists
        <c:if test="${not empty error}">
            window.addEventListener('DOMContentLoaded', () => {
                Toast.error('${error}');
            });
        </c:if>
    </script>
</body>
</html>
