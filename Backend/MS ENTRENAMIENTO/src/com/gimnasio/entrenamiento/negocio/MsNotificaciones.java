package com.gimnasio.entrenamiento.negocio;

import com.google.gson.Gson;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/** Cliente HTTP hacia MS Notificaciones (puerto 8085). */
public class MsNotificaciones {

    private static final String MS_Notificaciones_URL = "http://localhost:8085/notificaciones";

    /**
     * Envía un aviso. Indicar usuarioId (aviso a una persona) o rolDestino (aviso a un rol), o ambos.
     * Devuelve false si MS Notificaciones no está disponible o no lo acepta; nunca lanza excepción.
     */
    public static boolean enviar(Integer usuarioId, String rolDestino, String tipo, String mensaje) {
        try {
            Map<String, Object> cuerpo = new LinkedHashMap<>();
            if (usuarioId != null) {
                cuerpo.put("usuarioId", usuarioId);
            }
            if (rolDestino != null) {
                cuerpo.put("rolDestino", rolDestino);
            }
            cuerpo.put("tipo", tipo);
            cuerpo.put("mensaje", mensaje);

            URL url = URI.create(MS_Notificaciones_URL).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(new Gson().toJson(cuerpo).getBytes(StandardCharsets.UTF_8));
            }
            return conn.getResponseCode() == 201;

        } catch (Exception e) {
            System.out.println("Aviso: no se pudo enviar la notificación a MS Notificaciones: " + e.getMessage());
            return false;
        }
    }
}
