package com.gimnasio.entrenamiento.negocio;

import com.gimnasio.entrenamiento.dato.AsignacionDao;
import com.gimnasio.entrenamiento.dato.PlanAsignacionDao;
import com.gimnasio.entrenamiento.modelo.Asignacion;
import com.gimnasio.entrenamiento.modelo.PlanAsignacion;
import com.gimnasio.entrenamiento.modelo.RutinaRemota;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AsignacionN {

    // Atributo definido en el diagrama
    private AsignacionDao dao;
    private PlanAsignacionDao planDao;
    // [PATRON OBSERVER] Subject que publica los eventos de asignación
    private EntrenamientoObservable observable;

    public AsignacionN() {
        this.dao = new AsignacionDao();
        this.planDao = new PlanAsignacionDao();
        this.observable = new EntrenamientoObservableImpl();
        this.observable.agregarObservador(new NotificadorClienteObserver());
    }

    public List<Asignacion> listarAsignaciones() {
        return dao.listarAsignacion();
    }

    public Asignacion registrarAsignacion(Asignacion asignacion) {
        // Validaciones básicas de datos
        if (asignacion.getId_Usuario() <= 0 || asignacion.getId_Rutina() <= 0) {
            System.out.println("Validación fallida: ID de Usuario o Rutina inválidos.");
            return null;
        }
        
        // 1. Validar usando la clase externa MS Rutina
        if (!MsRutina.validarRutinaExistente(asignacion.getId_Rutina())) {
            System.out.println("Validación fallida: La rutina con ID " + asignacion.getId_Rutina() + " no existe.");
            return null;
        }

        // 2. Validar usando la clase externa MS Usuario
        if (!MsUsuario.validarUsuarioExistente(asignacion.getId_Usuario())) {
            System.out.println("Validación fallida: El usuario con ID " + asignacion.getId_Usuario() + " no existe.");
            return null;
        }

        if (asignacion.getFechaInicio() == null || asignacion.getFechaFin() == null) {
            System.out.println("Validación fallida: Las fechas son obligatorias.");
            return null;
        }

        Asignacion creada = dao.registrarAsignacion(asignacion);

        if (creada != null) {
            // [PATRON ESTRATEGIA] Calcula y guarda el plan ajustado de la asignación.
            // Si falla, la asignación igual queda registrada.
            try {
                calcularPlanConEstrategia(creada);
            } catch (Exception e) {
                System.out.println("Aviso: no se pudo calcular el plan de la asignación: " + e.getMessage());
            }

            // [PATRON OBSERVER] Publica el evento ASIGNACION_REGISTRADA.
            // Si MS Notificaciones no responde, la asignación igual queda registrada.
            try {
                dispararObserver(creada);
            } catch (Exception e) {
                System.out.println("Aviso: no se pudo publicar el evento de asignación: " + e.getMessage());
            }
        }
        return creada;
    }

    /** [PATRON OBSERVER] Arma y publica el evento ASIGNACION_REGISTRADA. */
    private void dispararObserver(Asignacion asignacion) {
        RutinaRemota rutina = MsRutina.obtenerRutina(asignacion.getId_Rutina());
        String nombreRutina = (rutina != null && rutina.getNombre() != null)
                ? rutina.getNombre() : "Rutina #" + asignacion.getId_Rutina();
        String detalle = "Periodo: del " + asignacion.getFechaInicio() + " al " + asignacion.getFechaFin() + ".";

        EventoEntrenamiento evento = new EventoEntrenamiento(
                TipoEvento.ASIGNACION_REGISTRADA,
                asignacion.getId(),
                asignacion.getId_Usuario(),
                nombreRutina,
                detalle);
        observable.notificarObservadores(evento);
    }

    /**
     * [PATRON ESTRATEGIA] Elige la estrategia según el nivel de la rutina, calcula el plan
     * (factor de series, sesiones por semana y sesiones esperadas) y lo guarda.
     */
    private PlanAsignacion calcularPlanConEstrategia(Asignacion asignacion) {
        Contexto contexto = new Contexto();
        EstrategiaAjusteRutina estrategia = contexto.obtenerEstrategia(asignacion.getId_Rutina());

        PlanAsignacion plan = new PlanAsignacion();
        plan.setId_Asignacion(asignacion.getId());
        plan.setEstrategia(estrategia.getNombreEstrategia());
        plan.setFactorSeries(estrategia.getFactorSeries());
        plan.setSesionesPorSemana(estrategia.getSesionesPorSemana());
        plan.setSesionesEsperadas(contexto.calcularSesionesEsperadas(asignacion.getFechaInicio(), asignacion.getFechaFin()));

        if (!planDao.registrarPlan(plan)) {
            return null;
        }
        return planDao.buscarPorAsignacion(asignacion.getId());
    }

    /**
     * CU6 - Consulta el plan ajustado de una asignación: datos de la asignación, la estrategia aplicada
     * y los ejercicios de la rutina con series y descanso ya ajustados. Devuelve null si la asignación no existe.
     */
    public Map<String, Object> obtenerPlan(int idAsignacion) {
        if (idAsignacion <= 0) {
            return null;
        }
        Asignacion asignacion = dao.buscarPorId(idAsignacion);
        if (asignacion == null) {
            return null;
        }

        // Si la asignación es anterior a los patrones y no tiene plan guardado, se calcula ahora.
        PlanAsignacion plan = planDao.buscarPorAsignacion(idAsignacion);
        if (plan == null) {
            plan = calcularPlanConEstrategia(asignacion);
        }

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("asignacion", asignacion);
        resultado.put("plan", plan);

        Contexto contexto = new Contexto();
        if (plan != null) {
            contexto.porNivel(plan.getEstrategia());
        } else {
            contexto.obtenerEstrategia(asignacion.getId_Rutina());
        }

        RutinaRemota rutina = MsRutina.obtenerRutina(asignacion.getId_Rutina());
        List<Map<String, Object>> ejercicios = new ArrayList<>();
        if (rutina != null) {
            Map<String, Object> datosRutina = new LinkedHashMap<>();
            datosRutina.put("id", rutina.getId());
            datosRutina.put("nombre", rutina.getNombre());
            datosRutina.put("nivel", rutina.getNivel());
            resultado.put("rutina", datosRutina);

            for (RutinaRemota.EjercicioRemoto e : rutina.getEjercicios()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("Id_Ejercicio", e.getId_Ejercicio());
                item.put("orden", e.getOrden());
                item.put("repeticiones", e.getRepeticiones());
                item.put("seriesBase", e.getSeries());
                item.put("seriesAjustadas", contexto.ejecutarAjusteSeries(e.getSeries()));
                item.put("descansoBaseSeg", e.getDescansoSeg());
                item.put("descansoAjustadoSeg", contexto.ejecutarAjusteDescanso(e.getDescansoSeg()));
                ejercicios.add(item);
            }
        }
        resultado.put("ejercicios", ejercicios);
        return resultado;
    }
}