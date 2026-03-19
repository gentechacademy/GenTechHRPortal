@echo off
echo ========================================
echo    HR PORTAL - RESTARTING
echo ========================================
echo.

set BACKEND_DIR=C:\GitHubDeskTop\GenTech_HR\GenTechHRPortal\backend
set FRONTEND_DIR=C:\GitHubDeskTop\GenTech_HR\GenTechHRPortal\frontend

:: Step 1: Stop everything
echo [1/5] Stopping existing services...
taskkill /F /IM java.exe 2>nul >nul
taskkill /F /IM node.exe 2>nul >nul
timeout /t 3 /nobreak >nul
echo      Done.
echo.

:: Step 2: Verify files exist
echo [2/5] Checking files...
if not exist "%BACKEND_DIR%\target\hr-portal-1.0.0.jar" (
    echo      ERROR: Backend JAR not found!
    echo      Build with: cd backend ^& mvn clean package -DskipTests
    pause
    exit /b 1
)
echo      Backend JAR: OK

if not exist "%FRONTEND_DIR%\node_modules" (
    echo      ERROR: node_modules not found!
    echo      Install with: cd frontend ^& npm install
    pause
    exit /b 1
)
echo      Frontend deps: OK
echo.

:: Step 3: Start Backend
echo [3/5] Starting Backend...
start "HR-BACKEND" cmd /c "cd /d "%BACKEND_DIR%" && java -jar target\hr-portal-1.0.0.jar --spring.profiles.active=dev"
echo      Waiting for backend (about 30 seconds)...

set /a attempts=0
:CHECK_BACKEND
set /a attempts+=1
timeout /t 2 /nobreak >nul

powershell -Command "try { Invoke-WebRequest -Uri 'http://localhost:8081' -TimeoutSec 2 | Out-Null; exit 0 } catch { if ($_.Exception.Response) { exit 0 } else { exit 1 } }" 2>nul

if %errorlevel%==0 (
    echo      Backend is READY!
    goto BACKEND_READY
)

if !attempts! GEQ 30 (
    echo      ERROR: Backend timeout!
    pause
    exit /b 1
)

goto CHECK_BACKEND

:BACKEND_READY
echo.

:: Step 4: Start Frontend
echo [4/5] Starting Frontend...
start "HR-FRONTEND" cmd /c "cd /d "%FRONTEND_DIR%" && npm start"
echo      Waiting for frontend (about 15 seconds)...
timeout /t 15 /nobreak >nul
echo      Frontend started.
echo.

:: Step 5: Done
echo [5/5] Opening browser...
echo.
echo ========================================
echo    RESTART COMPLETE!
echo ========================================
echo.
echo  Backend:  http://localhost:8081
echo  Frontend: http://localhost:3000
echo.
echo  Login Credentials:
echo    Admin:     admin       / admin123
echo    Developer: developer1  / developer123
echo.

start http://localhost:3000

echo  Services are running in separate windows.
echo  Press any key to close this window...
pause >nul
