package tablas;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PresupuestoProcessor {
    
    public void procesarPresupuesto() {
        System.out.println("=== INICIANDO CREACION DE EXCEL ===");
        
        try {
            // 1. Cargar y procesar JSON
            System.out.println("\n[PASO 1/5] Cargando JSON...");
            Path jsonPath = Paths.get("src/main/resources/resultado.json");
            String jsonContent = new String(Files.readAllBytes(jsonPath), StandardCharsets.UTF_8);
            jsonContent = jsonContent.replaceAll("^```json\\s*|^```\\s*|```\\s*$", "").trim();
            JSONArray jsonArray = new JSONArray(jsonContent);
            System.out.println("✅ JSON válido. Elementos: " + jsonArray.length());

            // 2. Cargar Excel
            System.out.println("\n[PASO 2/5] Cargando plantilla Excel...");
            try (InputStream excelFile = new FileInputStream("src/main/resources/presupuesto 0.xlsx");
                 Workbook workbook = new XSSFWorkbook(excelFile)) {
                
                // 3. Procesar datos
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

                // 4. Configuración para fórmulas (evitando evaluación)
                System.out.println("\n[PASO 4/5] Configurando fórmulas...");
                workbook.setForceFormulaRecalculation(true);
                
                // 5. Guardar archivo
                System.out.println("\n[PASO 5/5] Guardando archivo...");
                Path outputPath = Paths.get("src/main/resources/PresX.xlsx");
                Files.createDirectories(outputPath.getParent());
                
                try (FileOutputStream outputStream = new FileOutputStream(outputPath.toFile())) {
                    workbook.write(outputStream);
                    System.out.println("✅ Archivo guardado en: " + outputPath.toAbsolutePath());
                    System.out.println("⚠ Nota: Las fórmulas XLOOKUP/CONCAT se actualizarán al abrir en Excel");
                }
            }
            
        } catch (Exception e) {
            System.err.println("\n❌ ERROR: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al procesar archivos", e);
        }
    }
}