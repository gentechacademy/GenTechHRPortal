@echo off
echo ========================================
echo    HR PORTAL LAUNCHER
echo ========================================
echo.

:: Kill existing first
echo Stopping any existing processes...
taskkill /F /IM java.exe 2>nul
taskkill /F /IM node.exe 2>nul
timeout /t 2 /nobreak >nul
echo Done.
echo.

:: Start Backend
echo Starting Backend...
start "BACKEND" cmd /k "cd /d C:\GitHubDeskTop\GenTech_HR\GenTechHRPortal\backend && java -jar target\hr-portal-1.0.0.jar --spring.profiles.active=dev"
echo.
echo Backend is starting...
echo Wait for 'Started HrPortalApplication' message in the backend window.
echo.
pause

:: Start Frontend
echo.
echo Starting Frontend...
start "FRONTEND" cmd /k "cd /d C:\GitHubDeskTop\GenTech_HR\GenTechHRPortal\frontend && npm start"
echo.
echo Frontend is starting...
echo Wait for 'Compiled successfully!' message.
echo.
pause

:: Open Browser
echo.
echo Opening browser...
start http://localhost:3000
echo.
echo ========================================
echo    HR Portal is running!
echo ========================================
echo.
echo  Login: developer1 / developer123
echo.
pause
