<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

            <jsp:include page="header.jsp" />

            <div class="container">
                <h1>Shopping Cart</h1>

                <c:choose>
                    <c:when test="${not empty sessionScope.cart}">
                        <c:set var="total" value="0" />

                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Product</th>
                                    <th>Price</th>
                                    <th>Quantity</th>
                                    <th>Total</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="item" items="${sessionScope.cart}">
                                    <c:set var="itemTotal" value="${item.totalPrice}" />
                                    <c:set var="total" value="${total + itemTotal}" />

                                    <tr>
                                        <td>
                                            <div style="display: flex; align-items: center; gap: 10px;">
                                                <img src="${not empty item.product.imageUrl ? item.product.imageUrl : 'https://via.placeholder.com/50'}"
                                                    width="50" height="50"
                                                    style="object-fit: cover; border-radius: 5px;">
                                                <div>
                                                    <a href="product?id=${item.product.productId}"
                                                        style="font-weight: bold;">
                                                        ${item.product.name}
                                                    </a>
                                                    <br>
                                                    <small>Shop: ${item.product.shopName}</small>
                                                </div>
                                            </div>
                                        </td>
                                        <td>
                                            <fmt:formatNumber value="${item.product.salePrice}" pattern="#,##0" /> đ
                                        </td>
                                        <td>
                                            <form action="cart" method="POST" style="display: inline;">
                                                <input type="hidden" name="action" value="update">
                                                <input type="hidden" name="productId" value="${item.product.productId}">
                                                <input type="number" name="quantity" value="${item.quantity}" min="1"
                                                    style="width: 60px; padding: 5px;" onchange="this.form.submit()">
                                            </form>
                                        </td>
                                        <td>
                                            <fmt:formatNumber value="${itemTotal}" pattern="#,##0" /> đ
                                        </td>
                                        <td>
                                            <form action="cart" method="POST" style="display: inline;">
                                                <input type="hidden" name="action" value="remove">
                                                <input type="hidden" name="productId" value="${item.product.productId}">
                                                <button type="submit" class="text-danger"
                                                    style="background:none; border:none; cursor:pointer; text-decoration: underline;">
                                                    Remove
                                                </button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>

                        <div
                            style="display: flex; justify-content: space-between; align-items: center; background: white; padding: 20px; border-radius: 10px; margin-top: 20px;">
                            <div style="font-size: 24px;">
                                Total: <strong>
                                    <fmt:formatNumber value="${total}" pattern="#,##0" /> đ
                                </strong>
                            </div>
                            <div>
                                <a href="${pageContext.request.contextPath}/" class="btn btn-secondary">Continue
                                    Shopping</a>
                                <a href="${pageContext.request.contextPath}/checkout" class="btn"
                                    style="margin-left: 10px;">Proceed to Checkout</a>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center" style="padding: 50px;">
                            <h2>Your cart is empty</h2>
                            <p>Looks like you haven't added anything to your cart yet.</p>
                            <a href="${pageContext.request.contextPath}/" class="btn mt-20">Start Shopping</a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <jsp:include page="footer.jsp" />