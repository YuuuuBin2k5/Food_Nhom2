<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" type="text/css" href="css/ban.css">

<!-- ==================== THÔNG BÁO ==================== -->
<c:if test="${not empty message}">
    <div class="alert alert-success" role="alert">${message}</div>
</c:if>
<c:if test="${not empty error}">
    <div class="alert alert-error" role="alert">${error}</div>
</c:if>

<!-- ==================== HEADER ==================== -->
<header class="page-header">
    <h2>Quản lý User</h2>
</header>

<!-- ==================== THANH TÌM KIẾM & LỌC ==================== -->
<section class="search-section">
    <form action="admin" method="get" class="search-form">
        <input type="hidden" name="action" value="searchUser">
        <input type="text" name="keyword" class="search-input" 
               placeholder="Tìm theo tên, email..." value="${keyword}">
        <button type="submit" class="btn btn-search">Tìm kiếm</button>
    </form>
    
    <div class="filter-buttons">
        <a href="admin?action=viewBanUser&filter=all" 
           class="filter-btn ${filter == 'all' || empty filter ? 'active' : ''}">Tất cả</a>
        <a href="admin?action=viewBanUser&filter=sellers" 
           class="filter-btn ${filter == 'sellers' ? 'active' : ''}">Sellers</a>
        <a href="admin?action=viewBanUser&filter=buyers" 
           class="filter-btn ${filter == 'buyers' ? 'active' : ''}">Buyers</a>
        <a href="admin?action=viewBanUser&filter=shippers" 
           class="filter-btn ${filter == 'shippers' ? 'active' : ''}">Shippers</a>
        <a href="admin?action=viewBanUser&filter=banned" 
           class="filter-btn ${filter == 'banned' ? 'active' : ''}">Đã ban</a>
    </div>
</section>

<!-- ==================== DANH SÁCH USER ==================== -->
<main class="user-container">
    
    <c:if test="${empty sellers && empty buyers && empty shippers}">
        <div class="empty-state">
            <span class="icon">👤</span>
            <h3>Không tìm thấy user nào</h3>
            <p>Thử tìm kiếm với từ khóa khác</p>
        </div>
    </c:if>
    
    <c:if test="${not empty sellers || not empty buyers || not empty shippers}">
        <table class="user-table">
            <thead>
                <tr>
                    <th>Tên</th>
                    <th>Role</th>
                    <th>Email</th>
                    <th>SĐT</th>
                    <th>Trạng thái</th>
                    <th>Hành động</th>
                </tr>
            </thead>
            <tbody>
                <!-- Sellers -->
                <c:forEach var="s" items="${sellers}">
                    <tr class="${s.banned ? 'banned-row' : ''}">
                        <td>${s.fullName}</td>
                        <td><span class="role-badge seller">Seller</span></td>
                        <td>${s.email}</td>
                        <td>${s.phoneNumber}</td>
                        <td>
                            <c:choose>
                                <c:when test="${s.banned}">
                                    <span class="status-badge banned">Đã ban</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="status-badge active">Hoạt động</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${s.banned}">
                                    <form action="admin" method="post" class="action-form">
                                        <input type="hidden" name="action" value="unbanSeller">
                                        <input type="hidden" name="userId" value="${s.userId}">
                                        <input type="hidden" name="userName" value="${s.fullName}">
                                        <button type="submit" class="btn btn-unban">Unban</button>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <form action="admin" method="post" class="action-form">
                                        <input type="hidden" name="action" value="banSeller">
                                        <input type="hidden" name="userId" value="${s.userId}">
                                        <input type="hidden" name="userName" value="${s.fullName}">
                                        <button type="submit" class="btn btn-ban" 
                                                onclick="return confirm('Bạn có chắc muốn ban user này?')">Ban</button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
                
                <!-- Buyers -->
                <c:forEach var="b" items="${buyers}">
                    <tr class="${b.banned ? 'banned-row' : ''}">
                        <td>${b.fullName}</td>
                        <td><span class="role-badge buyer">Buyer</span></td>
                        <td>${b.email}</td>
                        <td>${b.phoneNumber}</td>
                        <td>
                            <c:choose>
                                <c:when test="${b.banned}">
                                    <span class="status-badge banned">Đã ban</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="status-badge active">Hoạt động</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${b.banned}">
                                    <form action="admin" method="post" class="action-form">
                                        <input type="hidden" name="action" value="unbanBuyer">
                                        <input type="hidden" name="userId" value="${b.userId}">
                                        <input type="hidden" name="userName" value="${b.fullName}">
                                        <button type="submit" class="btn btn-unban">Unban</button>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <form action="admin" method="post" class="action-form">
                                        <input type="hidden" name="action" value="banBuyer">
                                        <input type="hidden" name="userId" value="${b.userId}">
                                        <input type="hidden" name="userName" value="${b.fullName}">
                                        <button type="submit" class="btn btn-ban"
                                                onclick="return confirm('Bạn có chắc muốn ban user này?')">Ban</button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
                
                <!-- Shippers -->
                <c:forEach var="sh" items="${shippers}">
                    <tr class="${sh.banned ? 'banned-row' : ''}">
                        <td>${sh.fullName}</td>
                        <td><span class="role-badge shipper">Shipper</span></td>
                        <td>${sh.email}</td>
                        <td>${sh.phoneNumber}</td>
                        <td>
                            <c:choose>
                                <c:when test="${sh.banned}">
                                    <span class="status-badge banned">Đã ban</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="status-badge active">Hoạt động</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${sh.banned}">
                                    <form action="admin" method="post" class="action-form">
                                        <input type="hidden" name="action" value="unbanShipper">
                                        <input type="hidden" name="userId" value="${sh.userId}">
                                        <input type="hidden" name="userName" value="${sh.fullName}">
                                        <button type="submit" class="btn btn-unban">Unban</button>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <form action="admin" method="post" class="action-form">
                                        <input type="hidden" name="action" value="banShipper">
                                        <input type="hidden" name="userId" value="${sh.userId}">
                                        <input type="hidden" name="userName" value="${sh.fullName}">
                                        <button type="submit" class="btn btn-ban"
                                                onclick="return confirm('Bạn có chắc muốn ban user này?')">Ban</button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>
</main>
