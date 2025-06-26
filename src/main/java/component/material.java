
package component;

/**
 *
 * @author Luis
 */
public class material extends abstracto {
      
    
    // Constructor único y funcional
    public material(String descripcion, String unidad, int vrunit) {
        super(descripcion, unidad, vrunit);
    }
    

    
    @Override
    public String toString() {
        return super.toString() + " [Material]";
    }

    
}
