<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>${product.name} - FoodRescue</title>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
                <style>
                    body {
                        margin: 0;
                        padding-top: 96px;
                        background: #f8fafc;
                        min-height: 100vh;
                    }

                    .product-header {
                        background: white;
                        position: sticky;
                        top: 0;
                        z-index: 100;
                        border-bottom: 1px solid #e2e8f0;
                        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
                    }

                    .product-container {
                        max-width: 1280px;
                        margin: 0 auto;
                        padding: 1.5rem 1rem;
                    }

                    .product-grid {
                        display: grid;
                        grid-template-columns: 1fr;
                        gap: 2rem;
                    }

                    @media (min-width: 1024px) {
                        .product-grid {
                            grid-template-columns: 1fr 1fr;
                        }
                    }

                    .product-image-section {
                        position: relative;
                        aspect-ratio: 1;
                        width: 100%;
                        border-radius: 1rem;
                        overflow: hidden;
                        background: white;
                        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                    }

                    .product-image {
                        width: 100%;
                        height: 100%;
                        object-fit: cover;
                    }

                    .discount-badge {
                        position: absolute;
                        top: 1rem;
                        left: 1rem;
                        background: #ef4444;
                        color: white;
                        padding: 0.5rem 1rem;
                        border-radius: 0.5rem;
                        font-weight: 700;
                        font-size: 0.875rem;
                        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                    }

                    .out-of-stock-overlay {
                        position: absolute;
                        inset: 0;
                        background: rgba(0, 0, 0, 0.6);
                        backdrop-filter: blur(4px);
                        display: flex;
                        align-items: center;
                        justify-content: center;
                    }

                    .category-badge {
                        display: inline-block;
                        background: #fed7aa;
                        color: #c2410c;
                        padding: 0.25rem 0.75rem;
                        border-radius: 9999px;
                        font-size: 0.875rem;
                        font-weight: 500;
                    }

                    .product-title {
                        font-size: 2rem;
                        font-weight: 700;
                        color: #111827;
                        line-height: 1.2;
                        margin: 1rem 0;
                    }

                    .price-section {
                        background: linear-gradient(to bottom right, #fff7ed, #fef2f2);
                        border-radius: 1rem;
                        padding: 1.5rem;
                        border: 1px solid #fed7aa;
                        margin: 1.5rem 0;
                    }

                    .price-main {
                        font-size: 2.25rem;
                        font-weight: 700;
                        color: #dc2626;
                    }

                    .price-original {
                        font-size: 1.25rem;
                        color: #9ca3af;
                        text-decoration: line-through;
                        margin-left: 0.75rem;
                    }

                    .info-card {
                        background: white;
                        border-radius: 1rem;
                        padding: 1.5rem;
                        border: 1px solid #e5e7eb;
                        margin: 1.5rem 0;
                    }

                    .info-card h3 {
                        font-weight: 700;
                        color: #111827;
                        margin-bottom: 0.75rem;
                        display: flex;
                        align-items: center;
                        gap: 0.5rem;
                    }

                    .quantity-control {
                        display: flex;
                        align-items: center;
                        border: 2px solid #d1d5db;
                        border-radius: 0.75rem;
                        overflow: hidden;
                    }

                    .quantity-btn {
                        width: 2.5rem;
                        height: 2.5rem;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        color: #4b5563;
                        background: white;
                        border: none;
                        cursor: pointer;
                        transition: background-color 0.2s;
                    }

                    .quantity-btn:hover:not(:disabled) {
                        background: #f3f4f6;
                    }

                    .quantity-btn:disabled {
                        opacity: 0.4;
                        cursor: not-allowed;
                    }

                    .quantity-input {
                        width: 4rem;
                        height: 2.5rem;
                        text-align: center;
                        border: none;
                        border-left: 2px solid #d1d5db;
                        border-right: 2px solid #d1d5db;
                        font-weight: 700;
                        color: #111827;
                    }

                    .quantity-input:focus {
                        outline: none;
                    }

                    .btn-add-cart {
                        padding: 0.75rem 1rem;
                        background: white;
                        border: 2px solid #f97316;
                        color: #ea580c;
                        font-weight: 700;
                        border-radius: 0.75rem;
                        cursor: pointer;
                        transition: all 0.2s;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        gap: 0.5rem;
                    }

                    .btn-add-cart:hover:not(:disabled) {
                        background: #fff7ed;
                    }

                    .btn-buy-now {
                        padding: 0.75rem 1rem;
                        background: linear-gradient(to right, #f97316, #dc2626);
                        color: white;
                        font-weight: 700;
                        border-radius: 0.75rem;
                        border: none;
                        cursor: pointer;
                        transition: all 0.2s;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        gap: 0.5rem;
                        box-shadow: 0 4px 6px rgba(249, 115, 22, 0.3);
                    }

                    .btn-buy-now:hover:not(:disabled) {
                        box-shadow: 0 10px 15px rgba(249, 115, 22, 0.4);
                    }

                    .btn-add-cart:disabled,
                    .btn-buy-now:disabled {
                        opacity: 0.5;
                        cursor: not-allowed;
                    }

                    .shop-card {
                        background: white;
                        border-radius: 1rem;
                        padding: 1.5rem;
                        border: 1px solid #e5e7eb;
                        margin-top: 1rem;
                    }

                    .shop-avatar {
                        width: 4rem;
                        height: 4rem;
                        background: linear-gradient(to bottom right, #fb923c, #ef4444);
                        border-radius: 0.75rem;
                        display: flex;
                        align-items: center;
                        justify-items: center;
                        color: white;
                        font-size: 1.5rem;
                        font-weight: 700;
                        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                    }

                    .status-badge {
                        display: inline-block;
                        padding: 0.375rem 0.75rem;
                        border-radius: 0.5rem;
                        font-size: 0.875rem;
                        font-weight: 500;
                    }

                    .badge-red {
                        background: #fee2e2;
                        color: #991b1b;
                    }

                    .badge-orange {
                        background: #ffedd5;
                        color: #9a3412;
                    }

                    .badge-green {
                        background: #d1fae5;
                        color: #065f46;
                    }

                    .badge-blue {
                        background: #dbeafe;
                        color: #1e40af;
                    }

                    .badge-gray {
                        background: #f3f4f6;
                        color: #374151;
                    }
                </style>
            </head>

            <body>
                <!-- Include Sidebar -->
                <jsp:include page="../common/sidebar.jsp">
                    <jsp:param name="currentPath" value="/products" />
                </jsp:include>

                <!-- Header -->
                <header class="product-header">
                    <div
                        style="max-width: 1280px; margin: 0 auto; padding: 0 1rem; height: 4rem; display: flex; align-items: center;">
                        <a href="${pageContext.request.contextPath}/products"
                            style="display: flex; align-items: center; gap: 0.5rem; color: #374151; text-decoration: none; font-weight: 500; transition: color 0.2s;">
                            <svg style="width: 1.25rem; height: 1.25rem;" fill="none" stroke="currentColor"
                                viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                    d="M10 19l-7-7m0 0l7-7m-7 7h18" />
                            </svg>
                            <span>Quay lại</span>
                        </a>
                    </div>
                </header>

                <c:choose>
                    <c:when test="${not empty product}">
                        <main class="product-container">
                            <div class="product-grid">
                                <!-- Left: Image Section -->
                                <div>
                                    <div class="product-image-section">
                                        <img src="https://placehold.co/600x600/FF6B6B/FFFFFF?text=Food"
                                            alt="${product.name}" class="product-image">
                                        <c:if test="${product.originalPrice > product.salePrice}">
                                            <c:set var="discount"
                                                value="${((product.originalPrice - product.salePrice) / product.originalPrice * 100)}" />
                                            <div class="discount-badge">
                                                -
                                                <fmt:formatNumber value="${discount}" maxFractionDigits="0" />%
                                            </div>
                                        </c:if>
                                        <c:if test="${product.quantity == 0}">
                                            <div class="out-of-stock-overlay">
                                                <span
                                                    style="background: white; color: #111827; padding: 0.75rem 1.5rem; border-radius: 9999px; font-weight: 700; box-shadow: 0 10px 15px rgba(0, 0, 0, 0.2);">
                                                    Hết hàng
                                                </span>
                                            </div>
                                        </c:if>
                                    </div>

                                    <!-- Shop Info Card - Desktop -->
                                    <c:if test="${not empty product.seller}">
                                        <div class="shop-card" style="display: none;">
                                            <div
                                                style="display: flex; align-items: center; gap: 1rem; margin-bottom: 1rem;">
                                                <div class="shop-avatar">
                                                    ${product.seller.shopName.substring(0, 1).toUpperCase()}
                                                </div>
                                                <div style="flex: 1; min-width: 0;">
                                                    <h3
                                                        style="font-weight: 700; color: #111827; font-size: 1.125rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
                                                        ${product.seller.shopName}
                                                    </h3>
                                                    <div
                                                        style="display: flex; align-items: center; gap: 0.5rem; font-size: 0.875rem; color: #6b7280;">
                                                        <span>🏪</span>
                                                        <span>Người bán</span>
                                                    </div>
                                                </div>
                                            </div>

                                            <div
                                                style="display: flex; flex-direction: column; gap: 0.75rem; font-size: 0.875rem;">
                                                <div
                                                    style="display: flex; align-items: center; gap: 0.75rem; color: #4b5563;">
                                                    <span style="color: #f97316;">🕐</span>
                                                    <span>08:00 - 22:00</span>
                                                </div>
                                            </div>
                                        </div>
                                    </c:if>
                                </div>

                                <!-- Right: Product Info -->
                                <div>
                                    <!-- Category Badge -->
                                    <div>
                                        <span class="category-badge">
                                            ${product.category}
                                        </span>
                                    </div>

                                    <!-- Product Name -->
                                    <h1 class="product-title">${product.name}</h1>

                                    <!-- Price Section -->
                                    <div class="price-section">
                                        <div
                                            style="display: flex; align-items: baseline; gap: 0.75rem; margin-bottom: 0.75rem;">
                                            <span class="price-main">
                                                <fmt:formatNumber value="${product.salePrice}" type="currency"
                                                    currencySymbol="₫" maxFractionDigits="0" />
                                            </span>
                                            <c:if test="${product.originalPrice > product.salePrice}">
                                                <span class="price-original">
                                                    <fmt:formatNumber value="${product.originalPrice}" type="currency"
                                                        currencySymbol="₫" maxFractionDigits="0" />
                                                </span>
                                            </c:if>
                                        </div>

                                        <div style="display: flex; flex-wrap: wrap; gap: 0.5rem;">
                                            <span
                                                class="status-badge ${product.quantity > 0 ? 'badge-green' : 'badge-gray'}">
                                                ${product.quantity > 0 ? 'Còn '.concat(product.quantity).concat(' sản
                                                phẩm') : 'Hết hàng'}
                                            </span>
                                            <c:if test="${cartQuantity > 0}">
                                                <span class="status-badge badge-blue">
                                                    ${cartQuantity} trong giỏ hàng
                                                </span>
                                            </c:if>
                                        </div>
                                    </div>

                                    <!-- Description -->
                                    <c:if test="${not empty product.description}">
                                        <div class="info-card">
                                            <h3>
                                                <span>📝</span>
                                                Mô tả sản phẩm
                                            </h3>
                                            <p style="color: #4b5563; line-height: 1.6; white-space: pre-line;">
                                                ${product.description}
                                            </p>
                                        </div>
                                    </c:if>

                                    <!-- Quantity & Actions -->
                                    <div class="info-card">
                                        <c:set var="isAvailable" value="${not empty product && product.quantity > 0}" />

                                        <div
                                            style="display: flex; align-items: center; gap: 1rem; margin-bottom: 1rem;">
                                            <span style="color: #374151; font-weight: 500;">Số lượng:</span>
                                            <div class="quantity-control">
                                                <button class="quantity-btn" onclick="changeQuantity(-1)" ${!isAvailable
                                                    ? 'disabled' : '' }>
                                                    <svg style="width: 1.25rem; height: 1.25rem;" fill="none"
                                                        stroke="currentColor" viewBox="0 0 24 24">
                                                        <path stroke-linecap="round" stroke-linejoin="round"
                                                            stroke-width="2" d="M20 12H4" />
                                                    </svg>
                                                </button>
                                                <input type="number" id="quantity" value="1" min="1" max="999"
                                                    class="quantity-input" onchange="validateQuantity()" ${!isAvailable
                                                    ? 'disabled' : '' }>
                                                <button class="quantity-btn" onclick="changeQuantity(1)" ${!isAvailable
                                                    ? 'disabled' : '' }>
                                                    <svg style="width: 1.25rem; height: 1.25rem;" fill="none"
                                                        stroke="currentColor" viewBox="0 0 24 24">
                                                        <path stroke-linecap="round" stroke-linejoin="round"
                                                            stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                                                    </svg>
                                                </button>
                                            </div>
                                        </div>

                                        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 0.75rem;">
                                            <button class="btn-add-cart" onclick="addToCart(false)" ${!isAvailable
                                                ? 'disabled' : '' }>
                                                <svg style="width: 1.25rem; height: 1.25rem;" fill="none"
                                                    stroke="currentColor" viewBox="0 0 24 24">
                                                    <path stroke-linecap="round" stroke-linejoin="round"
                                                        stroke-width="2"
                                                        d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
                                                </svg>
                                                <span>Thêm vào giỏ</span>
                                            </button>
                                            <button class="btn-buy-now" onclick="addToCart(true)" ${!isAvailable
                                                ? 'disabled' : '' }>
                                                Mua ngay
                                                <svg style="width: 1.25rem; height: 1.25rem;" fill="none"
                                                    stroke="currentColor" viewBox="0 0 24 24">
                                                    <path stroke-linecap="round" stroke-linejoin="round"
                                                        stroke-width="2" d="M14 5l7 7m0 0l-7 7m7-7H3" />
                                                </svg>
                                            </button>
                                        </div>
                                    </div>

                                    <c:if test="${not empty product}">
                                        <script>
                                            // Set max quantity dynamically
                                            document.getElementById('quantity').max = ${ product.quantity };
                                        </script>
                                    </c:if>

                                    <!-- Shop Info Card - Mobile -->
                                    <c:if test="${not empty product.seller}">
                                        <div class="shop-card">
                                            <div
                                                style="display: flex; align-items: center; gap: 1rem; margin-bottom: 1rem;">
                                                <div class="shop-avatar"
                                                    style="width: 3.5rem; height: 3.5rem; font-size: 1.25rem;">
                                                    ${product.seller.shopName.substring(0, 1).toUpperCase()}
                                                </div>
                                                <div style="flex: 1; min-width: 0;">
                                                    <h3
                                                        style="font-weight: 700; color: #111827; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
                                                        ${product.seller.shopName}
                                                    </h3>
                                                    <div
                                                        style="display: flex; align-items: center; gap: 0.5rem; font-size: 0.875rem; color: #6b7280;">
                                                        <span>🏪</span>
                                                        <span>Người bán</span>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </c:if>
                                </div>
                            </div>
                        </main>
                    </c:when>
                    <c:otherwise>
                        <div
                            style="min-height: 100vh; display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 1rem;">
                            <div style="font-size: 4rem; margin-bottom: 1rem;">🔍</div>
                            <h2 style="font-size: 1.5rem; font-weight: 700; color: #1f2937; margin-bottom: 0.5rem;">
                                Không tìm thấy sản phẩm</h2>
                            <a href="${pageContext.request.contextPath}/products"
                                style="margin-top: 1rem; padding: 0.5rem 1.5rem; background: #3b82f6; color: white; border-radius: 0.5rem; text-decoration: none; transition: background 0.2s;">
                                Quay lại danh sách
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>

                <script src="${pageContext.request.contextPath}/js/main.js"></script>
                <script>
                    const maxQuantity = ${ not empty product ?product.quantity: 0};
                    const productId = ${ not empty product ?product.productId: 0};

                    function changeQuantity(delta) {
                        const input = document.getElementById('quantity');
                        let value = parseInt(input.value) || 1;
                        value += delta;

                        if (value >= 1 && value <= maxQuantity) {
                            input.value = value;
                        }
                    }

                    function validateQuantity() {
                        const input = document.getElementById('quantity');
                        let value = parseInt(input.value) || 1;

                        if (value < 1) value = 1;
                        if (value > maxQuantity) value = maxQuantity;

                        input.value = value;
                    }

                    async function addToCart(buyNow) {
                        const quantity = parseInt(document.getElementById('quantity').value);

                        try {
                            showLoading();

                            const result = await apiRequest('${pageContext.request.contextPath}/api/cart/add', {
                                method: 'POST',
                                body: JSON.stringify({
                                    productId: productId,
                                    quantity: quantity
                                })
                            });

                            if (buyNow) {
                                // Mua ngay - chuyển đến trang thanh toán
                                window.location.href = '${pageContext.request.contextPath}/checkout';
                            } else {
                                // Thêm vào giỏ - hiển thị thông báo và cập nhật số lượng
                                showToast('Đã thêm ' + quantity + ' sản phẩm vào giỏ hàng!', 'success');
                                document.getElementById('quantity').value = 1;

                                // Cập nhật số lượng giỏ hàng trong header
                                if (result && result.cartSize) {
                                    const cartBadge = document.querySelector('.cart-badge');
                                    if (cartBadge) {
                                        cartBadge.textContent = result.cartSize;
                                        cartBadge.style.display = result.cartSize > 0 ? 'flex' : 'none';
                                    }
                                }
                            }
                        } catch (error) {
                            showToast('Không thể thêm vào giỏ hàng: ' + error.message, 'error');
                        } finally {
                            hideLoading();
                        }
                    }

                    // Show desktop shop card on large screens
                    if (window.innerWidth >= 1024) {
                        document.querySelectorAll('.shop-card')[0].style.display = 'block';
                        document.querySelectorAll('.shop-card')[1].style.display = 'none';
                    }
                </script>
            </body>

            </html>