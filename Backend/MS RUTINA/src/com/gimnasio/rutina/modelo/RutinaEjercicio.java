package com.gimnasio.rutina.modelo;

public class RutinaEjercicio {
    private int id;
    private int Id_Rutina;
    private int Id_Ejercicio;
    private int series;
    private int repeticiones;
    private int descansoSeg;
    private int orden;

    public RutinaEjercicio() {}

    public RutinaEjercicio(int id, int Id_Rutina, int Id_Ejercicio, int series, int repeticiones, int descansoSeg, int orden) {
        this.id = id;
        this.Id_Rutina = Id_Rutina;
        this.Id_Ejercicio = Id_Ejercicio;
        this.series = series;
        this.repeticiones = repeticiones;
        this.descansoSeg = descansoSeg;
        this.orden = orden;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getId_Rutina() { return Id_Rutina; }
    public void setId_Rutina(int Id_Rutina) { this.Id_Rutina = Id_Rutina; }

    public int getId_Ejercicio() { return Id_Ejercicio; }
    public void setId_Ejercicio(int Id_Ejercicio) { this.Id_Ejercicio = Id_Ejercicio; }

    public int getSeries() { return series; }
    public void setSeries(int series) { this.series = series; }

    public int getRepeticiones() { return repeticiones; }
    public void setRepeticiones(int repeticiones) { this.repeticiones = repeticiones; }

    public int getDescansoSeg() { return descansoSeg; }
    public void setDescansoSeg(int descansoSeg) { this.descansoSeg = descansoSeg; }

    public int getOrden() { return orden; }
    public void setOrden(int orden) { this.orden = orden; }
}