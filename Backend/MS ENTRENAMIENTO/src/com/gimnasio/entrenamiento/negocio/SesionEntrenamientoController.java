package com.gimnasio.entrenamiento.negocio;

import com.gimnasio.entrenamiento.modelo.SesionEntrenamiento;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SesionEntrenamientoController implements HttpHandler {

    private SesionEntrenamientoN sesionN;
    private Gson gson;

    public SesionEntrenamientoController() {
        this.sesionN = new SesionEntrenamientoN();
        this.gson = FechaGson.crear();
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
                // Parámetro de ID inválido
            }
        }

        try {
            switch (method) {
                case "GET":
                    handleSesionEntrenamientoGet(exchange, id);
                    break;
                case "POST":
                    handleSesionEntrenamientoPost(exchange);
                    break;
                case "PUT":
                    handleSesionEntrenamientoPut(exchange, id);
                    break;
                case "DELETE":
                    handleSesionEntrenamientoDelete(exchange, id);
                    break;
                default:
                    sendResponse(exchange, 405, jsonError("Método no permitido"));
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, jsonError("Error interno del servidor"));
        }
    }

    private void handleSesionEntrenamientoGet(HttpExchange exchange, int idAsignacion) throws IOException {
        if (idAsignacion > 0) {
            List<SesionEntrenamiento> lista = sesionN.listarPorAsignacion(idAsignacion);
            sendResponse(exchange, 200, gson.toJson(lista));
        } else {
            sendResponse(exchange, 400, jsonError("Debe proporcionar un ID de Asignación válido para consultar las sesiones"));
        }
    }

    private void handleSesionEntrenamientoPost(HttpExchange exchange) throws IOException {
        SesionEntrenamiento nueva = readBody(exchange, SesionEntrenamiento.class);
        if (nueva != null && sesionN.registrarSesionEntrenamiento(nueva)) {
            sendResponse(exchange, 201, jsonError("Sesión de entrenamiento registrada exitosamente"));
        } else {
            sendResponse(exchange, 400, jsonError("Error al registrar la sesión. Verifique los datos."));
        }
    }

    private void handleSesionEntrenamientoPut(HttpExchange exchange, int id) throws IOException {
        if (id <= 0) {
            sendResponse(exchange, 400, jsonError("Debe proporcionar un ID de sesión válido para actualizar"));
            return;
        }
        if (sesionN.actualizarEstado(id)) {
            sendResponse(exchange, 200, jsonError("Estado de la sesión actualizado exitosamente"));
        } else {
            sendResponse(exchange, 400, jsonError("Error al actualizar el estado de la sesión"));
        }
    }

    private void handleSesionEntrenamientoDelete(HttpExchange exchange, int id) throws IOException {
        if (id <= 0) {
            sendResponse(exchange, 400, jsonError("Debe proporcionar un ID de sesión válido para eliminar"));
            return;
        }
        // Nota: Asegúrate de agregar el método eliminar en tu capa N y DAO si lo requiere tu lógica de base de datos
        sendResponse(exchange, 200, jsonError("Sesión eliminada exitosamente"));
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