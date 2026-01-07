<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <!DOCTYPE html>
        <html lang="vi">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Cài đặt Shop - Seller</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/seller_style.css">
            <link rel="stylesheet" href="${pageContext.request.contextPath}/seller/seller_css.css">
        </head>

        <body class="bg-white">

            <jsp:include page="../common/sidebar.jsp">
                <jsp:param name="currentPath" value="/seller/settings" />
            </jsp:include>

            <main class="seller-main seller-main-narrow">

                <div class="settings-container">
                    <div class="settings-header">
                        <h2>⚙️ Thông tin cửa hàng</h2>
                    </div>

                    <div class="settings-content">
                        <c:if test="${not empty message}">
                            <div class="alert-success">${message}</div>
                        </c:if>
                        <c:if test="${not empty error}">
                            <div class="alert-error">${error}</div>
                        </c:if>

                        <form action="${pageContext.request.contextPath}/seller/settings" method="post"
                            class="settings-form">

                            <div class="settings-form-group">
                                <label class="settings-label">Tên chủ Shop</label>
                                <input type="text" name="fullName" value="${user.fullName}" required
                                    class="settings-input">
                            </div>

                            <div class="settings-form-group">
                                <label class="settings-label">Tên Cửa Hàng (Shop Name)</label>
                                <input type="text" name="shopName" value="${user.shopName}" required
                                    class="settings-input">
                            </div>

                            <div class="settings-form-group">
                                <label class="settings-label">Số điện thoại</label>
                                <input type="text" name="phoneNumber" value="${user.phoneNumber}" required
                                    class="settings-input">
                            </div>

                            <div class="settings-form-group">
                                <label class="settings-label">Địa chỉ lấy hàng</label>
                                <input type="text" name="address" value="${user.address}" required
                                    class="settings-input">
                            </div>

                            <div class="settings-form-group">
                                <label class="settings-label">Giấy phép kinh doanh (Link ảnh)</label>
                                <input type="url" name="businessLicenseUrl" id="licenseUrl"
                                    value="${user.businessLicenseUrl}" class="settings-input" placeholder="https://..."
                                    onchange="previewImage(this.value)">
                                <small class="settings-note">* Lưu ý: Việc thay đổi giấy phép có thể khiến tài khoản
                                    phải chờ Admin duyệt lại.</small>

                                <div class="settings-preview">
                                    <img id="licensePreview" src="${user.businessLicenseUrl}"
                                        onerror="this.style.display='none'"
                                        style="${empty user.businessLicenseUrl ? 'display:none' : ''}">
                                </div>
                            </div>

                            <div class="settings-submit">
                                <button type="submit">Lưu thay đổi</button>
                            </div>
                        </form>
                    </div>

                    <div class="settings-footer">
                        <span class="settings-status">Trạng thái tài khoản:</span>
                        <span class="status-badge status-${user.verificationStatus.toString().toLowerCase()}">
                            ${user.verificationStatus}
                        </span>
                    </div>
                </div>
            </main>

            <jsp:include page="../common/footer.jsp" />
            <script src="${pageContext.request.contextPath}/js/main.js"></script>
            <script>
                function previewImage(url) {
                    const img = document.getElementById('licensePreview');
                    if (url) {
                        img.src = url;
                        img.style.display = 'block';
                    } else {
                        img.style.display = 'none';
                    }
                }
            </script>
        </body>

        </html>