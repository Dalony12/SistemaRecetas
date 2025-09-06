package domain;

import Model.Usuario;

/**
 * Clase que representa al usuario actualmente autenticado en el sistema.
 * Se maneja como Singleton para que solo exista una instancia a lo largo de la aplicación.
 */
public class UsuarioActual {

    // 🔹 Instancia única (Singleton)
    private static UsuarioActual instancia;

    // 🔹 Objeto usuario del modelo
    private Usuario usuario;
    private String rol; // Ejemplo: "Administrador", "Médico", "Farmacéutico"

    // 🔹 Constructor privado para evitar instanciación externa
    private UsuarioActual() {}

    // 🔹 Obtener la instancia única
    public static UsuarioActual getInstancia() {
        if (instancia == null) {
            instancia = new UsuarioActual();
        }
        return instancia;
    }

    // 🔹 Métodos para manejar el usuario actual
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Asigna el usuario actualmente autenticado junto con su rol.
     */
    public void setUsuario(Usuario usuario, String rol) {
        this.usuario = usuario;
        this.rol = rol;
    }

    public String getId() {
        return usuario != null ? usuario.getId() : null;
    }

    public String getNombre() {
        return usuario != null ? usuario.getNombre() : null;
    }

    public String getPassword() {
        return usuario != null ? usuario.getPassword() : null;
    }

    public String getRol() {
        return rol;
    }

    public void cerrarSesion() {
        this.usuario = null;
        this.rol = null;
        instancia = null;
    }

    @Override
    public String toString() {
        return "UsuarioActual{" +
                "usuario=" + (usuario != null ? usuario.toString() : "null") +
                ", rol='" + rol + '\'' +
                '}';
    }
}
