<%@ page contentType="text/html;charset=UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Trang chủ - FoodRescue</title>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/buyer/home.css">
                <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&display=swap"
                    rel="stylesheet">
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
                        <h2>Tại sao chọn FoodRescue?</h2>
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