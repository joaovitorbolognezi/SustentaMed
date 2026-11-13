@echo off
setlocal
chcp 65001 > nul
title SustentaMed - Console Interativo

echo ========================================================
echo   Iniciando SustentaMed - Menu Interativo
echo ========================================================
echo.

cd /d "%~dp0"

:: 1. Verificar se o comando java esta no PATH
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERRO] O comando java nao foi encontrado no sistema ou nao esta no PATH.
    echo Por favor, instale o Java JDK 17 ou superior e tente novamente.
    echo.
    pause
    exit /b 1
)

:: 2. Verificar se ja existe classe compilada
if exist "bin\com\sustentamed\app\Main.class" goto RODAR
if exist "out\production\SustentaMed\com\sustentamed\app\Main.class" goto RODAR
if exist "out\production\SustentaMed01\com\sustentamed\app\Main.class" goto RODAR

:: Se nao existir, compilar
echo [INFO] Classes compiladas nao encontradas. Compilando o backend Java pela primeira vez...
where javac >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERRO] O compilador javac nao foi encontrado no PATH para compilar o projeto.
    echo Certifique-se de que o JDK esta instalado.
    echo.
    pause
    exit /b 1
)

if not exist "bin" mkdir "bin"

powershell -NoProfile -ExecutionPolicy Bypass -Command "$srcs = (Get-ChildItem -Recurse -Path src -Filter *.java).FullName; javac -encoding UTF-8 -d bin -cp 'lib/mysql-connector-j.jar' $srcs"
if %errorlevel% neq 0 (
    echo.
    echo [ERRO] Falha ao compilar o backend Java.
    echo.
    pause
    exit /b 1
)
echo [SUCESSO] Backend Java compilado com sucesso!
echo.

:RODAR
:: 3. Definir o Classpath
set "CP=bin;out\production\SustentaMed;out\production\SustentaMed01;lib\mysql-connector-j.jar"

:: 4. Executar aplicacao console
echo [INFO] Abrindo o menu no console...
echo.
java -cp "%CP%" com.sustentamed.app.Main

echo.
echo ========================================================
echo   Aplicacao encerrada.
echo ========================================================
pause
