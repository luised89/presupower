package funtion;

import javax.swing.JOptionPane;
import component.material;

/**
 *
 * @author Luis
 */
public class manager {
    
    private controller based;

    
    //######## EJECUTAR CONEXION######
    public manager() {
        this.based = new controller();
        if (!"conexion".equals(based.conection())) {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos");
            System.exit(1);
        }
    }
    
    
    
    
    
    // #########Registrar nuevo Material #########
    
    public boolean registrarMaterial(material materialn) {
        try {
            String sql = "INSERT INTO materiales (descripcion, unidad, valor) "
                    + "VALUES ('" + materialn.getDescripcion() + "', '" + materialn.getUnidad() + "', '"
                    + materialn.getVrunit() + "')";

            // Necesitarías agregar un método executeUpdate en Accesbd
            return based.executeUpdate(sql) > 0;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al registrar paciente: " + e.getMessage());
            return false;
        }
    }
    
    
}
