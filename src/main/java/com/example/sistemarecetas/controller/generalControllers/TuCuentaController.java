package com.example.sistemarecetas.controller.generalControllers;

import com.example.sistemarecetas.Model.Medico;
import com.example.sistemarecetas.Model.Farmaceutico;
import com.example.sistemarecetas.Model.Usuario;
import com.example.sistemarecetas.domain.UsuarioActual;
import com.example.sistemarecetas.logica.MedicoLogica;
import com.example.sistemarecetas.logica.FarmaceuticoLogica;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;

public class TuCuentaController {

    private static TuCuentaController instance;
    public TuCuentaController() { instance = this; }
    public static TuCuentaController getInstance() { return instance; }

    @FXML private PasswordField txtNuevaContrasena;
    @FXML private PasswordField txtVerificarContrasena;

    private Usuario usuarioActivo;
    private MedicoLogica medicosLogica;
    private FarmaceuticoLogica farmaceutasLogica;

    @FXML
    private void initialize() {
        usuarioActivo = UsuarioActual.getInstancia().getUsuario();

        // Inicializar lógica usando DB directamente
        medicosLogica = new MedicoLogica();       // Constructor DB
        farmaceutasLogica = new FarmaceuticoLogica(); // Constructor DB
    }

    public void cargarDatosCuenta() {
        limpiarCampos();
    }

    @FXML
    private void EnviarContrasenaNueva() {
        String nueva = txtNuevaContrasena.getText().trim();
        String verificar = txtVerificarContrasena.getText().trim();

        if (nueva.isEmpty() || verificar.isEmpty()) {
            mostrarAlerta("Campos vacíos", "Por favor, completar todos los campos.");
            return;
        }

        if (!nueva.equals(verificar)) {
            mostrarAlerta("Contraseñas no coinciden", "La nueva contraseña y la verificación no son iguales.");
            return;
        }

        boolean actualizado = false;

        try {
            if (usuarioActivo instanceof Medico medico) {
                medico.setPassword(nueva);
                medicosLogica.update(medico);  // Ahora va a DB
                actualizado = true;
            } else if (usuarioActivo instanceof Farmaceutico farmaceutico) {
                farmaceutico.setPassword(nueva);
                farmaceutasLogica.update(farmaceutico);  // Ahora va a DB
                actualizado = true;
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo actualizar la contraseña: " + e.getMessage());
            return;
        }

        if (!actualizado) {
            mostrarAlerta("Error", "No se encontró la cuenta del usuario activo.");
            return;
        }

        usuarioActivo.setPassword(nueva);
        mostrarAlerta("Éxito", "La contraseña ha sido actualizada correctamente.");
        limpiarCampos();
    }

    @FXML
    private void limpiarCampos() {
        txtNuevaContrasena.clear();
        txtVerificarContrasena.clear();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}