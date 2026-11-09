@echo off
chcp 65001 > nul
echo ========================================================
echo   Iniciando SustentaMed - Servidor REST API Nativo (Porta 8080)
echo ========================================================

set CP=bin;lib/mysql-connector-j.jar

java -cp "%CP%" com.sustentamed.app.ServidorHttp
pause

