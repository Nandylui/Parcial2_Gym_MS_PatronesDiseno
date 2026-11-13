package com.gimnasio.rutina.dato;

import com.gimnasio.rutina.modelo.Ejercicio;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EjercicioDao {

    private ConnectionDB conexionDB;

    public EjercicioDao() {
        this.conexionDB = new ConnectionDB();
    }

    // Método mapeador del diagrama
    private Ejercicio mapEjercicio(ResultSet rs) throws SQLException {
        Ejercicio ejercicio = new Ejercicio();
        ejercicio.setId(rs.getInt("id"));
        ejercicio.setId_Categoria(rs.getInt("Id_Categoria")); 
        ejercicio.setNombre(rs.getString("nombre"));
        ejercicio.setDescripcion(rs.getString("descripcion"));
        ejercicio.setVideoURL(rs.getString("videourl"));
        return ejercicio;
    }

    public List<Ejercicio> listarEjercicios() {
        List<Ejercicio> lista = new ArrayList<>();
        String sql = "SELECT * FROM ejercicios ORDER BY id ASC";
        
        try (Connection conn = conexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                lista.add(mapEjercicio(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar ejercicios: " + e.getMessage());
        }
        return lista;
    }

    public Ejercicio buscarPorId(int id) {
        Ejercicio ejercicio = null;
        String sql = "SELECT * FROM ejercicios WHERE id = ?";
        
        try (Connection conn = conexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ejercicio = mapEjercicio(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar ejercicio: " + e.getMessage());
        }
        return ejercicio;
    }

    public boolean registrarEjercicio(Ejercicio ejercicio) {
        // Los nombres de las columnas se ajustan a SQL estándar (se asumen sin comillas especiales)
        String sql = "INSERT INTO ejercicios (id_categoria, nombre, descripcion, videourl) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = conexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, ejercicio.getId_Categoria());
            ps.setString(2, ejercicio.getNombre());
            ps.setString(3, ejercicio.getDescripcion());
            ps.setString(4, ejercicio.getVideoURL());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Error al registrar ejercicio: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarEjercicio(Ejercicio ejercicio) {
        String sql = "UPDATE ejercicios SET id_categoria = ?, nombre = ?, descripcion = ?, videourl = ? WHERE id = ?";
        
        try (Connection conn = conexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, ejercicio.getId_Categoria());
            ps.setString(2, ejercicio.getNombre());
            ps.setString(3, ejercicio.getDescripcion());
            ps.setString(4, ejercicio.getVideoURL());
            ps.setInt(5, ejercicio.getId());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Error al actualizar ejercicio: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarEjercicio(int id) {
        String sql = "DELETE FROM ejercicios WHERE id = ?";
        
        try (Connection conn = conexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Error al eliminar ejercicio: " + e.getMessage());
            return false;
        }
    }
}
