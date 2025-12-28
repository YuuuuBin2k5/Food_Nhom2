<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html>

        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Food Nhom 2 - Buyer</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        </head>

        <body>
            <header>
                <div class="container">
                    <a href="${pageContext.request.contextPath}/" class="logo">FoodHub</a>

                    <nav class="nav-links">
                        <a href="${pageContext.request.contextPath}/">Trang chủ</a>

                        <c:choose>
                            <c:when test="${not empty sessionScope.userId}">
                                <a href="${pageContext.request.contextPath}/my-orders">Đơn hàng của tôi</a>
                                <a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/login">Đăng nhập</a>
                            </c:otherwise>
                        </c:choose>

                        <a href="${pageContext.request.contextPath}/cart" class="cart-icon">
                            🛒 Giỏ hàng
                            <span class="cart-count">${empty sessionScope.cartSize ? 0 : sessionScope.cartSize}</span>
                        </a>
                    </nav>
                </div>
            </header>

            <!-- Main Content Start -->
            <div style="min-height: 80vh;">