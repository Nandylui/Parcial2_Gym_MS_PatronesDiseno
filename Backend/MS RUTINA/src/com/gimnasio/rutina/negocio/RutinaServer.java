package com.gimnasio.rutina.negocio;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class RutinaServer {
    
    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8082), 0);
            
            // Mapeo ordenado de todas las rutas del MS_RUTINA
            server.createContext("/categorias", new CategoriaController());
            server.createContext("/ejercicios", new EjercicioController());
            server.createContext("/rutinas", new RutinaController());
            
            server.setExecutor(null);
            server.start();
            System.out.println("MS_Rutina Server iniciado y escuchando en el puerto 8082...");
            
        } catch (IOException e) {
            System.out.println("Error al iniciar el servidor MS_Rutina: " + e.getMessage());
        }
    }
}