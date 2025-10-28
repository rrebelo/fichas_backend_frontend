@echo off
setlocal enabledelayedexpansion

rem Build & push backend and frontend containers to Docker Hub

set BACKEND_IMAGE=rrebelo/cmderma-backend
set FRONTEND_IMAGE=rrebelo/cmderma-frontend
set TAG=latest

echo.
echo === Building backend (Spring Boot) ===
call mvn -B clean package
if errorlevel 1 goto :error

echo.
echo === Building frontend (Vue/Vite) ===
pushd frontend
call npm ci
if errorlevel 1 goto :error
call npm run build
if errorlevel 1 goto :error
popd

echo.
echo === Building Docker images ===
call docker compose build backend frontend
if errorlevel 1 goto :error

echo.
echo === Pushing images to Docker Hub ===
call docker compose push backend frontend
if errorlevel 1 goto :error

echo.
echo === Stopping existing stack (docker-compose.server) ===
call docker compose -f docker-compose.yml down --remove-orphans
if errorlevel 1 goto :error

echo.
echo === Removing previously deployed images ===
call docker image rm -f %BACKEND_IMAGE%:%TAG% %FRONTEND_IMAGE%:%TAG% >nul 2>&1
set IMG_REMOVE_ERROR=!ERRORLEVEL!
if !IMG_REMOVE_ERROR! neq 0 (
    echo Warning: Unable to remove one or more images. They may not exist locally.
)

echo.
echo === Deploying stack (docker-compose.server) ===
call docker compose -f docker-compose.yml up -d
if errorlevel 1 goto :error

echo.
echo Done! Images published:
echo   %BACKEND_IMAGE%:%TAG%
echo   %FRONTEND_IMAGE%:%TAG%
exit /b 0

:error
echo.
echo Build or push failed. Check the logs above.
exit /b 1
