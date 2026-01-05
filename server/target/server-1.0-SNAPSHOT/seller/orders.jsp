<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Đơn hàng - Seller</title>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
                <style>
                    .filter-bar {
                        border-bottom: 2px solid #e2e8f0;
                        display: flex;
                        flex-wrap: wrap;
                        gap: 0.5rem;
                        overflow-x: auto;
                        padding-bottom: 0.5rem;
                    }

                    .filter-bar a {
                        padding: 0.75rem 1.5rem;
                        display: inline-block;
                        text-decoration: none;
                        font-weight: 600;
                        white-space: nowrap;
                        border-radius: 8px 8px 0 0;
                        transition: all 0.2s ease;
                        min-width: fit-content;
                    }

                    .filter-bar a:hover {
                        background-color: #f7fafc;
                    }

                    @media (max-width: 768px) {
                        .filter-bar {
                            gap: 0.25rem;
                        }

                        .filter-bar a {
                            padding: 0.5rem 1rem;
                            font-size: 0.875rem;
                        }
                    }

                    .action-buttons {
                        display: flex;
                        gap: 0.5rem;
                        flex-wrap: wrap;
                    }

                    .action-buttons button {
                        font-size: 0.875rem;
                        padding: 0.5rem 1rem;
                        border-radius: 4px;
                        border: none;
                        cursor: pointer;
                        font-weight: 600;
                        transition: all 0.2s ease;
                    }

                    .action-buttons button:hover {
                        transform: translateY(-1px);
                        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                    }

                    @media (max-width: 640px) {
                        .action-buttons {
                            flex-direction: column;
                        }

                        .action-buttons button {
                            width: 100%;
                        }
                    }
                </style>
            </head>

            <body class="bg-white">

                <jsp:include page="../common/sidebar.jsp">
                    <jsp:param name="currentPath" value="/seller/orders" />
                </jsp:include>

                <main
                    style="margin-top: 96px; min-height: 80vh; padding: 2rem; max-width: 1400px; margin-left: auto; margin-right: auto;">

                    <div style="margin-bottom: 2rem;">
                        <h2 style="font-size: 1.5rem; color: #1a202c; margin-bottom: 1rem;">📦 Quản lý đơn hàng</h2>

                        <div class="filter-bar"
                            style="border-bottom: 2px solid #e2e8f0; display: flex; flex-wrap: wrap; gap: 0.5rem;">
                            <a href="?status=PENDING"
                                style="padding: 0.75rem 1.5rem; display: inline-block; text-decoration: none; color: ${param.status == 'PENDING' || empty param.status ? '#ea580c' : '#718096'}; border-bottom: 2px solid ${param.status == 'PENDING' || empty param.status ? '#ea580c' : 'transparent'}; font-weight: 600; white-space: nowrap;">
                                🕐 Chờ duyệt
                            </a>
                            <a href="?status=CONFIRMED"
                                style="padding: 0.75rem 1.5rem; display: inline-block; text-decoration: none; color: ${param.status == 'CONFIRMED' ? '#ea580c' : '#718096'}; border-bottom: 2px solid ${param.status == 'CONFIRMED' ? '#ea580c' : 'transparent'}; font-weight: 600; white-space: nowrap;">
                                ✅ Đã duyệt
                            </a>
                            <a href="?status=SHIPPING"
                                style="padding: 0.75rem 1.5rem; display: inline-block; text-decoration: none; color: ${param.status == 'SHIPPING' ? '#ea580c' : '#718096'}; border-bottom: 2px solid ${param.status == 'SHIPPING' ? '#ea580c' : 'transparent'}; font-weight: 600; white-space: nowrap;">
                                🚚 Đang giao
                            </a>
                            <a href="?status=DELIVERED"
                                style="padding: 0.75rem 1.5rem; display: inline-block; text-decoration: none; color: ${param.status == 'DELIVERED' ? '#ea580c' : '#718096'}; border-bottom: 2px solid ${param.status == 'DELIVERED' ? '#ea580c' : 'transparent'}; font-weight: 600; white-space: nowrap;">
                                📦 Đã giao
                            </a>
                            <a href="?status=CANCELLED"
                                style="padding: 0.75rem 1.5rem; display: inline-block; text-decoration: none; color: ${param.status == 'CANCELLED' ? '#ea580c' : '#718096'}; border-bottom: 2px solid ${param.status == 'CANCELLED' ? '#ea580c' : 'transparent'}; font-weight: 600; white-space: nowrap;">
                                ❌ Đã hủy
                            </a>
                            <a href="?status=ALL"
                                style="padding: 0.75rem 1.5rem; display: inline-block; text-decoration: none; color: ${param.status == 'ALL' ? '#ea580c' : '#718096'}; border-bottom: 2px solid ${param.status == 'ALL' ? '#ea580c' : 'transparent'}; font-weight: 600; white-space: nowrap;">
                                📋 Tất cả
                            </a>
                        </div>
                    </div>

                    <c:choose>
                        <c:when test="${not empty orders}">
                            <table
                                style="width: 100%; border-collapse: collapse; background: white; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
                                <thead style="background: #f7fafc;">
                                    <tr>
                                        <th style="padding: 1rem; text-align: left;">Mã đơn</th>
                                        <th style="padding: 1rem; text-align: left;">Khách hàng</th>
                                        <th style="padding: 1rem; text-align: left;">Địa chỉ</th>
                                        <th style="padding: 1rem; text-align: left;">Tổng tiền</th>
                                        <th style="padding: 1rem; text-align: left;">Ngày đặt</th>
                                        <th style="padding: 1rem; text-align: left;">Trạng thái</th>
                                        <th style="padding: 1rem; text-align: left;">Hành động</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="o" items="${orders}">
                                        <tr style="border-top: 1px solid #e2e8f0;">
                                            <td style="padding: 1rem; font-weight: bold;">#${o.orderId}</td>
                                            <td style="padding: 1rem;">${o.buyer.fullName}</td>
                                            <td style="padding: 1rem;">${o.shippingAddress}</td>
                                            <td style="padding: 1rem; font-weight: 600; color: #2d3748;">
                                                <fmt:formatNumber value="${o.payment.amount}" type="currency"
                                                    currencySymbol="₫" maxFractionDigits="0" />
                                            </td>
                                            <td style="padding: 1rem; color: #718096;">
                                                <fmt:formatDate value="${o.orderDate}" pattern="dd/MM/yyyy HH:mm" />
                                            </td>
                                            <td style="padding: 1rem;">
                                                <c:choose>
                                                    <c:when test="${o.status == 'PENDING'}">
                                                        <span
                                                            style="background: #fef3c7; color: #92400e; padding: 0.25rem 0.75rem; border-radius: 9999px; font-size: 0.875rem; font-weight: 600;">🕐
                                                            Chờ duyệt</span>
                                                    </c:when>
                                                    <c:when test="${o.status == 'CONFIRMED'}">
                                                        <span
                                                            style="background: #d1fae5; color: #065f46; padding: 0.25rem 0.75rem; border-radius: 9999px; font-size: 0.875rem; font-weight: 600;">✅
                                                            Đã duyệt</span>
                                                    </c:when>
                                                    <c:when test="${o.status == 'SHIPPING'}">
                                                        <span
                                                            style="background: #dbeafe; color: #1e40af; padding: 0.25rem 0.75rem; border-radius: 9999px; font-size: 0.875rem; font-weight: 600;">🚚
                                                            Đang giao</span>
                                                    </c:when>
                                                    <c:when test="${o.status == 'DELIVERED'}">
                                                        <span
                                                            style="background: #dcfce7; color: #166534; padding: 0.25rem 0.75rem; border-radius: 9999px; font-size: 0.875rem; font-weight: 600;">📦
                                                            Đã giao</span>
                                                    </c:when>
                                                    <c:when test="${o.status == 'CANCELLED'}">
                                                        <span
                                                            style="background: #fee2e2; color: #991b1b; padding: 0.25rem 0.75rem; border-radius: 9999px; font-size: 0.875rem; font-weight: 600;">❌
                                                            Đã hủy</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span
                                                            style="background: #f3f4f6; color: #374151; padding: 0.25rem 0.75rem; border-radius: 9999px; font-size: 0.875rem; font-weight: 600;">${o.status}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td style="padding: 1rem;">
                                                <c:choose>
                                                    <c:when test="${o.status == 'PENDING'}">
                                                        <div style="display: flex; gap: 0.5rem; flex-wrap: wrap;">
                                                            <form
                                                                action="${pageContext.request.contextPath}/seller/orders"
                                                                method="post" style="display: inline;">
                                                                <input type="hidden" name="action" value="CONFIRM">
                                                                <input type="hidden" name="orderId"
                                                                    value="${o.orderId}">
                                                                <button type="submit"
                                                                    style="background: #38a169; color: white; border: none; padding: 0.5rem 1rem; border-radius: 4px; cursor: pointer; font-weight: 600; font-size: 0.875rem;">✅
                                                                    Duyệt đơn</button>
                                                            </form>
                                                            <form
                                                                action="${pageContext.request.contextPath}/seller/orders"
                                                                method="post" style="display: inline;">
                                                                <input type="hidden" name="action" value="CANCEL">
                                                                <input type="hidden" name="orderId"
                                                                    value="${o.orderId}">
                                                                <button type="submit"
                                                                    style="background: #dc2626; color: white; border: none; padding: 0.5rem 1rem; border-radius: 4px; cursor: pointer; font-weight: 600; font-size: 0.875rem;"
                                                                    onclick="return confirm('Bạn có chắc muốn hủy đơn hàng này?')">❌
                                                                    Hủy đơn</button>
                                                            </form>
                                                        </div>
                                                    </c:when>
                                                    <c:when test="${o.status == 'CONFIRMED'}">
                                                        <span
                                                            style="color: #16a34a; font-size: 0.875rem; font-weight: 600;">✅
                                                            Đã duyệt - Chờ shipper nhận đơn</span>
                                                    </c:when>
                                                    <c:when test="${o.status == 'SHIPPING'}">
                                                        <span
                                                            style="color: #2563eb; font-size: 0.875rem; font-weight: 600;">🚚
                                                            Đang được giao bởi shipper</span>
                                                    </c:when>
                                                    <c:when test="${o.status == 'DELIVERED'}">
                                                        <span
                                                            style="color: #16a34a; font-size: 0.875rem; font-weight: 600;">✅
                                                            Đã giao thành công</span>
                                                    </c:when>
                                                    <c:when test="${o.status == 'CANCELLED'}">
                                                        <span
                                                            style="color: #dc2626; font-size: 0.875rem; font-weight: 600;">❌
                                                            Đơn hàng đã bị hủy</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span style="color: #718096; font-size: 0.875rem;">Không có hành
                                                            động</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:when>
                        <c:otherwise>
                            <div
                                style="text-align: center; padding: 3rem; background: #f7fafc; border-radius: 8px; color: #718096;">
                                <p>Chưa có đơn hàng nào trong mục này.</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </main>

                <jsp:include page="../common/footer.jsp" />
                <script src="${pageContext.request.contextPath}/js/main.js"></script>
            </body>

            </html>