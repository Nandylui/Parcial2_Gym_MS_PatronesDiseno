package com.gimnasio.entrenamiento.negocio;

/** [PATRON OBSERVER] Observer concreto B: avisa al rol INSTRUCTOR cuando un cliente completa una sesión. */
public class NotificadorInstructorObserver implements EntrenamientoObserver {

    @Override
    public void actualizar(EventoEntrenamiento evento) {
        if (evento.getTipo() != TipoEvento.SESION_COMPLETADA) {
            return;
        }
        String mensaje = "El cliente (id " + evento.getIdUsuario() + ") completó una sesión de la rutina \""
                + evento.getNombreRutina() + "\". " + evento.getDetalle();
        MsNotificaciones.enviar(null, "INSTRUCTOR", evento.getTipo().name(), mensaje.trim());
    }
}
