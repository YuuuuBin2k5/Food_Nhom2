<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Giỏ hàng - FreshSave</title>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
                <style>
                    body {
                        margin: 0;
                        padding-top: 96px;
                        background: linear-gradient(to bottom right, #fff7ed, #fef3c7, #fef9c3);
                        min-height: 100vh;
                    }

                    .cart-header {
                        background: linear-gradient(to right, #FF6B6B, #FF8E53, #FFC75F);
                        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                    }

                    .cart-container {
                        max-width: 1280px;
                        margin: 0 auto;
                        padding: 2rem 1rem;
                    }

                    .cart-grid {
                        display: grid;
                        grid-template-columns: 1fr;
                        gap: 2rem;
                    }

                    @media (min-width: 1024px) {
                        .cart-grid {
                            grid-template-columns: 2fr 1fr;
                        }
                    }

                    .cart-item {
                        background: white;
                        border-radius: 1rem;
                        padding: 1rem;
                        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
                        display: flex;
                        gap: 1rem;
                        transition: all 0.2s;
                    }

                    .cart-item:hover {
                        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                    }

                    .item-image {
                        width: 100px;
                        height: 100px;
                        border-radius: 0.75rem;
                        object-fit: cover;
                        background: #f1f5f9;
                    }

                    .item-info {
                        flex: 1;
                        display: flex;
                        flex-direction: column;
                        justify-content: space-between;
                    }

                    .item-name {
                        font-weight: 700;
                        color: #0f172a;
                        font-size: 1.125rem;
                        margin-bottom: 0.25rem;
                    }

                    .item-shop {
                        font-size: 0.875rem;
                        color: #64748b;
                        margin-bottom: 0.5rem;
                    }

                    .item-price {
                        font-size: 1.25rem;
                        font-weight: 700;
                        color: #FF6B6B;
                    }

                    .item-original-price {
                        font-size: 0.875rem;
                        color: #94a3b8;
                        text-decoration: line-through;
                        margin-left: 0.5rem;
                    }

                    .quantity-control {
                        display: flex;
                        align-items: center;
                        gap: 0.5rem;
                        margin-top: 0.5rem;
                    }

                    .quantity-btn {
                        width: 2rem;
                        height: 2rem;
                        border-radius: 0.5rem;
                        border: 1px solid #e2e8f0;
                        background: white;
                        cursor: pointer;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        transition: all 0.2s;
                    }

                    .quantity-btn:hover {
                        background: #f8fafc;
                        border-color: #FF6B6B;
                    }

                    .quantity-value {
                        min-width: 2rem;
                        text-align: center;
                        font-weight: 600;
                    }

                    .remove-btn {
                        color: #ef4444;
                        background: none;
                        border: none;
                        cursor: pointer;
                        padding: 0.5rem;
                        transition: color 0.2s;
                    }

                    .remove-btn:hover {
                        color: #dc2626;
                    }

                    .summary-card {
                        background: white;
                        border-radius: 1rem;
                        padding: 1.5rem;
                        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
                        position: sticky;
                        top: 120px;
                    }

                    .summary-title {
                        font-size: 1.25rem;
                        font-weight: 700;
                        color: #0f172a;
                        margin-bottom: 1.5rem;
                        padding-bottom: 1rem;
                        border-bottom: 1px solid #e5e7eb;
                    }

                    .summary-row {
                        display: flex;
                        justify-content: space-between;
                        margin-bottom: 1rem;
                        color: #334155;
                    }

                    .summary-total {
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                        padding-top: 1rem;
                        border-top: 1px solid #e5e7eb;
                        margin-bottom: 1.5rem;
                    }

                    .total-label {
                        font-size: 1.125rem;
                        font-weight: 600;
                        color: #0f172a;
                    }

                    .total-value {
                        font-size: 1.5rem;
                        font-weight: 700;
                        color: #FF6B6B;
                    }

                    .checkout-btn {
                        width: 100%;
                        padding: 1rem;
                        background: linear-gradient(to right, #FF6B6B, #FF8E53, #FFC75F);
                        color: white;
                        font-weight: 700;
                        border: none;
                        border-radius: 0.75rem;
                        cursor: pointer;
                        transition: all 0.2s;
                        box-shadow: 0 4px 6px rgba(255, 107, 107, 0.3);
                    }

                    .checkout-btn:hover {
                        box-shadow: 0 10px 15px rgba(255, 107, 107, 0.4);
                    }

                    .continue-btn {
                        width: 100%;
                        padding: 0.75rem;
                        background: white;
                        color: #FF6B6B;
                        font-weight: 500;
                        border: 2px solid #fed7aa;
                        border-radius: 0.75rem;
                        cursor: pointer;
                        transition: all 0.2s;
                        margin-top: 0.75rem;
                    }

                    .continue-btn:hover {
                        background: #fff7ed;
                    }

                    .empty-cart {
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
                </style>
            </head>

            <body>
                <!-- Include Sidebar -->
                <jsp:include page="../common/sidebar.jsp">
                    <jsp:param name="currentPath" value="/cart" />
                </jsp:include>

                <!-- Header -->
                <header class="cart-header">
                    <div style="max-width: 1280px; margin: 0 auto; padding: 0 1rem;">
                        <div style="display: flex; align-items: center; justify-content: space-between; height: 4rem;">
                            <a href="${pageContext.request.contextPath}/products"
                                style="display: flex; align-items: center; gap: 0.5rem; color: white; text-decoration: none; transition: opacity 0.2s;">
                                <svg style="width: 1.25rem; height: 1.25rem;" fill="none" stroke="currentColor"
                                    viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                        d="M15 19l-7-7 7-7" />
                                </svg>
                                Tiếp tục mua sắm
                            </a>
                            <h1 style="font-size: 1.25rem; font-weight: 700; color: white;">🛒 Giỏ hàng</h1>
                            <div style="width: 8rem;"></div>
                        </div>
                    </div>
                </header>

                <main class="cart-container">
                    <c:choose>
                        <c:when test="${empty cartItems}">
                            <!-- Empty Cart -->
                            <div class="empty-cart">
                                <div class="empty-icon">🛒</div>
                                <h2 style="font-size: 1.5rem; font-weight: 700; color: #0f172a; margin-bottom: 0.5rem;">
                                    Giỏ hàng trống</h2>
                                <p style="color: #334155; margin-bottom: 2rem;">
                                    Bạn chưa có sản phẩm nào trong giỏ hàng.<br>
                                    Hãy khám phá các sản phẩm tươi ngon!
                                </p>
                                <a href="${pageContext.request.contextPath}/products"
                                    style="display: inline-block; padding: 1rem 2rem; background: linear-gradient(to right, #FF6B6B, #FF8E53, #FFC75F); color: white; font-weight: 600; border-radius: 0.75rem; text-decoration: none; box-shadow: 0 4px 6px rgba(255, 107, 107, 0.3); transition: all 0.2s;">
                                    🛍️ Khám phá sản phẩm
                                </a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="cart-grid">
                                <!-- Cart Items -->
                                <div>
                                    <!-- Cart Header -->
                                    <div
                                        style="background: white; border-radius: 1rem; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05); padding: 1rem; display: flex; align-items: center; justify-content: space-between; margin-bottom: 1rem;">
                                        <h2 style="font-size: 1.125rem; font-weight: 600; color: #0f172a;">
                                            ${cartItems.size()} sản phẩm
                                        </h2>
                                        <button onclick="clearCart()"
                                            style="color: #ef4444; font-size: 0.875rem; font-weight: 500; display: flex; align-items: center; gap: 0.25rem; background: none; border: none; cursor: pointer; transition: color 0.2s;">
                                            <svg style="width: 1rem; height: 1rem;" fill="none" stroke="currentColor"
                                                viewBox="0 0 24 24">
                                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                                    d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                            </svg>
                                            Xóa tất cả
                                        </button>
                                    </div>

                                    <!-- Items List -->
                                    <div style="display: flex; flex-direction: column; gap: 1rem;">
                                        <c:forEach items="${cartItems}" var="item">
                                            <div class="cart-item">
                                                <img src="${item.product.imageUrl != null ? item.product.imageUrl : 'https://placehold.co/100x100/FF6B6B/FFFFFF?text=Food'}"
                                                    alt="${item.product.name}" class="item-image">
                                                <div class="item-info">
                                                    <div>
                                                        <h3 class="item-name">${item.product.name}</h3>
                                                        <p class="item-shop">🏪 ${item.product.seller.shopName}</p>
                                                    </div>
                                                    <div
                                                        style="display: flex; justify-content: space-between; align-items: center;">
                                                        <div>
                                                            <span class="item-price">
                                                                <fmt:formatNumber value="${item.product.salePrice}"
                                                                    type="currency" currencySymbol="₫"
                                                                    maxFractionDigits="0" />
                                                            </span>
                                                            <c:if
                                                                test="${item.product.originalPrice > item.product.salePrice}">
                                                                <span class="item-original-price">
                                                                    <fmt:formatNumber
                                                                        value="${item.product.originalPrice}"
                                                                        type="currency" currencySymbol="₫"
                                                                        maxFractionDigits="0" />
                                                                </span>
                                                            </c:if>
                                                        </div>
                                                        <div class="quantity-control">
                                                            <button class="quantity-btn"
                                                                onclick="updateQuantity('${item.product.productId}', '${item.quantity - 1}')">
                                                                <svg style="width: 1rem; height: 1rem;" fill="none"
                                                                    stroke="currentColor" viewBox="0 0 24 24">
                                                                    <path stroke-linecap="round" stroke-linejoin="round"
                                                                        stroke-width="2" d="M20 12H4" />
                                                                </svg>
                                                            </button>
                                                            <span class="quantity-value">${item.quantity}</span>
                                                            <button class="quantity-btn"
                                                                onclick="updateQuantity('${item.product.productId}', '${item.quantity + 1}')">
                                                                <svg style="width: 1rem; height: 1rem;" fill="none"
                                                                    stroke="currentColor" viewBox="0 0 24 24">
                                                                    <path stroke-linecap="round" stroke-linejoin="round"
                                                                        stroke-width="2"
                                                                        d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                                                                </svg>
                                                            </button>
                                                            <button class="remove-btn"
                                                                onclick="removeItem('${item.product.productId}')">
                                                                <svg style="width: 1.25rem; height: 1.25rem;"
                                                                    fill="none" stroke="currentColor"
                                                                    viewBox="0 0 24 24">
                                                                    <path stroke-linecap="round" stroke-linejoin="round"
                                                                        stroke-width="2"
                                                                        d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                                                </svg>
                                                            </button>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>

                                <!-- Order Summary -->
                                <div>
                                    <div class="summary-card">
                                        <h2 class="summary-title">Tóm tắt đơn hàng</h2>

                                        <div class="summary-row">
                                            <span>Số món</span>
                                            <span style="font-weight: 500;">${totalItems} món</span>
                                        </div>

                                        <div class="summary-row">
                                            <span>Tạm tính</span>
                                            <span style="font-weight: 500;">
                                                <fmt:formatNumber value="${subtotal}" type="currency" currencySymbol="₫"
                                                    maxFractionDigits="0" />
                                            </span>
                                        </div>

                                        <div class="summary-row" style="color: #10b981;">
                                            <span>Tiết kiệm được</span>
                                            <span style="font-weight: 500;">
                                                -
                                                <fmt:formatNumber value="${savings}" type="currency" currencySymbol="₫"
                                                    maxFractionDigits="0" />
                                            </span>
                                        </div>

                                        <div class="summary-total">
                                            <span class="total-label">Tổng cộng</span>
                                            <span class="total-value">
                                                <fmt:formatNumber value="${total}" type="currency" currencySymbol="₫"
                                                    maxFractionDigits="0" />
                                            </span>
                                        </div>

                                        <button onclick="checkout()" class="checkout-btn">
                                            Tiến hành thanh toán
                                        </button>

                                        <p
                                            style="text-align: center; font-size: 0.75rem; color: #9ca3af; margin-top: 1rem;">
                                            Bạn có 30 phút để đến lấy đồ sau khi đặt
                                        </p>

                                        <a href="${pageContext.request.contextPath}/products" class="continue-btn"
                                            style="display: block; text-align: center; text-decoration: none;">
                                            Tiếp tục mua sắm
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </main>

                <script src="${pageContext.request.contextPath}/js/main.js"></script>
                <script>
                    function updateQuantity(productId, newQuantity) {
                        if (newQuantity < 1) {
                            removeItem(productId);
                            return;
                        }

                        Loading.show();

                        API.post('${pageContext.request.contextPath}/api/cart/update', {
                            productId: productId,
                            quantity: newQuantity
                        })
                            .then(data => {
                                window.location.reload();
                            })
                            .catch(error => {
                                Loading.hide();
                                Toast.error('Không thể cập nhật giỏ hàng');
                            });
                    }

                    function removeItem(productId) {
                        if (!confirm('Bạn có chắc muốn xóa sản phẩm này?')) {
                            return;
                        }

                        Loading.show();

                        API.delete('${pageContext.request.contextPath}/api/cart/remove/' + productId)
                            .then(data => {
                                Toast.success('Đã xóa sản phẩm');
                                window.location.reload();
                            })
                            .catch(error => {
                                Loading.hide();
                                Toast.error('Không thể xóa sản phẩm');
                            });
                    }

                    function clearCart() {
                        if (!confirm('Bạn có chắc muốn xóa toàn bộ giỏ hàng?')) {
                            return;
                        }

                        Loading.show();

                        API.delete('${pageContext.request.contextPath}/api/cart/clear')
                            .then(data => {
                                Toast.success('Đã xóa giỏ hàng');
                                window.location.reload();
                            })
                            .catch(error => {
                                Loading.hide();
                                Toast.error('Không thể xóa giỏ hàng');
                            });
                    }

                    function checkout() {
                        window.location.href = '${pageContext.request.contextPath}/checkout';
                    }
                </script>
            </body>

            </html>