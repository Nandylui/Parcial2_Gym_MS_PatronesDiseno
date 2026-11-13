package com.gimnasio.notificaciones.dato;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // La BD debe llamarse exactamente 'db_ms_notificaciones'
            String url = "jdbc:postgresql://localhost:5432/db_ms_notificaciones";
            String user = "postgres";
            String password = "password";

            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
        return connection;
    }
}
