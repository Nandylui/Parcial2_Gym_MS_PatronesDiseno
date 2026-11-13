package com.gimnasio.entrenamiento.dato;

import com.gimnasio.entrenamiento.modelo.PlanAsignacion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlanAsignacionDao {

    public PlanAsignacion mapPlanAsignacion(ResultSet rs) throws SQLException {
        PlanAsignacion plan = new PlanAsignacion();
        plan.setId(rs.getInt("id"));
        plan.setId_Asignacion(rs.getInt("Id_Asignacion"));
        plan.setEstrategia(rs.getString("estrategia"));
        plan.setFactorSeries(rs.getBigDecimal("factor_series").doubleValue());
        plan.setSesionesPorSemana(rs.getInt("sesiones_por_semana"));
        plan.setSesionesEsperadas(rs.getInt("sesiones_esperadas"));
        plan.setFechaCalculo(rs.getTimestamp("fecha_calculo"));
        return plan;
    }

    public boolean registrarPlan(PlanAsignacion plan) {
        String sql = "INSERT INTO planes_asignacion (Id_Asignacion, estrategia, factor_series, sesiones_por_semana, sesiones_esperadas) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, plan.getId_Asignacion());
            ps.setString(2, plan.getEstrategia());
            ps.setBigDecimal(3, java.math.BigDecimal.valueOf(plan.getFactorSeries()));
            ps.setInt(4, plan.getSesionesPorSemana());
            ps.setInt(5, plan.getSesionesEsperadas());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al registrar plan de asignación: " + e.getMessage());
            return false;
        }
    }

    public PlanAsignacion buscarPorAsignacion(int idAsignacion) {
        String sql = "SELECT * FROM planes_asignacion WHERE Id_Asignacion = ?";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idAsignacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapPlanAsignacion(rs);
                }
            }
        } catch (Exception e) {
            System.out.println("Error al buscar plan de la asignación: " + e.getMessage());
        }
        return null;
    }
}
