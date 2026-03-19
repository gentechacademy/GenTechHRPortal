@echo off
echo ========================================
echo    HR PORTAL - STOPPING SERVICES
echo ========================================
echo.

echo Stopping Java (Backend)...
taskkill /F /IM java.exe 2>nul
if %errorlevel%==0 (
    echo    [OK] Java stopped
) else (
    echo    [INFO] No Java running
)

echo.
echo Stopping Node (Frontend)...
taskkill /F /IM node.exe 2>nul
if %errorlevel%==0 (
    echo    [OK] Node stopped
) else (
    echo    [INFO] No Node running
)

echo.
echo ========================================
echo    ALL SERVICES STOPPED
echo ========================================
timeout /t 2 >nul
