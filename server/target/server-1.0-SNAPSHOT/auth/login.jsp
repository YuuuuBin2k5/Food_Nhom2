<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập - FreshSave</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth/login.css">
</head>
<body>
    <div class="auth-container">
        <!-- Left Side -->
        <div class="auth-side">
            <div>
                <h2>Our Mission</h2>
                <h1>
                    Sẻ Chia<br>
                    Hương Vị<br>
                    Yêu Thương
                </h1>
            </div>
            <div>
                <p>
                    Đừng để món ngon bị lãng phí. Hãy biến thức ăn dư thừa thành niềm vui cho cộng đồng ngay hôm nay.
                </p>
            </div>
        </div>
        
        <!-- Right Side - Form -->
        <div class="auth-form">
            <div class="form-header">
                <h1 class="form-title">Chào mừng trở lại!</h1>
                <p class="form-subtitle">
                    Đăng nhập <strong>hệ thống quản lý</strong>
                </p>
            </div>
            
            <form method="POST" action="${pageContext.request.contextPath}/login" id="loginForm">
                <div class="form-group">
                    <label class="form-label" for="email">Email</label>
                    <input 
                        type="email" 
                        id="email" 
                        name="email" 
                        class="form-input" 
                        placeholder="admin@gmail.com"
                        required
                        value="${param.email != null ? param.email : ''}"
                    >
                </div>
                
                <div class="form-group">
                    <label class="form-label" for="password">Mật khẩu</label>
                    <input 
                        type="password" 
                        id="password" 
                        name="password" 
                        class="form-input" 
                        placeholder="••••••••"
                        required
                    >
                </div>
                
                <div class="form-row">
                    <div class="checkbox-group">
                        <input type="checkbox" id="remember" name="remember">
                        <label for="remember">Ghi nhớ tôi</label>
                    </div>
                    <a href="${pageContext.request.contextPath}/forgot-password" class="forgot-link">
                        Quên mật khẩu?
                    </a>
                </div>
                
                <c:if test="${not empty error}">
                    <div class="error-message">
                        ⚠️ ${error}
                    </div>
                </c:if>
                
                <div style="display: flex; justify-content: center;">
                    <button type="submit" class="btn-submit" id="submitBtn">
                        Đăng nhập
                    </button>
                </div>
            </form>
            
            <div class="form-footer">
                Chưa có tài khoản? 
                <a href="${pageContext.request.contextPath}/register" class="register-link">
                    Tạo tài khoản mới
                </a>
            </div>
        </div>
    </div>
    
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
    <script>
        // Handle form submit
        document.getElementById('loginForm').addEventListener('submit', function() {
            const btn = document.getElementById('submitBtn');
            btn.disabled = true;
            btn.textContent = 'Đang xác thực...';
        });
    </script>
    
    <c:if test="${not empty error}">
    <script>
        window.addEventListener('DOMContentLoaded', function() {
            if (typeof Toast !== 'undefined') {
                Toast.error('${error}');
            }
        });
    </script>
    </c:if>
</body>
</html>
