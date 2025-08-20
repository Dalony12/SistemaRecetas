package Backend;

public class Medicamento {
    private int codigo;
    private String nombre;
    private String descripcion;

    public Medicamento(int codigo, String nombre, String presentacion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = presentacion;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String toString() {
        return "Medicamento [Codigo: " + getCodigo() +
                ", Nombre: " + getNombre() +
                ", Presentación: " + getDescripcion() + "]";
    }
}


