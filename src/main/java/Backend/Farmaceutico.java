package Backend;

public class Farmaceutico extends Usuario {

    public Farmaceutico(String nombre, String id, String password) {
        super(id, nombre, password);
    }

    @Override
    public void mostrarInfo() {
        System.out.println("Medico [ID: " + getId() +
                ", Nombre: " + getNombre() + "]");
    }
}
