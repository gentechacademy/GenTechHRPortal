@echo off
echo ========================================
echo    HR PORTAL - STATUS CHECK
echo ========================================
echo.

echo BACKEND (Java):
echo ----------------
tasklist /FI "IMAGENAME eq java.exe" 2>nul | find /I "java.exe" >nul
if %errorlevel%==0 (
    echo Status: RUNNING
echo.
    tasklist /FI "IMAGENAME eq java.exe" /FO TABLE | find /I "java.exe"
    echo.
    echo URL: http://localhost:8081
) else (
    echo Status: NOT RUNNING
    echo.
    echo Start with: start-all.bat
)

echo.
echo FRONTEND (Node):
echo -----------------
tasklist /FI "IMAGENAME eq node.exe" 2>nul | find /I "node.exe" >nul
if %errorlevel%==0 (
    echo Status: RUNNING
echo.
    tasklist /FI "IMAGENAME eq node.exe" /FO TABLE | find /I "node.exe"
    echo.
    echo URL: http://localhost:3000
) else (
    echo Status: NOT RUNNING
    echo.
    echo Start with: start-all.bat
)

echo.
echo ========================================
pause
