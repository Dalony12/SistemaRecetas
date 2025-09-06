package com.example.sistemarecetas.generalControllers;

import Model.Medico;
import Model.Farmaceutico;
import Model.Usuario;
import Gestores.GestorMedicos;
import Gestores.GestorFarmaceuticos;
import domain.UsuarioActual;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;

public class TuCuentaController {

    @FXML private PasswordField txtNuevaContrasena;
    @FXML private PasswordField txtVerificarContrasena;

    private Usuario usuarioActivo;
    private Object gestor; // Guardará GestorMedicos o GestorFarmaceuticos según el usuario

    @FXML
    private void initialize() {
        usuarioActivo = UsuarioActual.getInstancia().getUsuario();

        if (usuarioActivo instanceof Medico) {
            gestor = GestorMedicos.getInstancia();
        } else if (usuarioActivo instanceof Farmaceutico) {
            gestor = GestorFarmaceuticos.getInstancia();
        }
    }

    @FXML
    private void EnviarContrasenaNueva() {
        String nueva = txtNuevaContrasena.getText().trim();
        String verificar = txtVerificarContrasena.getText().trim();

        // Validación de campos vacíos
        if (nueva.isEmpty() || verificar.isEmpty()) {
            mostrarAlerta("Campos vacíos", "Por favor, completar todos los campos.");
            return;
        }

        // Validación de coincidencia
        if (!nueva.equals(verificar)) {
            mostrarAlerta("Contraseñas no coinciden", "La nueva contraseña y la verificación no son iguales.");
            return;
        }

        boolean actualizado = false;

        // Actualizar según tipo de usuario
        if (usuarioActivo instanceof Medico) {
            actualizado = ((GestorMedicos) gestor).actualizarPassword(usuarioActivo.getId(), nueva);
        } else if (usuarioActivo instanceof Farmaceutico) {
            actualizado = ((GestorFarmaceuticos) gestor).actualizarPassword(usuarioActivo.getId(), nueva);
        }

        if (!actualizado) {
            mostrarAlerta("Error", "No se encontró la cuenta del usuario activo.");
            return;
        }

        // Actualizar también la contraseña en UsuarioActual
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
