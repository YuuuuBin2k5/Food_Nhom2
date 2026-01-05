<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Duyệt Seller - Admin</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin_css/admin_main.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin_css/admin_approves_seller.css">
</head>
<body>

<jsp:include page="../common/sidebar.jsp">
    <jsp:param name="currentPath" value="/admin/approveSeller" />
</jsp:include>

<div class="main-content">

<div id="lightbox" class="lightbox" onclick="closeLightbox()">
    <button type="button" class="lightbox-close" aria-label="Đóng">&times;</button>
    <img id="lightbox-img" src="" alt="Giấy phép kinh doanh">
</div>

<c:if test="${not empty message}"><div class="alert alert-success">${message}</div></c:if>
<c:if test="${not empty error}"><div class="alert alert-error">${error}</div></c:if>

<section class="approval-section">
    <c:choose>
        <c:when test="${not empty seller}">
            <div class="approval-card">
                <div class="approval-info">
                    <h2 class="shop-title">${seller.shopName}</h2>
                    <div class="info-list">
                        <div class="info-item"><span class="label">Chủ Shop</span><span class="value">${seller.fullName}</span></div>
                        <div class="info-item"><span class="label">Số điện thoại</span><span class="value">${seller.phoneNumber}</span></div>
                        <div class="info-item"><span class="label">Email</span><span class="value">${seller.email}</span></div>
                        <div class="info-item"><span class="label">Địa chỉ</span><span class="value">${seller.address}</span></div>
                        <div class="info-item">
                            <span class="label">Trạng thái</span>
                            <span class="value status-${seller.verificationStatus.toString().toLowerCase()}">
                                <c:choose>
                                    <c:when test="${seller.verificationStatus.name() == 'APPROVED'}">Đã duyệt</c:when>
                                    <c:when test="${seller.verificationStatus.name() == 'PENDING'}">Chờ duyệt</c:when>
                                    <c:when test="${seller.verificationStatus.name() == 'REJECTED'}">Từ chối</c:when>
                                    <c:otherwise>Chưa đăng ký</c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                        <div class="info-item"><span class="label">Ngày nộp</span><span class="value"><fmt:formatDate value="${seller.licenseSubmittedDate}" pattern="dd/MM/yyyy HH:mm"/></span></div>
                        <div class="info-item"><span class="label">Ngày kiểm duyệt</span><span class="value"><fmt:formatDate value="${seller.licenseApprovedDate}" pattern="dd/MM/yyyy HH:mm"/></span></div>
                    </div>
                    <div class="approval-actions">
                        <c:if test="${seller.verificationStatus.name() == 'PENDING'}">
                            <form action="${pageContext.request.contextPath}/admin/approveSeller" method="post">
                                <input type="hidden" name="action" value="approve">
                                <input type="hidden" name="sellerId" value="${seller.userId}">
                                <input type="hidden" name="shopName" value="${seller.shopName}">
                                <button type="submit" class="btn btn-approve"><span>Duyệt</span></button>
                            </form>
                            <form action="${pageContext.request.contextPath}/admin/approveSeller" method="post">
                                <input type="hidden" name="action" value="reject">
                                <input type="hidden" name="sellerId" value="${seller.userId}">
                                <input type="hidden" name="shopName" value="${seller.shopName}">
                                <button type="submit" class="btn btn-reject"><span>Từ chối</span></button>
                            </form>
                        </c:if>
                    </div>
                </div>
                <div class="approval-image">
                    <c:choose>
                        <c:when test="${not empty seller.businessLicenseUrl}">
                            <img src="${seller.businessLicenseUrl}" alt="Giấy phép kinh doanh" onclick="openLightbox(this.src)" onerror="this.src='https://via.placeholder.com/400x300?text=No+Image'">
                            <span class="click-hint">Click để phóng to</span>
                        </c:when>
                        <c:otherwise><div class="no-license">Chưa đăng ký giấy phép</div></c:otherwise>
                    </c:choose>
                </div>
            </div>
        </c:when>
        <c:otherwise><div class="empty-state-small"><span>📋</span> Chọn một seller từ danh sách bên dưới để xem chi tiết</div></c:otherwise>
    </c:choose>
</section>

<section class="seller-list-section">
    <form action="${pageContext.request.contextPath}/admin/approveSeller" method="get" class="filter-bar">
        <div class="filter-group">
            <label for="tab">Trạng thái:</label>
            <select id="tab" name="tab" onchange="this.form.submit()">
                <option value="pending" ${currentTab == 'pending' ? 'selected' : ''}>Chờ duyệt</option>
                <option value="rejected" ${currentTab == 'rejected' ? 'selected' : ''}>Từ chối</option>
                <option value="approved" ${currentTab == 'approved' ? 'selected' : ''}>Đã duyệt</option>
                <option value="unverified" ${currentTab == 'unverified' ? 'selected' : ''}>Chưa đăng ký</option>
                <option value="all" ${currentTab == 'all' ? 'selected' : ''}>Tất cả</option>
            </select>
        </div>
        <div class="filter-group">
            <label for="sort">Sắp xếp:</label>
            <select id="sort" name="sort" onchange="this.form.submit()">
                <option value="newest" ${currentSort == 'newest' ? 'selected' : ''}>Mới nhất</option>
                <option value="oldest" ${currentSort == 'oldest' ? 'selected' : ''}>Cũ nhất</option>
                <option value="name" ${currentSort == 'name' ? 'selected' : ''}>Theo tên chủ shop</option>
                <option value="shop" ${currentSort == 'shop' ? 'selected' : ''}>Theo tên shop</option>
            </select>
        </div>
        <div class="filter-count">
            <strong>${sellerList.size()}</strong>
        </div>
    </form>
    
    <c:choose>
        <c:when test="${not empty sellerList}">
            <div class="table-wrapper">
                <table class="seller-table">
                    <thead><tr><th>Shop</th><th>Chủ shop</th><th>Ngày nộp</th><th>Ngày kiểm duyệt</th><th>Trạng thái</th></tr></thead>
                    <tbody>
                        <c:forEach var="s" items="${sellerList}">
                            <tr onclick="window.location='${pageContext.request.contextPath}/admin/approveSeller?action=detail&sellerId=${s.userId}&tab=${currentTab}&sort=${currentSort}'" class="clickable-row">
                                <td>${s.shopName}</td>
                                <td>${s.fullName}</td>
                                <td><fmt:formatDate value="${s.licenseSubmittedDate}" pattern="dd/MM/yyyy"/></td>
                                <td><fmt:formatDate value="${s.licenseApprovedDate}" pattern="dd/MM/yyyy"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${s.verificationStatus.name() == 'APPROVED'}"><span class="status-badge status-approved">Đã duyệt</span></c:when>
                                        <c:when test="${s.verificationStatus.name() == 'PENDING'}"><span class="status-badge status-pending">Chờ duyệt</span></c:when>
                                        <c:when test="${s.verificationStatus.name() == 'REJECTED'}"><span class="status-badge status-rejected">Từ chối</span></c:when>
                                        <c:otherwise><span class="status-badge status-unverified">Chưa đăng ký</span></c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:when>
        <c:otherwise><p class="no-data">Không có seller nào trong danh sách này</p></c:otherwise>
    </c:choose>
</section>

</div>

<script>
/* Lightbox - zoom ảnh giấy phép */
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
