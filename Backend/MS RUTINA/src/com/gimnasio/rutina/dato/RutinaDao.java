package com.gimnasio.rutina.dato;

import com.gimnasio.rutina.modelo.Rutina;
import com.gimnasio.rutina.modelo.RutinaEjercicio;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RutinaDao {

    private ConnectionDB conexionDB;

    public RutinaDao() {
        this.conexionDB = new ConnectionDB();
    }

    // 1. Mapeador principal de Rutina
    private Rutina mapRutina(ResultSet rs) throws SQLException {
        Rutina rutina = new Rutina();
        rutina.setId(rs.getInt("id"));
        rutina.setNombre(rs.getString("nombre"));
        rutina.setDescripcion(rs.getString("descripcion"));
        rutina.setNivel(rs.getString("nivel"));
        return rutina;
    }

    // 2. Mapeador de la tabla intermedia (RutinaEjercicio)
    private RutinaEjercicio mapRutinaEjercicio(ResultSet rs) throws SQLException {
        RutinaEjercicio re = new RutinaEjercicio();
        re.setId(rs.getInt("id"));
        re.setId_Rutina(rs.getInt("Id_Rutina"));
        re.setId_Ejercicio(rs.getInt("Id_Ejercicio"));
        re.setSeries(rs.getInt("series"));
        re.setRepeticiones(rs.getInt("repeticiones"));
        re.setDescansoSeg(rs.getInt("descansoSeg"));
        re.setOrden(rs.getInt("orden"));
        return re;
    }

    // Método auxiliar para obtener la lista de ejercicios de una rutina específica
    private List<RutinaEjercicio> obtenerDetalles(int idRutina, Connection conn) {
        List<RutinaEjercicio> lista = new ArrayList<>();
        String sql = "SELECT * FROM rutina_ejercicio WHERE Id_Rutina = ? ORDER BY orden ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idRutina);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRutinaEjercicio(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar detalles de la rutina: " + e.getMessage());
        }
        return lista;
    }

    // 3. listarRutinas()
    public List<Rutina> listarRutinas() {
        List<Rutina> lista = new ArrayList<>();
        String sql = "SELECT * FROM rutinas ORDER BY id ASC";
        
        try (Connection conn = conexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Rutina rutina = mapRutina(rs);
                // Cargamos los ejercicios asociados a esta rutina
                rutina.setEjercicios(obtenerDetalles(rutina.getId(), conn));
                lista.add(rutina);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar rutinas: " + e.getMessage());
        }
        return lista;
    }

    // 4. buscarPorId(id)
    public Rutina buscarPorId(int id) {
        Rutina rutina = null;
        String sql = "SELECT * FROM rutinas WHERE id = ?";
        
        try (Connection conn = conexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    rutina = mapRutina(rs);
                    // Cargamos los ejercicios asociados
                    rutina.setEjercicios(obtenerDetalles(rutina.getId(), conn));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar rutina: " + e.getMessage());
        }
        return rutina;
    }

    // 5. registrarRutina(Rutina)
    public boolean registrarRutina(Rutina rutina) {
        String sql = "INSERT INTO rutinas (nombre, descripcion, nivel) VALUES (?, ?, ?)";
        
        try (Connection conn = conexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, rutina.getNombre());
            ps.setString(2, rutina.getDescripcion());
            ps.setString(3, rutina.getNivel());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Error al registrar rutina: " + e.getMessage());
            return false;
        }
    }

    // 6. actualizarRutina(Rutina)
    public boolean actualizarRutina(Rutina rutina) {
        String sql = "UPDATE rutinas SET nombre = ?, descripcion = ?, nivel = ? WHERE id = ?";
        
        try (Connection conn = conexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, rutina.getNombre());
            ps.setString(2, rutina.getDescripcion());
            ps.setString(3, rutina.getNivel());
            ps.setInt(4, rutina.getId());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Error al actualizar rutina: " + e.getMessage());
            return false;
        }
    }

    // 7. eliminarRutina(id)
    public boolean eliminarRutina(int id) {
        String sql = "DELETE FROM rutinas WHERE id = ?";
        // Gracias al ON DELETE CASCADE en la BD, se borrarán automáticamente sus ejercicios asociados en rutina_ejercicio
        try (Connection conn = conexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Error al eliminar rutina: " + e.getMessage());
            return false;
        }
    }
}
