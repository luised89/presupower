
package dpsk;

/**
 *
 * @author Luis
 */
import javax.swing.*;
import okhttp3.*;
import com.google.gson.*;
import java.io.IOException;

public class DeepSeekChatTest {
    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";
    private final String apiKey;
    private final OkHttpClient client;
    private final Gson gson;

    public DeepSeekChatTest(String apiKey) {
        this.apiKey = apiKey;
        this.client = new OkHttpClient();
        this.gson = new Gson();
    }

    public String chatWithDeepSeek(String message) throws IOException {
        // Creamos el cuerpo de la solicitud en formato JSON
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "deepseek-chat");
        
        JsonArray messages = new JsonArray();
        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", message);
        messages.add(userMessage);
        
        requestBody.add("messages", messages);

        // Construimos la solicitud HTTP
        Request request = new Request.Builder()
                .url(API_URL)
                .post(RequestBody.create(
                    gson.toJson(requestBody),
                    MediaType.parse("application/json")
                ))
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .build();

        // Ejecutamos la solicitud
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error en la solicitud: " + response.code() + " - " + response.message());
            }

            // Parseamos la respuesta JSON
            JsonObject jsonResponse = gson.fromJson(response.body().charStream(), JsonObject.class);
            JsonArray choices = jsonResponse.getAsJsonArray("choices");
            if (choices.size() > 0) {
                return choices.get(0).getAsJsonObject()
                        .getAsJsonObject("message")
                        .get("content").getAsString();
            }
            return "No recibí respuesta de la API";
        }
    }

    public static void main(String[] args) {
        // Pedimos la API key al usuario
        String apiKey = JOptionPane.showInputDialog(
            null, 
            "Ingresa tu API Key de DeepSeek:", 
            "Configuración inicial", 
            JOptionPane.QUESTION_MESSAGE
        );

        if (apiKey == null || apiKey.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                null, 
                "Debes proporcionar una API Key válida", 
                "Error", 
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        DeepSeekChatTest chatTest = new DeepSeekChatTest(apiKey);

        // Bucle de chat
        while (true) {
            String userMessage = JOptionPane.showInputDialog(
                null, 
                "Escribe tu mensaje para DeepSeek (o cancela para salir):", 
                "Chat con DeepSeek", 
                JOptionPane.QUESTION_MESSAGE
            );

            if (userMessage == null) {
                break; // El usuario canceló
            }

            try {
                String response = chatTest.chatWithDeepSeek(userMessage);
                JOptionPane.showMessageDialog(
                    null, 
                    "<html><body><p style='width: 300px;'>" + response + "</p></body></html>", 
                    "Respuesta de DeepSeek", 
                    JOptionPane.INFORMATION_MESSAGE
                );
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                    null, 
                    "Error al comunicarse con la API: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE
                );
                break;
            }
        }

        JOptionPane.showMessageDialog(
            null, 
            "Gracias por usar el chat con DeepSeek", 
            "Chat finalizado", 
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}