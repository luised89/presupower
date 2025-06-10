package funtion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;

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
    
    public void consultauser(Connection conexion, String usuarioin, String passin, JFrame frameParaCerrar) {
        List<Map<String, Object>> listaUsuarios = new ArrayList<>(); //######## Mapa de Usuarios
        
        try {
            PreparedStatement consulta = conexion.prepareStatement(
                "SELECT id, nombre, correo, pass FROM " + this.tabla);
            
            ResultSet resultado = consulta.executeQuery();
            


            while (resultado.next()) {
                
                Map<String, Object> usuario = new LinkedHashMap<>();
                usuario.put("id", resultado.getInt("id"));
                usuario.put("nombre", resultado.getString("nombre"));
                usuario.put("correo", resultado.getString("correo"));
                usuario.put("pass", resultado.getString("pass"));
    
                listaUsuarios.add(usuario);
            }
            
        } catch (SQLException ex) {
            System.err.println("Error al recuperar usuarios: " + ex.getMessage());
        }
        
  //#### IMPRIMIR LISTA DE USUARIOS##########################
  
//  System.out.println("\n=== LISTA DE USUARIOS ===");
//  System.out.printf("%-5s %-20s %-30s %-15s%n", "ID", "NOMBRE", "CORREO", "CONTRASEÑA");
//  System.out.println("--------------------------------------------------------------------");

//    for (Map<String, Object> usuario : listaUsuarios) {
//        System.out.printf("%-5d %-20s %-30s %-15s%n",
//        usuario.get("id"),
//        usuario.get("nombre"),
//        usuario.get("correo"),
//        usuario.get("pass"));
//}



//#######BUSQUEDA DE USUARIO##############

busquedas.buscarUsuario(listaUsuarios, usuarioin, passin, frameParaCerrar);

    }
 
}
