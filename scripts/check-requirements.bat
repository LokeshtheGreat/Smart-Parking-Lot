@echo off
setlocal

title ParkEase - Environment Check

for %%I in ("%~dp0..") do set "ROOT=%%~fI"

cd /d "%ROOT%"

echo.
echo ==================================================
echo           PARKEASE - ENVIRONMENT CHECK
echo ==================================================
echo.

set "FAILED=0"

:: Check Java runtime
where java >nul 2>&1

if %errorlevel% equ 0 (
    echo [OK] Java Runtime detected
    java -version 2>&1
) else (
    echo [MISSING] Java Runtime not found
    set "FAILED=1"
)

echo.

:: Check Java compiler
where javac >nul 2>&1

if %errorlevel% equ 0 (
    echo [OK] Java Compiler detected
    javac -version
) else (
    echo [MISSING] Java Compiler not found
    echo Install a JDK, not just a JRE.
    set "FAILED=1"
)

echo.

:: Check MySQL JDBC driver
if exist "lib\mysql-connector-j-26.7.0.jar" (
    echo [OK] MySQL JDBC Driver found
) else (
    echo [MISSING] MySQL JDBC Driver not found
    set "FAILED=1"
)

echo.

:: Check database configuration
if exist "db.properties" (
    echo [OK] Database configuration found
) else (
    echo [MISSING] db.properties not found
    echo Copy db.properties.example to db.properties
    set "FAILED=1"
)

echo.

:: Check database SQL script
if exist "database\parkease.sql" (
    echo [OK] Database setup script found
) else (
    echo [MISSING] Database setup script not found
    set "FAILED=1"
)

echo.

:: Check MySQL Windows service
powershell -NoProfile -Command ^
 "$s = Get-Service | Where-Object { $_.Name -like 'MySQL*' -or $_.DisplayName -like '*MySQL*' }; if ($s -and ($s.Status -contains 'Running')) { exit 0 } else { exit 1 }" >nul 2>&1

if %errorlevel% equ 0 (
    echo [OK] Running MySQL service detected
) else (
    echo [WARNING] Running MySQL service not detected
    echo Check whether MySQL Server is installed and running.
)

echo.
echo ==================================================

if "%FAILED%"=="1" (
    echo              SETUP INCOMPLETE
    echo ==================================================
    echo.
    echo Fix the missing requirements listed above.
    echo Then run this checker again.
    echo.
    pause
    exit /b 1
)

echo              REQUIREMENTS CHECK PASSED
echo ==================================================
echo.
echo You can proceed to run ParkEase.
echo.

pause
exit /b 0