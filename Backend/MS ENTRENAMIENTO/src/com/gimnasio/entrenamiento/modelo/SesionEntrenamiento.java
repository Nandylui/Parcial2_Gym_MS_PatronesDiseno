package com.gimnasio.entrenamiento.modelo;

import java.sql.Date;

public class SesionEntrenamiento {
    private int id;
    private int Id_Asignacion;
    private Date fecha;
    private String estado;

    public SesionEntrenamiento() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getId_Asignacion() { return Id_Asignacion; }
    public void setId_Asignacion(int Id_Asignacion) { this.Id_Asignacion = Id_Asignacion; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
