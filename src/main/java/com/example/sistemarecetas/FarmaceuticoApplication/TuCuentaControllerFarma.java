package com.example.sistemarecetas.FarmaceuticoApplication;

import Model.Farmaceutico;
import Gestores.GestorFarmaceuticos;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class TuCuentaControllerFarma {
    @FXML private TextField txtIDCambiarContraseña;
    @FXML private TextField txtNuevaContraseña;
    @FXML private TextField txtVerificarContraseña;
    @FXML private Button btnEnviarContraseña;
    @FXML private Button btnCancelarContraseña;

    private GestorFarmaceuticos gestorFarmaceutico = GestorFarmaceuticos.getInstancia();


    @FXML
    private void EnviarContraseñaNueva(){
        String id = txtIDCambiarContraseña.getText().trim();
        String nueva = txtNuevaContraseña.getText().trim();
        String verificar = txtVerificarContraseña.getText().trim();

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
        txtNuevaContraseña.clear();
        txtVerificarContraseña.clear();
    }
}

