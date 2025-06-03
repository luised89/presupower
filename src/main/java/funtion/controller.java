package funtion;

//Es relevante importar el driver que permite lograr la conexión
//con la BD
import java.sql.*;
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
        //insertar.guardar(con,"test","test","test");
        
       // Inserccion insertar = new Inserccion();
       // insertar.guardar(con);
        
        //insertar.recuperarPorId(con, 1);
        
        /* 
    
        Debido a las posibles excepciones el código de ingreso se 
        debe poner en un TRY, de esta forma con el objeto 
        Connection, logramos establecer la conexión entre la 
        BD creada y NetBeans

    */    

    }
}
    

