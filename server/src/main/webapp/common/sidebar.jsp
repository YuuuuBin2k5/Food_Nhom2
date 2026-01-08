<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<header class="sidebar" id="sidebar">
    <div class="sidebar-container">
        <!-- Logo -->
        <c:choose>
            <c:when test="${sessionScope.role == 'SELLER'}">
                <c:set var="homeUrl" value="/seller/products" />
            </c:when>
            <c:when test="${sessionScope.role == 'SHIPPER'}">
                <c:set var="homeUrl" value="/shipper/orders" />
            </c:when>
            <c:when test="${sessionScope.role == 'ADMIN'}">
                <c:set var="homeUrl" value="/admin/statistics" />
            </c:when>
            <c:otherwise>
                <c:set var="homeUrl" value="/" />
            </c:otherwise>
        </c:choose>
        
        <a href="${pageContext.request.contextPath}${homeUrl}" class="logo-link">
            <div class="logo-icon" style="border-radius: 100%">
                <img src="${pageContext.request.contextPath}/images/icon-192.png" 
                     alt="FoodRescue Logo"
                     style="object-fit: contain;" />
            </div>
            <div class="logo-text">
                <span class="logo-title">FoodRescue</span>
                <span class="logo-subtitle">Smart Shopping</span>
            </div>
        </a>

        <!-- Desktop Menu -->
        <nav class="nav-menu">
            <c:forEach items="${menuItems}" var="item">
                <a href="${pageContext.request.contextPath}${item.path}"
                    class="nav-link ${currentPath == item.path ? 'active' : ''}">
                    ${item.label}
                </a>
            </c:forEach>
        </nav>

        <!-- Right Actions -->
        <div class="right-actions">
            <!-- Cart Button (only for BUYER) -->
            <c:if test="${sessionScope.role == 'BUYER' || empty sessionScope.role}">
                <a href="${pageContext.request.contextPath}/cart" class="cart-btn">
                    <svg style="width: 1.25rem; height: 1.25rem; color: #c2410c;" fill="none"
                        stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
                    </svg>
                    <span class="cart-badge">${cartCount != null ? cartCount : 0}</span>
                </a>
            </c:if>

            <!-- Profile Dropdown -->
            <c:if test="${not empty sessionScope.user}">
                <div style="position: relative;">
                    <button class="profile-btn" onclick="toggleDropdown()">
                        <div class="profile-avatar">
                            ${sessionScope.user.fullName.substring(0, 1).toUpperCase()}
                        </div>
                        <div class="profile-info">
                            <span class="profile-name">${sessionScope.user.fullName}</span>
                            <span class="profile-role">${sessionScope.user.role}</span>
                        </div>
                        <svg style="width: 1rem; height: 1rem; color: #c2410c; display: none;" class="sm-block"
                            fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                d="M19 9l-7 7-7-7" />
                        </svg>
                    </button>

                    <!-- Dropdown Menu -->
                    <div class="dropdown-menu" id="profileDropdown">
                        <div class="dropdown-header">
                            <p class="dropdown-name">${sessionScope.user.fullName}</p>
                            <p class="dropdown-email">${sessionScope.user.email}</p>
                            <span class="dropdown-role-badge">${sessionScope.user.role}</span>
                        </div>
                        <div class="dropdown-items">
                            <a href="${pageContext.request.contextPath}/settings" class="dropdown-item">
                                <svg style="width: 1.25rem; height: 1.25rem;" viewBox="0 0 24 24" fill="none"
                                    stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                        d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                                </svg>
                                Tài khoản của tôi
                            </a>
                            <a href="${pageContext.request.contextPath}/logout" class="dropdown-item logout">
                                <svg style="width: 1.25rem; height: 1.25rem;" viewBox="0 0 24 24" fill="none"
                                    stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                        d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                                </svg>
                                Đăng xuất
                            </a>
                        </div>
                    </div>
                </div>
            </c:if>

            <c:if test="${empty sessionScope.user}">
                <div style="display: none; align-items: center; gap: 0.75rem;" class="sm-flex">
                    <a href="${pageContext.request.contextPath}/login"
                        style="padding: 0.625rem 1.25rem; font-size: 0.875rem; font-weight: 600; color: #c2410c; transition: color 0.2s; text-decoration: none;">
                        Đăng nhập
                    </a>
                    <a href="${pageContext.request.contextPath}/register"
                        style="padding: 0.625rem 1.25rem; font-size: 0.875rem; font-weight: 700; background: linear-gradient(to right, #f97316, #f59e0b, #eab308); color: white; border-radius: 0.5rem; transition: all 0.2s; text-decoration: none;">
                        Đăng ký
                    </a>
                </div>
            </c:if>

            <!-- Mobile Menu Button -->
            <button class="mobile-menu-btn" onclick="toggleMobileMenu()" aria-label="Menu">
                <svg style="width: 1.5rem; height: 1.5rem; color: #c2410c;" fill="none" viewBox="0 0 24 24"
                    stroke="currentColor">
                    <path id="menuIcon" stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5"
                        d="M4 6h16M4 12h16M4 18h16" />
                </svg>
            </button>
        </div>
    </div>

    <!-- Mobile Menu -->
    <div class="mobile-menu" id="mobileMenu">
        <c:forEach items="${menuItems}" var="item">
            <a href="${pageContext.request.contextPath}${item.path}"
                class="mobile-nav-link ${currentPath == item.path ? 'active' : ''}">
                ${item.label}
            </a>
        </c:forEach>

        <c:if test="${empty sessionScope.user}">
            <div
                style="margin-top: 1rem; padding-top: 1rem; border-top: 1px solid rgba(251, 146, 60, 0.5); display: grid; grid-template-columns: repeat(2, 1fr); gap: 0.75rem; padding-left: 1rem; padding-right: 1rem;">
                <a href="${pageContext.request.contextPath}/login"
                    style="display: flex; justify-content: center; padding: 0.75rem; border: 2px solid #fdba74; color: #c2410c; border-radius: 0.5rem; font-weight: 600; transition: all 0.2s; text-decoration: none;">
                    Đăng nhập
                </a>
                <a href="${pageContext.request.contextPath}/register"
                    style="display: flex; justify-content: center; padding: 0.75rem; background: linear-gradient(to right, #f97316, #f59e0b, #eab308); color: white; border-radius: 0.5rem; font-weight: 700; transition: all 0.2s; text-decoration: none;">
                    Đăng ký
                </a>
            </div>
        </c:if>
    </div>
</header>

<script src="${pageContext.request.contextPath}/js/sidebar.js"></script>
