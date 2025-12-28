<%@ page contentType="text/html;charset=UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Trang chủ - FreshSave</title>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
                <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&display=swap"
                    rel="stylesheet">
                <style>
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }

                    body {
                        font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
                        background: #ffffff;
                        color: #1f2937;
                        padding-top: 96px;
                    }

                    /* Hero Section */
                    .hero {
                        background: linear-gradient(135deg, #fff7ed 0%, #ffedd5 50%, #fed7aa 100%);
                        padding: 80px 20px;
                        position: relative;
                        overflow: hidden;
                    }

                    .hero::before {
                        content: '';
                        position: absolute;
                        top: -50%;
                        right: -10%;
                        width: 500px;
                        height: 500px;
                        background: radial-gradient(circle, rgba(251, 146, 60, 0.15) 0%, transparent 70%);
                        border-radius: 50%;
                    }

                    .hero-container {
                        max-width: 1200px;
                        margin: 0 auto;
                        display: grid;
                        grid-template-columns: 1fr 1fr;
                        gap: 60px;
                        align-items: center;
                        position: relative;
                        z-index: 1;
                    }

                    .hero-content h1 {
                        font-size: 56px;
                        font-weight: 900;
                        line-height: 1.1;
                        margin-bottom: 24px;
                        color: #111827;
                    }

                    .hero-content .highlight {
                        background: linear-gradient(135deg, #f97316, #ea580c);
                        -webkit-background-clip: text;
                        -webkit-text-fill-color: transparent;
                        background-clip: text;
                    }

                    .hero-content p {
                        font-size: 18px;
                        color: #6b7280;
                        margin-bottom: 32px;
                        line-height: 1.6;
                    }

                    .hero-cta {
                        display: flex;
                        gap: 16px;
                    }

                    .btn-primary {
                        padding: 16px 32px;
                        background: linear-gradient(135deg, #f97316, #ea580c);
                        color: white;
                        text-decoration: none;
                        border-radius: 12px;
                        font-weight: 700;
                        font-size: 16px;
                        transition: transform 0.2s, box-shadow 0.2s;
                        box-shadow: 0 4px 12px rgba(249, 115, 22, 0.3);
                    }

                    .btn-primary:hover {
                        transform: translateY(-2px);
                        box-shadow: 0 6px 20px rgba(249, 115, 22, 0.4);
                    }

                    .btn-secondary {
                        padding: 16px 32px;
                        background: white;
                        color: #f97316;
                        text-decoration: none;
                        border-radius: 12px;
                        font-weight: 700;
                        font-size: 16px;
                        border: 2px solid #f97316;
                        transition: all 0.2s;
                    }

                    .btn-secondary:hover {
                        background: #f97316;
                        color: white;
                    }

                    .hero-image {
                        position: relative;
                    }

                    .hero-image img {
                        width: 100%;
                        border-radius: 24px;
                        box-shadow: 0 20px 60px rgba(0, 0, 0, 0.12);
                    }

                    .stats {
                        display: grid;
                        grid-template-columns: repeat(3, 1fr);
                        gap: 16px;
                        margin-top: 40px;
                    }

                    .stat-card {
                        background: white;
                        padding: 20px;
                        border-radius: 12px;
                        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
                    }

                    .stat-number {
                        font-size: 28px;
                        font-weight: 900;
                        color: #f97316;
                        margin-bottom: 4px;
                    }

                    .stat-label {
                        font-size: 13px;
                        color: #6b7280;
                        font-weight: 600;
                    }

                    /* Features Section */
                    .features {
                        padding: 80px 20px;
                        background: #f9fafb;
                    }

                    .section-title {
                        text-align: center;
                        margin-bottom: 56px;
                    }

                    .section-title h2 {
                        font-size: 36px;
                        font-weight: 900;
                        color: #111827;
                        margin-bottom: 12px;
                    }

                    .section-title p {
                        font-size: 16px;
                        color: #6b7280;
                    }

                    .features-grid {
                        max-width: 1200px;
                        margin: 0 auto;
                        display: grid;
                        grid-template-columns: repeat(4, 1fr);
                        gap: 32px;
                    }

                    .feature-card {
                        background: white;
                        padding: 32px;
                        border-radius: 16px;
                        text-align: center;
                        transition: transform 0.2s, box-shadow 0.2s;
                        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
                    }

                    .feature-card:hover {
                        transform: translateY(-4px);
                        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
                    }

                    .feature-icon {
                        width: 64px;
                        height: 64px;
                        margin: 0 auto 20px;
                        border-radius: 16px;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        font-size: 32px;
                    }

                    .feature-card h3 {
                        font-size: 18px;
                        font-weight: 700;
                        color: #111827;
                        margin-bottom: 8px;
                    }

                    .feature-card p {
                        font-size: 14px;
                        color: #6b7280;
                        line-height: 1.5;
                    }

                    /* Products Section */
                    .products {
                        padding: 80px 20px;
                    }

                    .products-header {
                        max-width: 1200px;
                        margin: 0 auto 48px;
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                    }

                    .products-title h2 {
                        font-size: 36px;
                        font-weight: 900;
                        color: #111827;
                    }

                    .products-title .badge {
                        display: inline-block;
                        background: #fef3c7;
                        color: #92400e;
                        padding: 6px 12px;
                        border-radius: 6px;
                        font-size: 12px;
                        font-weight: 700;
                        margin-bottom: 8px;
                    }

                    .products-grid {
                        max-width: 1200px;
                        margin: 0 auto;
                        display: grid;
                        grid-template-columns: repeat(4, 1fr);
                        gap: 24px;
                    }

                    .product-card {
                        background: white;
                        border-radius: 16px;
                        overflow: hidden;
                        transition: transform 0.2s, box-shadow 0.2s;
                        cursor: pointer;
                        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
                    }

                    .product-card:hover {
                        transform: translateY(-4px);
                        box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);
                    }

                    .product-image {
                        position: relative;
                        padding-top: 75%;
                        overflow: hidden;
                        background: #f3f4f6;
                    }

                    .product-image img {
                        position: absolute;
                        top: 0;
                        left: 0;
                        width: 100%;
                        height: 100%;
                        object-fit: cover;
                    }

                    .discount-badge {
                        position: absolute;
                        top: 12px;
                        left: 12px;
                        background: #ef4444;
                        color: white;
                        padding: 6px 12px;
                        border-radius: 8px;
                        font-weight: 700;
                        font-size: 14px;
                    }

                    .product-info {
                        padding: 20px;
                    }

                    .product-name {
                        font-size: 16px;
                        font-weight: 700;
                        color: #111827;
                        margin-bottom: 8px;
                        display: -webkit-box;
                        -webkit-line-clamp: 2;
                        -webkit-box-orient: vertical;
                        overflow: hidden;
                    }

                    .product-shop {
                        font-size: 13px;
                        color: #6b7280;
                        margin-bottom: 12px;
                    }

                    .product-price {
                        display: flex;
                        align-items: center;
                        gap: 8px;
                    }

                    .price-current {
                        font-size: 20px;
                        font-weight: 900;
                        color: #ef4444;
                    }

                    .price-original {
                        font-size: 14px;
                        color: #9ca3af;
                        text-decoration: line-through;
                    }

                    @media (max-width: 1024px) {
                        .hero-container {
                            grid-template-columns: 1fr;
                            gap: 40px;
                        }

                        .hero-content h1 {
                            font-size: 42px;
                        }

                        .features-grid {
                            grid-template-columns: repeat(2, 1fr);
                        }

                        .products-grid {
                            grid-template-columns: repeat(3, 1fr);
                        }
                    }

                    @media (max-width: 768px) {
                        .hero-content h1 {
                            font-size: 36px;
                        }

                        .products-grid {
                            grid-template-columns: repeat(2, 1fr);
                        }

                        .stats {
                            grid-template-columns: repeat(3, 1fr);
                        }
                    }
                </style>
            </head>

            <body>
                <!-- Include Sidebar -->
                <jsp:include page="../common/sidebar.jsp">
                    <jsp:param name="currentPath" value="/" />
                </jsp:include>

                <!-- Hero Section -->
                <section class="hero">
                    <div class="hero-container">
                        <div class="hero-content">
                            <h1>
                                Thực phẩm tươi ngon<br>
                                giá <span class="highlight">siêu rẻ</span>
                            </h1>
                            <p>
                                Mua sắm thông minh với sản phẩm chất lượng cao.
                                Tiết kiệm đến <strong>70%</strong> và giảm lãng phí thực phẩm.
                            </p>
                            <div class="hero-cta">
                                <a href="${pageContext.request.contextPath}/products" class="btn-primary">
                                    Khám phá ngay →
                                </a>
                                <a href="${pageContext.request.contextPath}/products" class="btn-secondary">
                                    Xem deal hot
                                </a>
                            </div>

                            <div class="stats">
                                <div class="stat-card">
                                    <div class="stat-number">${productsCount > 99 ? '99+' : productsCount}</div>
                                    <div class="stat-label">Sản phẩm</div>
                                </div>
                                <div class="stat-card">
                                    <div class="stat-number">2H</div>
                                    <div class="stat-label">Giao hàng</div>
                                </div>
                                <div class="stat-card">
                                    <div class="stat-number">70%</div>
                                    <div class="stat-label">Tiết kiệm</div>
                                </div>
                            </div>
                        </div>

                        <div class="hero-image">
                            <c:choose>
                                <c:when test="${not empty products and products.size() > 0}">
                                    <img src="${products[0].imageUrl != null ? products[0].imageUrl : 'https://images.unsplash.com/photo-1542838132-92c53300491e?w=800'}"
                                        alt="Featured product">
                                </c:when>
                                <c:otherwise>
                                    <img src="https://images.unsplash.com/photo-1542838132-92c53300491e?w=800"
                                        alt="Fresh food">
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </section>

                <!-- Features Section -->
                <section class="features">
                    <div class="section-title">
                        <h2>Tại sao chọn FreshSave?</h2>
                        <p>Giải pháp mua sắm thông minh cho bạn</p>
                    </div>

                    <div class="features-grid">
                        <div class="feature-card">
                            <div class="feature-icon" style="background: linear-gradient(135deg, #fbbf24, #f59e0b);">
                                ⏰
                            </div>
                            <h3>Tươi ngon đảm bảo</h3>
                            <p>Sản phẩm gần hết hạn nhưng vẫn giữ được chất lượng tuyệt đối</p>
                        </div>

                        <div class="feature-card">
                            <div class="feature-icon" style="background: linear-gradient(135deg, #34d399, #10b981);">
                                📉
                            </div>
                            <h3>Giá siêu rẻ</h3>
                            <p>Tiết kiệm tới 70% so với giá thông thường trên thị trường</p>
                        </div>

                        <div class="feature-card">
                            <div class="feature-icon" style="background: linear-gradient(135deg, #60a5fa, #3b82f6);">
                                🚚
                            </div>
                            <h3>Giao hàng nhanh</h3>
                            <p>Miễn phí ship cho đơn từ 200.000đ, giao trong 2 giờ</p>
                        </div>

                        <div class="feature-card">
                            <div class="feature-icon" style="background: linear-gradient(135deg, #f472b6, #ec4899);">
                                ❤️
                            </div>
                            <h3>Bảo vệ môi trường</h3>
                            <p>Góp phần giảm thiểu lãng phí thực phẩm hiệu quả</p>
                        </div>
                    </div>
                </section>

                <!-- Products Section -->
                <section class="products">
                    <div class="products-header">
                        <div class="products-title">
                            <span class="badge">🔥 DEAL HOT</span>
                            <h2>Sản phẩm nổi bật</h2>
                        </div>
                        <a href="${pageContext.request.contextPath}/products" class="btn-primary">
                            Xem tất cả →
                        </a>
                    </div>

                    <c:choose>
                        <c:when test="${not empty products}">
                            <div class="products-grid">
                                <c:forEach items="${products}" var="product" begin="0" end="7">
                                    <div class="product-card"
                                        onclick="location.href='${pageContext.request.contextPath}/product?id=${product.productId}'">
                                        <div class="product-image">
                                            <c:set var="discount"
                                                value="${((product.originalPrice - product.salePrice) / product.originalPrice * 100)}" />
                                            <c:if test="${product.originalPrice > product.salePrice}">
                                                <div class="discount-badge">
                                                    -
                                                    <fmt:formatNumber value="${discount}" maxFractionDigits="0" />%
                                                </div>
                                            </c:if>
                                            <img src="${product.imageUrl != null ? product.imageUrl : 'https://placehold.co/400x300/f97316/FFFFFF?text=Food'}"
                                                alt="${product.name}">
                                        </div>
                                        <div class="product-info">
                                            <h3 class="product-name">${product.name}</h3>
                                            <p class="product-shop">🏪 ${product.seller.shopName}</p>
                                            <div class="product-price">
                                                <span class="price-current">
                                                    <fmt:formatNumber value="${product.salePrice}" type="number"
                                                        groupingUsed="true" />₫
                                                </span>
                                                <c:if test="${product.originalPrice > product.salePrice}">
                                                    <span class="price-original">
                                                        <fmt:formatNumber value="${product.originalPrice}" type="number"
                                                            groupingUsed="true" />₫
                                                    </span>
                                                </c:if>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div style="text-align: center; padding: 80px 20px; color: #9ca3af;">
                                <div style="font-size: 64px; margin-bottom: 16px;">🛒</div>
                                <p>Chưa có sản phẩm nào</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </section>

                <!-- Footer -->
                <jsp:include page="../common/footer.jsp" />
            </body>

            </html>