package com.gimnasio.entrenamiento.negocio;

import com.gimnasio.entrenamiento.dato.AsignacionDao;
import com.gimnasio.entrenamiento.dato.SesionEntrenamientoDao;
import com.gimnasio.entrenamiento.modelo.Asignacion;
import com.gimnasio.entrenamiento.modelo.RutinaRemota;
import com.gimnasio.entrenamiento.modelo.SesionEntrenamiento;
import java.util.List;

public class SesionEntrenamientoN {

    // Atributo definido en el diagrama
    private SesionEntrenamientoDao dao;
    private AsignacionDao asignacionDao;
    // [PATRON OBSERVER] Subject que publica los eventos de sesión
    private EntrenamientoObservable observable;

    public SesionEntrenamientoN() {
        this.dao = new SesionEntrenamientoDao();
        this.asignacionDao = new AsignacionDao();
        this.observable = new EntrenamientoObservableImpl();
        this.observable.agregarObservador(new NotificadorClienteObserver());
        this.observable.agregarObservador(new NotificadorInstructorObserver());
    }

    // 1. listarPorAsignacion
    public List<SesionEntrenamiento> listarPorAsignacion(int idAsignacion) {
        if (idAsignacion <= 0) {
            return null;
        }
        return dao.listarPorAsignacion(idAsignacion);
    }

    // 2. registrarSesionEntrenamiento
    public boolean registrarSesionEntrenamiento(SesionEntrenamiento sesion) {
        if (sesion.getId_Asignacion() <= 0 || sesion.getFecha() == null) {
            System.out.println("Validación fallida: ID de asignación o fecha inválidos.");
            return false;
        }
        boolean registrada = dao.registrarSesionEntrenamiento(sesion);

        // [PATRON OBSERVER] Si la sesión se registra ya completada, se publica el evento.
        if (registrada && "COMPLETADO".equalsIgnoreCase(sesion.getEstado())) {
            dispararObserver(sesion.getId_Asignacion());
        }
        return registrada;
    }

    // 3. actualizarEstado
    public boolean actualizarEstado(int id) {
        if (id <= 0) {
            return false;
        }

        SesionEntrenamiento antes = dao.buscarPorId(id);
        boolean yaCompletada = antes != null && "COMPLETADO".equalsIgnoreCase(antes.getEstado());

        boolean actualizada = dao.actualizarEstado(id);

        // [PATRON OBSERVER] Publica SESION_COMPLETADA (una sola vez por sesión)
        if (actualizada && antes != null && !yaCompletada) {
            dispararObserver(antes.getId_Asignacion());
        }
        return actualizada;
    }

    /**
     * [PATRON OBSERVER] Arma y publica el evento SESION_COMPLETADA para la asignación indicada.
     * Si MS Notificaciones no responde, la sesión igual queda registrada.
     */
    private void dispararObserver(int idAsignacion) {
        try {
            Asignacion asignacion = asignacionDao.buscarPorId(idAsignacion);
            if (asignacion == null) {
                return;
            }
            RutinaRemota rutina = MsRutina.obtenerRutina(asignacion.getId_Rutina());
            String nombreRutina = (rutina != null && rutina.getNombre() != null)
                    ? rutina.getNombre() : "Rutina #" + asignacion.getId_Rutina();

            EventoEntrenamiento evento = new EventoEntrenamiento(
                    TipoEvento.SESION_COMPLETADA,
                    idAsignacion,
                    asignacion.getId_Usuario(),
                    nombreRutina,
                    "Asignación #" + idAsignacion + ".");
            observable.notificarObservadores(evento);
        } catch (Exception e) {
            System.out.println("Aviso: no se pudo publicar el evento de sesión: " + e.getMessage());
        }
    }
}
