package com.gimnasio.usuario.dato;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {
    public static Connection getConnection() {
        Connection connection = null;
        try {
            String url = "jdbc:postgresql://localhost:5432/db_ms_usuario";
            String user = "postgres";   //usuario
            String password = "password"; // contraseña
            
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Conexión exitosa a db_ms_usuario");
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
        return connection;
    }

    public static void main(String[] args) {
        // 1. Crear un usuario de prueba
        com.gimnasio.usuario.modelo.Usuario nuevoUsuario = new com.gimnasio.usuario.modelo.Usuario();
        nuevoUsuario.setCi(1234567);
        nuevoUsuario.setNombre("Carlos");
        nuevoUsuario.setApellido("Perez");
        nuevoUsuario.setEmail("carlos.perez@email.com");
        nuevoUsuario.setTelefono(77712345);
        nuevoUsuario.setFechaNac(java.sql.Date.valueOf("1995-05-15"));
        nuevoUsuario.setGenero("Masculino");

        // 2. Llamar al DAO para registrarlo
        UsuarioDao dao = new UsuarioDao();
        dao.registrarUsuario(nuevoUsuario);
    }
}