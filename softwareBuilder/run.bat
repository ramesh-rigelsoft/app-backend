@echo off
title TodoApp Launcher
color 0A

echo =====================================================
echo      Checking and freeing port 8088...
echo =====================================================

:: Kill any process using port 8088
echo Checking and freeing port 8088...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8088') do (
    echo Killing process with PID %%a using port 8088...
    taskkill /F /PID %%a >nul 2>&1
)

echo Port 8088 is now free.
echo Starting TodoApp...
echo =====================================================
echo Starting TodoApp...

:: Set local JDK path
set JAVA_EXE=%~dp0jdk\bin\java.exe

:: Run JAR in background and log output
"%JAVA_EXE%" -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -jar "%~dp0todoapp.jar" >> "%~dp0todoapp.log" 2>&1
