@echo off
setlocal
chcp 65001 > nul
title SustentaMed - Sistema Completo

echo ========================================================
echo   Iniciando SustentaMed - Sistema Completo Web
echo ========================================================
echo.

cd /d "%~dp0"

:: 1. Verificar pre-requisito do Java
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERRO] O comando java nao foi encontrado no sistema ou nao esta no PATH.
    echo Por favor, instale o Java JDK 17 ou superior para executar o backend.
    echo.
    pause
    exit /b 1
)

:: 2. Verificar pre-requisito do Node.js e NPM
where npm >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERRO] O comando npm nao foi encontrado no sistema ou nao esta no PATH.
    echo Por favor, instale o Node.js versao 18 ou superior para executar o frontend Web.
    echo Download oficial: https://nodejs.org/
    echo.
    pause
    exit /b 1
)

:: 3. Verificar se ja existe classe compilada do servidor
if exist "bin\com\sustentamed\app\ServidorHttp.class" goto SUBIR_SERVICOS
if exist "out\production\SustentaMed\com\sustentamed\app\ServidorHttp.class" goto SUBIR_SERVICOS
if exist "out\production\SustentaMed01\com\sustentamed\app\ServidorHttp.class" goto SUBIR_SERVICOS

echo [1/3] Classes compiladas nao encontradas. Compilando o Backend Java...
where javac >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERRO] O compilador javac nao foi encontrado no PATH para compilar o backend.
    echo Certifique-se de que o JDK esta instalado.
    echo.
    pause
    exit /b 1
)

if not exist "bin" mkdir "bin"

powershell -NoProfile -ExecutionPolicy Bypass -Command "$srcs = (Get-ChildItem -Recurse -Path src -Filter *.java).FullName; javac -encoding UTF-8 -d bin -cp 'lib/mysql-connector-j.jar' $srcs"
if %errorlevel% neq 0 (
    echo.
    echo [ERRO] Falha ao compilar o Backend Java.
    echo.
    pause
    exit /b 1
)
echo [SUCESSO] Backend Java compilado com sucesso!
echo.

:SUBIR_SERVICOS
:: 4. Iniciar Servidor HTTP REST (porta 8080) em nova janela
echo [2/3] Iniciando Servidor HTTP REST na Porta 8080...
set "CP=bin;out\production\SustentaMed;out\production\SustentaMed01;lib\mysql-connector-j.jar"
start "SustentaMed - Backend API (8080)" cmd /k "chcp 65001 > nul && title SustentaMed - Backend API (8080) && java -cp ""%CP%"" com.sustentamed.app.ServidorHttp"

:: 5. Iniciar Frontend Web (React + Vite)
echo [3/3] Preparando o Frontend Web React...
cd frontend

if not exist "node_modules" (
    echo [INFO] Instalando dependencias do frontend com npm install...
    call npm install
    if %errorlevel% neq 0 (
        echo.
        echo [ERRO] Falha ao instalar as dependencias do frontend com npm install.
        echo.
        pause
        exit /b 1
    )
)

echo.
echo ========================================================
echo   Frontend iniciando em: http://localhost:5173
echo   Backend API rodando em: http://localhost:8080
echo.
echo   O navegador sera aberto automaticamente!
echo   Para encerrar: feche esta janela e a janela do Backend.
echo ========================================================
echo.

call npm run dev -- --open
pause
