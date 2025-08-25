package Backend;
import Gestores.GestorRecetas;

public class Medico extends Usuario  {
    private String clave;
    private String especialidad;
    private GestorRecetas gestorRecetas;

    public Medico(String nombre, int id, String password, String especialidad, GestorRecetas gestorRecetas) {
        super(id, nombre, password);
        this.clave = clave;
        this.especialidad = especialidad;
        this.gestorRecetas = gestorRecetas;
    }

    public GestorRecetas getGestorRecetas() {
        return gestorRecetas;
    }

    public void setGestorRecetas(GestorRecetas gestorRecetas) {
        this.gestorRecetas = gestorRecetas;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    @Override
    public void mostrarInfo() {
        System.out.println("Medico [ID: " + getId() +
                ", Nombre: " + getNombre() +
                ", Especialidad: " + getEspecialidad() + "]");
    }
}
