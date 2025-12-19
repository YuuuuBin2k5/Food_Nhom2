@echo off
echo ========================================
echo Deploying Food Rescue Server to Tomcat
echo ========================================
echo.

set TOMCAT_HOME=C:\Vo Tan Tai\Code cua Tai\NetBean\apache-tomcat-10.1.48
set WAR_FILE=%~dp0target\server-1.0-SNAPSHOT.war

if not exist "%WAR_FILE%" (
    echo ERROR: WAR file not found!
    echo Please run build.bat first.
    pause
    exit /b 1
)

echo [1/4] Stopping Tomcat...
call "%TOMCAT_HOME%\bin\shutdown.bat"
timeout /t 5 /nobreak > nul

echo.
echo [2/4] Removing old deployment...
if exist "%TOMCAT_HOME%\webapps\server" (
    rmdir /s /q "%TOMCAT_HOME%\webapps\server"
)
if exist "%TOMCAT_HOME%\webapps\server.war" (
    del /q "%TOMCAT_HOME%\webapps\server.war"
)

echo.
echo [3/4] Copying new WAR file...
copy /y "%WAR_FILE%" "%TOMCAT_HOME%\webapps\server.war"

echo.
echo [4/4] Starting Tomcat...
start "" "%TOMCAT_HOME%\bin\startup.bat"

echo.
echo ========================================
echo Deployment complete!
echo Server will be available at:
echo http://localhost:8080/server
echo.
echo WebSocket endpoint:
echo ws://localhost:8080/server/ws/notifications/{userId}
echo ========================================
echo.

pause
