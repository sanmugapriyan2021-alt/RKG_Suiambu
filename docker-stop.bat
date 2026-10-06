@echo off
title Stopping RKG Suyambu Docker Containers...
set "PATH=C:\Users\sanmu\AppData\Local\Programs\DockerDesktop\resources\bin;%PATH%"

echo ======================================================================
echo   RKG SUYAMBU ENTERPRISE — DOCKER CONTAINER SHUTDOWN
echo ======================================================================
echo.

docker compose down

echo.
echo [+] All RKG Suyambu containers stopped safely. Data is preserved in volume.
echo.
pause
