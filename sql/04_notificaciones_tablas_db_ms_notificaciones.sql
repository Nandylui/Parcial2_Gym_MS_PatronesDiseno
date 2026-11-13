-- Ejecutar sobre la BD db_ms_notificaciones
CREATE TABLE IF NOT EXISTS notificaciones (
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER,                   -- referencia logica -> MS Usuario
    rol_destino VARCHAR(30),              -- referencia logica -> MS Usuario (Roles)
    tipo VARCHAR(30) NOT NULL,
    mensaje TEXT NOT NULL,
    leida BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT ck_notificacion_destino CHECK (usuario_id IS NOT NULL OR rol_destino IS NOT NULL)
);

CREATE INDEX IF NOT EXISTS ix_notificaciones_usuario ON notificaciones(usuario_id);
CREATE INDEX IF NOT EXISTS ix_notificaciones_rol ON notificaciones(rol_destino);
