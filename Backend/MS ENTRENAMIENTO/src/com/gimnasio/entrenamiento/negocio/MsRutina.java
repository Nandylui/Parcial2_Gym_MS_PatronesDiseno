package com.gimnasio.entrenamiento.negocio;

import com.gimnasio.entrenamiento.modelo.RutinaRemota;
import com.google.gson.Gson;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MsRutina {
    
    // Atributo definido en el diagrama
    private static final String MS_Rutina_URL = "http://localhost:8082/rutinas/";

    public static boolean validarRutinaExistente(int id) {
        try {
            URI uri = URI.create(MS_Rutina_URL + id);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);

            int responseCode = conn.getResponseCode();
            // Si el código es 200, la rutina existe
            return responseCode == 200;
            
        } catch (Exception e) {
            System.out.println("Error al conectar con MS Rutina: " + e.getMessage());
            return false;
        }
    }

    /** Obtiene la rutina completa desde MS Rutina. Devuelve null si no existe o el MS no responde. */
    public static RutinaRemota obtenerRutina(int id) {
        try {
            URI uri = URI.create(MS_Rutina_URL + id);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);

            if (conn.getResponseCode() != 200) {
                return null;
            }
            try (InputStreamReader reader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)) {
                return new Gson().fromJson(reader, RutinaRemota.class);
            }
        } catch (Exception e) {
            System.out.println("Error al obtener la rutina desde MS Rutina: " + e.getMessage());
            return null;
        }
    }

    /** Devuelve el nivel de la rutina (Principiante/Intermedio/Avanzado) o null si no se pudo obtener. */
    public static String obtenerNivel(int id) {
        RutinaRemota rutina = obtenerRutina(id);
        return (rutina != null) ? rutina.getNivel() : null;
    }
}
