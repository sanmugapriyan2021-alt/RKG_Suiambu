@echo off
:MENU
cls
color 1F
echo ==============================================================================
echo       RKG SUYAMBU ENTERPRISE — MASTER APP LAUNCHER & CONTROL CENTER
echo       ஆர்.கே.ஜி சுயம்பு மாட்டுத்தீவனம் & ஆலை மேலாண்மை தளம்
echo ==============================================================================
echo.
echo   [1]  START WINDOWS POS BILLING GUI (Instant Counter Screen - Firebase Sync)
echo   [2]  OPEN PUBLIC STORE & CATALOG (Online Products & WhatsApp Ordering)
echo   [3]  OPEN CEO EXECUTIVE DASHBOARD (Real-Time Sales, Ledgers & Stock)
echo   [4]  OPEN MOBILE POS & MILL HUB (Mobile Interface)
echo   [5]  VIEW / SHARE ANDROID APPLICATION APK (RKG_Suyambu_CEO.apk)
echo   [6]  START DOCKER CONTAINERS (Local Full-Stack Server)
echo   [7]  STOP DOCKER CONTAINERS
echo   [8]  EXIT
echo.
echo ==============================================================================
set /p choice="Select an option (1-8) and press Enter: "

if "%choice%"=="1" (
    cd /d "%~dp0RKG_Suyambu_Windows11_POS"
    start "" "New_Bill_Windows11.exe"
    goto MENU
)
if "%choice%"=="2" (
    start "" "https://sanmugapriyan2021-alt.github.io/RKG_Suiambu/"
    goto MENU
)
if "%choice%"=="3" (
    start "" "%~dp0RKG_Suyambu_Public_Website\ceo.html"
    goto MENU
)
if "%choice%"=="4" (
    start "" "%~dp0RKG_Suyambu_Public_Website\mobile.html"
    goto MENU
)
if "%choice%"=="5" (
    explorer.exe /select,"%~dp0RKG_Suyambu_CEO.apk"
    goto MENU
)
if "%choice%"=="6" (
    call "%~dp0docker-start.bat"
    goto MENU
)
if "%choice%"=="7" (
    call "%~dp0docker-stop.bat"
    goto MENU
)
if "%choice%"=="8" exit

goto MENU
