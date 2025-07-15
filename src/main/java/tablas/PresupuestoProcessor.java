package tablas;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.JOptionPane;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public class PresupuestoProcessor {

    public void procesarPresupuesto() {
        System.out.println("=== INICIANDO CREACIÓN DE EXCEL ===");

        try {
            // [PASO 1/5] Cargar y procesar JSON
            System.out.println("\n[PASO 1/5] Cargando JSON...");
            Path jsonPath = Paths.get("src/main/resources/resultado.json");
            String jsonContent = new String(Files.readAllBytes(jsonPath), StandardCharsets.UTF_8);
            jsonContent = jsonContent.replaceAll("^```json\\s*|^```\\s*|```\\s*$", "").trim();
            JSONArray jsonArray = new JSONArray(jsonContent);
            System.out.println("✅ JSON válido. Elementos: " + jsonArray.length());

            // [PASO 2/5] Cargar plantilla Excel
            System.out.println("\n[PASO 2/5] Cargando plantilla Excel...");
            try (InputStream excelFile = new FileInputStream("src/main/resources/presupuesto 0.xlsx");
                 Workbook workbook = new XSSFWorkbook(excelFile)) {

                // [PASO 3/5] Procesar datos
                System.out.println("\n[PASO 3/5] Procesando datos...");
                Sheet cantidadesSheet = workbook.getSheet("Cantidades");
                if (cantidadesSheet == null) throw new RuntimeException("❌ Hoja 'Cantidades' no encontrada");

                int rowIndex = 9;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    Row row = cantidadesSheet.getRow(rowIndex) != null ?
                            cantidadesSheet.getRow(rowIndex) : cantidadesSheet.createRow(rowIndex);

                    row.createCell(1).setCellValue(item.getString("code")); // Col B
                    row.createCell(3).setCellValue(item.getString("Descripcion")); // Col D
                    row.createCell(5).setCellValue(item.getDouble("Cantidad")); // Col F

                    System.out.printf("✔ Fila %d: %s | %s | %.2f%n",
                            rowIndex + 1, item.getString("code"), item.getString("Descripcion"), item.getDouble("Cantidad"));
                    rowIndex++;
                }

                // [PASO 4/5] Configurar fórmulas
                System.out.println("\n[PASO 4/5] Configurando fórmulas...");
                workbook.setForceFormulaRecalculation(true);

                // [PASO 5/5] Guardar archivo temporal
                System.out.println("\n[PASO 5/5] Guardando archivo temporal...");
                Path outputPath = Paths.get("src/main/resources/PresX.xlsx");
                Files.createDirectories(outputPath.getParent());
                try (FileOutputStream outputStream = new FileOutputStream(outputPath.toFile())) {
                    workbook.write(outputStream);
                    System.out.println("✅ Archivo temporal guardado en: " + outputPath.toAbsolutePath());
                }
            }

            // [PASO 6/6] Generar copia final con nombre aleatorio
            System.out.println("\n[PASO 6/6] Generando copia final...");

            // 6.1. Generar nombre aleatorio en hexadecimal
            System.out.println("[PASO 6.1] Generando nombre aleatorio...");
            Random random = new Random();
            String hexRandom = Integer.toHexString(random.nextInt(0xFFFFF + 1)).toUpperCase();
            String newFileName = "presupuesto" + hexRandom + ".xlsx";
            System.out.println("✅ Nombre generado: " + newFileName);

            // 6.2. Ruta en "Documentos"
            System.out.println("[PASO 6.2] Obteniendo ruta de destino...");
            String userHome = System.getProperty("user.home");
            Path destPath = Paths.get(userHome, "Documents", newFileName);
            System.out.println("✅ Ruta destino: " + destPath.toAbsolutePath());

            // 6.3. Copiar archivo temporal a destino
            System.out.println("[PASO 6.3] Copiando archivo...");
            Files.copy(Paths.get("src/main/resources/PresX.xlsx"), destPath);
            System.out.println("✅ Archivo copiado.");

            // 6.4. Mostrar mensaje de éxito
            String message = "Presupuesto generado en:\n" + destPath.toAbsolutePath();
            JOptionPane.showMessageDialog(null, message, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("✅ " + message);

        } catch (Exception e) {
            System.err.println("\n❌ ERROR: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}