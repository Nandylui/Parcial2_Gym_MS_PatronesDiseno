package com.gimnasio.rutina.negocio;

import com.gimnasio.rutina.dato.RutinaDao;
import com.gimnasio.rutina.modelo.Rutina;
import java.util.List;

public class RutinaN {

    // Atributo exigido por el diagrama
    private RutinaDao dao;

    public RutinaN() {
        this.dao = new RutinaDao();
    }

    public List<Rutina> listarRutinas() {
        return dao.listarRutinas();
    }

    public Rutina buscarPorId(int id) {
        if (id <= 0) {
            return null; // Validación básica
        }
        return dao.buscarPorId(id);
    }

    public boolean registrarRutina(Rutina rutina) {
        // Validaciones de negocio: el nombre es obligatorio
        if (rutina.getNombre() == null || rutina.getNombre().trim().isEmpty()) {
            System.out.println("Validación fallida: El nombre de la rutina no puede estar vacío.");
            return false;
        }
        return dao.registrarRutina(rutina);
    }

    public boolean actualizarRutina(Rutina rutina) {
        if (rutina.getId() <= 0 || rutina.getNombre() == null || rutina.getNombre().trim().isEmpty()) {
            System.out.println("Validación fallida: Datos incompletos para actualizar la rutina.");
            return false;
        }
        return dao.actualizarRutina(rutina);
    }

    public boolean eliminarRutina(int id) {
        if (id <= 0) {
            return false;
        }
        return dao.eliminarRutina(id);
    }
}
