package com.gimnasio.entrenamiento.negocio;

/** [PATRON OBSERVER] Datos del evento que se envía a los observadores. */
public class EventoEntrenamiento {
    private TipoEvento tipo;
    private int idAsignacion;
    private int idUsuario;
    private String nombreRutina;
    private String detalle;

    public EventoEntrenamiento(TipoEvento tipo, int idAsignacion, int idUsuario, String nombreRutina, String detalle) {
        this.tipo = tipo;
        this.idAsignacion = idAsignacion;
        this.idUsuario = idUsuario;
        this.nombreRutina = nombreRutina;
        this.detalle = detalle;
    }

    public TipoEvento getTipo() { return tipo; }
    public int getIdAsignacion() { return idAsignacion; }
    public int getIdUsuario() { return idUsuario; }
    public String getNombreRutina() { return nombreRutina; }
    public String getDetalle() { return detalle; }
}
