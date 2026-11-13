package com.gimnasio.entrenamiento.negocio;

import com.gimnasio.entrenamiento.modelo.Asignacion;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class AsignacionController implements HttpHandler {

    private AsignacionN asignacionN;
    private Gson gson;

    public AsignacionController() {
        this.asignacionN = new AsignacionN();
        this.gson = FechaGson.crear();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        try {
            switch (method) {
                case "GET":
                    handleAsignacionGet(exchange);
                    break;
                case "POST":
                    handleAsignacionPost(exchange);
                    break;
                default:
                    sendResponse(exchange, 405, jsonError("Método no permitido para este caso de uso"));
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, jsonError("Error interno del servidor"));
        }
    }

    private void handleAsignacionGet(HttpExchange exchange) throws IOException {
        int id = obtenerIdDeRuta(exchange);

        if (id > 0) {
            // CU6: GET /asignaciones/{id} (o /asignaciones/{id}/plan) devuelve el plan ajustado
            Map<String, Object> plan = asignacionN.obtenerPlan(id);
            if (plan != null) {
                sendResponse(exchange, 200, gson.toJson(plan));
            } else {
                sendResponse(exchange, 404, jsonError("Asignación no encontrada"));
            }
            return;
        }

        List<Asignacion> lista = asignacionN.listarAsignaciones();
        sendResponse(exchange, 200, gson.toJson(lista));
    }

    private int obtenerIdDeRuta(HttpExchange exchange) {
        String[] partes = exchange.getRequestURI().getPath().split("/");
        if (partes.length > 2) {
            try {
                return Integer.parseInt(partes[2]);
            } catch (NumberFormatException e) {
                // id inválido: se ignora y se lista todo
            }
        }
        return -1;
    }

    private void handleAsignacionPost(HttpExchange exchange) throws IOException {
        Asignacion nueva = readBody(exchange, Asignacion.class);
        Asignacion creada = (nueva != null) ? asignacionN.registrarAsignacion(nueva) : null;
        
        if (creada != null) {
            sendResponse(exchange, 201, gson.toJson(creada));
        } else {
            sendResponse(exchange, 400, jsonError("Error al registrar la asignación. Verifique los datos."));
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