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
                
                <div class="form-group password-group">
                    <label class="form-label" for="password">Mật khẩu</label>
                    <div class="password-wrapper">
                        <input 
                            type="password" 
                            id="password" 
                            name="password" 
                            class="form-input" 
                            placeholder="••••••••"
                            required
                        >
                        <button type="button" class="toggle-password" id="togglePassword">
                            <svg id="eyeIcon" xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" viewBox="0 0 16 16">
                                <path d="M10.5 8a2.5 2.5 0 1 1-5 0 2.5 2.5 0 0 1 5 0z"/>
                                <path d="M0 8s3-5.5 8-5.5S16 8 16 8s-3 5.5-8 5.5S0 8 0 8zm8 3.5a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7z"/>
                            </svg>
                        </button>
                    </div>
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
        // Toggle password visibility
        document.getElementById('togglePassword').addEventListener('click', function() {
            const passwordInput = document.getElementById('password');
            const eyeIcon = document.getElementById('eyeIcon');
            
            if (passwordInput.type === 'password') {
                passwordInput.type = 'text';
                // Icon mắt gạch (eye-slash)
                eyeIcon.innerHTML = '<path d="M13.359 11.238C15.06 9.72 16 8 16 8s-3-5.5-8-5.5a7.028 7.028 0 0 0-2.79.588l.77.771A5.944 5.944 0 0 1 8 3.5c2.12 0 3.879 1.168 5.168 2.457A13.134 13.134 0 0 1 14.828 8c-.058.087-.122.183-.195.288-.335.48-.83 1.12-1.465 1.755-.165.165-.337.328-.517.486l.708.709z"/><path d="M11.297 9.176a3.5 3.5 0 0 0-4.474-4.474l.823.823a2.5 2.5 0 0 1 2.829 2.829l.822.822zm-2.943 1.299.822.822a3.5 3.5 0 0 1-4.474-4.474l.823.823a2.5 2.5 0 0 0 2.829 2.829z"/><path d="M3.35 5.47c-.18.16-.353.322-.518.487A13.134 13.134 0 0 0 1.172 8l.195.288c.335.48.83 1.12 1.465 1.755C4.121 11.332 5.881 12.5 8 12.5c.716 0 1.39-.133 2.02-.36l.77.772A7.029 7.029 0 0 1 8 13.5C3 13.5 0 8 0 8s.939-1.721 2.641-3.238l.708.709zm10.296 8.884-12-12 .708-.708 12 12-.708.708z"/>';
            } else {
                passwordInput.type = 'password';
                // Icon mắt mở (eye)
                eyeIcon.innerHTML = '<path d="M10.5 8a2.5 2.5 0 1 1-5 0 2.5 2.5 0 0 1 5 0z"/><path d="M0 8s3-5.5 8-5.5S16 8 16 8s-3 5.5-8 5.5S0 8 0 8zm8 3.5a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7z"/>';
            }
        });
        
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
