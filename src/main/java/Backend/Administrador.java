package Backend;

import Gestores.GestorMedicamentos;

public class Administrador extends Usuario{
    private GestorMedicamentos gestor;

    public Administrador(String nombre, int id) {
        super(nombre, id);
        this.gestor = new GestorMedicamentos();
    }

    public GestorMedicamentos getGestor() {
        return gestor;
    }

    public void setGestor(GestorMedicamentos gestor) {
        this.gestor = gestor;
    }

    @Override
    public void mostrarInfo() {
        System.out.println("Administrador [ID: " + getId() + ", Nombre: " + getNombre() + "]");
    }
}
