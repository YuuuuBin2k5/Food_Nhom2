<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Duyệt Product - Admin</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/sidebar.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin_css/admin_main.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin_css/admin_approves_product.css">
</head>
<body>

<!-- Include Sidebar -->
    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="currentPath" value="/admin/approveProduct" />
    </jsp:include>

<div class="main-content">

<div id="lightbox" class="lightbox" onclick="closeLightbox()">
    <button type="button" class="lightbox-close" aria-label="Đóng">&times;</button>
    <img id="lightbox-img" src="" alt="Ảnh sản phẩm">
</div>

<c:if test="${not empty message}"><div class="alert alert-success">${message}</div></c:if>
<c:if test="${not empty error}"><div class="alert alert-error">${error}</div></c:if>

<section class="approval-section">
    <c:choose>
        <c:when test="${not empty product}">
            <div class="approval-card">
                <div class="approval-info">
                    <h2 class="product-title">${product.name}</h2>
                    <p class="shop-name">Shop: ${product.seller.shopName}</p>
                    <div class="info-list">
                        <div class="info-item"><span class="label">Mô tả</span><span class="value">${product.description}</span></div>
                        <div class="info-row">
                            <div class="info-item"><span class="label">Giá gốc</span><span class="value price-original"><fmt:formatNumber value="${product.originalPrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></span></div>
                            <div class="info-item"><span class="label">Giá bán</span><span class="value price-sale"><fmt:formatNumber value="${product.salePrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></span></div>
                        </div>
                        <div class="info-row">
                            <div class="info-item"><span class="label">Số lượng</span><span class="value">${product.quantity}</span></div>
                            <div class="info-item"><span class="label">Chủ shop</span><span class="value">${product.seller.fullName}</span></div>
                        </div>
                        <div class="info-row">
                            <div class="info-item"><span class="label">Ngày SX</span><span class="value"><fmt:formatDate value="${product.manufactureDate}" pattern="dd/MM/yyyy"/></span></div>
                            <div class="info-item"><span class="label">Hạn SD</span><span class="value"><fmt:formatDate value="${product.expirationDate}" pattern="dd/MM/yyyy"/></span></div>
                        </div>
                        <div class="info-row">
                            <div class="info-item"><span class="label">Ngày đăng</span><span class="value"><fmt:formatDate value="${product.createdDate}" pattern="dd/MM/yyyy HH:mm"/></span></div>
                            <div class="info-item"><span class="label">Ngày kiểm duyệt</span><span class="value"><fmt:formatDate value="${product.approvedDate}" pattern="dd/MM/yyyy HH:mm"/></span></div>
                        </div>
                        <div class="info-item">
                            <span class="label">Trạng thái</span>
                            <span class="value status-${product.status.toString().toLowerCase()}">
                                <c:choose>
                                    <c:when test="${product.status.name() == 'ACTIVE'}">Đã duyệt</c:when>
                                    <c:when test="${product.status.name() == 'PENDING_APPROVAL'}">Chờ duyệt</c:when>
                                    <c:when test="${product.status.name() == 'REJECTED'}">Từ chối</c:when>
                                    <c:when test="${product.status.name() == 'HIDDEN'}">Đã ẩn</c:when>
                                    <c:otherwise>${product.status}</c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                    </div>
                    <div class="approval-actions">
                    <c:if test="${product.status.name() == 'PENDING_APPROVAL'}">
                        <form action="${pageContext.request.contextPath}/admin/approveProduct" method="post">
                            <input type="hidden" name="action" value="approve">
                            <input type="hidden" name="productId" value="${product.productId}">
                            <input type="hidden" name="productName" value="${product.name}">
                            <button type="submit" class="btn btn-approve"><span>Duyệt</span></button>
                        </form>
                        <form action="${pageContext.request.contextPath}/admin/approveProduct" method="post">
                            <input type="hidden" name="action" value="reject">
                            <input type="hidden" name="productId" value="${product.productId}">
                            <input type="hidden" name="productName" value="${product.name}">
                            <button type="submit" class="btn btn-reject"><span>Từ chối</span></button>
                        </form>
                    </c:if>
                    </div>
                </div>
                <div class="approval-image">
                    <c:choose>
                        <c:when test="${not empty product.imageUrl}">
                            <img src="${product.imageUrl}" alt="Ảnh sản phẩm" onclick="openLightbox(this.src)" onerror="this.src='https://via.placeholder.com/400x300?text=No+Image'">
                            <span class="click-hint">Click để phóng to</span>
                        </c:when>
                        <c:otherwise><div class="no-image">Chưa có ảnh sản phẩm</div></c:otherwise>
                    </c:choose>
                </div>
            </div>
        </c:when>
        <c:otherwise><div class="empty-state-small"><span>📦</span> Chọn một sản phẩm từ danh sách bên dưới để xem chi tiết</div></c:otherwise>
    </c:choose>
</section>


<section class="product-list-section">
    <form action="${pageContext.request.contextPath}/admin/approveProduct" method="get" class="filter-bar">
        <div class="filter-group">
            <label for="tab">Trạng thái:</label>
            <select id="tab" name="tab" onchange="this.form.submit()">
                <option value="pending" ${currentTab == 'pending' ? 'selected' : ''}>Chờ duyệt</option>
                <option value="rejected" ${currentTab == 'rejected' ? 'selected' : ''}>Từ chối</option>
                <option value="active" ${currentTab == 'active' ? 'selected' : ''}>Đã duyệt</option>
                <option value="hidden" ${currentTab == 'hidden' ? 'selected' : ''}>Đã ẩn</option>
                <option value="all" ${currentTab == 'all' ? 'selected' : ''}>Tất cả</option>
            </select>   
        </div>
        <div class="filter-group">
            <label for="sort">Sắp xếp:</label>
            <select id="sort" name="sort" onchange="this.form.submit()">
                <option value="newest" ${currentSort == 'newest' ? 'selected' : ''}>Mới nhất</option>
                <option value="oldest" ${currentSort == 'oldest' ? 'selected' : ''}>Cũ nhất</option>
                <option value="name" ${currentSort == 'name' ? 'selected' : ''}>Theo tên sản phẩm</option>
                <option value="shop" ${currentSort == 'shop' ? 'selected' : ''}>Theo tên shop</option>
            </select>
        </div>
        <div class="filter-count">
            <strong>${productList.size()}</strong>
        </div>
    </form>
    
    <c:choose>
        <c:when test="${not empty productList}">
            <div class="table-wrapper">
                <table class="product-table">
                    <thead>
                        <tr>
                            <th>Sản phẩm</th>
                            <th>Shop</th>
                            <th>Chủ shop</th>
                            <th>Giá bán</th>
                            <th>Ngày đăng</th>
                            <th>Ngày kiểm duyệt</th>
                            <th>Trạng thái</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="p" items="${productList}">
                            <tr onclick="window.location='${pageContext.request.contextPath}/admin/approveProduct?action=detail&productId=${p.productId}&tab=${currentTab}&sort=${currentSort}'" class="clickable-row">
                                <td>${p.name}</td>
                                <td>${p.seller.shopName}</td>
                                <td>${p.seller.fullName}</td>
                                <td><fmt:formatNumber value="${p.salePrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></td>
                                <td><fmt:formatDate value="${p.createdDate}" pattern="dd/MM/yyyy"/></td>
                                <td><fmt:formatDate value="${p.approvedDate}" pattern="dd/MM/yyyy"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${p.status.name() == 'ACTIVE'}"><span class="status-badge status-active">Đã duyệt</span></c:when>
                                        <c:when test="${p.status.name() == 'PENDING_APPROVAL'}"><span class="status-badge status-pending_approval">Chờ duyệt</span></c:when>
                                        <c:when test="${p.status.name() == 'REJECTED'}"><span class="status-badge status-rejected">Từ chối</span></c:when>
                                        <c:when test="${p.status.name() == 'HIDDEN'}"><span class="status-badge status-hidden">Đã ẩn</span></c:when>
                                        <c:otherwise><span class="status-badge">${p.status}</span></c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>

        </c:when>
        <c:otherwise><p class="no-data">Không có sản phẩm nào trong danh sách này</p></c:otherwise>
    </c:choose>
</section>

</div>

<script>
/* Lightbox for image zoom */
let zoomLevel = 1;

function openLightbox(src) {
    zoomLevel = 1;
    const img = document.getElementById('lightbox-img');
    img.src = src;
    img.style.transform = 'scale(1)';
    document.getElementById('lightbox').style.display = 'flex';
}

function closeLightbox() {
    document.getElementById('lightbox').style.display = 'none';
    zoomLevel = 1;
}

document.getElementById('lightbox-img').addEventListener('click', function(e) {
    e.stopPropagation();
});

document.getElementById('lightbox').addEventListener('wheel', function(e) {
    e.preventDefault();
    zoomLevel = e.deltaY < 0 ? Math.min(4, zoomLevel + 0.2) : Math.max(0.5, zoomLevel - 0.2);
    document.getElementById('lightbox-img').style.transform = 'scale(' + zoomLevel + ')';
});

document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') closeLightbox();
});
</script>
</body>
</html>
