<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý đơn hàng - FreshSave Seller</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin_main.css">
    <style>
        body { background: #f8fafc; padding-top: 0; }
        .main-content { margin-left: 250px; padding: 2rem; transition: margin-left 0.3s; }
        
        /* Search & Filter Toolbar */
        .toolbar {
            display: flex; justify-content: space-between; align-items: center;
            background: white; padding: 1rem; border-radius: 12px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.05); margin-bottom: 1.5rem;
            flex-wrap: wrap; gap: 1rem;
        }
        .search-group { display: flex; align-items: center; gap: 0.5rem; flex: 1; min-width: 250px; }
        .search-input {
            width: 100%; padding: 0.6rem 1rem; border: 1px solid #e2e8f0;
            border-radius: 8px; font-size: 0.95rem; transition: all 0.2s;
        }
        .search-input:focus { outline: none; border-color: #f97316; box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.1); }

        /* Bulk Action Bar */
        .bulk-actions { display: none; gap: 10px; align-items: center; }
        .bulk-actions.active { display: flex; }
        
        /* Table enhancements */
        .table-container { background: white; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1); overflow-x: auto; }
        th, td { padding: 1rem; vertical-align: middle; white-space: nowrap; }
        
        /* Customer Info Compact */
        .customer-info div { line-height: 1.4; }
        .customer-name { font-weight: 600; color: #0f172a; }
        .customer-phone { font-size: 0.85rem; color: #64748b; }
        .customer-addr { font-size: 0.8rem; color: #94a3b8; max-width: 200px; overflow: hidden; text-overflow: ellipsis; }

        /* Print Style */
        @media print {
            .sidebar, .toolbar, .tabs, .action-column, .checkbox-column { display: none !important; }
            .main-content { margin: 0; padding: 0; }
            body { background: white; }
            .table-container { box-shadow: none; }
        }
    </style>
</head>
<body>

    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="currentPath" value="/seller/orders" />
    </jsp:include>

    <main class="main-content">
        <header style="margin-bottom: 1.5rem; display: flex; justify-content: space-between; align-items: center;">
            <div>
                <h1 style="font-size: 1.8rem; color: #1e293b; margin-bottom: 0.5rem;">📦 Quản lý đơn hàng</h1>
                <p style="color: #64748b; font-size: 0.9rem;">Xem và xử lý các đơn hàng từ khách hàng</p>
            </div>
            <button onclick="window.print()" class="btn" style="background: white; border: 1px solid #cbd5e1; color: #475569;">
                🖨️ In danh sách
            </button>
        </header>

        <nav class="tabs" style="margin-bottom: 1.5rem; border-bottom: 2px solid #e2e8f0; display: flex; gap: 1.5rem;">
            <a href="orders?status=PENDING" class="tab-link ${param.status == 'PENDING' || param.status == null ? 'active' : ''}" style="padding-bottom: 0.8rem; text-decoration: none; color: ${param.status == 'PENDING' || param.status == null ? '#f97316' : '#64748b'}; border-bottom: 2px solid ${param.status == 'PENDING' || param.status == null ? '#f97316' : 'transparent'}; font-weight: 600;">Chờ duyệt</a>
            <a href="orders?status=CONFIRMED" class="tab-link ${param.status == 'CONFIRMED' ? 'active' : ''}" style="padding-bottom: 0.8rem; text-decoration: none; color: ${param.status == 'CONFIRMED' ? '#f97316' : '#64748b'}; border-bottom: 2px solid ${param.status == 'CONFIRMED' ? '#f97316' : 'transparent'}; font-weight: 600;">Đã duyệt</a>
            <a href="orders?status=SHIPPING" class="tab-link ${param.status == 'SHIPPING' ? 'active' : ''}" style="padding-bottom: 0.8rem; text-decoration: none; color: ${param.status == 'SHIPPING' ? '#f97316' : '#64748b'}; border-bottom: 2px solid ${param.status == 'SHIPPING' ? '#f97316' : 'transparent'}; font-weight: 600;">Đang giao</a>
            <a href="orders?status=ALL" class="tab-link ${param.status == 'ALL' ? 'active' : ''}" style="padding-bottom: 0.8rem; text-decoration: none; color: ${param.status == 'ALL' ? '#f97316' : '#64748b'}; border-bottom: 2px solid ${param.status == 'ALL' ? '#f97316' : 'transparent'}; font-weight: 600;">Tất cả</a>
        </nav>

        <section class="toolbar">
            <div class="search-group">
                <span style="font-size: 1.2rem;">🔍</span>
                <input type="text" id="searchInput" class="search-input" placeholder="Tìm theo Mã đơn, Tên khách, SĐT..." onkeyup="filterTable()">
            </div>
            
            <c:if test="${param.status == 'PENDING' || param.status == null}">
                <div class="bulk-actions" id="bulkActions">
                    <span style="font-size: 0.9rem; color: #64748b;">Đã chọn <strong id="selectedCount">0</strong> đơn: </span>
                    <button onclick="processBulk('CONFIRM')" class="btn btn-primary" style="background: #10b981; border: none; padding: 0.6rem 1rem;">✅ Duyệt nhanh</button>
                </div>
            </c:if>
        </section>

        <section class="table-container">
            <c:choose>
                <c:when test="${not empty orders}">
                    <table style="width: 100%; border-collapse: collapse;">
                        <thead style="background: #f1f5f9; text-align: left; border-bottom: 2px solid #e2e8f0;">
                            <tr>
                                <th class="checkbox-column" style="width: 40px; text-align: center;">
                                    <input type="checkbox" onchange="toggleAll(this)" style="transform: scale(1.2); cursor: pointer;">
                                </th>
                                <th>Mã đơn</th>
                                <th>Khách hàng</th> <th>Sản phẩm</th>
                                <th>Tổng tiền</th>
                                <th>Trạng thái</th>
                                <th class="action-column">Hành động</th>
                            </tr>
                        </thead>
                        <tbody id="orderTableBody">
                            <c:forEach var="o" items="${orders}">
                                <tr class="order-row" style="border-bottom: 1px solid #e2e8f0;">
                                    <td class="checkbox-column" style="text-align: center;">
                                        <c:if test="${o.status == 'PENDING'}">
                                            <input type="checkbox" name="selectedOrders" value="${o.orderId}" class="order-checkbox" onchange="updateBulkState()" style="transform: scale(1.2); cursor: pointer;">
                                        </c:if>
                                    </td>
                                    
                                    <td style="color: #64748b; font-family: monospace; font-size: 1rem;">#${o.orderId}</td>
                                    
                                    <td class="customer-info">
                                        <div class="customer-name">${o.buyer.fullName}</div>
                                        <div class="customer-phone">📞 ${o.buyer.phoneNumber}</div>
                                        <div class="customer-addr">📍 ${o.shippingAddress}</div>
                                    </td>
                                    
                                    <td>
                                        <ul style="list-style: none; padding: 0; margin: 0; font-size: 0.9rem;">
                                            <c:forEach var="d" items="${o.orderDetails}">
                                                <li style="margin-bottom: 4px;">
                                                    <span style="color: #f97316; font-weight: bold;">${d.quantity}x</span> 
                                                    ${d.product.name}
                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </td>
                                    
                                    <td style="font-weight: 700; color: #0f172a;">
                                        <fmt:formatNumber value="${o.payment.amount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                    </td>

                                    <td>
                                        <span class="status-badge 
                                            ${o.status == 'PENDING' ? 'badge-orange' : 
                                              o.status == 'CONFIRMED' ? 'badge-blue' : 
                                              o.status == 'DELIVERED' ? 'badge-green' : 
                                              o.status == 'CANCELLED' ? 'badge-red' : 'badge-gray'}"
                                              style="padding: 0.35em 0.8em; border-radius: 999px; font-size: 0.8rem; font-weight: 600; 
                                              background: ${o.status == 'PENDING' ? '#fff7ed' : (o.status == 'CONFIRMED' ? '#eff6ff' : '#f3f4f6')}; 
                                              color: ${o.status == 'PENDING' ? '#c2410c' : (o.status == 'CONFIRMED' ? '#1e40af' : '#4b5563')}; border: 1px solid rgba(0,0,0,0.05);">
                                            <c:choose>
                                                <c:when test="${o.status == 'PENDING'}">Chờ duyệt</c:when>
                                                <c:when test="${o.status == 'CONFIRMED'}">Đã duyệt</c:when>
                                                <c:when test="${o.status == 'SHIPPING'}">Đang giao</c:when>
                                                <c:when test="${o.status == 'DELIVERED'}">Hoàn tất</c:when>
                                                <c:when test="${o.status == 'CANCELLED'}">Đã hủy</c:when>
                                                <c:otherwise>${o.status}</c:otherwise>
                                            </c:choose>
                                        </span>
                                    </td>

                                    <td class="action-column">
                                        <c:if test="${o.status == 'PENDING'}">
                                            <div style="display: flex; gap: 6px;">
                                                <form action="${pageContext.request.contextPath}/seller/orders" method="post">
                                                    <input type="hidden" name="action" value="CONFIRM">
                                                    <input type="hidden" name="orderId" value="${o.orderId}">
                                                    <button type="submit" style="background: #dcfce7; color: #15803d; border: 1px solid #bbf7d0; padding: 6px 10px; border-radius: 6px; font-weight: 600; cursor: pointer; font-size: 0.85rem;">✓ Duyệt</button>
                                                </form>

                                                <form action="${pageContext.request.contextPath}/seller/orders" method="post">
                                                    <input type="hidden" name="action" value="CANCEL">
                                                    <input type="hidden" name="orderId" value="${o.orderId}">
                                                    <button type="submit" onclick="return confirm('Từ chối đơn hàng này?')" style="background: #fee2e2; color: #b91c1c; border: 1px solid #fecaca; padding: 6px 10px; border-radius: 6px; font-weight: 600; cursor: pointer; font-size: 0.85rem;">✕ Hủy</button>
                                                </form>
                                            </div>
                                        </c:if>
                                        <c:if test="${o.status == 'CONFIRMED'}">
                                            <button onclick="alert('Đơn đã sẵn sàng cho Shipper')" style="background: transparent; border: 1px dashed #cbd5e1; color: #64748b; padding: 6px 10px; border-radius: 6px; font-size: 0.8rem; cursor: help;">📦 Chờ lấy hàng</button>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <div style="padding: 4rem; text-align: center;">
                        <div style="font-size: 3rem; margin-bottom: 1rem; opacity: 0.5;">📭</div>
                        <p style="color: #64748b; font-size: 1.1rem;">Chưa có đơn hàng nào trong mục này</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>
    </main>

    <script>
        // 1. Tìm kiếm Client-side (Lọc bảng)
        function filterTable() {
            const input = document.getElementById("searchInput");
            const filter = input.value.toUpperCase();
            const table = document.getElementById("orderTableBody");
            const tr = table.getElementsByTagName("tr");

            for (let i = 0; i < tr.length; i++) {
                // Lấy nội dung các cột: Mã đơn (1), Khách hàng (2), SĐT (trong cột 2)
                let tdId = tr[i].getElementsByTagName("td")[1];
                let tdCustomer = tr[i].getElementsByTagName("td")[2];
                
                if (tdId || tdCustomer) {
                    let txtValueId = tdId.textContent || tdId.innerText;
                    let txtValueCust = tdCustomer.textContent || tdCustomer.innerText;
                    
                    if (txtValueId.toUpperCase().indexOf(filter) > -1 || txtValueCust.toUpperCase().indexOf(filter) > -1) {
                        tr[i].style.display = "";
                    } else {
                        tr[i].style.display = "none";
                    }
                }       
            }
        }

        // 2. Xử lý Checkbox Chọn tất cả
        function toggleAll(source) {
            checkboxes = document.getElementsByName('selectedOrders');
            for(let i=0; i<checkboxes.length; i++) {
                checkboxes[i].checked = source.checked;
            }
            updateBulkState();
        }

        // 3. Cập nhật trạng thái thanh Bulk Action
        function updateBulkState() {
            const checkboxes = document.querySelectorAll('input[name="selectedOrders"]:checked');
            const bulkBar = document.getElementById('bulkActions');
            const countSpan = document.getElementById('selectedCount');
            
            if (checkboxes.length > 0) {
                bulkBar.classList.add('active');
                countSpan.textContent = checkboxes.length;
            } else {
                bulkBar.classList.remove('active');
            }
        }

        // 4. Xử lý Duyệt hàng loạt (Gửi nhiều request liên tiếp)
        async function processBulk(action) {
            const checkboxes = document.querySelectorAll('input[name="selectedOrders"]:checked');
            if (checkboxes.length === 0) return;

            if (!confirm('Bạn có chắc muốn ' + (action === 'CONFIRM' ? 'DUYỆT' : 'TỪ CHỐI') + ' ' + checkboxes.length + ' đơn hàng đã chọn?')) return;

            // Hiển thị loading (nếu có thư viện) hoặc đổi text nút
            const btn = document.querySelector('.bulk-actions button');
            const originalText = btn.innerText;
            btn.innerText = "⏳ Đang xử lý...";
            btn.disabled = true;

            // Loop và gửi POST request
            // Lưu ý: Đây là cách đơn giản nhất để tái sử dụng Servlet hiện tại
            // Tốt hơn là viết 1 API bulk, nhưng cách này không cần sửa Java
            const promises = Array.from(checkboxes).map(cb => {
                const formData = new URLSearchParams();
                formData.append('action', action);
                formData.append('orderId', cb.value);

                return fetch('${pageContext.request.contextPath}/seller/orders', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: formData
                });
            });

            try {
                await Promise.all(promises);
                alert('Đã xử lý thành công!');
                window.location.reload();
            } catch (error) {
                alert('Có lỗi xảy ra, vui lòng kiểm tra lại.');
                window.location.reload();
            }
        }
    </script>
</body>
</html>