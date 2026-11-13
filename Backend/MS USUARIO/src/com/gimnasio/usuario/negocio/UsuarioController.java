package com.gimnasio.usuario.negocio;

import com.gimnasio.usuario.modelo.Usuario;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializationContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

public class UsuarioController implements HttpHandler {

    private UsuarioN negocio;
    private Gson gson;

    public UsuarioController() {
        this.negocio = new UsuarioN();
        
        // Adaptador robusto para procesar las fechas de React a PostgreSQL
        this.gson = new GsonBuilder()
            .registerTypeAdapter(java.sql.Date.class, new JsonDeserializer<java.sql.Date>() {
                @Override
                public java.sql.Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
                    String fechaString = json.getAsString();
                    if (fechaString == null || fechaString.trim().isEmpty()) {
                        return null;
                    }
                    try {
                        // Lee el formato exacto que envía el input type="date" de React (yyyy-MM-dd)
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        java.util.Date parsed = format.parse(fechaString);
                        // Lo convierte al formato estricto que exige PostgreSQL (java.sql.Date)
                        return new java.sql.Date(parsed.getTime());
                    } catch (ParseException e) {
                        System.out.println("Error parseando fecha: " + fechaString);
                        return null;
                    }
                }
            })
            .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        
        try {
            switch (method) {
                case "GET":
                    handleUsuarioGet(exchange);
                    break;
                case "POST":
                    handleUsuarioPost(exchange);
                    break;
                case "PUT":
                    handleUsuarioPut(exchange);
                    break;
                case "DELETE":
                    handleUsuarioDelete(exchange);
                    break;
                default:
                    sendResponse(exchange, 405, jsonError("Método HTTP no soportado"));
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, jsonError("Error interno del servidor: " + e.getMessage()));
        }
    }

    private void handleUsuarioGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        if (parts.length <= 2 || parts[2].isEmpty()) {
            List<Usuario> lista = negocio.listarUsuario();
            String jsonResponse = gson.toJson(lista);
            sendResponse(exchange, 200, jsonResponse);
        } else {
            try {
                int id = Integer.parseInt(parts[2]);
                Usuario usuario = negocio.buscarPorId(id);
                if (usuario != null) {
                    sendResponse(exchange, 200, gson.toJson(usuario));
                } else {
                    sendResponse(exchange, 404, jsonError("Usuario no encontrado"));
                }
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, jsonError("ID inválido"));
            }
        }
    }

    private void handleUsuarioPost(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String body = readBody(exchange);

        if (path.endsWith("/login")) {
            Usuario credenciales = gson.fromJson(body, Usuario.class);
            Usuario usuarioEnBd = negocio.buscarPorEmail(credenciales.getEmail());
            
            if (usuarioEnBd != null && usuarioEnBd.getContrasena().equals(credenciales.getContrasena())) {
                List<String> roles = negocio.obtenerRoles(usuarioEnBd.getId());
                
                if (roles.isEmpty() && usuarioEnBd.getEmail().equals("admin@gym.com")) {
                    roles.add("INSTRUCTOR");
                }

                String respuestaJson = "{\"usuario\": " + gson.toJson(usuarioEnBd) + ", \"roles\": " + gson.toJson(roles) + "}";
                sendResponse(exchange, 200, respuestaJson);
            } else {
                sendResponse(exchange, 401, jsonError("Credenciales inválidas")); 
            }

        } else {
            Usuario nuevoUsuario = gson.fromJson(body, Usuario.class);
            boolean creado = negocio.registrarUsuario(nuevoUsuario);
            if (creado) {
                sendResponse(exchange, 201, "{\"mensaje\": \"Usuario registrado exitosamente\"}");
            } else {
                sendResponse(exchange, 400, jsonError("No se pudo registrar el usuario (posible correo duplicado)"));
            }
        }
    }

    private void handleUsuarioPut(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        Usuario usuarioModificado = gson.fromJson(body, Usuario.class);

        boolean actualizado = negocio.actualizarUsuario(usuarioModificado);
        if (actualizado) {
            sendResponse(exchange, 200, "{\"mensaje\": \"Usuario actualizado exitosamente\"}");
        } else {
            sendResponse(exchange, 400, jsonError("No se pudo actualizar el usuario"));
        }
    }

    private void handleUsuarioDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        if (parts.length > 2 && !parts[2].isEmpty()) {
            try {
                int id = Integer.parseInt(parts[2]);
                boolean eliminado = negocio.eliminarUsuario(id);
                if (eliminado) {
                    sendResponse(exchange, 200, "{\"mensaje\": \"Usuario eliminado exitosamente\"}");
                } else {
                    sendResponse(exchange, 404, jsonError("Usuario no encontrado para eliminar"));
                }
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, jsonError("ID inválido"));
            }
        } else {
            sendResponse(exchange, 400, jsonError("Debe proporcionar un ID para eliminar"));
        }
    }

    private String readBody(HttpExchange exchange) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private void sendResponse(HttpExchange exchange, int status, String body) throws IOException {
        byte[] responseBytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }

    private String jsonError(String message) {
        return "{\"error\": \"" + message + "\"}";
    }
}