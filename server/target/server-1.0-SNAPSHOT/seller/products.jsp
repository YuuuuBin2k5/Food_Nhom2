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
                <link rel="stylesheet" href="${pageContext.request.contextPath}/seller/seller_css.css">
            </head>

            <body class="bg-white">

                <jsp:include page="../common/sidebar.jsp">
                    <jsp:param name="currentPath" value="/seller/products" />
                </jsp:include>

                <main class="seller-main">

                    <!-- Success/Error Messages -->
                    <c:if test="${not empty param.message}">
                        <div class="alert-success">
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
                        <div class="alert-error">
                            ❌ ${error}
                        </div>
                    </c:if>

                    <!-- Expiring Products Warning -->
                    <c:if test="${not empty expiringSoonProducts}">
                        <div class="expiring-warning">
                            <div class="expiring-warning-header">
                                <span class="expiring-warning-icon">⚠️</span>
                                <strong>Cảnh báo: Có ${expiringSoonProducts.size()} sản phẩm sắp hết hạn trong 3 ngày
                                    tới!</strong>
                            </div>
                            <div class="expiring-warning-content">
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

                    <!-- Add New Product Form -->
                    <div class="product-form">
                        <h3>📝 Đăng sản phẩm mới</h3>
                        <form action="${pageContext.request.contextPath}/seller/products" method="post"
                            class="form-grid">
                            <input type="hidden" name="action" value="create">

                            <div class="form-grid-full">
                                <label class="form-label">Tên sản phẩm</label>
                                <input type="text" name="name" class="form-control" required>
                            </div>

                            <div class="form-grid-full">
                                <label class="form-label">Mô tả</label>
                                <textarea name="description" class="form-control" rows="3"></textarea>
                            </div>

                            <div>
                                <label class="form-label">Danh mục sản phẩm</label>
                                <select name="category" class="form-control">
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
                                <label class="form-label">Giá gốc (VNĐ)</label>
                                <input type="number" name="originalPrice" class="form-control" min="1000" required
                                    placeholder="Giá gốc trước khi giảm">
                            </div>

                            <div>
                                <label class="form-label">Giá bán (VNĐ)</label>
                                <input type="number" name="price" class="form-control" min="1000" required
                                    placeholder="Giá bán thực tế">
                            </div>

                            <div>
                                <label class="form-label">Số lượng</label>
                                <input type="number" name="quantity" class="form-control" min="1" required>
                            </div>

                            <div>
                                <label class="form-label">Ngày hết hạn</label>
                                <input type="date" name="expirationDate" id="expDate" class="form-control" required>
                            </div>

                            <div>
                                <label class="form-label">Link ảnh sản phẩm</label>
                                <input type="url" name="imageUrl" class="form-control" placeholder="https://...">
                            </div>

                            <div class="form-grid-full">
                                <button type="submit" class="form-submit">Đăng bán ngay</button>
                            </div>
                        </form>
                    </div>

                    <!-- Filter Bar -->
                    <div class="filter-bar">
                        <a href="?status=PENDING_APPROVAL"
                            class="${param.status == 'PENDING_APPROVAL' || empty param.status ? 'text-orange-600' : 'text-gray-500'}"
                            style="border-bottom: 2px solid ${param.status == 'PENDING_APPROVAL' || empty param.status ? '#ea580c' : 'transparent'};">
                            🕐 Chờ duyệt
                        </a>
                        <a href="?status=REJECTED"
                            class="${param.status == 'REJECTED' ? 'text-orange-600' : 'text-gray-500'}"
                            style="border-bottom: 2px solid ${param.status == 'REJECTED' ? '#ea580c' : 'transparent'};">
                            ❌ Bị từ chối
                        </a>
                        <a href="?status=ACTIVE"
                            class="${param.status == 'ACTIVE' ? 'text-orange-600' : 'text-gray-500'}"
                            style="border-bottom: 2px solid ${param.status == 'ACTIVE' ? '#ea580c' : 'transparent'};">
                            ✅ Đang bán
                        </a>
                        <a href="?status=SOLD_OUT"
                            class="${param.status == 'SOLD_OUT' ? 'text-orange-600' : 'text-gray-500'}"
                            style="border-bottom: 2px solid ${param.status == 'SOLD_OUT' ? '#ea580c' : 'transparent'};">
                            📦 Hết hàng
                        </a>
                        <a href="?status=EXPIRED"
                            class="${param.status == 'EXPIRED' ? 'text-orange-600' : 'text-gray-500'}"
                            style="border-bottom: 2px solid ${param.status == 'EXPIRED' ? '#ea580c' : 'transparent'};">
                            ⏰ Hết hạn
                        </a>
                        <a href="?status=HIDDEN"
                            class="${param.status == 'HIDDEN' ? 'text-orange-600' : 'text-gray-500'}"
                            style="border-bottom: 2px solid ${param.status == 'HIDDEN' ? '#ea580c' : 'transparent'};">
                            👁️ Đã ẩn
                        </a>
                        <a href="?status=ALL" class="${param.status == 'ALL' ? 'text-orange-600' : 'text-gray-500'}"
                            style="border-bottom: 2px solid ${param.status == 'ALL' ? '#ea580c' : 'transparent'};">
                            📋 Tất cả
                        </a>
                    </div>

                    <!-- Products Table -->
                    <table class="product-table">
                        <thead>
                            <tr>
                                <th>Ảnh</th>
                                <th>Tên SP</th>
                                <th>Danh mục</th>
                                <th>Giá gốc</th>
                                <th>Giá bán</th>
                                <th>Hết hạn</th>
                                <th>Trạng thái</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="p" items="${products}">
                                <tr>
                                    <td><img src="${p.imageUrl}" class="product-image"></td>
                                    <td class="product-name">${p.name}</td>
                                    <td>
                                        <span class="product-category">
                                            ${p.category.emoji} ${p.category.displayName}
                                        </span>
                                    </td>
                                    <td>
                                        <span class="product-price-original">
                                            <fmt:formatNumber value="${p.originalPrice}" type="currency"
                                                currencySymbol="₫" maxFractionDigits="0" />
                                        </span>
                                    </td>
                                    <td class="product-price-sale">
                                        <fmt:formatNumber value="${p.salePrice}" type="currency" currencySymbol="₫"
                                            maxFractionDigits="0" />
                                        <c:if test="${p.originalPrice > p.salePrice}">
                                            <span class="product-discount">
                                                -
                                                <fmt:formatNumber
                                                    value="${(p.originalPrice - p.salePrice) / p.originalPrice * 100}"
                                                    maxFractionDigits="0" />%
                                            </span>
                                        </c:if>
                                    </td>
                                    <td>
                                        <fmt:formatDate value="${p.expirationDate}" pattern="dd/MM/yyyy" />
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${p.status == 'PENDING_APPROVAL'}">
                                                <span class="status-pending-approval">🕐 Chờ duyệt</span>
                                            </c:when>
                                            <c:when test="${p.status == 'REJECTED'}">
                                                <span class="status-rejected">❌ Bị từ chối</span>
                                            </c:when>
                                            <c:when test="${p.status == 'ACTIVE'}">
                                                <span class="status-active">✅ Đang bán</span>
                                            </c:when>
                                            <c:when test="${p.status == 'SOLD_OUT'}">
                                                <span class="status-sold-out">📦 Hết hàng</span>
                                            </c:when>
                                            <c:when test="${p.status == 'EXPIRED'}">
                                                <span class="status-expired">⏰ Hết hạn</span>
                                            </c:when>
                                            <c:when test="${p.status == 'HIDDEN'}">
                                                <span class="status-hidden">👁️ Đã ẩn</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="status-badge">${p.status}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div class="action-buttons">
                                            <c:choose>
                                                <c:when test="${p.status == 'ACTIVE'}">
                                                    <form action="${pageContext.request.contextPath}/seller/products"
                                                        method="post" style="display:inline;">
                                                        <input type="hidden" name="action" value="hide">
                                                        <input type="hidden" name="productId" value="${p.productId}">
                                                        <button type="submit" class="btn-hide">👁️ Ẩn đi</button>
                                                    </form>
                                                </c:when>
                                                <c:when test="${p.status == 'HIDDEN'}">
                                                    <button
                                                        onclick="openEditModal(${p.productId}, '${p.name}', '${p.description}', ${p.originalPrice}, ${p.salePrice}, ${p.quantity}, '${p.imageUrl}', '${p.expirationDate}', '${p.category}')"
                                                        class="btn-edit">✏️ Chỉnh sửa</button>
                                                    <form action="${pageContext.request.contextPath}/seller/products"
                                                        method="post" style="display:inline;"
                                                        onsubmit="return confirm('Hiện lại sản phẩm sẽ cần Admin duyệt lại. Bạn chắc chắn chứ?');">
                                                        <input type="hidden" name="action" value="show">
                                                        <input type="hidden" name="productId" value="${p.productId}">
                                                        <button type="submit" class="btn-show">⚠️ Hiện lại</button>
                                                    </form>
                                                </c:when>
                                                <c:when test="${p.status == 'REJECTED'}">
                                                    <button
                                                        onclick="openEditModal(${p.productId}, '${p.name}', '${p.description}', ${p.originalPrice}, ${p.salePrice}, ${p.quantity}, '${p.imageUrl}', '${p.expirationDate}', '${p.category}')"
                                                        class="btn-fix">✏️ Sửa & Gửi lại</button>
                                                </c:when>
                                                <c:when test="${p.status == 'PENDING_APPROVAL'}">
                                                    <span class="text-gray-500 text-sm">⏳ Đang chờ admin duyệt</span>
                                                </c:when>
                                                <c:when test="${p.status == 'SOLD_OUT'}">
                                                    <form action="${pageContext.request.contextPath}/seller/products"
                                                        method="post" style="display:inline;">
                                                        <input type="hidden" name="action" value="restock">
                                                        <input type="hidden" name="productId" value="${p.productId}">
                                                        <button type="submit" class="btn-restock">📦 Nhập thêm
                                                            hàng</button>
                                                    </form>
                                                </c:when>
                                                <c:when test="${p.status == 'EXPIRED'}">
                                                    <a href="${pageContext.request.contextPath}/seller/products/edit?id=${p.productId}"
                                                        class="btn-extend">🔄 Gia hạn</a>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="text-gray-500 text-sm">Không có hành động</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>

                    <!-- Edit Modal -->
                    <div id="editModal" class="modal">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h3>✏️ Chỉnh sửa sản phẩm</h3>
                                <button onclick="closeEditModal()" class="modal-close">&times;</button>
                            </div>

                            <form id="editForm" action="${pageContext.request.contextPath}/seller/products"
                                method="post" class="modal-form">
                                <input type="hidden" name="action" value="update">
                                <input type="hidden" id="editProductId" name="productId">

                                <div class="modal-form-full">
                                    <label class="form-label">Tên sản phẩm</label>
                                    <input type="text" id="editName" name="name" required class="form-control">
                                </div>

                                <div class="modal-form-full">
                                    <label class="form-label">Mô tả</label>
                                    <textarea id="editDescription" name="description" rows="3"
                                        class="form-control"></textarea>
                                </div>

                                <div>
                                    <label class="form-label">Danh mục sản phẩm</label>
                                    <select id="editCategory" name="category" class="form-control">
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
                                    <label class="form-label">Giá gốc (VNĐ)</label>
                                    <input type="number" id="editOriginalPrice" name="originalPrice" min="1000" required
                                        class="form-control">
                                </div>

                                <div>
                                    <label class="form-label">Giá bán (VNĐ)</label>
                                    <input type="number" id="editPrice" name="price" min="1000" required
                                        class="form-control">
                                </div>

                                <div>
                                    <label class="form-label">Số lượng</label>
                                    <input type="number" id="editQuantity" name="quantity" min="1" required
                                        class="form-control">
                                </div>

                                <div>
                                    <label class="form-label">Ngày hết hạn</label>
                                    <input type="date" id="editExpirationDate" name="expirationDate" required
                                        class="form-control">
                                </div>

                                <div class="modal-form-full">
                                    <label class="form-label">Link ảnh sản phẩm</label>
                                    <input type="url" id="editImageUrl" name="imageUrl" placeholder="https://..."
                                        class="form-control">
                                </div>

                                <div class="modal-actions">
                                    <button type="button" onclick="closeEditModal()" class="btn-cancel">Hủy</button>
                                    <button type="submit" class="btn-update">Cập nhật</button>
                                </div>
                            </form>
                        </div>
                    </div>

                </main>

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
                            const dateStr = expirationDate.toString();
                            if (dateStr.includes('/')) {
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
            </body>

            </html>