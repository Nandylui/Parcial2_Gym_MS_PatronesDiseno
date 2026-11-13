import React, { useState, useEffect } from 'react';
import { registrarUsuario, loginUsuario, obtenerCategorias, crearCategoria, actualizarCategoria, eliminarCategoria, obtenerEjercicios, crearEjercicio, actualizarEjercicio, eliminarEjercicio, obtenerRutinas, crearRutina, actualizarRutina, eliminarRutina, obtenerUsuarios, actualizarUsuarioAdmin, eliminarUsuario, obtenerAsignaciones, crearAsignacion, crearSesion, obtenerNotificacionesUsuario, obtenerNotificacionesRol, marcarNotificacionLeida, obtenerPlanAsignacion } from './services/api';

// MÓDULO: CATEGORÍAS 

function ModuloCategorias({ setLog }) {
  const [categorias, setCategorias] = useState([]);
  const [busqueda, setBusqueda] = useState('');
  const [mostrarModal, setMostrarModal] = useState(false);
  const [modoEdicion, setModoEdicion] = useState(false);
  const [formCategoria, setFormCategoria] = useState({ id: '', nombre: '' });

  useEffect(() => { cargarDatos(); }, []);

  const cargarDatos = async () => {
    try {
      const data = await obtenerCategorias();
      setCategorias(data);
    } catch (error) { setLog("❌ Error al cargar categorías."); }
  };

  const handleGuardar = async (e) => {
    e.preventDefault();
    try {
      if (modoEdicion) {
        await actualizarCategoria(formCategoria.id, { nombre: formCategoria.nombre });
        setLog("Categoría actualizada correctamente.");
      } else {
        await crearCategoria({ nombre: formCategoria.nombre });
        setLog("Categoría creada correctamente.");
      }
      setMostrarModal(false);
      cargarDatos();
    } catch (error) { setLog("❌ Error al guardar la categoría."); }
  };

  const handleEliminar = async (id) => {
    if (window.confirm("¿Estás seguro de eliminar esta categoría?")) {
      try {
        await eliminarCategoria(id);
        setLog("Categoría eliminada.");
        cargarDatos();
      } catch (error) { setLog("❌ Error al eliminar la categoría."); }
    }
  };

  const abrirModalNuevo = () => {
    setModoEdicion(false);
    setFormCategoria({ id: '', nombre: '' });
    setMostrarModal(true);
  };

  const abrirModalEditar = (cat) => {
    setModoEdicion(true);
    setFormCategoria(cat);
    setMostrarModal(true);
  };

  const categoriasFiltradas = categorias.filter(c => 
    c.nombre.toLowerCase().includes(busqueda.toLowerCase())
  );

  return (
    // max-w-5xl mx-auto centra la tabla y evita que se estire demasiado en pantallas grandes
    <div className="flex flex-col gap-6 w-full max-w-5xl mx-auto animate-fade-in">
      
      {/* 1. BUSCADOR COMPACTO Y BOTÓN CREAR */}
      <div className="flex items-center justify-between gap-4">
        <div className="relative w-80"> {/* w-80 hace que el buscador no sea tan largo */}
          <span className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 text-lg">🔍</span>
          <input 
            type="text" 
            placeholder="Buscar categorías..." 
            className="w-full pl-11 pr-4 py-2.5 bg-white border border-slate-200 rounded-full shadow-sm text-slate-700 focus:outline-none focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500 transition-all text-sm"
            value={busqueda}
            onChange={(e) => setBusqueda(e.target.value)}
          />
        </div>
        <button onClick={abrirModalNuevo} className="bg-emerald-600 text-white px-5 py-2.5 rounded-full text-sm font-bold hover:bg-emerald-700 transition-colors shadow-sm flex items-center gap-2">
          <span>+</span> Crear
        </button>
      </div>

      {/* 2. TABLA COMPACTA Y BONITA */}
      <div className="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
        <div className="p-5 border-b border-slate-100 bg-slate-50">
          <h2 className="text-xl font-bold text-slate-800">Gestión de categorías</h2>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-white text-slate-500 text-xs uppercase tracking-wider border-b border-slate-200">
                <th className="p-4 font-semibold w-24 pl-6 text-center">ID</th> {/* ID simple */}
                <th className="p-4 font-semibold">Categoría</th>
                <th className="p-4 font-semibold text-center w-32">Estado</th>
                <th className="p-4 font-semibold text-center w-64 pr-6">Acciones</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {categoriasFiltradas.length > 0 ? (
                categoriasFiltradas.map((cat) => (
                  <tr key={cat.id} className="hover:bg-slate-50 transition-colors">
                    <td className="p-4 pl-6 text-sm text-slate-500 text-center font-medium">{cat.id}</td>
                    <td className="p-4 text-sm font-bold text-slate-800">{cat.nombre}</td>
                    <td className="p-4 text-center">
                      <span className="bg-green-100 text-green-700 text-xs font-bold px-3 py-1 rounded-full">Activo</span>
                    </td>
                    <td className="p-4 pr-6 text-center space-x-2">
                      {/* Botones de acción redondos y con color */}
                      <button onClick={() => abrirModalEditar(cat)} className="bg-amber-500 hover:bg-amber-600 text-white px-4 py-1.5 rounded-full text-xs font-bold transition-colors shadow-sm">Editar</button>
                      <button onClick={() => handleEliminar(cat.id)} className="bg-red-500 hover:bg-red-600 text-white px-4 py-1.5 rounded-full text-xs font-bold transition-colors shadow-sm">Eliminar</button>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="4" className="p-8 text-center text-sm text-slate-400">No se encontraron resultados.</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* 3. MODAL (Pop-up) */}
      {mostrarModal && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm flex items-center justify-center z-50">
          <div className="bg-white p-8 rounded-2xl shadow-2xl w-96 transform transition-all">
            <h3 className="text-xl font-bold text-slate-800 mb-6">
              {modoEdicion ? 'Editar Categoría' : 'Nueva Categoría'}
            </h3>
            <form onSubmit={handleGuardar}>
              <div className="mb-6">
                <label className="block text-sm font-bold text-slate-700 mb-2">Nombre de la Categoría</label>
                <input 
                  type="text" required autoFocus
                  className="w-full p-3 border border-slate-300 rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500"
                  value={formCategoria.nombre}
                  onChange={(e) => setFormCategoria({...formCategoria, nombre: e.target.value})}
                />
              </div>
              <div className="flex justify-end gap-3">
                <button type="button" onClick={() => setMostrarModal(false)} className="px-5 py-2.5 text-slate-600 hover:bg-slate-100 rounded-lg text-sm font-bold transition-colors">Cancelar</button>
                <button type="submit" className="bg-emerald-600 text-white px-5 py-2.5 rounded-lg text-sm font-bold hover:bg-emerald-700 transition-colors">Guardar</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

// MÓDULO: EJERCICIOS (Con Select de Categorías y Tarjeta de Detalles)

function ModuloEjercicios({ setLog }) {
  const [ejercicios, setEjercicios] = useState([]);
  const [categorias, setCategorias] = useState([]);
  const [busqueda, setBusqueda] = useState('');
  
  // Estados para controlar las vistas (Tabla vs Tarjeta de Detalles)
  const [vistaActual, setVistaActual] = useState('tabla'); // 'tabla' o 'detalle'
  const [ejercicioViendo, setEjercicioViendo] = useState(null);

  // Estados para el Modal (Crear/Editar)
  const [mostrarModal, setMostrarModal] = useState(false);
  const [modoEdicion, setModoEdicion] = useState(false);
  const [form, setForm] = useState({ id: '', Id_Categoria: '', nombre: '', descripcion: '', videoURL: '' });

  // Al cargar el módulo, traemos los Ejercicios Y las Categorías
  useEffect(() => {
    cargarDatos();
  }, []);

  const cargarDatos = async () => {
    try {
      const dataEjercicios = await obtenerEjercicios();
      const dataCategorias = await obtenerCategorias();
      setEjercicios(dataEjercicios);
      setCategorias(dataCategorias);
    } catch (error) { setLog("❌ Error al cargar ejercicios o categorías."); }
  };

  // Función para traducir el ID de la categoría a su Nombre real
  const obtenerNombreCategoria = (idCat) => {
    const cat = categorias.find(c => c.id === idCat);
    return cat ? cat.nombre : 'Desconocida';
  };

  const handleGuardar = async (e) => {
    e.preventDefault();
    try {
      // 1. Armamos un paquete limpio SOLO con los datos necesarios (sin el ID vacío)
      const datosAEnviar = { 
        Id_Categoria: parseInt(form.Id_Categoria, 10),
        nombre: form.nombre,
        descripcion: form.descripcion,
        videoURL: form.videoURL
      };
      
      // 2. Si estamos editando, usamos la función actualizar (que sí pide el form.id)
      if (modoEdicion) {
        await actualizarEjercicio(form.id, datosAEnviar);
        setLog("Ejercicio actualizado correctamente.");
      } else {
        // 3. Si es nuevo, mandamos el paquete limpio
        await crearEjercicio(datosAEnviar);
        setLog("Ejercicio creado correctamente.");
      }
      
      // 4. Cerramos el modal y recargamos la tabla
      setMostrarModal(false);
      cargarDatos();
      
    } catch (error) { 
      // Si falla, avisamos en los logs y cerramos el modal para que no parezca congelado
      setLog("❌ Error al guardar el ejercicio."); 
      setMostrarModal(false);
    }
  };

  
  const handleEliminar = async (id) => {
    if (window.confirm("¿Estás seguro de eliminar este ejercicio?")) {
      try {
        await eliminarEjercicio(id);
        setLog("Ejercicio eliminado.");
        cargarDatos();
      } catch (error) { setLog("❌ Error al eliminar."); }
    }
  };

  const abrirModalNuevo = () => {
    setModoEdicion(false);
    setForm({ id: '', Id_Categoria: '', nombre: '', descripcion: '', videoURL: '' });
    setMostrarModal(true);
  };

  const abrirModalEditar = (ej) => {
    setModoEdicion(true);
    setForm(ej);
    setMostrarModal(true);
  };

  const verDetalles = (ej) => {
    setEjercicioViendo(ej);
    setVistaActual('detalle');
  };

  const ejerciciosFiltrados = ejercicios.filter(e => 
    e.nombre.toLowerCase().includes(busqueda.toLowerCase())
  );

  
  // --------------------------------------------------------
  // VISTA 1: TARJETA DE DETALLES ("VER")
  // --------------------------------------------------------
  if (vistaActual === 'detalle' && ejercicioViendo) {
    return (
      <div className="bg-white p-8 rounded-2xl shadow-sm border border-slate-200 w-full max-w-4xl mx-auto animate-fade-in">
        <button onClick={() => setVistaActual('tabla')} className="text-slate-500 hover:text-emerald-600 font-bold mb-6 flex items-center gap-2 transition-colors">
          <span>←</span> Volver a la lista
        </button>
        
        <div className="flex justify-between items-start mb-6">
          <div>
            <h2 className="text-3xl font-black text-slate-800">{ejercicioViendo.nombre}</h2>
            <span className="inline-block mt-2 bg-emerald-100 text-emerald-800 font-bold px-4 py-1 rounded-full text-sm">
              Categoría: {obtenerNombreCategoria(ejercicioViendo.Id_Categoria)}
            </span>
          </div>
          <span className="text-slate-400 font-bold text-lg">ID: #{ejercicioViendo.id}</span>
        </div>

        <div className="bg-slate-50 p-6 rounded-xl border border-slate-100 mb-6">
          <h4 className="text-sm font-bold text-slate-500 uppercase tracking-wider mb-2">Descripción del Ejercicio</h4>
          <p className="text-slate-700 leading-relaxed">{ejercicioViendo.descripcion || "No hay descripción disponible."}</p>
        </div>

        {ejercicioViendo.videoURL && (
          <div className="bg-blue-50 p-6 rounded-xl border border-blue-100 flex items-center gap-4">
            <span className="text-2xl">📺</span>
            <div>
              <h4 className="text-sm font-bold text-blue-800">Video de Referencia</h4>
              <a href={ejercicioViendo.videoURL} target="_blank" rel="noreferrer" className="text-blue-600 hover:underline break-all text-sm">
                {ejercicioViendo.videoURL}
              </a>
            </div>
          </div>
        )}
      </div>
    );
  }

  // --------------------------------------------------------
  // VISTA 2: TABLA PRINCIPAL Y BUSCADOR
  // --------------------------------------------------------
  return (
    <div className="flex flex-col gap-6 w-full max-w-5xl mx-auto animate-fade-in">
      <div className="flex items-center justify-between gap-4">
        <div className="relative w-80">
          <span className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 text-lg">🔍</span>
          <input 
            type="text" placeholder="Buscar ejercicios..." 
            className="w-full pl-11 pr-4 py-2.5 bg-white border border-slate-200 rounded-full shadow-sm text-slate-700 focus:outline-none focus:border-emerald-500"
            value={busqueda} onChange={(e) => setBusqueda(e.target.value)}
          />
        </div>
        <button onClick={abrirModalNuevo} className="bg-emerald-600 text-white px-5 py-2.5 rounded-full text-sm font-bold hover:bg-emerald-700 shadow-sm flex items-center gap-2">
          <span>+</span> Crear Ejercicio
        </button>
      </div>

      <div className="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
        <div className="p-5 border-b border-slate-100 bg-slate-50">
          <h2 className="text-xl font-bold text-slate-800">Catálogo de Ejercicios</h2>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-white text-slate-500 text-xs uppercase tracking-wider border-b border-slate-200">
                <th className="p-4 font-semibold w-20 pl-6 text-center">ID</th>
                <th className="p-4 font-semibold">Ejercicio</th>
                <th className="p-4 font-semibold">Categoría</th>
                <th className="p-4 font-semibold text-center w-72 pr-6">Acciones</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {ejerciciosFiltrados.length > 0 ? (
                ejerciciosFiltrados.map((ej) => (
                  <tr key={ej.id} className="hover:bg-slate-50 transition-colors">
                    <td className="p-4 pl-6 text-sm text-slate-500 text-center font-medium">{ej.id}</td>
                    <td className="p-4 text-sm font-bold text-slate-800">{ej.nombre}</td>
                    <td className="p-4 text-sm text-slate-600">{obtenerNombreCategoria(ej.Id_Categoria)}</td>
                    <td className="p-4 pr-6 text-center space-x-2">
                      <button onClick={() => verDetalles(ej)} className="bg-blue-500 hover:bg-blue-600 text-white px-3 py-1.5 rounded-full text-xs font-bold transition-colors shadow-sm">Ver</button>
                      <button onClick={() => abrirModalEditar(ej)} className="bg-amber-500 hover:bg-amber-600 text-white px-3 py-1.5 rounded-full text-xs font-bold transition-colors shadow-sm">Editar</button>
                      <button onClick={() => handleEliminar(ej.id)} className="bg-red-500 hover:bg-red-600 text-white px-3 py-1.5 rounded-full text-xs font-bold transition-colors shadow-sm">Eliminar</button>
                    </td>
                  </tr>
                ))
              ) : (
                <tr><td colSpan="4" className="p-8 text-center text-sm text-slate-400">No se encontraron resultados.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* MODAL PARA CREAR/EDITAR */}
      {mostrarModal && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm flex items-center justify-center z-50">
          <div className="bg-white p-8 rounded-2xl shadow-2xl w-[500px] transform transition-all">
            <h3 className="text-xl font-bold text-slate-800 mb-6">{modoEdicion ? 'Editar Ejercicio' : 'Nuevo Ejercicio'}</h3>
            <form onSubmit={handleGuardar} className="space-y-4">
              <div>
                <label className="block text-sm font-bold text-slate-700 mb-1">Nombre del Ejercicio</label>
                <input type="text" required autoFocus className="w-full p-2 border border-slate-300 rounded-lg" value={form.nombre} onChange={(e) => setForm({...form, nombre: e.target.value})} />
              </div>
              
              <div>
                <label className="block text-sm font-bold text-slate-700 mb-1">Categoría</label>
                {/* ¡AQUÍ ESTÁ EL SELECT*/}
                <select required className="w-full p-2 border border-slate-300 rounded-lg bg-white" value={form.Id_Categoria} onChange={(e) => setForm({...form, Id_Categoria: e.target.value})}>
                  <option value="">Seleccione una categoría...</option>
                  {categorias.map(cat => (
                    <option key={cat.id} value={cat.id}>{cat.nombre}</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-bold text-slate-700 mb-1">Descripción</label>
                <textarea rows="3" className="w-full p-2 border border-slate-300 rounded-lg resize-none" value={form.descripcion} onChange={(e) => setForm({...form, descripcion: e.target.value})}></textarea>
              </div>

              <div>
                <label className="block text-sm font-bold text-slate-700 mb-1">URL del Video (Opcional)</label>
                <input type="url" placeholder="https://youtube.com/..." className="w-full p-2 border border-slate-300 rounded-lg" value={form.videoURL} onChange={(e) => setForm({...form, videoURL: e.target.value})} />
              </div>

              <div className="flex justify-end gap-3 mt-6">
                <button type="button" onClick={() => setMostrarModal(false)} className="px-5 py-2.5 text-slate-600 hover:bg-slate-100 rounded-lg text-sm font-bold">Cancelar</button>
                <button type="submit" className="bg-emerald-600 text-white px-5 py-2.5 rounded-lg text-sm font-bold hover:bg-emerald-700">Guardar</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

// MÓDULO: RUTINAS (Con Maestro-Detalle de Ejercicios)

function ModuloRutinas({ setLog }) {
  const [rutinas, setRutinas] = useState([]);
  const [ejerciciosDisponibles, setEjerciciosDisponibles] = useState([]);
  const [busqueda, setBusqueda] = useState('');
  
  const [vistaActual, setVistaActual] = useState('tabla'); 
  const [rutinaViendo, setRutinaViendo] = useState(null);

  const [mostrarModal, setMostrarModal] = useState(false);
  const [modoEdicion, setModoEdicion] = useState(false);
  
  // Estado principal de la rutina
  const [form, setForm] = useState({ id: '', nombre: '', descripcion: '', nivel: '' });
  // Estado para la lista temporal de ejercicios que vamos agregando
  const [ejerciciosAsignados, setEjerciciosAsignados] = useState([]);
  
  // Estado para el mini-formulario de agregar un ejercicio a la lista
  const [ejercicioTemporal, setEjercicioTemporal] = useState({ Id_Ejercicio: '', series: 4, repeticiones: 12, descansoSeg: 60 });

  useEffect(() => { cargarDatos(); }, []);

  const cargarDatos = async () => {
    try {
      const dataRutinas = await obtenerRutinas();
      const dataEjercicios = await obtenerEjercicios();
      setRutinas(dataRutinas);
      setEjerciciosDisponibles(dataEjercicios);
    } catch (error) { setLog("❌ Error al cargar rutinas."); }
  };

  const obtenerNombreEjercicio = (idEj) => {
    const ej = ejerciciosDisponibles.find(e => e.id === idEj);
    return ej ? ej.nombre : 'Desconocido';
  };

  // Agregar un ejercicio a la lista temporal antes de guardar en BD
  const agregarEjercicioALista = () => {
    if (!ejercicioTemporal.Id_Ejercicio) return alert("Selecciona un ejercicio");
    
    const nuevo = {
      Id_Ejercicio: parseInt(ejercicioTemporal.Id_Ejercicio, 10),
      series: parseInt(ejercicioTemporal.series, 10),
      repeticiones: parseInt(ejercicioTemporal.repeticiones, 10),
      descansoSeg: parseInt(ejercicioTemporal.descansoSeg, 10),
      orden: ejerciciosAsignados.length + 1 // Se asigna el orden automáticamente
    };
    
    setEjerciciosAsignados([...ejerciciosAsignados, nuevo]);
    // Limpiamos el mini-form
    setEjercicioTemporal({ Id_Ejercicio: '', series: 4, repeticiones: 12, descansoSeg: 60 });
  };

  const quitarEjercicioDeLista = (indexToRemove) => {
    const nuevaLista = ejerciciosAsignados.filter((_, index) => index !== indexToRemove);
    // Reordenamos
    const listaReordenada = nuevaLista.map((ej, index) => ({ ...ej, orden: index + 1 }));
    setEjerciciosAsignados(listaReordenada);
  };

  const handleGuardar = async (e) => {
    e.preventDefault();
    if (ejerciciosAsignados.length === 0) {
      return alert("Debes agregar al menos un ejercicio a la rutina.");
    }

    try {
      // Empaquetamos Rutina + Ejercicios (Maestro-Detalle)
      const datosAEnviar = { 
        nombre: form.nombre,
        descripcion: form.descripcion,
        nivel: form.nivel,
        ejercicios: ejerciciosAsignados 
      };
      
      if (modoEdicion) {
        await actualizarRutina(form.id, datosAEnviar);
        setLog("Rutina actualizada correctamente.");
      } else {
        await crearRutina(datosAEnviar);
        setLog("Rutina creada correctamente con sus ejercicios.");
      }
      setMostrarModal(false);
      cargarDatos();
    } catch (error) { setLog("❌ Error al guardar la rutina."); setMostrarModal(false); }
  };

  const handleEliminar = async (id) => {
    if (window.confirm("¿Eliminar esta rutina? Se borrarán sus ejercicios asignados.")) {
      try {
        await eliminarRutina(id);
        setLog("Rutina eliminada.");
        cargarDatos();
      } catch (error) { setLog("❌ Error al eliminar."); }
    }
  };

  const abrirModalNuevo = () => {
    setModoEdicion(false);
    setForm({ id: '', nombre: '', descripcion: '', nivel: '' });
    setEjerciciosAsignados([]);
    setMostrarModal(true);
  };

  const abrirModalEditar = (rutina) => {
    setModoEdicion(true);
    setForm({ id: rutina.id, nombre: rutina.nombre, descripcion: rutina.descripcion, nivel: rutina.nivel });
    setEjerciciosAsignados(rutina.ejercicios || []);
    setMostrarModal(true);
  };

  const verDetalles = (rutina) => {
    setRutinaViendo(rutina);
    setVistaActual('detalle');
  };

  const rutinasFiltradas = rutinas.filter(r => r.nombre.toLowerCase().includes(busqueda.toLowerCase()));

  // ================= VISTA: DETALLES DE LA RUTINA =================
  if (vistaActual === 'detalle' && rutinaViendo) {
    return (
      <div className="bg-white p-8 rounded-2xl shadow-sm border border-slate-200 w-full max-w-4xl mx-auto animate-fade-in">
        <button onClick={() => setVistaActual('tabla')} className="text-slate-500 hover:text-emerald-600 font-bold mb-6 flex items-center gap-2">
          <span>←</span> Volver a la lista
        </button>
        
        <div className="flex justify-between items-start mb-6">
          <div>
            <h2 className="text-3xl font-black text-slate-800">{rutinaViendo.nombre}</h2>
            <span className="inline-block mt-2 bg-blue-100 text-blue-800 font-bold px-4 py-1 rounded-full text-sm uppercase">Nivel: {rutinaViendo.nivel}</span>
          </div>
          <span className="text-slate-400 font-bold text-lg">ID: #{rutinaViendo.id}</span>
        </div>

        <p className="text-slate-700 mb-8 bg-slate-50 p-4 rounded-xl border border-slate-100">{rutinaViendo.descripcion}</p>

        <h3 className="text-xl font-bold text-slate-800 mb-4">Ejercicios en esta Rutina</h3>
        <div className="bg-white rounded-xl border border-slate-200 overflow-hidden shadow-sm">
          <table className="w-full text-left">
            <thead className="bg-slate-50 border-b border-slate-200 text-slate-500 text-xs uppercase">
              <tr>
                <th className="p-3 pl-6">Orden</th>
                <th className="p-3">Ejercicio</th>
                <th className="p-3 text-center">Series</th>
                <th className="p-3 text-center">Reps</th>
                <th className="p-3 text-center">Descanso</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {rutinaViendo.ejercicios && rutinaViendo.ejercicios.length > 0 ? (
                rutinaViendo.ejercicios.map((ej) => (
                  <tr key={ej.id} className="hover:bg-slate-50">
                    <td className="p-3 pl-6 font-bold text-slate-400">{ej.orden}</td>
                    <td className="p-3 font-bold text-slate-700">{obtenerNombreEjercicio(ej.Id_Ejercicio)}</td>
                    <td className="p-3 text-center font-medium text-slate-600">{ej.series}</td>
                    <td className="p-3 text-center font-medium text-slate-600">{ej.repeticiones}</td>
                    <td className="p-3 text-center text-sm text-slate-500">{ej.descansoSeg}s</td>
                  </tr>
                ))
              ) : (
                <tr><td colSpan="5" className="p-4 text-center text-slate-400">No hay ejercicios asignados.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    );
  }

  // ================= VISTA: TABLA PRINCIPAL =================
  return (
    <div className="flex flex-col gap-6 w-full max-w-5xl mx-auto animate-fade-in">
      <div className="flex items-center justify-between gap-4">
        <div className="relative w-80">
          <span className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 text-lg">🔍</span>
          <input type="text" placeholder="Buscar rutinas..." className="w-full pl-11 pr-4 py-2.5 bg-white border border-slate-200 rounded-full shadow-sm text-slate-700 focus:border-emerald-500 outline-none" value={busqueda} onChange={(e) => setBusqueda(e.target.value)} />
        </div>
        <button onClick={abrirModalNuevo} className="bg-emerald-600 text-white px-5 py-2.5 rounded-full text-sm font-bold hover:bg-emerald-700 shadow-sm flex items-center gap-2">
          <span>+</span> Crear Rutina
        </button>
      </div>

      <div className="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
        <div className="p-5 border-b border-slate-100 bg-slate-50">
          <h2 className="text-xl font-bold text-slate-800">Catálogo de Rutinas</h2>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-white text-slate-500 text-xs uppercase tracking-wider border-b border-slate-200">
                <th className="p-4 pl-6 text-center w-20">ID</th>
                <th className="p-4">Nombre Rutina</th>
                <th className="p-4">Nivel</th>
                <th className="p-4 text-center">Cant. Ejercicios</th>
                <th className="p-4 text-center w-72 pr-6">Acciones</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {rutinasFiltradas.length > 0 ? (
                rutinasFiltradas.map((r) => (
                  <tr key={r.id} className="hover:bg-slate-50">
                    <td className="p-4 pl-6 text-center text-slate-500">{r.id}</td>
                    <td className="p-4 font-bold text-slate-800">{r.nombre}</td>
                    <td className="p-4"><span className="bg-blue-100 text-blue-700 text-xs font-bold px-3 py-1 rounded-full">{r.nivel}</span></td>
                    <td className="p-4 text-center font-bold text-slate-500">{r.ejercicios ? r.ejercicios.length : 0}</td>
                    <td className="p-4 pr-6 text-center space-x-2">
                      <button onClick={() => verDetalles(r)} className="bg-blue-500 hover:bg-blue-600 text-white px-3 py-1.5 rounded-full text-xs font-bold shadow-sm">Ver</button>
                      <button onClick={() => abrirModalEditar(r)} className="bg-amber-500 hover:bg-amber-600 text-white px-3 py-1.5 rounded-full text-xs font-bold shadow-sm">Editar</button>
                      <button onClick={() => handleEliminar(r.id)} className="bg-red-500 hover:bg-red-600 text-white px-3 py-1.5 rounded-full text-xs font-bold shadow-sm">Eliminar</button>
                    </td>
                  </tr>
                ))
              ) : (
                <tr><td colSpan="5" className="p-8 text-center text-sm text-slate-400">No se encontraron resultados.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* ================= MODAL: CREAR / EDITAR ================= */}
      {mostrarModal && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-3xl max-h-[90vh] flex flex-col overflow-hidden">
            <div className="p-6 border-b border-slate-100 flex justify-between items-center bg-slate-50">
              <h3 className="text-xl font-bold text-slate-800">{modoEdicion ? 'Editar Rutina' : 'Armar Nueva Rutina'}</h3>
              <button onClick={() => setMostrarModal(false)} className="text-slate-400 hover:text-red-500 font-bold text-xl">&times;</button>
            </div>
            
            <div className="p-6 overflow-y-auto flex-1">
              {/* DATOS DE LA RUTINA */}
              <div className="grid grid-cols-2 gap-4 mb-4">
                <div className="col-span-2 sm:col-span-1">
                  <label className="block text-sm font-bold text-slate-700 mb-1">Nombre de Rutina</label>
                  <input type="text" required className="w-full p-2 border rounded-lg outline-none focus:border-emerald-500" value={form.nombre} onChange={e => setForm({...form, nombre: e.target.value})} />
                </div>
                <div className="col-span-2 sm:col-span-1">
                  <label className="block text-sm font-bold text-slate-700 mb-1">Nivel</label>
                  <select className="w-full p-2 border rounded-lg bg-white outline-none focus:border-emerald-500" value={form.nivel} onChange={e => setForm({...form, nivel: e.target.value})}>
                    <option value="">Seleccione nivel...</option>
                    <option value="Principiante">Principiante</option>
                    <option value="Intermedio">Intermedio</option>
                    <option value="Avanzado">Avanzado</option>
                  </select>
                </div>
                <div className="col-span-2">
                  <label className="block text-sm font-bold text-slate-700 mb-1">Descripción General</label>
                  <input type="text" className="w-full p-2 border rounded-lg outline-none focus:border-emerald-500" value={form.descripcion} onChange={e => setForm({...form, descripcion: e.target.value})} />
                </div>
              </div>

              <hr className="my-6 border-slate-200" />

              {/* AGREGAR EJERCICIOS */}
              <h4 className="font-bold text-slate-800 mb-3">Agregar Ejercicios a la Rutina</h4>
              <div className="bg-slate-100 p-4 rounded-xl flex flex-wrap gap-3 items-end mb-6">
                <div className="flex-1 min-w-[200px]">
                  <label className="block text-xs font-bold text-slate-600 mb-1">Ejercicio</label>
                  <select className="w-full p-2 border rounded-lg bg-white text-sm" value={ejercicioTemporal.Id_Ejercicio} onChange={e => setEjercicioTemporal({...ejercicioTemporal, Id_Ejercicio: e.target.value})}>
                    <option value="">Seleccionar...</option>
                    {ejerciciosDisponibles.map(ej => <option key={ej.id} value={ej.id}>{ej.nombre}</option>)}
                  </select>
                </div>
                <div className="w-20">
                  <label className="block text-xs font-bold text-slate-600 mb-1">Series</label>
                  <input type="number" min="1" className="w-full p-2 border rounded-lg text-sm text-center" value={ejercicioTemporal.series} onChange={e => setEjercicioTemporal({...ejercicioTemporal, series: e.target.value})} />
                </div>
                <div className="w-20">
                  <label className="block text-xs font-bold text-slate-600 mb-1">Reps.</label>
                  <input type="number" min="1" className="w-full p-2 border rounded-lg text-sm text-center" value={ejercicioTemporal.repeticiones} onChange={e => setEjercicioTemporal({...ejercicioTemporal, repeticiones: e.target.value})} />
                </div>
                <div className="w-24">
                  <label className="block text-xs font-bold text-slate-600 mb-1">Desc.(seg)</label>
                  <input type="number" min="0" step="15" className="w-full p-2 border rounded-lg text-sm text-center" value={ejercicioTemporal.descansoSeg} onChange={e => setEjercicioTemporal({...ejercicioTemporal, descansoSeg: e.target.value})} />
                </div>
                <button type="button" onClick={agregarEjercicioALista} className="bg-blue-600 text-white font-bold text-sm px-4 py-2 rounded-lg hover:bg-blue-700 h-[38px]">
                  Añadir
                </button>
              </div>

              {/* TABLA DE EJERCICIOS AGREGADOS */}
              {ejerciciosAsignados.length > 0 ? (
                <div className="border border-slate-200 rounded-lg overflow-hidden">
                  <table className="w-full text-left text-sm">
                    <thead className="bg-slate-50 text-slate-500 border-b border-slate-200">
                      <tr><th className="p-2 pl-4">#</th><th className="p-2">Ejercicio</th><th className="p-2 text-center">SxR</th><th className="p-2 text-center">Desc.</th><th className="p-2 text-center">Quitar</th></tr>
                    </thead>
                    <tbody className="divide-y divide-slate-100">
                      {ejerciciosAsignados.map((ej, idx) => (
                        <tr key={idx}>
                          <td className="p-2 pl-4 font-bold text-slate-400">{ej.orden}</td>
                          <td className="p-2 font-bold text-slate-700">{obtenerNombreEjercicio(ej.Id_Ejercicio)}</td>
                          <td className="p-2 text-center">{ej.series} x {ej.repeticiones}</td>
                          <td className="p-2 text-center text-slate-500">{ej.descansoSeg}s</td>
                          <td className="p-2 text-center"><button type="button" onClick={() => quitarEjercicioDeLista(idx)} className="text-red-500 hover:text-red-700 font-bold px-2">X</button></td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              ) : (
                <p className="text-center text-sm text-amber-600 font-bold bg-amber-50 p-3 rounded-lg border border-amber-200">Aún no has agregado ejercicios a esta rutina.</p>
              )}
            </div>

            <div className="p-4 border-t border-slate-100 flex justify-end gap-3 bg-slate-50">
              <button type="button" onClick={() => setMostrarModal(false)} className="px-5 py-2.5 text-slate-600 hover:bg-slate-200 rounded-lg text-sm font-bold">Cancelar</button>
              <button type="button" onClick={handleGuardar} className="bg-emerald-600 text-white px-5 py-2.5 rounded-lg text-sm font-bold hover:bg-emerald-700 shadow-md">
                Guardar Rutina Completa
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

// MÓDULO: GESTIÓN DE CLIENTES / USUARIOS

function ModuloClientes({ setLog }) {
  const [usuarios, setUsuarios] = useState([]);
  const [busqueda, setBusqueda] = useState('');
  
  const [vistaActual, setVistaActual] = useState('tabla'); 
  const [usuarioViendo, setUsuarioViendo] = useState(null);

  const [mostrarModal, setMostrarModal] = useState(false);
  const [form, setForm] = useState({ id: '', ci: '', nombre: '', apellido: '', email: '', telefono: '', fecha_nac: '', genero: '' });

  useEffect(() => { cargarDatos(); }, []);

  const cargarDatos = async () => {
    try {
      const data = await obtenerUsuarios();
      setUsuarios(data);
    } catch (error) { setLog("❌ Error al cargar usuarios."); }
  };

  const handleGuardar = async (e) => {
    e.preventDefault();
    try {
      const datosAEnviar = { 
        ...form, 
        ci: parseInt(form.ci, 10),
        telefono: form.telefono ? parseInt(form.telefono, 10) : null,
        fecha_nac: form.fecha_nac || null
      };
      
      await actualizarUsuarioAdmin(datosAEnviar);
      setLog("Datos del cliente actualizados.");
      setMostrarModal(false);
      cargarDatos();
    } catch (error) { setLog("❌ Error al actualizar el cliente."); setMostrarModal(false); }
  };

  const handleEliminar = async (id) => {
    if (window.confirm("¿Estás seguro de eliminar este cliente? Se borrarán todos sus registros asociados.")) {
      try {
        await eliminarUsuario(id);
        setLog("Cliente eliminado del sistema.");
        cargarDatos();
      } catch (error) { setLog("❌ Error al eliminar el cliente."); }
    }
  };

  const abrirModalEditar = (user) => {
    setForm({ 
      id: user.id, 
      ci: user.ci, 
      nombre: user.nombre, 
      apellido: user.apellido, 
      email: user.email, 
      telefono: user.telefono || '', 
      fecha_nac: user.fecha_nac ? user.fecha_nac.split('T')[0] : '', // Formateamos para el input type="date"
      genero: user.genero || ''
    });
    setMostrarModal(true);
  };

  const verDetalles = (user) => {
    setUsuarioViendo(user);
    setVistaActual('detalle');
  };

  // Filtramos por nombre, apellido o email
  const usuariosFiltrados = usuarios.filter(u => 
    u.nombre.toLowerCase().includes(busqueda.toLowerCase()) || 
    u.apellido.toLowerCase().includes(busqueda.toLowerCase()) ||
    u.email.toLowerCase().includes(busqueda.toLowerCase())
  );

  // ================= VISTA 1: TARJETA DE DETALLES =================
  if (vistaActual === 'detalle' && usuarioViendo) {
    return (
      <div className="bg-white p-8 rounded-2xl shadow-sm border border-slate-200 w-full max-w-4xl mx-auto animate-fade-in">
        <button onClick={() => setVistaActual('tabla')} className="text-slate-500 hover:text-emerald-600 font-bold mb-6 flex items-center gap-2">
          <span>←</span> Volver a la lista
        </button>
        
        <div className="flex justify-between items-start mb-8 pb-6 border-b border-slate-100">
          <div className="flex items-center gap-4">
            <div className="w-16 h-16 bg-emerald-100 text-emerald-600 rounded-full flex items-center justify-center text-2xl font-black">
              {usuarioViendo.nombre.charAt(0)}{usuarioViendo.apellido.charAt(0)}
            </div>
            <div>
              <h2 className="text-3xl font-black text-slate-800">{usuarioViendo.nombre} {usuarioViendo.apellido}</h2>
              <span className={`inline-block mt-1 text-xs font-bold px-3 py-1 rounded-full ${usuarioViendo.estado === 'ACTIVO' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
                {usuarioViendo.estado || 'ACTIVO'}
              </span>
            </div>
          </div>
          <div className="text-right">
            <p className="text-sm font-bold text-slate-400 uppercase tracking-wide">ID Usuario</p>
            <p className="text-2xl font-black text-slate-700">#{usuarioViendo.id}</p>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="bg-slate-50 p-6 rounded-xl border border-slate-100">
            <h4 className="text-sm font-bold text-slate-400 mb-4 uppercase tracking-wide flex items-center gap-2"><span>📋</span> Datos Personales</h4>
            <div className="space-y-3">
              <p className="flex justify-between"><span className="text-slate-500">Cédula (CI):</span> <span className="font-bold text-slate-800">{usuarioViendo.ci}</span></p>
              <p className="flex justify-between"><span className="text-slate-500">Género:</span> <span className="font-bold text-slate-800">{usuarioViendo.genero || 'No especificado'}</span></p>
              <p className="flex justify-between"><span className="text-slate-500">Fecha de Nac.:</span> <span className="font-bold text-slate-800">{usuarioViendo.fecha_nac || 'No registrada'}</span></p>
            </div>
          </div>
          
          <div className="bg-slate-50 p-6 rounded-xl border border-slate-100">
            <h4 className="text-sm font-bold text-slate-400 mb-4 uppercase tracking-wide flex items-center gap-2"><span>📱</span> Contacto</h4>
            <div className="space-y-3">
              <p className="flex justify-between items-center"><span className="text-slate-500">Email:</span> <span className="font-bold text-slate-800 truncate ml-2 max-w-[200px]">{usuarioViendo.email}</span></p>
              <p className="flex justify-between"><span className="text-slate-500">Teléfono:</span> <span className="font-bold text-slate-800">{usuarioViendo.telefono || 'No registrado'}</span></p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  // ================= VISTA 2: TABLA PRINCIPAL =================
  return (
    <div className="flex flex-col gap-6 w-full max-w-6xl mx-auto animate-fade-in">
      <div className="flex items-center justify-between gap-4">
        <div className="relative w-96">
          <span className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 text-lg">🔍</span>
          <input type="text" placeholder="Buscar por nombre, apellido o correo..." className="w-full pl-11 pr-4 py-2.5 bg-white border border-slate-200 rounded-full shadow-sm text-slate-700 focus:border-emerald-500 outline-none" value={busqueda} onChange={(e) => setBusqueda(e.target.value)} />
        </div>
      </div>

      <div className="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
        <div className="p-5 border-b border-slate-100 bg-slate-50">
          <h2 className="text-xl font-bold text-slate-800">Directorio de Clientes</h2>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-white text-slate-500 text-xs uppercase tracking-wider border-b border-slate-200">
                <th className="p-4 pl-6">Cliente</th>
                <th className="p-4">Email</th>
                <th className="p-4">C.I.</th>
                <th className="p-4 text-center">Estado</th>
                <th className="p-4 text-center w-64 pr-6">Acciones</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {usuariosFiltrados.length > 0 ? (
                usuariosFiltrados.map((u) => (
                  <tr key={u.id} className="hover:bg-slate-50">
                    <td className="p-4 pl-6">
                      <div className="font-bold text-slate-800">{u.nombre} {u.apellido}</div>
                      <div className="text-xs text-slate-400 text-emerald-600 font-medium">{u.genero}</div>
                    </td>
                    <td className="p-4">
                      <div className="text-sm font-medium text-slate-700">{u.email}</div>
                      <div className="text-xs text-slate-400">{u.telefono ? `📞 ${u.telefono}` : 'Sin teléfono'}</div>
                    </td>
                    <td className="p-4 text-sm font-bold text-slate-500">{u.ci}</td>
                    <td className="p-4 text-center">
                      <span className={`text-xs font-bold px-3 py-1 rounded-full ${u.estado === 'ACTIVO' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
                        {u.estado || 'ACTIVO'}
                      </span>
                    </td>
                    <td className="p-4 pr-6 text-center space-x-2">
                      <button onClick={() => verDetalles(u)} className="bg-blue-500 hover:bg-blue-600 text-white px-3 py-1.5 rounded-full text-xs font-bold shadow-sm">Ver</button>
                      <button onClick={() => abrirModalEditar(u)} className="bg-amber-500 hover:bg-amber-600 text-white px-3 py-1.5 rounded-full text-xs font-bold shadow-sm">Editar</button>
                      <button onClick={() => handleEliminar(u.id)} className="bg-red-500 hover:bg-red-600 text-white px-3 py-1.5 rounded-full text-xs font-bold shadow-sm">Eliminar</button>
                    </td>
                  </tr>
                ))
              ) : (
                <tr><td colSpan="5" className="p-8 text-center text-sm text-slate-400">No se encontraron clientes registrados.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* ================= MODAL: EDITAR DATOS BÁSICOS ================= */}
      {mostrarModal && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-2xl max-h-[90vh] flex flex-col overflow-hidden">
            <div className="p-6 border-b border-slate-100 flex justify-between items-center bg-slate-50">
              <h3 className="text-xl font-bold text-slate-800">Modificar Datos de Cliente</h3>
              <button onClick={() => setMostrarModal(false)} className="text-slate-400 hover:text-red-500 font-bold text-xl">&times;</button>
            </div>
            
            <form onSubmit={handleGuardar} className="p-6 overflow-y-auto flex-1 space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-bold text-slate-700 mb-1">Nombre</label>
                  <input type="text" required className="w-full p-2 border rounded-lg outline-none focus:border-emerald-500" value={form.nombre} onChange={e => setForm({...form, nombre: e.target.value})} />
                </div>
                <div>
                  <label className="block text-sm font-bold text-slate-700 mb-1">Apellido</label>
                  <input type="text" required className="w-full p-2 border rounded-lg outline-none focus:border-emerald-500" value={form.apellido} onChange={e => setForm({...form, apellido: e.target.value})} />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-bold text-slate-700 mb-1">C.I.</label>
                  <input type="number" required className="w-full p-2 border rounded-lg outline-none focus:border-emerald-500" value={form.ci} onChange={e => setForm({...form, ci: e.target.value})} />
                </div>
                <div>
                  <label className="block text-sm font-bold text-slate-700 mb-1">Correo Electrónico</label>
                  <input type="email" required className="w-full p-2 border rounded-lg outline-none focus:border-emerald-500 bg-slate-100 text-slate-500" value={form.email} readOnly title="El correo no se puede modificar por seguridad" />
                </div>
              </div>

              <div className="grid grid-cols-3 gap-4">
                <div>
                  <label className="block text-sm font-bold text-slate-700 mb-1">Teléfono</label>
                  <input type="number" className="w-full p-2 border rounded-lg outline-none focus:border-emerald-500" value={form.telefono} onChange={e => setForm({...form, telefono: e.target.value})} />
                </div>
                <div>
                  <label className="block text-sm font-bold text-slate-700 mb-1">Fecha Nac.</label>
                  <input type="date" className="w-full p-2 border rounded-lg outline-none focus:border-emerald-500" value={form.fecha_nac} onChange={e => setForm({...form, fecha_nac: e.target.value})} />
                </div>
                <div>
                  <label className="block text-sm font-bold text-slate-700 mb-1">Género</label>
                  <select className="w-full p-2 border rounded-lg bg-white outline-none focus:border-emerald-500" value={form.genero} onChange={e => setForm({...form, genero: e.target.value})}>
                    <option value="">Seleccione...</option>
                    <option value="Masculino">Masculino</option>
                    <option value="Femenino">Femenino</option>
                  </select>
                </div>
              </div>

              <div className="flex justify-end gap-3 pt-6 mt-6 border-t border-slate-100">
                <button type="button" onClick={() => setMostrarModal(false)} className="px-5 py-2.5 text-slate-600 hover:bg-slate-200 rounded-lg text-sm font-bold">Cancelar</button>
                <button type="submit" className="bg-emerald-600 text-white px-5 py-2.5 rounded-lg text-sm font-bold hover:bg-emerald-700 shadow-md">Guardar Cambios</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}


// MÓDULO: ASIGNACIÓN DE RUTINAS (Instructor)

function ModuloAsignaciones({ setLog }) {
  const [asignaciones, setAsignaciones] = useState([]);
  const [clientes, setClientes] = useState([]);
  const [rutinas, setRutinas] = useState([]);
  const [busqueda, setBusqueda] = useState('');
  
  const [mostrarModal, setMostrarModal] = useState(false);
  const [form, setForm] = useState({ Id_Usuario: '', Id_Rutina: '', fechaInicio: '', fechaFin: '', estado: 'ACTIVA' });

  useEffect(() => { cargarDatos(); }, []);

  const cargarDatos = async () => {
    try {
      // Cargamos las asignaciones, y también los catálogos para traducir IDs a Nombres
      const dataAsignaciones = await obtenerAsignaciones();
      const dataUsuarios = await obtenerUsuarios();
      const dataRutinas = await obtenerRutinas();
      
      setAsignaciones(dataAsignaciones);
      setClientes(dataUsuarios);
      setRutinas(dataRutinas);
    } catch (error) { setLog("❌ Error al cargar datos de asignaciones."); }
  };

  const obtenerNombreCliente = (id) => {
    const c = clientes.find(x => x.id === id);
    return c ? `${c.nombre} ${c.apellido}` : 'Desconocido';
  };

  const obtenerNombreRutina = (id) => {
    const r = rutinas.find(x => x.id === id);
    return r ? r.nombre : 'Desconocida';
  };

  const handleGuardar = async (e) => {
    e.preventDefault();
    try {
      const datosAEnviar = { 
        Id_Usuario: parseInt(form.Id_Usuario, 10),
        Id_Rutina: parseInt(form.Id_Rutina, 10),
        fechaInicio: form.fechaInicio,
        fechaFin: form.fechaFin,
        estado: form.estado
      };
      
      await crearAsignacion(datosAEnviar);
      setLog("Rutina asignada exitosamente al cliente.");
      setMostrarModal(false);
      cargarDatos();
    } catch (error) { setLog("❌ Error al asignar la rutina."); setMostrarModal(false); }
  };

  const abrirModalNuevo = () => {
    setForm({ Id_Usuario: '', Id_Rutina: '', fechaInicio: '', fechaFin: '', estado: 'ACTIVA' });
    setMostrarModal(true);
  };

  const asignacionesFiltradas = asignaciones.filter(a => 
    obtenerNombreCliente(a.Id_Usuario).toLowerCase().includes(busqueda.toLowerCase()) ||
    obtenerNombreRutina(a.Id_Rutina).toLowerCase().includes(busqueda.toLowerCase())
  );

  return (
    <div className="flex flex-col gap-6 w-full max-w-6xl mx-auto animate-fade-in">
      <div className="flex items-center justify-between gap-4">
        <div className="relative w-96">
          <span className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 text-lg">🔍</span>
          <input type="text" placeholder="Buscar por cliente o rutina..." className="w-full pl-11 pr-4 py-2.5 bg-white border border-slate-200 rounded-full shadow-sm text-slate-700 focus:border-emerald-500 outline-none" value={busqueda} onChange={(e) => setBusqueda(e.target.value)} />
        </div>
        <button onClick={abrirModalNuevo} className="bg-emerald-600 text-white px-5 py-2.5 rounded-full text-sm font-bold hover:bg-emerald-700 shadow-sm flex items-center gap-2">
          <span>+</span> Nueva Asignación
        </button>
      </div>

      <div className="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
        <div className="p-5 border-b border-slate-100 bg-slate-50">
          <h2 className="text-xl font-bold text-slate-800">Historial de Asignaciones</h2>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-white text-slate-500 text-xs uppercase tracking-wider border-b border-slate-200">
                <th className="p-4 pl-6 text-center w-20">ID</th>
                <th className="p-4">Cliente</th>
                <th className="p-4">Rutina Asignada</th>
                <th className="p-4 text-center">Periodo</th>
                <th className="p-4 text-center">Estado</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {asignacionesFiltradas.length > 0 ? (
                asignacionesFiltradas.map((a) => (
                  <tr key={a.id} className="hover:bg-slate-50">
                    <td className="p-4 pl-6 text-center text-slate-500 font-bold">{a.id}</td>
                    <td className="p-4 font-bold text-slate-800">{obtenerNombreCliente(a.Id_Usuario)}</td>
                    <td className="p-4 text-emerald-700 font-bold">{obtenerNombreRutina(a.Id_Rutina)}</td>
                    <td className="p-4 text-center text-sm text-slate-600">
                      <div className="font-medium">{a.fechaInicio}</div>
                      <div className="text-xs text-slate-400">hasta {a.fechaFin}</div>
                    </td>
                    <td className="p-4 text-center">
                      <span className={`text-xs font-bold px-3 py-1 rounded-full ${a.estado === 'ACTIVA' ? 'bg-green-100 text-green-700' : 'bg-slate-100 text-slate-500'}`}>
                        {a.estado}
                      </span>
                    </td>
                  </tr>
                ))
              ) : (
                <tr><td colSpan="5" className="p-8 text-center text-sm text-slate-400">No hay asignaciones registradas.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {mostrarModal && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-lg flex flex-col overflow-hidden">
            <div className="p-6 border-b border-slate-100 flex justify-between items-center bg-slate-50">
              <h3 className="text-xl font-bold text-slate-800">Vincular Rutina a Cliente</h3>
              <button onClick={() => setMostrarModal(false)} className="text-slate-400 hover:text-red-500 font-bold text-xl">&times;</button>
            </div>
            
            <form onSubmit={handleGuardar} className="p-6 space-y-5">
              <div>
                <label className="block text-sm font-bold text-slate-700 mb-1">Seleccionar Cliente</label>
                <select required className="w-full p-3 border border-slate-200 rounded-lg outline-none focus:border-emerald-500 bg-white" value={form.Id_Usuario} onChange={e => setForm({...form, Id_Usuario: e.target.value})}>
                  <option value="">-- Elija un cliente --</option>
                  {clientes.map(c => <option key={c.id} value={c.id}>{c.nombre} {c.apellido} (CI: {c.ci})</option>)}
                </select>
              </div>

              <div>
                <label className="block text-sm font-bold text-slate-700 mb-1">Seleccionar Rutina</label>
                <select required className="w-full p-3 border border-slate-200 rounded-lg outline-none focus:border-emerald-500 bg-white" value={form.Id_Rutina} onChange={e => setForm({...form, Id_Rutina: e.target.value})}>
                  <option value="">-- Elija una rutina --</option>
                  {rutinas.map(r => <option key={r.id} value={r.id}>{r.nombre} ({r.nivel})</option>)}
                </select>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-bold text-slate-700 mb-1">Fecha de Inicio</label>
                  <input type="date" required className="w-full p-3 border border-slate-200 rounded-lg outline-none focus:border-emerald-500" value={form.fechaInicio} onChange={e => setForm({...form, fechaInicio: e.target.value})} />
                </div>
                <div>
                  <label className="block text-sm font-bold text-slate-700 mb-1">Fecha de Fin</label>
                  <input type="date" required className="w-full p-3 border border-slate-200 rounded-lg outline-none focus:border-emerald-500" value={form.fechaFin} onChange={e => setForm({...form, fechaFin: e.target.value})} />
                </div>
              </div>

              <div className="flex justify-end gap-3 pt-4 border-t border-slate-100 mt-6">
                <button type="button" onClick={() => setMostrarModal(false)} className="px-5 py-2.5 text-slate-600 hover:bg-slate-200 rounded-lg text-sm font-bold">Cancelar</button>
                <button type="submit" className="bg-emerald-600 text-white px-5 py-2.5 rounded-lg text-sm font-bold hover:bg-emerald-700 shadow-md">Asignar Rutina</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

// MÓDULO: PORTAL DE CLIENTE (Mi Entrenamiento - Diseño Compacto y Videos)

function PortalCliente({ usuario, setLog }) {
  const [misAsignaciones, setMisAsignaciones] = useState([]);
  const [rutinas, setRutinas] = useState([]);
  const [ejercicios, setEjercicios] = useState([]);
  
  const [vistaActual, setVistaActual] = useState('tabla'); 
  const [asignacionViendo, setAsignacionViendo] = useState(null);
  const [rutinaViendo, setRutinaViendo] = useState(null);
  // Plan ajustado por la estrategia según el nivel de la rutina (CU6)
  const [plan, setPlan] = useState(null);

  useEffect(() => { cargarMiEntrenamiento(); }, []);

  const cargarMiEntrenamiento = async () => {
    try {
      const dataAsignaciones = await obtenerAsignaciones();
      const dataRutinas = await obtenerRutinas();
      const dataEjercicios = await obtenerEjercicios();

      const misAsig = dataAsignaciones.filter(a => a.Id_Usuario === usuario.id);
      
      setMisAsignaciones(misAsig);
      setRutinas(dataRutinas);
      setEjercicios(dataEjercicios);
    } catch (error) {
      setLog("❌ Error al cargar tu plan de entrenamiento.");
    }
  };

  const obtenerNombreRutina = (id) => {
    const r = rutinas.find(x => x.id === id);
    return r ? r.nombre : 'Desconocida';
  };

  const obtenerEjercicioCompleto = (id) => {
    const e = ejercicios.find(x => x.id === id);
    return e ? e : { nombre: 'Ejercicio Desconocido', videoURL: '' };
  };

  const verDetalles = async (asignacion) => {
    const r = rutinas.find(x => x.id === asignacion.Id_Rutina);
    setAsignacionViendo(asignacion);
    setRutinaViendo(r);
    setPlan(null);
    setVistaActual('detalle');
    try {
      const planAjustado = await obtenerPlanAsignacion(asignacion.id);
      setPlan(planAjustado);
    } catch (error) {
      // Si no se puede calcular el plan, se muestran los valores base de la rutina
    }
  };

  // Valores ajustados de un ejercicio (si hay plan); si no, los de la rutina
  const obtenerAjuste = (idEjercicio) => {
    if (!plan || !plan.ejercicios) return null;
    return plan.ejercicios.find(x => x.Id_Ejercicio === idEjercicio) || null;
  };

  const registrarEntrenamientoDeHoy = async () => {
    try {
      const hoy = new Date();
      const fechaLocal = hoy.getFullYear() + '-' + String(hoy.getMonth() + 1).padStart(2, '0') + '-' + String(hoy.getDate()).padStart(2, '0');

      const nuevaSesion = {
        Id_Asignacion: asignacionViendo.id,
        fecha: fechaLocal,
        estado: 'COMPLETADO'
      };
      
      await crearSesion(nuevaSesion); 
      setLog("✅ Asistencia registrada en la base de datos.");
      alert("¡Asistencia registrada exitosamente!");
    } catch (error) {
      setLog("❌ Error al registrar la sesión en la base de datos.");
    }
  };

  // ================= VISTA 1: DETALLE DE LA RUTINA (TARJETA COMPACTA) =================
  if (vistaActual === 'detalle' && asignacionViendo && rutinaViendo) {
    return (
      // Cambiamos max-w-4xl a max-w-3xl para que sea más compacta y bonita
      <div className="bg-white p-6 rounded-2xl shadow-sm border border-slate-200 w-full max-w-3xl mx-auto animate-fade-in mt-6">
        <button onClick={() => setVistaActual('tabla')} className="text-slate-500 hover:text-emerald-600 font-bold mb-4 flex items-center gap-2 transition-colors text-sm">
          <span>←</span> Volver a mis rutinas
        </button>
        
        <div className="flex justify-between items-start mb-6 border-b border-slate-100 pb-6">
          <div>
            <span className="inline-block mb-2 bg-blue-100 text-blue-800 font-bold px-3 py-1 rounded-full text-xs uppercase tracking-wider">
              Nivel: {rutinaViendo.nivel}
            </span>
            <h2 className="text-2xl font-black text-slate-800">{rutinaViendo.nombre}</h2>
            <p className="text-slate-500 mt-1 text-sm">{rutinaViendo.descripcion}</p>
          </div>
          
          {/* EL BOTÓN QUE LLENA TU TABLA: Ahora es pequeño, discreto y claro */}
          <button onClick={registrarEntrenamientoDeHoy} className="bg-emerald-600 text-white px-4 py-2 rounded-lg text-sm font-bold shadow-sm hover:bg-emerald-700 transition-colors flex items-center gap-2">
            <span>✓</span> Registrar Asistencia
          </button>
        </div>

        {plan && plan.plan && (
          <div className="mb-6 bg-emerald-50 border border-emerald-200 rounded-xl p-4 flex flex-wrap gap-x-8 gap-y-2 items-center">
            <div>
              <p className="text-xs font-bold text-emerald-700 uppercase tracking-wider">Tu plan ajustado</p>
              <p className="text-lg font-black text-slate-800">{plan.plan.estrategia}</p>
            </div>
            <div className="text-sm text-slate-600"><span className="font-bold text-slate-800">{plan.plan.sesionesPorSemana}</span> sesiones por semana</div>
            <div className="text-sm text-slate-600"><span className="font-bold text-slate-800">{plan.plan.sesionesEsperadas}</span> sesiones esperadas en el periodo</div>
          </div>
        )}

        <h3 className="text-sm font-bold text-slate-800 mb-3 uppercase tracking-wider">Ejercicios de la Sesión</h3>
        <div className="bg-slate-50 rounded-xl border border-slate-200 overflow-hidden">
          <table className="w-full text-left">
            <thead className="border-b border-slate-200 text-slate-500 text-xs uppercase tracking-wider">
              <tr>
                <th className="p-3 pl-4 w-16 text-center">Orden</th>
                <th className="p-3">Ejercicio</th>
                <th className="p-3 text-center">Series x Reps</th>
                <th className="p-3 text-center">Descanso</th>
                <th className="p-3 text-center">Guía</th> {/* NUEVA COLUMNA VIDEO */}
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200">
              {rutinaViendo.ejercicios && rutinaViendo.ejercicios.length > 0 ? (
                rutinaViendo.ejercicios.map((ej) => {
                  const ejCompleto = obtenerEjercicioCompleto(ej.Id_Ejercicio);
                  const ajuste = obtenerAjuste(ej.Id_Ejercicio);
                  const seriesMostrar = ajuste ? ajuste.seriesAjustadas : ej.series;
                  const descansoMostrar = ajuste ? ajuste.descansoAjustadoSeg : ej.descansoSeg;
                  return (
                    <tr key={ej.id} className="hover:bg-white transition-colors">
                      <td className="p-3 pl-4 font-black text-slate-400 text-center">{ej.orden}</td>
                      <td className="p-3 font-bold text-slate-700 text-sm">{ejCompleto.nombre}</td>
                      <td className="p-3 text-center font-medium text-slate-600 text-sm">
                        {seriesMostrar} x {ej.repeticiones}
                        {ajuste && ajuste.seriesAjustadas !== ej.series && <span className="block text-xs text-slate-400 font-normal">base: {ej.series}</span>}
                      </td>
                      <td className="p-3 text-center text-xs text-slate-500">
                        {descansoMostrar} seg
                        {ajuste && ajuste.descansoAjustadoSeg !== ej.descansoSeg && <span className="block text-slate-400">base: {ej.descansoSeg}</span>}
                      </td>
                      <td className="p-3 text-center">
                        {/* BOTÓN DE VIDEO */}
                        {ejCompleto.videoURL ? (
                          <a href={ejCompleto.videoURL} target="_blank" rel="noreferrer" className="inline-flex items-center gap-1 bg-red-100 text-red-600 hover:bg-red-200 px-3 py-1 rounded-full text-xs font-bold transition-colors">
                            ▶ Video
                          </a>
                        ) : (
                          <span className="text-slate-300 text-xs">-</span>
                        )}
                      </td>
                    </tr>
                  );
                })
              ) : (
                <tr><td colSpan="5" className="p-6 text-center text-slate-400 text-sm">Tu instructor no agregó ejercicios a esta rutina.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    );
  }

  // ================= VISTA 2: TABLA PRINCIPAL DE ASIGNACIONES =================
  return (
    <div className="flex flex-col gap-6 w-full max-w-5xl mx-auto animate-fade-in mt-6">
      <div className="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
        <div className="p-6 border-b border-slate-100 bg-slate-50 flex justify-between items-center">
          <h2 className="text-xl font-bold text-slate-800">Mis Rutinas Asignadas</h2>
          <span className="text-sm font-bold text-emerald-600">{usuario.nombre} {usuario.apellido}</span>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-white text-slate-500 text-xs uppercase tracking-wider border-b border-slate-200">
                <th className="p-4 pl-6">Rutina</th>
                <th className="p-4 text-center">Fecha Inicio</th>
                <th className="p-4 text-center">Fecha Fin</th>
                <th className="p-4 text-center">Estado</th>
                <th className="p-4 text-center pr-6">Acción</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {misAsignaciones.length > 0 ? (
                misAsignaciones.map((a) => (
                  <tr key={a.id} className="hover:bg-slate-50 transition-colors">
                    <td className="p-4 pl-6 font-bold text-slate-800 text-lg">{obtenerNombreRutina(a.Id_Rutina)}</td>
                    <td className="p-4 text-center text-slate-600">{a.fechaInicio}</td>
                    <td className="p-4 text-center text-slate-600">{a.fechaFin}</td>
                    <td className="p-4 text-center">
                      <span className={`text-xs font-bold px-3 py-1 rounded-full ${a.estado === 'ACTIVA' ? 'bg-green-100 text-green-700' : 'bg-slate-100 text-slate-500'}`}>
                        {a.estado}
                      </span>
                    </td>
                    <td className="p-4 pr-6 text-center">
                      <button onClick={() => verDetalles(a)} className="bg-blue-600 hover:bg-blue-700 text-white px-5 py-2 rounded-full text-xs font-bold transition-colors shadow-sm">
                        Ver Detalles
                      </button>
                    </td>
                  </tr>
                ))
              ) : (
                <tr><td colSpan="5" className="p-8 text-center text-sm text-slate-400">No tienes rutinas asignadas actualmente.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

// MÓDULO: CAMPANA DE NOTIFICACIONES (patrón Observer: los avisos los genera MS Entrenamiento)

function CampanaNotificaciones({ usuario, rol }) {
  const [abierta, setAbierta] = useState(false);
  const [notificaciones, setNotificaciones] = useState([]);

  const cargarNotificaciones = async () => {
    try {
      let lista = await obtenerNotificacionesUsuario(usuario.id);
      // El instructor también recibe los avisos dirigidos al rol INSTRUCTOR
      if (rol === 'instructor') {
        const porRol = await obtenerNotificacionesRol('INSTRUCTOR');
        lista = [...lista, ...porRol];
      }
      const unicas = lista.filter((n, i) => lista.findIndex(x => x.id === n.id) === i);
      unicas.sort((a, b) => b.id - a.id);
      setNotificaciones(unicas);
    } catch (error) {
      // Si MS Notificaciones no está disponible, la campana simplemente queda vacía
    }
  };

  useEffect(() => {
    cargarNotificaciones();
    const intervalo = setInterval(cargarNotificaciones, 15000);
    return () => clearInterval(intervalo);
  }, []);

  const marcarLeida = async (id) => {
    try {
      await marcarNotificacionLeida(id);
      setNotificaciones(prev => prev.map(n => n.id === id ? { ...n, leida: true } : n));
    } catch (error) { /* se mantiene como no leída */ }
  };

  const marcarTodasLeidas = async () => {
    const pendientes = notificaciones.filter(n => !n.leida);
    for (const n of pendientes) {
      await marcarLeida(n.id);
    }
  };

  const sinLeer = notificaciones.filter(n => !n.leida).length;

  return (
    <div className="relative">
      <button
        onClick={() => { setAbierta(!abierta); if (!abierta) cargarNotificaciones(); }}
        className="relative p-2 rounded-full hover:bg-slate-100 transition-colors text-2xl leading-none"
        title="Notificaciones"
      >
        🔔
        {sinLeer > 0 && (
          <span className="absolute -top-1 -right-1 bg-red-600 text-white text-xs font-bold rounded-full min-w-5 h-5 px-1 flex items-center justify-center">
            {sinLeer}
          </span>
        )}
      </button>

      {abierta && (
        <div className="absolute right-0 top-full mt-2 w-96 max-h-96 overflow-y-auto bg-white border border-slate-200 rounded-xl shadow-xl z-50">
          <div className="p-3 border-b border-slate-100 bg-slate-50 flex justify-between items-center sticky top-0">
            <span className="font-bold text-slate-800 text-sm">Notificaciones</span>
            {sinLeer > 0 && (
              <button onClick={marcarTodasLeidas} className="text-xs font-bold text-blue-600 hover:underline">Marcar todas como leídas</button>
            )}
          </div>
          {notificaciones.length === 0 ? (
            <p className="p-6 text-center text-sm text-slate-400">No tienes notificaciones.</p>
          ) : (
            <ul className="divide-y divide-slate-100">
              {notificaciones.map(n => (
                <li key={n.id} className={`p-3 text-sm ${n.leida ? 'bg-white text-slate-500' : 'bg-blue-50 text-slate-800'}`}>
                  <div className="flex justify-between gap-2 mb-1">
                    <span className="text-xs font-bold uppercase tracking-wider text-emerald-700">{n.tipo === 'ASIGNACION_REGISTRADA' ? 'Nueva rutina' : 'Sesión completada'}</span>
                    <span className="text-xs text-slate-400">{n.fechaCreacion}</span>
                  </div>
                  <p>{n.mensaje}</p>
                  {!n.leida && (
                    <button onClick={() => marcarLeida(n.id)} className="mt-2 text-xs font-bold text-blue-600 hover:underline">Marcar como leída</button>
                  )}
                </li>
              ))}
            </ul>
          )}
        </div>
      )}
    </div>
  );
}

// COMPONENTE PRINCIPAL APP

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [authMode, setAuthMode] = useState('login'); 
  const [rol, setRol] = useState('');
  const [activeTab, setActiveTab] = useState('categorias');
  const [usuarioActivo, setUsuarioActivo] = useState(null);
  const [datosUsuario, setDatosUsuario] = useState(null);
  const [log, setLog] = useState('Esperando acciones...');

  const [loginEmail, setLoginEmail] = useState('');
  const [loginPass, setLoginPass] = useState('');
  
  const [nuevoCliente, setNuevoCliente] = useState({
    ci: '', nombre: '', apellido: '', email: '', contrasena: '', telefono: '', fecha_nac: '', genero: ''
  });

  const handleLogin = async (e) => {
    e.preventDefault();
    setLog("Validando credenciales...");
    try {
      const credenciales = { email: loginEmail, contrasena: loginPass };
      
      // Volvemos a usar la función loginUsuario que apunta al backend
      const respuesta = await loginUsuario(credenciales);
      const roles = respuesta.roles || []; 
      
      if (roles.includes("INSTRUCTOR")) {
        setRol('instructor');
        setLog(`¡Bienvenido Instructor ${respuesta.usuario.nombre}!`);
      } else {
        setRol('cliente');
        setLog(`¡Bienvenido Cliente ${respuesta.usuario.nombre}!`);
      }
      setUsuarioActivo(respuesta.usuario.email);
      setDatosUsuario(respuesta.usuario);
      setIsAuthenticated(true);
    } catch (error) {
      setLog("❌ Error: Correo o contraseña incorrectos.");
    }
  };

  const handleRegistroCliente = async (e) => {
    e.preventDefault();
    try {
      let datosAEnviar = { ...nuevoCliente, estado: 'ACTIVO' };
      datosAEnviar.ci = parseInt(datosAEnviar.ci, 10);
      
      if (datosAEnviar.telefono === "") datosAEnviar.telefono = null; 
      else datosAEnviar.telefono = parseInt(datosAEnviar.telefono, 10);

      if (datosAEnviar.fecha_nac === "") datosAEnviar.fecha_nac = null;

      await registrarUsuario(datosAEnviar);
      setLog("¡Registro exitoso! Ya puedes iniciar sesión.");
      setAuthMode('login'); 
      setLoginEmail(nuevoCliente.email);
    } catch (error) {
      setLog("❌ Error al registrar. Revisa los datos.");
    }
  };

  const cerrarSesion = () => {
    setIsAuthenticated(false);
    setLoginEmail(''); setLoginPass(''); setRol(''); setUsuarioActivo(null);
  };

  // PANTALLAS DE ACCESO
  if (!isAuthenticated) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-slate-100 p-4">
        <div className="bg-white p-8 rounded-xl shadow-lg w-full max-w-md border border-slate-200">
          <div className="text-center mb-8">
            <h1 className="text-3xl font-black text-slate-800">GymApp</h1>
            <p className="text-slate-500 mt-2 text-sm">
              {authMode === 'login' ? 'Ingresa a tu cuenta' : 'Crea tu cuenta de Cliente'}
            </p>
          </div>
          
          {log !== 'Esperando acciones...' && (
            <div className={`mb-4 p-3 rounded text-sm text-center font-bold ${log.includes('Error') || log.includes('❌') ? 'bg-red-100 text-red-700 border border-red-200' : 'bg-green-100 text-green-700 border border-green-200'}`}>
              {log}
            </div>
          )}

          {authMode === 'login' ? (
            <form onSubmit={handleLogin} className="space-y-4">
              <div>
                <label className="block text-sm font-bold text-slate-700 mb-1">Correo Electrónico</label>
                <input type="email" required className="w-full p-3 border rounded bg-slate-50" value={loginEmail} onChange={e => setLoginEmail(e.target.value)} />
              </div>
              <div>
                <label className="block text-sm font-bold text-slate-700 mb-1">Contraseña</label>
                <input type="password" required className="w-full p-3 border rounded bg-slate-50" value={loginPass} onChange={e => setLoginPass(e.target.value)} />
              </div>
              <button type="submit" className="w-full bg-blue-600 text-white font-bold p-3 rounded hover:bg-blue-700 transition">Iniciar Sesión</button>
              <p className="text-center text-sm text-slate-600 mt-4">
                ¿No tienes cuenta? <span className="text-blue-600 font-bold cursor-pointer hover:underline" onClick={() => setAuthMode('register')}>Regístrate aquí</span>
              </p>
            </form>
          ) : (
            <form onSubmit={handleRegistroCliente} className="space-y-4">
               {/* Formulario de registro resumido por espacio, lo mantuve intacto para ti */}
               <div className="grid grid-cols-2 gap-4">
                <div><label className="block text-xs font-bold text-slate-700 mb-1">CI</label><input type="text" inputMode="numeric" pattern="[0-9]*" required className="w-full p-2 border rounded bg-slate-50" value={nuevoCliente.ci} onChange={e => setNuevoCliente({...nuevoCliente, ci: e.target.value})} /></div>
                <div><label className="block text-xs font-bold text-slate-700 mb-1">Teléfono</label><input type="text" inputMode="numeric" pattern="[0-9]*" className="w-full p-2 border rounded bg-slate-50" value={nuevoCliente.telefono} onChange={e => setNuevoCliente({...nuevoCliente, telefono: e.target.value})} /></div>
                <div><label className="block text-xs font-bold text-slate-700 mb-1">Nombre</label><input type="text" required className="w-full p-2 border rounded bg-slate-50" value={nuevoCliente.nombre} onChange={e => setNuevoCliente({...nuevoCliente, nombre: e.target.value})} /></div>
                <div><label className="block text-xs font-bold text-slate-700 mb-1">Apellido</label><input type="text" required className="w-full p-2 border rounded bg-slate-50" value={nuevoCliente.apellido} onChange={e => setNuevoCliente({...nuevoCliente, apellido: e.target.value})} /></div>
              </div>
              <div><label className="block text-xs font-bold text-slate-700 mb-1">Correo</label><input type="email" required className="w-full p-2 border rounded bg-slate-50" value={nuevoCliente.email} onChange={e => setNuevoCliente({...nuevoCliente, email: e.target.value})} /></div>
              <div><label className="block text-xs font-bold text-slate-700 mb-1">Contraseña</label><input type="password" required className="w-full p-2 border rounded bg-slate-50" value={nuevoCliente.contrasena} onChange={e => setNuevoCliente({...nuevoCliente, contrasena: e.target.value})} /></div>
              <div className="grid grid-cols-2 gap-4">
                <div><label className="block text-xs font-bold text-slate-700 mb-1">F. Nacimiento</label><input type="date" className="w-full p-2 border rounded bg-slate-50" value={nuevoCliente.fecha_nac} onChange={e => setNuevoCliente({...nuevoCliente, fecha_nac: e.target.value})} /></div>
                <div>
                  <label className="block text-xs font-bold text-slate-700 mb-1">Género</label>
                  <select className="w-full p-2 border rounded bg-slate-50" value={nuevoCliente.genero} onChange={e => setNuevoCliente({...nuevoCliente, genero: e.target.value})}>
                    <option value="">Seleccione...</option><option value="Masculino">Masculino</option><option value="Femenino">Femenino</option>
                  </select>
                </div>
              </div>
              <button type="submit" className="w-full bg-green-600 text-white font-bold p-3 rounded hover:bg-green-700 transition mt-2">Crear Cuenta</button>
              <p className="text-center text-sm text-slate-600 mt-4">¿Ya tienes cuenta? <span className="text-blue-600 font-bold cursor-pointer hover:underline" onClick={() => setAuthMode('login')}>Inicia Sesión</span></p>
            </form>
          )}
        </div>
      </div>
    );
  }

  // INTERFAZ PRINCIPAL (LOGUEADO)
  return (
    <div className="flex h-screen bg-gray-100 font-sans">
      {/* MENÚ LATERAL RESTAURADO */}
      <div className="w-64 bg-slate-900 text-white flex flex-col justify-between shrink-0">
        <div>
          <div className="p-6 bg-slate-950 text-center font-bold text-lg border-b border-slate-800">GymApp 2.0</div>
          <div className="p-4 border-b border-slate-800 bg-slate-800/50">
            <p className="text-xs text-slate-400">Usuario activo:</p>
            <p className="font-bold truncate">{usuarioActivo}</p>
          </div>

          {rol === 'instructor' ? (
            <ul className="py-2">
              <li className={`px-6 py-3 cursor-pointer ${activeTab === 'categorias' ? 'bg-emerald-600 font-bold' : 'hover:bg-slate-800'}`} onClick={() => setActiveTab('categorias')}>Categorías</li>
              <li className={`px-6 py-3 cursor-pointer ${activeTab === 'ejercicios' ? 'bg-emerald-600 font-bold' : 'hover:bg-slate-800'}`} onClick={() => setActiveTab('ejercicios')}>Ejercicios</li>
              <li className={`px-6 py-3 cursor-pointer ${activeTab === 'rutinas' ? 'bg-emerald-600 font-bold' : 'hover:bg-slate-800'}`} onClick={() => setActiveTab('rutinas')}>Rutinas</li>
              <li className={`px-6 py-3 cursor-pointer ${activeTab === 'clientes' ? 'bg-emerald-600 font-bold' : 'hover:bg-slate-800'}`} onClick={() => setActiveTab('clientes')}>Gestionar Clientes</li>
              <li className={`px-6 py-3 cursor-pointer ${activeTab === 'asignar' ? 'bg-emerald-600 font-bold' : 'hover:bg-slate-800'}`} onClick={() => setActiveTab('asignar')}>Asignar Rutina</li>
            </ul>
          ) : (
            <ul className="py-2">
              <li className="px-6 py-3 bg-blue-600 font-bold">Mi Entrenamiento</li>
            </ul>
          )}
        </div>
        <button onClick={cerrarSesion} className="p-4 bg-red-600 hover:bg-red-700 text-white font-bold text-sm text-left">Cerrar Sesión</button>
      </div>

      {/* CONTENIDO DERECHO */}
      <div className="flex-1 flex flex-col overflow-y-auto">
        <header className="p-6 bg-white shadow-sm flex justify-between items-center shrink-0">
          <h2 className="text-2xl font-bold text-slate-800">
            {rol === 'instructor' ? 'Administración' : 'Portal de Cliente'}
          </h2>
          <div className="flex items-center gap-4">
            <CampanaNotificaciones usuario={datosUsuario} rol={rol} />
            <span className="text-xs bg-green-100 text-green-700 font-bold px-3 py-1 rounded-full border border-green-300">Conectado</span>
          </div>
        </header>

        <main className="p-8 flex-1 bg-slate-50 flex flex-col">
          {rol === 'instructor' && (
            <div className="flex-1">
              {activeTab === 'categorias' && <ModuloCategorias setLog={setLog} />}
              {activeTab === 'ejercicios' && <ModuloEjercicios setLog={setLog} />}
              {activeTab === 'rutinas' && <ModuloRutinas setLog={setLog} />}
              {activeTab === 'clientes' && <ModuloClientes setLog={setLog} />}
              {activeTab === 'asignar' && <ModuloAsignaciones setLog={setLog} />}
              
              {/* Si es otra pestaña diferente a las que ya hicimos, mostramos el mensaje de construcción */}
              {activeTab !== 'categorias' && activeTab !== 'ejercicios' && activeTab !== 'rutinas' && activeTab !== 'clientes' && activeTab !== 'asignar' && (
                <div className="bg-white p-12 rounded-2xl border border-slate-200 text-center shadow-sm max-w-3xl mx-auto">
                  <h3 className="text-xl font-bold text-slate-700 mb-2">Módulo: {activeTab.toUpperCase()}</h3>
                  <p className="text-slate-500">Módulo en construcción...</p>
                </div>
              )}
            </div>
          )}

          {rol === 'cliente' && (
             <PortalCliente usuario={datosUsuario} setLog={setLog} />
          )}

          <div className="mt-8 p-4 bg-slate-900 text-emerald-400 font-mono text-sm rounded-xl shadow-lg shrink-0">
            ➜ Registros del Sistema: {log}
          </div>
        </main>
      </div>
    </div>
  );
}

export default App;