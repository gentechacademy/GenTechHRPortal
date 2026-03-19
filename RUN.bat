@echo off
echo ========================================
echo  HR PORTAL LAUNCHER
echo ========================================
echo.
echo 1. Starting Backend...
start "BACKEND" cmd /k "cd /d C:\GitHubDeskTop\GenTech_HR\GenTechHRPortal\backend && java -jar target\hr-portal-1.0.0.jar --spring.profiles.active=dev"
echo.
echo Wait for 'Started HrPortalApplication' message, then
echo.
pause

echo.
echo 2. Starting Frontend...
start "FRONTEND" cmd /k "cd /d C:\GitHubDeskTop\GenTech_HR\GenTechHRPortal\frontend && npm start"
echo.
echo Wait for 'Compiled successfully!' then
echo.
pause

echo.
echo 3. Opening Browser...
start http://localhost:3000
echo.
echo Done! Login: developer1 / developer123
echo.
pause
