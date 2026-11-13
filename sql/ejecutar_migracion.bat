@echo off
setlocal
REM ============================================================
REM  Migracion BD Gym (patrones). Haz doble clic o ejecutalo en CMD.
REM  1) Respalda las 3 BDs existentes en la carpeta "respaldos"
REM  2) Actualiza db_ms_entrenamiento
REM  3) Crea db_ms_notificaciones y su tabla
REM  (El script 01_rutina... se ejecuta aparte, mas adelante)
REM ============================================================
set PGBIN=C:\Program Files\PostgreSQL\17\bin
set PGPASSWORD=password
set PGUSER=postgres
set PGHOST=localhost
set PGPORT=5432

if not exist respaldos mkdir respaldos

echo --- Respaldando bases ---
"%PGBIN%\pg_dump.exe" -d db_ms_usuario -f respaldos\db_ms_usuario.sql || goto error
"%PGBIN%\pg_dump.exe" -d db_ms_rutina -f respaldos\db_ms_rutina.sql || goto error
"%PGBIN%\pg_dump.exe" -d db_ms_entrenamiento -f respaldos\db_ms_entrenamiento.sql || goto error

echo --- Entrenamiento ---
"%PGBIN%\psql.exe" -v ON_ERROR_STOP=1 -d db_ms_entrenamiento -f 02_entrenamiento_db_ms_entrenamiento.sql || goto error

echo --- Notificaciones: crear BD ---
"%PGBIN%\psql.exe" -d postgres -c "CREATE DATABASE db_ms_notificaciones;"
echo (si dice que ya existe, no pasa nada)

echo --- Notificaciones: tabla ---
"%PGBIN%\psql.exe" -v ON_ERROR_STOP=1 -d db_ms_notificaciones -f 04_notificaciones_tablas_db_ms_notificaciones.sql || goto error

echo.
echo LISTO. Todo se ejecuto sin errores.
pause
exit /b 0

:error
echo.
echo *** HUBO UN ERROR. Copia el mensaje de arriba y envialo. ***
pause
exit /b 1
