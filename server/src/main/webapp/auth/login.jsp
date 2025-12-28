<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập - FreshSave</title>
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
            background-image: url('${pageContext.request.contextPath}/images/backgroundLogin.png');
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
            margin-bottom: 2.5rem;
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
        }
        
        .form-subtitle strong {
            color: #0f172a;
            font-weight: 700;
        }
        
        .form-group {
            margin-bottom: 1.5rem;
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
        
        .form-row {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 1.5rem;
        }
        
        .checkbox-group {
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
        
        .checkbox-group input[type="checkbox"] {
            width: 1rem;
            height: 1rem;
            border-radius: 0.25rem;
            border: 1px solid #cbd5e1;
            cursor: pointer;
        }
        
        .checkbox-group label {
            font-size: 0.875rem;
            color: #334155;
            font-weight: 500;
            cursor: pointer;
            user-select: none;
        }
        
        .forgot-link {
            font-size: 0.875rem;
            font-weight: 700;
            color: #FF6B6B;
            text-decoration: none;
            transition: color 0.2s;
        }
        
        .forgot-link:hover {
            color: #FF8E53;
            text-decoration: underline;
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
            margin-bottom: 1.5rem;
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
        
        .register-link {
            font-weight: 700;
            color: #10B981;
            text-decoration: none;
            cursor: pointer;
        }
        
        .register-link:hover {
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
