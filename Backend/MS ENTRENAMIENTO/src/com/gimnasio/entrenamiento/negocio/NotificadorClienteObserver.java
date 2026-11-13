package com.gimnasio.entrenamiento.negocio;

/** [PATRON OBSERVER] Observer concreto A: avisa al cliente dueño de la asignación o sesión. */
public class NotificadorClienteObserver implements EntrenamientoObserver {

    @Override
    public void actualizar(EventoEntrenamiento evento) {
        String mensaje;
        if (evento.getTipo() == TipoEvento.ASIGNACION_REGISTRADA) {
            mensaje = "Se te asignó la rutina \"" + evento.getNombreRutina() + "\". " + evento.getDetalle();
        } else if (evento.getTipo() == TipoEvento.SESION_COMPLETADA) {
            mensaje = "Registraste tu sesión de entrenamiento de la rutina \"" + evento.getNombreRutina() + "\". " + evento.getDetalle();
        } else {
            return;
        }
        MsNotificaciones.enviar(evento.getIdUsuario(), null, evento.getTipo().name(), mensaje.trim());
    }
}
