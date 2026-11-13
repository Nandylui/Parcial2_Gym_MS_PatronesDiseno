package com.gimnasio.entrenamiento.modelo;

import java.sql.Date;

public class Asignacion {
    private int id;
    private int Id_Usuario;
    private int Id_Rutina;
    private Date fechaAsignacion;
    private Date fechaInicio;
    private Date fechaFin;
    private String estado;

    public Asignacion() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getId_Usuario() { return Id_Usuario; }
    public void setId_Usuario(int Id_Usuario) { this.Id_Usuario = Id_Usuario; }

    public int getId_Rutina() { return Id_Rutina; }
    public void setId_Rutina(int Id_Rutina) { this.Id_Rutina = Id_Rutina; }

    public Date getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(Date fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }

    public Date getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(Date fechaInicio) { this.fechaInicio = fechaInicio; }

    public Date getFechaFin() { return fechaFin; }
    public void setFechaFin(Date fechaFin) { this.fechaFin = fechaFin; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
