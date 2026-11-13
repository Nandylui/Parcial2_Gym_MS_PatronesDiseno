package com.gimnasio.rutina.dato;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {
    
    // Conectamos a la nueva base de datos db_ms_rutina
    private static final String URL = "jdbc:postgresql://localhost:5432/db_ms_rutina";
    private static final String USER = "postgres";
    private static final String PASSWORD = "password"; // contraseña

    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa a db_ms_rutina");
        } catch (SQLException e) {
            System.out.println("Error de conexión a db_ms_rutina: " + e.getMessage());
        }
        return connection;
    }
}
