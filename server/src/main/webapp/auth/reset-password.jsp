<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặt lại mật khẩu - FoodRescue</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth/auth-common.css">
    <style>
        body {
            background-image: url('${pageContext.request.contextPath}/images/backgroundLogin.png');
        }
        .auth-container {
            height: 600px;
        }
        .form-group {
            margin-bottom: 1.5rem;
        }
        .form-footer {
            margin-top: 1.5rem;
        }
    </style>
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
                <span class="form-label-small">Reset Password</span>
                <h1 class="form-title">Đặt lại mật khẩu</h1>
                <p class="form-subtitle">
                    Nhập mật khẩu mới của bạn.
                </p>
            </div>
            
            <form method="POST" action="${pageContext.request.contextPath}/reset-password" id="resetForm">
                <c:if test="${not empty error}">
                    <div class="error-message">
                        ⚠️ <span>${error}</span>
                    </div>
                </c:if>
                
                <c:if test="${not empty success}">
                    <div class="success-message">
                        ✅ <span>${success}</span>
                    </div>
                </c:if>
                
                <input type="hidden" name="token" value="${param.token}">
                
                <div class="form-group password-group">
                    <label class="form-label" for="password">Mật khẩu mới</label>
                    <div class="password-wrapper">
                        <input 
                            type="password" 
                            id="password" 
                            name="password" 
                            class="form-input" 
                            placeholder="••••••••"
                            minlength="6"
                            required
                        >
                        <button type="button" class="toggle-password" onclick="togglePassword('password', 'eyeIcon1')">
                            <svg id="eyeIcon1" xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" viewBox="0 0 16 16">
                                <path d="M10.5 8a2.5 2.5 0 1 1-5 0 2.5 2.5 0 0 1 5 0z"/>
                                <path d="M0 8s3-5.5 8-5.5S16 8 16 8s-3 5.5-8 5.5S0 8 0 8zm8 3.5a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7z"/>
                            </svg>
                        </button>
                    </div>
                </div>
                
                <div class="form-group password-group">
                    <label class="form-label" for="confirm">Xác nhận mật khẩu</label>
                    <div class="password-wrapper">
                        <input 
                            type="password" 
                            id="confirm" 
                            name="confirm" 
                            class="form-input" 
                            placeholder="••••••••"
                            minlength="6"
                            required
                        >
                        <button type="button" class="toggle-password" onclick="togglePassword('confirm', 'eyeIcon2')">
                            <svg id="eyeIcon2" xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" viewBox="0 0 16 16">
                                <path d="M10.5 8a2.5 2.5 0 1 1-5 0 2.5 2.5 0 0 1 5 0z"/>
                                <path d="M0 8s3-5.5 8-5.5S16 8 16 8s-3 5.5-8 5.5S0 8 0 8zm8 3.5a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7z"/>
                            </svg>
                        </button>
                    </div>
                </div>
                
                <div style="display: flex; justify-content: center;">
                    <button type="submit" class="btn-submit" id="submitBtn">
                        Đặt lại mật khẩu
                    </button>
                </div>
            </form>
            
            <div class="form-footer">
                Quay lại đăng nhập? 
                <a href="${pageContext.request.contextPath}/login" class="login-link">
                    Đăng nhập
                </a>
            </div>
        </div>
    </div>
    
    <script>
        // Toggle password visibility
        function togglePassword(inputId, iconId) {
            const passwordInput = document.getElementById(inputId);
            const eyeIcon = document.getElementById(iconId);
            
            if (passwordInput.type === 'password') {
                passwordInput.type = 'text';
                // Icon mắt gạch (eye-slash)
                eyeIcon.innerHTML = '<path d="M13.359 11.238C15.06 9.72 16 8 16 8s-3-5.5-8-5.5a7.028 7.028 0 0 0-2.79.588l.77.771A5.944 5.944 0 0 1 8 3.5c2.12 0 3.879 1.168 5.168 2.457A13.134 13.134 0 0 1 14.828 8c-.058.087-.122.183-.195.288-.335.48-.83 1.12-1.465 1.755-.165.165-.337.328-.517.486l.708.709z"/><path d="M11.297 9.176a3.5 3.5 0 0 0-4.474-4.474l.823.823a2.5 2.5 0 0 1 2.829 2.829l.822.822zm-2.943 1.299.822.822a3.5 3.5 0 0 1-4.474-4.474l.823.823a2.5 2.5 0 0 0 2.829 2.829z"/><path d="M3.35 5.47c-.18.16-.353.322-.518.487A13.134 13.134 0 0 0 1.172 8l.195.288c.335.48.83 1.12 1.465 1.755C4.121 11.332 5.881 12.5 8 12.5c.716 0 1.39-.133 2.02-.36l.77.772A7.029 7.029 0 0 1 8 13.5C3 13.5 0 8 0 8s.939-1.721 2.641-3.238l.708.709zm10.296 8.884-12-12 .708-.708 12 12-.708.708z"/>';
            } else {
                passwordInput.type = 'password';
                // Icon mắt mở (eye)
                eyeIcon.innerHTML = '<path d="M10.5 8a2.5 2.5 0 1 1-5 0 2.5 2.5 0 0 1 5 0z"/><path d="M0 8s3-5.5 8-5.5S16 8 16 8s-3 5.5-8 5.5S0 8 0 8zm8 3.5a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7z"/>';
            }
        }
        
        document.getElementById('resetForm').addEventListener('submit', function() {
            const btn = document.getElementById('submitBtn');
            btn.disabled = true;
            btn.textContent = 'Đang xử lý...';
        });
    </script>
    
    <c:if test="${not empty success}">
        <script>
            setTimeout(function() {
                window.location.href = '${pageContext.request.contextPath}/login';
            }, 2000);
        </script>
    </c:if>
</body>
</html>
