package com.gimnasio.rutina.negocio;

import com.gimnasio.rutina.dato.EjercicioDao;
import com.gimnasio.rutina.modelo.Ejercicio;
import java.util.List;

public class EjercicioN {

    // Atributo definido en el diagrama
    private EjercicioDao dao;

    public EjercicioN() {
        this.dao = new EjercicioDao();
    }

    public List<Ejercicio> listarEjercicios() {
        return dao.listarEjercicios();
    }

    public Ejercicio buscarPorId(int id) {
        if (id <= 0) {
            return null; // Validación básica
        }
        return dao.buscarPorId(id);
    }

    public boolean registrarEjercicio(Ejercicio ejercicio) {
        // Validaciones de negocio exigidas
        if (ejercicio.getNombre() == null || ejercicio.getNombre().trim().isEmpty()) {
            System.out.println("Validación fallida: El nombre del ejercicio no puede estar vacío.");
            return false;
        }
        if (ejercicio.getId_Categoria() <= 0) {
            System.out.println("Validación fallida: Debe pertenecer a una categoría válida.");
            return false;
        }
        return dao.registrarEjercicio(ejercicio);
    }

    public boolean actualizarEjercicio(Ejercicio ejercicio) {
        if (ejercicio.getId() <= 0 || ejercicio.getNombre() == null || ejercicio.getNombre().trim().isEmpty() || ejercicio.getId_Categoria() <= 0) {
            return false;
        }
        return dao.actualizarEjercicio(ejercicio);
    }

    public boolean eliminarEjercicio(int id) {
        if (id <= 0) {
            return false;
        }
        return dao.eliminarEjercicio(id);
    }
}
