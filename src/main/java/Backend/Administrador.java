package Backend;

import Gestores.GestorMedicamentos;
import Gestores.GestorFarmaceuticos;
import Gestores.GestorPacientes;
import Gestores.GestorMedicos;

public class Administrador extends Usuario{


    public Administrador(String nombre, String id, String password) {
        super(id, nombre, password);

    }

    @Override
    public void mostrarInfo() {
        System.out.println("Administrador [ID: " + getId() + ", Nombre: " + getNombre() + "]");
    }
}
