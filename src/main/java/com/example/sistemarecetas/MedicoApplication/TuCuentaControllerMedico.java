package com.example.sistemarecetas.MedicoApplication;

import Model.Medico;
import Gestores.GestorMedicos;
import domain.UsuarioActual;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;

public class TuCuentaControllerMedico {

    @FXML private PasswordField pwfNuevaContraseña;
    @FXML private PasswordField pwfVerificarContraseña;

    private GestorMedicos gestorMedico = GestorMedicos.getInstancia();

    @FXML
    private void EnviarContraseñaNueva() {
        String nueva = pwfNuevaContraseña.getText().trim();
        String verificar = pwfVerificarContraseña.getText().trim();

        // Validación básica de campos vacíos
        if (nueva.isEmpty() || verificar.isEmpty()) {
            mostrarAlerta("Campos vacíos", "Por favor, completar todos los campos.");
            return;
        }

        // Obtener ID del usuario activo
        String idActual = UsuarioActual.getInstancia().getId();

        Medico medico = gestorMedico.buscarPorId(idActual);

        if (medico == null) {
            mostrarAlerta("Error", "No se encontró la cuenta del usuario activo.");
            return;
        }

        // Verificar que las contraseñas coincidan
        if (!nueva.equals(verificar)) {
            mostrarAlerta("Contraseñas no coinciden", "La nueva contraseña y la verificación no son iguales.");
            return;
        }

        // Actualizar la contraseña
        medico.setPassword(nueva);
        mostrarAlerta("Éxito", "La contraseña ha sido actualizada correctamente.");
        limpiarCampos();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void limpiarCampos() {
        pwfNuevaContraseña.clear();
        pwfVerificarContraseña.clear();
    }
}