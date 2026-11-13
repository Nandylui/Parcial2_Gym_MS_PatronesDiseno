package com.gimnasio.entrenamiento.negocio;

/**
 * [PATRON ESTRATEGIA] Estrategia concreta: nivel AVANZADO.
 * Series x1.25, descanso -15 s, 5 sesiones por semana.
 */
public class AjusteAvanzado implements EstrategiaAjusteRutina {

    private static final double FACTOR_SERIE = 1.25;
    private static final int AJUSTE_DESCANSO = -15;
    private static final int SESIONES_SEMANA = 5;

    @Override
    public int ajustarSeries(int seriesBase) {
        int ajustadas = (int) Math.round(seriesBase * FACTOR_SERIE);
        return Math.max(1, ajustadas);
    }

    @Override
    public int ajustarDescanso(int descansoBaseSeg) {
        return Math.max(15, descansoBaseSeg + AJUSTE_DESCANSO);
    }

    @Override
    public int getSesionesPorSemana() {
        return SESIONES_SEMANA;
    }

    @Override
    public double getFactorSeries() {
        return FACTOR_SERIE;
    }

    @Override
    public String getNombreEstrategia() {
        return "AVANZADO";
    }
}
