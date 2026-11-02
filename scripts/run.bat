@echo off
setlocal

title ParkEase - Smart Parking System

for %%I in ("%~dp0..") do set "ROOT=%%~fI"

cd /d "%ROOT%"

echo.
echo ==================================================
echo                    PARKEASE
echo               SMART PARKING SYSTEM
echo ==================================================
echo.

echo Checking environment requirements...
echo.

call "%~dp0check-requirements.bat"

if errorlevel 1 (
    echo.
    echo [ERROR] Requirements check failed.
    echo Please resolve the missing requirements first.
    pause
    exit /b 1
)

echo.
echo ==================================================
echo                  COMPILING PROJECT
echo ==================================================
echo.

if not exist "out" mkdir out

javac -cp "lib/*" -d out src/*.java

if errorlevel 1 (
    echo.
    echo [ERROR] Compilation failed.
    echo Check the Java source files for errors.
    pause
    exit /b 1
)

echo.
echo [OK] Compilation successful.

echo.
echo ==================================================
echo                 STARTING PARKEASE
echo ==================================================
echo.

java -cp "out;lib/*" Main

if errorlevel 1 (
    echo.
    echo [ERROR] ParkEase terminated unexpectedly.
)

echo.
echo ==================================================
echo                  APPLICATION CLOSED
echo ==================================================

pause