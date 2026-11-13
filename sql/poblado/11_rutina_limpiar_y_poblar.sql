-- ============================================================
-- BD: db_ms_rutina
-- Limpia categorias, ejercicios, rutinas y rutina_ejercicio y los vuelve a poblar.
-- 4 categorias, 11 ejercicios, 5 rutinas (2 Principiante, 2 Intermedio, 1 Avanzado)
-- ============================================================
BEGIN;

TRUNCATE TABLE rutina_ejercicio, rutinas, ejercicios, categorias RESTART IDENTITY CASCADE;

INSERT INTO categorias (nombre) VALUES
 ('Fuerza'), ('Cardio'), ('Core'), ('Flexibilidad');

INSERT INTO ejercicios (id_categoria, nombre, descripcion, videourl)
SELECT c.id, v.nombre, v.descripcion, v.videourl
FROM (VALUES
 ('Fuerza',       'Sentadilla',               'Flexion de rodillas y cadera con la espalda recta.',          'https://www.youtube.com/results?search_query=sentadilla+tecnica'),
 ('Fuerza',       'Press de banca',           'Empuje de la barra desde el pecho acostado en banco plano.',  'https://www.youtube.com/results?search_query=press+de+banca+tecnica'),
 ('Fuerza',       'Peso muerto',              'Levantamiento de la barra desde el suelo con espalda neutra.','https://www.youtube.com/results?search_query=peso+muerto+tecnica'),
 ('Fuerza',       'Remo con barra',           'Tiron de la barra hacia el abdomen con el torso inclinado.',  'https://www.youtube.com/results?search_query=remo+con+barra+tecnica'),
 ('Fuerza',       'Press militar',            'Empuje de la barra por encima de la cabeza de pie.',          'https://www.youtube.com/results?search_query=press+militar+tecnica'),
 ('Cardio',       'Trote continuo',           'Carrera suave a ritmo constante.',                            'https://www.youtube.com/results?search_query=trote+continuo+tecnica'),
 ('Cardio',       'Saltos de cuerda',         'Saltos continuos con cuerda a ritmo moderado.',               'https://www.youtube.com/results?search_query=saltos+de+cuerda+tecnica'),
 ('Cardio',       'Burpees',                  'Flexion, salto y extension de cuerpo completo.',              'https://www.youtube.com/results?search_query=burpees+tecnica'),
 ('Core',         'Plancha',                  'Mantener el cuerpo recto apoyado en antebrazos y puntas.',    'https://www.youtube.com/results?search_query=plancha+abdominal+tecnica'),
 ('Core',         'Abdominales bicicleta',    'Giro de torso llevando codo a rodilla contraria.',            'https://www.youtube.com/results?search_query=abdominales+bicicleta'),
 ('Flexibilidad', 'Estiramiento isquiotibiales','Estiramiento sentado de la parte posterior del muslo.',     'https://www.youtube.com/results?search_query=estiramiento+isquiotibiales')
) AS v(cat, nombre, descripcion, videourl)
JOIN categorias c ON c.nombre = v.cat;

INSERT INTO rutinas (nombre, descripcion, nivel) VALUES
 ('Full Body Inicial',        'Rutina de cuerpo completo para empezar con cargas bajas.',     'Principiante'),
 ('Cardio Activo',            'Trabajo cardiovascular suave con core y estiramiento.',        'Principiante'),
 ('Fuerza Tren Superior',     'Empujes y tirones para pecho, espalda y hombros.',             'Intermedio'),
 ('Piernas y Core',           'Fuerza de piernas combinada con trabajo de core.',             'Intermedio'),
 ('Hipertrofia Avanzada',     'Alto volumen de fuerza para atletas con experiencia.',         'Avanzado');

-- Detalle de cada rutina: (rutina, ejercicio, series, repeticiones, descansoSeg, orden)
INSERT INTO rutina_ejercicio (Id_Rutina, Id_Ejercicio, series, repeticiones, descansoSeg, orden)
SELECT r.id, e.id, v.series, v.reps, v.descanso, v.orden
FROM (VALUES
 ('Full Body Inicial',    'Sentadilla',                 3, 12, 60, 1),
 ('Full Body Inicial',    'Press de banca',             3, 10, 60, 2),
 ('Full Body Inicial',    'Remo con barra',             3, 10, 60, 3),
 ('Full Body Inicial',    'Plancha',                    3, 30, 45, 4),

 ('Cardio Activo',        'Trote continuo',             3, 10, 45, 1),
 ('Cardio Activo',        'Saltos de cuerda',           3, 50, 45, 2),
 ('Cardio Activo',        'Abdominales bicicleta',      3, 20, 30, 3),
 ('Cardio Activo',        'Estiramiento isquiotibiales',2, 30, 20, 4),

 ('Fuerza Tren Superior', 'Press de banca',             4, 8,  90, 1),
 ('Fuerza Tren Superior', 'Remo con barra',             4, 8,  90, 2),
 ('Fuerza Tren Superior', 'Press militar',              3, 10, 75, 3),
 ('Fuerza Tren Superior', 'Plancha',                    3, 45, 45, 4),

 ('Piernas y Core',       'Sentadilla',                 4, 10, 90, 1),
 ('Piernas y Core',       'Peso muerto',                3, 8,  90, 2),
 ('Piernas y Core',       'Abdominales bicicleta',      3, 25, 45, 3),
 ('Piernas y Core',       'Estiramiento isquiotibiales',2, 40, 20, 4),

 ('Hipertrofia Avanzada', 'Sentadilla',                 5, 6,  120, 1),
 ('Hipertrofia Avanzada', 'Peso muerto',                5, 5,  120, 2),
 ('Hipertrofia Avanzada', 'Press de banca',             5, 6,  120, 3),
 ('Hipertrofia Avanzada', 'Burpees',                    4, 15, 60,  4),
 ('Hipertrofia Avanzada', 'Plancha',                    4, 60, 45,  5)
) AS v(rutina, ejercicio, series, reps, descanso, orden)
JOIN rutinas r ON r.nombre = v.rutina
JOIN ejercicios e ON e.nombre = v.ejercicio;

COMMIT;

-- Verificacion
SELECT r.id, r.nombre, r.nivel, COUNT(re.id) AS ejercicios
FROM rutinas r LEFT JOIN rutina_ejercicio re ON re.Id_Rutina = r.id
GROUP BY r.id, r.nombre, r.nivel ORDER BY r.id;
