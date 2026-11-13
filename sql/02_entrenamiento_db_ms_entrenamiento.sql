-- Ejecutar sobre la BD db_ms_entrenamiento
-- Crea solo lo que falta (IF NOT EXISTS); no toca lo existente.
CREATE TABLE IF NOT EXISTS asignaciones (
    id SERIAL PRIMARY KEY,
    Id_Usuario INTEGER NOT NULL,          -- referencia logica -> MS Usuario
    Id_Rutina INTEGER NOT NULL,           -- referencia logica -> MS Rutina
    fechaAsignacion DATE NOT NULL DEFAULT CURRENT_DATE,
    fechaInicio DATE NOT NULL,
    fechaFin DATE NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVA',
    CONSTRAINT ck_asignaciones_fechas CHECK (fechaFin >= fechaInicio),
    CONSTRAINT ck_asignaciones_estado CHECK (estado IN ('ACTIVA', 'FINALIZADA', 'CANCELADA'))
);

CREATE INDEX IF NOT EXISTS ix_asignaciones_usuario ON asignaciones(Id_Usuario);
CREATE INDEX IF NOT EXISTS ix_asignaciones_rutina ON asignaciones(Id_Rutina);

CREATE TABLE IF NOT EXISTS sesion_entrenamiento (
    id SERIAL PRIMARY KEY,
    Id_Asignacion INTEGER NOT NULL REFERENCES asignaciones(id) ON DELETE CASCADE,
    fecha DATE NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    CONSTRAINT ck_sesion_estado CHECK (estado IN ('PENDIENTE', 'COMPLETADO'))
);

CREATE INDEX IF NOT EXISTS ix_sesion_asignacion ON sesion_entrenamiento(Id_Asignacion);

-- [PATRON STRATEGY] Resultado de aplicar la estrategia de ajuste a cada asignacion
CREATE TABLE IF NOT EXISTS planes_asignacion (
    id SERIAL PRIMARY KEY,
    Id_Asignacion INTEGER NOT NULL UNIQUE REFERENCES asignaciones(id) ON DELETE CASCADE,
    estrategia VARCHAR(20) NOT NULL,
    factor_series NUMERIC(3,2) NOT NULL,
    sesiones_por_semana INTEGER NOT NULL CHECK (sesiones_por_semana > 0),
    sesiones_esperadas INTEGER NOT NULL DEFAULT 0 CHECK (sesiones_esperadas >= 0),
    fecha_calculo TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT ck_plan_estrategia CHECK (estrategia IN ('PRINCIPIANTE', 'INTERMEDIO', 'AVANZADO'))
);
