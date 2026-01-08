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
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sidebar.css">
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
                    <div class="features-container">
                        <div class="features-intro">
                            <span class="features-label">Tại sao?</span>
                            <h2>Hôm nay mua,<br>ngày mai ăn</h2>
                            <p>Thực phẩm sắp hết hạn không có nghĩa là hỏng. Chúng tôi giúp bạn tiết kiệm tiền và giảm
                                lãng phí.</p>
                        </div>

                        <div class="benefits-list">
                            <div class="benefit-item">
                                <div class="benefit-number">01</div>
                                <div class="benefit-content">
                                    <h3>Sắp hết hạn = Vẫn tươi ngon</h3>
                                    <p>Hạn sử dụng gần đến không đồng nghĩa với kém chất lượng. Bạn vẫn được sản phẩm
                                        tươi ngon, chỉ cần dùng sớm hơn một chút.</p>
                                </div>
                            </div>

                            <div class="benefit-item">
                                <div class="benefit-number">02</div>
                                <div class="benefit-content">
                                    <h3>Giảm thẳng 50-70%</h3>
                                    <p>Không phải trả giá đắt cho thực phẩm bạn sẽ ăn ngay. Tiết kiệm hàng trăm ngàn mỗi
                                        tuần cho hóa đơn chợ.</p>
                                </div>
                            </div>

                            <div class="benefit-item">
                                <div class="benefit-number">03</div>
                                <div class="benefit-content">
                                    <h3>Đặt trưa, chiều có</h3>
                                    <p>Miễn phí giao hàng cho đơn từ 200k. Giao nhanh trong 2 giờ khu vực nội thành.</p>
                                </div>
                            </div>

                            <div class="benefit-item">
                                <div class="benefit-number">04</div>
                                <div class="benefit-content">
                                    <h3>Giảm rác thải thực phẩm</h3>
                                    <p>Mỗi đơn hàng là một phần thức ăn được cứu khỏi đổ bỏ. Bạn mua, môi trường được
                                        hưởng lợi.</p>
                                </div>
                            </div>
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