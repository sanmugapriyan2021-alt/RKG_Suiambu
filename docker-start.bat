@echo off
title Starting RKG Suyambu Docker Containers...
set "PATH=C:\Users\sanmu\AppData\Local\Programs\DockerDesktop\resources\bin;%PATH%"

echo ======================================================================
echo   RKG SUYAMBU ENTERPRISE — DOCKER CONTAINER LAUNCHER
echo   ஆர்.கே.ஜி சுயம்பு மாட்டுத்தீவனம் & ஆலை
echo ======================================================================
echo.

docker compose up --build -d

if %ERRORLEVEL% equ 0 (
    echo.
    echo ======================================================================
    echo   [+] ALL CONTAINERS STARTED SUCCESSFULLY!
    echo ======================================================================
    echo   - Public Website & Store: http://localhost (Port 80 / 8080)
    echo   - CEO Cloud Portal:       http://localhost/ceo
    echo   - Mobile Mill Hub:        http://localhost/mobile
    echo   - FastAPI Backend & Docs: http://localhost:8000/docs
    echo ======================================================================
) else (
    echo.
    echo [-] Error starting Docker containers. Please ensure Docker Desktop engine is running.
)

echo.
pause
