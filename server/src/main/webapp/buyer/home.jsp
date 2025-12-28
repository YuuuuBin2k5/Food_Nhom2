<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

            <jsp:include page="header.jsp" />

            <style>
                .home-container {
                    display: flex;
                    gap: 30px;
                    padding-top: 40px;
                    padding-bottom: 60px;
                    max-width: 1200px;
                    margin: 0 auto;
                }

                /* Sidebar Filters */
                .filters {
                    width: 250px;
                    flex-shrink: 0;
                    background: #fff;
                    padding: 20px;
                    border-radius: 12px;
                    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
                    height: fit-content;
                }

                .filter-group {
                    margin-bottom: 25px;
                }

                .filter-title {
                    font-size: 1.1rem;
                    font-weight: 600;
                    margin-bottom: 15px;
                    color: #2c3e50;
                    border-bottom: 2px solid #eee;
                    padding-bottom: 8px;
                }

                .category-list {
                    list-style: none;
                    padding: 0;
                }

                .category-item {
                    margin-bottom: 8px;
                }

                .category-link {
                    display: flex;
                    align-items: center;
                    text-decoration: none;
                    color: #555;
                    padding: 8px 12px;
                    border-radius: 8px;
                    transition: all 0.2s;
                }

                .category-link:hover {
                    background: #f8f9fa;
                    color: #e74c3c;
                }

                .category-link.active {
                    background: #e74c3c;
                    color: #fff;
                }

                .category-emoji {
                    margin-right: 10px;
                }

                /* Price Input */
                .price-inputs {
                    display: flex;
                    gap: 10px;
                    align-items: center;
                }

                .price-input {
                    width: 80px;
                    padding: 6px;
                    border: 1px solid #ddd;
                    border-radius: 4px;
                    font-size: 0.9rem;
                }

                .btn-apply {
                    margin-top: 10px;
                    width: 100%;
                    padding: 8px;
                    background-color: #34495e;
                    color: white;
                    border: none;
                    border-radius: 6px;
                    cursor: pointer;
                }

                .btn-apply:hover {
                    background-color: #2c3e50;
                }

                /* Main Content */
                .main-content {
                    flex: 1;
                }

                /* Top Bar */
                .top-bar {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 25px;
                    background: #fff;
                    padding: 15px 20px;
                    border-radius: 12px;
                    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
                }

                .sort-select {
                    padding: 8px 12px;
                    border-radius: 6px;
                    border: 1px solid #ddd;
                    outline: none;
                }

                /* Override product-grid from style.css for better grid */
                .product-grid {
                    display: grid;
                    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
                    gap: 25px;
                }

                .product-card {
                    border: none;
                    transition: transform 0.3s, box-shadow 0.3s;
                }

                .product-card:hover {
                    transform: translateY(-5px);
                    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
                }

                .search-input {
                    padding: 8px 15px;
                    border: 1px solid #ddd;
                    border-radius: 20px;
                    width: 300px;
                    outline: none;
                    transition: border 0.3s;
                }

                .search-input:focus {
                    border-color: #e74c3c;
                }
            </style>

            <!-- Hero Section (Compact) -->
            <div class="hero" style="padding: 40px 0; min-height: auto; background-position: center bottom;">
                <div class="container text-center">
                    <h1>Delicious Food Delivered</h1>
                    <p>Choose directly from our menu</p>
                </div>
            </div>

            <div class="home-container">

                <!-- LEFT SIDEBAR -->
                <aside class="filters">
                    <!-- Categories -->
                    <div class="filter-group">
                        <div class="filter-title">Danh mục món ăn</div>
                        <ul class="category-list">
                            <li class="category-item">
                                <a href="home" class="category-link ${empty currentCategory ? 'active' : ''}">
                                    All Products
                                </a>
                            </li>
                            <c:forEach var="cat" items="${categories}">
                                <li class="category-item">
                                    <a href="home?category=${cat.name()}&sortBy=${currentSort}&search=${search}"
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
                        <form action="home" method="GET">
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
                        <div>
                            <strong>${products.size()}</strong> món ăn được tìm thấy
                        </div>

                        <div style="display: flex; gap: 15px; align-items: center;">
                            <form action="home" method="GET" style="margin: 0;">
                                <input type="hidden" name="category" value="${currentCategory}">
                                <input type="hidden" name="sortBy" value="${currentSort}">
                                <input type="text" name="search" class="search-input" placeholder="Tìm kiếm món ăn..."
                                    value="${search}">
                            </form>

                            <form action="home" method="GET" id="sortForm" style="margin: 0;">
                                <input type="hidden" name="category" value="${currentCategory}">
                                <input type="hidden" name="search" value="${search}">
                                <select name="sortBy" class="sort-select"
                                    onchange="document.getElementById('sortForm').submit()">
                                    <option value="newest" ${currentSort eq 'newest' ? 'selected' : '' }>Mới nhất
                                    </option>
                                    <option value="price_asc" ${currentSort eq 'price_asc' ? 'selected' : '' }>Giá: Thấp
                                        đến Cao</option>
                                    <option value="price_desc" ${currentSort eq 'price_desc' ? 'selected' : '' }>Giá:
                                        Cao đến Thấp</option>
                                    <option value="name_asc" ${currentSort eq 'name_asc' ? 'selected' : '' }>Tên A-Z
                                    </option>
                                </select>
                            </form>
                        </div>
                    </div>

                    <!-- Product Grid -->
                    <div class="product-grid">
                        <c:choose>
                            <c:when test="${not empty products}">
                                <c:forEach var="p" items="${products}">
                                    <div class="product-card">
                                        <div style="position: relative;">
                                            <img src="${not empty p.imageUrl ? p.imageUrl : 'https://placehold.co/300x200?text=Food'}"
                                                alt="${p.name}" class="product-image"
                                                style="height: 180px; object-fit: cover;">
                                            <c:if test="${p.originalPrice > p.salePrice}">
                                                <span
                                                    style="position: absolute; top: 10px; right: 10px; background: #e74c3c; color: #fff; padding: 2px 8px; border-radius: 4px; font-size: 0.8rem; font-weight: bold;">
                                                    Sale
                                                </span>
                                            </c:if>
                                        </div>

                                        <div class="product-info">
                                            <div>
                                                <div class="product-title"
                                                    style="font-size: 1.1rem; margin-bottom: 5px;">
                                                    <a href="product-detail?id=${p.productId}"
                                                        style="text-decoration: none; color: inherit;">
                                                        ${p.name}
                                                    </a>
                                                </div>
                                                <div class="product-shop" style="color: #7f8c8d; font-size: 0.9rem;">
                                                    <i class="fas fa-store"></i> ${p.seller.shopName}
                                                </div>
                                            </div>
                                            <div style="margin-top: 15px;">
                                                <div class="product-price"
                                                    style="color: #e74c3c; font-size: 1.2rem; font-weight: bold;">
                                                    <fmt:formatNumber value="${p.salePrice}" pattern="#,##0" /> đ
                                                    <c:if test="${p.originalPrice > p.salePrice}">
                                                        <span
                                                            style="text-decoration: line-through; color: #bdc3c7; font-size: 0.9rem; margin-left: 5px;">
                                                            <fmt:formatNumber value="${p.originalPrice}"
                                                                pattern="#,##0" />
                                                        </span>
                                                    </c:if>
                                                </div>
                                                <a href="product-detail?id=${p.productId}" class="btn btn-block"
                                                    style="margin-top: 10px;">
                                                    Xem chi tiết
                                                </a>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <div style="grid-column: 1 / -1; text-align: center; padding: 50px;">
                                    <img src="https://cdni.iconscout.com/illustration/premium/thumb/empty-cart-2130356-1800917.png"
                                        alt="Empty" style="width: 200px; opacity: 0.5;">
                                    <h3>Không tìm thấy món nào :(</h3>
                                    <p>Hãy thử tìm kiếm từ khóa khác xem sao</p>
                                    <a href="home" class="btn btn-secondary">Xem tất cả</a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Pagination -->
                    <div class="text-center mt-20">
                        <c:if test="${not empty currentPage and not empty totalPages and totalPages > 1}">
                            <div class="pagination" style="display: flex; justify-content: center; gap: 10px;">
                                <c:if test="${currentPage > 0}">
                                    <a href="?page=${currentPage - 1}&category=${currentCategory}&search=${search}&sortBy=${currentSort}"
                                        class="btn btn-secondary">Previous</a>
                                </c:if>

                                <span style="padding: 10px 15px; background: #eee; border-radius: 6px;">
                                    Page ${currentPage + 1} / ${totalPages}
                                </span>

                                <c:if test="${currentPage < totalPages - 1}">
                                    <a href="?page=${currentPage + 1}&category=${currentCategory}&search=${search}&sortBy=${currentSort}"
                                        class="btn btn-secondary">Next</a>
                                </c:if>
                            </div>
                        </c:if>
                    </div>
                </main>
            </div>

            <jsp:include page="footer.jsp" />