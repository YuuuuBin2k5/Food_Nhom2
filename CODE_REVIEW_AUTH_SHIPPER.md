# 🔍 CODE REVIEW - AUTH & SHIPPER

> Kiểm tra toàn bộ code Auth và Shipper để tìm vấn đề, code dư thừa, hoặc cần tối ưu

---

## ✅ PHẦN 1: AUTH - ĐÁNH GIÁ TỔNG QUAN

### **1.1 LoginPageServlet.java**

#### ✅ Điểm tốt:
- Session Fixation Prevention đúng chuẩn
- Remember Me cookie với HttpOnly flag
- Đã refactor dùng `user.getRole().name()` thay vì `instanceof`

#### ⚠️ Vấn đề tìm thấy:

**1. Import dư thừa:**
```java
import com.ecommerce.entity.Admin;    // ❌ KHÔNG DÙNG
import com.ecommerce.entity.Buyer;    // ❌ KHÔNG DÙNG
import com.ecommerce.entity.Seller;   // ❌ KHÔNG DÙNG
import com.ecommerce.entity.Shipper;  // ❌ KHÔNG DÙNG
```
**Fix:** Xóa 4 import này vì đã không dùng `instanceof` nữa

**2. Cookie userId type mismatch:**
```java
String[] parts = cookieValue.split(":");
Long userId = Long.parseLong(parts[0]);  // ❌ Parse Long
User user = authService.getUserById(userId);
```
Nhưng trong User entity:
```java
protected String userId;  // ✅ userId là String!
```
**Fix:** Không cần parse Long, dùng trực tiếp String

---

### **1.2 RegisterPageServlet.java**

#### ✅ Điểm tốt:
- Validation đầy đủ
- OTP verification trước khi register
- Error handling tốt

#### ⚠️ Vấn đề:

**1. Biến `address` không dùng:**
```java
String address = request.getParameter("address");  // ❌ Lấy nhưng không dùng

// Không truyền vào authService.register()
authService.register(fullName, email, password, phone, role, shopName);
```
**Fix:** Xóa dòng này hoặc thêm address vào register()

**2. Biến `businessLicense` không dùng:**
```java
String businessLicense = request.getParameter("businessLicense");  // ❌ Không dùng
```
**Fix:** Xóa nếu không cần

---

### **1.3 OtpServlet.java**

#### ✅ Điểm tốt:
- Email validation regex đúng
- Redirect với query params để preserve form data
- Dev mode hiển thị OTP

#### ⚠️ Vấn đề:

**1. Dev OTP vẫn còn trong production:**
```java
// For development - show OTP (remove in production)
session.setAttribute("devOtp", otp);  // ⚠️ Cần remove khi deploy
```
**Fix:** Thêm check environment hoặc remove trước khi deploy

---

### **1.4 ForgotPasswordPageServlet.java**

#### ✅ Hoàn hảo!
- Dùng AppConfig.getBaseUrl() động
- Error handling tốt

---

### **1.5 ResetPasswordPageServlet.java**

#### ✅ Hoàn hảo!
- Validation đầy đủ
- Token verification

---

### **1.6 LogoutServlet.java**

#### ✅ Hoàn hảo!
- Invalidate session
- Clear cookie
- Support cả GET và POST

---

## ✅ PHẦN 2: SHIPPER - ĐÁNH GIÁA TỔNG QUAN

### **2.1 ShipperOrdersServlet.java**

#### ✅ Điểm tốt:
- Session và role validation
- Stats calculation
- hasActiveDelivery check

#### ⚠️ Vấn đề:

**1. Biến `totalEarnings` không dùng:**
```java
double totalEarnings = orders.stream()
    .filter(...)
    .mapToDouble(o -> 15000.0)
    .sum();
// ❌ Tính nhưng không set vào request attribute
```
**Fix:** Thêm `request.setAttribute("totalEarnings", totalEarnings);` hoặc xóa

**2. OrderService không final:**
```java
private OrderService orderService = new OrderService();  // ⚠️ Nên là final
```
**Fix:** Thêm `final` để consistent với các servlet khác

---

### **2.2 ShipperDeliveringServlet.java**

#### ✅ Điểm tốt:
- Logic đơn giản, rõ ràng
- Filter đúng SHIPPING order

#### ⚠️ Vấn đề:

**1. OrderService không final:**
```java
private OrderService orderService = new OrderService();  // ⚠️ Nên là final
```

---

### **2.3 ShipperHistoryServlet.java**

#### ✅ Điểm tốt:
- Filter DELIVERED orders
- Clean code

#### ⚠️ Vấn đề:

**1. OrderService không final:**
```java
private OrderService orderService = new OrderService();  // ⚠️ Nên là final
```

---

### **2.4 ShipperActionServlet.java**

#### ✅ Hoàn hảo!
- UserLog tracking
- Error handling tốt
- Switch-case clean

---

## 📊 TỔNG KẾT VẤN ĐỀ

### 🔴 **Critical (Phải fix ngay):**

1. **LoginPageServlet - Cookie userId type mismatch**
   - Parse Long nhưng userId là String
   - Có thể gây lỗi runtime

### 🟡 **Medium (Nên fix):**

2. **LoginPageServlet - Import dư thừa**
   - 4 imports không dùng (Admin, Buyer, Seller, Shipper)

3. **RegisterPageServlet - Biến không dùng**
   - `address` và `businessLicense` lấy nhưng không dùng

4. **ShipperOrdersServlet - totalEarnings không dùng**
   - Tính toán nhưng không hiển thị

### 🟢 **Low (Tùy chọn):**

5. **OtpServlet - Dev OTP**
   - Nên remove `devOtp` khi deploy production

6. **Shipper Servlets - OrderService không final**
   - Nên thêm `final` cho consistency

---

## 🛠️ KHUYẾN NGHỊ FIX

### **Priority 1 - Fix ngay:**


#### **1. Fix LoginPageServlet - Cookie userId**

```java
// TRƯỚC (SAI):
String[] parts = cookieValue.split(":");
Long userId = Long.parseLong(parts[0]);  // ❌
User user = authService.getUserById(userId);

// SAU (ĐÚNG):
String[] parts = cookieValue.split(":");
String userId = parts[0];  // ✅ userId là String
User user = authService.getUserById(userId);
```

#### **2. Xóa imports dư thừa trong LoginPageServlet**

```java
// XÓA 4 dòng này:
import com.ecommerce.entity.Admin;
import com.ecommerce.entity.Buyer;
import com.ecommerce.entity.Seller;
import com.ecommerce.entity.Shipper;
```

### **Priority 2 - Nên fix:**

#### **3. Fix RegisterPageServlet - Xóa biến không dùng**

```java
// XÓA 2 dòng này:
String address = request.getParameter("address");
String businessLicense = request.getParameter("businessLicense");

// Và xóa trong error handling:
request.setAttribute("address", address);
request.setAttribute("businessLicense", businessLicense);
```

#### **4. Fix ShipperOrdersServlet - Thêm totalEarnings**

```java
// Đã tính rồi, chỉ cần thêm:
request.setAttribute("totalEarnings", totalEarnings);
```

Hoặc xóa nếu không dùng:
```java
// XÓA đoạn này:
double totalEarnings = orders.stream()
    .filter(o -> o.getStatus() == OrderStatus.DELIVERED && 
               shipperId.equals(o.getShipper() != null ? o.getShipper().getUserId() : null))
    .mapToDouble(o -> 15000.0)
    .sum();
```

### **Priority 3 - Tùy chọn:**

#### **5. Remove Dev OTP khi deploy**

```java
// OtpServlet.java
// XÓA dòng này khi deploy production:
session.setAttribute("devOtp", otp);
```

Hoặc thêm check environment:
```java
if (System.getenv("ENVIRONMENT") == null || "development".equals(System.getenv("ENVIRONMENT"))) {
    session.setAttribute("devOtp", otp);
}
```

#### **6. Thêm final cho OrderService**

```java
// Trong ShipperOrdersServlet, ShipperDeliveringServlet, ShipperHistoryServlet:
private final OrderService orderService = new OrderService();
```

---

## 📈 ĐIỂM MẠNH CỦA CODE

### ✅ **Security:**
- Session Fixation Prevention
- HttpOnly cookies
- CSRF protection (form-based)
- Password validation
- OTP rate limiting

### ✅ **Code Quality:**
- Consistent error handling
- Good separation of concerns
- Clear naming conventions
- Proper use of services

### ✅ **User Experience:**
- Form data preservation on error
- Clear error messages (Vietnamese)
- Remember me functionality
- OTP email verification

---

## 🎯 KẾT LUẬN

**Tổng số vấn đề:** 6
- 🔴 Critical: 1
- 🟡 Medium: 3
- 🟢 Low: 2

**Đánh giá chung:** Code quality tốt, chỉ có vài vấn đề nhỏ cần fix.

**Ưu tiên fix:**
1. LoginPageServlet userId type (Critical)
2. Xóa imports dư thừa
3. Xóa biến không dùng
4. Các vấn đề khác (optional)

