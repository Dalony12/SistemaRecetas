package com.example.sistemarecetas;

import Model.Farmaceutico;
import Model.Medico;
import Gestores.GestorFarmaceuticos;
import Gestores.GestorMedicos;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField txtId;
    @FXML private PasswordField txtContrasenaLogin;
    @FXML private Button btnIniciarSesion;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private RadioButton rbtAyuda;


    @FXML private Label lblAyudaId;
    @FXML private Label lblAyudaPassword;
    @FXML private Label lblAyudaSoporte;
    @FXML private Label lblMensajeCampos;

    @FXML
    private void initialize() {
        // Ocultar inicialmente etiquetas de ayuda y mensaje
        lblAyudaId.setVisible(false);
        lblAyudaPassword.setVisible(false);
        lblAyudaSoporte.setVisible(false);

        Tooltip tooltip = new Tooltip("Debe llenar los campos antes de iniciar sesión.");
        Tooltip.install(btnIniciarSesion, tooltip);

        // Inicialmente deshabilitar botón
        btnIniciarSesion.setDisable(true);
        lblMensajeCampos.setVisible(true);


        // Listener para habilitar/deshabilitar botón según campos llenos
        ChangeListener<String> textListener = (obs, oldText, newText) -> {
            boolean habilitar = !txtId.getText().trim().isEmpty() && !txtContrasenaLogin.getText().trim().isEmpty();
            btnIniciarSesion.setDisable(!habilitar);
            lblMensajeCampos.setVisible(false); // Ocultar mensaje mientras escribe
        };

        txtId.textProperty().addListener(textListener);
        txtContrasenaLogin.textProperty().addListener(textListener);
    }

    @FXML
    private void clickIniciarSesion(ActionEvent event) {
        // Bloquear inputs y mostrar animación
        btnIniciarSesion.setVisible(false);
        progressIndicator.setVisible(true);
        txtId.setDisable(true);
        txtContrasenaLogin.setDisable(true);
        rbtAyuda.setDisable(true);

        String id = txtId.getText().trim();
        String password = txtContrasenaLogin.getText().trim();

        new Thread(() -> {
            try { Thread.sleep(1500); } catch (InterruptedException e) { e.printStackTrace(); }

            Platform.runLater(() -> {
                // Restaurar estado UI
                btnIniciarSesion.setVisible(true);
                progressIndicator.setVisible(false);
                txtId.setDisable(false);
                txtContrasenaLogin.setDisable(false);
                rbtAyuda.setDisable(false);


                boolean encontrado = false;

                // ----- ADMIN -----
                if (id.equals("admin") && password.equals("1234")) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("View/adminView/admin-view.fxml"));
                        Parent root = loader.load();
                        Stage stage = (Stage) txtId.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Pantalla de Inicio");
                    } catch (Exception e) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Error de sistema");
                        alert.setHeaderText(null);
                        alert.setContentText("No fue posible iniciar sesión debido a un error: " + e.getMessage());
                        alert.showAndWait();
                        txtId.clear();
                        txtContrasenaLogin.clear();
                        lblMensajeCampos.setVisible(true);
                    }
                    encontrado = true;
                }

                // ----- MÉDICO -----
                for (Medico m : GestorMedicos.getInstancia().getMedicos()) {
                    if (m.getId().equals(id) && m.getPassword().equals(password)) {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("View/admin-view.fxml"));
                            Parent root = loader.load();
                            Stage stage = (Stage) txtId.getScene().getWindow();
                            stage.setScene(new Scene(root));
                            stage.setTitle("Pantalla de Inicio");
                        } catch (Exception e) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Error de sistema");
                            alert.setHeaderText(null);
                            alert.setContentText("No fue posible iniciar sesión debido a un error: " + e.getMessage());
                            alert.showAndWait();
                        }
                        encontrado = true;
                        break;
                    }
                }

                // ----- FARMACEUTA -----
                    for (Farmaceutico f : GestorFarmaceuticos.getInstancia().getFarmaceuticos()) {
                        if (f.getId().equals(id) && f.getPassword().equals(password)) {
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("View/farma-view.fxml"));
                                Parent root = loader.load();
                                Stage stage = (Stage) txtId.getScene().getWindow();
                                stage.setScene(new Scene(root));
                                stage.setTitle("Pantalla de Inicio");
                            } catch (Exception e) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Error de sistema");
                                alert.setHeaderText(null);
                                alert.setContentText("No fue posible iniciar sesión debido a un error: " + e.getMessage());
                                alert.showAndWait();
                            }
                            encontrado = true;
                            break;
                        }
                    }

                // ----- NO ENCONTRADO -----
                if (!encontrado) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Autenticación");
                    alert.setHeaderText(null);
                    alert.setContentText("Usuario o contraseña incorrectos.");
                    alert.showAndWait();
                    txtId.clear();
                    txtContrasenaLogin.clear();
                    lblMensajeCampos.setVisible(true);
                }
            });
        }).start();
    }

    @FXML
    private void clickAyudaIniciarSesion(ActionEvent event) {
        boolean mostrarAyuda = rbtAyuda.isSelected();
        lblAyudaId.setVisible(mostrarAyuda);
        lblAyudaPassword.setVisible(mostrarAyuda);
        lblAyudaSoporte.setVisible(mostrarAyuda);
    }

}
