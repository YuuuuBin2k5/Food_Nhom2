@echo off
echo ========================================
echo Build and Deploy - Food Rescue Server
echo ========================================
echo.

cd /d "%~dp0"

echo Step 1: Building project...
call build.bat

if errorlevel 1 (
    echo.
    echo BUILD FAILED!
    pause
    exit /b 1
)

echo.
echo Step 2: Deploying to Tomcat...
call deploy.bat

echo.
echo ========================================
echo All done! Server is starting...
echo ========================================
