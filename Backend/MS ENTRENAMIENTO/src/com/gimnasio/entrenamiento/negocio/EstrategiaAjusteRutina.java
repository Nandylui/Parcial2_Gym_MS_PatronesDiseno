package com.gimnasio.entrenamiento.negocio;

/**
 * [PATRON ESTRATEGIA] Contrato común de los algoritmos de ajuste de una rutina según el nivel.
 */
public interface EstrategiaAjusteRutina {

    /** Series ajustadas a partir de las series base de la rutina (nunca menor a 1). */
    int ajustarSeries(int seriesBase);

    /** Descanso ajustado en segundos a partir del descanso base (nunca menor a 15 s). */
    int ajustarDescanso(int descansoBaseSeg);

    int getSesionesPorSemana();

    double getFactorSeries();

    String getNombreEstrategia();
}
