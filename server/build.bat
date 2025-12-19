@echo off
echo ========================================
echo Building Food Rescue Server
echo ========================================
echo.

cd /d "%~dp0"

echo [1/3] Cleaning previous build...
call mvn clean

echo.
echo [2/3] Compiling and packaging...
call mvn package -DskipTests

echo.
echo [3/3] Build complete!
echo.
echo WAR file location: target\server-1.0-SNAPSHOT.war
echo.
echo ========================================
echo Next steps:
echo 1. Stop Tomcat server
echo 2. Copy WAR file to Tomcat webapps folder
echo 3. Start Tomcat server
echo ========================================
echo.

pause
