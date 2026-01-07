# TÀI LIỆU CHI TIẾT: AUTH & SHIPPER MODULE

## MỤC LỤC
1. [AUTHENTICATION MODULE](#1-authentication-module)
2. [SHIPPER MODULE](#2-shipper-module)
3. [DATABASE ENTITIES](#3-database-entities)
4. [SECURITY](#4-security)

---

## 1. AUTHENTICATION MODULE

### 1.1 Tổng quan kiến trúc

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   JSP Pages     │────▶│    Servlets     │────▶│    Services     │
│  (View Layer)   │     │  (Controller)   │     │ (Business Logic)│
└─────────────────┘     └─────────────────┘     └─────────────────┘
                                                        │
                                                        ▼
                                                ┌─────────────────┐
                                                │   Database      │
                                                │  (PostgreSQL)   │
                                                └─────────────────┘
```

### 1.2 Files liên quan

| File | Loại | Mô tả |
|------|------|-------|
| `LoginPageServlet.java` | Servlet | Xử lý đăng nhập |
| `RegisterPageServlet.java` | Servlet | Xử lý đăng ký |
| `LogoutServlet.java` | Servlet | Xử lý đăng xuất |
| `OtpServlet.java` | Servlet | Gửi OTP xác thực email |
| `AuthService.java` | Service | Logic xác thực |
| `OtpService.java` | Service | Quản lý OTP |
| `PasswordUtil.java` | Utility | Hash/verify password |
| `MailUtil.java` | Utility | Gửi email |
| `login.jsp` | JSP | Trang đăng nhập |
| `register.jsp` | JSP | Trang đăng ký |

---

### 1.3 CHI TIẾT TỪNG COMPONENT

#### 1.3.1 LoginPageServlet.java

**URL Pattern:** `/login`

**Annotations:**
```java
@WebServlet(name = "LoginPageServlet", urlPatterns = {"/login"})
```

**Phương thức:**

| Method | Chức năng |
|--------|-----------|
| `doGet()` | Hiển thị trang login, kiểm tra auto-login từ cookie |
| `doPost()` | Xử lý form đăng nhập |
| `determineRole(User)` | Xác định role từ instance type |
| `redirectByRole(request, response, role)` | Redirect theo role |

**Flow đăng nhập (doPost):**
```
1. Lấy email, password từ form
2. Gọi authService.login(email, password)
3. Invalidate session cũ (chống Session Fixation Attack)
4. Tạo session mới
5. Lưu user, userId, role vào session
6. Xử lý "Remember Me" cookie (nếu có)
7. Redirect theo role
```

**Remember Me Cookie:**
- Tên cookie: `rememberToken`
- Thời gian sống: 30 ngày
- Format: `userId:randomToken`
- Flags: `HttpOnly = true` (bảo mật)

**Redirect theo Role:**
| Role | Redirect URL |
|------|--------------|
| ADMIN | `/admin/statistics` |
| SELLER | `/seller/dashboard` |
| SHIPPER | `/shipper/orders` |
| BUYER | `/` (home) |

---

#### 1.3.2 RegisterPageServlet.java

**URL Pattern:** `/register`

**Flow đăng ký:**
```
1. Validate input (fullName, email, password, phone, OTP)
2. Verify OTP qua otpService.verifyOtp()
3. Gọi authService.register()
4. Redirect về /login?registered=true
```

**Validation rules:**
- fullName: required
- email: required
- password: min 6 ký tự
- confirmPassword: phải khớp password
- phone: required
- otp: required, phải đúng

---

#### 1.3.3 AuthService.java

**Các phương thức:**

| Method | Parameters | Return | Mô tả |
|--------|------------|--------|-------|
| `login(email, password)` | String, String | User | Xác thực đăng nhập |
| `register(fullName, email, password, phone, role, shopName)` | String x6 | void | Đăng ký user mới |
| `forgotPassword(email)` | String | void | Gửi email reset password |
| `resetPassword(token, newPassword)` | String, String | boolean | Đổi mật khẩu |
| `getUserById(userId)` | Long | User | Lấy user theo ID |
| `findUserByEmail(em, email)` | EntityManager, String | User | Tìm user theo email |

**Chi tiết login():**
```java
public User login(String email, String password) throws Exception {
    // 1. Tìm user trong 4 bảng: sellers, buyers, shippers, admins
    User user = findUserByEmail(em, email);
    
    // 2. Kiểm tra tồn tại
    if (user == null) throw new Exception("Email không tồn tại");
    
    // 3. Verify password bằng BCrypt
    if (!PasswordUtil.verify(password, user.getPassword())) 
        throw new Exception("Sai mật khẩu");
    
    // 4. Kiểm tra banned
    if (user.isBanned()) 
        throw new Exception("Tài khoản của bạn đã bị khóa");
    
    return user;
}
```

**Chi tiết register():**
```java
public void register(...) throws Exception {
    // 1. Kiểm tra email đã tồn tại
    // 2. Tạo entity theo role (Buyer/Seller/Shipper)
    // 3. Hash password bằng BCrypt
    // 4. Set các field
    // 5. Persist vào database
}
```

**findUserByEmail() - Tìm trong 4 bảng:**
```java
private User findUserByEmail(EntityManager em, String email) {
    // Thứ tự tìm: Seller -> Buyer -> Shipper -> Admin
    // Dùng JPQL query cho từng entity
    // Return null nếu không tìm thấy
}
```

---

#### 1.3.4 OtpService.java

**Cơ chế lưu trữ:** In-memory `ConcurrentHashMap`

**Thời gian hết hạn:** 10 phút

**Phương thức:**

| Method | Mô tả |
|--------|-------|
| `generateOtp(email)` | Tạo OTP 6 số, gửi email |
| `verifyOtp(email, otp)` | Xác thực OTP |
| `clearOtp(email)` | Xóa OTP |

**Format OTP:** 6 chữ số (000000 - 999999)

---

#### 1.3.5 PasswordUtil.java

**Thư viện:** jBCrypt

**Phương thức:**
```java
// Hash password
public static String hash(String rawPassword) {
    return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
}

// Verify password
public static boolean verify(String raw, String hashed) {
    return BCrypt.checkpw(raw, hashed);
}
```

**Đặc điểm BCrypt:**
- Salt tự động sinh
- Cost factor mặc định: 10
- Output format: `$2a$10$...` (60 ký tự)

---

#### 1.3.6 MailUtil.java

**SMTP Configuration (Environment Variables):**
| Variable | Mô tả | Ví dụ Brevo |
|----------|-------|-------------|
| `SMTP_HOST` | SMTP server | `smtp-relay.brevo.com` |
| `SMTP_PORT` | Port | `587` |
| `SMTP_USERNAME` | Username | email |
| `SMTP_PASSWORD` | Password/Key | SMTP key |
| `FROM_EMAIL` | Sender email | verified email |

**Phương thức:**
```java
public static void send(String to, String subject, String content)
```

**Properties:**
- `mail.smtp.auth`: true
- `mail.smtp.starttls.enable`: true

---

### 1.4 Session Management

**Session Attributes:**
| Attribute | Type | Mô tả |
|-----------|------|-------|
| `user` | User | Object user đã đăng nhập |
| `userId` | String | ID của user |
| `role` | String | ADMIN/SELLER/BUYER/SHIPPER |
| `rememberToken` | String | Token cho Remember Me |

**Session Timeout:**
- Mặc định: 30 phút
- Với Remember Me: 30 ngày

---

## 2. SHIPPER MODULE

### 2.1 Tổng quan

```
┌─────────────────────────────────────────────────────────────┐
│                     SHIPPER WORKFLOW                        │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  [Đơn có sẵn]  ──▶  [Nhận đơn]  ──▶  [Đang giao]  ──▶  [Hoàn thành]
│   (CONFIRMED)       (accept)        (SHIPPING)       (DELIVERED)
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 Files liên quan

| File | Loại | URL Pattern | Mô tả |
|------|------|-------------|-------|
| `ShipperOrdersServlet.java` | Servlet | `/shipper/orders` | Danh sách đơn có sẵn |
| `ShipperDeliveringServlet.java` | Servlet | `/shipper/delivering` | Đơn đang giao |
| `ShipperHistoryServlet.java` | Servlet | `/shipper/history` | Lịch sử giao hàng |
| `ShipperActionServlet.java` | Servlet | `/shipper/action` | Xử lý actions |
| `OrderService.java` | Service | - | Logic đơn hàng |
| `Shipper.java` | Entity | - | Entity shipper |
| `orders.jsp` | JSP | - | UI đơn có sẵn |
| `delivering.jsp` | JSP | - | UI đang giao |
| `history.jsp` | JSP | - | UI lịch sử |

---

### 2.3 CHI TIẾT TỪNG COMPONENT

#### 2.3.1 ShipperOrdersServlet.java

**URL:** `/shipper/orders`

**Chức năng:** Hiển thị danh sách đơn hàng có sẵn (CONFIRMED)

**Flow:**
```
1. Kiểm tra session (phải đăng nhập)
2. Kiểm tra role (phải là SHIPPER)
3. Lấy danh sách orders từ OrderService
4. Tính toán stats (available, shipping, delivered)
5. Kiểm tra shipper có đơn đang giao không
6. Forward đến orders.jsp
```

**Request Attributes:**
| Attribute | Type | Mô tả |
|-----------|------|-------|
| `orders` | List<Order> | Đơn CONFIRMED |
| `availableOrders` | long | Số đơn có sẵn |
| `shippingOrders` | long | Số đơn đang giao |
| `deliveredOrders` | long | Số đơn đã giao |
| `totalEarnings` | double | Tổng thu nhập |
| `hasActiveDelivery` | boolean | Có đơn đang giao? |
| `user` | User | User hiện tại |

---

#### 2.3.2 ShipperDeliveringServlet.java

**URL:** `/shipper/delivering`

**Chức năng:** Hiển thị đơn hàng đang giao

**Flow:**
```
1. Kiểm tra session & role
2. Tìm đơn có status=SHIPPING và shipper=current user
3. Forward đến delivering.jsp
```

**Request Attributes:**
| Attribute | Type | Mô tả |
|-----------|------|-------|
| `currentOrder` | Order | Đơn đang giao (hoặc null) |
| `user` | User | User hiện tại |

---

#### 2.3.3 ShipperHistoryServlet.java

**URL:** `/shipper/history`

**Chức năng:** Hiển thị lịch sử đơn đã giao

**Flow:**
```
1. Kiểm tra session & role
2. Lọc đơn có status=DELIVERED và shipper=current user
3. Forward đến history.jsp
```

**Request Attributes:**
| Attribute | Type | Mô tả |
|-----------|------|-------|
| `deliveredOrders` | List<Order> | Đơn đã giao |
| `user` | User | User hiện tại |

---

#### 2.3.4 ShipperActionServlet.java

**URL:** `/shipper/action`

**Method:** POST only

**Parameters:**
| Param | Mô tả |
|-------|-------|
| `action` | `accept` hoặc `complete` |
| `orderId` | ID đơn hàng |

**Actions:**

**1. accept - Nhận đơn:**
```
- Kiểm tra shipper chưa có đơn đang giao
- Cập nhật order.status = SHIPPING
- Gán order.shipper = current shipper
- Tạo UserLog (SHIPPER_ACCEPT_ORDER)
- Redirect đến /shipper/delivering
```

**2. complete - Hoàn thành:**
```
- Cập nhật order.status = DELIVERED
- Tạo UserLog (SHIPPER_COMPLETE_ORDER)
- Redirect đến /shipper/orders
```

---

#### 2.3.5 OrderService.java (Shipper methods)

**Phương thức liên quan Shipper:**

| Method | Mô tả |
|--------|-------|
| `getOrdersForShipper(shipperId)` | Lấy đơn cho shipper (CONFIRMED + đơn của shipper) |
| `getOrdersByShipper(shipperId)` | Lấy đơn theo shipper ID |
| `updateOrderStatus(orderId, status, shipperId)` | Cập nhật status với shipper |
| `shipperHasActiveDelivery(shipperId)` | Kiểm tra có đơn đang giao |
| `assignShipper(orderId, shipperId)` | Gán shipper cho đơn |

**getOrdersForShipper() - JPQL Query:**
```sql
SELECT DISTINCT o FROM Order o 
LEFT JOIN FETCH o.payment 
LEFT JOIN FETCH o.orderDetails od 
LEFT JOIN FETCH od.product p 
LEFT JOIN FETCH p.seller 
LEFT JOIN FETCH o.buyer 
LEFT JOIN FETCH o.shipper 
WHERE o.status = :confirmed 
   OR (o.shipper.userId = :shipperId 
       AND (o.status = :shipping OR o.status = :delivered))
ORDER BY o.orderDate DESC
```

**Giải thích:** Lấy tất cả đơn CONFIRMED (ai cũng thấy) + đơn SHIPPING/DELIVERED của shipper này

---

#### 2.3.6 Shipper.java (Entity)

**Table:** `shippers`

**Kế thừa:** `User` (MappedSuperclass)

**Fields riêng:**
| Field | Type | Column | Mô tả |
|-------|------|--------|-------|
| `isAvailable` | boolean | `is_available` | Trạng thái sẵn sàng |
| `assignedOrders` | List<Order> | - | Đơn được gán (OneToMany) |

**Relationships:**
```java
@OneToMany(mappedBy = "shipper", fetch = FetchType.LAZY)
private List<Order> assignedOrders;
```

**Constructor:**
```java
public Shipper() {
    super();
    this.role = Role.SHIPPER;
    this.isAvailable = true;
    this.assignedOrders = new ArrayList<>();
}
```

---

### 2.4 Order Status Flow

```
┌──────────┐    Seller     ┌───────────┐   Shipper    ┌──────────┐   Shipper    ┌───────────┐
│ PENDING  │──────────────▶│ CONFIRMED │─────────────▶│ SHIPPING │─────────────▶│ DELIVERED │
└──────────┘   confirms    └───────────┘   accepts    └──────────┘  completes   └───────────┘
     │                           │
     │                           │
     ▼                           ▼
┌───────────┐              ┌───────────┐
│ CANCELLED │              │ CANCELLED │
└───────────┘              └───────────┘
```

---

## 3. DATABASE ENTITIES

### 3.1 User (MappedSuperclass)

**Không có table riêng** - các subclass có table riêng

**Fields:**
| Field | Type | Column | Constraints |
|-------|------|--------|-------------|
| userId | String | user_id | PK, UUID |
| fullName | String | full_name | NOT NULL |
| email | String | email | NOT NULL, UNIQUE |
| password | String | password | NOT NULL (BCrypt hash) |
| phoneNumber | String | phone_number | - |
| address | String | address | max 200 chars |
| role | Role (enum) | role | ADMIN/SELLER/BUYER/SHIPPER |
| isBanned | boolean | is_banned | default false |
| createdDate | Date | created_date | auto |

### 3.2 Role (Enum)

```java
public enum Role {
    ADMIN,
    SELLER,
    BUYER,
    SHIPPER
}
```

### 3.3 PasswordResetToken

**Table:** `password_reset_tokens`

| Field | Type | Column | Mô tả |
|-------|------|--------|-------|
| id | Long | id | PK, auto increment |
| token | String | token | UNIQUE, UUID |
| createdAt | Date | created_at | Thời điểm tạo |
| expiredAt | Date | expired_at | Thời điểm hết hạn (15 phút) |
| used | boolean | used | Đã sử dụng chưa |
| userId | String | user_id | FK đến user |
| userType | String | user_type | ADMIN/SELLER/BUYER/SHIPPER |

---

## 4. SECURITY

### 4.1 Password Security

- **Algorithm:** BCrypt
- **Salt:** Auto-generated
- **Cost factor:** 10 (default)
- **Storage:** 60-character hash

### 4.2 Session Security

- **Session Fixation Prevention:** Invalidate old session trước khi tạo mới
- **Session Timeout:** 30 phút (mặc định), 30 ngày (remember me)
- **HttpOnly Cookie:** Remember token không thể truy cập từ JavaScript

### 4.3 Authentication Flow

```
┌─────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐
│ Request │────▶│ Servlet │────▶│ Session │────▶│ Forward │
└─────────┘     └────┬────┘     │  Check  │     │   or    │
                     │          └────┬────┘     │Redirect │
                     │               │          └─────────┘
                     │          ┌────▼────┐
                     │          │  Role   │
                     │          │  Check  │
                     │          └─────────┘
                     │
                ┌────▼────┐
                │ Login   │
                │ Redirect│
                └─────────┘
```

### 4.4 Authorization (Role-based)

| URL Pattern | Allowed Roles |
|-------------|---------------|
| `/admin/*` | ADMIN |
| `/seller/*` | SELLER |
| `/shipper/*` | SHIPPER |
| `/buyer/*`, `/` | BUYER |
| `/login`, `/register` | Anonymous |

### 4.5 OTP Security

- **Length:** 6 digits
- **Expiry:** 10 minutes
- **Storage:** In-memory (ConcurrentHashMap)
- **One-time use:** Xóa sau khi verify thành công

---

## 5. PWA (Progressive Web App) - Shipper Only

### 5.1 Files

| File | Mô tả |
|------|-------|
| `manifest.json` | PWA manifest |
| `sw.js` | Service Worker |
| `pwa-head.jsp` | Include trong <head> |
| `pwa-script.jsp` | Include trước </body> |
| `StaticResourceServlet.java` | Serve static files |

### 5.2 Manifest Configuration

```json
{
  "name": "FreshSave Shipper",
  "short_name": "Shipper",
  "start_url": "./shipper/orders",
  "display": "standalone",
  "theme_color": "#4CAF50",
  "icons": [...]
}
```

### 5.3 Service Worker Strategy

- **Install:** Skip waiting
- **Fetch:** Network first, fallback to cache
- **Cache:** Static files only (CSS, JS, images)

---

## 6. TỔNG KẾT

### 6.1 Tech Stack

| Layer | Technology |
|-------|------------|
| Frontend | JSP, CSS, JavaScript |
| Backend | Java Servlet, JPA/Hibernate |
| Database | PostgreSQL (Supabase) |
| Password | BCrypt |
| Email | Jakarta Mail (Brevo SMTP) |
| PWA | Service Worker, Web App Manifest |

### 6.2 Design Patterns

- **MVC:** Servlet (Controller), JSP (View), Entity (Model)
- **Service Layer:** Business logic tách riêng
- **Repository Pattern:** JPA EntityManager

### 6.3 Key Features

**Auth:**
- Multi-role authentication (4 roles)
- Remember Me với secure cookie
- OTP email verification
- Password reset via email
- BCrypt password hashing
- Session fixation prevention

**Shipper:**
- Real-time order list
- Single active delivery constraint
- Order status workflow
- Action logging (UserLog)
- PWA support for mobile
