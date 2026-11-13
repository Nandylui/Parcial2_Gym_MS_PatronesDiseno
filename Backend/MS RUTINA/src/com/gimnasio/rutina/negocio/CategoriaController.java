package com.gimnasio.rutina.negocio;

import com.gimnasio.rutina.modelo.Categoria;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CategoriaController implements HttpHandler {

    private CategoriaN categoriaN;
    private Gson gson;

    public CategoriaController() {
        this.categoriaN = new CategoriaN();
        this.gson = new Gson();
    }

    // Método principal exigido por el diagrama y la interfaz HttpHandler
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        
        // Extraer el ID de la URL si existe (ej: /rutinas/1)
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
                    handleCategoriaGet(exchange, id);
                    break;
                case "POST":
                    handleCategoriaPost(exchange);
                    break;
                case "PUT":
                    handleCategoriaPut(exchange, id);
                    break;
                case "DELETE":
                    handleCategoriaDelete(exchange, id);
                    break;
                default:
                    sendResponse(exchange, 405, jsonError("Método no permitido"));
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, jsonError("Error interno del servidor"));
        }
    }

    // Métodos específicos del diagrama
    private void handleCategoriaGet(HttpExchange exchange, int id) throws IOException {
        if (id > 0) {
            Categoria categoria = categoriaN.buscarPorId(id);
            if (categoria != null) {
                sendResponse(exchange, 200, gson.toJson(categoria));
            } else {
                sendResponse(exchange, 404, jsonError("Categoría no encontrada"));
            }
        } else {
            List<Categoria> lista = categoriaN.listarCategorias();
            sendResponse(exchange, 200, gson.toJson(lista));
        }
    }

    private void handleCategoriaPost(HttpExchange exchange) throws IOException {
        Categoria nuevaCategoria = readBody(exchange, Categoria.class);
        if (nuevaCategoria != null && categoriaN.registrarCategoria(nuevaCategoria)) {
            sendResponse(exchange, 201, jsonError("Categoría registrada exitosamente")); // Usamos jsonError como wrapper de mensaje simple
        } else {
            sendResponse(exchange, 400, jsonError("Error al registrar la categoría. Verifique los datos."));
        }
    }

    private void handleCategoriaPut(HttpExchange exchange, int id) throws IOException {
        if (id <= 0) {
            sendResponse(exchange, 400, jsonError("Debe proporcionar un ID válido para actualizar"));
            return;
        }
        Categoria categoriaActualizada = readBody(exchange, Categoria.class);
        if (categoriaActualizada != null) {
            categoriaActualizada.setId(id);
            if (categoriaN.actualizarCategoria(categoriaActualizada)) {
                sendResponse(exchange, 200, jsonError("Categoría actualizada exitosamente"));
            } else {
                sendResponse(exchange, 400, jsonError("Error al actualizar la categoría"));
            }
        } else {
            sendResponse(exchange, 400, jsonError("Cuerpo de la petición inválido"));
        }
    }

    // Nota: El diagrama dice handleCategoriaDelete(id), añadimos HttpExchange para poder responder al cliente
    private void handleCategoriaDelete(HttpExchange exchange, int id) throws IOException {
        if (id <= 0) {
            sendResponse(exchange, 400, jsonError("Debe proporcionar un ID válido para eliminar"));
            return;
        }
        if (categoriaN.eliminarCategoria(id)) {
            sendResponse(exchange, 200, jsonError("Categoría eliminada exitosamente"));
        } else {
            sendResponse(exchange, 404, jsonError("Error al eliminar. La categoría no existe o tiene dependencias."));
        }
    }

    // Métodos utilitarios del diagrama
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
