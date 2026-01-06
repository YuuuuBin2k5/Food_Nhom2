<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quên mật khẩu - FreshSave</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth/auth-common.css">
    <style>
        body {
            background-image: url('${pageContext.request.contextPath}/images/backgroundLogin.png');
        }
        .auth-container {
            height: 600px;
        }
        .form-header {
            margin-bottom: 2.5rem;
        }
        .form-subtitle {
            margin-top: 0.75rem;
            margin-bottom: 1.5rem;
            opacity: 0.9;
            line-height: 1.6;
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
                <span class="form-label-small">Forgot Password</span>
                <h1 class="form-title">Quên mật khẩu?</h1>
                <p class="form-subtitle">
                    Nhập email của bạn để nhận hướng dẫn đặt lại mật khẩu.
                </p>
            </div>
            
            <form method="POST" action="${pageContext.request.contextPath}/forgot-password" id="forgotForm">
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
                
                <div class="form-group">
                    <label class="form-label" for="email">Email</label>
                    <input 
                        type="email" 
                        id="email" 
                        name="email" 
                        class="form-input" 
                        placeholder="email@domain.com"
                        value="${email != null ? email : ''}"
                        required
                    >
                </div>
                
                <div style="display: flex; justify-content: center;">
                    <button type="submit" class="btn-submit" id="submitBtn">
                        Gửi email đặt lại
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
        document.getElementById('forgotForm').addEventListener('submit', function() {
            const btn = document.getElementById('submitBtn');
            btn.disabled = true;
            btn.textContent = 'Đang gửi...';
        });
    </script>
</body>
</html>
