<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng ký - FreshSave</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth/auth-common.css">
    <style>
        body {
            background-image: url('${pageContext.request.contextPath}/images/backgroundLogin.png');
            max-height: 100vh;
        }
        .auth-container {
            max-width: 1100px;
            max-height: 100vh;
        }
        .auth-form {
            justify-content: flex-start;
        }
        .form-header {
            margin-bottom: 1rem;
        }
        .form-group {
            margin-bottom: 0.5rem;
        }
    </style>
</head>
<body>
    <div class="auth-container">
        <!-- Left Side -->
        <div class="auth-side">
            <div>
                <h2>Join the Movement</h2>
                <h1>Bạn đang tham gia<br>Hành Trình<br>Giải Cứu Thức Ăn</h1>
            </div>
            <div>
                <p>Cùng nhau chuyển hóa phần dư thành cơ hội cho người cần. Bắt đầu bằng việc tạo một tài khoản và chia sẻ yêu thương.</p>
            </div>
        </div>
        
        <!-- Right Side - Form -->
        <div class="auth-form">
            <div class="form-header">
                <h1 class="form-title">Tạo tài khoản</h1>
                <p class="form-subtitle">Tham gia cộng đồng <strong>Food Rescue</strong> ngay hôm nay</p>
            </div>
            
            <!-- Role Selection -->
            <div class="role-tabs">
                <button type="button" class="role-tab ${param.role == 'BUYER' || empty param.role ? 'active' : ''}" data-role="BUYER" onclick="selectRole('BUYER', this)">Khách hàng</button>
                <button type="button" class="role-tab ${param.role == 'SELLER' ? 'active' : ''}" data-role="SELLER" onclick="selectRole('SELLER', this)">Người bán</button>
                <button type="button" class="role-tab ${param.role == 'SHIPPER' ? 'active' : ''}" data-role="SHIPPER" onclick="selectRole('SHIPPER', this)">Shipper</button>
            </div>
            
            <!-- OTP Messages -->
            <c:if test="${not empty sessionScope.otpSuccess}">
                <div class="success-message">✅ ${sessionScope.otpSuccess}</div>
                <% session.removeAttribute("otpSuccess"); %>
            </c:if>
            <c:if test="${not empty sessionScope.otpError}">
                <div class="error-message">⚠️ ${sessionScope.otpError}</div>
                <% session.removeAttribute("otpError"); %>
            </c:if>
            
            <!-- Dev OTP (remove in production) -->
            <c:if test="${not empty sessionScope.devOtp}">
                <div style="background: #fef3c7; color: #92400e; padding: 0.5rem 0.75rem; border-radius: 0.5rem; font-size: 0.8rem; margin-bottom: 1rem;">
                    🔑 [DEV] Mã OTP: <strong>${sessionScope.devOtp}</strong>
                </div>
            </c:if>
            
            <!-- OTP Send Form (hidden, submitted via JS) -->
            <form method="POST" action="${pageContext.request.contextPath}/otp/send" id="otpForm" style="display: none;">
                <input type="hidden" name="fullName" id="otpFullName">
                <input type="hidden" name="phone" id="otpPhone">
                <input type="hidden" name="email" id="otpEmail">
                <input type="hidden" name="role" id="otpRole">
                <input type="hidden" name="shopName" id="otpShopName">
            </form>
            
            <!-- Main Registration Form -->
            <form method="POST" action="${pageContext.request.contextPath}/register" onsubmit="return handleSubmit(event)" id="registerForm">
                <input type="hidden" name="role" id="roleInput" value="${not empty param.role ? param.role : 'BUYER'}">
                
                <!-- Shop Name (only for Seller) -->
                <div class="form-group" id="shopNameGroup" style="display: ${param.role == 'SELLER' ? 'block' : 'none'};">
                    <label class="form-label" for="shopName">Tên Cửa Hàng *</label>
                    <input type="text" id="shopName" name="shopName" class="form-input" placeholder="Ví dụ: Cơm Tấm Sài Gòn" value="${param.shopName}">
                </div>
                
                <div class="form-group">
                    <label class="form-label" for="fullName">Họ và tên *</label>
                    <input type="text" id="fullName" name="fullName" class="form-input" placeholder="Nguyễn Văn A" value="${param.fullName}" required>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label" for="phone">Số điện thoại *</label>
                        <input type="tel" id="phone" name="phone" class="form-input" placeholder="0901234567" pattern="0[0-9]{9}" value="${param.phone}" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="email">Email *</label>
                        <div style="display: flex; gap: 0.5rem;">
                            <input type="email" id="email" name="email" class="form-input" placeholder="email@domain.com" value="${param.email}" style="flex: 1;" required>
                            <button type="button" id="sendOtpBtn" onclick="sendOtpForm()" class="btn-otp">
                                <c:choose>
                                    <c:when test="${sessionScope.otpSent}">Gửi lại</c:when>
                                    <c:otherwise>Gửi OTP</c:otherwise>
                                </c:choose>
                            </button>
                        </div>
                    </div>
                </div>
                
                <!-- OTP Input -->
                <div class="form-group">
                    <label class="form-label" for="otp">📧 Mã xác thực OTP * <small style="color: #64748b; font-weight: 400;">(Gửi qua email)</small></label>
                    <input type="text" id="otp" name="otp" class="form-input" placeholder="Nhập mã 6 số" maxlength="6" pattern="[0-9]{6}" required>
                    <small style="color: #64748b; font-size: 0.75rem; margin-top: 0.25rem; display: block;">💡 Kiểm tra hộp thư spam nếu không thấy email</small>
                </div>
                
                <div class="form-row">
                    <div class="form-group password-group">
                        <label class="form-label" for="password">Mật khẩu *</label>
                        <div class="password-wrapper">
                            <input type="password" id="password" name="password" class="form-input" placeholder="••••••••" minlength="6" required>
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
                            <input type="password" id="confirmPassword" name="confirmPassword" class="form-input" placeholder="••••••••" minlength="6" required>
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
                    <div class="error-message">⚠️ <c:out value="${error}" escapeXml="true" /></div>
                </c:if>
                
                <div id="clientError" class="error-message" style="display: none;">
                    ⚠️ <span id="clientErrorText"></span>
                </div>
                
                <div style="display: flex; justify-content: center;">
                    <button type="submit" class="btn-submit" id="submitBtn">Đăng Ký</button>
                </div>
            </form>
            
            <div class="form-footer">
                Đã có tài khoản? <a href="${pageContext.request.contextPath}/login" class="login-link">Đăng nhập ngay</a>
            </div>
        </div>
    </div>
    
    <script>
        function togglePassword(inputId, iconId) {
            var passwordInput = document.getElementById(inputId);
            var eyeIcon = document.getElementById(iconId);
            if (passwordInput.type === 'password') {
                passwordInput.type = 'text';
                eyeIcon.innerHTML = '<path d="M13.359 11.238C15.06 9.72 16 8 16 8s-3-5.5-8-5.5a7.028 7.028 0 0 0-2.79.588l.77.771A5.944 5.944 0 0 1 8 3.5c2.12 0 3.879 1.168 5.168 2.457A13.134 13.134 0 0 1 14.828 8c-.058.087-.122.183-.195.288-.335.48-.83 1.12-1.465 1.755-.165.165-.337.328-.517.486l.708.709z"/><path d="M11.297 9.176a3.5 3.5 0 0 0-4.474-4.474l.823.823a2.5 2.5 0 0 1 2.829 2.829l.822.822zm-2.943 1.299.822.822a3.5 3.5 0 0 1-4.474-4.474l.823.823a2.5 2.5 0 0 0 2.829 2.829z"/><path d="M3.35 5.47c-.18.16-.353.322-.518.487A13.134 13.134 0 0 0 1.172 8l.195.288c.335.48.83 1.12 1.465 1.755C4.121 11.332 5.881 12.5 8 12.5c.716 0 1.39-.133 2.02-.36l.77.772A7.029 7.029 0 0 1 8 13.5C3 13.5 0 8 0 8s.939-1.721 2.641-3.238l.708.709zm10.296 8.884-12-12 .708-.708 12 12-.708.708z"/>';
            } else {
                passwordInput.type = 'password';
                eyeIcon.innerHTML = '<path d="M10.5 8a2.5 2.5 0 1 1-5 0 2.5 2.5 0 0 1 5 0z"/><path d="M0 8s3-5.5 8-5.5S16 8 16 8s-3 5.5-8 5.5S0 8 0 8zm8 3.5a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7z"/>';
            }
        }
        
        var selectedRole = '${not empty param.role ? param.role : "BUYER"}';
        
        function selectRole(role, button) {
            selectedRole = role;
            document.getElementById('roleInput').value = role;
            
            var tabs = document.querySelectorAll('.role-tab');
            for (var i = 0; i < tabs.length; i++) {
                tabs[i].classList.remove('active');
            }
            button.classList.add('active');
            
            var shopNameGroup = document.getElementById('shopNameGroup');
            var shopNameInput = document.getElementById('shopName');
            if (role === 'SELLER') {
                shopNameGroup.style.display = 'block';
                shopNameInput.required = true;
            } else {
                shopNameGroup.style.display = 'none';
                shopNameInput.required = false;
                shopNameInput.value = '';
            }
        }
        
        function sendOtpForm() {
            var email = document.getElementById('email').value.trim();
            var clientError = document.getElementById('clientError');
            var clientErrorText = document.getElementById('clientErrorText');
            
            var emailRegex = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
            if (!email || !emailRegex.test(email)) {
                clientErrorText.textContent = 'Vui lòng nhập email hợp lệ';
                clientError.style.display = 'flex';
                document.getElementById('email').focus();
                return;
            }
            
            clientError.style.display = 'none';
            
            document.getElementById('otpFullName').value = document.getElementById('fullName').value;
            document.getElementById('otpPhone').value = document.getElementById('phone').value;
            document.getElementById('otpEmail').value = email;
            document.getElementById('otpRole').value = selectedRole;
            document.getElementById('otpShopName').value = document.getElementById('shopName').value;
            
            document.getElementById('otpForm').submit();
        }
        
        function handleSubmit(event) {
            var clientError = document.getElementById('clientError');
            var clientErrorText = document.getElementById('clientErrorText');
            clientError.style.display = 'none';
            
            var otpSent = ${sessionScope.otpSent ? 'true' : 'false'};
            if (!otpSent) {
                event.preventDefault();
                clientErrorText.textContent = 'Vui lòng gửi mã OTP trước!';
                clientError.style.display = 'flex';
                return false;
            }
            
            var password = document.getElementById('password').value;
            var confirmPassword = document.getElementById('confirmPassword').value;
            if (password !== confirmPassword) {
                event.preventDefault();
                clientErrorText.textContent = 'Mật khẩu nhập lại không khớp!';
                clientError.style.display = 'flex';
                return false;
            }
            
            if (selectedRole === 'SELLER') {
                var shopName = document.getElementById('shopName').value.trim();
                if (!shopName) {
                    event.preventDefault();
                    clientErrorText.textContent = 'Vui lòng nhập tên cửa hàng!';
                    clientError.style.display = 'flex';
                    return false;
                }
            }
            
            var btn = document.getElementById('submitBtn');
            btn.disabled = true;
            btn.textContent = 'Đang đăng ký...';
            return true;
        }
    </script>
</body>
</html>