<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Kho hàng - Seller</title>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/seller_style.css">
            </head>

            <body class="bg-white">

                <jsp:include page="../common/sidebar.jsp">
                    <jsp:param name="currentPath" value="/seller/products" />
                </jsp:include>

                <main
                    style="margin-top: 96px; min-height: 80vh; padding: 2rem; max-width: 1400px; margin-left: auto; margin-right: auto;">

                    <!-- Thông báo thành công/lỗi -->
                    <c:if test="${not empty param.message}">
                        <div
                            style="background: #d4edda; color: #155724; padding: 1rem; border-radius: 0.375rem; margin-bottom: 1rem; border: 1px solid #c3e6cb;">
                            <c:choose>
                                <c:when test="${param.message == 'created'}">
                                    ✅ Sản phẩm đã được đăng thành công! Đang chờ admin duyệt.
                                </c:when>
                                <c:otherwise>
                                    ${param.message}
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:if>

                    <c:if test="${not empty error}">
                        <div
                            style="background: #f8d7da; color: #721c24; padding: 1rem; border-radius: 0.375rem; margin-bottom: 1rem; border: 1px solid #f5c6cb;">
                            ❌ ${error}
                        </div>
                    </c:if>

                    <div
                        style="background: white; padding: 2rem; border-radius: 0.5rem; box-shadow: 0 4px 6px rgba(0,0,0,0.05); margin-bottom: 2rem; border: 1px solid #e2e8f0;">
                        <h3 style="margin-top: 0; color: #2d3748;">📝 Đăng sản phẩm mới</h3>
                        <form action="${pageContext.request.contextPath}/seller/products" method="post"
                            style="display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem;">
                            <input type="hidden" name="action" value="create">

                            <div style="grid-column: span 2;">
                                <label style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Tên sản
                                    phẩm</label>
                                <input type="text" name="name" class="form-control" required
                                    style="width: 100%; padding: 0.5rem; border: 1px solid #cbd5e0; border-radius: 0.25rem;">
                            </div>

                            <div style="grid-column: span 2;">
                                <label style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Mô tả</label>
                                <textarea name="description" class="form-control" rows="3"
                                    style="width: 100%; padding: 0.5rem; border: 1px solid #cbd5e0; border-radius: 0.25rem;"></textarea>
                            </div>

                            <div>
                                <label style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Danh mục sản
                                    phẩm</label>
                                <select name="category" class="form-control"
                                    style="width: 100%; padding: 0.5rem; border: 1px solid #cbd5e0; border-radius: 0.25rem;">
                                    <option value="VEGETABLES">🥬 Rau củ quả</option>
                                    <option value="FRUITS">🍎 Trái cây</option>
                                    <option value="MEAT">🥩 Thịt tươi</option>
                                    <option value="SEAFOOD">🦐 Hải sản</option>
                                    <option value="DAIRY">🥛 Sữa & Phô mai</option>
                                    <option value="BAKERY">🥖 Bánh mì & Bánh ngọt</option>
                                    <option value="SNACKS">🍿 Snack & Đồ ăn vặt</option>
                                    <option value="BEVERAGES">🥤 Đồ uống</option>
                                    <option value="FROZEN">🧊 Thực phẩm đông lạnh</option>
                                    <option value="CANNED">🥫 Đồ hộp</option>
                                    <option value="CONDIMENTS">🧂 Gia vị & Nước sốt</option>
                                    <option value="OTHER" selected>📦 Khác</option>
                                </select>
                            </div>

                            <div>
                                <label style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Giá bán
                                    (VNĐ)</label>
                                <input type="number" name="price" class="form-control" min="1000" required
                                    style="width: 100%; padding: 0.5rem; border: 1px solid #cbd5e0; border-radius: 0.25rem;">
                            </div>

                            <div>
                                <label style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Số lượng</label>
                                <input type="number" name="quantity" class="form-control" min="1" required
                                    style="width: 100%; padding: 0.5rem; border: 1px solid #cbd5e0; border-radius: 0.25rem;">
                            </div>

                            <div>
                                <label style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Ngày hết
                                    hạn</label>
                                <input type="date" name="expirationDate" id="expDate" class="form-control" required
                                    style="width: 100%; padding: 0.5rem; border: 1px solid #cbd5e0; border-radius: 0.25rem;">
                            </div>

                            <div>
                                <label style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Link ảnh sản
                                    phẩm</label>
                                <input type="url" name="imageUrl" class="form-control" placeholder="https://..."
                                    style="width: 100%; padding: 0.5rem; border: 1px solid #cbd5e0; border-radius: 0.25rem;">
                            </div>

                            <div style="grid-column: span 2; margin-top: 1rem;">
                                <button type="submit"
                                    style="background: #ea580c; color: white; border: none; padding: 0.75rem 2rem; border-radius: 0.375rem; font-weight: 600; cursor: pointer;">Đăng
                                    bán ngay</button>
                            </div>
                        </form>
                    </div>

                    <div class="filter-bar" style="margin-bottom: 1.5rem; border-bottom: 2px solid #e2e8f0;">
                        <a href="?tab=active"
                            class="filter-btn ${param.tab == 'active' || empty param.tab ? 'active' : ''}"
                            style="padding: 0.75rem 1.5rem; display: inline-block; text-decoration: none; color: ${param.tab == 'active' || empty param.tab ? '#ea580c' : '#718096'}; border-bottom: 2px solid ${param.tab == 'active' || empty param.tab ? '#ea580c' : 'transparent'}; font-weight: 600;">
                            Đang bán (Active)
                        </a>
                        <a href="?tab=pending" class="filter-btn ${param.tab == 'pending' ? 'active' : ''}"
                            style="padding: 0.75rem 1.5rem; display: inline-block; text-decoration: none; color: ${param.tab == 'pending' ? '#ea580c' : '#718096'}; border-bottom: 2px solid ${param.tab == 'pending' ? '#ea580c' : 'transparent'}; font-weight: 600;">
                            Chờ duyệt / Bị ẩn
                        </a>
                        <a href="?tab=sold" class="filter-btn ${param.tab == 'sold' ? 'active' : ''}"
                            style="padding: 0.75rem 1.5rem; display: inline-block; text-decoration: none; color: ${param.tab == 'sold' ? '#ea580c' : '#718096'}; border-bottom: 2px solid ${param.tab == 'sold' ? '#ea580c' : 'transparent'}; font-weight: 600;">
                            Đã bán hết / Hết hạn
                        </a>
                    </div>

                    <table
                        style="width: 100%; border-collapse: collapse; background: white; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
                        <thead style="background: #f7fafc;">
                            <tr>
                                <th style="padding: 1rem; text-align: left;">Ảnh</th>
                                <th style="padding: 1rem; text-align: left;">Tên SP</th>
                                <th style="padding: 1rem; text-align: left;">Danh mục</th>
                                <th style="padding: 1rem; text-align: left;">Giá</th>
                                <th style="padding: 1rem; text-align: left;">Hết hạn</th>
                                <th style="padding: 1rem; text-align: left;">Trạng thái</th>
                                <th style="padding: 1rem; text-align: left;">Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="p" items="${products}">
                                <tr style="border-top: 1px solid #e2e8f0;">
                                    <td style="padding: 1rem;"><img src="${p.imageUrl}"
                                            style="width: 48px; height: 48px; object-fit: cover; border-radius: 4px;">
                                    </td>
                                    <td style="padding: 1rem; font-weight: 500;">${p.name}</td>
                                    <td style="padding: 1rem;">
                                        <span
                                            style="background: #f7fafc; padding: 0.25rem 0.5rem; border-radius: 0.25rem; font-size: 0.875rem;">
                                            ${p.category.emoji} ${p.category.displayName}
                                        </span>
                                    </td>
                                    <td style="padding: 1rem;">
                                        <fmt:formatNumber value="${p.salePrice}" type="currency" currencySymbol="₫"
                                            maxFractionDigits="0" />
                                    </td>
                                    <td style="padding: 1rem;">
                                        <fmt:formatDate value="${p.expirationDate}" pattern="dd/MM/yyyy" />
                                    </td>
                                    <td style="padding: 1rem;">
                                        <span
                                            class="status-badge status-${p.status.toString().toLowerCase()}">${p.status}</span>
                                    </td>
                                    <td style="padding: 1rem;">
                                        <c:if test="${p.status == 'ACTIVE'}">
                                            <form action="${pageContext.request.contextPath}/seller/products"
                                                method="post" style="display:inline;">
                                                <input type="hidden" name="action" value="hide">
                                                <input type="hidden" name="productId" value="${p.productId}">
                                                <button type="submit"
                                                    style="background: #718096; color: white; border: none; padding: 4px 8px; border-radius: 4px; cursor: pointer;">👁️
                                                    Ẩn đi</button>
                                            </form>
                                        </c:if>

                                        <c:if test="${p.status == 'HIDDEN'}">
                                            <form action="${pageContext.request.contextPath}/seller/products"
                                                method="post" style="display:inline;"
                                                onsubmit="return confirm('Hiện lại sản phẩm sẽ cần Admin duyệt lại. Bạn chắc chắn chứ?');">
                                                <input type="hidden" name="action" value="show">
                                                <input type="hidden" name="productId" value="${p.productId}">
                                                <button type="submit"
                                                    style="background: #d69e2e; color: white; border: none; padding: 4px 8px; border-radius: 4px; cursor: pointer;">⚠️
                                                    Hiện lại</button>
                                            </form>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </main>

                <jsp:include page="../common/footer.jsp" />
                <script src="${pageContext.request.contextPath}/js/main.js"></script>
                <script>
                    document.getElementById('expDate').min = new Date().toISOString().split("T")[0];
                </script>
            </body>

            </html>