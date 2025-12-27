<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Quản lí User - Admin</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin_main.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin_manages_user.css">
</head>
<body>

<jsp:include page="admin_header.jsp"/>

<div class="main-content">

<c:if test="${not empty message}"><div class="alert alert-success">${message}</div></c:if>
<c:if test="${not empty error}"><div class="alert alert-error">${error}</div></c:if>

<section class="log-section">
    <div class="log-header">
        <c:choose>
            <c:when test="${not empty selectedUserName}">
                <span class="selected-user">${selectedUserName}</span>
                <span class="selected-role ${selectedUserType}">${selectedUserType}</span>
            </c:when>
            <c:otherwise><span class="log-title">Log hoạt động</span></c:otherwise>
        </c:choose>
    </div>
    <div class="log-container">
        <c:choose>
            <c:when test="${empty selectedUserId}">
                <div class="log-empty"><span class="icon">📋</span><p>Chọn một user từ danh sách bên dưới để xem log hoạt động</p></div>
            </c:when>
            <c:when test="${empty userLogs}">
                <div class="log-empty"><span class="icon">📭</span><p>Chưa có log hoạt động nào cho user này</p></div>
            </c:when>
            <c:otherwise>
                <table class="log-table">
                    <thead><tr><th>Thời gian</th><th>Hành động</th><th>Mô tả</th><th>Đối tượng</th></tr></thead>
                    <tbody>
                        <c:forEach var="log" items="${userLogs}">
                            <tr>
                                <td class="log-time"><fmt:formatDate value="${log.createdAt}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
                                <td><span class="action-badge ${log.action}">${log.action}</span></td>
                                <td>${log.description}</td>
                                <td><c:if test="${not empty log.targetType}"><span class="target-info">${log.targetType}: ${log.targetId}</span></c:if></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</section>

<section class="user-list-section">
    <div class="search-bar">
        <form action="${pageContext.request.contextPath}/admin/manageUser" method="get" class="search-form">
            <input type="hidden" name="action" value="search">
            <input type="text" name="keyword" class="search-input" placeholder="Tìm theo tên, email..." value="${keyword}">
            <button type="submit" class="btn btn-search">Tìm kiếm</button>
        </form>
        <div class="filter-buttons">
            <a href="${pageContext.request.contextPath}/admin/manageUser?filter=all" class="filter-btn ${filter == 'all' || empty filter ? 'active' : ''}">Tất cả</a>
            <a href="${pageContext.request.contextPath}/admin/manageUser?filter=sellers" class="filter-btn ${filter == 'sellers' ? 'active' : ''}">Sellers</a>
            <a href="${pageContext.request.contextPath}/admin/manageUser?filter=buyers" class="filter-btn ${filter == 'buyers' ? 'active' : ''}">Buyers</a>
            <a href="${pageContext.request.contextPath}/admin/manageUser?filter=shippers" class="filter-btn ${filter == 'shippers' ? 'active' : ''}">Shippers</a>
            <a href="${pageContext.request.contextPath}/admin/manageUser?filter=banned" class="filter-btn ${filter == 'banned' ? 'active' : ''}">Đã ban</a>
        </div>
    </div>

    <div class="table-wrapper">
        <c:if test="${empty sellers && empty buyers && empty shippers}">
            <div class="empty-state"><span class="icon">👤</span><h3>Không tìm thấy user nào</h3><p>Thử tìm kiếm với từ khóa khác</p></div>
        </c:if>
        
        <c:if test="${not empty sellers || not empty buyers || not empty shippers}">
            <table class="user-table">
                <thead><tr><th>Tên</th><th>Role</th><th>Email</th><th>SĐT</th><th>Trạng thái</th><th>Hành động</th></tr></thead>
                <tbody>
                    <c:forEach var="s" items="${sellers}">
                        <tr class="${s.banned ? 'banned-row' : ''} ${s.userId == selectedUserId ? 'selected-row' : ''}"
                            onclick="window.location='${pageContext.request.contextPath}/admin/manageUser?action=viewLog&userId=${s.userId}&userName=${s.fullName}&userType=seller&filter=${filter}&page=${currentPage}'">
                            <td>${s.fullName}</td>
                            <td><span class="role-badge seller">Seller</span></td>
                            <td>${s.email}</td>
                            <td>${s.phoneNumber}</td>
                            <td><c:choose><c:when test="${s.banned}"><span class="status-badge banned">Đã ban</span></c:when><c:otherwise><span class="status-badge active">Hoạt động</span></c:otherwise></c:choose></td>
                            <td>
                                <c:choose>
                                    <c:when test="${s.banned}">
                                        <form action="${pageContext.request.contextPath}/admin/manageUser" method="post" class="action-form" onclick="event.stopPropagation()">
                                            <input type="hidden" name="action" value="unban"><input type="hidden" name="userId" value="${s.userId}"><input type="hidden" name="userName" value="${s.fullName}"><input type="hidden" name="userType" value="seller">
                                            <button type="submit" class="btn btn-unban">Unban</button>
                                        </form>
                                    </c:when>
                                    <c:otherwise>
                                        <form action="${pageContext.request.contextPath}/admin/manageUser" method="post" class="action-form" onclick="event.stopPropagation()">
                                            <input type="hidden" name="action" value="ban"><input type="hidden" name="userId" value="${s.userId}"><input type="hidden" name="userName" value="${s.fullName}"><input type="hidden" name="userType" value="seller">
                                            <button type="submit" class="btn btn-ban" onclick="return confirm('Bạn có chắc muốn ban user này?')">Ban</button>
                                        </form>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:forEach var="b" items="${buyers}">
                        <tr class="${b.banned ? 'banned-row' : ''} ${b.userId == selectedUserId ? 'selected-row' : ''}"
                            onclick="window.location='${pageContext.request.contextPath}/admin/manageUser?action=viewLog&userId=${b.userId}&userName=${b.fullName}&userType=buyer&filter=${filter}&page=${currentPage}'">
                            <td>${b.fullName}</td>
                            <td><span class="role-badge buyer">Buyer</span></td>
                            <td>${b.email}</td>
                            <td>${b.phoneNumber}</td>
                            <td><c:choose><c:when test="${b.banned}"><span class="status-badge banned">Đã ban</span></c:when><c:otherwise><span class="status-badge active">Hoạt động</span></c:otherwise></c:choose></td>
                            <td>
                                <c:choose>
                                    <c:when test="${b.banned}">
                                        <form action="${pageContext.request.contextPath}/admin/manageUser" method="post" class="action-form" onclick="event.stopPropagation()">
                                            <input type="hidden" name="action" value="unban"><input type="hidden" name="userId" value="${b.userId}"><input type="hidden" name="userName" value="${b.fullName}"><input type="hidden" name="userType" value="buyer">
                                            <button type="submit" class="btn btn-unban">Unban</button>
                                        </form>
                                    </c:when>
                                    <c:otherwise>
                                        <form action="${pageContext.request.contextPath}/admin/manageUser" method="post" class="action-form" onclick="event.stopPropagation()">
                                            <input type="hidden" name="action" value="ban"><input type="hidden" name="userId" value="${b.userId}"><input type="hidden" name="userName" value="${b.fullName}"><input type="hidden" name="userType" value="buyer">
                                            <button type="submit" class="btn btn-ban" onclick="return confirm('Bạn có chắc muốn ban user này?')">Ban</button>
                                        </form>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:forEach var="sh" items="${shippers}">
                        <tr class="${sh.banned ? 'banned-row' : ''} ${sh.userId == selectedUserId ? 'selected-row' : ''}"
                            onclick="window.location='${pageContext.request.contextPath}/admin/manageUser?action=viewLog&userId=${sh.userId}&userName=${sh.fullName}&userType=shipper&filter=${filter}&page=${currentPage}'">
                            <td>${sh.fullName}</td>
                            <td><span class="role-badge shipper">Shipper</span></td>
                            <td>${sh.email}</td>
                            <td>${sh.phoneNumber}</td>
                            <td><c:choose><c:when test="${sh.banned}"><span class="status-badge banned">Đã ban</span></c:when><c:otherwise><span class="status-badge active">Hoạt động</span></c:otherwise></c:choose></td>
                            <td>
                                <c:choose>
                                    <c:when test="${sh.banned}">
                                        <form action="${pageContext.request.contextPath}/admin/manageUser" method="post" class="action-form" onclick="event.stopPropagation()">
                                            <input type="hidden" name="action" value="unban"><input type="hidden" name="userId" value="${sh.userId}"><input type="hidden" name="userName" value="${sh.fullName}"><input type="hidden" name="userType" value="shipper">
                                            <button type="submit" class="btn btn-unban">Unban</button>
                                        </form>
                                    </c:when>
                                    <c:otherwise>
                                        <form action="${pageContext.request.contextPath}/admin/manageUser" method="post" class="action-form" onclick="event.stopPropagation()">
                                            <input type="hidden" name="action" value="ban"><input type="hidden" name="userId" value="${sh.userId}"><input type="hidden" name="userName" value="${sh.fullName}"><input type="hidden" name="userType" value="shipper">
                                            <button type="submit" class="btn btn-ban" onclick="return confirm('Bạn có chắc muốn ban user này?')">Ban</button>
                                        </form>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <c:if test="${totalPages > 1 && empty keyword && (filter == 'sellers' || filter == 'buyers' || filter == 'shippers')}">
                <div class="pagination">
                    <c:if test="${currentPage > 1}"><a href="${pageContext.request.contextPath}/admin/manageUser?filter=${filter}&page=${currentPage - 1}" class="page-btn">&laquo; Trước</a></c:if>
                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <c:choose>
                            <c:when test="${i == currentPage}"><span class="page-btn active">${i}</span></c:when>
                            <c:otherwise><a href="${pageContext.request.contextPath}/admin/manageUser?filter=${filter}&page=${i}" class="page-btn">${i}</a></c:otherwise>
                        </c:choose>
                    </c:forEach>
                    <c:if test="${currentPage < totalPages}"><a href="${pageContext.request.contextPath}/admin/manageUser?filter=${filter}&page=${currentPage + 1}" class="page-btn">Sau &raquo;</a></c:if>
                </div>
            </c:if>
        </c:if>
    </div>
</section>

</div>
</body>
</html>
