package com.gimnasio.entrenamiento.dato;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Asegúrate de que el nombre de la BD sea exactamente 'db_ms_entrenamiento'
            String url = "jdbc:postgresql://localhost:5432/db_ms_entrenamiento";
            String user = "postgres";
            String password = "password"; 
            
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Conexión exitosa a db_ms_entrenamiento");
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
        return connection;
    }
}