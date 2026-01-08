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
