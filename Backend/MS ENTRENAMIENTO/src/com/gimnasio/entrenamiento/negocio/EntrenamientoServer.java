package com.gimnasio.entrenamiento.negocio;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class EntrenamientoServer {
    
    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8083), 0);
            
            // Mapeo de la ruta principal para MS ENTRENAMIENTO
            server.createContext("/asignaciones", new AsignacionController());
            server.createContext("/sesiones", new SesionEntrenamientoController());
            
            server.setExecutor(null);
            server.start();
            System.out.println("MS_Entrenamiento Server iniciado y escuchando en el puerto 8083...");
            
        } catch (IOException e) {
            System.out.println("Error al iniciar el servidor MS_Entrenamiento: " + e.getMessage());
        }
    }
}
