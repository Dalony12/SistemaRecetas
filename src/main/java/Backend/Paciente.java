package Backend;
import java.time.LocalDate;

public class Paciente extends Usuario {
    private int telefono;
    private LocalDate fechaNacimiento;

    public Paciente(String nombre, int id, int telefono, LocalDate fechaNacimiento) {
        super(nombre, id);
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    @Override
    public void mostrarInfo() {
        System.out.println("Paciente [ID: " + getId() +
                ", Nombre: " + getNombre() +
                ", Telefono: " + getTelefono() +
                ", Fecha Nacimiento: " + getFechaNacimiento() + "]");
    }
}
