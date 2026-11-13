package com.gimnasio.rutina.negocio;

import com.gimnasio.rutina.dato.CategoriaDao;
import com.gimnasio.rutina.modelo.Categoria;
import java.util.List;

public class CategoriaN {

    // Atributo definido en el diagrama
    private CategoriaDao dao;

    public CategoriaN() {
        this.dao = new CategoriaDao();
    }

    public List<Categoria> listarCategorias() {
        return dao.listarCategorias();
    }

    public Categoria buscarPorId(int id) {
        if (id <= 0) {
            return null; // Validación básica de negocio
        }
        return dao.buscarPorId(id);
    }

    public boolean registrarCategoria(Categoria categoria) {
        // Validaciones de negocio antes de enviar a la base de datos
        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            System.out.println("Validación fallida: El nombre no puede estar vacío.");
            return false;
        }
        return dao.registrarCategoria(categoria);
    }

    public boolean actualizarCategoria(Categoria categoria) {
        if (categoria.getId() <= 0 || categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            return false;
        }
        return dao.actualizarCategoria(categoria);
    }

    public boolean eliminarCategoria(int id) {
        if (id <= 0) {
            return false;
        }
        return dao.eliminarCategoria(id);
    }
}
