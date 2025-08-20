package Backend;

public class Medico extends Persona  {
    private String clave;
    private String especialidad;

    public Medico(String nombre, int id, String clave, String especialidad) {
        super(nombre, id);
        this.clave = clave;
        this.especialidad = especialidad;
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
                ", Especialidad: " + getEspecialidad() +
                ", Clave: " + getClave() + "]");
    }
}
