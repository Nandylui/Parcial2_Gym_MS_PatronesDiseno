package com.gimnasio.rutina.negocio;

import com.gimnasio.rutina.modelo.Rutina;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RutinaController implements HttpHandler {

    private RutinaN rutinaN;
    private Gson gson;

    public RutinaController() {
        this.rutinaN = new RutinaN();
        this.gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        
        int id = -1;
        String[] pathParts = path.split("/");
        if (pathParts.length > 2) {
            try {
                id = Integer.parseInt(pathParts[2]);
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, jsonError("ID inválido"));
                return;
            }
        }

        try {
            switch (method) {
                case "GET":
                    handleRutinaGet(exchange, id);
                    break;
                case "POST":
                    handleRutinaPost(exchange);
                    break;
                case "PUT":
                    handleRutinaPut(exchange, id);
                    break;
                case "DELETE":
                    handleRutinaDelete(exchange, id);
                    break;
                default:
                    sendResponse(exchange, 405, jsonError("Método no permitido"));
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, jsonError("Error interno del servidor"));
        }
    }

    private void handleRutinaGet(HttpExchange exchange, int id) throws IOException {
        if (id > 0) {
            Rutina rutina = rutinaN.buscarPorId(id);
            if (rutina != null) {
                sendResponse(exchange, 200, gson.toJson(rutina));
            } else {
                sendResponse(exchange, 404, jsonError("Rutina no encontrada"));
            }
        } else {
            List<Rutina> lista = rutinaN.listarRutinas();
            sendResponse(exchange, 200, gson.toJson(lista));
        }
    }

    private void handleRutinaPost(HttpExchange exchange) throws IOException {
        Rutina nuevaRutina = readBody(exchange, Rutina.class);
        if (nuevaRutina != null && rutinaN.registrarRutina(nuevaRutina)) {
            sendResponse(exchange, 201, jsonError("Rutina registrada exitosamente"));
        } else {
            sendResponse(exchange, 400, jsonError("Error al registrar la rutina. Verifique los datos."));
        }
    }

    private void handleRutinaPut(HttpExchange exchange, int id) throws IOException {
        if (id <= 0) {
            sendResponse(exchange, 400, jsonError("Debe proporcionar un ID válido para actualizar"));
            return;
        }
        Rutina rutinaActualizada = readBody(exchange, Rutina.class);
        if (rutinaActualizada != null) {
            rutinaActualizada.setId(id);
            if (rutinaN.actualizarRutina(rutinaActualizada)) {
                sendResponse(exchange, 200, jsonError("Rutina actualizada exitosamente"));
            } else {
                sendResponse(exchange, 400, jsonError("Error al actualizar la rutina"));
            }
        } else {
            sendResponse(exchange, 400, jsonError("Cuerpo de la petición inválido"));
        }
    }

    private void handleRutinaDelete(HttpExchange exchange, int id) throws IOException {
        if (id <= 0) {
            sendResponse(exchange, 400, jsonError("Debe proporcionar un ID válido para eliminar"));
            return;
        }
        if (rutinaN.eliminarRutina(id)) {
            sendResponse(exchange, 200, jsonError("Rutina eliminada exitosamente"));
        } else {
            sendResponse(exchange, 404, jsonError("Error al eliminar. La rutina no existe."));
        }
    }

    private <T> T readBody(HttpExchange exchange, Class<T> clazz) {
        try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    private void sendResponse(HttpExchange exchange, int status, String body) throws IOException {
        byte[] responseBytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    private String jsonError(String message) {
        return "{\"mensaje\": \"" + message + "\"}";
    }
}
