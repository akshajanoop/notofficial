@echo off
echo Starting Movie Ticket Booking System...
java -version >nul 2>&1
if ERRORLEVEL 1 (
    echo [ERROR] Java is not installed or not found on your system PATH!
    pause
    exit /B 1
)

echo Compiling and running Spring Boot with Java...
echo Opening: http://localhost:8080
echo.
