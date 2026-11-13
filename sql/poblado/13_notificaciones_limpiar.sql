-- ============================================================
-- BD: db_ms_notificaciones
-- Solo LIMPIA. Las notificaciones nuevas las genera el Observer.
-- ============================================================
TRUNCATE TABLE notificaciones RESTART IDENTITY;

SELECT COUNT(*) AS notificaciones FROM notificaciones;
