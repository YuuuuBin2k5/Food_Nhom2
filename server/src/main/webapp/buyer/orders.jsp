<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Lịch sử đơn hàng - FoodRescue</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <style>
        body { padding-top: 96px; background: #f8fafc; }
        .container { max-width: 900px; margin: 0 auto; padding: 2rem 1rem; }
        
        .order-card {
            background: white;
            border-radius: 1rem;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
            margin-bottom: 1.5rem;
            border: 1px solid #e2e8f0;
            overflow: hidden;
            transition: transform 0.2s;
        }
        .order-card:hover { box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1); }

        .order-header {
            background: #f8fafc;
            padding: 1rem 1.5rem;
            border-bottom: 1px solid #e2e8f0;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .order-items { list-style: none; padding: 0; margin: 0; }
        
        .order-item {
            display: flex;
            justify-content: space-between;
            padding: 1rem 1.5rem;
            border-bottom: 1px dashed #f1f5f9;
        }
        .order-item:last-child { border-bottom: none; }

        .order-footer {
            padding: 1rem 1.5rem;
            background: white;
            border-top: 1px solid #e2e8f0;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .price-highlight { color: #ef4444; font-weight: 700; font-size: 1.1rem; }
        
        .btn-cancel {
            background: white;
            border: 1px solid #ef4444;
            color: #ef4444;
            padding: 0.5rem 1rem;
            border-radius: 0.5rem;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.2s;
        }
        .btn-cancel:hover { background: #fee2e2; }
        
        .btn-reorder {
            text-decoration: none; 
            color: #f97316; 
            font-weight: 600;
            display: flex;
            align-items: center;
            gap: 5px;
        }
        .btn-reorder:hover { text-decoration: underline; }
    </style>
</head>
<body>

    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="currentPath" value="/orders" />
    </jsp:include>

    <main class="container">
        <h1 style="font-size: 1.8rem; color: #1e293b; margin-bottom: 1.5rem;">🛍️ Lịch sử mua hàng</h1>

        <c:if test="${not empty message}">
            <div style="background: #d1fae5; color: #065f46; padding: 1rem; border-radius: 0.75rem; margin-bottom: 1.5rem;">
                ✅ ${message}
            </div>
        </c:if>
        
        <c:if test="${not empty param.error}">
            <div style="background: #fee2e2; color: #991b1b; padding: 1rem; border-radius: 0.75rem; margin-bottom: 1.5rem;">
                ⚠️ ${param.error}
            </div>
        </c:if>

        <c:choose>
            <c:when test="${not empty orders}">
                <c:forEach var="o" items="${orders}">
                    <article class="order-card">
                        <header class="order-header">
                            <div>
                                <strong style="color: #0f172a;">Đơn hàng #${o.orderId}</strong>
                                <span style="color: #64748b; font-size: 0.9rem; margin-left: 0.5rem;">
                                    • <fmt:formatDate value="${o.orderDate}" pattern="dd/MM/yyyy HH:mm"/>
                                </span>
                            </div>
                            <span class="status-badge 
                                ${o.status == 'PENDING' ? 'badge-orange' : 
                                  o.status == 'CONFIRMED' ? 'badge-blue' : 
                                  o.status == 'DELIVERED' ? 'badge-green' : 
                                  o.status == 'CANCELLED' ? 'badge-red' : 'badge-gray'}">
                                <c:choose>
                                    <c:when test="${o.status == 'PENDING'}">Chờ xác nhận</c:when>
                                    <c:when test="${o.status == 'CONFIRMED'}">Đã xác nhận</c:when>
                                    <c:when test="${o.status == 'SHIPPING'}">Đang giao hàng</c:when>
                                    <c:when test="${o.status == 'DELIVERED'}">Giao thành công</c:when>
                                    <c:when test="${o.status == 'CANCELLED'}">Đã hủy</c:when>
                                    <c:otherwise>${o.status}</c:otherwise>
                                </c:choose>
                            </span>
                        </header>

                        <section>
                            <ul class="order-items">
                                <c:forEach var="d" items="${o.orderDetails}">
                                    <li class="order-item">
                                        <div style="display: flex; gap: 1rem; align-items: center;">
                                            <img src="${d.product.imageUrl != null ? d.product.imageUrl : 'https://placehold.co/50x50'}" 
                                                 alt="" style="width: 50px; height: 50px; border-radius: 8px; object-fit: cover;">
                                            <div>
                                                <div style="font-weight: 500; color: #334155;">${d.product.name}</div>
                                                <div style="font-size: 0.85rem; color: #64748b;">x${d.quantity}</div>
                                            </div>
                                        </div>
                                        <div style="font-weight: 600; color: #334155;">
                                            <fmt:formatNumber value="${d.product.salePrice * d.quantity}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                        </div>
                                    </li>
                                </c:forEach>
                            </ul>
                        </section>

                        <footer class="order-footer">
                            <div>
                                Tổng tiền: <span class="price-highlight"><fmt:formatNumber value="${o.payment.amount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></span>
                            </div>
                            
                            <c:if test="${o.status == 'PENDING'}">
                                <form action="${pageContext.request.contextPath}/order-cancel" method="post">
                                    <input type="hidden" name="orderId" value="${o.orderId}">
                                    <button type="submit" class="btn-cancel" onclick="return confirm('Bạn có chắc chắn muốn hủy đơn hàng này?')">
                                        Hủy đơn hàng
                                    </button>
                                </form>
                            </c:if>
                            
                            <c:if test="${o.status == 'DELIVERED' || o.status == 'CANCELLED'}">
                                <a href="${pageContext.request.contextPath}/products" class="btn-reorder">
                                    🛒 Mua lại
                                </a>
                            </c:if>
                        </footer>
                    </article>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div style="text-align: center; padding: 4rem 0;">
                    <div style="font-size: 4rem; margin-bottom: 1rem;">📦</div>
                    <h3 style="color: #1e293b;">Bạn chưa có đơn hàng nào</h3>
                    <p style="color: #64748b; margin-bottom: 2rem;">Hãy khám phá các món ngon đang chờ được giải cứu!</p>
                    <a href="${pageContext.request.contextPath}/products" style="background: #f97316; color: white; padding: 0.75rem 1.5rem; border-radius: 0.75rem; text-decoration: none; font-weight: 600; display: inline-block;">
                        Mua sắm ngay
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </main>
</body>
</html>