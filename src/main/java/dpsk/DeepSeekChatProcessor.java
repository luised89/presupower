package dpsk;

import javax.swing.*;
import okhttp3.*;
import com.google.gson.*;
import java.io.*;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;

public class DeepSeekChatProcessor {
    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";
    private final String apiKey;
    private final OkHttpClient client;
    private final Gson gson;
    private JLabel statusLabel;

    public DeepSeekChatProcessor(JLabel statusLabel) throws IOException {
        this.statusLabel = statusLabel;
        this.apiKey = leerApiKeyDeArchivo();
        System.out.println("API Key cargada: " + (apiKey != null && !apiKey.isEmpty() ? "OK" : "FALLO"));
        
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    private String leerApiKeyDeArchivo() throws IOException {
        String resourcesPath = System.getProperty("user.dir") + File.separator + "src" + 
                            File.separator + "main" + File.separator + "resources" + 
                            File.separator + "DSapiK.txt";
        System.out.println("Buscando API Key en: " + resourcesPath);
        return new String(Files.readAllBytes(Paths.get(resourcesPath))).trim();
    }

    public void procesarArchivos() {
        System.out.println("Iniciando procesamiento...");
        new Thread(() -> {
            try {
                System.out.println("=== INICIANDO SALUDO ===");
                mostrarMensajeTemporal("En línea", 3000);
                String saludoRespuesta = chatWithDeepSeek("Hola");
                System.out.println("Respuesta saludo: " + saludoRespuesta);

                System.out.println("=== CARGANDO ARCHIVOS ===");
                mostrarMensajeTemporal("Cargando archivos", 5000);
                String loadRequest = "Por favor carga los archivos JSON 'datos.json' y 'lista_de_apus.json'";
                String loadResponse = chatWithDeepSeek(loadRequest);
                System.out.println("Respuesta carga archivos: " + loadResponse);

                System.out.println("=== PROCESANDO ARCHIVOS ===");
                String processRequest = "El archivo 'lista_de_apus' tiene 4 columnas. " +
                        "Crea un tercer JSON de 2 columnas: 'id' y 'descripcion'. " +
                        "Busca el contenido de cada fila de 'datos' en la columna 'descripcion' de 'lista_de_apus'. " +
                        "La coincidencia no es exacta, encuentra la mejor coincidencia. " +
                        "El archivo 'resultado.json' debe tener en la primera columna las filas del archivo 'datos' " +
                        "y en la segunda columna el 'id' correspondiente de 'lista_de_apus'. " +
                        "Si no hay coincidencia, usa '04' como ID. " +
                        "Guarda el archivo 'resultado.json' en la carpeta resources del proyecto.";
                
                String processResponse = chatWithDeepSeek(processRequest);
                System.out.println("Respuesta procesamiento: " + processResponse);

                System.out.println("=== VERIFICANDO RESULTADO ===");
                String resultadoPath = System.getProperty("user.dir") + File.separator + "src" + 
                                     File.separator + "main" + File.separator + "resources" + 
                                     File.separator + "resultado.json";
                System.out.println("Buscando archivo en: " + resultadoPath);
                
                if (new File(resultadoPath).exists()) {
                    System.out.println("Archivo resultado creado exitosamente");
                    mostrarMensajeTemporal("Información procesada", 3000);
                } else {
                    System.out.println("No se encontró el archivo resultado");
                    mostrarMensajeTemporal("Error al procesar archivos", 3000);
                }

            } catch (IOException e) {
                System.err.println("ERROR DURANTE PROCESAMIENTO:");
                e.printStackTrace();
                mostrarMensajeTemporal("Error: " + e.getMessage(), 3000);
            } finally {
                System.out.println("=== PROCESO FINALIZADO ===");
            }
        }).start();
    }

    private String chatWithDeepSeek(String message) throws IOException {
        System.out.println("Enviando mensaje a DeepSeek: " + message);
        
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "deepseek-chat");
        
        JsonArray messages = new JsonArray();
        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", message);
        messages.add(userMessage);
        
        requestBody.add("messages", messages);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(RequestBody.create(
                    gson.toJson(requestBody),
                    MediaType.parse("application/json")
                ))
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println("Respuesta recibida. Código: " + response.code());
            
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "sin detalles";
                System.err.println("Error en la API: " + response.code() + " - " + response.message());
                System.err.println("Detalles error: " + errorBody);
                throw new IOException("Error en la solicitud: " + response.code() + " - " + 
                                    response.message() + "\nDetalles: " + errorBody);
            }

            String responseBody = response.body().string();
            System.out.println("Respuesta completa: " + responseBody);
            
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
            JsonArray choices = jsonResponse.getAsJsonArray("choices");
            if (choices.size() > 0) {
                String content = choices.get(0).getAsJsonObject()
                        .getAsJsonObject("message")
                        .get("content").getAsString();
                System.out.println("Contenido respuesta: " + content);
                return content;
            }
            return "No recibí respuesta de la API";
        } catch (JsonSyntaxException e) {
            System.err.println("Error parseando JSON: " + e.getMessage());
            throw new IOException("Error al parsear la respuesta JSON: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado en chatWithDeepSeek: " + e.getMessage());
            throw e;
        }
    }

    private void mostrarMensajeTemporal(String mensaje, int duracionMs) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("Mostrando mensaje: " + mensaje);
            statusLabel.setText(mensaje);
            Timer timer = new Timer(duracionMs, e -> {
                statusLabel.setText("");
                System.out.println("Mensaje ocultado");
            });
            timer.setRepeats(false);
            timer.start();
        });
    }
}