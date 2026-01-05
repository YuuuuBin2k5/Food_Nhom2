<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặt lại mật khẩu - FreshSave</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <style>
        body {
            margin: 0;
            padding: 0;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            background-image: url('/images/backgroundLogin.png');
            background-size: cover;
            background-position: center;
        }
        
        .auth-container {
            display: flex;
            width: 100%;
            max-width: 1200px;
            height: 600px;
            overflow: hidden;
            border-radius: 1rem;
            box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
            background: rgba(255, 255, 255, 0.1);
            backdrop-filter: blur(12px);
            border: 1px solid rgba(255, 255, 255, 0.3);
        }
        
        .auth-side {
            width: 50%;
            position: relative;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            padding: 2.5rem;
            background: linear-gradient(135deg, rgba(255, 107, 107, 0.9), rgba(255, 142, 83, 0.9));
        }
        
        .auth-side h2 {
            color: white;
            font-size: 0.625rem;
            font-weight: 700;
            letter-spacing: 0.2em;
            text-transform: uppercase;
            margin-bottom: 1rem;
            opacity: 0.9;
        }
        
        .auth-side h1 {
            color: white;
            font-size: 2.25rem;
            font-weight: 900;
            line-height: 1.2;
            text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }
        
        .auth-side p {
            color: rgba(255, 255, 255, 0.9);
            font-size: 0.875rem;
            font-weight: 500;
            line-height: 1.6;
            max-width: 400px;
        }
        
        .auth-form {
            width: 50%;
            padding: 2.5rem;
            background: white;
            display: flex;
            flex-direction: column;
            justify-content: center;
        }
        
        .form-header {
            text-align: center;
            margin-bottom: 1.5rem;
        }
        
        .form-label-small {
            color: #FF8E53;
            font-size: 0.75rem;
            font-weight: 700;
            letter-spacing: 0.2em;
            text-transform: uppercase;
            margin-bottom: 0.5rem;
            opacity: 0.9;
        }
        
        .form-title {
            font-size: 2.25rem;
            font-weight: 900;
            background: linear-gradient(to bottom, #FF6B6B, #FF8E53, #FFC75F);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            margin-bottom: 0.75rem;
        }
        
        .form-subtitle {
            color: #334155;
            font-size: 1rem;
            margin-top: 0.75rem;
            margin-bottom: 1.5rem;
            opacity: 0.9;
            line-height: 1.6;
        }
        
        .form-group {
            margin-bottom: 1.5rem;
        }
        
        /* Password wrapper for toggle button */
        .password-group {
            position: relative;
        }

        .password-wrapper {
            position: relative;
            display: block;
            width: 100%;
        }

        .toggle-password {
            position: absolute;
            right: 10px;
            top: 50%;
            transform: translateY(-50%);
            background: none;
            border: none;
            cursor: pointer;
            padding: 6px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #64748b;
            transition: color 0.2s;
            z-index: 10;
            line-height: 1;
        }

        .toggle-password:hover {
            color: #FF6B6B;
        }

        .toggle-password svg {
            width: 20px;
            height: 20px;
            pointer-events: none;
            display: block;
        }
        
        .password-wrapper input.form-input {
            padding-right: 50px !important;
        }
        
        .form-label {
            display: block;
            font-weight: 600;
            margin-bottom: 0.5rem;
            color: #0f172a;
            font-size: 0.875rem;
        }
        
        .form-input {
            width: 100%;
            padding: 0.875rem 1rem;
            border: 2px solid #e2e8f0;
            border-radius: 0.75rem;
            font-size: 1rem;
            transition: all 0.2s;
            background: #f8fafc;
        }
        
        .form-input:focus {
            outline: none;
            border-color: #FF6B6B;
            background: white;
            box-shadow: 0 0 0 3px rgba(255, 107, 107, 0.1);
        }
        
        .error-message {
            border-radius: 0.75rem;
            background: rgba(255, 107, 107, 0.1);
            padding: 0.75rem;
            font-size: 0.875rem;
            color: #FF6B6B;
            border: 1px solid rgba(255, 107, 107, 0.3);
            display: flex;
            align-items: center;
            gap: 0.5rem;
            margin-bottom: 1rem;
        }
        
        .success-message {
            border-radius: 0.75rem;
            background: rgba(16, 185, 129, 0.1);
            padding: 0.75rem;
            font-size: 0.875rem;
            color: #065f46;
            border: 1px solid rgba(16, 185, 129, 0.3);
            display: flex;
            align-items: center;
            gap: 0.5rem;
            margin-bottom: 1rem;
        }
        
        .btn-submit {
            width: 100%;
            max-width: 384px;
            margin: 0 auto;
            padding: 0.875rem 2rem;
            font-weight: 700;
            font-size: 1rem;
            background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 50%, #FFC75F 100%);
            color: white;
            border: none;
            border-radius: 0.75rem;
            cursor: pointer;
            transition: all 0.3s;
            box-shadow: 0 4px 6px rgba(255, 107, 107, 0.3);
        }
        
        .btn-submit:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 15px rgba(255, 107, 107, 0.4);
        }
        
        .btn-submit:active {
            transform: translateY(0);
        }
        
        .btn-submit:disabled {
            opacity: 0.6;
            cursor: not-allowed;
        }
        
        .form-footer {
            margin-top: 1.5rem;
            text-align: center;
            font-size: 0.875rem;
            color: #334155;
        }
        
        .login-link {
            font-weight: 700;
            color: #10B981;
            text-decoration: none;
            cursor: pointer;
        }
        
        .login-link:hover {
            text-decoration: underline;
        }
        
        @media (max-width: 768px) {
            .auth-container {
                flex-direction: column;
                height: auto;
            }
            
            .auth-side {
                width: 100%;
                min-height: 200px;
            }
            
            .auth-form {
                width: 100%;
            }
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
