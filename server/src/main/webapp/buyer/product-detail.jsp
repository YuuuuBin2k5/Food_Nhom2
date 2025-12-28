<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

            <jsp:include page="header.jsp" />

            <div class="container">
                <c:choose>
                    <c:when test="${not empty product}">
                        <div
                            style="display: flex; gap: 40px; background: white; padding: 40px; border-radius: 10px; margin-top: 20px;">
                            <div style="flex: 1;">
                                <img src="${not empty product.imageUrl ? product.imageUrl : 'https://via.placeholder.com/500'}"
                                    alt="${product.name}" style="width: 100%; border-radius: 10px;">
                            </div>

                            <div style="flex: 1;">
                                <h1 style="margin-top: 0;">${product.name}</h1>
                                <p style="color: #666;">Sold by: <strong>${product.shopName}</strong></p>

                                <div style="font-size: 24px; color: #e74c3c; font-weight: bold; margin: 20px 0;">
                                    <fmt:formatNumber value="${product.salePrice}" pattern="#,##0" /> đ
                                    <c:if test="${product.originalPrice > product.salePrice}">
                                        <span
                                            style="font-size: 16px; color: #999; text-decoration: line-through; margin-left: 10px;">
                                            <fmt:formatNumber value="${product.originalPrice}" pattern="#,##0" /> đ
                                        </span>
                                    </c:if>
                                </div>

                                <p>${not empty product.description ? product.description : 'No description available.'}
                                </p>

                                <hr style="border: 0; border-top: 1px solid #eee; margin: 20px 0;">

                                <form action="${pageContext.request.contextPath}/cart" method="POST">
                                    <input type="hidden" name="action" value="add">
                                    <input type="hidden" name="productId" value="${product.productId}">

                                    <div class="form-group">
                                        <label for="quantity">Quantity:</label>
                                        <input type="number" id="quantity" name="quantity" value="1" min="1"
                                            max="${product.quantity}"
                                            style="width: 80px; padding: 8px; margin-left: 10px;">
                                        <span style="color: #666; font-size: 14px; margin-left: 10px;">
                                            (${product.quantity} available)
                                        </span>
                                    </div>

                                    <button type="submit" class="btn"
                                        style="font-size: 18px; padding: 12px 30px; margin-top: 10px;">
                                        Add to Cart
                                    </button>

                                    <!-- If using success message from redirect -->
                                    <c:if test="${not empty param.message}">
                                        <div style="margin-top: 10px; color: green;">
                                            ${param.message}
                                        </div>
                                    </c:if>
                                </form>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p>Product not found.</p>
                    </c:otherwise>
                </c:choose>
            </div>

            <jsp:include page="footer.jsp" />