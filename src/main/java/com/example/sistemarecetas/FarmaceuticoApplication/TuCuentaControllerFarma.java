package com.example.sistemarecetas.FarmaceuticoApplication;

import Model.Farmaceutico;
import Gestores.GestorFarmaceuticos;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class TuCuentaControllerFarma {
    @FXML private TextField txtIDCambiarContraseña;
    @FXML private PasswordField pwfNuevaContraseña;
    @FXML private PasswordField pwfVerificarContraseña;

    private GestorFarmaceuticos gestorFarmaceutico = GestorFarmaceuticos.getInstancia();


    @FXML
    private void EnviarContraseñaNueva(){
        String id = txtIDCambiarContraseña.getText().trim();
        String nueva = pwfNuevaContraseña.getText().trim();
        String verificar = pwfVerificarContraseña.getText().trim();

        // Validación básica de campos vacíos
        if (id.isEmpty() || nueva.isEmpty() || verificar.isEmpty()) {
            mostrarAlerta("Campos vacíos", "Por favor, completar todos los campos.");
            return;
        }

        Farmaceutico farma = gestorFarmaceutico.buscarPorid(id);

        if (farma == null) {
            mostrarAlerta("ID inválido", "No se encontró ningún médico con ese ID.");
            return;
        }


        // Verificar que las contraseñas coincidan
        if (!nueva.equals(verificar)) {
            mostrarAlerta("Contraseñas no coinciden", "La nueva contraseña y la verificación no son iguales.");
            return;
        }

        // Actualizar la contraseña
        farma.setPassword(nueva);
        System.out.println("Nueva contraseña guardada: " + farma.getPassword());
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
        txtIDCambiarContraseña.clear();
        pwfNuevaContraseña.clear();
        pwfVerificarContraseña.clear();
    }
}

