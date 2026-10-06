@echo off
title RKG SUYAMBU - Docker Hub Push Engine (Version 0.1)
set "PATH=C:\Users\sanmu\AppData\Local\Programs\DockerDesktop\resources\bin;%PATH%"

echo ======================================================================
echo   RKG SUYAMBU ENTERPRISE — DOCKER HUB DEPLOYMENT (v0.1)
echo   Pushing Customer Web App & CEO Mobile Portal to Docker Hub
echo ======================================================================
echo.

set DOCKER_USER=sanmugapriyan2021
set /p USER_INPUT="Enter Docker Hub Username [Default: %DOCKER_USER%]: "
if not "%USER_INPUT%"=="" set DOCKER_USER=%USER_INPUT%

echo.
echo [*] Target Docker Hub Repository: %DOCKER_USER%/rkg-suiambu-web:v0.1
echo [*] Building Production Web & CEO App Docker Image...
echo.

cd /d "C:\Users\sanmu\Documents\suiambu\RKG_Suyambu_Public_Website"
docker build -t %DOCKER_USER%/rkg-suiambu-web:v0.1 -t %DOCKER_USER%/rkg-suiambu-web:latest .

if %ERRORLEVEL% neq 0 (
    echo.
    echo [-] Docker build failed. Please ensure Docker Desktop is running.
    pause
    exit /b 1
)

echo.
echo [*] Logging into Docker Hub (if not logged in)...
docker login

echo.
echo [*] Pushing version v0.1 to Docker Hub...
docker push %DOCKER_USER%/rkg-suiambu-web:v0.1
docker push %DOCKER_USER%/rkg-suiambu-web:latest

echo.
echo ======================================================================
echo   [+] DOCKER HUB DEPLOYMENT COMPLETE!
echo   Image: %DOCKER_USER%/rkg-suiambu-web:v0.1
echo ======================================================================
echo.
pause
