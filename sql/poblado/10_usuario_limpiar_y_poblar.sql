-- ============================================================
-- BD: db_ms_usuario
-- Deja SOLO al admin (id 1, rol INSTRUCTOR) y agrega 4 usuarios de prueba:
--   1 instructor + 3 clientes. NO toca la tabla de roles.
-- Contrasena de todos los usuarios nuevos: 123456 (texto plano, como el sistema actual)
-- ============================================================
BEGIN;

-- Seguridad: si el admin (id 1) no existe con rol 1, se cancela todo
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM usuario WHERE id = 1)
       OR NOT EXISTS (SELECT 1 FROM usuario_rol WHERE id_usuario = 1 AND id_rol = 1) THEN
        RAISE EXCEPTION 'No se encontro al admin (usuario 1 con rol 1). Se cancela la limpieza.';
    END IF;
END $$;

-- Compatibilidad: UsuarioDao consulta la tabla "roles". Si en tu BD se llama "rol",
-- se crea una VISTA "roles" (no renombra ni borra nada).
DO $$
BEGIN
    IF to_regclass('public.roles') IS NULL AND to_regclass('public.rol') IS NOT NULL THEN
        EXECUTE 'CREATE VIEW roles AS SELECT id, nombre FROM rol';
        RAISE NOTICE 'Se creo la vista roles sobre la tabla rol.';
    END IF;
END $$;

-- Limpieza: todo menos el admin
DELETE FROM usuario_rol WHERE id_usuario <> 1;
DELETE FROM usuario WHERE id <> 1;

-- Los ids nuevos empiezan en 2
SELECT setval(pg_get_serial_sequence('usuario', 'id'), 1);
SELECT setval(pg_get_serial_sequence('usuario_rol', 'id'), (SELECT COALESCE(MAX(id), 1) FROM usuario_rol));

-- Usuarios de prueba (ids 2 a 5)
INSERT INTO usuario (ci, nombre, apellido, email, contrasena, telefono, fecha_nac, genero) VALUES
 (7654321, 'Carlos', 'Mendoza', 'instructor@gym.com', '123456', 71234567, '1990-05-14', 'Masculino'),
 (8123456, 'Adela',  'Rojas',   'adela@gym.com',      '123456', 72345678, '1998-03-22', 'Femenino'),
 (9012345, 'Marcos', 'Vargas',  'marcos@gym.com',     '123456', 73456789, '1995-11-08', 'Masculino'),
 (6789012, 'Daniela','Suarez',  'daniela@gym.com',    '123456', 74567890, '2000-07-30', 'Femenino');

-- Roles: Carlos = INSTRUCTOR (1), los otros 3 = CLIENTE (2)
INSERT INTO usuario_rol (id_usuario, id_rol)
SELECT id, 1 FROM usuario WHERE email = 'instructor@gym.com';

INSERT INTO usuario_rol (id_usuario, id_rol)
SELECT id, 2 FROM usuario WHERE email IN ('adela@gym.com', 'marcos@gym.com', 'daniela@gym.com');

COMMIT;

-- Verificacion
SELECT u.id, u.nombre, u.apellido, u.email, ur.id_rol
FROM usuario u LEFT JOIN usuario_rol ur ON ur.id_usuario = u.id
ORDER BY u.id;
