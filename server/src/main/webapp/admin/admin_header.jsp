<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<header class="top-header">
    <div class="header-brand">ADMIN PANEL</div>
    <nav class="header-nav">
        <a href="${pageContext.request.contextPath}/admin/statistics" 
           class="nav-item ${activePage == 'statistics' ? 'active' : ''}">Thống kê</a>
        <a href="${pageContext.request.contextPath}/admin/approveSeller" 
           class="nav-item ${activePage == 'seller' ? 'active' : ''}">Duyệt Seller</a>
        <a href="${pageContext.request.contextPath}/admin/approveProduct" 
           class="nav-item ${activePage == 'product' ? 'active' : ''}">Duyệt Product</a>
        <a href="${pageContext.request.contextPath}/admin/manageUser" 
           class="nav-item ${activePage == 'user' ? 'active' : ''}">Quản lí User</a>
    </nav>
    <a href="${pageContext.request.contextPath}/admin?action=logout" class="nav-item logout">Đăng xuất</a>
</header>
