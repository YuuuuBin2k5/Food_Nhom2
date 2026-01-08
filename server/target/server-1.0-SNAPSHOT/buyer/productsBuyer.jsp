<%@ page contentType="text/html;charset=UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Sản phẩm - FoodRescue</title>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sidebar.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/buyer/products.css">
            </head>

            <body>
                <!-- Include Sidebar -->
                <jsp:include page="../common/sidebar.jsp">
                    <jsp:param name="currentPath" value="/products" />
                </jsp:include>

                <div class="container">
                    <!-- LEFT SIDEBAR -->
                    <aside class="filters">
                        <!-- Categories -->
                        <div class="filter-group">
                            <div class="filter-title">Danh mục món ăn</div>
                            <ul class="category-list">
                                <li class="category-item">
                                    <a href="products" class="category-link ${empty currentCategory ? 'active' : ''}">
                                        All Products
                                    </a>
                                </li>
                                <c:forEach var="cat" items="${categories}">
                                    <li class="category-item">
                                        <a href="products?category=${cat.name()}&sortBy=${currentSort}&search=${search}"
                                            class="category-link ${currentCategory eq cat.name() ? 'active' : ''}">
                                            <span class="category-emoji">${cat.getEmoji()}</span>
                                            ${cat.getDisplayName()}
                                        </a>
                                    </li>
                                </c:forEach>
                            </ul>
                        </div>

                        <!-- Price Range -->
                        <div class="filter-group">
                            <div class="filter-title">Khoảng giá</div>
                            <form action="products" method="GET">
                                <input type="hidden" name="category" value="${currentCategory}">
                                <input type="hidden" name="search" value="${search}">
                                <input type="hidden" name="sortBy" value="${currentSort}">
                                <div class="price-inputs">
                                    <input type="number" name="minPrice" class="price-input" placeholder="Min"
                                        value="${param.minPrice}">
                                    <span>-</span>
                                    <input type="number" name="maxPrice" class="price-input" placeholder="Max"
                                        value="${param.maxPrice}">
                                </div>
                                <button type="submit" class="btn-apply">Áp dụng</button>
                            </form>
                        </div>
                    </aside>

                    <!-- MAIN CONTENT -->
                    <main class="main-content">
                        <!-- Top Toolbar -->
                        <div class="top-bar">
                            <div class="top-bar-left">
                                <strong>${products.size()}</strong> món ăn được tìm thấy
                            </div>

                            <div class="top-bar-controls">
                                <form action="products" method="GET" style="margin: 0;">
                                    <input type="hidden" name="category" value="${currentCategory}">
                                    <input type="hidden" name="sortBy" value="${currentSort}">
                                    <input type="text" name="search" class="search-input"
                                        placeholder="Tìm kiếm món ăn..." value="${search}">
                                </form>

                                <form action="products" method="GET" id="sortForm" style="margin: 0;">
                                    <input type="hidden" name="category" value="${currentCategory}">
                                    <input type="hidden" name="search" value="${search}">
                                    <select name="sortBy" class="sort-select"
                                        onchange="document.getElementById('sortForm').submit()">
                                        <option value="newest" ${currentSort eq 'newest' ? 'selected' : '' }>Mới nhất
                                        </option>
                                        <option value="price_asc" ${currentSort eq 'price_asc' ? 'selected' : '' }>Giá:
                                            Thấp → Cao</option>
                                        <option value="price_desc" ${currentSort eq 'price_desc' ? 'selected' : '' }>
                                            Giá: Cao → Thấp</option>
                                        <option value="name_asc" ${currentSort eq 'name_asc' ? 'selected' : '' }>Tên A-Z
                                        </option>
                                    </select>
                                </form>
                            </div>
                        </div>

                        <!-- Products Grid -->
                        <c:choose>
                            <c:when test="${not empty products}">
                                <div class="products-grid">
                                    <c:forEach items="${products}" var="product">
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
                                                <img src="${product.imageUrl != null ? product.imageUrl : 'https://placehold.co/400x400/f8f9fa/ff6b6b?text=FOOD'}"
                                                    alt="${product.name}">
                                            </div>
                                            <div class="product-info">
                                                <h3 class="product-name">${product.name}</h3>
                                                <p class="product-seller">🏪 ${product.seller.shopName}</p>
                                                <div class="product-pricing">
                                                    <div>
                                                        <span class="product-price">
                                                            <fmt:formatNumber value="${product.salePrice}" type="number"
                                                                groupingUsed="true" />đ
                                                        </span>
                                                        <c:if test="${product.originalPrice > product.salePrice}">
                                                            <div class="product-original-price">
                                                                <fmt:formatNumber value="${product.originalPrice}"
                                                                    type="number" groupingUsed="true" />đ
                                                            </div>
                                                        </c:if>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="empty-state">
                                    <div class="empty-state-icon">🔍</div>
                                    <p class="empty-state-text">Không tìm thấy sản phẩm</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </main>
                </div>

                <script src="${pageContext.request.contextPath}/js/main.js"></script>
            </body>

            </html>