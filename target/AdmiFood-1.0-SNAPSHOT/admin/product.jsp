<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<link rel="stylesheet" type="text/css" href="css/product.css">

<!-- ==================== LIGHTBOX ==================== -->
<div id="lightbox" class="lightbox" onclick="closeLightbox()">
    <button type="button" class="lightbox-close" aria-label="Đóng">&times;</button>
    <img id="lightbox-img" src="" alt="Ảnh sản phẩm">
</div>

<!-- ==================== HEADER ==================== -->
<header class="page-header">
    <h2>Duyệt sản phẩm</h2>
    <span class="pending-badge">${pendingCount} đang chờ</span>
</header>

<!-- ==================== THÔNG BÁO ==================== -->
<c:if test="${not empty message}">
    <div class="alert alert-success" role="alert">${message}</div>
</c:if>
<c:if test="${not empty error}">
    <div class="alert alert-error" role="alert">${error}</div>
</c:if>

<!-- ==================== NỘI DUNG CHÍNH ==================== -->
<c:choose>
    <c:when test="${not empty product}">
        <main class="product-container">
            
            <!-- BÊN TRÁI: THÔNG TIN SẢN PHẨM -->
            <aside class="info-section">
                <header class="info-header">
                    <h3>${product.name}</h3>
                    <p>Shop: ${product.seller.shopName}</p>
                </header>
                
                <section class="info-content">
                    <!-- Box giá -->
                    <div class="price-box">
                        <div class="original-price">
                            <span class="label">Giá gốc</span>
                            <span class="value"><fmt:formatNumber value="${product.originalPrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></span>
                        </div>
                        <div class="sale-price">
                            <span class="label">Giá bán</span>
                            <span class="value"><fmt:formatNumber value="${product.salePrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></span>
                        </div>
                    </div>
                    
                    <div class="info-row">
                        <span class="label">Mô tả</span>
                        <span class="value description">${product.description}</span>
                    </div>
                    
                    <div class="info-row">
                        <span class="label">Số lượng</span>
                        <span class="value">${product.quantity}</span>
                    </div>
                    
                    <div class="info-row">
                        <span class="label">Ngày sản xuất</span>
                        <span class="value">
                            <fmt:formatDate value="${product.manufactureDate}" pattern="dd/MM/yyyy"/>
                        </span>
                    </div>
                    
                    <div class="info-row">
                        <span class="label">Hạn sử dụng</span>
                        <span class="value">
                            <fmt:formatDate value="${product.expirationDate}" pattern="dd/MM/yyyy"/>
                        </span>
                    </div>
                    
                    <div class="info-row">
                        <span class="label">Ngày đăng bán</span>
                        <span class="value">
                            <fmt:formatDate value="${product.createdDate}" pattern="dd/MM/yyyy HH:mm"/>
                        </span>
                    </div>
                </section>
                
                <footer class="info-actions">
                    <form action="admin" method="post">
                        <input type="hidden" name="action" value="approveProduct">
                        <input type="hidden" name="productId" value="${product.productId}">
                        <input type="hidden" name="productName" value="${product.name}">
                        <button type="submit" class="btn btn-approve">Duyệt</button>
                    </form>
                    
                    <form action="admin" method="post">
                        <input type="hidden" name="action" value="rejectProduct">
                        <input type="hidden" name="productId" value="${product.productId}">
                        <input type="hidden" name="productName" value="${product.name}">
                        <button type="submit" class="btn btn-reject">Từ chối</button>
                    </form>
                </footer>
            </aside>
            
            <!-- BÊN PHẢI: ẢNH SẢN PHẨM -->
            <figure class="image-section">
                <img src="${product.imageUrl}" 
                     class="product-image" 
                     alt="Ảnh sản phẩm ${product.name}"
                     onclick="openLightbox(this.src)"
                     onerror="this.src='https://via.placeholder.com/600x400?text=Không+có+ảnh'">
                <figcaption class="click-hint">Click vào ảnh để xem chi tiết</figcaption>
            </figure>
        </main>
    </c:when>
    
    <c:otherwise>
        <main class="product-container">
            <section class="empty-state">
                <span class="icon" aria-hidden="true">✓</span>
                <h3>Không có sản phẩm nào</h3>
                <p>Tất cả sản phẩm đã được xử lý</p>
            </section>
        </main>
    </c:otherwise>
</c:choose>

<!-- ==================== JAVASCRIPT ==================== -->
<script>
function openLightbox(src) {
    document.getElementById('lightbox-img').src = src;
    document.getElementById('lightbox').style.display = 'flex';
}

function closeLightbox() {
    document.getElementById('lightbox').style.display = 'none';
}

document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') closeLightbox();
});
</script>
