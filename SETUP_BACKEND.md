# 🚀 Hướng dẫn Chạy Backend không cần NetBeans

## ⚠️ Vấn đề hiện tại

- ✅ Java đã cài: **Java 23** (nhưng project cần Java 17)
- ❌ Maven chưa cài: `mvn: command not found`
- ❌ Tomcat chưa có

## 📋 Yêu cầu

1. **Java 17** (project config trong pom.xml)
2. **Maven** (build tool)
3. **Tomcat 10** (servlet container)
4. **PostgreSQL** (đã có - Supabase)

---

## 🔧 Cách 1: Cài Maven và chạy với Maven Tomcat Plugin (Khuyến nghị)

### Bước 1: Cài Maven

#### Windows (Chocolatey):
```powershell
# Cài Chocolatey nếu chưa có
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Cài Maven
choco install maven -y
```

#### Windows (Manual):
1. Download Maven: https://maven.apache.org/download.cgi
2. Giải nén vào `C:\Program Files\Apache\maven`
3. Thêm vào PATH:
   - Mở System Properties → Environment Variables
   - Thêm `C:\Program Files\Apache\maven\bin` vào PATH
4. Restart terminal

#### Verify:
```bash
mvn -version
```

### Bước 2: Build và Run

```bash
# Di chuyển vào thư mục server
cd server

# Clean và build project
mvn clean install

# Chạy với Tomcat plugin
mvn tomcat7:run
```

**Lưu ý**: Cần thêm Tomcat plugin vào `pom.xml` (xem bên dưới)

---

## 🔧 Cách 2: Cài Tomcat và Deploy WAR file

### Bước 1: Cài Tomcat 10

1. Download Tomcat 10: https://tomcat.apache.org/download-10.cgi
2. Giải nén vào `C:\Program Files\Apache\tomcat10`
3. Thêm vào PATH (optional):
   ```
   C:\Program Files\Apache\tomcat10\bin
   ```

### Bước 2: Build WAR file

```bash
cd server
mvn clean package
```

Output: `server/target/server-1.0-SNAPSHOT.war`

### Bước 3: Deploy

#### Option A: Copy WAR file
```bash
# Copy WAR vào Tomcat webapps
copy target\server-1.0-SNAPSHOT.war "C:\Program Files\Apache\tomcat10\webapps\"
```

#### Option B: Rename và deploy
```bash
# Rename để URL ngắn hơn
copy target\server-1.0-SNAPSHOT.war "C:\Program Files\Apache\tomcat10\webapps\server.war"
```

### Bước 4: Start Tomcat

```bash
# Windows
cd "C:\Program Files\Apache\tomcat10\bin"
.\startup.bat

# Hoặc
.\catalina.bat run
```

### Bước 5: Access

- URL: http://localhost:8080/server/api/
- Logs: `C:\Program Files\Apache\tomcat10\logs\catalina.out`

### Stop Tomcat

```bash
.\shutdown.bat
```

---

## 🔧 Cách 3: Sử dụng Maven Wrapper (Không cần cài Maven)

Nếu project có Maven Wrapper (`mvnw` hoặc `mvnw.cmd`):

```bash
cd server

# Windows
.\mvnw.cmd clean install
.\mvnw.cmd tomcat7:run

# Linux/Mac
./mvnw clean install
./mvnw tomcat7:run
```

---

## 📝 Cấu hình cần thiết

### 1. Thêm Tomcat Plugin vào pom.xml

Mở `server/pom.xml` và thêm vào `<build><plugins>`:

```xml
<plugin>
    <groupId>org.apache.tomcat.maven</groupId>
    <artifactId>tomcat7-maven-plugin</artifactId>
    <version>2.2</version>
    <configuration>
        <port>8080</port>
        <path>/server</path>
        <contextReloadable>true</contextReloadable>
    </configuration>
</plugin>
```

### 2. Fix Java Version

Bạn có Java 23 nhưng project cần Java 17. Có 2 cách:

#### Option A: Cài Java 17 (Khuyến nghị)
```powershell
# Chocolatey
choco install openjdk17 -y

# Hoặc download từ
# https://adoptium.net/temurin/releases/?version=17
```

Sau đó set JAVA_HOME:
```powershell
# Set JAVA_HOME
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.x-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
```

#### Option B: Update pom.xml để dùng Java 23
```xml
<properties>
    <maven.compiler.source>23</maven.compiler.source>
    <maven.compiler.target>23</maven.compiler.target>
</properties>
```

---

## 🐛 Troubleshooting

### Lỗi: `mvn: command not found`
**Giải pháp**: Cài Maven (xem Bước 1)

### Lỗi: `JAVA_HOME not set`
**Giải pháp**:
```powershell
# Check JAVA_HOME
echo $env:JAVA_HOME

# Set JAVA_HOME
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
```

### Lỗi: Port 8080 already in use
**Giải pháp**:
```powershell
# Tìm process đang dùng port 8080
netstat -ano | findstr :8080

# Kill process
taskkill /PID <PID> /F

# Hoặc đổi port trong pom.xml
<port>8081</port>
```

### Lỗi: Database connection failed
**Giải pháp**: Check `persistence.xml`:
```xml
<property name="jakarta.persistence.jdbc.url" value="jdbc:postgresql://..."/>
<property name="jakarta.persistence.jdbc.user" value="..."/>
<property name="jakarta.persistence.jdbc.password" value="..."/>
```

### Lỗi: Class not found
**Giải pháp**:
```bash
# Clean và rebuild
mvn clean install -U
```

---

## 📊 So sánh các cách

| Cách | Ưu điểm | Nhược điểm |
|------|---------|------------|
| **Maven Plugin** | Nhanh, đơn giản, auto-reload | Cần cài Maven |
| **Tomcat Standalone** | Production-ready, stable | Phức tạp hơn, manual deploy |
| **Maven Wrapper** | Không cần cài Maven | Cần có mvnw trong project |

---

## 🎯 Khuyến nghị

### Cho Development:
```bash
# Cài Maven
choco install maven -y

# Thêm Tomcat plugin vào pom.xml
# (xem phần cấu hình)

# Run
cd server
mvn clean install
mvn tomcat7:run
```

### Cho Production:
```bash
# Build WAR
mvn clean package

# Deploy lên Tomcat standalone
copy target\*.war C:\tomcat\webapps\
```

---

## 🚀 Quick Start (Sau khi cài Maven)

```bash
# 1. Di chuyển vào thư mục server
cd server

# 2. Build project
mvn clean install

# 3. Run với Tomcat plugin
mvn tomcat7:run

# 4. Access
# http://localhost:8080/server/api/
```

---

## 📝 Logs và Debugging

### Maven logs
```bash
# Verbose output
mvn clean install -X

# Skip tests
mvn clean install -DskipTests
```

### Tomcat logs
```bash
# Standalone Tomcat
tail -f C:\tomcat\logs\catalina.out

# Maven plugin
# Logs hiện trực tiếp trong terminal
```

### Database logs
```xml
<!-- persistence.xml -->
<property name="hibernate.show_sql" value="true"/>
<property name="hibernate.format_sql" value="true"/>
```

---

## 🎉 Kết luận

**Cách nhanh nhất**:
1. Cài Maven: `choco install maven -y`
2. Thêm Tomcat plugin vào pom.xml
3. Run: `mvn tomcat7:run`
4. Access: http://localhost:8080/server/api/

**Không muốn cài Maven**:
1. Cài Tomcat 10
2. Build WAR với NetBeans hoặc IDE khác
3. Copy WAR vào Tomcat webapps
4. Start Tomcat

Chọn cách nào phù hợp với bạn nhất! 🚀
