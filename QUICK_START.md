# ⚡ Quick Start - Chạy Backend không cần NetBeans

## 🎯 Mục tiêu
Chạy backend server mà không cần mở NetBeans

---

## 📋 Bước 1: Cài Maven (Chỉ cần làm 1 lần)

### Windows - Cách 1: Chocolatey (Khuyến nghị)
```powershell
# Mở PowerShell as Administrator
choco install maven -y

# Verify
mvn -version
```

### Windows - Cách 2: Manual
1. Download: https://maven.apache.org/download.cgi
2. Giải nén vào: `C:\Program Files\Apache\maven`
3. Thêm vào PATH:
   - Windows Search → "Environment Variables"
   - System Variables → Path → Edit
   - Add: `C:\Program Files\Apache\maven\bin`
4. Restart terminal và test: `mvn -version`

---

## 🚀 Bước 2: Chạy Server

```bash
# Di chuyển vào thư mục server
cd server

# Build project (lần đầu sẽ download dependencies)
mvn clean install

# Chạy server
mvn tomcat7:run
```

**Server sẽ chạy tại**: http://localhost:8080/server/api/

---

## 🛑 Dừng Server

Nhấn `Ctrl + C` trong terminal

---

## ✅ Test Server

### Test API
```bash
# Test health check
curl http://localhost:8080/server/api/categories

# Hoặc mở browser
http://localhost:8080/server/api/categories
```

### Test với Frontend
```bash
# Terminal 1: Backend
cd server
mvn tomcat7:run

# Terminal 2: Frontend
cd client
npm run dev
```

Mở browser: http://localhost:5173

---

## 🐛 Troubleshooting

### ❌ Lỗi: `mvn: command not found`
**Nguyên nhân**: Chưa cài Maven hoặc chưa thêm vào PATH

**Giải pháp**:
```powershell
# Check PATH
echo $env:PATH

# Thêm Maven vào PATH (temporary)
$env:PATH += ";C:\Program Files\Apache\maven\bin"

# Verify
mvn -version
```

### ❌ Lỗi: Port 8080 already in use
**Nguyên nhân**: Có process khác đang dùng port 8080

**Giải pháp**:
```powershell
# Tìm process
netstat -ano | findstr :8080

# Kill process (thay <PID> bằng số thực tế)
taskkill /PID <PID> /F
```

### ❌ Lỗi: Database connection failed
**Nguyên nhân**: Không kết nối được PostgreSQL

**Giải pháp**: Check file `server/src/main/resources/META-INF/persistence.xml`
```xml
<property name="jakarta.persistence.jdbc.url" value="jdbc:postgresql://..."/>
<property name="jakarta.persistence.jdbc.user" value="..."/>
<property name="jakarta.persistence.jdbc.password" value="..."/>
```

### ❌ Lỗi: Java version mismatch
**Nguyên nhân**: Đang dùng Java 23 nhưng project cần Java 17

**Giải pháp**:
```powershell
# Cài Java 17
choco install openjdk17 -y

# Set JAVA_HOME
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.x-hotspot"

# Verify
java -version
```

---

## 📝 Các lệnh Maven hữu ích

```bash
# Clean project
mvn clean

# Compile code
mvn compile

# Run tests
mvn test

# Build WAR file
mvn package

# Skip tests
mvn clean install -DskipTests

# Verbose output
mvn clean install -X

# Update dependencies
mvn clean install -U
```

---

## 🎯 Development Workflow

### Workflow thông thường:
```bash
# 1. Start backend
cd server
mvn tomcat7:run

# 2. Start frontend (terminal mới)
cd client
npm run dev

# 3. Code changes
# - Backend: Ctrl+C → mvn tomcat7:run
# - Frontend: Auto reload (HMR)

# 4. Stop
# Ctrl+C trong cả 2 terminals
```

### Hot reload backend (không cần restart):
```bash
# Terminal 1: Run server
mvn tomcat7:run

# Terminal 2: Rebuild khi có thay đổi
mvn compile
```

---

## 📊 Logs

### Backend logs
```bash
# Logs hiện trực tiếp trong terminal khi chạy
mvn tomcat7:run

# Để xem SQL queries, check persistence.xml:
<property name="hibernate.show_sql" value="true"/>
```

### Frontend logs
```bash
# Browser DevTools Console
# Hoặc terminal khi chạy npm run dev
```

---

## 🎉 Tóm tắt

### Lần đầu tiên:
```bash
# 1. Cài Maven
choco install maven -y

# 2. Build
cd server
mvn clean install

# 3. Run
mvn tomcat7:run
```

### Lần sau:
```bash
cd server
mvn tomcat7:run
```

**Đơn giản vậy thôi!** 🚀

---

## 📚 Tài liệu thêm

- Maven: https://maven.apache.org/guides/
- Tomcat Plugin: https://tomcat.apache.org/maven-plugin.html
- Chi tiết hơn: Xem file `SETUP_BACKEND.md`
