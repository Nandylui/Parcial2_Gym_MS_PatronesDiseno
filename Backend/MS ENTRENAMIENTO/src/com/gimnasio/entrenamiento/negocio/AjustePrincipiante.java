package com.gimnasio.entrenamiento.negocio;

/**
 * [PATRON ESTRATEGIA] Estrategia concreta: nivel PRINCIPIANTE.
 * Series x0.75, descanso 30 s, 3 sesiones por semana.
 */
public class AjustePrincipiante implements EstrategiaAjusteRutina {

    private static final double FACTOR_SERIE = 0.75;
    private static final int AJUSTE_DESCANSO = 30;
    private static final int SESIONES_SEMANA = 3;

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
        return "PRINCIPIANTE";
    }
}
