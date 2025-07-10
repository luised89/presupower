package funtion;

//Es relevante importar el driver que permite lograr la conexión
//con la BD
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
//import java.util.*;

public class controller {

    //Esta primera linea, permite instanciar un objeto para
    // la conexión entre el IDE y la BD
private static Connection con;



    private static final String driver="com.mysql.jdbc.Driver";

    private static final String user="root";

    private static final String pass="";
    
    //private static final String url="jdbc:mysql://192.168.1.89:3306/test";
    private static final String url="jdbc:mysql://localhost:3306/user-presupower";
    // en su caso deben poner en URL, localhost o la ip
    //del servidor 

    //####### GENERAL CONECTION##################
    
    
        public String conection() {
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, user, pass);
            if (con != null) {
                System.out.println("Conexion establecida");
                return "conexion";
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error de conexion" + e);
            return "fallo";
        }
        return null;
    }
    
        

    public void conectar(String nombre, String correo, String pss) {
      
        con=null;
        try{
            Class.forName(driver);
            // Nos conectamos al gestor de bd
            con= DriverManager.getConnection(url, user, pass);
            // Si la conexion fue exitosa mostramos un mensaje de conexion exitosa
            if (con!=null){
                System.out.println("Conexion establecida");
            }
        }
        // Si la conexion NO fue exitosa mostramos un mensaje de error
        catch (ClassNotFoundException | SQLException e){
            System.out.println("Error de conexion" + e);
        }   
        
        // Se procede a realizar las operaciones correspondientes para la insercción de datos
        
        cuentasave insertar = new cuentasave();
        insertar.guardar(con, nombre, correo, pss);
        
    }
    
    
    public void consultar(String usuarioin, String passin, JFrame frameParaCerrar) {
      
        con=null;
        try{
            Class.forName(driver);
            // Nos conectamos al gestor de bd
            con= DriverManager.getConnection(url, user, pass);
            // Si la conexion fue exitosa mostramos un mensaje de conexion exitosa
            if (con!=null){
                System.out.println("Conexion establecida");
            }
        }
        // Si la conexion NO fue exitosa mostramos un mensaje de error
        catch (ClassNotFoundException | SQLException e){
            System.out.println("Error de conexion" + e);
        }   
                // Consulta de Usuarrios
        
        cuentasave consusr = new cuentasave();
        consusr.consultauser(con, usuarioin, passin, frameParaCerrar);    
    } 
    
    public void consultageneral(String tablasearch) throws SQLException {
      
        con=null;
        try{
            Class.forName(driver);
            // Nos conectamos al gestor de bd
            con= DriverManager.getConnection(url, user, pass);
            // Si la conexion fue exitosa mostramos un mensaje de conexion exitosa
            if (con!=null){
                System.out.println("Conexion establecida");
            }
        }
        // Si la conexion NO fue exitosa mostramos un mensaje de error
        catch (ClassNotFoundException | SQLException e){
            System.out.println("Error de conexion" + e);
        }   
     
        busquedas nwsearch = new busquedas();
        nwsearch.consultamaterial(con, tablasearch);
        
    }
    
    
    public void descargarApusComoJson() throws SQLException {
    Connection con = null;
    try {
        Class.forName(driver);
        con = DriverManager.getConnection(url, user, pass);
        
        if (con != null) {
            System.out.println("Conexión establecida");
            
            // 1. Obtener datos de la tabla APUS
            String query = "SELECT * FROM apus";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            // 2. Convertir ResultSet a JSON
            JsonArray jsonArray = new JsonArray();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            while (rs.next()) {
                JsonObject jsonObject = new JsonObject();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(i);
                    if (value != null) {
                        jsonObject.addProperty(columnName, value.toString());
                    } else {
                        jsonObject.add(columnName, JsonNull.INSTANCE);
                    }
                }
                jsonArray.add(jsonObject);
            }
            
            // 3. Definir ruta del archivo
            String resourcesPath = System.getProperty("user.dir") + File.separator + "src" + 
                                 File.separator + "main" + File.separator + "resources" + 
                                 File.separator + "lista_de_apus.json";
            
            // 4. Eliminar archivo existente si existe
            File jsonFile = new File(resourcesPath);
            if (jsonFile.exists()) {
                if (!jsonFile.delete()) {
                    throw new IOException("No se pudo eliminar el archivo existente");
                }
            }
            
            // 5. Guardar el nuevo archivo
            try (FileWriter fileWriter = new FileWriter(jsonFile)) {
                new GsonBuilder()
                    .setPrettyPrinting()
                    .create()
                    .toJson(jsonArray, fileWriter);
            }
            
            // 6. Mostrar mensaje temporal
            JDialog messageDialog = new JDialog();
            messageDialog.setTitle("Información");
            messageDialog.setSize(300, 100);
            messageDialog.setLocationRelativeTo(null);
            messageDialog.setUndecorated(true);
            
            JLabel messageLabel = new JLabel("Datos descargados", SwingConstants.CENTER);
            messageDialog.add(messageLabel);
            messageDialog.setVisible(true);
            
            Timer timer = new Timer(3500, e -> messageDialog.dispose());
            timer.setRepeats(false);
            timer.start();
        }
    } catch (ClassNotFoundException | SQLException | IOException e) {
        System.out.println("Error: " + e);
        JOptionPane.showMessageDialog(null, 
            "Error al descargar datos: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    } finally {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar conexión: " + e);
            }
        }
    }
}
    
    
    
    /**########################
     * Ejecuta una consulta SQL de actualización (INSERT, UPDATE, DELETE)
     * @param sql La consulta SQL a ejecutar
     * @return Cantidad de filas afectadas
     * @throws SQLException Si ocurre un error en la base de datos
     */
    public int executeUpdate(String sql) throws SQLException {
        try (Statement stmt = con.createStatement()) {
            return stmt.executeUpdate(sql);
        }
    }
    
    
    
    }
