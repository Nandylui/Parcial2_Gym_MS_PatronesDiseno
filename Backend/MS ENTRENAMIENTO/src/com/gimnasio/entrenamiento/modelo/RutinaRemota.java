package com.gimnasio.entrenamiento.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Vista de solo lectura de una rutina de MS Rutina (se llena con el JSON de GET /rutinas/{id}).
 */
public class RutinaRemota {
    private int id;
    private String nombre;
    private String descripcion;
    private String nivel;
    private List<EjercicioRemoto> ejercicios = new ArrayList<>();

    public RutinaRemota() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public List<EjercicioRemoto> getEjercicios() { return ejercicios; }
    public void setEjercicios(List<EjercicioRemoto> ejercicios) { this.ejercicios = ejercicios; }

    /** Ejercicio de la rutina (campos de rutina_ejercicio). */
    public static class EjercicioRemoto {
        private int Id_Ejercicio;
        private int series;
        private int repeticiones;
        private int descansoSeg;
        private int orden;

        public int getId_Ejercicio() { return Id_Ejercicio; }
        public int getSeries() { return series; }
        public int getRepeticiones() { return repeticiones; }
        public int getDescansoSeg() { return descansoSeg; }
        public int getOrden() { return orden; }
    }
}
