<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Kho thực phẩm - Seller</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>
    <jsp:include page="../common/sidebar.jsp" />
    
    <div class="main-content">
        <!-- Header Banner -->
        <div class="header-banner">
            <div class="container">
                <div class="flex-between">
                    <div>
                        <h1 class="page-title">
                            <span class="icon">📦</span>
                            Kho thực phẩm
                        </h1>
                        <p class="page-subtitle">Quản lý sản phẩm của cửa hàng</p>
                    </div>
                    <button onclick="openAddProductModal()" class="btn btn-primary">
                        <span class="icon">+</span>
                        Thêm sản phẩm
                    </button>
                </div>
            </div>
        </div>

        <div class="container py-4">
            <c:choose>
                <c:when test="${empty products}">
                    <div class="empty-state">
                        <span class="empty-icon">📦</span>
                        <h3>Chưa có sản phẩm nào</h3>
                        <p>Hãy thêm sản phẩm đầu tiên để bắt đầu bán hàng</p>
                        <button onclick="openAddProductModal()" class="btn btn-primary mt-3">
                            + Thêm sản phẩm ngay
                        </button>
                    </div>
                </c:when>
                <c:otherwise>
                    <!-- Products Table -->
                    <div class="card">
                        <div class="table-responsive">
                            <table class="table">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Tên sản phẩm</th>
                                        <th>Giá gốc</th>
                                        <th>Giá bán</th>
                                        <th>Số lượng</th>
                                        <th>HSD</th>
                                        <th>Trạng thái</th>
                                        <th>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="product" items="${products}">
                                        <tr>
                                            <td>#${product.productId}</td>
                                            <td>
                                                <div class="product-name">${product.name}</div>
                                                <c:if test="${not empty product.description}">
                                                    <div class="text-muted text-sm">${product.description}</div>
                                                </c:if>
                                            </td>
                                            <td>
                                                <fmt:formatNumber value="${product.originalPrice}" type="currency" currencySymbol="₫" />
                                            </td>
                                            <td>
                                                <span class="text-primary fw-bold">
                                                    <fmt:formatNumber value="${product.salePrice}" type="currency" currencySymbol="₫" />
                                                </span>
                                            </td>
                                            <td>
                                                <span class="badge ${product.quantity > 0 ? 'badge-success' : 'badge-danger'}">
                                                    ${product.quantity}
                                                </span>
                                            </td>
                                            <td>
                                                <fmt:formatDate value="${product.expirationDate}" pattern="dd/MM/yyyy" />
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${product.status == 'ACTIVE'}">
                                                        <span class="badge badge-success">✅ Đang bán</span>
                                                    </c:when>
                                                    <c:when test="${product.status == 'PENDING_APPROVAL'}">
                                                        <span class="badge badge-warning">⏳ Chờ duyệt</span>
                                                    </c:when>
                                                    <c:when test="${product.status == 'REJECTED'}">
                                                        <span class="badge badge-danger">❌ Từ chối</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge badge-secondary">${product.status}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <div class="btn-group">
                                                    <button onclick="editProduct(${product.productId})" 
                                                            class="btn btn-sm btn-outline" title="Sửa">
                                                        ✏️
                                                    </button>
                                                    <c:if test="${product.status == 'ACTIVE'}">
                                                        <button onclick="toggleProductStatus(${product.productId}, 'INACTIVE')" 
                                                                class="btn btn-sm btn-outline" title="Ẩn">
                                                            👁️
                                                        </button>
                                                    </c:if>
                                                    <button onclick="deleteProduct(${product.productId})" 
                                                            class="btn btn-sm btn-danger" title="Xóa">
                                                        🗑️
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- Product Form Modal -->
    <div id="productModal" class="modal">
        <div class="modal-content modal-lg">
            <div class="modal-header">
                <h3 id="modalTitle">Thêm sản phẩm mới</h3>
                <button onclick="closeProductModal()" class="btn-close">&times;</button>
            </div>
            <form id="productForm" onsubmit="handleSubmitProduct(event)">
                <div class="modal-body">
                    <input type="hidden" id="productId" name="productId">
                    
                    <div class="form-group">
                        <label for="name">Tên sản phẩm *</label>
                        <input type="text" id="name" name="name" class="form-control" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="description">Mô tả</label>
                        <textarea id="description" name="description" class="form-control" rows="3"></textarea>
                    </div>
                    
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="originalPrice">Giá gốc (₫) *</label>
                                <input type="number" id="originalPrice" name="originalPrice" 
                                       class="form-control" min="0" step="1000" required>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="salePrice">Giá bán (₫) *</label>
                                <input type="number" id="salePrice" name="salePrice" 
                                       class="form-control" min="0" step="1000" required>
                            </div>
                        </div>
                    </div>
                    
                    <div class="row">
                        <div class="col-md-4">
                            <div class="form-group">
                                <label for="quantity">Số lượng *</label>
                                <input type="number" id="quantity" name="quantity" 
                                       class="form-control" min="0" required>
                            </div>
                        </div>
                        <div class="col-md-4">
                            <div class="form-group">
                                <label for="manufactureDate">Ngày sản xuất *</label>
                                <input type="date" id="manufactureDate" name="manufactureDate" 
                                       class="form-control" required>
                            </div>
                        </div>
                        <div class="col-md-4">
                            <div class="form-group">
                                <label for="expirationDate">Hạn sử dụng *</label>
                                <input type="date" id="expirationDate" name="expirationDate" 
                                       class="form-control" required>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="modal-footer">
                    <button type="button" onclick="closeProductModal()" class="btn btn-secondary">
                        Hủy
                    </button>
                    <button type="submit" class="btn btn-primary">
                        <span id="submitBtnText">Thêm sản phẩm</span>
                    </button>
                </div>
            </form>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/main.js"></script>
    <script>
        const API_BASE = '${pageContext.request.contextPath}/api';
        
        function openAddProductModal() {
            document.getElementById('modalTitle').textContent = 'Thêm sản phẩm mới';
            document.getElementById('submitBtnText').textContent = 'Thêm sản phẩm';
            document.getElementById('productForm').reset();
            document.getElementById('productId').value = '';
            openModal('productModal');
        }
        
        function closeProductModal() {
            closeModal('productModal');
        }
        
        async function editProduct(productId) {
            try {
                showLoading();
                const response = await apiRequest(API_BASE + '/products/' + productId);
                
                document.getElementById('modalTitle').textContent = 'Sửa sản phẩm';
                document.getElementById('submitBtnText').textContent = 'Cập nhật';
                
                document.getElementById('productId').value = response.productId;
                document.getElementById('name').value = response.name;
                document.getElementById('description').value = response.description || '';
                document.getElementById('originalPrice').value = response.originalPrice;
                document.getElementById('salePrice').value = response.salePrice;
                document.getElementById('quantity').value = response.quantity;
                document.getElementById('manufactureDate').value = formatDateForInput(response.manufactureDate);
                document.getElementById('expirationDate').value = formatDateForInput(response.expirationDate);
                
                openModal('productModal');
            } catch (error) {
                showToast('Lỗi tải thông tin sản phẩm', 'error');
            } finally {
                hideLoading();
            }
        }
        
        async function handleSubmitProduct(event) {
            event.preventDefault();
            
            const formData = new FormData(event.target);
            const productId = formData.get('productId');
            
            const data = {
                productId: productId || null,
                name: formData.get('name'),
                description: formData.get('description'),
                originalPrice: parseFloat(formData.get('originalPrice')),
                salePrice: parseFloat(formData.get('salePrice')),
                quantity: parseInt(formData.get('quantity')),
                manufactureDate: formData.get('manufactureDate'),
                expirationDate: formData.get('expirationDate')
            };
            
            try {
                showLoading();
                
                if (productId) {
                    await apiRequest(API_BASE + '/seller/products/' + productId, {
                        method: 'PUT',
                        body: JSON.stringify(data)
                    });
                    showToast('Cập nhật sản phẩm thành công!', 'success');
                } else {
                    await apiRequest(API_BASE + '/seller/products', {
                        method: 'POST',
                        body: JSON.stringify(data)
                    });
                    showToast('Thêm sản phẩm thành công!', 'success');
                }
                
                closeProductModal();
                setTimeout(() => window.location.reload(), 1000);
                
            } catch (error) {
                showToast(error.message || 'Có lỗi xảy ra', 'error');
            } finally {
                hideLoading();
            }
        }
        
        async function deleteProduct(productId) {
            if (!confirm('Bạn có chắc muốn xóa sản phẩm này?')) return;
            
            try {
                showLoading();
                await apiRequest(API_BASE + '/seller/products/' + productId, {
                    method: 'DELETE'
                });
                showToast('Xóa sản phẩm thành công!', 'success');
                setTimeout(() => window.location.reload(), 1000);
            } catch (error) {
                showToast(error.message || 'Không thể xóa sản phẩm', 'error');
            } finally {
                hideLoading();
            }
        }
        
        async function toggleProductStatus(productId, newStatus) {
            try {
                showLoading();
                await apiRequest(API_BASE + '/seller/products/' + productId + '/status', {
                    method: 'PATCH',
                    body: JSON.stringify({ status: newStatus })
                });
                showToast('Cập nhật trạng thái thành công!', 'success');
                setTimeout(() => window.location.reload(), 1000);
            } catch (error) {
                showToast('Lỗi cập nhật trạng thái', 'error');
            } finally {
                hideLoading();
            }
        }
        
        function formatDateForInput(dateString) {
            if (!dateString) return '';
            const date = new Date(dateString);
            return date.toISOString().split('T')[0];
        }
    </script>
</body>
</html>
