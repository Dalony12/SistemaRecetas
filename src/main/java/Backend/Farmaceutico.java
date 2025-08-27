package Backend;

public class Farmaceutico extends Usuario {
    private String clave;

    public Farmaceutico(String nombre, String id, String password, String clave) {
        super(id, nombre, password);
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
                ", Nombre: " + getNombre() + "]");
    }
}
