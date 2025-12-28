<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>

        <style>
            .sidebar {
                position: fixed;
                top: 0;
                left: 0;
                right: 0;
                z-index: 1000;
                background: linear-gradient(to right, rgba(255, 237, 213, 0.9), rgba(254, 243, 199, 0.9), rgba(254, 249, 195, 0.9));
                backdrop-filter: blur(12px);
                border-bottom: 1px solid rgba(251, 146, 60, 0.3);
                box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
                transition: all 0.3s;
            }

            .sidebar.scrolled {
                height: 64px;
                box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            }

            .sidebar:not(.scrolled) {
                height: 96px;
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
            }

            .sidebar-container {
                max-width: 1280px;
                margin: 0 auto;
                padding: 0 1rem;
                height: 100%;
                display: flex;
                align-items: center;
                justify-content: space-between;
            }

            .logo-link {
                display: flex;
                align-items: center;
                gap: 0.75rem;
                text-decoration: none;
            }

            .logo-icon {
                background: linear-gradient(to bottom right, #fb923c, #f59e0b, #eab308);
                display: flex;
                align-items: center;
                justify-content: center;
                position: relative;
                overflow: hidden;
                box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                border-radius: 0.75rem;
                transition: all 0.3s;
            }

            .sidebar:not(.scrolled) .logo-icon {
                width: 56px;
                height: 56px;
            }

            .sidebar.scrolled .logo-icon {
                width: 40px;
                height: 40px;
            }

            .logo-icon:hover {
                box-shadow: 0 10px 15px rgba(0, 0, 0, 0.15);
            }

            .logo-text {
                transition: all 0.3s;
            }

            .sidebar.scrolled .logo-text {
                opacity: 0;
                width: 0;
                overflow: hidden;
            }

            .logo-title {
                display: block;
                font-weight: 900;
                font-size: 1.5rem;
                letter-spacing: -0.025em;
                background: linear-gradient(to right, #ea580c, #d97706, #ca8a04);
                -webkit-background-clip: text;
                -webkit-text-fill-color: transparent;
                background-clip: text;
                white-space: nowrap;
            }

            .logo-subtitle {
                display: block;
                font-size: 0.625rem;
                color: rgba(234, 88, 12, 0.7);
                margin-top: -0.25rem;
                letter-spacing: 0.1em;
                text-transform: uppercase;
                font-weight: 600;
                white-space: nowrap;
            }

            .nav-menu {
                display: none;
                align-items: center;
                gap: 0.25rem;
            }

            @media (min-width: 768px) {
                .nav-menu {
                    display: flex;
                }
            }

            .nav-link {
                padding: 0.5rem 1rem;
                color: #374151;
                text-decoration: none;
                border-radius: 0.5rem;
                transition: all 0.2s;
                position: relative;
                display: flex;
                align-items: center;
                gap: 0.5rem;
                font-weight: 500;
            }

            .nav-link:hover {
                color: #ea580c;
                background-color: rgba(254, 215, 170, 0.5);
            }

            .nav-link.active {
                color: #ea580c;
                background-color: rgba(254, 215, 170, 0.5);
            }

            .nav-link::after {
                content: '';
                position: absolute;
                bottom: 0;
                left: 0;
                width: 0;
                height: 2px;
                background: linear-gradient(to right, #f97316, #f59e0b, #eab308);
                transition: width 0.3s;
            }

            .nav-link:hover::after {
                width: 100%;
            }

            .right-actions {
                display: flex;
                align-items: center;
                gap: 0.5rem;
            }

            .cart-btn {
                position: relative;
                padding: 0.75rem;
                border-radius: 0.5rem;
                transition: background-color 0.2s;
                border: none;
                background: transparent;
                cursor: pointer;
            }

            .cart-btn:hover {
                background-color: rgba(254, 215, 170, 0.5);
            }

            .cart-badge {
                position: absolute;
                top: 0.25rem;
                right: 0.25rem;
                width: 1.25rem;
                height: 1.25rem;
                background: linear-gradient(to bottom right, #f97316, #f59e0b);
                color: white;
                font-size: 0.625rem;
                display: flex;
                align-items: center;
                justify-content: center;
                font-weight: 700;
                border-radius: 9999px;
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
            }

            .profile-btn {
                display: flex;
                align-items: center;
                gap: 0.5rem;
                padding: 0.5rem 0.75rem;
                border-radius: 0.5rem;
                transition: background-color 0.2s;
                border: none;
                background: transparent;
                cursor: pointer;
            }

            .profile-btn:hover {
                background-color: rgba(254, 215, 170, 0.5);
            }

            .profile-avatar {
                width: 36px;
                height: 36px;
                border-radius: 9999px;
                background: linear-gradient(to bottom right, #fb923c, #f59e0b, #eab308);
                display: flex;
                align-items: center;
                justify-content: center;
                color: white;
                font-weight: 700;
                box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            }

            .profile-info {
                display: none;
                flex-direction: column;
                align-items: flex-start;
            }

            @media (min-width: 640px) {
                .profile-info {
                    display: flex;
                }
            }

            .profile-name {
                font-size: 0.875rem;
                font-weight: 700;
                color: #111827;
                line-height: 1;
            }

            .profile-role {
                font-size: 0.75rem;
                color: #ea580c;
                font-weight: 600;
                text-transform: uppercase;
                margin-top: 0.125rem;
            }

            .dropdown-menu {
                position: absolute;
                right: 0;
                margin-top: 0.75rem;
                width: 256px;
                background-color: white;
                border-radius: 1rem;
                box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
                border: 1px solid rgba(251, 146, 60, 0.1);
                overflow: hidden;
                display: none;
            }

            .dropdown-menu.show {
                display: block;
            }

            .dropdown-header {
                padding: 1.25rem;
                background: linear-gradient(to bottom right, #fff7ed, #fef3c7, #fef9c3);
                border-bottom: 1px solid rgba(251, 146, 60, 0.1);
            }

            .dropdown-name {
                font-size: 1rem;
                font-weight: 700;
                color: #111827;
            }

            .dropdown-email {
                font-size: 0.875rem;
                color: #4b5563;
            }

            .dropdown-role-badge {
                display: inline-block;
                margin-top: 0.5rem;
                padding: 0.25rem 0.75rem;
                background: linear-gradient(to right, rgba(254, 215, 170, 1), rgba(253, 230, 138, 1));
                color: #c2410c;
                font-size: 0.75rem;
                font-weight: 700;
                border-radius: 9999px;
                text-transform: uppercase;
            }

            .dropdown-items {
                padding: 0.5rem 0;
            }

            .dropdown-item {
                display: flex;
                align-items: center;
                gap: 0.75rem;
                padding: 0.75rem 1.25rem;
                font-size: 0.875rem;
                font-weight: 500;
                color: #374151;
                text-decoration: none;
                transition: all 0.2s;
            }

            .dropdown-item:hover {
                background-color: #fff7ed;
                color: #ea580c;
            }

            .dropdown-item.logout {
                color: #dc2626;
            }

            .dropdown-item.logout:hover {
                background-color: #fef2f2;
            }

            .mobile-menu-btn {
                display: block;
                padding: 0.5rem;
                color: white;
                border-radius: 0.5rem;
                transition: background-color 0.2s;
                border: none;
                background: transparent;
                cursor: pointer;
            }

            @media (min-width: 768px) {
                .mobile-menu-btn {
                    display: none;
                }
            }

            .mobile-menu {
                display: block;
                padding: 1rem 0;
                border-top: 1px solid rgba(251, 146, 60, 0.5);
                background: linear-gradient(to bottom, rgba(255, 247, 237, 0.5), rgba(254, 243, 199, 0.5));
            }

            @media (min-width: 768px) {
                .mobile-menu {
                    display: none;
                }
            }

            .mobile-nav-link {
                display: flex;
                align-items: center;
                gap: 0.75rem;
                padding: 0.75rem 1rem;
                margin: 0 0.5rem;
                color: #374151;
                text-decoration: none;
                border-radius: 0.5rem;
                transition: all 0.2s;
                font-weight: 500;
            }

            .mobile-nav-link:hover,
            .mobile-nav-link.active {
                color: #ea580c;
                background-color: rgba(254, 215, 170, 0.5);
            }
        </style>

        <header class="sidebar" id="sidebar">
            <div class="sidebar-container">
                <!-- Logo -->
                <a href="${pageContext.request.contextPath}/" class="logo-link">
                    <div class="logo-icon">
                        <div
                            style="position: absolute; inset: 0; background: linear-gradient(to top, rgba(234, 88, 12, 0.2), transparent);">
                        </div>
                        <span
                            style="position: relative; z-index: 10; font-size: 1.5rem; filter: drop-shadow(0 1px 2px rgba(0,0,0,0.1));">🍊</span>
                    </div>
                    <div class="logo-text">
                        <span class="logo-title">FreshSave</span>
                        <span class="logo-subtitle">Smart Shopping</span>
                    </div>
                </a>

                <!-- Desktop Menu -->
                <nav class="nav-menu">
                    <c:forEach items="${menuItems}" var="item">
                        <a href="${pageContext.request.contextPath}${item.path}"
                            class="nav-link ${currentPath == item.path ? 'active' : ''}">
                            <span style="font-size: 1rem;">${item.icon}</span>
                            ${item.label}
                        </a>
                    </c:forEach>
                </nav>

                <!-- Right Actions -->
                <div class="right-actions">
                    <!-- Cart Button (only for BUYER) -->
                    <c:if test="${sessionScope.role == 'BUYER' || empty sessionScope.role}">
                        <a href="${pageContext.request.contextPath}/view-cart" class="cart-btn">
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
                    <button class="mobile-menu-btn" onclick="toggleMobileMenu()">
                        <svg style="width: 1.5rem; height: 1.5rem;" fill="none" viewBox="0 0 24 24"
                            stroke="currentColor">
                            <path id="menuIcon" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                d="M4 6h16M4 12h16M4 18h16" />
                        </svg>
                    </button>
                </div>
            </div>

            <!-- Mobile Menu -->
            <div class="mobile-menu" id="mobileMenu" style="display: none;">
                <c:forEach items="${menuItems}" var="item">
                    <a href="${pageContext.request.contextPath}${item.path}"
                        class="mobile-nav-link ${currentPath == item.path ? 'active' : ''}">
                        <span style="font-size: 1.125rem;">${item.icon}</span>
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

        <script>
            // Auto-hide on scroll
            let lastScrollY = 0;
            const sidebar = document.getElementById('sidebar');

            window.addEventListener('scroll', () => {
                const currentScrollY = window.scrollY;

                // Add/remove scrolled class
                if (currentScrollY < 10) {
                    sidebar.classList.remove('scrolled');
                } else {
                    sidebar.classList.add('scrolled');
                }

                lastScrollY = currentScrollY;
            }, { passive: true });

            // Toggle dropdown
            function toggleDropdown() {
                const dropdown = document.getElementById('profileDropdown');
                dropdown.classList.toggle('show');
            }

            // Close dropdown when clicking outside
            document.addEventListener('click', (e) => {
                const dropdown = document.getElementById('profileDropdown');
                const profileBtn = document.querySelector('.profile-btn');

                if (dropdown && profileBtn && !profileBtn.contains(e.target) && !dropdown.contains(e.target)) {
                    dropdown.classList.remove('show');
                }
            });

            // Toggle mobile menu
            function toggleMobileMenu() {
                const mobileMenu = document.getElementById('mobileMenu');
                const menuIcon = document.getElementById('menuIcon');

                if (mobileMenu.style.display === 'none' || mobileMenu.style.display === '') {
                    mobileMenu.style.display = 'block';
                    menuIcon.setAttribute('d', 'M6 18L18 6M6 6l12 12');
                } else {
                    mobileMenu.style.display = 'none';
                    menuIcon.setAttribute('d', 'M4 6h16M4 12h16M4 18h16');
                }
            }
        </script>