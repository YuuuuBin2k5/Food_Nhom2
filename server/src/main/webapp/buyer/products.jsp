<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Danh sách sản phẩm - E-Commerce</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body style="background: linear-gradient(to bottom right, #fff7ed, #fef3c7, #fef9c3); min-height: 100vh;">
    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="currentPath" value="/products" />
    </jsp:include>
    
    <!-- Hero Banner -->
    <div style="position: relative; background: linear-gradient(to right, #FF6B6B, #FF8E53, #FFC75F); padding: 3rem 0; overflow: hidden; margin-top: 96px;">
        <div style="position: absolute; inset: 0; opacity: 0.1;">
            <div style="position: absolute; top: 2.5rem; left: 2.5rem; width: 8rem; height: 8rem; background: white; border-radius: 9999px; filter: blur(3rem);"></div>
            <div style="position: absolute; bottom: 2.5rem; right: 2.5rem; width: 10rem; height: 10rem; background: white; border-radius: 9999px; filter: blur(3rem);"></div>
        </div>
        
        <div style="max-width: 80rem; margin: 0 auto; padding: 0 1rem; position: relative; z-index: 10;">
            <div style="text-align: center;">
                <div style="display: inline-block; margin-bottom: 1rem;">
                    <span style="background: rgba(255,255,255,0.2); backdrop-filter: blur(12px); color: white; padding: 0.5rem 1rem; border-radius: 0.25rem; font-size: 0.875rem; font-weight: 600;">
                        🌱 Giải cứu thực phẩm, bảo vệ môi trường
                    </span>
                </div>
                <h2 style="font-size: 2.25rem; font-weight: 900; color: white; margin-bottom: 1rem; line-height: 1.2;">
                    Thực phẩm tươi ngon,<br/> giá siêu hời!
                </h2>
                <p style="color: rgba(255,255,255,0.9); font-size: 1.125rem; max-width: 42rem; margin: 0 auto 2rem;">
                    Mua thực phẩm sắp hết hạn với giá giảm đến <span style="font-weight: bold; color: #fef08a;">70%</span>. 
                    Vừa tiết kiệm, vừa bảo vệ môi trường!
                </p>

                <div style="max-width: 42rem; margin: 0 auto;">
                    <form method="GET" action="${pageContext.request.contextPath}/products" style="display: flex; gap: 0.5rem;">
                        <input type="text" 
                               name="search" 
                               placeholder="Tìm kiếm sản phẩm..." 
                               value="${search}"
                               style="flex: 1; padding: 0.875rem 1rem; border: none; border-radius: 0.75rem; font-size: 1rem; box-shadow: 0 4px 6px rgba(0,0,0,0.1);">
                        <button type="submit" style="padding: 0.875rem 1.5rem; background: white; color: #FF6B6B; font-weight: 600; border: none; border-radius: 0.75rem; cursor: pointer; box-shadow: 0 4px 6px rgba(0,0,0,0.1);">
                            🔍 Tìm
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Stats Bar -->
    <div style="background: white; box-shadow: 0 4px 6px rgba(0,0,0,0.05); border-bottom: 1px solid #f3f4f6;">
        <div style="max-width: 80rem; margin: 0 auto; padding: 1.5rem 1rem;">
            <div style="display: grid; grid-template-columns: repeat(4, 1fr); gap: 1.5rem;">
                <div style="text-align: center;">
                    <div style="display: flex; align-items: center; justify-content: center; gap: 0.75rem; margin-bottom: 0.5rem;">
                        <div style="width: 3rem; height: 3rem; border-radius: 0.75rem; background: linear-gradient(to bottom right, #fed7aa, #fde68a); display: flex; align-items: center; justify-content: center; font-size: 1.5rem;">
                            🍎
                        </div>
                        <div style="text-align: left;">
                            <div style="font-size: 1.5rem; font-weight: bold; color: #FF6B6B;">${productsCount}+</div>
                            <div style="font-size: 0.75rem; color: #334155;">Sản phẩm</div>
                        </div>
                    </div>
                </div>
                <div style="text-align: center;">
                    <div style="display: flex; align-items: center; justify-content: center; gap: 0.75rem; margin-bottom: 0.5rem;">
                        <div style="width: 3rem; height: 3rem; border-radius: 0.75rem; background: linear-gradient(to bottom right, #fed7aa, #fde68a); display: flex; align-items: center; justify-content: center; font-size: 1.5rem;">
                            🏪
                        </div>
                        <div style="text-align: left;">
                            <div style="font-size: 1.5rem; font-weight: bold; color: #FF8E53;">56</div>
                            <div style="font-size: 0.75rem; color: #334155;">Cửa hàng</div>
                        </div>
                    </div>
                </div>
                <div style="text-align: center;">
                    <div style="display: flex; align-items: center; justify-content: center; gap: 0.75rem; margin-bottom: 0.5rem;">
                        <div style="width: 3rem; height: 3rem; border-radius: 0.75rem; background: linear-gradient(to bottom right, #fed7aa, #fde68a); display: flex; align-items: center; justify-content: center; font-size: 1.5rem;">
                            🌍
                        </div>
                        <div style="text-align: left;">
                            <div style="font-size: 1.5rem; font-weight: bold; color: #10B981;">5.2 tấn</div>
                            <div style="font-size: 0.75rem; color: #334155;">Đã cứu</div>
                        </div>
                    </div>
                </div>
                <div style="text-align: center;">
                    <div style="display: flex; align-items: center; justify-content: center; gap: 0.75rem; margin-bottom: 0.5rem;">
                        <div style="width: 3rem; height: 3rem; border-radius: 0.75rem; background: linear-gradient(to bottom right, #fed7aa, #fde68a); display: flex; align-items: center; justify-content: center; font-size: 1.5rem;">
                            💰
                        </div>
                        <div style="text-align: left;">
                            <div style="font-size: 1.5rem; font-weight: bold; color: #FFC75F;">-70%</div>
                            <div style="font-size: 0.75rem; color: #334155;">Giảm đến</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Main Content -->
    <main style="max-width: 80rem; margin: 0 auto; padding: 2rem 1rem;">
        <div style="display: flex; gap: 1.5rem;">
            <!-- Sidebar Filter -->
            <aside style="width: 20rem; flex-shrink: 0;">
                <div style="position: sticky; top: 6rem; background: white; border-radius: 0.5rem; box-shadow: 0 10px 15px rgba(0,0,0,0.1); border: 1px solid #f3f4f6; overflow: hidden;">
                    <!-- Header -->
                    <div style="background: linear-gradient(to right, #FF6B6B, #FF8E53); padding: 0.75rem;">
                        <h2 style="color: white; font-weight: bold; font-size: 1.125rem; margin: 0;">
                            🔍 Bộ lọc sản phẩm
                        </h2>
                    </div>

                    <div style="padding: 1.25rem;">
                        <form method="GET" action="${pageContext.request.contextPath}/products">
                            <!-- Category Filter -->
                            <div style="margin-bottom: 1.5rem;">
                                <h3 style="font-weight: bold; color: #111827; margin-bottom: 0.75rem;">
                                    🏷️ Danh mục
                                </h3>
                                <div style="display: flex; flex-wrap: wrap; gap: 0.5rem;">
                                    <button type="submit" name="category" value="" 
                                            style="padding: 0.375rem 0.75rem; border-radius: 0.25rem; font-size: 0.875rem; font-weight: 500; border: none; cursor: pointer; ${empty category ? 'background: linear-gradient(to right, #FF6B6B, #FF8E53); color: white;' : 'background: #f3f4f6; color: #374151;'}">
                                        Tất cả
                                    </button>
                                    <button type="submit" name="category" value="ELECTRONICS"
                                            style="padding: 0.375rem 0.75rem; border-radius: 0.25rem; font-size: 0.875rem; font-weight: 500; border: none; cursor: pointer; ${category == 'ELECTRONICS' ? 'background: linear-gradient(to right, #FF6B6B, #FF8E53); color: white;' : 'background: #f3f4f6; color: #374151;'}">
                                        📱 Điện tử
                                    </button>
                                    <button type="submit" name="category" value="FASHION"
                                            style="padding: 0.375rem 0.75rem; border-radius: 0.25rem; font-size: 0.875rem; font-weight: 500; border: none; cursor: pointer; ${category == 'FASHION' ? 'background: linear-gradient(to right, #FF6B6B, #FF8E53); color: white;' : 'background: #f3f4f6; color: #374151;'}">
                                        👕 Thời trang
                                    </button>
                                    <button type="submit" name="category" value="HOME"
                                            style="padding: 0.375rem 0.75rem; border-radius: 0.25rem; font-size: 0.875rem; font-weight: 500; border: none; cursor: pointer; ${category == 'HOME' ? 'background: linear-gradient(to right, #FF6B6B, #FF8E53); color: white;' : 'background: #f3f4f6; color: #374151;'}">
                                        🏠 Gia dụng
                                    </button>
                                    <button type="submit" name="category" value="BOOKS"
                                            style="padding: 0.375rem 0.75rem; border-radius: 0.25rem; font-size: 0.875rem; font-weight: 500; border: none; cursor: pointer; ${category == 'BOOKS' ? 'background: linear-gradient(to right, #FF6B6B, #FF8E53); color: white;' : 'background: #f3f4f6; color: #374151;'}">
                                        📚 Sách
                                    </button>
                                    <button type="submit" name="category" value="SPORTS"
                                            style="padding: 0.375rem 0.75rem; border-radius: 0.25rem; font-size: 0.875rem; font-weight: 500; border: none; cursor: pointer; ${category == 'SPORTS' ? 'background: linear-gradient(to right, #FF6B6B, #FF8E53); color: white;' : 'background: #f3f4f6; color: #374151;'}">
                                        ⚽ Thể thao
                                    </button>
                                </div>
                            </div>

                            <!-- Sort -->
                            <div>
                                <h3 style="font-weight: bold; color: #111827; margin-bottom: 0.75rem;">
                                    📊 Sắp xếp
                                </h3>
                                <select name="sortBy" onchange="this.form.submit()"
                                        style="width: 100%; padding: 0.625rem; border: 1px solid #e5e7eb; border-radius: 0.75rem; font-size: 0.875rem; color: #0f172a; font-weight: 500; background: white; cursor: pointer;">
                                    <option value="">Mới nhất</option>
                                    <option value="price_asc" ${sortBy == 'price_asc' ? 'selected' : ''}>Giá: Thấp → Cao</option>
                                    <option value="price_desc" ${sortBy == 'price_desc' ? 'selected' : ''}>Giá: Cao → Thấp</option>
                                    <option value="name" ${sortBy == 'name' ? 'selected' : ''}>Tên A-Z</option>
                                </select>
                            </div>
                        </form>
                    </div>
                </div>
            </aside>

            <!-- Products Section -->
            <div style="flex: 1; min-width: 0;">
                <!-- Results Header -->
                <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 1.5rem; background: white; padding: 1.25rem; border-radius: 1rem; box-shadow: 0 1px 3px rgba(0,0,0,0.1); border: 1px solid #f3f4f6;">
                    <div style="display: flex; align-items: center; gap: 0.75rem;">
                        <div style="width: 2.5rem; height: 2.5rem; border-radius: 0.75rem; background: linear-gradient(to bottom right, #fed7aa, #fde68a); display: flex; align-items: center; justify-content: center;">
                            <span style="color: #FF6B6B; font-weight: bold; font-size: 1.125rem;">${productsCount}</span>
                        </div>
                        <div>
                            <h3 style="font-size: 1.125rem; font-weight: bold; color: #0f172a; margin: 0;">Sản phẩm tìm thấy</h3>
                        </div>
                    </div>
                </div>

                <c:if test="${not empty error}">
                    <div style="background: #fee2e2; border: 1px solid #fecaca; color: #991b1b; padding: 1rem; border-radius: 0.5rem; margin-bottom: 1.5rem;">
                        ${error}
                    </div>
                </c:if>

                <!-- Products Grid -->
                <c:choose>
                    <c:when test="${empty products}">
                        <div style="text-align: center; padding: 8rem 0; background: white; border-radius: 1rem; box-shadow: 0 1px 3px rgba(0,0,0,0.1); border: 2px dashed #e5e7eb;">
                            <div style="width: 8rem; height: 8rem; background: linear-gradient(to bottom right, #fff7ed, #fef3c7); border-radius: 9999px; display: flex; align-items: center; justify-content: center; margin: 0 auto 1.5rem;">
                                <span style="font-size: 4rem;">🔍</span>
                            </div>
                            <h3 style="font-size: 1.5rem; font-weight: bold; color: #0f172a; margin-bottom: 0.75rem;">
                                Không tìm thấy sản phẩm
                            </h3>
                            <p style="color: #334155; max-width: 28rem; margin: 0 auto 1.5rem;">
                                Rất tiếc, chúng tôi không tìm thấy sản phẩm nào phù hợp.
                            </p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 1.5rem;">
                            <c:forEach var="product" items="${products}">
                                <c:set var="discountPercent" value="0" />
                                <c:if test="${product.originalPrice > product.salePrice}">
                                    <c:set var="discountPercent" value="${((product.originalPrice - product.salePrice) / product.originalPrice * 100)}" />
                                </c:if>
                                
                                <div style="background: white; border-radius: 1rem; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1); transition: transform 0.2s, box-shadow 0.2s;">
                                    <a href="${pageContext.request.contextPath}/products/${product.productId}" style="text-decoration: none; color: inherit; display: block;">
                                        <div style="position: relative; width: 100%; height: 220px; overflow: hidden; background: #f5f5f5;">
                                            <c:choose>
                                                <c:when test="${not empty product.imageUrl}">
                                                    <img src="${product.imageUrl}" alt="${product.name}" style="width: 100%; height: 100%; object-fit: cover;">
                                                </c:when>
                                                <c:otherwise>
                                                    <div style="width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; font-size: 4rem; color: #ccc;">📦</div>
                                                </c:otherwise>
                                            </c:choose>
                                            
                                            <c:if test="${discountPercent > 0}">
                                                <span style="position: absolute; top: 0.75rem; right: 0.75rem; background: #FF6B6B; color: white; padding: 0.375rem 0.625rem; border-radius: 0.5rem; font-weight: bold; font-size: 0.875rem; box-shadow: 0 2px 4px rgba(0,0,0,0.2);">
                                                    -<fmt:formatNumber value="${discountPercent}" maxFractionDigits="0" />%
                                                </span>
                                            </c:if>
                                        </div>
                                        
                                        <div style="padding: 1rem;">
                                            <h3 style="font-size: 1rem; font-weight: 600; margin: 0 0 0.75rem 0; color: #111827; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; min-height: 3rem;">
                                                ${product.name}
                                            </h3>
                                            
                                            <div style="display: flex; align-items: center; gap: 0.5rem; margin-bottom: 0.75rem;">
                                                <span style="font-size: 1.5rem; font-weight: bold; color: #FF6B6B;">
                                                    <fmt:formatNumber value="${product.salePrice}" pattern="#,##0" />đ
                                                </span>
                                                <c:if test="${product.originalPrice > product.salePrice}">
                                                    <span style="font-size: 0.875rem; color: #9ca3af; text-decoration: line-through;">
                                                        <fmt:formatNumber value="${product.originalPrice}" pattern="#,##0" />đ
                                                    </span>
                                                </c:if>
                                            </div>
                                            
                                            <div style="font-size: 0.875rem; color: #6b7280; margin-bottom: 1rem;">
                                                <c:choose>
                                                    <c:when test="${product.quantity > 0}">
                                                        ✅ Còn ${product.quantity} sản phẩm
                                                    </c:when>
                                                    <c:otherwise>
                                                        ❌ Hết hàng
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                    </a>
                                    
                                    <div style="padding: 0 1rem 1rem 1rem;">
                                        <c:choose>
                                            <c:when test="${product.quantity > 0}">
                                                <button onclick="addToCart(${product.productId})" 
                                                        style="width: 100%; padding: 0.75rem; background: linear-gradient(to right, #FF6B6B, #FF8E53); color: white; font-weight: 600; border: none; border-radius: 0.75rem; cursor: pointer; transition: opacity 0.2s; box-shadow: 0 2px 4px rgba(255,107,107,0.3);">
                                                    🛒 Thêm vào giỏ
                                                </button>
                                            </c:when>
                                            <c:otherwise>
                                                <button disabled style="width: 100%; padding: 0.75rem; background: #e5e7eb; color: #9ca3af; font-weight: 600; border: none; border-radius: 0.75rem; cursor: not-allowed;">
                                                    Hết hàng
                                                </button>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </main>
    
    <!-- Footer -->
    <jsp:include page="../common/footer.jsp" />

    <script src="${pageContext.request.contextPath}/js/main.js"></script>
    <script>
        function addToCart(productId) {
            // Create form data
            const formData = new FormData();
            formData.append('productId', productId);
            formData.append('quantity', 1);
            
            fetch('${pageContext.request.contextPath}/add-to-cart', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('✅ ' + data.message);
                } else {
                    alert('❌ ' + data.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('❌ Có lỗi xảy ra');
            });
        }
    </script>
</body>
</html>
