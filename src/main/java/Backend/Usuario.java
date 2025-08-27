package Backend;

public abstract class Usuario extends Persona {
    private String password;

    public Usuario(String id, String nombre, String password) {
        super(nombre, id);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public abstract void mostrarInfo();
}
