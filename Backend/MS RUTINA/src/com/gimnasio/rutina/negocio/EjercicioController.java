package com.gimnasio.rutina.negocio;

import com.gimnasio.rutina.modelo.Ejercicio;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EjercicioController implements HttpHandler {

    private EjercicioN ejercicioN;
    private Gson gson;

    public EjercicioController() {
        this.ejercicioN = new EjercicioN();
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
                    handleEjercicioGet(exchange, id);
                    break;
                case "POST":
                    handleEjercicioPost(exchange);
                    break;
                case "PUT":
                    handleEjercicioPut(exchange, id);
                    break;
                case "DELETE":
                    handleEjercicioDelete(exchange, id);
                    break;
                default:
                    sendResponse(exchange, 405, jsonError("Método no permitido"));
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, jsonError("Error interno del servidor"));
        }
    }

    private void handleEjercicioGet(HttpExchange exchange, int id) throws IOException {
        if (id > 0) {
            Ejercicio ejercicio = ejercicioN.buscarPorId(id);
            if (ejercicio != null) {
                sendResponse(exchange, 200, gson.toJson(ejercicio));
            } else {
                sendResponse(exchange, 404, jsonError("Ejercicio no encontrado"));
            }
        } else {
            List<Ejercicio> lista = ejercicioN.listarEjercicios();
            sendResponse(exchange, 200, gson.toJson(lista));
        }
    }

    private void handleEjercicioPost(HttpExchange exchange) throws IOException {
        Ejercicio nuevo = readBody(exchange, Ejercicio.class);
        if (nuevo != null && ejercicioN.registrarEjercicio(nuevo)) {
            sendResponse(exchange, 201, jsonError("Ejercicio registrado exitosamente"));
        } else {
            sendResponse(exchange, 400, jsonError("Error al registrar. Verifique los datos."));
        }
    }

    private void handleEjercicioPut(HttpExchange exchange, int id) throws IOException {
        if (id <= 0) {
            sendResponse(exchange, 400, jsonError("ID válido requerido"));
            return;
        }
        Ejercicio ejercicio = readBody(exchange, Ejercicio.class);
        if (ejercicio != null) {
            ejercicio.setId(id);
            if (ejercicioN.actualizarEjercicio(ejercicio)) {
                sendResponse(exchange, 200, jsonError("Ejercicio actualizado"));
            } else {
                sendResponse(exchange, 400, jsonError("Error al actualizar"));
            }
        } else {
            sendResponse(exchange, 400, jsonError("Cuerpo inválido"));
        }
    }

    private void handleEjercicioDelete(HttpExchange exchange, int id) throws IOException {
        if (id <= 0) {
            sendResponse(exchange, 400, jsonError("ID válido requerido"));
            return;
        }
        if (ejercicioN.eliminarEjercicio(id)) {
            sendResponse(exchange, 200, jsonError("Ejercicio eliminado"));
        } else {
            sendResponse(exchange, 404, jsonError("Error al eliminar"));
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
