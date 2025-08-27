package Backend;
import Gestores.GestorRecetas;

public class Medico extends Usuario  {
    private String especialidad;

    public Medico(String nombre, String id, String password, String especialidad) {
        super(id, nombre, password);
        this.especialidad = especialidad;
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
