<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Lịch sử đơn hàng - FoodRescue</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sidebar.css">
    
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">

    <style>
        :root {
            /* Warm Theme - Lấy từ shipper-modern.css */
            --primary: #f97316;
            --primary-dark: #ea580c;
            --primary-light: #fdba74;
            --secondary: #fbbf24;
            
            /* Gradient Background đặc trưng */
            --gradient-warm: linear-gradient(to right, #fff7ed, #fef3c7, #fef9c3);
            
            --text-dark: #1c1917;
            --text-gray: #78716c;
            --bg-card: rgba(255, 255, 255, 0.95);
            
            /* Border màu cam nhạt cho tiệp màu */
            --border-warm: rgba(251, 146, 60, 0.2); 
            
            --card-shadow: 0 4px 6px -1px rgba(249, 115, 22, 0.1), 0 2px 4px -1px rgba(249, 115, 22, 0.06);
        }

        body {
            font-family: 'Inter', sans-serif;
            background: var(--gradient-warm); /* Áp dụng background shipper */
            padding-top: 80px;
            color: var(--text-dark);
            min-height: 100vh;
        }

        .container {
            max-width: 850px;
            margin: 0 auto;
            padding: 2rem 1rem;
        }

        .page-title {
            font-size: 1.75rem;
            font-weight: 800; /* Đậm hơn giống style shipper */
            color: #ea580c; /* Màu cam đậm */
            margin-bottom: 2rem;
            display: flex;
            align-items: center;
            gap: 0.75rem;
            text-shadow: 0 1px 2px rgba(0,0,0,0.05);
        }

        /* --- Order Card Styles --- */
        .order-card {
            background: var(--bg-card);
            border-radius: 20px; /* Bo góc tròn hơn giống shipper */
            box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
            margin-bottom: 2rem;
            overflow: hidden;
            border: 1px solid var(--border-warm); /* Viền cam nhạt */
            transition: all 0.3s ease;
        }

        .order-card:hover {
            transform: translateY(-4px);
            box-shadow: 0 12px 24px rgba(251, 146, 60, 0.15); /* Shadow cam khi hover */
            border-color: var(--primary-light);
            background: #ffffff;
        }

        /* --- Header --- */
        .order-header {
            padding: 1.25rem 1.5rem;
            border-bottom: 1px solid var(--border-warm);
            display: flex;
            justify-content: space-between;
            align-items: center;
            background: linear-gradient(to right, rgba(255,255,255,0.8), rgba(255,247,237,0.5));
        }

        .order-meta {
            display: flex;
            flex-direction: column;
        }

        .order-id {
            font-weight: 800;
            color: var(--text-dark);
            font-size: 1.1rem;
        }

        .order-date {
            font-size: 0.85rem;
            color: var(--text-gray);
            margin-top: 0.25rem;
            display: flex;
            align-items: center;
            gap: 4px;
            font-weight: 500;
        }

        /* --- Badges (Updated Colors) --- */
        .status-badge {
            padding: 0.5rem 1rem;
            border-radius: 9999px;
            font-size: 0.8rem;
            font-weight: 700;
            letter-spacing: 0.025em;
            text-transform: uppercase;
        }

        .badge-pending { background: #fff7ed; color: #c2410c; border: 1px solid #ffedd5; }
        .badge-confirmed { background: #eff6ff; color: #1d4ed8; border: 1px solid #dbeafe; }
        .badge-shipping { background: #f0f9ff; color: #0284c7; border: 1px solid #e0f2fe; }
        .badge-delivered { background: #ecfdf5; color: #047857; border: 1px solid #d1fae5; }
        .badge-cancelled { background: #fef2f2; color: #b91c1c; border: 1px solid #fee2e2; }

        /* --- Items List --- */
        .order-body {
            padding: 0 1.5rem;
        }

        .product-item {
            display: flex;
            align-items: center;
            padding: 1.25rem 0;
            border-bottom: 1px dashed var(--border-warm);
        }
        .product-item:last-child { border-bottom: none; }

        .product-img {
            width: 70px;
            height: 70px;
            border-radius: 12px;
            object-fit: cover;
            border: 1px solid var(--border-warm);
            margin-right: 1rem;
        }

        .product-info { flex: 1; }
        .product-name {
            font-weight: 700;
            color: var(--text-dark);
            font-size: 1rem;
            margin-bottom: 0.25rem;
        }
        .product-qty {
            font-size: 0.85rem;
            color: var(--text-gray);
            font-weight: 600;
        }

        .product-price {
            font-weight: 700;
            color: var(--text-dark);
        }

        /* --- Footer --- */
        .order-footer {
            background: #fffbf5; /* Warm background for footer */
            padding: 1.25rem 1.5rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-top: 1px solid var(--border-warm);
        }

        .total-wrapper {
            display: flex;
            flex-direction: column;
            align-items: flex-end;
        }
        
        .total-label { font-size: 0.75rem; color: var(--text-gray); text-transform: uppercase; font-weight: 600; }
        .total-price {
            font-size: 1.25rem;
            font-weight: 800;
            color: var(--primary);
        }

        /* --- Buttons --- */
        .btn {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            padding: 0.7rem 1.4rem;
            border-radius: 12px;
            font-weight: 700;
            cursor: pointer;
            transition: all 0.2s;
            text-decoration: none;
            gap: 0.5rem;
            font-size: 0.9rem;
        }

        .btn-cancel {
            background: white;
            border: 1px solid #ef4444;
            color: #ef4444;
        }
        .btn-cancel:hover { background: #fef2f2; }

        .btn-reorder {
            background: linear-gradient(135deg, #f97316 0%, #fb923c 100%); /* Gradient button */
            border: none;
            color: white;
            box-shadow: 0 4px 6px -1px rgba(249, 115, 22, 0.3);
        }
        .btn-reorder:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 12px -1px rgba(249, 115, 22, 0.4);
        }

        /* Empty State */
        .empty-state {
            text-align: center;
            padding: 4rem 1rem;
            background: white;
            border-radius: 24px;
            box-shadow: 0 10px 25px -5px rgba(249, 115, 22, 0.1);
            border: 1px solid var(--border-warm);
        }
        .empty-icon-circle {
            width: 80px; height: 80px;
            background: #fff7ed;
            border-radius: 50%;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            font-size: 2.5rem;
            margin-bottom: 1rem;
            border: 2px solid #ffedd5;
        }
    </style>
</head>
<body>

    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="currentPath" value="/orders" />
    </jsp:include>

    <main class="container">
        <div class="page-title">
            <span style="font-size: 2rem;">🛍️</span> Lịch sử mua hàng
        </div>

        <c:if test="${not empty message}">
            <div style="background: #ecfdf5; color: #047857; padding: 1rem; border-radius: 12px; margin-bottom: 1.5rem; border: 1px solid #a7f3d0; display: flex; align-items: center; gap: 0.5rem; font-weight: 500;">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg>
                ${message}
            </div>
        </c:if>
        
        <c:if test="${not empty param.error}">
            <div style="background: #fef2f2; color: #b91c1c; padding: 1rem; border-radius: 12px; margin-bottom: 1.5rem; border: 1px solid #fecaca; display: flex; align-items: center; gap: 0.5rem; font-weight: 500;">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line></svg>
                ${param.error}
            </div>
        </c:if>

        <c:choose>
            <c:when test="${not empty orders}">
                <c:forEach var="o" items="${orders}">
                    <article class="order-card">
                        <div class="order-header">
                            <div class="order-meta">
                                <span class="order-id">Đơn hàng #${o.orderId}</span>
                                <span class="order-date">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>
                                    <fmt:formatDate value="${o.orderDate}" pattern="HH:mm - dd/MM/yyyy"/>
                                </span>
                            </div>
                            
                            <c:choose>
                                <c:when test="${o.status == 'PENDING'}">
                                    <span class="status-badge badge-pending">Chờ xác nhận</span>
                                </c:when>
                                <c:when test="${o.status == 'CONFIRMED'}">
                                    <span class="status-badge badge-confirmed">Đã xác nhận</span>
                                </c:when>
                                <c:when test="${o.status == 'SHIPPING'}">
                                    <span class="status-badge badge-shipping">Đang giao</span>
                                </c:when>
                                <c:when test="${o.status == 'DELIVERED'}">
                                    <span class="status-badge badge-delivered">Hoàn thành</span>
                                </c:when>
                                <c:when test="${o.status == 'CANCELLED'}">
                                    <span class="status-badge badge-cancelled">Đã hủy</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="status-badge" style="background: #f1f5f9; color: #64748b;">${o.status}</span>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <div class="order-body">
                            <c:forEach var="d" items="${o.orderDetails}">
                                <div class="product-item">
                                    <img src="${d.product.imageUrl != null ? d.product.imageUrl : 'https://placehold.co/70x70'}" 
                                         alt="${d.product.name}" class="product-img">
                                    
                                    <div class="product-info">
                                        <div class="product-name">${d.product.name}</div>
                                        <div class="product-qty">Số lượng: x${d.quantity}</div>
                                    </div>
                                    
                                    <div class="product-price">
                                        <fmt:formatNumber value="${d.product.salePrice * d.quantity}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>

                        <div class="order-footer">
                            <div class="actions">
                                <c:if test="${o.status == 'PENDING'}">
                                    <form action="${pageContext.request.contextPath}/order-cancel" method="post" style="display:inline;">
                                        <input type="hidden" name="orderId" value="${o.orderId}">
                                        <button type="submit" class="btn btn-cancel" onclick="return confirm('Bạn có chắc chắn muốn hủy đơn hàng này?')">
                                            Hủy đơn
                                        </button>
                                    </form>
                                </c:if>
                                
                                <c:if test="${o.status == 'DELIVERED' || o.status == 'CANCELLED'}">
                                    <a href="${pageContext.request.contextPath}/products" class="btn btn-reorder">
                                        Mua lại
                                    </a>
                                </c:if>
                            </div>

                            <div class="total-wrapper">
                                <span class="total-label">Tổng thanh toán</span>
                                <span class="total-price">
                                    <fmt:formatNumber value="${o.payment.amount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                </span>
                            </div>
                        </div>
                    </article>
                </c:forEach>
            </c:when>
            
            <c:otherwise>
                <div class="empty-state">
                    <div class="empty-icon-circle">🍽️</div>
                    <h3 style="color: #1e293b; font-size: 1.5rem; margin-bottom: 0.5rem; font-weight: 800;">Chưa có đơn hàng nào</h3>
                    <p style="color: #64748b; margin-bottom: 2rem;">Đừng để bụng đói! Hãy khám phá các món ngon ngay.</p>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-reorder">
                        Khám phá ngay
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </main>
</body>
</html>