-- ============================================================
-- BD: db_ms_entrenamiento
-- Solo LIMPIA. Las asignaciones y sesiones se crean por la API
-- (20_poblar_por_api.ps1) para que se generen los planes (Strategy)
-- y las notificaciones (Observer).
-- ============================================================
TRUNCATE TABLE sesion_entrenamiento, planes_asignacion, asignaciones RESTART IDENTITY CASCADE;

SELECT 'asignaciones' AS tabla, COUNT(*) AS filas FROM asignaciones
UNION ALL SELECT 'sesion_entrenamiento', COUNT(*) FROM sesion_entrenamiento
UNION ALL SELECT 'planes_asignacion', COUNT(*) FROM planes_asignacion;
