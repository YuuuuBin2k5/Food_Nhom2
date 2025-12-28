<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

            <jsp:include page="header.jsp" />

            <div class="container">
                <h1>My Orders</h1>

                <c:if test="${not empty message}">
                    <div class="alert success"
                        style="background: #d4edda; color: #155724; padding: 10px; margin-bottom: 20px; border-radius: 5px;">
                        ${message}
                    </div>
                </c:if>

                <c:choose>
                    <c:when test="${not empty orders}">
                        <c:forEach var="order" items="${orders}">
                            <div
                                style="background: white; border-radius: 10px; padding: 20px; margin-bottom: 20px; box-shadow: 0 2px 5px rgba(0,0,0,0.05);">
                                <div
                                    style="display: flex; justify-content: space-between; border-bottom: 1px solid #eee; padding-bottom: 15px; margin-bottom: 15px;">
                                    <div>
                                        <strong>Order #${order.orderId}</strong>
                                        <span style="color: #666; font-size: 14px; margin-left: 10px;">
                                            <fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm" />
                                        </span>
                                    </div>
                                    <div>
                                        <c:choose>
                                            <c:when test="${order.status == 'CANCELLED'}">
                                                <c:set var="statusColor" value="red" />
                                            </c:when>
                                            <c:when test="${order.status == 'DELIVERED'}">
                                                <c:set var="statusColor" value="green" />
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="statusColor" value="orange" />
                                            </c:otherwise>
                                        </c:choose>

                                        <span style="font-weight: bold; color: ${statusColor};">
                                            ${order.status}
                                        </span>
                                        <span style="margin: 0 10px;">|</span>
                                        <strong>Total:
                                            <fmt:formatNumber
                                                value="${not empty order.payment ? order.payment.amount : 0}"
                                                pattern="#,##0" /> đ
                                        </strong>
                                    </div>
                                </div>

                                <div style="margin-bottom: 15px;">
                                    <small>Ship to: ${order.shippingAddress}</small>
                                </div>

                                <table class="table" style="margin-bottom: 10px;">
                                    <c:if test="${not empty order.orderDetails}">
                                        <c:forEach var="detail" items="${order.orderDetails}">
                                            <tr>
                                                <td style="width: 60px;">
                                                    <img src="${not empty detail.product.imageUrl ? detail.product.imageUrl : 'https://via.placeholder.com/40'}"
                                                        width="40" height="40"
                                                        style="object-fit: cover; border-radius: 4px;">
                                                </td>
                                                <td>${detail.product.name}</td>
                                                <td>x${detail.quantity}</td>
                                                <td class="text-right">
                                                    <fmt:formatNumber value="${detail.priceAtPurchase}"
                                                        pattern="#,##0" /> đ
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:if>
                                </table>

                                <c:if test="${order.status == 'PENDING'}">
                                    <div class="text-right">
                                        <form action="${pageContext.request.contextPath}/order-cancel" method="POST"
                                            style="display:inline;"
                                            onsubmit="return confirm('Are you sure you want to cancel this order?');">
                                            <input type="hidden" name="orderId" value="${order.orderId}">
                                            <button type="submit" class="text-danger"
                                                style="background: white; border: 1px solid #dc3545; padding: 5px 15px; border-radius: 5px; cursor: pointer;">
                                                Cancel Order
                                            </button>
                                        </form>
                                    </div>
                                </c:if>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <p>No orders found.</p>
                    </c:otherwise>
                </c:choose>
            </div>

            <jsp:include page="footer.jsp" />