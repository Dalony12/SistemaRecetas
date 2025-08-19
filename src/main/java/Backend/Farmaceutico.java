package Backend;

public class Farmaceutico extends Usuario {
    private String clave;

    public Farmaceutico(String nombre, int id, String clave) {
        super(nombre, id);
        this.clave = clave;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    @Override
    public void mostrarInfo() {
        System.out.println("Medico [ID: " + getId() +
                ", Nombre: " + getNombre() +
                ", Clave: " + getClave() + "]");
    }
}
