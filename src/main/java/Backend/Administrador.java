package Backend;

import Gestores.GestorMedicamentos;
import Gestores.GestorFarmaceuticos;
import Gestores.GestorPacientes;
import Gestores.GestorMedicos;

public class Administrador extends Usuario{
    private GestorMedicamentos gestormedicamentos;
    private GestorPacientes gestorPacientes;
    private GestorMedicos gestorMedicos;
    private GestorFarmaceuticos gestorFarmaceuticos;

    public Administrador(String nombre, int id, String password, GestorMedicamentos gestormedicamentos, GestorPacientes gestorPacientes, GestorMedicos gestorMedicos, GestorFarmaceuticos gestorFarmaceuticos) {
        super(id, nombre, password);
        this.gestormedicamentos = gestormedicamentos;
        this.gestorPacientes = gestorPacientes;
        this.gestorMedicos = gestorMedicos;
        this.gestorFarmaceuticos = gestorFarmaceuticos;
    }

    public GestorMedicamentos getGestormedicamentos() {
        return gestormedicamentos;
    }

    public void setGestormedicamentos(GestorMedicamentos gestormedicamentos) {
        this.gestormedicamentos = gestormedicamentos;
    }

    public GestorPacientes getGestorPacientes() {
        return gestorPacientes;
    }

    public void setGestorPacientes(GestorPacientes gestorPacientes) {
        this.gestorPacientes = gestorPacientes;
    }

    public GestorMedicos getGestorMedicos() {
        return gestorMedicos;
    }

    public void setGestorMedicos(GestorMedicos gestorMedicos) {
        this.gestorMedicos = gestorMedicos;
    }

    public GestorFarmaceuticos getGestorFarmaceuticos() {
        return gestorFarmaceuticos;
    }

    public void setGestorFarmaceuticos(GestorFarmaceuticos gestorFarmaceuticos) {
        this.gestorFarmaceuticos = gestorFarmaceuticos;
    }

    @Override
    public void mostrarInfo() {
        System.out.println("Administrador [ID: " + getId() + ", Nombre: " + getNombre() + "]");
    }
}
