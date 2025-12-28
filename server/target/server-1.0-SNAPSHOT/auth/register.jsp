<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng ký - FreshSave</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <style>
        body {
            margin: 0;
            padding: 0;
            min-height: 100vh;
            max-height: 100vh;
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
            max-width: 1100px;
            min-height: 590px;
            max-height: 100vh;
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
            justify-content: flex-start;
            overflow-y: auto;
            overflow: hidden;
        }
        
        .form-header {
            text-align: center;
            margin-bottom: 1rem;
        }
        
        .form-title {
            font-size: 2.25rem;
            font-weight: 900;
            background: linear-gradient(to bottom, #FF6B6B, #FF8E53, #FFC75F);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            margin-bottom: 0.5rem;
        }
        
        .form-subtitle {
            color: #334155;
            font-size: 1rem;
            margin-top: 0.5rem;
        }
        
        .form-subtitle strong {
            color: #0f172a;
            font-weight: 700;
        }
        
        .role-tabs {
            display: flex;
            gap: 0.5rem;
            background: #FFF9F0;
            padding: 0.5rem;
            border-radius: 9999px;
            margin-bottom: 1.5rem;
            border: 1px solid rgba(231, 218, 206, 0.4);
        }
        
        .role-tab {
            flex: 1;
            padding: 0.5rem 1rem;
            border: none;
            background: transparent;
            color: #334155;
            font-weight: 700;
            font-size: 0.875rem;
            border-radius: 9999px;
            cursor: pointer;
            transition: all 0.2s;
        }
        
        .role-tab.active {
            background: #FF6B6B;
            color: white;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }
        
        .form-group {
            margin-bottom: 0.5rem;
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
            font-weight: 500;
            margin-bottom: 0.3rem;
            color: #0f172a;
            font-size: 0.875rem;
        }
        
        .form-input {
            width: 100%;
            padding: 0.875rem 1rem;
            border: 2px solid #e2e8f0;
            border-radius: 0.75rem;
            font-size: 0.8rem;
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
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 1rem;
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
            margin-top: 1rem;
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
            
            .form-row {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
    <div class="auth-container">
        <!-- Left Side -->
        <div class="auth-side">
            <div>
                <h2>Join the Movement</h2>
                <h1>
                    Bạn đang tham gia<br>
                    Hành Trình<br>
                    Giải Cứu Thức Ăn
                </h1>
            </div>
            <div>
                <p>
                    Cùng nhau chuyển hóa phần dư thành cơ hội cho người cần. Bắt đầu bằng việc tạo một tài khoản và chia sẻ yêu thương.
                </p>
            </div>
        </div>
        
        <!-- Right Side - Form -->
        <div class="auth-form">
            <div class="form-header">
                <h1 class="form-title">Tạo tài khoản</h1>
                <p class="form-subtitle">
                    Tham gia cộng đồng <strong>Food Rescue</strong> ngay hôm nay
                </p>
            </div>
            
            <!-- Role Selection -->
            <div class="role-tabs">
                <button type="button" class="role-tab active" data-role="BUYER" onclick="selectRole('BUYER', this)">
                    Khách hàng
                </button>
                <button type="button" class="role-tab" data-role="SELLER" onclick="selectRole('SELLER', this)">
                    Người bán
                </button>
                <button type="button" class="role-tab" data-role="SHIPPER" onclick="selectRole('SHIPPER', this)">
                    Shipper
                </button>
            </div>
            
            <form method="POST" action="${pageContext.request.contextPath}/register" onsubmit="return handleSubmit(event)">
                <input type="hidden" name="role" id="roleInput" value="BUYER">
                
                <!-- Shop Name (only for Seller) -->
                <div class="form-group" id="shopNameGroup" style="display: none;">
                    <label class="form-label" for="shopName">Tên Cửa Hàng *</label>
                    <input 
                        type="text" 
                        id="shopName" 
                        name="shopName" 
                        class="form-input" 
                        placeholder="Ví dụ: Cơm Tấm Sài Gòn"
                    >
                </div>
                
                <div class="form-group">
                    <label class="form-label" for="fullName">Họ và tên *</label>
                    <input 
                        type="text" 
                        id="fullName" 
                        name="fullName" 
                        class="form-input" 
                        placeholder="Nguyễn Văn A"
                        required
                    >
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label" for="phone">Số điện thoại *</label>
                        <input 
                            type="tel" 
                            id="phone" 
                            name="phone" 
                            class="form-input" 
                            placeholder="0901234567"
                            pattern="0[0-9]{9}"
                            value="${phone}"
                            required
                        >
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="email">Email *</label>
                        <input 
                            type="email" 
                            id="email" 
                            name="email" 
                            class="form-input" 
                            placeholder="email@domain.com"
                            value="${email}"
                            required
                        >
                    </div>
                </div>
                
                <!-- OTP Verification -->
                <div class="form-group">
                    <label class="form-label" for="otp">
                        📧 Mã xác thực OTP *
                        <small style="color: #64748b; font-weight: 400;">(Gửi qua email)</small>
                    </label>
                    <div style="display: flex; gap: 0.5rem;">
                        <input 
                            type="text" 
                            id="otp" 
                            name="otp" 
                            class="form-input" 
                            placeholder="Nhập mã 6 số"
                            maxlength="6"
                            pattern="[0-9]{6}"
                            style="flex: 1;"
                            required
                        >
                        <button 
                            type="button" 
                            id="sendOtpBtn" 
                            onclick="sendOtp()"
                            style="padding: 0.875rem 1.5rem; background: #10B981; color: white; border: none; border-radius: 0.75rem; font-weight: 600; cursor: pointer; white-space: nowrap; transition: all 0.2s;"
                            onmouseover="this.style.background='#059669'"
                            onmouseout="this.style.background='#10B981'"
                        >
                            Gửi mã
                        </button>
                    </div>
                    <small id="otpTimer" style="color: #64748b; font-size: 0.75rem; margin-top: 0.25rem; display: none;">
                        ⏱️ Mã OTP sẽ hết hạn sau <span id="countdown">600</span>s
                    </small>
                    <small style="color: #64748b; font-size: 0.75rem; margin-top: 0.25rem; display: block;">
                        💡 Kiểm tra hộp thư spam nếu không thấy email
                    </small>
                </div>
                
                <div class="form-row">
                    <div class="form-group password-group">
                        <label class="form-label" for="password">Mật khẩu *</label>
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
                        <label class="form-label" for="confirmPassword">Xác nhận mật khẩu *</label>
                        <div class="password-wrapper">
                            <input 
                                type="password" 
                                id="confirmPassword" 
                                name="confirmPassword" 
                                class="form-input" 
                                placeholder="••••••••"
                                minlength="6"
                                required
                            >
                            <button type="button" class="toggle-password" onclick="togglePassword('confirmPassword', 'eyeIcon2')">
                                <svg id="eyeIcon2" xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" viewBox="0 0 16 16">
                                    <path d="M10.5 8a2.5 2.5 0 1 1-5 0 2.5 2.5 0 0 1 5 0z"/>
                                    <path d="M0 8s3-5.5 8-5.5S16 8 16 8s-3 5.5-8 5.5S0 8 0 8zm8 3.5a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7z"/>
                                </svg>
                            </button>
                        </div>
                    </div>
                </div>
                
                <c:if test="${not empty error}">
                    <div class="error-message">
                        ⚠️ <c:out value="${error}" escapeXml="true" />
                    </div>
                </c:if>
                
                <div id="clientError" class="error-message" style="display: none;">
                    ⚠️ <span id="clientErrorText"></span>
                </div>
                
                <div style="display: flex; justify-content: center;">
                    <button type="submit" class="btn-submit" id="submitBtn">
                        Đăng Ký
                    </button>
                </div>
            </form>
            
            <div class="form-footer">
                Đã có tài khoản? 
                <a href="${pageContext.request.contextPath}/login" class="login-link">
                    Đăng nhập ngay
                </a>
            </div>
        </div>
    </div>
    
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
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
        
        let selectedRole = 'BUYER';
        let otpSent = false;
        let otpVerified = false;
        let countdownInterval = null;
        
        function selectRole(role, button) {
            selectedRole = role;
            document.getElementById('roleInput').value = role;
            
            // Update active tab
            document.querySelectorAll('.role-tab').forEach(function(tab) {
                tab.classList.remove('active');
            });
            button.classList.add('active');
            
            // Show/hide shop name field
            const shopNameGroup = document.getElementById('shopNameGroup');
            const shopNameInput = document.getElementById('shopName');
            if (role === 'SELLER') {
                shopNameGroup.style.display = 'block';
                shopNameInput.required = true;
            } else {
                shopNameGroup.style.display = 'none';
                shopNameInput.required = false;
                shopNameInput.value = '';
            }
        }
        
        async function sendOtp() {
            const emailInput = document.getElementById('email');
            const email = emailInput.value.trim();
            const sendOtpBtn = document.getElementById('sendOtpBtn');
            const clientError = document.getElementById('clientError');
            const clientErrorText = document.getElementById('clientErrorText');
            
            // Validate email
            const emailRegex = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
            if (!email || !emailRegex.test(email)) {
                clientErrorText.textContent = 'Vui lòng nhập email hợp lệ';
                clientError.style.display = 'flex';
                emailInput.focus();
                return;
            }
            
            // Hide error
            clientError.style.display = 'none';
            
            // Disable button
            sendOtpBtn.disabled = true;
            sendOtpBtn.textContent = 'Đang gửi...';
            
            try {
                const response = await fetch('${pageContext.request.contextPath}/api/otp/send', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ email: email })
                });
                
                const data = await response.json();
                
                if (data.success) {
                    otpSent = true;
                    alert('✅ ' + data.message + '\n\n💡 Kiểm tra hộp thư spam nếu không thấy email');
                    
                    // Start countdown
                    startCountdown();
                    
                    // Change button text
                    sendOtpBtn.textContent = 'Gửi lại';
                    
                    // Focus OTP input
                    document.getElementById('otp').focus();
                } else {
                    clientErrorText.textContent = data.message;
                    clientError.style.display = 'flex';
                }
            } catch (error) {
                clientErrorText.textContent = 'Lỗi kết nối. Vui lòng thử lại.';
                clientError.style.display = 'flex';
            } finally {
                sendOtpBtn.disabled = false;
            }
        }
        
        function startCountdown() {
            const otpTimer = document.getElementById('otpTimer');
            const countdownSpan = document.getElementById('countdown');
            let seconds = 600; // 10 minutes
            
            otpTimer.style.display = 'block';
            
            if (countdownInterval) {
                clearInterval(countdownInterval);
            }
            
            countdownInterval = setInterval(() => {
                seconds--;
                countdownSpan.textContent = seconds;
                
                if (seconds <= 0) {
                    clearInterval(countdownInterval);
                    otpTimer.style.display = 'none';
                    otpSent = false;
                }
            }, 1000);
        }
        
        async function handleSubmit(event) {
            event.preventDefault();
            
            const form = event.target;
            const btn = document.getElementById('submitBtn');
            const clientError = document.getElementById('clientError');
            const clientErrorText = document.getElementById('clientErrorText');
            
            // Hide previous errors
            clientError.style.display = 'none';
            
            // Check if OTP was sent
            if (!otpSent) {
                clientErrorText.textContent = 'Vui lòng gửi mã OTP trước!';
                clientError.style.display = 'flex';
                return false;
            }
            
            // Validate passwords match
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            
            if (password !== confirmPassword) {
                clientErrorText.textContent = 'Mật khẩu nhập lại không khớp!';
                clientError.style.display = 'flex';
                return false;
            }
            
            // Validate shop name for seller
            if (selectedRole === 'SELLER') {
                const shopName = document.getElementById('shopName').value.trim();
                if (!shopName) {
                    clientErrorText.textContent = 'Vui lòng nhập tên cửa hàng!';
                    clientError.style.display = 'flex';
                    return false;
                }
            }
            
            // Disable submit button
            btn.disabled = true;
            btn.textContent = 'Đang đăng ký...';
            
            // Submit form
            form.submit();
            return true;
        }
    </script>
</body>
</html>
