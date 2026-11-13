package com.gimnasio.rutina.dato;

import com.gimnasio.rutina.modelo.Categoria;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDao {

    private ConnectionDB conexionDB;

    public CategoriaDao() {
        this.conexionDB = new ConnectionDB();
    }

    // Método mapeador del diagrama
    private Categoria mapCategoria(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria();
        categoria.setId(rs.getInt("id"));
        categoria.setNombre(rs.getString("nombre"));
        return categoria;
    }

    public List<Categoria> listarCategorias() {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM categorias ORDER BY id ASC";
        
        try (Connection conn = conexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                lista.add(mapCategoria(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar categorías: " + e.getMessage());
        }
        return lista;
    }

    public Categoria buscarPorId(int id) {
        Categoria categoria = null;
        String sql = "SELECT * FROM categorias WHERE id = ?";
        
        try (Connection conn = conexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    categoria = mapCategoria(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar categoría: " + e.getMessage());
        }
        return categoria;
    }

    public boolean registrarCategoria(Categoria categoria) {
        String sql = "INSERT INTO categorias (nombre) VALUES (?)";
        
        try (Connection conn = conexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, categoria.getNombre());
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Error al registrar categoría: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarCategoria(Categoria categoria) {
        String sql = "UPDATE categorias SET nombre = ? WHERE id = ?";
        
        try (Connection conn = conexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, categoria.getNombre());
            ps.setInt(2, categoria.getId());
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Error al actualizar categoría: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarCategoria(int id) {
        String sql = "DELETE FROM categorias WHERE id = ?";
        
        try (Connection conn = conexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Error al eliminar categoría: " + e.getMessage());
            return false;
        }
    }
}