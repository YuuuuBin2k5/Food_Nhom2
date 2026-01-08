# TÀI LIỆU LUỒNG HOẠT ĐỘNG - AUTH & SHIPPER

> **Mục đích**: Giải thích chi tiết cách JSP và Servlet hoạt động theo từng bước, bám sát cấu trúc code thực tế.

---

## 📋 MỤC LỤC

1. [PHẦN 1: AUTH - XÁC THỰC NGƯỜI DÙNG](#phần-1-auth---xác-thực-người-dùng)
   - [1.1 Đăng nhập (Login)](#11-đăng-nhập-login)
   - [1.2 Đăng ký (Register)](#12-đăng-ký-register)
   - [1.3 Quên mật khẩu (Forgot Password)](#13-quên-mật-khẩu-forgot-password)
   - [1.4 Đặt lại mật khẩu (Reset Password)](#14-đặt-lại-mật-khẩu-reset-password)
   - [1.5 Đăng xuất (Logout)](#15-đăng-xuất-logout)

2. [PHẦN 2: SHIPPER - GIAO HÀNG](#phần-2-shipper---giao-hàng)
   - [2.1 Xem đơn có sẵn (Orders)](#21-xem-đơn-có-sẵn-orders)
   - [2.2 Nhận đơn hàng (Accept Order)](#22-nhận-đơn-hàng-accept-order)
   - [2.3 Đang giao hàng (Delivering)](#23-đang-giao-hàng-delivering)
   - [2.4 Hoàn thành giao hàng (Complete Order)](#24-hoàn-thành-giao-hàng-complete-order)
   - [2.5 Lịch sử giao hàng (History)](#25-lịch-sử-giao-hàng-history)

---


# PHẦN 1: AUTH - XÁC THỰC NGƯỜI DÙNG

## 1.1 Đăng nhập (Login)

### 📍 URL Pattern
- **GET** `/login` - Hiển thị trang đăng nhập
- **POST** `/login` - Xử lý đăng nhập

### 🔄 LUỒNG HOẠT ĐỘNG CHI TIẾT

#### **BƯỚC 1: User truy cập `/login`**

**Servlet xử lý**: `LoginPageServlet.doGet()`

```java
// File: LoginPageServlet.java - Line 23-70
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    // 1. Kiểm tra đã đăng nhập chưa
    HttpSession session = request.getSession(false);
    if (session != null && session.getAttribute("user") != null) {
        String role = (String) session.getAttribute("role");
        redirectByRole(request, response, role); // Redirect về trang tương ứng
        return;
    }
    
    // 2. Kiểm tra cookie "Ghi nhớ tôi"
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (Cookie cookie : cookies) {
            if ("rememberToken".equals(cookie.getName())) {
                // Parse cookie: userId:token
                String[] parts = cookie.getValue().split(":");
                Long userId = Long.parseLong(parts[0]);
                
                // Lấy user từ database
                User user = authService.getUserById(userId);
                if (user != null && !user.isBanned()) {
                    // Tạo session mới và redirect
                    HttpSession newSession = request.getSession(true);
                    newSession.setAttribute("user", user);
                    newSession.setAttribute("userId", user.getUserId());
                    newSession.setAttribute("role", determineRole(user));
                    redirectByRole(request, response, role);
                    return;
                }
            }
        }
    }
    
    // 3. Hiển thị trang login
    request.getRequestDispatcher("/auth/login.jsp").forward(request, response);
}
```


**JSP hiển thị**: `login.jsp`

```jsp
<!-- File: login.jsp - Cấu trúc chính -->
<form method="POST" action="${pageContext.request.contextPath}/login" id="loginForm">
    <!-- 1. Input Email -->
    <input type="email" name="email" required 
           value="${param.email != null ? param.email : ''}">
    
    <!-- 2. Input Password với toggle visibility -->
    <input type="password" name="password" required>
    <button type="button" id="togglePassword">👁️</button>
    
    <!-- 3. Checkbox "Ghi nhớ tôi" -->
    <input type="checkbox" name="remember">
    
    <!-- 4. Link quên mật khẩu -->
    <a href="${pageContext.request.contextPath}/forgot-password">Quên mật khẩu?</a>
    
    <!-- 5. Hiển thị lỗi nếu có -->
    <c:if test="${not empty error}">
        <div class="error-message">⚠️ ${error}</div>
    </c:if>
    
    <!-- 6. Button submit -->
    <button type="submit" id="submitBtn">Đăng nhập</button>
</form>

<!-- JavaScript: Disable form khi submit để tránh double-click -->
<script>
document.getElementById('loginForm').addEventListener('submit', function(e) {
    var btn = document.getElementById('submitBtn');
    btn.disabled = true;
    btn.textContent = 'Đang xác thực...';
    
    // Set input readonly thay vì disabled để vẫn gửi được giá trị
    var inputs = form.querySelectorAll('input');
    inputs.forEach(function(el) {
        el.readOnly = true;
    });
    
    // Hiển thị loading overlay
    createLoadingOverlay();
});
</script>
```


#### **BƯỚC 2: User nhấn "Đăng nhập"**

**Servlet xử lý**: `LoginPageServlet.doPost()`

```java
// File: LoginPageServlet.java - Line 72-130
@Override
protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    // 1. Lấy dữ liệu từ form
    String email = request.getParameter("email");
    String password = request.getParameter("password");
    String remember = request.getParameter("remember");
    
    try {
        // 2. Xác thực user qua AuthService
        User user = authService.login(email, password);
        // AuthService sẽ:
        // - Tìm user theo email
        // - Kiểm tra password (BCrypt)
        // - Kiểm tra user có bị banned không
        // - Throw Exception nếu sai
        
        // 3. SECURITY: Ngăn Session Fixation Attack
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate(); // Hủy session cũ
        }
        
        // 4. Tạo session MỚI
        HttpSession session = request.getSession(true);
        String role = determineRole(user); // ADMIN/SELLER/SHIPPER/BUYER
        
        session.setAttribute("user", user);
        session.setAttribute("userId", user.getUserId());
        session.setAttribute("role", role);
        
        // 5. Xử lý "Ghi nhớ tôi"
        if ("on".equals(remember)) {
            // Tạo remember token
            String rememberToken = UUID.randomUUID().toString();
            session.setAttribute("rememberToken", rememberToken);
            
            // Tạo cookie: userId:token
            Cookie rememberCookie = new Cookie("rememberToken", 
                user.getUserId() + ":" + rememberToken);
            rememberCookie.setMaxAge(30 * 24 * 60 * 60); // 30 ngày
            rememberCookie.setHttpOnly(true); // Bảo mật
            response.addCookie(rememberCookie);
            
            // Session timeout dài
            session.setMaxInactiveInterval(30 * 24 * 60 * 60);
        } else {
            // Xóa cookie cũ
            Cookie rememberCookie = new Cookie("rememberToken", "");
            rememberCookie.setMaxAge(0);
            response.addCookie(rememberCookie);
            
            // Session timeout 30 phút
            session.setMaxInactiveInterval(30 * 60);
        }
        
        // 6. Redirect theo role
        redirectByRole(request, response, role);
        
    } catch (Exception e) {
        // 7. Login thất bại
        request.setAttribute("error", e.getMessage());
        request.setAttribute("email", email); // Giữ lại email
        request.getRequestDispatcher("/auth/login.jsp").forward(request, response);
    }
}

// Hàm redirect theo role
private void redirectByRole(String role) {
    switch (role) {
        case "ADMIN":   return "/admin/statistics";
        case "SELLER":  return "/seller/dashboard";
        case "SHIPPER": return "/shipper/orders";
        case "BUYER":   return "/";
    }
}
```

**Kết quả**:
- ✅ Thành công → Redirect về trang tương ứng với role
- ❌ Thất bại → Quay lại login.jsp với thông báo lỗi


---

## 1.2 Đăng ký (Register)

### 📍 URL Pattern
- **GET** `/register` - Hiển thị trang đăng ký
- **POST** `/register` - Xử lý đăng ký
- **POST** `/otp/send` - Gửi mã OTP

### 🔄 LUỒNG HOẠT ĐỘNG CHI TIẾT

#### **BƯỚC 1: User truy cập `/register`**

**Servlet xử lý**: `RegisterPageServlet.doGet()`

```java
// File: RegisterPageServlet.java - Line 18-30
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    // 1. Kiểm tra đã đăng nhập chưa
    HttpSession session = request.getSession(false);
    if (session != null && session.getAttribute("user") != null) {
        response.sendRedirect(request.getContextPath() + "/");
        return;
    }
    
    // 2. Hiển thị trang register
    request.getRequestDispatcher("/auth/register.jsp").forward(request, response);
}
```

**JSP hiển thị**: `register.jsp`

```jsp
<!-- File: register.jsp - Cấu trúc chính -->

<!-- 1. Role Selection Tabs -->
<div class="role-tabs">
    <button data-role="BUYER" onclick="selectRole('BUYER', this)">Khách hàng</button>
    <button data-role="SELLER" onclick="selectRole('SELLER', this)">Người bán</button>
    <button data-role="SHIPPER" onclick="selectRole('SHIPPER', this)">Shipper</button>
</div>

<!-- 2. Hidden OTP Form (submit qua JavaScript) -->
<form method="POST" action="/otp/send" id="otpForm" style="display: none;">
    <input type="hidden" name="fullName" id="otpFullName">
    <input type="hidden" name="phone" id="otpPhone">
    <input type="hidden" name="email" id="otpEmail">
    <input type="hidden" name="role" id="otpRole">
    <input type="hidden" name="shopName" id="otpShopName">
</form>

<!-- 3. Main Registration Form -->
<form method="POST" action="/register" onsubmit="return handleSubmit(event)">
    <input type="hidden" name="role" id="roleInput" value="BUYER">
    
    <!-- Shop Name (chỉ hiện khi role = SELLER) -->
    <div id="shopNameGroup" style="display: none;">
        <input type="text" name="shopName">
    </div>
    
    <!-- Họ tên -->
    <input type="text" name="fullName" required>
    
    <!-- Số điện thoại -->
    <input type="tel" name="phone" pattern="0[0-9]{9}" required>
    
    <!-- Email + Button gửi OTP -->
    <div style="display: flex;">
        <input type="email" name="email" required>
        <button type="button" onclick="sendOtpForm()">Gửi OTP</button>
    </div>
    
    <!-- OTP Input -->
    <input type="text" name="otp" maxlength="6" pattern="[0-9]{6}" required>
    
    <!-- Password -->
    <input type="password" name="password" minlength="6" required>
    <input type="password" name="confirmPassword" minlength="6" required>
    
    <button type="submit">Đăng Ký</button>
</form>
```


**JavaScript xử lý**:

```javascript
// File: register.jsp - JavaScript section

// 1. Chọn role
function selectRole(role, button) {
    selectedRole = role;
    document.getElementById('roleInput').value = role;
    
    // Toggle active class
    document.querySelectorAll('.role-tab').forEach(tab => {
        tab.classList.remove('active');
    });
    button.classList.add('active');
    
    // Hiện/ẩn shop name field
    var shopNameGroup = document.getElementById('shopNameGroup');
    if (role === 'SELLER') {
        shopNameGroup.style.display = 'block';
        document.getElementById('shopName').required = true;
    } else {
        shopNameGroup.style.display = 'none';
        document.getElementById('shopName').required = false;
    }
}

// 2. Gửi OTP
function sendOtpForm() {
    var email = document.getElementById('email').value.trim();
    
    // Validate email
    var emailRegex = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
    if (!email || !emailRegex.test(email)) {
        alert('Vui lòng nhập email hợp lệ');
        return;
    }
    
    // Copy data vào hidden form
    document.getElementById('otpFullName').value = document.getElementById('fullName').value;
    document.getElementById('otpPhone').value = document.getElementById('phone').value;
    document.getElementById('otpEmail').value = email;
    document.getElementById('otpRole').value = selectedRole;
    document.getElementById('otpShopName').value = document.getElementById('shopName').value;
    
    // Submit hidden form
    document.getElementById('otpForm').submit();
}

// 3. Validate trước khi submit
function handleSubmit(event) {
    // Kiểm tra đã gửi OTP chưa
    var otpSent = ${sessionScope.otpSent ? 'true' : 'false'};
    if (!otpSent) {
        event.preventDefault();
        alert('Vui lòng gửi mã OTP trước!');
        return false;
    }
    
    // Kiểm tra password match
    var password = document.getElementById('password').value;
    var confirmPassword = document.getElementById('confirmPassword').value;
    if (password !== confirmPassword) {
        event.preventDefault();
        alert('Mật khẩu nhập lại không khớp!');
        return false;
    }
    
    // Disable button
    var btn = document.getElementById('submitBtn');
    btn.disabled = true;
    btn.textContent = 'Đang đăng ký...';
    return true;
}
```


#### **BƯỚC 2: User nhấn "Gửi OTP"**

**Servlet xử lý**: `OtpServlet.doPost()`

```java
// File: OtpServlet.java - Line 21-70
@Override
protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    HttpSession session = request.getSession();
    
    // 1. Lấy dữ liệu từ hidden form
    String email = request.getParameter("email");
    String fullName = request.getParameter("fullName");
    String phone = request.getParameter("phone");
    String role = request.getParameter("role");
    String shopName = request.getParameter("shopName");
    
    // 2. Validate email
    if (email == null || email.trim().isEmpty()) {
        session.setAttribute("otpError", "Vui lòng nhập email");
        redirectBackToRegister(response, request, fullName, phone, email, role, shopName);
        return;
    }
    
    email = email.trim().toLowerCase();
    
    if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
        session.setAttribute("otpError", "Định dạng email không hợp lệ");
        redirectBackToRegister(response, request, fullName, phone, email, role, shopName);
        return;
    }
    
    try {
        // 3. Generate và gửi OTP qua OtpService
        String otp = otpService.generateOtp(email);
        // OtpService sẽ:
        // - Kiểm tra rate limiting (1 OTP/phút)
        // - Generate OTP 6 số
        // - Lưu vào memory với timestamp
        // - Gửi email qua MailUtil
        
        // 4. Lưu vào session
        session.setAttribute("otpSent", true);
        session.setAttribute("otpEmail", email);
        session.setAttribute("otpSuccess", "Mã OTP đã được gửi đến " + email);
        session.setAttribute("devOtp", otp); // Dev mode - hiện OTP
        
    } catch (Exception e) {
        session.setAttribute("otpError", "Lỗi gửi OTP: " + e.getMessage());
    }
    
    // 5. Redirect về register với form data preserved
    redirectBackToRegister(response, request, fullName, phone, email, role, shopName);
}

// Redirect về register với query params để giữ lại data
private void redirectBackToRegister(...) {
    StringBuilder url = new StringBuilder("/register?");
    url.append("fullName=").append(URLEncoder.encode(fullName, "UTF-8"));
    url.append("&phone=").append(URLEncoder.encode(phone, "UTF-8"));
    url.append("&email=").append(URLEncoder.encode(email, "UTF-8"));
    url.append("&role=").append(URLEncoder.encode(role, "UTF-8"));
    if (shopName != null) {
        url.append("&shopName=").append(URLEncoder.encode(shopName, "UTF-8"));
    }
    response.sendRedirect(url.toString());
}
```

**OtpService xử lý**:

```java
// File: OtpService.java - Line 40-80
public String generateOtp(String email) throws Exception {
    email = email.toLowerCase();
    
    // 1. Kiểm tra rate limiting
    OtpData existingData = otpStore.get(email);
    if (existingData != null) {
        long timeSinceLastOtp = System.currentTimeMillis() - existingData.timestamp;
        if (timeSinceLastOtp < 60 * 1000) { // 1 phút
            long secondsRemaining = (60 * 1000 - timeSinceLastOtp) / 1000;
            throw new Exception("Vui lòng đợi " + secondsRemaining + " giây");
        }
    }
    
    // 2. Generate OTP 6 số
    String otp = String.format("%06d", new Random().nextInt(1000000));
    
    // 3. Lưu vào memory
    otpStore.put(email, new OtpData(otp, System.currentTimeMillis()));
    
    // 4. Gửi email
    String subject = "Mã xác thực đăng ký tài khoản - FoodRescue";
    String body = buildEmailBody(otp);
    MailUtil.send(email, subject, body);
    
    return otp;
}
```

**Kết quả**:
- ✅ Thành công → Redirect về `/register` với message "OTP đã gửi"
- ❌ Thất bại → Redirect về `/register` với error message


#### **BƯỚC 3: User nhập OTP và nhấn "Đăng Ký"**

**Servlet xử lý**: `RegisterPageServlet.doPost()`

```java
// File: RegisterPageServlet.java - Line 32-80
@Override
protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    request.setCharacterEncoding("UTF-8");
    
    // 1. Lấy dữ liệu từ form
    String fullName = request.getParameter("fullName");
    String email = request.getParameter("email");
    String password = request.getParameter("password");
    String confirmPassword = request.getParameter("confirmPassword");
    String phone = request.getParameter("phone");
    String role = request.getParameter("role");
    String otp = request.getParameter("otp");
    String shopName = request.getParameter("shopName"); // Chỉ có khi role = SELLER
    
    try {
        // 2. Validate các field
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new Exception("Vui lòng nhập họ tên");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new Exception("Vui lòng nhập email");
        }
        if (password == null || password.length() < 6) {
            throw new Exception("Mật khẩu phải có ít nhất 6 ký tự");
        }
        if (!password.equals(confirmPassword)) {
            throw new Exception("Mật khẩu xác nhận không khớp");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new Exception("Vui lòng nhập số điện thoại");
        }
        if (otp == null || otp.trim().isEmpty()) {
            throw new Exception("Vui lòng nhập mã OTP");
        }
        
        // 3. Verify OTP
        String cleanEmail = email.trim().toLowerCase();
        if (!otpService.verifyOtp(cleanEmail, otp)) {
            throw new Exception("Mã OTP không đúng hoặc đã hết hạn");
        }
        // OtpService.verifyOtp() sẽ:
        // - Tìm OTP trong memory
        // - Kiểm tra expired (10 phút)
        // - So sánh OTP
        // - Xóa OTP sau khi verify thành công
        
        // 4. Đăng ký user qua AuthService
        authService.register(fullName, email, password, phone, role, shopName);
        // AuthService.register() sẽ:
        // - Kiểm tra email đã tồn tại chưa
        // - Hash password bằng BCrypt
        // - Tạo entity tương ứng (Buyer/Seller/Shipper)
        // - Lưu vào database
        // - Nếu SELLER: status = PENDING (chờ admin duyệt)
        
        // 5. Success - redirect to login
        response.sendRedirect(request.getContextPath() + "/login?registered=true");
        
    } catch (Exception e) {
        // 6. Registration failed - giữ lại data
        request.setAttribute("error", e.getMessage());
        request.setAttribute("fullName", fullName);
        request.setAttribute("email", email);
        request.setAttribute("phone", phone);
        request.setAttribute("role", role);
        request.setAttribute("shopName", shopName);
        request.getRequestDispatcher("/auth/register.jsp").forward(request, response);
    }
}
```

**Kết quả**:
- ✅ Thành công → Redirect về `/login?registered=true`
- ❌ Thất bại → Quay lại register.jsp với error và giữ lại data


---

## 1.3 Quên mật khẩu (Forgot Password)

### 📍 URL Pattern
- **GET** `/forgot-password` - Hiển thị trang quên mật khẩu
- **POST** `/forgot-password` - Gửi email reset password

### 🔄 LUỒNG HOẠT ĐỘNG CHI TIẾT

#### **BƯỚC 1: User truy cập `/forgot-password`**

**Servlet**: `ForgotPasswordPageServlet.doGet()`
```java
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    request.getRequestDispatcher("/auth/forgot-password.jsp").forward(request, response);
}
```

**JSP**: `forgot-password.jsp`
```jsp
<form method="POST" action="/forgot-password">
    <input type="email" name="email" required>
    <button type="submit">Gửi email đặt lại</button>
</form>
```

#### **BƯỚC 2: User nhập email và submit**

**Servlet**: `ForgotPasswordPageServlet.doPost()`
```java
@Override
protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    String email = request.getParameter("email");
    
    try {
        if (email == null || email.trim().isEmpty()) {
            throw new Exception("Vui lòng nhập email");
        }
        
        // Lấy base URL động (để tạo link reset)
        String baseUrl = AppConfig.getBaseUrl(request);
        // AppConfig.getBaseUrl() ưu tiên:
        // 1. Environment variable APP_BASE_URL
        // 2. Auto-detect từ request
        // 3. Fallback localhost
        
        // Gửi email reset password
        authService.forgotPassword(email, baseUrl);
        // AuthService.forgotPassword() sẽ:
        // - Tìm user theo email
        // - Generate reset token (UUID)
        // - Lưu token vào database với expiry (1 giờ)
        // - Gửi email với link: {baseUrl}/reset-password?token={token}
        
        request.setAttribute("success", "Đã gửi email hướng dẫn đặt lại mật khẩu");
        request.getRequestDispatcher("/auth/forgot-password.jsp").forward(request, response);
        
    } catch (Exception e) {
        request.setAttribute("error", e.getMessage());
        request.getRequestDispatcher("/auth/forgot-password.jsp").forward(request, response);
    }
}
```

**Kết quả**: Email được gửi với link reset password

---

## 1.4 Đặt lại mật khẩu (Reset Password)

### 📍 URL Pattern
- **GET** `/reset-password?token=xxx` - Hiển thị form đặt lại mật khẩu
- **POST** `/reset-password` - Xử lý đặt lại mật khẩu

#### **BƯỚC 1: User click link trong email**

URL: `/reset-password?token=abc-123-xyz`

**Servlet**: `ResetPasswordPageServlet.doGet()`
```java
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    // Token được truyền qua query param
    request.getRequestDispatcher("/auth/reset-password.jsp").forward(request, response);
}
```

**JSP**: `reset-password.jsp`
```jsp
<form method="POST" action="/reset-password">
    <!-- Hidden field chứa token -->
    <input type="hidden" name="token" value="${param.token}">
    
    <input type="password" name="password" minlength="6" required>
    <input type="password" name="confirm" minlength="6" required>
    
    <button type="submit">Đặt lại mật khẩu</button>
</form>
```


#### **BƯỚC 2: User nhập mật khẩu mới và submit**

**Servlet**: `ResetPasswordPageServlet.doPost()`
```java
@Override
protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    String token = request.getParameter("token");
    String password = request.getParameter("password");
    String confirm = request.getParameter("confirm");
    
    try {
        // 1. Validate
        if (token == null || token.trim().isEmpty()) {
            throw new Exception("Token không hợp lệ");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new Exception("Vui lòng nhập mật khẩu mới");
        }
        if (!password.equals(confirm)) {
            throw new Exception("Mật khẩu xác nhận không khớp");
        }
        if (password.length() < 6) {
            throw new Exception("Mật khẩu phải có ít nhất 6 ký tự");
        }
        
        // 2. Reset password
        boolean success = authService.resetPassword(token, password);
        // AuthService.resetPassword() sẽ:
        // - Tìm user theo token
        // - Kiểm tra token expired chưa (1 giờ)
        // - Hash password mới bằng BCrypt
        // - Update password trong database
        // - Xóa token
        
        if (!success) {
            throw new Exception("Token không hợp lệ hoặc đã hết hạn");
        }
        
        // 3. Success
        request.setAttribute("success", "Đổi mật khẩu thành công!");
        request.getRequestDispatcher("/auth/reset-password.jsp").forward(request, response);
        
    } catch (Exception e) {
        request.setAttribute("error", e.getMessage());
        request.setAttribute("token", token);
        request.getRequestDispatcher("/auth/reset-password.jsp").forward(request, response);
    }
}
```

**JSP auto redirect sau 2 giây**:
```jsp
<c:if test="${not empty success}">
    <script>
        setTimeout(function() {
            window.location.href = '/login';
        }, 2000);
    </script>
</c:if>
```

---

## 1.5 Đăng xuất (Logout)

### 📍 URL Pattern
- **GET/POST** `/logout` - Đăng xuất

**Servlet**: `LogoutServlet.doGet()`
```java
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    // 1. Invalidate session
    HttpSession session = request.getSession(false);
    if (session != null) {
        session.invalidate();
    }
    
    // 2. Xóa remember cookie
    Cookie rememberCookie = new Cookie("rememberToken", "");
    rememberCookie.setMaxAge(0); // Xóa cookie
    rememberCookie.setPath("/");
    rememberCookie.setHttpOnly(true);
    response.addCookie(rememberCookie);
    
    // 3. Redirect to login
    response.sendRedirect(request.getContextPath() + "/login");
}
```


---

# PHẦN 2: SHIPPER - GIAO HÀNG

## 2.1 Xem đơn có sẵn (Orders)

### 📍 URL Pattern
- **GET** `/shipper/orders` - Xem danh sách đơn hàng chờ nhận

### 🔄 LUỒNG HOẠT ĐỘNG CHI TIẾT

#### **BƯỚC 1: Shipper truy cập `/shipper/orders`**

**Servlet xử lý**: `ShipperOrdersServlet.doGet()`

```java
// File: ShipperOrdersServlet.java - Line 22-80
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    // 1. Kiểm tra session
    HttpSession session = request.getSession(false);
    if (session == null || session.getAttribute("user") == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    
    // 2. Kiểm tra role
    String role = (String) session.getAttribute("role");
    if (!"SHIPPER".equals(role)) {
        response.sendRedirect(request.getContextPath() + "/home");
        return;
    }
    
    User user = (User) session.getAttribute("user");
    String shipperId = user.getUserId();
    
    // 3. Set menu items cho sidebar
    MenuHelper.setMenuItems(request, "SHIPPER", "/shipper/orders");
    
    try {
        // 4. Lấy tất cả orders liên quan đến shipper
        List<Order> orders = orderService.getOrdersForShipper(shipperId);
        // OrderService.getOrdersForShipper() sẽ lấy:
        // - CONFIRMED: Đơn chờ shipper nhận
        // - SHIPPING: Đơn shipper này đang giao
        // - DELIVERED: Đơn shipper này đã giao
        
        // 5. Tính toán thống kê
        long availableOrders = orders.stream()
            .filter(o -> o.getStatus() == OrderStatus.CONFIRMED)
            .count();
        
        long shippingOrders = orders.stream()
            .filter(o -> o.getStatus() == OrderStatus.SHIPPING && 
                       shipperId.equals(o.getShipper().getUserId()))
            .count();
        
        long deliveredOrders = orders.stream()
            .filter(o -> o.getStatus() == OrderStatus.DELIVERED && 
                       shipperId.equals(o.getShipper().getUserId()))
            .count();
        
        // 6. Lọc chỉ lấy đơn CONFIRMED để hiển thị
        List<Order> availableOrdersList = orders.stream()
            .filter(o -> o.getStatus() == OrderStatus.CONFIRMED)
            .toList();
        
        // 7. Kiểm tra shipper có đơn đang giao không
        boolean hasActiveDelivery = orderService.shipperHasActiveDelivery(shipperId);
        
        // 8. Set attributes
        request.setAttribute("orders", availableOrdersList);
        request.setAttribute("availableOrders", availableOrders);
        request.setAttribute("shippingOrders", shippingOrders);
        request.setAttribute("deliveredOrders", deliveredOrders);
        request.setAttribute("hasActiveDelivery", hasActiveDelivery);
        request.setAttribute("user", user);
        
        // 9. Forward to JSP
        request.getRequestDispatcher("/shipper/ordersShipper.jsp").forward(request, response);
        
    } catch (Exception e) {
        e.printStackTrace();
        response.sendError(500, "Lỗi tải danh sách đơn hàng");
    }
}
```


**JSP hiển thị**: `ordersShipper.jsp`

```jsp
<!-- File: ordersShipper.jsp - Cấu trúc chính -->

<!-- 1. Include sidebar -->
<jsp:include page="../common/sidebar.jsp">
    <jsp:param name="currentPath" value="/shipper/orders"/>
</jsp:include>

<!-- 2. Toast Messages (hiển thị success/error từ session) -->
<c:if test="${not empty sessionScope.successMessage}">
    <div class="toast-modern show success">
        ✓ ${sessionScope.successMessage}
    </div>
    <c:remove var="successMessage" scope="session"/>
</c:if>

<!-- 3. Header với stats -->
<header class="shipper-header">
    <h1>Sẵn sàng giao hàng</h1>
    <div class="status-text">Online • Đang hoạt động</div>
</header>

<!-- 4. Stats Cards -->
<div class="stats-grid">
    <div class="stat-card">
        <span>📦</span>
        <p>Đơn có sẵn</p>
        <p>${availableOrders} đơn</p>
    </div>
    <div class="stat-card">
        <span>🔥</span>
        <p>Đang giao</p>
        <p>${shippingOrders} đơn</p>
    </div>
    <div class="stat-card">
        <span>✅</span>
        <p>Đã hoàn thành</p>
        <p>${deliveredOrders} đơn</p>
    </div>
</div>

<!-- 5. Warning nếu đang có đơn giao -->
<c:if test="${hasActiveDelivery}">
    <div class="alert-warning-modern">
        ⚠️ Bạn đang có đơn hàng chưa hoàn thành!
        <a href="/shipper/delivering">Xem đơn đang giao →</a>
    </div>
</c:if>

<!-- 6. Danh sách đơn hàng -->
<c:choose>
    <c:when test="${empty orders}">
        <!-- Empty state -->
        <div class="empty-state-modern">
            <div class="empty-icon">📭</div>
            <h3>Đã xử lý hết!</h3>
            <p>Chúng tôi đang quét khu vực của bạn để tìm đơn hàng mới.</p>
            <a href="/shipper/orders" class="btn-scan-modern">Quét lại khu vực</a>
        </div>
    </c:when>
    <c:otherwise>
        <!-- Orders grid -->
        <div class="orders-grid">
            <c:forEach var="order" items="${orders}">
                <div class="order-card-modern">
                    <!-- Map visual -->
                    <div class="order-card-map">
                        <div class="map-badge">📍 ~2 km</div>
                        <div class="map-badge">🛍️ ${order.orderDetails.size()} món</div>
                    </div>
                    
                    <!-- Content -->
                    <div class="order-card-content">
                        <!-- Time estimate -->
                        <div class="time-estimate-box">
                            ⏱️ Thời gian ước tính: 15-20 phút
                        </div>
                        
                        <!-- Route details -->
                        <div class="route-details">
                            <div class="route-point">
                                🏪 Cửa hàng
                                <p>Đơn hàng #${order.orderId}</p>
                            </div>
                            <div class="route-point">
                                📍 ${order.buyer.fullName}
                                <p>${order.shippingAddress}</p>
                            </div>
                        </div>
                        
                        <!-- Actions -->
                        <div class="order-card-actions">
                            <c:choose>
                                <c:when test="${hasActiveDelivery}">
                                    <!-- Disabled nếu đang có đơn -->
                                    <button disabled>Đang có đơn</button>
                                </c:when>
                                <c:otherwise>
                                    <!-- Form nhận đơn -->
                                    <form action="/shipper/action" method="post"
                                          onsubmit="return confirm('Bạn có chắc muốn nhận đơn này?');">
                                        <input type="hidden" name="action" value="accept">
                                        <input type="hidden" name="orderId" value="${order.orderId}">
                                        <button type="submit">Nhận đơn này</button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<!-- 7. Bottom Navigation (Mobile) -->
<nav class="bottom-nav-modern">
    <a href="/shipper/orders" class="active">📦 Đơn hàng</a>
    <a href="/shipper/delivering">🚚 Đang giao</a>
    <a href="/shipper/history">📋 Lịch sử</a>
</nav>
```

**JavaScript xử lý**:
```javascript
// Auto hide toast sau 3 giây
setTimeout(function() {
    document.querySelectorAll('.toast-modern').forEach(function(toast) {
        toast.classList.remove('show');
    });
}, 3000);

// Sync sidebar state với container
function syncSidebarState() {
    var sidebar = document.getElementById('sidebar');
    var container = document.querySelector('.shipper-container');
    if (sidebar.classList.contains('scrolled')) {
        container.classList.add('sidebar-scrolled');
    }
}
```


---

## 2.2 Nhận đơn hàng (Accept Order)

### 📍 URL Pattern
- **POST** `/shipper/action?action=accept&orderId=123` - Nhận đơn hàng

### 🔄 LUỒNG HOẠT ĐỘNG CHI TIẾT

#### **BƯỚC 1: Shipper nhấn "Nhận đơn này"**

**Form trong JSP**:
```jsp
<form action="/shipper/action" method="post"
      onsubmit="return confirm('Bạn có chắc muốn nhận đơn này?');">
    <input type="hidden" name="action" value="accept">
    <input type="hidden" name="orderId" value="${order.orderId}">
    <button type="submit">Nhận đơn này</button>
</form>
```

**Servlet xử lý**: `ShipperActionServlet.doPost()`

```java
// File: ShipperActionServlet.java - Line 28-70
@Override
protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    // 1. Validate shipper session
    HttpSession session = request.getSession(false);
    if (session == null || session.getAttribute("user") == null) {
        response.sendRedirect("/login");
        return;
    }
    
    String role = (String) session.getAttribute("role");
    if (!"SHIPPER".equals(role)) {
        response.sendRedirect("/home");
        return;
    }
    
    User user = (User) session.getAttribute("user");
    String shipperId = user.getUserId();
    
    // 2. Lấy parameters
    String action = request.getParameter("action");
    String orderIdStr = request.getParameter("orderId");
    
    if (action == null || orderIdStr == null) {
        session.setAttribute("errorMessage", "Thiếu thông tin yêu cầu");
        response.sendRedirect("/shipper/orders");
        return;
    }
    
    try {
        Long orderId = Long.parseLong(orderIdStr);
        
        // 3. Route theo action
        switch (action) {
            case "accept":
                handleAcceptOrder(orderId, shipperId, session, response, request);
                break;
            case "complete":
                handleCompleteOrder(orderId, shipperId, session, response, request);
                break;
            default:
                session.setAttribute("errorMessage", "Hành động không hợp lệ");
                response.sendRedirect("/shipper/orders");
        }
        
    } catch (Exception e) {
        session.setAttribute("errorMessage", "Lỗi: " + e.getMessage());
        response.sendRedirect("/shipper/orders");
    }
}

// Handler cho accept order
private void handleAcceptOrder(Long orderId, String shipperId, 
        HttpSession session, HttpServletResponse response, HttpServletRequest request) 
        throws Exception {
    
    // 1. Update order status
    orderService.updateOrderStatus(orderId, OrderStatus.SHIPPING, shipperId);
    // OrderService.updateOrderStatus() sẽ:
    // - Tìm order theo ID
    // - Kiểm tra order status = CONFIRMED
    // - Set status = SHIPPING
    // - Set shipper = shipperId
    // - Update trong database
    
    // 2. Tạo log
    UserLog log = new UserLog(
        shipperId, 
        Role.SHIPPER, 
        ActionType.SHIPPER_ACCEPT_ORDER,
        "Shipper nhận đơn hàng #" + orderId, 
        orderId.toString(), 
        "ORDER", 
        null
    );
    userLogService.save(log);
    
    // 3. Set success message
    session.setAttribute("successMessage", "Nhận đơn thành công! Bắt đầu giao hàng.");
    
    // 4. Redirect to delivering page
    response.sendRedirect(request.getContextPath() + "/shipper/delivering");
}
```

**Kết quả**:
- Order status: CONFIRMED → SHIPPING
- Order.shipper: null → shipperId
- Redirect về `/shipper/delivering` với success message


---

## 2.3 Đang giao hàng (Delivering)

### 📍 URL Pattern
- **GET** `/shipper/delivering` - Xem đơn đang giao

### 🔄 LUỒNG HOẠT ĐỘNG CHI TIẾT

#### **BƯỚC 1: Shipper truy cập `/shipper/delivering`**

**Servlet xử lý**: `ShipperDeliveringServlet.doGet()`

```java
// File: ShipperDeliveringServlet.java - Line 21-60
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    // 1. Validate session và role
    HttpSession session = request.getSession(false);
    if (session == null || session.getAttribute("user") == null) {
        response.sendRedirect("/login");
        return;
    }
    
    String role = (String) session.getAttribute("role");
    if (!"SHIPPER".equals(role)) {
        response.sendRedirect("/home");
        return;
    }
    
    User user = (User) session.getAttribute("user");
    String shipperId = user.getUserId();
    
    // 2. Set menu items
    MenuHelper.setMenuItems(request, "SHIPPER", "/shipper/delivering");
    
    try {
        // 3. Lấy tất cả orders
        List<Order> orders = orderService.getOrdersForShipper(shipperId);
        
        // 4. Tìm đơn đang giao (SHIPPING) của shipper này
        Order currentOrder = orders.stream()
            .filter(o -> o.getStatus() == OrderStatus.SHIPPING && 
                       shipperId.equals(o.getShipper().getUserId()))
            .findFirst()
            .orElse(null);
        
        // 5. Set attributes
        request.setAttribute("currentOrder", currentOrder);
        request.setAttribute("user", user);
        
        // 6. Forward to JSP
        request.getRequestDispatcher("/shipper/delivering.jsp").forward(request, response);
        
    } catch (Exception e) {
        e.printStackTrace();
        response.sendError(500, "Lỗi tải thông tin đơn hàng");
    }
}
```


**JSP hiển thị**: `delivering.jsp`

```jsp
<!-- File: delivering.jsp - Cấu trúc chính -->

<c:choose>
    <c:when test="${not empty currentOrder}">
        <!-- Active Order Card -->
        <div class="active-order-card">
            <!-- Header -->
            <div class="active-order-header">
                <h2>#${currentOrder.orderId}</h2>
                <span class="badge">🚚 Đang giao</span>
            </div>
            
            <!-- Body -->
            <div class="active-order-body">
                <!-- Customer Info Grid -->
                <div class="customer-info-grid">
                    <div>
                        <p>👤 Khách hàng</p>
                        <p>${currentOrder.buyer.fullName}</p>
                    </div>
                    <div>
                        <p>📞 Điện thoại</p>
                        <p>${currentOrder.buyer.phoneNumber}</p>
                    </div>
                    <div>
                        <p>📅 Ngày đặt</p>
                        <p><fmt:formatDate value="${currentOrder.orderDate}" pattern="dd/MM HH:mm"/></p>
                    </div>
                </div>
                
                <!-- Address -->
                <div class="address-box-modern">
                    <p>📍 Địa chỉ giao hàng</p>
                    <p>${currentOrder.shippingAddress}</p>
                </div>
                
                <!-- Products List -->
                <div class="products-section">
                    <p>🛍️ Sản phẩm (${currentOrder.orderDetails.size()} món)</p>
                    <div class="products-list">
                        <c:forEach var="item" items="${currentOrder.orderDetails}">
                            <div class="product-item">
                                <span>${item.product.name}</span>
                                <span>x${item.quantity}</span>
                            </div>
                        </c:forEach>
                    </div>
                </div>
                
                <!-- Actions -->
                <div class="active-order-actions">
                    <!-- Call customer -->
                    <a href="tel:${currentOrder.buyer.phoneNumber}" class="btn-call-modern">
                        📞 Gọi khách
                    </a>
                    
                    <!-- Open Google Maps -->
                    <a href="https://www.google.com/maps/dir/?api=1&destination=${currentOrder.shippingAddress}" 
                       target="_blank" class="btn-map-modern">
                        🗺️ Chỉ đường
                    </a>
                    
                    <!-- Complete Order Form -->
                    <form action="/shipper/action" method="post"
                          onsubmit="return confirm('Xác nhận đã giao hàng thành công cho khách?');">
                        <input type="hidden" name="action" value="complete">
                        <input type="hidden" name="orderId" value="${currentOrder.orderId}">
                        <button type="submit" class="btn-complete-modern">
                            ✅ Đã giao xong
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <!-- Empty State -->
        <div class="empty-state-modern">
            <div class="empty-icon">🚚</div>
            <h3>Chưa có đơn đang giao</h3>
            <p>Nhận đơn mới từ trang "Đơn có sẵn" để bắt đầu giao hàng.</p>
            <a href="/shipper/orders" class="btn-scan-modern">
                📦 Xem đơn có sẵn
            </a>
        </div>
    </c:otherwise>
</c:choose>

<!-- Bottom Navigation -->
<nav class="bottom-nav-modern">
    <a href="/shipper/orders">📦 Đơn hàng</a>
    <a href="/shipper/delivering" class="active">🚚 Đang giao</a>
    <a href="/shipper/history">📋 Lịch sử</a>
</nav>
```

**Các tính năng**:
- Hiển thị thông tin đơn hàng đang giao
- Button gọi điện cho khách (tel: link)
- Button chỉ đường Google Maps
- Form hoàn thành giao hàng


---

## 2.4 Hoàn thành giao hàng (Complete Order)

### 📍 URL Pattern
- **POST** `/shipper/action?action=complete&orderId=123` - Hoàn thành giao hàng

### 🔄 LUỒNG HOẠT ĐỘNG CHI TIẾT

#### **BƯỚC 1: Shipper nhấn "Đã giao xong"**

**Form trong JSP**:
```jsp
<form action="/shipper/action" method="post"
      onsubmit="return confirm('Xác nhận đã giao hàng thành công cho khách?');">
    <input type="hidden" name="action" value="complete">
    <input type="hidden" name="orderId" value="${currentOrder.orderId}">
    <button type="submit">✅ Đã giao xong</button>
</form>
```

**Servlet xử lý**: `ShipperActionServlet.handleCompleteOrder()`

```java
// File: ShipperActionServlet.java - Handler method
private void handleCompleteOrder(Long orderId, String shipperId, 
        HttpSession session, HttpServletResponse response, HttpServletRequest request) 
        throws Exception {
    
    // 1. Update order status
    orderService.updateOrderStatus(orderId, OrderStatus.DELIVERED, shipperId);
    // OrderService.updateOrderStatus() sẽ:
    // - Tìm order theo ID
    // - Kiểm tra order status = SHIPPING
    // - Kiểm tra order.shipper = shipperId (đúng shipper)
    // - Set status = DELIVERED
    // - Set deliveredDate = now
    // - Update trong database
    
    // 2. Tạo log
    UserLog log = new UserLog(
        shipperId, 
        Role.SHIPPER, 
        ActionType.SHIPPER_COMPLETE_ORDER,
        "Shipper hoàn thành giao hàng #" + orderId, 
        orderId.toString(), 
        "ORDER", 
        null
    );
    userLogService.save(log);
    
    // 3. Set success message
    session.setAttribute("successMessage", "Giao hàng thành công! 🎉");
    
    // 4. Redirect về orders page
    response.sendRedirect(request.getContextPath() + "/shipper/orders");
}
```

**Kết quả**:
- Order status: SHIPPING → DELIVERED
- Order.deliveredDate: null → current timestamp
- Redirect về `/shipper/orders` với success message
- Shipper có thể nhận đơn mới


---

## 2.5 Lịch sử giao hàng (History)

### 📍 URL Pattern
- **GET** `/shipper/history` - Xem lịch sử đơn đã giao

### 🔄 LUỒNG HOẠT ĐỘNG CHI TIẾT

#### **BƯỚC 1: Shipper truy cập `/shipper/history`**

**Servlet xử lý**: `ShipperHistoryServlet.doGet()`

```java
// File: ShipperHistoryServlet.java - Line 22-60
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    // 1. Validate session và role
    HttpSession session = request.getSession(false);
    if (session == null || session.getAttribute("user") == null) {
        response.sendRedirect("/login");
        return;
    }
    
    String role = (String) session.getAttribute("role");
    if (!"SHIPPER".equals(role)) {
        response.sendRedirect("/home");
        return;
    }
    
    User user = (User) session.getAttribute("user");
    String shipperId = user.getUserId();
    
    // 2. Set menu items
    MenuHelper.setMenuItems(request, "SHIPPER", "/shipper/history");
    
    try {
        // 3. Lấy tất cả orders
        List<Order> orders = orderService.getOrdersForShipper(shipperId);
        
        // 4. Lọc chỉ lấy đơn DELIVERED của shipper này
        List<Order> deliveredOrders = orders.stream()
            .filter(o -> o.getStatus() == OrderStatus.DELIVERED && 
                       shipperId.equals(o.getShipper().getUserId()))
            .collect(Collectors.toList());
        
        // 5. Set attributes
        request.setAttribute("deliveredOrders", deliveredOrders);
        request.setAttribute("user", user);
        
        // 6. Forward to JSP
        request.getRequestDispatcher("/shipper/history.jsp").forward(request, response);
        
    } catch (Exception e) {
        e.printStackTrace();
        response.sendError(500, "Lỗi tải lịch sử");
    }
}
```

**JSP hiển thị**: `history.jsp`

```jsp
<!-- File: history.jsp - Cấu trúc chính -->

<header class="shipper-header">
    <h1>Lịch sử giao hàng</h1>
    <div class="subtitle">Các đơn đã hoàn thành</div>
</header>

<div class="history-card">
    <div class="history-header">
        <h3>✅ Đơn đã hoàn thành</h3>
    </div>
    
    <c:choose>
        <c:when test="${empty deliveredOrders}">
            <!-- Empty State -->
            <div class="empty-state-modern">
                <div class="empty-icon">📭</div>
                <h3>Chưa có lịch sử</h3>
                <p>Bạn chưa hoàn thành đơn hàng nào</p>
                <a href="/shipper/orders" class="btn-scan-modern">
                    📦 Nhận đơn ngay
                </a>
            </div>
        </c:when>
        <c:otherwise>
            <!-- History Table -->
            <table class="history-table">
                <thead>
                    <tr>
                        <th>Mã đơn</th>
                        <th>Khách hàng</th>
                        <th>Địa chỉ</th>
                        <th>Ngày giao</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="order" items="${deliveredOrders}">
                        <tr>
                            <td><span class="order-id-badge">#${order.orderId}</span></td>
                            <td><span class="customer-name">${order.buyer.fullName}</span></td>
                            <td><span class="address-truncate">${order.shippingAddress}</span></td>
                            <td>
                                <span class="date-text">
                                    <fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm"/>
                                </span>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>

<!-- Bottom Navigation -->
<nav class="bottom-nav-modern">
    <a href="/shipper/orders">📦 Đơn hàng</a>
    <a href="/shipper/delivering">🚚 Đang giao</a>
    <a href="/shipper/history" class="active">📋 Lịch sử</a>
</nav>
```


---

# TỔNG KẾT LUỒNG HOẠT ĐỘNG

## 🔐 AUTH Flow Summary

```
1. LOGIN
   User → GET /login → LoginPageServlet.doGet() → login.jsp
   User submit → POST /login → LoginPageServlet.doPost() → AuthService.login()
   → Create session → Redirect by role

2. REGISTER
   User → GET /register → RegisterPageServlet.doGet() → register.jsp
   User click "Gửi OTP" → POST /otp/send → OtpServlet.doPost() → OtpService.generateOtp()
   → MailUtil.send() → Redirect back to register
   User submit → POST /register → RegisterPageServlet.doPost() → OtpService.verifyOtp()
   → AuthService.register() → Redirect to login

3. FORGOT PASSWORD
   User → GET /forgot-password → ForgotPasswordPageServlet.doGet() → forgot-password.jsp
   User submit → POST /forgot-password → ForgotPasswordPageServlet.doPost()
   → AuthService.forgotPassword() → MailUtil.send() → Show success

4. RESET PASSWORD
   User click link → GET /reset-password?token=xxx → ResetPasswordPageServlet.doGet()
   → reset-password.jsp
   User submit → POST /reset-password → ResetPasswordPageServlet.doPost()
   → AuthService.resetPassword() → Redirect to login

5. LOGOUT
   User → GET /logout → LogoutServlet.doGet() → Invalidate session → Delete cookie
   → Redirect to login
```

## 🚚 SHIPPER Flow Summary

```
1. VIEW ORDERS (Đơn có sẵn)
   Shipper → GET /shipper/orders → ShipperOrdersServlet.doGet()
   → OrderService.getOrdersForShipper() → Filter CONFIRMED orders
   → ordersShipper.jsp

2. ACCEPT ORDER (Nhận đơn)
   Shipper click "Nhận đơn" → POST /shipper/action?action=accept&orderId=123
   → ShipperActionServlet.doPost() → handleAcceptOrder()
   → OrderService.updateOrderStatus(SHIPPING) → UserLogService.save()
   → Redirect to /shipper/delivering

3. VIEW DELIVERING (Đang giao)
   Shipper → GET /shipper/delivering → ShipperDeliveringServlet.doGet()
   → OrderService.getOrdersForShipper() → Filter SHIPPING orders
   → delivering.jsp

4. COMPLETE ORDER (Hoàn thành)
   Shipper click "Đã giao xong" → POST /shipper/action?action=complete&orderId=123
   → ShipperActionServlet.doPost() → handleCompleteOrder()
   → OrderService.updateOrderStatus(DELIVERED) → UserLogService.save()
   → Redirect to /shipper/orders

5. VIEW HISTORY (Lịch sử)
   Shipper → GET /shipper/history → ShipperHistoryServlet.doGet()
   → OrderService.getOrdersForShipper() → Filter DELIVERED orders
   → history.jsp
```


---

# CÁC ĐIỂM QUAN TRỌNG CẦN LƯU Ý

## 🔒 Security

1. **Session Fixation Prevention**: Luôn invalidate session cũ và tạo session mới khi login
2. **Password Hashing**: Sử dụng BCrypt để hash password
3. **HttpOnly Cookie**: Cookie "rememberToken" có flag HttpOnly để tránh XSS
4. **Token Expiry**: Reset password token có thời hạn 1 giờ
5. **OTP Expiry**: OTP có thời hạn 10 phút
6. **Rate Limiting**: Chỉ cho gửi 1 OTP mỗi phút

## 📝 Session Management

**Session Attributes**:
```java
session.setAttribute("user", user);           // User object
session.setAttribute("userId", user.getUserId()); // String
session.setAttribute("role", role);           // ADMIN/SELLER/SHIPPER/BUYER
session.setAttribute("rememberToken", token); // UUID (nếu "Ghi nhớ tôi")
```

**Session Timeout**:
- Với "Ghi nhớ tôi": 30 ngày
- Không "Ghi nhớ tôi": 30 phút

## 🍪 Cookie Management

**Remember Cookie**:
```
Name: rememberToken
Value: userId:token (ví dụ: "123:abc-def-ghi")
MaxAge: 30 ngày
HttpOnly: true
Path: /
```

## 📧 Email Configuration

**Environment Variables**:
```
SMTP_HOST=smtp-relay.brevo.com
SMTP_PORT=587
SMTP_USERNAME=your-api-key
SMTP_PASSWORD=your-api-key
FROM_EMAIL=noreply@yourdomain.com
```

**Email Templates**:
1. OTP Registration: 6-digit code, 10 phút expiry
2. Reset Password: Link với token, 1 giờ expiry

## 🗄️ Database Operations

**Order Status Flow**:
```
PENDING → CONFIRMED → SHIPPING → DELIVERED
                   ↓
                CANCELLED
```

**Shipper Order States**:
- `CONFIRMED`: Đơn chờ shipper nhận (shipper = null)
- `SHIPPING`: Đơn đang giao (shipper = shipperId)
- `DELIVERED`: Đơn đã giao (shipper = shipperId, deliveredDate = timestamp)

## 🎨 JSP Best Practices

1. **JSTL Tags**: Sử dụng `<c:if>`, `<c:choose>`, `<c:forEach>` thay vì scriptlet
2. **EL Expressions**: `${param.email}`, `${sessionScope.user}`, `${order.orderId}`
3. **Context Path**: Luôn dùng `${pageContext.request.contextPath}` cho URLs
4. **Form Method**: POST cho actions, GET cho navigation
5. **Hidden Fields**: Dùng để truyền data giữa các requests

## 🔄 Redirect vs Forward

**Redirect** (response.sendRedirect):
- Tạo request mới
- URL thay đổi
- Dùng cho: Login success, form submit success
- Session attributes được giữ lại

**Forward** (request.getRequestDispatcher().forward):
- Cùng request
- URL không đổi
- Dùng cho: Show JSP, form validation error
- Request attributes được giữ lại

## 📱 Mobile Support

**Bottom Navigation**:
- Hiển thị trên mobile (< 768px)
- Fixed position ở bottom
- 3 tabs: Đơn hàng, Đang giao, Lịch sử

**Responsive Design**:
- Desktop: Sidebar + Content
- Mobile: Bottom Nav + Content
- Touch-friendly buttons (min 44px)

---

# KẾT LUẬN

Tài liệu này mô tả chi tiết luồng hoạt động của:
- **AUTH**: Login, Register (với OTP), Forgot Password, Reset Password, Logout
- **SHIPPER**: View Orders, Accept Order, Delivering, Complete Order, History

Mỗi luồng được giải thích theo thứ tự:
1. User action (click, submit form)
2. Servlet xử lý (doGet/doPost)
3. Service layer (business logic)
4. Database operations
5. Response (redirect/forward)
6. JSP hiển thị

Tất cả code đều bám sát implementation thực tế trong project.

