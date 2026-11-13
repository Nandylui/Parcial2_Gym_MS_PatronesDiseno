package com.gimnasio.notificaciones.negocio;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class NotificacionesServer {

    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8085), 0);

            server.createContext("/notificaciones", new NotificacionController());

            server.setExecutor(null);
            server.start();
            System.out.println("MS_Notificaciones Server iniciado y escuchando en el puerto 8085...");

        } catch (IOException e) {
            System.out.println("Error al iniciar el servidor MS_Notificaciones: " + e.getMessage());
        }
    }
}
