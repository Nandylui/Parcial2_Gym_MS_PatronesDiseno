-- Ejecutar sobre la BD db_ms_rutina (RECOMENDADO: despues del Paso 2)
-- Hace que rutinas.nivel sea obligatorio (la Estrategia depende de este dato).
SELECT DISTINCT nivel FROM rutinas;

-- 1.2 Completar los nulos con el nivel por defecto y exigir el dato
UPDATE rutinas SET nivel = 'Intermedio' WHERE nivel IS NULL;
ALTER TABLE rutinas ALTER COLUMN nivel SET NOT NULL;


