<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Duyệt Seller - Admin</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin_main.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin_approves_seller.css">
</head>
<body>

<jsp:include page="admin_header.jsp"/>

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
                        <div class="info-item"><span class="label">Trạng thái</span><span class="value status-${seller.verificationStatus.toString().toLowerCase()}">${seller.verificationStatus}</span></div>
                        <div class="info-item"><span class="label">Ngày nộp</span><span class="value"><fmt:formatDate value="${seller.licenseSubmittedDate}" pattern="dd/MM/yyyy HH:mm"/></span></div>
                        <div class="info-item"><span class="label">Ngày duyệt</span><span class="value"><fmt:formatDate value="${seller.licenseApprovedDate}" pattern="dd/MM/yyyy HH:mm"/></span></div>
                    </div>
                    <c:if test="${seller.verificationStatus.name() != 'UNVERIFIED'}">
                        <div class="approval-actions">
                            <form action="${pageContext.request.contextPath}/admin/approveSeller" method="post">
                                <input type="hidden" name="action" value="approve">
                                <input type="hidden" name="sellerId" value="${seller.userId}">
                                <input type="hidden" name="shopName" value="${seller.shopName}">
                                <button type="submit" class="btn btn-approve">Duyệt</button>
                            </form>
                            <form action="${pageContext.request.contextPath}/admin/approveSeller" method="post">
                                <input type="hidden" name="action" value="reject">
                                <input type="hidden" name="sellerId" value="${seller.userId}">
                                <input type="hidden" name="shopName" value="${seller.shopName}">
                                <button type="submit" class="btn btn-reject">Từ chối</button>
                            </form>
                        </div>
                    </c:if>
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
    <div class="tabs">
        <a href="${pageContext.request.contextPath}/admin/approveSeller?tab=pending" class="tab-btn ${currentTab == 'pending' ? 'active' : ''}">Chờ duyệt (${pendingCount})</a>
        <a href="${pageContext.request.contextPath}/admin/approveSeller?tab=rejected" class="tab-btn ${currentTab == 'rejected' ? 'active' : ''}">Từ chối (${rejectedCount})</a>
        <a href="${pageContext.request.contextPath}/admin/approveSeller?tab=approved" class="tab-btn ${currentTab == 'approved' ? 'active' : ''}">Đã duyệt (${approvedCount})</a>
        <a href="${pageContext.request.contextPath}/admin/approveSeller?tab=unverified" class="tab-btn ${currentTab == 'unverified' ? 'active' : ''}">Chưa đăng ký (${unverifiedCount})</a>
        <a href="${pageContext.request.contextPath}/admin/approveSeller?tab=all" class="tab-btn ${currentTab == 'all' ? 'active' : ''}">Tất cả (${allCount})</a>
    </div>
    
    <c:choose>
        <c:when test="${not empty sellerList}">
            <div class="table-wrapper">
                <table class="seller-table">
                    <thead><tr><th>Shop</th><th>Chủ shop</th><th>Ngày nộp</th><th>Ngày duyệt</th><th>Trạng thái</th></tr></thead>
                    <tbody>
                        <c:forEach var="s" items="${sellerList}">
                            <tr onclick="window.location='${pageContext.request.contextPath}/admin/approveSeller?action=detail&sellerId=${s.userId}&tab=${currentTab}&page=${currentPage}'" class="clickable-row">
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
            <c:if test="${totalPages > 1}">
                <div class="pagination">
                    <c:if test="${currentPage > 1}"><a href="${pageContext.request.contextPath}/admin/approveSeller?tab=${currentTab}&page=${currentPage - 1}" class="page-btn">&laquo; Trước</a></c:if>
                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <c:choose>
                            <c:when test="${i == currentPage}"><span class="page-btn active">${i}</span></c:when>
                            <c:otherwise><a href="${pageContext.request.contextPath}/admin/approveSeller?tab=${currentTab}&page=${i}" class="page-btn">${i}</a></c:otherwise>
                        </c:choose>
                    </c:forEach>
                    <c:if test="${currentPage < totalPages}"><a href="${pageContext.request.contextPath}/admin/approveSeller?tab=${currentTab}&page=${currentPage + 1}" class="page-btn">Sau &raquo;</a></c:if>
                </div>
            </c:if>
        </c:when>
        <c:otherwise><p class="no-data">Không có seller nào trong danh sách này</p></c:otherwise>
    </c:choose>
</section>

</div>

<script>
let zoomLevel = 1;
function openLightbox(src) { zoomLevel = 1; document.getElementById('lightbox-img').src = src; document.getElementById('lightbox-img').style.transform = 'scale(1)'; document.getElementById('lightbox').style.display = 'flex'; }
function closeLightbox() { document.getElementById('lightbox').style.display = 'none'; zoomLevel = 1; }
document.getElementById('lightbox-img').addEventListener('click', function(e) { e.stopPropagation(); });
document.getElementById('lightbox').addEventListener('wheel', function(e) { e.preventDefault(); zoomLevel = e.deltaY < 0 ? Math.min(4, zoomLevel + 0.2) : Math.max(0.5, zoomLevel - 0.2); document.getElementById('lightbox-img').style.transform = 'scale(' + zoomLevel + ')'; });
document.addEventListener('keydown', function(e) { if (e.key === 'Escape') closeLightbox(); });
</script>
</body>
</html>
