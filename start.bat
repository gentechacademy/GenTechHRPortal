@echo off
echo Starting GenTech HR Portal...

echo Starting Backend (Spring Boot)...
start "GenTech Backend" cmd /k "cd backend && .\mvnw clean spring-boot:run"

echo Starting Frontend (React)...
start "GenTech Frontend" cmd /k "cd frontend && npm start"

echo.
echo Both services are starting in new windows. 
echo Please wait about 30 seconds for the backend to fully initialize.
echo.
echo ----------------------------------------
echo Backend URL:  http://localhost:8080
echo Frontend URL: http://localhost:3000
echo ----------------------------------------
echo.
echo Test Accounts:
echo - Super Admin: superadmin / superadmin123
echo - Admin: admin / admin123
echo - Employee: developer1 / developer123
echo.
pause
