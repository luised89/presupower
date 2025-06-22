
package funtion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import screen.area_de_trabajo;


public class busquedas {
    
 public static String usuarioEncontrado;   
 
 
public static void buscarUsuario(List<Map<String, Object>> listaUsuarios, 
                                   String nombreBuscado,
                                   String clavein,
                                   JFrame frameACerrar) {
    boolean encontrado = false;
        
    for (Map<String, Object> usuario : listaUsuarios) {
        String nombre = (String) usuario.get("nombre");
        String correo = (String) usuario.get("correo");
        
        if (nombre.equalsIgnoreCase(nombreBuscado) || correo.equalsIgnoreCase(nombreBuscado)) {
            usuarioEncontrado = nombre;
            System.out.println("\nUsuario encontrado:");
            System.out.println("ID: " + usuario.get("id"));
            System.out.println("Nombre: " + nombre);
            System.out.println("Correo: " + correo);
            String TmpPass = (String) usuario.get("pass");
            encontrado = true;
                    
            if (TmpPass.equals(clavein)){
            
                // Crear y mostrar nuevo frame
                area_de_trabajo nuevoFrame = new area_de_trabajo();
                nuevoFrame.setLocationRelativeTo(null);
                nuevoFrame.setVisible(true);
        
                // Cerrar el frame actual
                frameACerrar.dispose();
                return;  // Salir del método después de encontrar
            } else{
                System.out.println("no es la clave    "+clavein+"-----"+TmpPass); //#### AQÍ VA UN MENSAJE DE ERROR
                }
        }
    }
    
    if (!encontrado) {
        System.out.println("\nNo se encontró ningún usuario con nombre '" + nombreBuscado + "'");
    }
}
  

    public void consultamaterial(Connection conexion, String tabla) {
        List<Map<String, Object>> listaUsuarios = new ArrayList<>(); //######## Mapa de Usuarios
        
        try {
            PreparedStatement consulta = conexion.prepareStatement(
                "SELECT id, descripcion, unidad, valor FROM " + tabla);
            
            ResultSet resultado = consulta.executeQuery();
            


            while (resultado.next()) {
                
                Map<String, Object> usuario = new LinkedHashMap<>();
                usuario.put("id", resultado.getInt("id"));
                usuario.put("descripcion", resultado.getString("descripcion"));
                usuario.put("unidad", resultado.getString("unidad"));
                usuario.put("valor", resultado.getInt("valor"));
    
                listaUsuarios.add(usuario);
            }
            
        } catch (SQLException ex) {
            System.err.println("Error al recuperar usuarios: " + ex.getMessage());
        }
        
     //#### IMPRIMIR LISTA DE USUARIOS##########################
  
              System.out.println("\n=== LISTA DE USUARIOS ===");
              System.out.printf("%-5s %-40s %-8s %-15s%n", "ID", "DESCRIPCION", "UNIDAD", "VALOR");
              System.out.println("--------------------------------------------------------------------");

                for (Map<String, Object> usuario : listaUsuarios) {
                    System.out.printf("%-5d %-40s %-8s %-15s%n",
                    usuario.get("id"),
                    usuario.get("descripcion"),
                    usuario.get("unidad"),
                    usuario.get("valor")); 
                }
     
    }

}
