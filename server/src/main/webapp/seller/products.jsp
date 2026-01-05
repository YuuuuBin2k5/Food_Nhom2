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
                <style>
                    .filter-bar {
                        margin-bottom: 1.5rem;
                        border-bottom: 2px solid #e2e8f0;
                        display: flex;
                        flex-wrap: wrap;
                        gap: 0.5rem;
                        overflow-x: auto;
                        padding-bottom: 0.5rem;
                    }

                    .filter-bar a {
                        padding: 0.75rem 1.5rem;
                        display: inline-block;
                        text-decoration: none;
                        font-weight: 600;
                        white-space: nowrap;
                        border-radius: 8px 8px 0 0;
                        transition: all 0.2s ease;
                        min-width: fit-content;
                    }

                    .filter-bar a:hover {
                        background-color: #f7fafc;
                    }

                    @media (max-width: 768px) {
                        .filter-bar {
                            gap: 0.25rem;
                        }

                        .filter-bar a {
                            padding: 0.5rem 1rem;
                            font-size: 0.875rem;
                        }

                        .action-buttons {
                            flex-direction: column;
                        }

                        .action-buttons a,
                        .action-buttons button {
                            width: 100%;
                            text-align: center;
                        }

                        #editModal>div {
                            width: 95%;
                            margin: 1rem;
                            max-height: 85vh;
                        }

                        #editForm {
                            grid-template-columns: 1fr;
                        }

                        #editForm>div:not([style*="grid-column"]) {
                            grid-column: span 1;
                        }
                    }

                    .action-buttons {
                        display: flex;
                        gap: 0.5rem;
                        flex-wrap: wrap;
                    }

                    .action-buttons a,
                    .action-buttons button {
                        font-size: 0.875rem;
                        padding: 0.5rem 1rem;
                        border-radius: 4px;
                        border: none;
                        cursor: pointer;
                        font-weight: 600;
                        transition: all 0.2s ease;
                        text-decoration: none;
                        display: inline-block;
                    }

                    .action-buttons a:hover,
                    .action-buttons button:hover {
                        transform: translateY(-1px);
                        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                    }
                </style>
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
                                <c:when test="${param.message == 'updated'}">
                                    ✅ Sản phẩm đã được cập nhật thành công! Đang chờ admin duyệt lại.
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

                    <!-- Thông báo sản phẩm sắp hết hạn -->
                    <c:if test="${not empty expiringSoonProducts}">
                        <div
                            style="background: #fff3cd; color: #856404; padding: 1rem; border-radius: 0.375rem; margin-bottom: 1rem; border: 1px solid #ffeaa7;">
                            <div style="display: flex; align-items: center; margin-bottom: 0.5rem;">
                                <span style="font-size: 1.2rem; margin-right: 0.5rem;">⚠️</span>
                                <strong>Cảnh báo: Có ${expiringSoonProducts.size()} sản phẩm sắp hết hạn trong 3 ngày
                                    tới!</strong>
                            </div>
                            <div style="font-size: 0.9rem;">
                                <c:forEach var="product" items="${expiringSoonProducts}" varStatus="status">
                                    <c:if test="${status.index < 3}">
                                        • ${product.name} - Hết hạn:
                                        <fmt:formatDate value="${product.expirationDate}" pattern="dd/MM/yyyy" /><br>
                                    </c:if>
                                </c:forEach>
                                <c:if test="${expiringSoonProducts.size() > 3}">
                                    <em>... và ${expiringSoonProducts.size() - 3} sản phẩm khác</em>
                                </c:if>
                            </div>
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
                                <label style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Giá gốc
                                    (VNĐ)</label>
                                <input type="number" name="originalPrice" class="form-control" min="1000" required
                                    style="width: 100%; padding: 0.5rem; border: 1px solid #cbd5e0; border-radius: 0.25rem;"
                                    placeholder="Giá gốc trước khi giảm">
                            </div>

                            <div>
                                <label style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Giá bán
                                    (VNĐ)</label>
                                <input type="number" name="price" class="form-control" min="1000" required
                                    style="width: 100%; padding: 0.5rem; border: 1px solid #cbd5e0; border-radius: 0.25rem;"
                                    placeholder="Giá bán thực tế">
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

                    <div class="filter-bar"
                        style="margin-bottom: 1.5rem; border-bottom: 2px solid #e2e8f0; display: flex; flex-wrap: wrap; gap: 0.5rem; overflow-x: auto; padding-bottom: 0.5rem;">
                        <a href="?status=PENDING_APPROVAL"
                            style="padding: 0.75rem 1.5rem; display: inline-block; text-decoration: none; color: ${param.status == 'PENDING_APPROVAL' || empty param.status ? '#ea580c' : '#718096'}; border-bottom: 2px solid ${param.status == 'PENDING_APPROVAL' || empty param.status ? '#ea580c' : 'transparent'}; font-weight: 600; white-space: nowrap;">
                            🕐 Chờ duyệt
                        </a>
                        <a href="?status=REJECTED"
                            style="padding: 0.75rem 1.5rem; display: inline-block; text-decoration: none; color: ${param.status == 'REJECTED' ? '#ea580c' : '#718096'}; border-bottom: 2px solid ${param.status == 'REJECTED' ? '#ea580c' : 'transparent'}; font-weight: 600; white-space: nowrap;">
                            ❌ Bị từ chối
                        </a>
                        <a href="?status=ACTIVE"
                            style="padding: 0.75rem 1.5rem; display: inline-block; text-decoration: none; color: ${param.status == 'ACTIVE' ? '#ea580c' : '#718096'}; border-bottom: 2px solid ${param.status == 'ACTIVE' ? '#ea580c' : 'transparent'}; font-weight: 600; white-space: nowrap;">
                            ✅ Đang bán
                        </a>
                        <a href="?status=SOLD_OUT"
                            style="padding: 0.75rem 1.5rem; display: inline-block; text-decoration: none; color: ${param.status == 'SOLD_OUT' ? '#ea580c' : '#718096'}; border-bottom: 2px solid ${param.status == 'SOLD_OUT' ? '#ea580c' : 'transparent'}; font-weight: 600; white-space: nowrap;">
                            📦 Hết hàng
                        </a>
                        <a href="?status=EXPIRED"
                            style="padding: 0.75rem 1.5rem; display: inline-block; text-decoration: none; color: ${param.status == 'EXPIRED' ? '#ea580c' : '#718096'}; border-bottom: 2px solid ${param.status == 'EXPIRED' ? '#ea580c' : 'transparent'}; font-weight: 600; white-space: nowrap;">
                            ⏰ Hết hạn
                        </a>
                        <a href="?status=HIDDEN"
                            style="padding: 0.75rem 1.5rem; display: inline-block; text-decoration: none; color: ${param.status == 'HIDDEN' ? '#ea580c' : '#718096'}; border-bottom: 2px solid ${param.status == 'HIDDEN' ? '#ea580c' : 'transparent'}; font-weight: 600; white-space: nowrap;">
                            👁️ Đã ẩn
                        </a>
                        <a href="?status=ALL"
                            style="padding: 0.75rem 1.5rem; display: inline-block; text-decoration: none; color: ${param.status == 'ALL' ? '#ea580c' : '#718096'}; border-bottom: 2px solid ${param.status == 'ALL' ? '#ea580c' : 'transparent'}; font-weight: 600; white-space: nowrap;">
                            📋 Tất cả
                        </a>
                    </div>

                    <table
                        style="width: 100%; border-collapse: collapse; background: white; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
                        <thead style="background: #f7fafc;">
                            <tr>
                                <th style="padding: 1rem; text-align: left;">Ảnh</th>
                                <th style="padding: 1rem; text-align: left;">Tên SP</th>
                                <th style="padding: 1rem; text-align: left;">Danh mục</th>
                                <th style="padding: 1rem; text-align: left;">Giá gốc</th>
                                <th style="padding: 1rem; text-align: left;">Giá bán</th>
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
                                        <span
                                            style="text-decoration: line-through; color: #718096; font-size: 0.875rem;">
                                            <fmt:formatNumber value="${p.originalPrice}" type="currency"
                                                currencySymbol="₫" maxFractionDigits="0" />
                                        </span>
                                    </td>
                                    <td style="padding: 1rem; font-weight: 600; color: #e53e3e;">
                                        <fmt:formatNumber value="${p.salePrice}" type="currency" currencySymbol="₫"
                                            maxFractionDigits="0" />
                                        <c:if test="${p.originalPrice > p.salePrice}">
                                            <span
                                                style="background: #fed7d7; color: #c53030; padding: 0.125rem 0.25rem; border-radius: 0.125rem; font-size: 0.75rem; margin-left: 0.25rem;">
                                                -
                                                <fmt:formatNumber
                                                    value="${(p.originalPrice - p.salePrice) / p.originalPrice * 100}"
                                                    maxFractionDigits="0" />%
                                            </span>
                                        </c:if>
                                    </td>
                                    <td style="padding: 1rem;">
                                        <fmt:formatDate value="${p.expirationDate}" pattern="dd/MM/yyyy" />
                                    </td>
                                    <td style="padding: 1rem;">
                                        <c:choose>
                                            <c:when test="${p.status == 'PENDING_APPROVAL'}">
                                                <span
                                                    style="background: #fef3c7; color: #92400e; padding: 0.25rem 0.75rem; border-radius: 9999px; font-size: 0.875rem; font-weight: 600;">🕐
                                                    Chờ duyệt</span>
                                            </c:when>
                                            <c:when test="${p.status == 'REJECTED'}">
                                                <span
                                                    style="background: #fee2e2; color: #991b1b; padding: 0.25rem 0.75rem; border-radius: 9999px; font-size: 0.875rem; font-weight: 600;">❌
                                                    Bị từ chối</span>
                                            </c:when>
                                            <c:when test="${p.status == 'ACTIVE'}">
                                                <span
                                                    style="background: #dcfce7; color: #166534; padding: 0.25rem 0.75rem; border-radius: 9999px; font-size: 0.875rem; font-weight: 600;">✅
                                                    Đang bán</span>
                                            </c:when>
                                            <c:when test="${p.status == 'SOLD_OUT'}">
                                                <span
                                                    style="background: #f3f4f6; color: #374151; padding: 0.25rem 0.75rem; border-radius: 9999px; font-size: 0.875rem; font-weight: 600;">📦
                                                    Hết hàng</span>
                                            </c:when>
                                            <c:when test="${p.status == 'EXPIRED'}">
                                                <span
                                                    style="background: #fef3c7; color: #92400e; padding: 0.25rem 0.75rem; border-radius: 9999px; font-size: 0.875rem; font-weight: 600;">⏰
                                                    Hết hạn</span>
                                            </c:when>
                                            <c:when test="${p.status == 'HIDDEN'}">
                                                <span
                                                    style="background: #e5e7eb; color: #4b5563; padding: 0.25rem 0.75rem; border-radius: 9999px; font-size: 0.875rem; font-weight: 600;">👁️
                                                    Đã ẩn</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span
                                                    style="background: #f3f4f6; color: #374151; padding: 0.25rem 0.75rem; border-radius: 9999px; font-size: 0.875rem; font-weight: 600;">${p.status}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td style="padding: 1rem;">
                                        <div style="display: flex; gap: 0.5rem; flex-wrap: wrap;">
                                            <c:choose>
                                                <c:when test="${p.status == 'ACTIVE'}">
                                                    <form action="${pageContext.request.contextPath}/seller/products"
                                                        method="post" style="display:inline;">
                                                        <input type="hidden" name="action" value="hide">
                                                        <input type="hidden" name="productId" value="${p.productId}">
                                                        <button type="submit"
                                                            style="background: #718096; color: white; border: none; padding: 0.5rem 1rem; border-radius: 4px; cursor: pointer; font-size: 0.875rem; font-weight: 600;">👁️
                                                            Ẩn đi</button>
                                                    </form>
                                                </c:when>
                                                <c:when test="${p.status == 'HIDDEN'}">
                                                    <button
                                                        onclick="openEditModal(${p.productId}, '${p.name}', '${p.description}', ${p.originalPrice}, ${p.salePrice}, ${p.quantity}, '${p.imageUrl}', '${p.expirationDate}', '${p.category}')"
                                                        style="background: #2563eb; color: white; border: none; padding: 0.5rem 1rem; border-radius: 4px; cursor: pointer; font-size: 0.875rem; font-weight: 600;">✏️
                                                        Chỉnh sửa</button>
                                                    <form action="${pageContext.request.contextPath}/seller/products"
                                                        method="post" style="display:inline;"
                                                        onsubmit="return confirm('Hiện lại sản phẩm sẽ cần Admin duyệt lại. Bạn chắc chắn chứ?');">
                                                        <input type="hidden" name="action" value="show">
                                                        <input type="hidden" name="productId" value="${p.productId}">
                                                        <button type="submit"
                                                            style="background: #d69e2e; color: white; border: none; padding: 0.5rem 1rem; border-radius: 4px; cursor: pointer; font-size: 0.875rem; font-weight: 600;">⚠️
                                                            Hiện lại</button>
                                                    </form>
                                                </c:when>
                                                <c:when test="${p.status == 'REJECTED'}">
                                                    <button
                                                        onclick="openEditModal(${p.productId}, '${p.name}', '${p.description}', ${p.originalPrice}, ${p.salePrice}, ${p.quantity}, '${p.imageUrl}', '${p.expirationDate}', '${p.category}')"
                                                        style="background: #dc2626; color: white; border: none; padding: 0.5rem 1rem; border-radius: 4px; cursor: pointer; font-size: 0.875rem; font-weight: 600;">✏️
                                                        Sửa & Gửi lại</button>
                                                </c:when>
                                                <c:when test="${p.status == 'PENDING_APPROVAL'}">
                                                    <span
                                                        style="color: #718096; font-size: 0.875rem; font-style: italic;">⏳
                                                        Đang chờ admin duyệt</span>
                                                </c:when>
                                                <c:when test="${p.status == 'SOLD_OUT'}">
                                                    <form action="${pageContext.request.contextPath}/seller/products"
                                                        method="post" style="display:inline;">
                                                        <input type="hidden" name="action" value="restock">
                                                        <input type="hidden" name="productId" value="${p.productId}">
                                                        <button type="submit"
                                                            style="background: #16a34a; color: white; border: none; padding: 0.5rem 1rem; border-radius: 4px; cursor: pointer; font-size: 0.875rem; font-weight: 600;">📦
                                                            Nhập thêm hàng</button>
                                                    </form>
                                                </c:when>
                                                <c:when test="${p.status == 'EXPIRED'}">
                                                    <a href="${pageContext.request.contextPath}/seller/products/edit?id=${p.productId}"
                                                        style="background: #ea580c; color: white; border: none; padding: 0.5rem 1rem; border-radius: 4px; cursor: pointer; font-size: 0.875rem; font-weight: 600; text-decoration: none; display: inline-block;">🔄
                                                        Gia hạn</a>
                                                </c:when>
                                                <c:otherwise>
                                                    <span style="color: #718096; font-size: 0.875rem;">Không có hành
                                                        động</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </main>

                <!-- Modal chỉnh sửa sản phẩm -->
                <div id="editModal"
                    style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); z-index: 1000; justify-content: center; align-items: center;">
                    <div
                        style="background: white; border-radius: 8px; padding: 2rem; max-width: 600px; width: 90%; max-height: 90vh; overflow-y: auto; position: relative;">
                        <div
                            style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
                            <h3 style="margin: 0; color: #2d3748;">✏️ Chỉnh sửa sản phẩm</h3>
                            <button onclick="closeEditModal()"
                                style="background: none; border: none; font-size: 1.5rem; cursor: pointer; color: #718096;">&times;</button>
                        </div>

                        <form id="editForm" action="${pageContext.request.contextPath}/seller/products" method="post"
                            style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" id="editProductId" name="productId">

                            <div style="grid-column: span 2;">
                                <label style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Tên sản
                                    phẩm</label>
                                <input type="text" id="editName" name="name" required
                                    style="width: 100%; padding: 0.5rem; border: 1px solid #cbd5e0; border-radius: 0.25rem;">
                            </div>

                            <div style="grid-column: span 2;">
                                <label style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Mô tả</label>
                                <textarea id="editDescription" name="description" rows="3"
                                    style="width: 100%; padding: 0.5rem; border: 1px solid #cbd5e0; border-radius: 0.25rem;"></textarea>
                            </div>

                            <div>
                                <label style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Danh mục sản
                                    phẩm</label>
                                <select id="editCategory" name="category"
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
                                    <option value="OTHER">📦 Khác</option>
                                </select>
                            </div>

                            <div>
                                <label style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Giá gốc
                                    (VNĐ)</label>
                                <input type="number" id="editOriginalPrice" name="originalPrice" min="1000" required
                                    style="width: 100%; padding: 0.5rem; border: 1px solid #cbd5e0; border-radius: 0.25rem;">
                            </div>

                            <div>
                                <label style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Giá bán
                                    (VNĐ)</label>
                                <input type="number" id="editPrice" name="price" min="1000" required
                                    style="width: 100%; padding: 0.5rem; border: 1px solid #cbd5e0; border-radius: 0.25rem;">
                            </div>

                            <div>
                                <label style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Số lượng</label>
                                <input type="number" id="editQuantity" name="quantity" min="1" required
                                    style="width: 100%; padding: 0.5rem; border: 1px solid #cbd5e0; border-radius: 0.25rem;">
                            </div>

                            <div>
                                <label style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Ngày hết
                                    hạn</label>
                                <input type="date" id="editExpirationDate" name="expirationDate" required
                                    style="width: 100%; padding: 0.5rem; border: 1px solid #cbd5e0; border-radius: 0.25rem;">
                            </div>

                            <div style="grid-column: span 2;">
                                <label style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Link ảnh sản
                                    phẩm</label>
                                <input type="url" id="editImageUrl" name="imageUrl" placeholder="https://..."
                                    style="width: 100%; padding: 0.5rem; border: 1px solid #cbd5e0; border-radius: 0.25rem;">
                            </div>

                            <div
                                style="grid-column: span 2; margin-top: 1rem; display: flex; gap: 1rem; justify-content: flex-end;">
                                <button type="button" onclick="closeEditModal()"
                                    style="background: #718096; color: white; border: none; padding: 0.75rem 1.5rem; border-radius: 0.375rem; font-weight: 600; cursor: pointer;">Hủy</button>
                                <button type="submit"
                                    style="background: #ea580c; color: white; border: none; padding: 0.75rem 1.5rem; border-radius: 0.375rem; font-weight: 600; cursor: pointer;">Cập
                                    nhật</button>
                            </div>
                        </form>
                    </div>
                </div>

                <jsp:include page="../common/footer.jsp" />
                <script src="${pageContext.request.contextPath}/js/main.js"></script>
                <script>
                    document.getElementById('expDate').min = new Date().toISOString().split("T")[0];

                    // Validation giá gốc và giá bán
                    const originalPriceInput = document.querySelector('input[name="originalPrice"]');
                    const salePriceInput = document.querySelector('input[name="price"]');

                    function validatePrices() {
                        const originalPrice = parseFloat(originalPriceInput.value) || 0;
                        const salePrice = parseFloat(salePriceInput.value) || 0;

                        if (salePrice > originalPrice && originalPrice > 0) {
                            salePriceInput.setCustomValidity('Giá bán không được lớn hơn giá gốc');
                        } else {
                            salePriceInput.setCustomValidity('');
                        }
                    }

                    originalPriceInput.addEventListener('input', validatePrices);
                    salePriceInput.addEventListener('input', validatePrices);

                    // Modal functions
                    function openEditModal(productId, name, description, originalPrice, salePrice, quantity, imageUrl, expirationDate, category) {
                        // Populate form fields
                        document.getElementById('editProductId').value = productId;
                        document.getElementById('editName').value = name || '';
                        document.getElementById('editDescription').value = description || '';
                        document.getElementById('editOriginalPrice').value = originalPrice || '';
                        document.getElementById('editPrice').value = salePrice || '';
                        document.getElementById('editQuantity').value = quantity || '';
                        document.getElementById('editImageUrl').value = imageUrl || '';
                        document.getElementById('editCategory').value = category || 'OTHER';

                        // Format date for input field
                        if (expirationDate) {
                            // Convert date format from display format to input format
                            const dateStr = expirationDate.toString();
                            if (dateStr.includes('/')) {
                                // If date is in dd/MM/yyyy format, convert to yyyy-MM-dd
                                const parts = dateStr.split('/');
                                if (parts.length === 3) {
                                    const formattedDate = parts[2] + '-' + parts[1].padStart(2, '0') + '-' + parts[0].padStart(2, '0');
                                    document.getElementById('editExpirationDate').value = formattedDate;
                                }
                            } else {
                                document.getElementById('editExpirationDate').value = expirationDate;
                            }
                        }

                        // Set minimum date to today
                        document.getElementById('editExpirationDate').min = new Date().toISOString().split("T")[0];

                        // Show modal
                        const modal = document.getElementById('editModal');
                        modal.style.display = 'flex';

                        // Add validation for edit form
                        const editOriginalPrice = document.getElementById('editOriginalPrice');
                        const editSalePrice = document.getElementById('editPrice');

                        function validateEditPrices() {
                            const originalPrice = parseFloat(editOriginalPrice.value) || 0;
                            const salePrice = parseFloat(editSalePrice.value) || 0;

                            if (salePrice > originalPrice && originalPrice > 0) {
                                editSalePrice.setCustomValidity('Giá bán không được lớn hơn giá gốc');
                            } else {
                                editSalePrice.setCustomValidity('');
                            }
                        }

                        editOriginalPrice.addEventListener('input', validateEditPrices);
                        editSalePrice.addEventListener('input', validateEditPrices);
                    }

                    function closeEditModal() {
                        document.getElementById('editModal').style.display = 'none';
                    }

                    // Close modal when clicking outside
                    document.getElementById('editModal').addEventListener('click', function (e) {
                        if (e.target === this) {
                            closeEditModal();
                        }
                    });

                    // Close modal with Escape key
                    document.addEventListener('keydown', function (e) {
                        if (e.key === 'Escape') {
                            closeEditModal();
                        }
                    });
                </script>

                if (salePrice > originalPrice && originalPrice > 0) {
                salePriceInput.setCustomValidity('Giá bán không được lớn hơn giá gốc');
                } else {
                salePriceInput.setCustomValidity('');
                }
                }

                originalPriceInput.addEventListener('input', validatePrices);
                salePriceInput.addEventListener('input', validatePrices);
                </script>
            </body>

            </html>