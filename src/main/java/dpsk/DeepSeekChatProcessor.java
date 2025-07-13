package dpsk;

import javax.swing.*;
import okhttp3.*;
import com.google.gson.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;
import screen.iniciarExcel;

public class DeepSeekChatProcessor {
    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";
    private final String apiKey;
    private final OkHttpClient client;
    private final Gson gson;
    private final JLabel statusLabel;
    private JFrame parentFrame;
    
    public DeepSeekChatProcessor(JLabel statusLabel, JFrame parentFrame) throws IOException {
        this.statusLabel = statusLabel;
        this.parentFrame = parentFrame;
        this.apiKey = loadApiKeyFromFile();
        System.out.println("API Key status: " + (apiKey != null && !apiKey.isEmpty() ? "OK" : "FAILED"));
        
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    private String loadApiKeyFromFile() throws IOException {
        Path keyPath = Paths.get(
            System.getProperty("user.dir"), 
            "src", "main", "resources", "DSapiK.txt"
        );
        System.out.println("Loading API key from: " + keyPath);
        return Files.readString(keyPath).trim();
    }

    public void procesarArchivos() {
        new Thread(() -> {
            try {
                System.out.println("Starting processing...");
                mostrarMensajeTemporal("Processing started", 2000);
                
                // Cargar archivos
                String datosJson = loadResourceAsString("datos.json");
                String apusJson = loadResourceAsString("lista_de_apus.json");
                
                // Verificar conexión
                String greetingResponse = chatWithDeepSeek("Confirm connection");
                System.out.println("Connection test: " + greetingResponse);
                
                // Procesar archivos
                String processRequest = buildProcessRequest(datosJson, apusJson);
                String processResponse = chatWithDeepSeek(processRequest);
                
                // Guardar resultado
                saveResult(processResponse);
                
                mostrarMensajeTemporal("Processing completed", 3000);
            } catch (Exception e) {
                System.err.println("PROCESSING ERROR:");
                e.printStackTrace();
                mostrarMensajeTemporal("Error: " + e.getMessage(), 5000);
            } finally {
                System.out.println("=== INICIANDO CREACION DE EXCEL ===");
                
                //#################
 try {
                    // Crear y mostrar nuevo frame
                    SwingUtilities.invokeLater(() -> {
                        iniciarExcel nuevoFrame = new iniciarExcel();
                        nuevoFrame.setLocationRelativeTo(null);
                        nuevoFrame.setVisible(true);

                        // Cerrar el frame padre si existe
                        if (parentFrame != null && parentFrame.isDisplayable()) {
                            parentFrame.dispose();
                        }
                    });
                } catch (Exception e) {
                    System.err.println("ERROR AL CREAR NUEVO FRAME:");
                    e.printStackTrace();
                    mostrarMensajeTemporal("Error al abrir nueva ventana: " + e.getMessage(), 5000);
                }
                
                    }
        }).start();
    }

    private String loadResourceAsString(String resourceName) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (is == null) {
                throw new FileNotFoundException("Resource not found: " + resourceName);
            }
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println(resourceName + " loaded (" + content.length() + " chars)");
            return content;
        }
    }

    private String buildProcessRequest(String datosJson, String apusJson) {
        return String.format(
            "PROCESAR ESTOS DATOS:\n\n" +
            "ARCHIVO datos.json:\n%s\n\n" +
            "ARCHIVO lista_de_apus.json:\n%s\n\n" +
            "INSTRUCCIONES:\n" +
            "1. Para cada elemento en 'Descripcion' de datos.json, encuentra la mejor coincidencia en 'descripcion' de lista_de_apus.json\n" +
            "2. Devuelve UNICAMENTE un JSON con este formato:\n" +
            "[{\"Descripcion\": \"texto original\", \"Cantidad\": \"cantidades de 'datos'\", \"code\": \"code coincidente\"}]\n" +
            "3. Si no hay coincidencia, usa \"04\" como code\n" +
            "4. No incluyas explicaciones, solo el JSON requerido",
            datosJson, apusJson
        );
    }

    private String chatWithDeepSeek(String message) throws IOException {
        System.out.println("Sending to DeepSeek: " + message.substring(0, Math.min(100, message.length())) + "...");
        
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "deepseek-chat");
        
        JsonArray messages = new JsonArray();
        
        // Mensaje de sistema para contexto
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", "Eres un asistente técnico. Responde solo con el JSON solicitado.");
        messages.add(systemMessage);
        
        // Mensaje del usuario
        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", message);
        messages.add(userMessage);
        
        requestBody.add("messages", messages);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(RequestBody.create(
                    gson.toJson(requestBody),
                    MediaType.parse("application/json; charset=utf-8")
                ))
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No details";
                throw new IOException("API error: " + response.code() + " - " + errorBody);
            }

            String responseBody = response.body().string();
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
            
            return jsonResponse.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();
        }
    }

    private void saveResult(String apiResponse) throws IOException {
        // Extraer solo el JSON de la respuesta
        String jsonContent = apiResponse.replaceAll("(?s)^.*?(\\[\\{.*\\}\\]).*$", "$1");
        
        Path resultPath = Paths.get(
            System.getProperty("user.dir"),
            "src", "main", "resources", "resultado.json"
        );
        
        Files.writeString(resultPath, jsonContent, StandardCharsets.UTF_8);
        System.out.println("Result saved to: " + resultPath);
    }

    private void mostrarMensajeTemporal(String mensaje, int duracionMs) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(mensaje);
            new Timer(duracionMs, e -> statusLabel.setText("")).start();
        });
    }
}