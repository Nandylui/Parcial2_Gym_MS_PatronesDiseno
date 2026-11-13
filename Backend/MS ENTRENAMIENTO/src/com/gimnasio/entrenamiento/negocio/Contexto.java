package com.gimnasio.entrenamiento.negocio;

import java.sql.Date;
import java.time.temporal.ChronoUnit;

/**
 * [PATRON ESTRATEGIA] Contexto: elige la estrategia según el nivel de la rutina
 * y delega en ella los cálculos. No conoce los detalles de cada nivel.
 */
public class Contexto {

    private EstrategiaAjusteRutina estrategia;

    public Contexto() {
        // Estrategia por defecto si no se puede determinar el nivel
        this.estrategia = new AjusteIntermedio();
    }

    /** Consulta el nivel de la rutina en MS Rutina y selecciona la estrategia. */
    public EstrategiaAjusteRutina obtenerEstrategia(int idRutina) {
        String nivel = MsRutina.obtenerNivel(idRutina);   // null si MS Rutina no responde
        return porNivel(nivel);
    }

    /** Selecciona la estrategia a partir del nombre del nivel (acepta Principiante/PRINCIPIANTE, etc.). */
    public EstrategiaAjusteRutina porNivel(String nivel) {
        String n = (nivel == null) ? "" : nivel.trim().toLowerCase();
        if (n.startsWith("principiante")) {
            this.estrategia = new AjustePrincipiante();
        } else if (n.startsWith("avanzado")) {
            this.estrategia = new AjusteAvanzado();
        } else {
            // "intermedio" o nivel desconocido/vacío -> Intermedio
            this.estrategia = new AjusteIntermedio();
        }
        return this.estrategia;
    }

    public int ejecutarAjusteSeries(int seriesBase) {
        return estrategia.ajustarSeries(seriesBase);
    }

    public int ejecutarAjusteDescanso(int descansoBaseSeg) {
        return estrategia.ajustarDescanso(descansoBaseSeg);
    }

    /** Sesiones esperadas entre dos fechas (inclusive) según las sesiones por semana de la estrategia. */
    public int calcularSesionesEsperadas(Date fechaInicio, Date fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            return 0;
        }
        long dias = ChronoUnit.DAYS.between(fechaInicio.toLocalDate(), fechaFin.toLocalDate()) + 1;
        if (dias <= 0) {
            return 0;
        }
        return (int) Math.round(estrategia.getSesionesPorSemana() * dias / 7.0);
    }

    public EstrategiaAjusteRutina getEstrategia() {
        return estrategia;
    }
}
