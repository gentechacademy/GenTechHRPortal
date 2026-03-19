@echo off
setlocal EnableDelayedExpansion

echo ========================================
echo    HR PORTAL - STARTING SERVICES
echo ========================================
echo.

set BACKEND_DIR=C:\GitHubDeskTop\GenTech_HR\GenTechHRPortal\backend
set FRONTEND_DIR=C:\GitHubDeskTop\GenTech_HR\GenTechHRPortal\frontend
set JAR_PATH=%BACKEND_DIR%\target\hr-portal-1.0.0.jar

:: Kill existing processes first
echo [1/4] Stopping any existing processes...
taskkill /F /IM java.exe 2>nul >nul
taskkill /F /IM node.exe 2>nul >nul
timeout /t 3 /nobreak >nul
echo      Done.
echo.

:: Check JAR exists
if not exist "%JAR_PATH%" (
    echo ERROR: Backend JAR not found at:
    echo %JAR_PATH%
    echo.
    echo Please build first:
    echo cd backend ^& mvn clean package -DskipTests
    pause
    exit /b 1
)

:: Check node_modules exists
if not exist "%FRONTEND_DIR%\node_modules" (
    echo ERROR: node_modules not found.
    echo Please run: cd frontend ^& npm install
    pause
    exit /b 1
)

:: Start Backend
echo [2/4] Starting Backend...
echo      Path: %BACKEND_DIR%
start "HR-BACKEND" cmd /c "cd /d "%BACKEND_DIR%" && java -jar target\hr-portal-1.0.0.jar --spring.profiles.active=dev"
echo      Waiting for backend to initialize...

:: Wait loop - check if port 8081 is responding
set /a attempts=0
:CHECK_BACKEND
set /a attempts+=1
timeout /t 2 /nobreak >nul

:: Try to connect to backend
echo | set /p="      Checking attempt !attempts!... "
powershell -Command "try { $r=Invoke-WebRequest -Uri 'http://localhost:8081' -TimeoutSec 2 -ErrorAction Stop; exit 0 } catch { if ($_.Exception.Response -or $_.Exception.Status -eq 7) { exit 0 } else { exit 1 } }" 2>nul

if %errorlevel%==0 (
    echo READY!
    goto BACKEND_READY
)

if !attempts! GEQ 30 (
    echo TIMEOUT!
    echo      Backend failed to start within 60 seconds.
    pause
    exit /b 1
)

goto CHECK_BACKEND

:BACKEND_READY
echo.
echo [3/4] Starting Frontend...
echo      Path: %FRONTEND_DIR%
start "HR-FRONTEND" cmd /c "cd /d "%FRONTEND_DIR%" && npm start"

echo      Waiting for frontend to compile...
timeout /t 15 /nobreak >nul

echo.
echo ========================================
echo    ALL SERVICES STARTED!
echo ========================================
echo.
echo  Backend:  http://localhost:8081
echo  Frontend: http://localhost:3000
echo.
echo  Login:
echo    Admin:     admin / admin123
echo    Developer: developer1 / developer123
echo.

:: Open browser
start http://localhost:3000

echo  Press any key to close this window...
pause >nul
