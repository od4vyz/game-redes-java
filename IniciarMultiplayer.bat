@echo off
echo ===================================
echo    TESTE MULTIPLAYER CAP 1 e 2
echo ===================================
echo.
echo Escolha uma opcao:
echo 1. Teste automatico completo
echo 2. Terminais separados (Cap 1 apenas)
echo 3. Terminais separados (Cap 1 e 2)
echo.
set /p escolha="Digite sua escolha (1-3): "

if "%escolha%"=="1" (
    echo.
    echo Executando teste automatico...
    java TesteCap1Para2
    pause
    exit
)

if "%escolha%"=="2" (
    echo.
    echo Abrindo terminais para Capitulo 1...
    start "Jogador 1 - Deposito" cmd /k "cd /d %~dp0 && java Jogador1Terminal"
    timeout /t 2
    start "Jogador 2 - Galeria" cmd /k "cd /d %~dp0 && java Jogador2Terminal"
) else if "%escolha%"=="3" (
    echo.
    echo Compilando arquivos...
    javac *.java
    echo Abrindo terminais para Capitulos 1 e 2...
    start "Jogador 1 - Cap 1 e 2" cmd /k "cd /d %~dp0 && java Jogador1TerminalCap2"
    timeout /t 2
    start "Jogador 2 - Cap 1 e 2" cmd /k "cd /d %~dp0 && java Jogador2TerminalCap2"
) else (
    echo Opcao invalida!
    pause
    exit
)

echo.
echo Terminais abertos! 
echo.
echo INSTRUCOES:
echo 1. Jogador 1: Resolva o puzzle (testar A H P)
echo 2. Jogador 2: Vera a porta se abrir automaticamente
echo 3. Jogador 1: Use 'subir escada' para ir ao Hall
echo 4. Jogador 2: Use 'entrar porta' para ir a Fazenda
echo.
pause