package com.gimnasio.entrenamiento.negocio;

/**
 * [PATRON ESTRATEGIA] Estrategia concreta: nivel INTERMEDIO.
 * Series x1.00, descanso 0 s, 4 sesiones por semana.
 */
public class AjusteIntermedio implements EstrategiaAjusteRutina {

    private static final double FACTOR_SERIE = 1.00;
    private static final int AJUSTE_DESCANSO = 0;
    private static final int SESIONES_SEMANA = 4;

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
        return "INTERMEDIO";
    }
}
