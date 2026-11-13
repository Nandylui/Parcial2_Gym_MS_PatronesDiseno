package com.gimnasio.entrenamiento.dato;

import com.gimnasio.entrenamiento.modelo.Asignacion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AsignacionDao {

    // Método mapeador basado en el diagrama
    public Asignacion mapAsignacion(ResultSet rs) throws SQLException {
        Asignacion asignacion = new Asignacion();
        asignacion.setId(rs.getInt("id"));
        asignacion.setId_Usuario(rs.getInt("Id_Usuario"));
        asignacion.setId_Rutina(rs.getInt("Id_Rutina"));
        asignacion.setFechaAsignacion(rs.getDate("fechaAsignacion"));
        asignacion.setFechaInicio(rs.getDate("fechaInicio"));
        asignacion.setFechaFin(rs.getDate("fechaFin"));
        asignacion.setEstado(rs.getString("estado"));
        return asignacion;
    }

    public List<Asignacion> listarAsignacion() {
        List<Asignacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM asignaciones ORDER BY id ASC";
        
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                lista.add(mapAsignacion(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar asignaciones: " + e.getMessage());
        }
        return lista;
    }

    public Asignacion buscarPorId(int id) {
        String sql = "SELECT * FROM asignaciones WHERE id = ?";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAsignacion(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar asignación: " + e.getMessage());
        }
        return null;
    }

    public Asignacion registrarAsignacion(Asignacion asignacion) {
        String sql = "INSERT INTO asignaciones (Id_Usuario, Id_Rutina, fechaInicio, fechaFin, estado) VALUES (?, ?, ?, ?, ?) RETURNING id, fechaAsignacion";
        
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, asignacion.getId_Usuario());
            ps.setInt(2, asignacion.getId_Rutina());
            ps.setDate(3, asignacion.getFechaInicio());
            ps.setDate(4, asignacion.getFechaFin());
            ps.setString(5, asignacion.getEstado() != null ? asignacion.getEstado() : "ACTIVA");
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    asignacion.setId(rs.getInt("id"));
                    asignacion.setFechaAsignacion(rs.getDate("fechaAsignacion"));
                    return asignacion; // Retorna el objeto tal como lo pide tu diagrama
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al registrar asignación: " + e.getMessage());
        }
        return null;
    }
}
