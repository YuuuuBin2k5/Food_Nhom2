<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sản phẩm - FreshSave</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
            background: #f8f9fa;
            color: #2c3e50;
            min-height: 100vh;
        }

        .container {
            max-width: 1400px;
            margin: 120px auto 40px;
            padding: 0 20px;
            display: grid;
            grid-template-columns: 280px 1fr;
            gap: 30px;
        }

        /* ============================================
           SIDEBAR - CLEAN & MODERN (THEO ẢNH)
        ============================================ */
        .filters {
            position: sticky;
            top: 120px;
            height: fit-content;
            background: #ffffff;
            border-radius: 12px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
            overflow: hidden;
        }

        .filter-group {
            padding: 24px 20px;
            border-bottom: 1px solid #f0f0f0;
        }

        .filter-group:last-child {
            border-bottom: none;
        }

        .filter-title {
            font-size: 15px;
            font-weight: 700;
            color: #2c3e50;
            margin-bottom: 16px;
        }

        /* CATEGORY LIST */
        .category-list {
            list-style: none;
        }

        .category-item {
            margin-bottom: 4px;
        }

        .category-link {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 12px 16px;
            background: transparent;
            color: #5a6c7d;
            text-decoration: none;
            font-size: 14px;
            font-weight: 500;
            border-radius: 8px;
            transition: all 0.2s ease;
        }

        .category-link:hover {
            background: #f8f9fa;
            color: #2c3e50;
        }

        .category-link.active {
            background: linear-gradient(135deg, #ff6b6b, #ff8e53);
            color: #ffffff;
            font-weight: 600;
            box-shadow: 0 4px 12px rgba(255,107,107,0.3);
        }

        .category-emoji {
            font-size: 20px;
            transition: transform 0.2s;
        }

        .category-link:hover .category-emoji {
            transform: scale(1.1);
        }

        /* PRICE RANGE */
        .price-inputs {
            display: flex;
            align-items: center;
            gap: 12px;
            margin-bottom: 16px;
        }

        .price-input {
            flex: 1;
            background: #f8f9fa;
            border: 1px solid #e1e8ed;
            color: #2c3e50;
            padding: 10px 12px;
            font-size: 14px;
            font-weight: 500;
            outline: none;
            border-radius: 6px;
            transition: all 0.2s;
        }

        .price-input:focus {
            border-color: #ff6b6b;
            background: #ffffff;
            box-shadow: 0 0 0 3px rgba(255,107,107,0.1);
        }

        .price-input::placeholder {
            color: #95a5a6;
        }

        .price-inputs span {
            color: #95a5a6;
            font-weight: 500;
        }

        .btn-apply {
            width: 100%;
            background: #34495e;
            border: none;
            color: #ffffff;
            padding: 12px;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            border-radius: 8px;
            transition: all 0.2s;
        }

        .btn-apply:hover {
            background: #2c3e50;
            transform: translateY(-1px);
            box-shadow: 0 4px 12px rgba(52,73,94,0.3);
        }

        .btn-apply:active {
            transform: translateY(0);
        }

        /* ============================================
           MAIN CONTENT
        ============================================ */
        .main-content {
            min-height: 80vh;
        }

        /* TOP TOOLBAR */
        .top-bar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 20px 24px;
            background: #ffffff;
            border-radius: 12px;
            margin-bottom: 24px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
        }

        .top-bar-left {
            font-size: 14px;
            color: #5a6c7d;
            font-weight: 500;
        }

        .top-bar-left strong {
            color: #2c3e50;
            font-size: 18px;
            font-weight: 700;
        }

        .top-bar-controls {
            display: flex;
            gap: 12px;
            align-items: center;
        }

        /* SEARCH INPUT */
        .search-input {
            background: #f8f9fa;
            border: 1px solid #e1e8ed;
            color: #2c3e50;
            padding: 10px 16px;
            font-size: 14px;
            font-weight: 500;
            width: 260px;
            outline: none;
            border-radius: 8px;
            transition: all 0.2s;
        }

        .search-input:focus {
            border-color: #ff6b6b;
            background: #ffffff;
            box-shadow: 0 0 0 3px rgba(255,107,107,0.1);
            width: 300px;
        }

        .search-input::placeholder {
            color: #95a5a6;
        }

        /* SORT SELECT */
        .sort-select {
            background: #f8f9fa;
            border: 1px solid #e1e8ed;
            color: #2c3e50;
            padding: 10px 36px 10px 16px;
            font-size: 14px;
            font-weight: 500;
            cursor: pointer;
            outline: none;
            border-radius: 8px;
            appearance: none;
            background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath fill='%235a6c7d' d='M6 9L1 4h10z'/%3E%3C/svg%3E");
            background-repeat: no-repeat;
            background-position: right 12px center;
            transition: all 0.2s;
        }

        .sort-select:hover {
            border-color: #ff6b6b;
            background-color: #ffffff;
        }

        .sort-select option {
            background: #ffffff;
            color: #2c3e50;
        }

        /* ============================================
           PRODUCTS GRID
        ============================================ */
        .products-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
            gap: 24px;
        }

        .product-card {
            background: #ffffff;
            border-radius: 12px;
            overflow: hidden;
            cursor: pointer;
            transition: all 0.3s;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
        }

        .product-card:hover {
            transform: translateY(-4px);
            box-shadow: 0 8px 24px rgba(0,0,0,0.12);
        }

        .product-image {
            position: relative;
            width: 100%;
            aspect-ratio: 1;
            overflow: hidden;
        }

        .product-image img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            transition: transform 0.5s;
        }

        .product-card:hover .product-image img {
            transform: scale(1.05);
        }

        .discount-badge {
            position: absolute;
            top: 12px;
            left: 12px;
            background: linear-gradient(135deg, #ff6b6b, #ff8e53);
            color: #ffffff;
            padding: 6px 12px;
            font-size: 12px;
            font-weight: 700;
            border-radius: 6px;
            box-shadow: 0 4px 12px rgba(255,107,107,0.4);
        }

        .product-info {
            padding: 16px;
        }

        .product-name {
            font-size: 14px;
            font-weight: 600;
            color: #2c3e50;
            margin-bottom: 8px;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            line-height: 1.4;
        }

        .product-seller {
            font-size: 12px;
            color: #95a5a6;
            margin-bottom: 12px;
        }

        .product-pricing {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .product-price {
            font-size: 20px;
            font-weight: 700;
            color: #ff6b6b;
        }

        .product-original-price {
            font-size: 13px;
            color: #95a5a6;
            text-decoration: line-through;
            margin-top: 2px;
        }

        /* EMPTY STATE */
        .empty-state {
            text-align: center;
            padding: 80px 20px;
        }

        .empty-state-icon {
            font-size: 64px;
            margin-bottom: 16px;
            opacity: 0.3;
        }

        .empty-state-text {
            font-size: 14px;
            color: #95a5a6;
        }

        /* RESPONSIVE */
        @media (max-width: 1024px) {
            .container {
                grid-template-columns: 1fr;
            }

            .filters {
                position: relative;
                top: 0;
            }

            .top-bar {
                flex-direction: column;
                gap: 12px;
                align-items: stretch;
            }

            .top-bar-controls {
                flex-direction: column;
            }

            .search-input {
                width: 100%;
            }

            .search-input:focus {
                width: 100%;
            }
        }

        @media (max-width: 640px) {
            .products-grid {
                grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
                gap: 16px;
            }

            .container {
                margin-top: 100px;
            }
        }
    </style>
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
                        <input type="text" name="search" class="search-input" placeholder="Tìm kiếm món ăn..."
                            value="${search}">
                    </form>

                    <form action="products" method="GET" id="sortForm" style="margin: 0;">
                        <input type="hidden" name="category" value="${currentCategory}">
                        <input type="hidden" name="search" value="${search}">
                        <select name="sortBy" class="sort-select"
                            onchange="document.getElementById('sortForm').submit()">
                            <option value="newest" ${currentSort eq 'newest' ? 'selected' : '' }>Mới nhất</option>
                            <option value="price_asc" ${currentSort eq 'price_asc' ? 'selected' : '' }>Giá: Thấp → Cao</option>
                            <option value="price_desc" ${currentSort eq 'price_desc' ? 'selected' : '' }>Giá: Cao → Thấp</option>
                            <option value="name_asc" ${currentSort eq 'name_asc' ? 'selected' : '' }>Tên A-Z</option>
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
                                    <c:set var="discount" value="${((product.originalPrice - product.salePrice) / product.originalPrice * 100)}" />
                                    <c:if test="${product.originalPrice > product.salePrice}">
                                        <div class="discount-badge">
                                            -<fmt:formatNumber value="${discount}" maxFractionDigits="0"/>%
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
                                                <fmt:formatNumber value="${product.salePrice}" type="number" groupingUsed="true"/>đ
                                            </span>
                                            <c:if test="${product.originalPrice > product.salePrice}">
                                                <div class="product-original-price">
                                                    <fmt:formatNumber value="${product.originalPrice}" type="number" groupingUsed="true"/>đ
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
