// API - GATEWAY PRINCIPAL (PUERTO 9000)
// Todas las peticiones pasan por aquí. El Gateway las redirige a los puertos 8081, 8082 y 8083 internamente.

const GATEWAY_API = 'http://localhost:9000/api';

// ==========================================
// MICROSERVICIO USUARIO
// ==========================================
export const registrarUsuario = async (data) => {
  const response = await fetch(`${GATEWAY_API}/usuarios`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  if (!response.ok) throw new Error('Error al registrar usuario');
  return await response.json();
};

export const loginUsuario = async (credenciales) => {
  const response = await fetch(`${GATEWAY_API}/usuarios/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(credenciales),
  });
  if (!response.ok) throw new Error('Correo o contraseña incorrectos');
  return await response.json();
};

export const obtenerUsuarios = async () => {
  const response = await fetch(`${GATEWAY_API}/usuarios`);
  if (!response.ok) throw new Error('Error al obtener usuarios');
  return await response.json();
};

export const actualizarUsuarioAdmin = async (usuario) => {
  const response = await fetch(`${GATEWAY_API}/usuarios`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(usuario),
  });
  if (!response.ok) throw new Error('Error al actualizar usuario');
  return await response.json();
};

export const eliminarUsuario = async (id) => {
  const response = await fetch(`${GATEWAY_API}/usuarios/${id}`, { method: 'DELETE' });
  if (!response.ok) throw new Error('Error al eliminar usuario');
  return await response.json();
};


// ==========================================
// MICROSERVICIO RUTINAS
// ==========================================
export const obtenerCategorias = async () => {
  const response = await fetch(`${GATEWAY_API}/rutinas/categorias`);
  if (!response.ok) throw new Error('Error al obtener categorías');
  return await response.json();
};

export const crearCategoria = async (categoria) => {
  const response = await fetch(`${GATEWAY_API}/rutinas/categorias`, {
    method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(categoria),
  });
  if (!response.ok) throw new Error('Error al crear categoría');
  return await response.json();
};

export const actualizarCategoria = async (id, categoria) => {
  const response = await fetch(`${GATEWAY_API}/rutinas/categorias/${id}`, {
    method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(categoria),
  });
  if (!response.ok) throw new Error('Error al actualizar categoría');
  return await response.json();
};

export const eliminarCategoria = async (id) => {
  const response = await fetch(`${GATEWAY_API}/rutinas/categorias/${id}`, { method: 'DELETE' });
  if (!response.ok) throw new Error('Error al eliminar categoría');
  return await response.json();
};

export const obtenerEjercicios = async () => {
  const response = await fetch(`${GATEWAY_API}/rutinas/ejercicios`);
  if (!response.ok) throw new Error('Error al obtener ejercicios');
  return await response.json();
};

export const crearEjercicio = async (ejercicio) => {
  const response = await fetch(`${GATEWAY_API}/rutinas/ejercicios`, {
    method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(ejercicio),
  });
  if (!response.ok) throw new Error('Error al crear ejercicio');
  return await response.json();
};

export const actualizarEjercicio = async (id, ejercicio) => {
  const response = await fetch(`${GATEWAY_API}/rutinas/ejercicios/${id}`, {
    method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(ejercicio),
  });
  if (!response.ok) throw new Error('Error al actualizar ejercicio');
  return await response.json();
};

export const eliminarEjercicio = async (id) => {
  const response = await fetch(`${GATEWAY_API}/rutinas/ejercicios/${id}`, { method: 'DELETE' });
  if (!response.ok) throw new Error('Error al eliminar ejercicio');
  return await response.json();
};

export const obtenerRutinas = async () => {
  const response = await fetch(`${GATEWAY_API}/rutinas/rutinas`);
  if (!response.ok) throw new Error('Error al obtener rutinas');
  return await response.json();
};

export const crearRutina = async (rutina) => {
  const response = await fetch(`${GATEWAY_API}/rutinas/rutinas`, {
    method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(rutina),
  });
  if (!response.ok) throw new Error('Error al crear rutina');
  return await response.json();
};

export const actualizarRutina = async (id, rutina) => {
  const response = await fetch(`${GATEWAY_API}/rutinas/rutinas/${id}`, {
    method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(rutina),
  });
  if (!response.ok) throw new Error('Error al actualizar rutina');
  return await response.json();
};

export const eliminarRutina = async (id) => {
  const response = await fetch(`${GATEWAY_API}/rutinas/rutinas/${id}`, { method: 'DELETE' });
  if (!response.ok) throw new Error('Error al eliminar rutina');
  return await response.json();
};


// ==========================================
// MICROSERVICIO ENTRENAMIENTO
// ==========================================
export const obtenerAsignaciones = async () => {
  const response = await fetch(`${GATEWAY_API}/entrenamientos/asignaciones`);
  if (!response.ok) throw new Error('Error al obtener asignaciones');
  return await response.json();
};

export const crearAsignacion = async (asignacion) => {
  const response = await fetch(`${GATEWAY_API}/entrenamientos/asignaciones`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(asignacion),
  });
  if (!response.ok) throw new Error('Error al crear asignación');
  return await response.json();
};

export const obtenerSesiones = async (idAsignacion) => {
  const response = await fetch(`${GATEWAY_API}/entrenamientos/sesiones/${idAsignacion}`);
  if (!response.ok) throw new Error('Error al obtener sesiones');
  return await response.json();
};

export const crearSesion = async (sesion) => {
  const response = await fetch(`${GATEWAY_API}/entrenamientos/sesiones`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(sesion),
  });
  if (!response.ok) throw new Error('Error al registrar sesión');
  return await response.json();
};


// ==========================================
// MICROSERVICIO NOTIFICACIONES (patrón Observer)
// ==========================================
export const obtenerNotificacionesUsuario = async (usuarioId) => {
  const response = await fetch(`${GATEWAY_API}/notificaciones?usuarioId=${usuarioId}`);
  if (!response.ok) throw new Error('Error al obtener notificaciones');
  return await response.json();
};

export const obtenerNotificacionesRol = async (rol) => {
  const response = await fetch(`${GATEWAY_API}/notificaciones?rol=${encodeURIComponent(rol)}`);
  if (!response.ok) throw new Error('Error al obtener notificaciones del rol');
  return await response.json();
};

export const marcarNotificacionLeida = async (id) => {
  const response = await fetch(`${GATEWAY_API}/notificaciones/${id}`, { method: 'PUT' });
  if (!response.ok) throw new Error('Error al marcar la notificación como leída');
  return await response.json();
};

// ==========================================
// PLAN AJUSTADO DE UNA ASIGNACIÓN (patrón Estrategia, CU6)
// ==========================================
export const obtenerPlanAsignacion = async (idAsignacion) => {
  const response = await fetch(`${GATEWAY_API}/entrenamientos/asignaciones/${idAsignacion}`);
  if (!response.ok) throw new Error('Error al obtener el plan de la asignación');
  return await response.json();
};
