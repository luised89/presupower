package component;

/**
 *
 * @author Luis
 */

public abstract class abstracto {
    protected String descripcion;
    protected String unidad;
    protected int vrunit;
    
    
    public abstracto(String descripcion, String unidad, int vrunit) {
        this.descripcion = descripcion;
        this.unidad = unidad;
        this.vrunit = vrunit;
        
    }
    
    // Getters y Setters
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }
    
    public int getVrunit() { return vrunit; }
    public void setVrunit(int vrunit) { this.vrunit = vrunit; }
    
    @Override
    public String toString() {
        return descripcion + " "+"(" + unidad + ") " + vrunit;
    }
}