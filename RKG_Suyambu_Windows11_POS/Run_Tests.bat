@echo off
title RKG Suyambu POS - Automated Integration & Regression Test Runner
color 0A
echo ===============================================================
echo   EXECUTING RKG SUYAMBU POS AUTOMATED TEST SUITE...
echo ===============================================================

powershell -ExecutionPolicy Bypass -Command "& { Add-Type -ReferencedAssemblies 'System.Windows.Forms','System.Drawing','System.Data','System.Management' -Path '%~dp0RKG_Suyambu_Billing_Universal.cs','%~dp0Test_RKG_Suyambu_POS.cs'; [RKG_Suyambu_Billing.Tests.TestProgram]::Main(@()) }"

echo.
pause
