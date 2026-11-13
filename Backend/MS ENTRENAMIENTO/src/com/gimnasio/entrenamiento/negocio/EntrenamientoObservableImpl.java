package com.gimnasio.entrenamiento.negocio;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** [PATRON OBSERVER] Subject concreto: mantiene la lista de observadores y los notifica. */
public class EntrenamientoObservableImpl implements EntrenamientoObservable {

    private final List<EntrenamientoObserver> observadores = new CopyOnWriteArrayList<>();

    @Override
    public void agregarObservador(EntrenamientoObserver observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    @Override
    public void removerObservador(EntrenamientoObserver observador) {
        observadores.remove(observador);
    }

    @Override
    public void notificarObservadores(EventoEntrenamiento evento) {
        for (EntrenamientoObserver observador : observadores) {
            try {
                observador.actualizar(evento);
            } catch (Exception e) {
                // Un observador que falla no debe impedir que los demás reciban el evento
                System.out.println("Aviso: falló un observador (" + observador.getClass().getSimpleName() + "): " + e.getMessage());
            }
        }
    }
}
