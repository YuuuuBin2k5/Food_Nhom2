<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8" %> <%@ taglib prefix="c"
uri="http://java.sun.com/jsp/jstl/core" %> <%@ taglib prefix="fmt"
uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Tổng quan - Seller Dashboard</title>
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/css/main.css"
    />
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/css/sidebar.css"
    />
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/seller/seller_css.css"
    />
  </head>

  <body class="bg-white">
    <jsp:include page="../common/sidebar.jsp">
      <jsp:param name="currentPath" value="/seller/dashboard" />
    </jsp:include>

    <main class="seller-main">
      <div class="dashboard-header">
        <h2>Chào mừng, ${sessionScope.user.fullName}</h2>
        <p>Đây là tình hình kinh doanh hôm nay của bạn.</p>
      </div>

      <!-- Error Message -->
      <c:if test="${not empty error}">
        <div class="error-message">
          <h3>⚠️ Lỗi tải dữ liệu</h3>
          <p>${error}</p>
        </div>
      </c:if>

      <!-- Debug Info -->

      <!-- Stats Grid -->
      <div class="stat-grid">
        <div class="stat-card">
          <div class="stat-number">${totalProducts}</div>
          <div class="stat-label">Tổng sản phẩm</div>
        </div>

        <div class="stat-card">
          <div class="stat-number">${activeProducts}</div>
          <div class="stat-label">Đang bán</div>
        </div>

        <div class="stat-card">
          <div class="stat-number">
            <fmt:formatNumber
              value="${totalRevenue}"
              type="currency"
              currencySymbol="₫"
              maxFractionDigits="0"
            />
          </div>
          <div class="stat-label">Tổng doanh thu</div>
        </div>
      </div>

      <!-- Warning for expiring products -->
      <c:if test="${expiringSoon > 0}">
        <div class="warning-message">
          <h3>⚠️ Cảnh báo: Có ${expiringSoon} sản phẩm sắp hết hạn!</h3>
          <p>Hãy kiểm tra và xử lý các sản phẩm sắp hết hạn.</p>
        </div>
      </c:if>

      <!-- Recent Products -->
      <div class="dashboard-card">
        <h3>Sản phẩm gần đây</h3>
        <c:choose>
          <c:when test="${not empty recentProducts}">
            <div class="overflow-x-auto mt-4">
              <table class="dashboard-table">
                <thead>
                  <tr>
                    <th>Tên sản phẩm</th>
                    <th>Giá</th>
                    <th>Số lượng</th>
                    <th>Trạng thái</th>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach var="p" items="${recentProducts}">
                    <tr>
                      <td>${p.name}</td>
                      <td>
                        <fmt:formatNumber
                          value="${p.salePrice}"
                          type="currency"
                          currencySymbol="₫"
                          maxFractionDigits="0"
                        />
                      </td>
                      <td>${p.quantity}</td>
                      <td>
                        <span
                          class="status-badge ${p.status == 'ACTIVE' ? 'status-active' : 'status-inactive'}"
                        >
                          ${p.status}
                        </span>
                      </td>
                    </tr>
                  </c:forEach>
                </tbody>
              </table>
            </div>
            <div class="text-center mt-4">
              <a
                href="${pageContext.request.contextPath}/seller/products"
                class="text-blue-600 font-medium"
              >
                Xem tất cả sản phẩm →
              </a>
            </div>
          </c:when>
          <c:otherwise>
            <div class="text-center p-8 text-gray-500">
              <p>
                Chưa có sản phẩm nào.
                <a
                  href="${pageContext.request.contextPath}/seller/products"
                  class="text-blue-600"
                  >Đăng sản phẩm đầu tiên</a
                >
              </p>
            </div>
          </c:otherwise>
        </c:choose>
      </div>

      <!-- Quick Actions -->
      <div class="dashboard-card">
        <h3>Hành động nhanh</h3>
        <div class="quick-actions mt-4">
          <a
            href="${pageContext.request.contextPath}/seller/products"
            class="quick-action-btn quick-action-products"
          >
            Quản lý sản phẩm
          </a>
          <a
            href="${pageContext.request.contextPath}/seller/orders"
            class="quick-action-btn quick-action-orders"
          >
            Xem đơn hàng
          </a>
          <a
            href="${pageContext.request.contextPath}/seller/settings"
            class="quick-action-btn quick-action-settings"
          >
            Cài đặt shop
          </a>
        </div>
      </div>
    </main>

    <jsp:include page="../common/footer.jsp" />
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
  </body>
</html>
