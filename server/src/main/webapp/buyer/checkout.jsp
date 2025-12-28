<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

            <jsp:include page="header.jsp" />

            <div class="container">
                <h1>Checkout</h1>

                <c:choose>
                    <c:when test="${not empty sessionScope.cart}">
                        <c:set var="total" value="0" />
                        <c:forEach var="item" items="${sessionScope.cart}">
                            <c:set var="total" value="${total + item.totalPrice}" />
                        </c:forEach>

                        <div style="display: flex; gap: 40px; margin-top: 20px;">
                            <!-- Order Summary -->
                            <div
                                style="flex: 1; background: white; padding: 30px; border-radius: 10px; height: fit-content;">
                                <h3>Order Summary</h3>
                                <table class="table">
                                    <c:forEach var="item" items="${sessionScope.cart}">
                                        <tr>
                                            <td>${item.product.name} x ${item.quantity}</td>
                                            <td class="text-right">
                                                <fmt:formatNumber value="${item.totalPrice}" pattern="#,##0" /> đ
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    <tr style="font-weight: bold; border-top: 2px solid #ddd;">
                                        <td>Total</td>
                                        <td class="text-right">
                                            <fmt:formatNumber value="${total}" pattern="#,##0" /> đ
                                        </td>
                                    </tr>
                                </table>
                            </div>

                            <!-- Checkout Form -->
                            <div style="flex: 2; background: white; padding: 30px; border-radius: 10px;">
                                <h3>Shipping &amp; Payment</h3>

                                <c:if test="${not empty error}">
                                    <div class="text-danger mb-20">
                                        ${error}
                                    </div>
                                </c:if>

                                <form action="checkout" method="POST">
                                    <div class="form-group">
                                        <label for="address">Shipping Address</label>
                                        <textarea id="address" name="address" rows="3" class="form-control" required
                                            placeholder="Enter your full address"></textarea>
                                    </div>

                                    <div class="form-group">
                                        <label>Payment Method</label>
                                        <div style="margin-top: 10px;">
                                            <label style="margin-right: 20px;">
                                                <input type="radio" name="paymentMethod" value="COD" checked>
                                                Cash on Delivery (COD)
                                            </label>
                                            <label>
                                                <input type="radio" name="paymentMethod" value="BANKING">
                                                Banking / Transfer
                                            </label>
                                        </div>
                                    </div>

                                    <button type="submit" class="btn btn-block"
                                        style="margin-top: 20px; font-size: 18px;">
                                        Place Order
                                    </button>
                                </form>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p>Your cart is empty. <a href="${pageContext.request.contextPath}/">Go back to shop</a>.</p>
                    </c:otherwise>
                </c:choose>
            </div>

            <jsp:include page="footer.jsp" />