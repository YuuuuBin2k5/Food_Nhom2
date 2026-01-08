# Hướng dẫn Deploy FoodRescue

## Cấu hình Base URL

Khi deploy lên production, bạn cần set environment variable `APP_BASE_URL` để application biết base URL của server.

### Cách 1: Set Environment Variable trên Server

**Linux/Mac:**
```bash
export APP_BASE_URL=https://yourdomain.com/server
```

**Windows:**
```cmd
set APP_BASE_URL=https://yourdomain.com/server
```

**Tomcat (setenv.sh hoặc setenv.bat):**
```bash
# Linux/Mac - setenv.sh
export CATALINA_OPTS="$CATALINA_OPTS -DAPP_BASE_URL=https://yourdomain.com/server"

# Windows - setenv.bat
set CATALINA_OPTS=%CATALINA_OPTS% -DAPP_BASE_URL=https://yourdomain.com/server
```

### Cách 2: Set trong Docker

```dockerfile
ENV APP_BASE_URL=https://yourdomain.com/server
```

hoặc trong docker-compose.yml:
```yaml
environment:
  - APP_BASE_URL=https://yourdomain.com/server
```

### Cách 3: Set trong Cloud Platform

**Heroku:**
```bash
heroku config:set APP_BASE_URL=https://yourapp.herokuapp.com
```

**AWS Elastic Beanstalk:**
Thêm vào Configuration > Software > Environment properties

**Google Cloud:**
```bash
gcloud run services update SERVICE_NAME --set-env-vars APP_BASE_URL=https://yourapp.run.app
```

## Lưu ý

- Nếu không set `APP_BASE_URL`, application sẽ tự động detect từ HTTP request
- Trong development (localhost), không cần set environment variable
- Base URL không nên có trailing slash (/)
- Đảm bảo context path đúng (ví dụ: `/server` nếu deploy với context path là server)

## Kiểm tra

Sau khi deploy, test chức năng "Quên mật khẩu" để đảm bảo link trong email đúng.


---

## Cấu hình Email (SMTP)

Application sử dụng SMTP để gửi email (OTP đăng ký, reset password). Cần set các environment variables sau:

### Environment Variables Required

```bash
SMTP_HOST=smtp-relay.brevo.com
SMTP_PORT=2525
SMTP_USERNAME=your-smtp-key-here
SMTP_PASSWORD=your-smtp-key-here
FROM_EMAIL=your-verified-email@domain.com
```

**⚠️ LƯU Ý VỀ PORT:**
- **Port 587**: STARTTLS (thường bị block trên một số cloud platform)
- **Port 2525**: Alternative STARTTLS (ít bị block hơn) ✅ **KHUYẾN NGHỊ**
- **Port 465**: SSL/TLS (cũ hơn, ít dùng)

Nếu port 587 không hoạt động trên Render, hãy đổi sang port 2525.

### ⚠️ LƯU Ý QUAN TRỌNG VỀ BREVO SMTP

1. **SMTP API Key (không phải Master API Key)**
   - Đăng nhập Brevo → Settings → SMTP & API
   - Tab "SMTP" → Click "Generate a new SMTP key"
   - Copy key (dạng: `xsmtpsib-xxxxxxxxxxxxx`)
   - Dùng key này cho cả `SMTP_USERNAME` và `SMTP_PASSWORD`

2. **Verify FROM_EMAIL**
   - Vào Brevo → Senders → Add a sender
   - Nhập email và verify qua link gửi đến email đó
   - Chỉ email đã verify mới gửi được

3. **Quota**
   - Free plan: 300 emails/day
   - Check usage: Brevo Dashboard → Statistics

4. **Alternative Ports** (nếu 587 bị block)
   - Port 465 (SSL)
   - Port 2525 (Alternative)

### Cấu hình trên Render.com

1. Vào Render Dashboard → Your Service
2. Tab "Environment" → Add Environment Variable
3. Thêm từng biến:
   ```
   SMTP_HOST = smtp-relay.brevo.com
   SMTP_PORT = 2525
   SMTP_USERNAME = xsmtpsib-your-key-here
   SMTP_PASSWORD = xsmtpsib-your-key-here
   FROM_EMAIL = daonguyennhatanh0910@gmail.com
   ```
4. Click "Save Changes"
5. Service sẽ tự động restart

### Test Email Configuration

Sau khi deploy, truy cập:
```
https://your-app.onrender.com/test-email?to=your-email@example.com
```

Trang này sẽ:
- Hiển thị status của từng environment variable (✓ Set / ✗ Not set)
- Thử gửi email test
- Hiển thị error chi tiết nếu có

### Troubleshooting

**Lỗi: "SMTP_HOST chưa được cấu hình"**
- Environment variables chưa được set
- Hoặc service chưa restart sau khi set

**Lỗi: "Authentication failed"**
- SMTP_USERNAME/PASSWORD sai
- Đảm bảo dùng SMTP API Key (không phải Master API Key)

**Lỗi: "Sender not verified"**
- FROM_EMAIL chưa được verify trong Brevo
- Vào Brevo → Senders → Verify email

**Email không gửi được**
- Check Brevo quota (300 emails/day cho free plan)
- Check Brevo logs: Dashboard → Logs
- Check Render logs: Dashboard → Logs tab

