package com.gimnasio.entrenamiento.negocio;

/** [PATRON OBSERVER] Observer: recibe los eventos que publica el Subject. */
public interface EntrenamientoObserver {
    void actualizar(EventoEntrenamiento evento);
}
