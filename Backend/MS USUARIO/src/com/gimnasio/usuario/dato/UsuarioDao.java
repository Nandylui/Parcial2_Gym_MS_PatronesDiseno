package com.gimnasio.usuario.dato;

import com.gimnasio.usuario.modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDao {

	public boolean registrarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuario (ci, nombre, apellido, email, contrasena, telefono, fecha_nac, genero) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, usuario.getCi());
            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getApellido());
            ps.setString(4, usuario.getEmail());
            ps.setString(5, usuario.getContrasena()); 
            
            if (usuario.getTelefono() != null) ps.setInt(6, usuario.getTelefono());
            else ps.setNull(6, Types.INTEGER);
            
            ps.setDate(7, usuario.getFechaNac());
            ps.setString(8, usuario.getGenero());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al registrar: " + e.getMessage());
            return false;
        }
    }

    public List<Usuario> listarUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario";
        try (Connection con = ConnectionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapUsuario(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizarUsuario(Usuario usuario) {
        String sql = "UPDATE usuario SET ci=?, nombre=?, apellido=?, email=?, telefono=?, fecha_nac=?, genero=? WHERE id=?";
        try (Connection con = ConnectionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, usuario.getCi());
            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getApellido());
            ps.setString(4, usuario.getEmail());
            if (usuario.getTelefono() != null) ps.setInt(5, usuario.getTelefono());
            else ps.setNull(5, Types.INTEGER);
            ps.setDate(6, usuario.getFechaNac());
            ps.setString(7, usuario.getGenero());
            ps.setInt(8, usuario.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarUsuario(int id) {
        String sql = "DELETE FROM usuario WHERE id=?";
        try (Connection con = ConnectionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar: " + e.getMessage());
            return false;
        }
    }
    
    public Usuario buscarPorId(int id) {
        Usuario usuario = null;
        String sql = "SELECT * FROM usuario WHERE id=?";
        try (Connection con = ConnectionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = mapUsuario(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar por ID: " + e.getMessage());
        }
        return usuario;
    }

    public Usuario buscarPorEmail(String email) {
        Usuario usuario = null;
        String sql = "SELECT * FROM usuario WHERE email=?";
        try (Connection con = ConnectionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = mapUsuario(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar por Email: " + e.getMessage());
        }
        return usuario;
    }

    public List<String> obtenerRolesPorUsuario(int usuarioId) {
        List<String> roles = new ArrayList<>();
        // Hacemos un INNER JOIN para cruzar la tabla intermedia con los nombres de los roles
        String sql = "SELECT r.nombre FROM roles r INNER JOIN usuario_rol ur ON r.id = ur.Id_Rol WHERE ur.Id_Usuario = ?";
        try (Connection con = ConnectionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    roles.add(rs.getString("nombre"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener roles: " + e.getMessage());
        }
        return roles;
    }
    
    

    // Método auxiliar especificado en tu diagrama de clases
    private Usuario mapUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setCi(rs.getInt("ci"));
        u.setNombre(rs.getString("nombre"));
        u.setApellido(rs.getString("apellido"));
        u.setEmail(rs.getString("email"));
        u.setContrasena(rs.getString("contrasena"));
        u.setTelefono(rs.getInt("telefono"));
        u.setFechaNac(rs.getDate("fecha_nac"));
        u.setGenero(rs.getString("genero"));
        u.setEstado(rs.getString("estado"));
        return u;
    }
}