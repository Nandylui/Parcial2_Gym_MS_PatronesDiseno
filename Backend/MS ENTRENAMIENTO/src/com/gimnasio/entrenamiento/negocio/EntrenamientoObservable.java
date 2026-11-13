package com.gimnasio.entrenamiento.negocio;

/** [PATRON OBSERVER] Subject: administra a los observadores y los notifica. */
public interface EntrenamientoObservable {
    void agregarObservador(EntrenamientoObserver observador);

    void removerObservador(EntrenamientoObserver observador);

    void notificarObservadores(EventoEntrenamiento evento);
}
