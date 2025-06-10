
package funtion;

import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import screen.area_de_trabajo;
import screen.inicio;


public class busquedas {
    
    
public static void buscarUsuario(List<Map<String, Object>> listaUsuarios, 
                                   String nombreBuscado,
                                   String clavein,
                                   JFrame frameACerrar) {
    boolean encontrado = false;
        
    for (Map<String, Object> usuario : listaUsuarios) {
        String nombre = (String) usuario.get("nombre");
        String correo = (String) usuario.get("correo");
        
        if (nombre.equalsIgnoreCase(nombreBuscado) || correo.equalsIgnoreCase(nombreBuscado)) {
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
    
}
