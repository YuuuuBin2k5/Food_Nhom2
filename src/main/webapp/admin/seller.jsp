<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<link rel="stylesheet" type="text/css" href="css/seller.css">

<!-- ==================== LIGHTBOX ==================== -->
<div id="lightbox" class="lightbox" onclick="closeLightbox()">
    <button type="button" class="lightbox-close" aria-label="Đóng">&times;</button>
    <img id="lightbox-img" src="" alt="Giấy phép kinh doanh">
</div>

<!-- ==================== HEADER ==================== -->
<header class="page-header">
    <h2>Duyệt giấy phép kinh doanh</h2>
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
    <c:when test="${not empty seller}">
        <main class="seller-container">
            
            <!-- BÊN TRÁI: THÔNG TIN SELLER -->
            <aside class="info-section">
                <header class="info-header">
                    <h3>${seller.shopName}</h3>
                    <p>${seller.fullName}</p>
                </header>
                
                <section class="info-content">
                    <div class="info-row">
                        <span class="label">Số điện thoại</span>
                        <span class="value">${seller.phoneNumber}</span>
                    </div>
                    
                    <div class="info-row">
                        <span class="label">Email</span>
                        <span class="value">${seller.email}</span>
                    </div>
                    
                    <div class="info-row">
                        <span class="label">Địa chỉ</span>
                        <span class="value">${seller.address}</span>
                    </div>
                    
                    <div class="info-row">
                        <span class="label">Ngày nộp giấy phép</span>
                        <span class="value">
                            <fmt:formatDate value="${seller.licenseSubmittedDate}" pattern="dd/MM/yyyy HH:mm"/>
                        </span>
                    </div>
                </section>
                
                <footer class="info-actions">
                    <form action="admin" method="post">
                        <input type="hidden" name="action" value="approveSeller">
                        <input type="hidden" name="sellerId" value="${seller.userId}">
                        <input type="hidden" name="shopName" value="${seller.shopName}">
                        <button type="submit" class="btn btn-approve">Duyệt</button>
                    </form>
                    
                    <form action="admin" method="post">
                        <input type="hidden" name="action" value="rejectSeller">
                        <input type="hidden" name="sellerId" value="${seller.userId}">
                        <input type="hidden" name="shopName" value="${seller.shopName}">
                        <button type="submit" class="btn btn-reject">Từ chối</button>
                    </form>
                </footer>
            </aside>
            
            <!-- BÊN PHẢI: ẢNH GIẤY PHÉP -->
            <figure class="license-section">
                <img src="${seller.businessLicenseUrl}" 
                     class="license-image" 
                     alt="Giấy phép kinh doanh của ${seller.shopName}"
                     onclick="openLightbox(this.src)"
                     onerror="this.src='https://via.placeholder.com/600x400?text=Không+tải+được+ảnh'">
                <figcaption class="click-hint">Click vào ảnh để xem chi tiết</figcaption>
            </figure>
        </main>
    </c:when>
    
    <c:otherwise>
        <main class="seller-container">
            <section class="empty-state">
                <span class="icon" aria-hidden="true">✓</span>
                <h3>Không có yêu cầu nào</h3>
                <p>Tất cả seller đã được xử lý</p>
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
