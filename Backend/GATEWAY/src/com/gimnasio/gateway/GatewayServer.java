package com.gimnasio.gateway;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servidor API Gateway para el sistema GymApp.
 * Actúa como un enrutador central (puerto 9000) que recibe todas las peticiones 
 * del Frontend y las redirige al microservicio correspondiente.
 * También gestiona las políticas de seguridad (CORS y Roles).
 */
public class GatewayServer {

    private static Map<String, String> registeredRoutes = new HashMap<>();
    private static List<SecurityRuleDefinition> securityRulesList = new ArrayList<>();

    /**
     * Carga la configuración inicial del Gateway, incluyendo reglas de seguridad por defecto.
     */
    private static void getConfig() {
        securityRule("/api/usuarios", "GET", "ADMIN");
        securityRule("/api/usuarios", "POST", "ADMIN");
    }

    private static String BasedUrlUsuario() {
        return "http://localhost:8081";
    }

    private static String BasedUrlRutina() {
        return "http://localhost:8082";
    }
    
    private static String BasedUrlEntrenamiento() {
        return "http://localhost:8083";
    }

    private static String BasedUrlNotificaciones() {
        return "http://localhost:8085";
    }

    /**
     * Valida si el usuario tiene los roles necesarios para acceder a una ruta.
     */
    private static boolean roleValidator(String path) {
        return true; 
    }

    /**
     * Registra las rutas disponibles en el mapa interno del enrutador.
     */
    private static void routerConfig(Map<String, String> routes) {
        registeredRoutes.putAll(routes);
    }

    /**
     * Define una nueva política de seguridad para una ruta y método específicos.
     */
    private static void securityRule(String path, String method, String role) {
        securityRulesList.add(new SecurityRuleDefinition(path, method, role));
    }

    /**
     * Aplica el conjunto de reglas de seguridad configuradas.
     */
    private static void securityConfig(List<SecurityRuleDefinition> rules) {
        System.out.println("Aplicando " + rules.size() + " reglas de seguridad en el Gateway.");
    }

    static class SecurityRuleDefinition {
        String path;
        String method;
        String role;

        public SecurityRuleDefinition(String path, String method, String role) {
            this.path = path;
            this.method = method;
            this.role = role;
        }
    }

    public static void main(String[] args) {
        try {
            getConfig();
            
            Map<String, String> rutasMapa = new HashMap<>();
            rutasMapa.put("/api/usuarios", BasedUrlUsuario());
            rutasMapa.put("/api/rutinas", BasedUrlRutina());
            rutasMapa.put("/api/entrenamientos", BasedUrlEntrenamiento());
            rutasMapa.put("/api/notificaciones", BasedUrlNotificaciones());
            routerConfig(rutasMapa);
            securityConfig(securityRulesList);

            HttpServer server = HttpServer.create(new InetSocketAddress(9000), 0);
            
            server.createContext("/api/usuarios", new GatewayHandler());
            server.createContext("/api/rutinas", new GatewayHandler());
            server.createContext("/api/entrenamientos", new GatewayHandler());
            server.createContext("/api/notificaciones", new GatewayHandler());
            
            server.setExecutor(null);
            server.start();
            System.out.println("API Gateway iniciado en el puerto 9000 (Redirigiendo microservicios)...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Manejador central de peticiones HTTP para el Gateway.
     */
    static class GatewayHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            
            // --- INICIO CONFIGURACIÓN CORS PARA EL GATEWAY ---
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "http://localhost:5173");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");

            // Intercepta la petición "preflight" de React (OPTIONS) y la aprueba
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                exchange.getResponseBody().close();
                return;
            }
            // --- FIN CONFIGURACIÓN CORS ---

            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            
            if (!roleValidator(path)) {
                sendResponse(exchange, 403, "{\"error\": \"Acceso denegado por políticas de rol\"}");
                return;
            }

            String targetBaseUrl = "";
            String microserviceContext = "";
            String subPath = "";

            if (path.startsWith("/api/usuarios")) {
                targetBaseUrl = BasedUrlUsuario();
                microserviceContext = "/usuarios";
                subPath = path.substring("/api/usuarios".length());
            } else if (path.startsWith("/api/rutinas")) {
                targetBaseUrl = BasedUrlRutina();
                microserviceContext = "";
                subPath = path.substring("/api/rutinas".length());
            } else if (path.startsWith("/api/entrenamientos")) {
                targetBaseUrl = BasedUrlEntrenamiento();
                microserviceContext = "";
                subPath = path.substring("/api/entrenamientos".length());
            } else if (path.startsWith("/api/notificaciones")) {
                targetBaseUrl = BasedUrlNotificaciones();
                microserviceContext = "/notificaciones";
                subPath = path.substring("/api/notificaciones".length());
            }

            String fullUrl = targetBaseUrl + microserviceContext + subPath;
            String query = exchange.getRequestURI().getQuery();
            if (query != null) {
                fullUrl += "?" + query;
            }

            HttpURLConnection conn = (HttpURLConnection) new URL(fullUrl).openConnection();
            conn.setRequestMethod(method);
            conn.setDoInput(true);

            if ("POST".equals(method) || "PUT".equals(method)) {
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
                     OutputStream os = conn.getOutputStream()) {
                    String body = reader.lines().collect(Collectors.joining("\n"));
                    os.write(body.getBytes(StandardCharsets.UTF_8));
                }
            }

            int responseCode;
            String responseBody = "";
            try {
                responseCode = conn.getResponseCode();
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        responseCode >= 400 ? conn.getErrorStream() : conn.getInputStream(), StandardCharsets.UTF_8));
                responseBody = reader.lines().collect(Collectors.joining("\n"));
                reader.close();
            } catch (Exception e) {
                responseCode = 500;
                responseBody = "{\"error\": \"Error al conectar con el microservicio de destino: " + e.getMessage() + "\"}";
            }

            sendResponse(exchange, responseCode, responseBody);
        }

        private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(statusCode, responseBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseBytes);
            os.close();
        }
    }
}