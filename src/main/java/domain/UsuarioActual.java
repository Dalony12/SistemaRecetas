package domain;

/**
 * Clase que representa al usuario actualmente autenticado en el sistema.
 * Se maneja como Singleton para que solo exista una instancia a lo largo de la aplicación.
 */
public class UsuarioActual {

    // 🔹 Instancia única (Singleton)
    private static UsuarioActual instancia;

    // 🔹 Atributos del usuario
    private String id;
    private String nombre;
    private String rol; // Ejemplo: "Administrador", "Farmacéutico", "Paciente"

    // 🔹 Constructor privado para evitar instanciación externa
    private UsuarioActual() {
    }

    // 🔹 Obtener la instancia única
    public static UsuarioActual getInstancia() {
        if (instancia == null) {
            instancia = new UsuarioActual();
        }
        return instancia;
    }

    // 🔹 Métodos getter y setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    // 🔹 Método para limpiar los datos del usuario actual (cerrar sesión)
    public void cerrarSesion() {
        this.id = null;
        this.nombre = null;
        this.rol = null;
        instancia = null;
    }

    @Override
    public String toString() {
        return "UsuarioActual{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", rol='" + rol + '\'' +
                '}';
    }
}