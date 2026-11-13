package com.gimnasio.usuario.negocio;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class UsuarioServer {

    public static void main(String[] args) {
        try {
            // El microservicio MS_Usuario corre en el puerto 8081
            HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
            
            // Asignamos el controlador a la ruta /usuarios
            server.createContext("/usuarios", new UsuarioController());
            
            server.setExecutor(null); // crea un executor por defecto
            server.start();
            
            System.out.println("MS_Usuario Server iniciado y escuchando en el puerto 8081...");
        } catch (IOException e) {
            System.out.println("Error al iniciar el servidor MS_Usuario: " + e.getMessage());
        }
    }
}
