package com.gimnasio.notificaciones.modelo;

import java.sql.Timestamp;

public class Notificacion {
    private int id;
    private Integer usuarioId;     // destinatario puntual (puede ser null si va por rol)
    private String rolDestino;     // destinatarios por rol, ej: INSTRUCTOR (puede ser null si va por usuario)
    private String tipo;           // ASIGNACION_REGISTRADA, SESION_COMPLETADA
    private String mensaje;
    private boolean leida;
    private Timestamp fechaCreacion;

    public Notificacion() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public String getRolDestino() { return rolDestino; }
    public void setRolDestino(String rolDestino) { this.rolDestino = rolDestino; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public boolean isLeida() { return leida; }
    public void setLeida(boolean leida) { this.leida = leida; }

    public Timestamp getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
