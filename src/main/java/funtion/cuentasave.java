package funtion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Lenovo
 */
public class cuentasave {
    
    private final String tabla = "user";

    public void guardar(Connection conexion, String nombre,String correo, String pss){
        try {
            PreparedStatement consulta;

            consulta = conexion.prepareStatement("INSERT INTO " + this.tabla + "(nombre, correo, pass) VALUES(?, ?, ?)");
            consulta.setString(1, nombre);
            consulta.setString(2, correo);
            consulta.setString(3, pss);
            //asas
            
            consulta.executeUpdate();
            System.out.println("Se efectuo la operacion de escritura");

        } catch (SQLException ex) {
            
            
        }

    }
    
    public  void recuperarPorId(Connection conexion, int id_tarea) {
        
        try {
            PreparedStatement consulta = conexion.prepareStatement("SELECT titulo, descripcion, nivel_de_prioridad FROM " + this.tabla + " WHERE id_tarea = ?");
            consulta.setInt(1, id_tarea);
            ResultSet resultado = consulta.executeQuery();
            while (resultado.next()) {
              
                System.out.println(id_tarea);
                System.out.println(resultado.getString("titulo"));
                System.out.println(resultado.getInt("nivel_de_prioridad"));
            }
        } catch (SQLException ex) {
            
        }
        
    }
    
}
