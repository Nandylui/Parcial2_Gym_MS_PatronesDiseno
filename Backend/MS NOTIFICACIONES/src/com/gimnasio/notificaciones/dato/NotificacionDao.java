package com.gimnasio.notificaciones.dato;

import com.gimnasio.notificaciones.modelo.Notificacion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class NotificacionDao {

    public Notificacion mapNotificacion(ResultSet rs) throws SQLException {
        Notificacion n = new Notificacion();
        n.setId(rs.getInt("id"));
        int usuario = rs.getInt("usuario_id");
        n.setUsuarioId(rs.wasNull() ? null : usuario);
        n.setRolDestino(rs.getString("rol_destino"));
        n.setTipo(rs.getString("tipo"));
        n.setMensaje(rs.getString("mensaje"));
        n.setLeida(rs.getBoolean("leida"));
        n.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
        return n;
    }

    public boolean registrarNotificacion(Notificacion n) {
        String sql = "INSERT INTO notificaciones (usuario_id, rol_destino, tipo, mensaje) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (n.getUsuarioId() != null) {
                ps.setInt(1, n.getUsuarioId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            if (n.getRolDestino() != null) {
                ps.setString(2, n.getRolDestino());
            } else {
                ps.setNull(2, Types.VARCHAR);
            }
            ps.setString(3, n.getTipo());
            ps.setString(4, n.getMensaje());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al registrar notificación: " + e.getMessage());
            return false;
        }
    }

    public List<Notificacion> listarPorUsuario(int usuarioId) {
        List<Notificacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM notificaciones WHERE usuario_id = ? ORDER BY fecha_creacion DESC, id DESC";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapNotificacion(rs));
                }
            }
        } catch (Exception e) {
            System.out.println("Error al listar notificaciones por usuario: " + e.getMessage());
        }
        return lista;
    }

    public List<Notificacion> listarPorRol(String rol) {
        List<Notificacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM notificaciones WHERE rol_destino = ? ORDER BY fecha_creacion DESC, id DESC";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, rol);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapNotificacion(rs));
                }
            }
        } catch (Exception e) {
            System.out.println("Error al listar notificaciones por rol: " + e.getMessage());
        }
        return lista;
    }

    public boolean marcarLeida(int id) {
        String sql = "UPDATE notificaciones SET leida = TRUE WHERE id = ?";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al marcar notificación como leída: " + e.getMessage());
            return false;
        }
    }
}
