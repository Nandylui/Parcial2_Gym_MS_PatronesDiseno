package com.gimnasio.entrenamiento.dato;

import com.gimnasio.entrenamiento.modelo.SesionEntrenamiento;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SesionEntrenamientoDao {

    // 1. Mapeador especificado en el diagrama
    public SesionEntrenamiento mapSesionEntrenamiento(ResultSet rs) throws SQLException {
        SesionEntrenamiento sesion = new SesionEntrenamiento();
        sesion.setId(rs.getInt("id"));
        sesion.setId_Asignacion(rs.getInt("Id_Asignacion"));
        sesion.setFecha(rs.getDate("fecha"));
        sesion.setEstado(rs.getString("estado"));
        return sesion;
    }

    // 2. listarPorAsignacion(Id_Asignacion)
    public List<SesionEntrenamiento> listarPorAsignacion(int idAsignacion) {
        List<SesionEntrenamiento> lista = new ArrayList<>();
        String sql = "SELECT * FROM sesion_entrenamiento WHERE Id_Asignacion = ? ORDER BY id ASC";
        
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idAsignacion);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapSesionEntrenamiento(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar sesiones por asignación: " + e.getMessage());
        }
        return lista;
    }

    // buscarPorId(id): usado para publicar el evento SESION_COMPLETADA
    public SesionEntrenamiento buscarPorId(int id) {
        String sql = "SELECT * FROM sesion_entrenamiento WHERE id = ?";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapSesionEntrenamiento(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar la sesión: " + e.getMessage());
        }
        return null;
    }

    // 3. registrarSesionEntrenamiento(SesionEntrenamiento)
    public boolean registrarSesionEntrenamiento(SesionEntrenamiento sesion) {
        String sql = "INSERT INTO sesion_entrenamiento (Id_Asignacion, fecha, estado) VALUES (?, ?, ?)";
        
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, sesion.getId_Asignacion());
            ps.setDate(2, sesion.getFecha());
            ps.setString(3, sesion.getEstado() != null ? sesion.getEstado() : "PENDIENTE");
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Error al registrar sesión de entrenamiento: " + e.getMessage());
            return false;
        }
    }

    // 4. actualizarEstado(id)
    public boolean actualizarEstado(int id) {
        // Cambia el estado de PENDIENTE a COMPLETADO (o el flujo que manejes)
        String sql = "UPDATE sesion_entrenamiento SET estado = 'COMPLETADO' WHERE id = ?";
        
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Error al actualizar estado de la sesión: " + e.getMessage());
            return false;
        }
    }
}
