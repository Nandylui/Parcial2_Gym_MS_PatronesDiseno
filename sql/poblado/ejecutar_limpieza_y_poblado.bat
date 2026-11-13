@echo off
setlocal
cd /d "%~dp0"
REM ============================================================
REM  Limpia y puebla las 4 BDs del Gym.
REM  ANTES: detener los 4 microservicios (Usuario, Rutina, Entrenamiento,
REM         Notificaciones). El Gateway y el frontend pueden seguir.
REM  1) Respalda las 4 BDs en "respaldos_antes_de_limpiar"
REM  2) Pide confirmacion
REM  3) Ejecuta los 4 scripts SQL
REM ============================================================
set PGBIN=C:\Program Files\PostgreSQL\17\bin
set PGPASSWORD=password
set PGUSER=postgres
set PGHOST=localhost
set PGPORT=5432

echo.
echo Asegurate de haber DETENIDO los 4 microservicios.
echo.

if not exist respaldos_antes_de_limpiar mkdir respaldos_antes_de_limpiar

echo --- Respaldando bases ---
"%PGBIN%\pg_dump.exe" -d db_ms_usuario        -f respaldos_antes_de_limpiar\db_ms_usuario.sql        || goto error
"%PGBIN%\pg_dump.exe" -d db_ms_rutina         -f respaldos_antes_de_limpiar\db_ms_rutina.sql         || goto error
"%PGBIN%\pg_dump.exe" -d db_ms_entrenamiento  -f respaldos_antes_de_limpiar\db_ms_entrenamiento.sql  || goto error
"%PGBIN%\pg_dump.exe" -d db_ms_notificaciones -f respaldos_antes_de_limpiar\db_ms_notificaciones.sql || goto error
echo Respaldos guardados en la carpeta respaldos_antes_de_limpiar

echo.
set /p OK=Escribe SI para limpiar y poblar las 4 bases: 
if /i not "%OK%"=="SI" (
  echo Cancelado. No se toco nada.
  pause
  exit /b 0
)

echo --- Usuario ---
"%PGBIN%\psql.exe" -v ON_ERROR_STOP=1 -d db_ms_usuario -f 10_usuario_limpiar_y_poblar.sql || goto error
echo --- Rutina ---
"%PGBIN%\psql.exe" -v ON_ERROR_STOP=1 -d db_ms_rutina -f 11_rutina_limpiar_y_poblar.sql || goto error
echo --- Entrenamiento ---
"%PGBIN%\psql.exe" -v ON_ERROR_STOP=1 -d db_ms_entrenamiento -f 12_entrenamiento_limpiar.sql || goto error
echo --- Notificaciones ---
"%PGBIN%\psql.exe" -v ON_ERROR_STOP=1 -d db_ms_notificaciones -f 13_notificaciones_limpiar.sql || goto error

echo.
echo LISTO. Ahora inicia los 4 microservicios y el Gateway,
echo luego ejecuta 20_poblar_por_api.ps1 (clic derecho - Ejecutar con PowerShell).
pause
exit /b 0

:error
echo.
echo *** HUBO UN ERROR. Copia el mensaje de arriba y envialo. ***
echo (Los respaldos de antes estan en respaldos_antes_de_limpiar)
pause
exit /b 1
