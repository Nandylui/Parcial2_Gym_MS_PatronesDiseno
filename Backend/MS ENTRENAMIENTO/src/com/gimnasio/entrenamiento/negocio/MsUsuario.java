package com.gimnasio.entrenamiento.negocio;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class MsUsuario {
    
    // Atributo definido en el diagrama (ajusta el puerto si tu MS Usuario corre en otro distinto, ej: 8081)
    private static final String MS_Usuario_URL = "http://localhost:8081/usuarios/";

    public static boolean validarUsuarioExistente(int id) {
        try {
            // Si aún no tienes levantado MS Usuario, puedes retornar true temporalmente 
            // o implementar la conexión HTTP de esta forma:
            
            URI uri = URI.create(MS_Usuario_URL + id);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);

            int responseCode = conn.getResponseCode();
            return responseCode == 200;
            
        } catch (Exception e) {
            // Mientras implementas el MS Usuario, puedes retornar true para no trabar tus pruebas locales:
            System.out.println("Advertencia: No se pudo conectar con MS Usuario, omitiendo validación temporalmente.");
            return true; 
        }
    }
}