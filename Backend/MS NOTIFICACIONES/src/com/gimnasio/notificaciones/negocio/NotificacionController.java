package com.gimnasio.notificaciones.negocio;

import com.gimnasio.notificaciones.modelo.Notificacion;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Endpoints:
 *   POST /notificaciones                      -> registra una notificación
 *   GET  /notificaciones?usuarioId={id}       -> lista las de un usuario
 *   GET  /notificaciones?rol={ROL}            -> lista las dirigidas a un rol
 *   PUT  /notificaciones/{id}                 -> marca como leída
 */
public class NotificacionController implements HttpHandler {

    private NotificacionN notificacionN;
    private Gson gson;

    public NotificacionController() {
        this.notificacionN = new NotificacionN();
        this.gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // CORS (por si se consulta directo desde el frontend)
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");

        String method = exchange.getRequestMethod();
        if ("OPTIONS".equalsIgnoreCase(method)) {
            exchange.sendResponseHeaders(204, -1);
            exchange.getResponseBody().close();
            return;
        }

        try {
            switch (method) {
                case "GET":
                    handleGet(exchange);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "PUT":
                    handlePut(exchange);
                    break;
                default:
                    sendResponse(exchange, 405, jsonError("Método no permitido"));
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, jsonError("Error interno del servidor"));
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        Map<String, String> params = parseQuery(exchange.getRequestURI().getRawQuery());

        if (params.containsKey("usuarioId")) {
            int usuarioId;
            try {
                usuarioId = Integer.parseInt(params.get("usuarioId"));
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, jsonError("usuarioId inválido"));
                return;
            }
            List<Notificacion> lista = notificacionN.listarPorUsuario(usuarioId);
            if (lista == null) {
                sendResponse(exchange, 400, jsonError("usuarioId inválido"));
            } else {
                sendResponse(exchange, 200, gson.toJson(lista));
            }
        } else if (params.containsKey("rol")) {
            List<Notificacion> lista = notificacionN.listarPorRol(params.get("rol"));
            if (lista == null) {
                sendResponse(exchange, 400, jsonError("rol inválido"));
            } else {
                sendResponse(exchange, 200, gson.toJson(lista));
            }
        } else {
            sendResponse(exchange, 400, jsonError("Debe indicar usuarioId o rol como parámetro de consulta"));
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        Notificacion nueva = readBody(exchange, Notificacion.class);
        if (nueva != null && notificacionN.registrarNotificacion(nueva)) {
            sendResponse(exchange, 201, jsonError("Notificación registrada exitosamente"));
        } else {
            sendResponse(exchange, 400, jsonError("Error al registrar la notificación. Verifique los datos."));
        }
    }

    private void handlePut(HttpExchange exchange) throws IOException {
        int id = -1;
        String[] parts = exchange.getRequestURI().getPath().split("/");
        if (parts.length > 2) {
            try {
                id = Integer.parseInt(parts[2]);
            } catch (NumberFormatException e) {
                // id inválido
            }
        }
        if (id <= 0) {
            sendResponse(exchange, 400, jsonError("Debe proporcionar un ID de notificación válido"));
            return;
        }
        if (notificacionN.marcarLeida(id)) {
            sendResponse(exchange, 200, jsonError("Notificación marcada como leída"));
        } else {
            sendResponse(exchange, 404, jsonError("No se pudo marcar la notificación (¿existe?)"));
        }
    }

    private Map<String, String> parseQuery(String rawQuery) {
        Map<String, String> params = new HashMap<>();
        if (rawQuery == null || rawQuery.isEmpty()) {
            return params;
        }
        for (String pair : rawQuery.split("&")) {
            int idx = pair.indexOf('=');
            if (idx > 0) {
                String key = java.net.URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                String value = java.net.URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                params.put(key, value);
            }
        }
        return params;
    }

    private <T> T readBody(HttpExchange exchange, Class<T> clazz) {
        try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    private void sendResponse(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String jsonError(String message) {
        return "{\"mensaje\": \"" + message + "\"}";
    }
}
