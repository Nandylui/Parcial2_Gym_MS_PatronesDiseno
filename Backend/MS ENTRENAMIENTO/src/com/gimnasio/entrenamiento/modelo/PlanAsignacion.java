package com.gimnasio.entrenamiento.modelo;

import java.sql.Timestamp;

public class PlanAsignacion {
    private int id;
    private int Id_Asignacion;
    private String estrategia;        // PRINCIPIANTE, INTERMEDIO, AVANZADO
    private double factorSeries;
    private int sesionesPorSemana;
    private int sesionesEsperadas;
    private Timestamp fechaCalculo;

    public PlanAsignacion() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getId_Asignacion() { return Id_Asignacion; }
    public void setId_Asignacion(int Id_Asignacion) { this.Id_Asignacion = Id_Asignacion; }

    public String getEstrategia() { return estrategia; }
    public void setEstrategia(String estrategia) { this.estrategia = estrategia; }

    public double getFactorSeries() { return factorSeries; }
    public void setFactorSeries(double factorSeries) { this.factorSeries = factorSeries; }

    public int getSesionesPorSemana() { return sesionesPorSemana; }
    public void setSesionesPorSemana(int sesionesPorSemana) { this.sesionesPorSemana = sesionesPorSemana; }

    public int getSesionesEsperadas() { return sesionesEsperadas; }
    public void setSesionesEsperadas(int sesionesEsperadas) { this.sesionesEsperadas = sesionesEsperadas; }

    public Timestamp getFechaCalculo() { return fechaCalculo; }
    public void setFechaCalculo(Timestamp fechaCalculo) { this.fechaCalculo = fechaCalculo; }
}
