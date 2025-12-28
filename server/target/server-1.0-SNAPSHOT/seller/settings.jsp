<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cài đặt Shop - Seller</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/seller_style.css">
</head>
<body class="bg-white">

    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="currentPath" value="/seller/settings" />
    </jsp:include>

    <main style="margin-top: 96px; min-height: 80vh; padding: 2rem; max-width: 800px; margin-left: auto; margin-right: auto;">
        
        <div style="background: white; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); overflow: hidden; border: 1px solid #e2e8f0;">
            <div style="background: #f7fafc; padding: 1.5rem; border-bottom: 1px solid #e2e8f0;">
                <h2 style="margin: 0; color: #2d3748; font-size: 1.5rem;">⚙️ Thông tin cửa hàng</h2>
            </div>
            
            <div style="padding: 2rem;">
                <c:if test="${not empty message}"><div class="alert alert-success" style="background: #c6f6d5; color: #22543d; padding: 1rem; border-radius: 4px; margin-bottom: 1.5rem;">${message}</div></c:if>
                <c:if test="${not empty error}"><div class="alert alert-error" style="background: #fed7d7; color: #822727; padding: 1rem; border-radius: 4px; margin-bottom: 1.5rem;">${error}</div></c:if>

                <form action="${pageContext.request.contextPath}/seller/settings" method="post" style="display: grid; gap: 1.5rem;">
                    
                    <div>
                        <label style="display: block; margin-bottom: 0.5rem; font-weight: 500; color: #4a5568;">Tên chủ Shop</label>
                        <input type="text" name="fullName" value="${user.fullName}" required class="form-control" style="width: 100%; padding: 0.75rem; border: 1px solid #cbd5e0; border-radius: 0.375rem;">
                    </div>

                    <div>
                        <label style="display: block; margin-bottom: 0.5rem; font-weight: 500; color: #4a5568;">Tên Cửa Hàng (Shop Name)</label>
                        <input type="text" name="shopName" value="${user.shopName}" required class="form-control" style="width: 100%; padding: 0.75rem; border: 1px solid #cbd5e0; border-radius: 0.375rem;">
                    </div>

                    <div>
                        <label style="display: block; margin-bottom: 0.5rem; font-weight: 500; color: #4a5568;">Số điện thoại</label>
                        <input type="text" name="phoneNumber" value="${user.phoneNumber}" required class="form-control" style="width: 100%; padding: 0.75rem; border: 1px solid #cbd5e0; border-radius: 0.375rem;">
                    </div>

                    <div>
                        <label style="display: block; margin-bottom: 0.5rem; font-weight: 500; color: #4a5568;">Địa chỉ lấy hàng</label>
                        <input type="text" name="address" value="${user.address}" required class="form-control" style="width: 100%; padding: 0.75rem; border: 1px solid #cbd5e0; border-radius: 0.375rem;">
                    </div>
                    
                    <div>
                        <label style="display: block; margin-bottom: 0.5rem; font-weight: 500; color: #4a5568;">Giấy phép kinh doanh (Link ảnh)</label>
                        <input type="url" name="businessLicenseUrl" id="licenseUrl" value="${user.businessLicenseUrl}" class="form-control" 
                               placeholder="https://..." onchange="previewImage(this.value)"
                               style="width: 100%; padding: 0.75rem; border: 1px solid #cbd5e0; border-radius: 0.375rem;">
                        <small style="color: #718096; display: block; margin-top: 0.5rem;">* Lưu ý: Việc thay đổi giấy phép có thể khiến tài khoản phải chờ Admin duyệt lại.</small>
                        
                        <div style="margin-top: 1rem; max-width: 200px; border: 1px solid #e2e8f0; padding: 4px; border-radius: 4px;">
                            <img id="licensePreview" src="${user.businessLicenseUrl}" 
                                 onerror="this.style.display='none'" 
                                 style="width: 100%; border-radius: 4px; ${empty user.businessLicenseUrl ? 'display:none' : ''}">
                        </div>
                    </div>
                    
                    <div style="border-top: 1px solid #e2e8f0; padding-top: 1.5rem; margin-top: 0.5rem;">
                        <button type="submit" style="background: #3182ce; color: white; border: none; padding: 0.75rem 2rem; border-radius: 0.375rem; font-weight: 600; cursor: pointer; width: 100%;">Lưu thay đổi</button>
                    </div>
                </form>
            </div>
            
            <div style="background: #f7fafc; padding: 1.5rem; border-top: 1px solid #e2e8f0; display: flex; justify-content: space-between; align-items: center;">
                <span style="color: #4a5568;">Trạng thái tài khoản:</span>
                <span class="status-badge status-${user.verificationStatus.toString().toLowerCase()}" style="font-weight: bold; padding: 0.25rem 0.75rem; border-radius: 9999px;">
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
            if(url) {
                img.src = url;
                img.style.display = 'block';
            } else {
                img.style.display = 'none';
            }
        }
    </script>
</body>
</html>