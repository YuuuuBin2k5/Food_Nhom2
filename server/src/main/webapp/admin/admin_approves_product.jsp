<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <!DOCTYPE html>
            <html>

            <head>
                <meta charset="UTF-8">
                <title>Duyệt Product - Admin</title>
                <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin_main.css">
                <link rel="stylesheet" type="text/css"
                    href="${pageContext.request.contextPath}/css/admin_approves_product.css">
            </head>

            <body>

                <!-- Include Sidebar -->
                <jsp:include page="../common/sidebar.jsp">
                    <jsp:param name="currentPath" value="/admin/approveProduct" />
                </jsp:include>

                <div class="main-content">

                    <div id="lightbox" class="lightbox" onclick="closeLightbox()">
                        <button type="button" class="lightbox-close" aria-label="Đóng">&times;</button>
                        <img id="lightbox-img" src="" alt="Ảnh sản phẩm">
                    </div>

                    <c:if test="${not empty message}">
                        <div class="alert alert-success">${message}</div>
                    </c:if>
                    <c:if test="${not empty error}">
                        <div class="alert alert-error">${error}</div>
                    </c:if>

                    <section class="approval-section">
                        <c:choose>
                            <c:when test="${not empty product}">
                                <div class="approval-card">
                                    <div class="approval-info">
                                        <h2 class="product-title">${product.name}</h2>
                                        <p class="shop-name">Shop: ${product.seller.shopName}</p>
                                        <div class="info-list">
                                            <div class="info-item"><span class="label">Mô tả</span><span
                                                    class="value">${product.description}</span></div>
                                            <div class="info-row">
                                                <div class="info-item"><span class="label">Giá gốc</span><span
                                                        class="value price-original">
                                                        <fmt:formatNumber value="${product.originalPrice}"
                                                            type="currency" currencySymbol="₫" maxFractionDigits="0" />
                                                    </span></div>
                                                <div class="info-item"><span class="label">Giá bán</span><span
                                                        class="value price-sale">
                                                        <fmt:formatNumber value="${product.salePrice}" type="currency"
                                                            currencySymbol="₫" maxFractionDigits="0" />
                                                    </span></div>
                                            </div>
                                            <div class="info-row">
                                                <div class="info-item"><span class="label">Số lượng</span><span
                                                        class="value">${product.quantity}</span></div>
                                                <div class="info-item"><span class="label">Chủ shop</span><span
                                                        class="value">${product.seller.fullName}</span></div>
                                            </div>
                                            <div class="info-row">
                                                <div class="info-item"><span class="label">Ngày SX</span><span
                                                        class="value">
                                                        <fmt:formatDate value="${product.manufactureDate}"
                                                            pattern="dd/MM/yyyy" />
                                                    </span></div>
                                                <div class="info-item"><span class="label">Hạn SD</span><span
                                                        class="value">
                                                        <fmt:formatDate value="${product.expirationDate}"
                                                            pattern="dd/MM/yyyy" />
                                                    </span></div>
                                            </div>
                                            <div class="info-row">
                                                <div class="info-item"><span class="label">Danh mục</span><span
                                                        class="value">${product.category.emoji}
                                                        ${product.category.displayName}</span></div>
                                                <div class="info-item"><span class="label">Ngày đăng</span><span
                                                        class="value">
                                                        <fmt:formatDate value="${product.createdDate}"
                                                            pattern="dd/MM/yyyy HH:mm" />
                                                    </span></div>
                                            </div>
                                            <div class="info-row">
                                                <div class="info-item"><span class="label">Ngày duyệt</span><span
                                                        class="value">
                                                        <fmt:formatDate value="${product.approvedDate}"
                                                            pattern="dd/MM/yyyy HH:mm" />
                                                    </span></div>
                                                <div class="info-item"><span class="label">Trạng thái</span><span
                                                        class="value status-${product.status.toString().toLowerCase()}">${product.status}</span>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="approval-actions">
                                            <form action="${pageContext.request.contextPath}/admin/approveProduct"
                                                method="post">
                                                <input type="hidden" name="action" value="approve">
                                                <input type="hidden" name="productId" value="${product.productId}">
                                                <input type="hidden" name="productName" value="${product.name}">
                                                <button type="submit" class="btn btn-approve">Duyệt</button>
                                            </form>
                                            <form action="${pageContext.request.contextPath}/admin/approveProduct"
                                                method="post">
                                                <input type="hidden" name="action" value="reject">
                                                <input type="hidden" name="productId" value="${product.productId}">
                                                <input type="hidden" name="productName" value="${product.name}">
                                                <button type="submit" class="btn btn-reject">Từ chối</button>
                                            </form>
                                        </div>
                                    </div>
                                    <div class="approval-image">
                                        <c:choose>
                                            <c:when test="${not empty product.imageUrl}">
                                                <img src="${product.imageUrl}" alt="Ảnh sản phẩm"
                                                    onclick="openLightbox(this.src)"
                                                    onerror="this.src='https://via.placeholder.com/400x300?text=No+Image'">
                                                <span class="click-hint">Click để phóng to</span>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="no-image">Chưa có ảnh sản phẩm</div>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="empty-state-small"><span>📦</span> Chọn một sản phẩm từ danh sách bên dưới
                                    để xem chi tiết</div>
                            </c:otherwise>
                        </c:choose>
                    </section>


                    <section class="product-list-section">
                        <div class="tabs">
                            <a href="${pageContext.request.contextPath}/admin/approveProduct?tab=pending"
                                class="tab-btn ${currentTab == 'pending' ? 'active' : ''}">Chờ duyệt
                                (${pendingCount})</a>
                            <a href="${pageContext.request.contextPath}/admin/approveProduct?tab=rejected"
                                class="tab-btn ${currentTab == 'rejected' ? 'active' : ''}">Từ chối
                                (${rejectedCount})</a>
                            <a href="${pageContext.request.contextPath}/admin/approveProduct?tab=active"
                                class="tab-btn ${currentTab == 'active' ? 'active' : ''}">Đã duyệt (${activeCount})</a>
                            <a href="${pageContext.request.contextPath}/admin/approveProduct?tab=all"
                                class="tab-btn ${currentTab == 'all' ? 'active' : ''}">Tất cả (${allCount})</a>
                        </div>

                        <c:choose>
                            <c:when test="${not empty productList}">
                                <div class="table-wrapper">
                                    <table class="product-table">
                                        <thead>
                                            <tr>
                                                <th>Sản phẩm</th>
                                                <th>Shop</th>
                                                <th>Chủ shop</th>
                                                <th>Giá bán</th>
                                                <th>Ngày đăng</th>
                                                <th>Ngày duyệt</th>
                                                <th>Trạng thái</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="p" items="${productList}">
                                                <tr onclick="window.location='${pageContext.request.contextPath}/admin/approveProduct?action=detail&productId=${p.productId}&tab=${currentTab}&page=${currentPage}'"
                                                    class="clickable-row">
                                                    <td>${p.name}</td>
                                                    <td>${p.seller.shopName}</td>
                                                    <td>${p.seller.fullName}</td>
                                                    <td>
                                                        <fmt:formatNumber value="${p.salePrice}" type="currency"
                                                            currencySymbol="₫" maxFractionDigits="0" />
                                                    </td>
                                                    <td>
                                                        <fmt:formatDate value="${p.createdDate}" pattern="dd/MM/yyyy" />
                                                    </td>
                                                    <td>
                                                        <fmt:formatDate value="${p.approvedDate}"
                                                            pattern="dd/MM/yyyy" />
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${p.status.name() == 'ACTIVE'}"><span
                                                                    class="status-badge status-active">Đã duyệt</span>
                                                            </c:when>
                                                            <c:when test="${p.status.name() == 'PENDING_APPROVAL'}">
                                                                <span class="status-badge status-pending_approval">Chờ
                                                                    duyệt</span></c:when>
                                                            <c:when test="${p.status.name() == 'REJECTED'}"><span
                                                                    class="status-badge status-rejected">Từ chối</span>
                                                            </c:when>
                                                            <c:otherwise><span class="status-badge">${p.status}</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                                <c:if test="${totalPages > 1}">
                                    <div class="pagination">
                                        <c:if test="${currentPage > 1}"><a
                                                href="${pageContext.request.contextPath}/admin/approveProduct?tab=${currentTab}&page=${currentPage - 1}"
                                                class="page-btn">&laquo; Trước</a></c:if>
                                        <c:forEach begin="1" end="${totalPages}" var="i">
                                            <c:choose>
                                                <c:when test="${i == currentPage}"><span
                                                        class="page-btn active">${i}</span></c:when>
                                                <c:otherwise><a
                                                        href="${pageContext.request.contextPath}/admin/approveProduct?tab=${currentTab}&page=${i}"
                                                        class="page-btn">${i}</a></c:otherwise>
                                            </c:choose>
                                        </c:forEach>
                                        <c:if test="${currentPage < totalPages}"><a
                                                href="${pageContext.request.contextPath}/admin/approveProduct?tab=${currentTab}&page=${currentPage + 1}"
                                                class="page-btn">Sau &raquo;</a></c:if>
                                    </div>
                                </c:if>
                            </c:when>
                            <c:otherwise>
                                <p class="no-data">Không có sản phẩm nào trong danh sách này</p>
                            </c:otherwise>
                        </c:choose>
                    </section>

                </div>

                <script>
                    let zoomLevel = 1;
                    function openLightbox(src) { zoomLevel = 1; document.getElementById('lightbox-img').src = src; document.getElementById('lightbox-img').style.transform = 'scale(1)'; document.getElementById('lightbox').style.display = 'flex'; }
                    function closeLightbox() { document.getElementById('lightbox').style.display = 'none'; zoomLevel = 1; }
                    document.getElementById('lightbox-img').addEventListener('click', function (e) { e.stopPropagation(); });
                    document.getElementById('lightbox').addEventListener('wheel', function (e) { e.preventDefault(); zoomLevel = e.deltaY < 0 ? Math.min(4, zoomLevel + 0.2) : Math.max(0.5, zoomLevel - 0.2); document.getElementById('lightbox-img').style.transform = 'scale(' + zoomLevel + ')'; });
                    document.addEventListener('keydown', function (e) { if (e.key === 'Escape') closeLightbox(); });
                </script>
            </body>

            </html>