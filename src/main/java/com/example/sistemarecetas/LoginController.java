package com.example.sistemarecetas;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField txtId;
    @FXML private PasswordField txtContraseñaLogin;
    @FXML private Button btnIniciarSesion;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private RadioButton rbtAyuda;

    private Tooltip tooltipId;
    private Tooltip tooltipPassword;

    @FXML
    private void initialize() {
        // Crear tooltips y asociarlos a los campos
        tooltipId = new Tooltip("Ingrese su ID de usuario. Ejemplo: admin");
        tooltipPassword = new Tooltip("Ingrese su contraseña asignada. Ejemplo: 1234");

        Tooltip.install(txtId, tooltipId);
        Tooltip.install(txtContraseñaLogin, tooltipPassword);

        // Por defecto los tooltips están deshabilitados
        tooltipId.setAutoHide(true);
        tooltipPassword.setAutoHide(true);
    }

    // Método que se ejecuta al hacer clic en "Iniciar Sesión"
    @FXML
    private void clickIniciarSesion(ActionEvent event) {
        // Bloquear inputs y mostrar animación
        btnIniciarSesion.setVisible(false);
        progressIndicator.setVisible(true);
        txtId.setDisable(true);
        txtContraseñaLogin.setDisable(true);
        rbtAyuda.setDisable(true);

        String id = txtId.getText();
        String password = txtContraseñaLogin.getText();

        new Thread(() -> {
            try {
                Thread.sleep(1500); // Simula procesamiento
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Platform.runLater(() -> {
                // Restaurar estado UI
                btnIniciarSesion.setVisible(true);
                progressIndicator.setVisible(false);
                txtId.setDisable(false);
                txtContraseñaLogin.setDisable(false);
                rbtAyuda.setDisable(false);

                if (id.equals("admin") && password.equals("1234")) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("View/farma-view.fxml"));
                        Parent root = loader.load();
                        Stage stage = (Stage) txtId.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Pantalla de Inicio");
                    } catch (Exception e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error de sistema");
                        alert.setHeaderText(e.getStackTrace()[0].getClassName());
                        alert.setContentText("No fue posible iniciar sesión, debido a un error de sistema: " + e.getMessage());
                        alert.showAndWait();
                    }
                } else {
                    // Mostrar error de autenticación
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error de autenticación");
                    alert.setHeaderText(null);
                    alert.setContentText("Usuario o contraseña incorrectos.");
                    alert.showAndWait();

                    // Vaciar campos después del error
                    txtId.clear();
                    txtContraseñaLogin.clear();
                }
            });
        }).start();
    }

    @FXML
    private void clickAyudaIniciarSesion(ActionEvent event) {
        if (rbtAyuda.isSelected()) {
            // Mostrar tooltips en pantalla
            tooltipId.show(txtId,
                    txtId.localToScreen(txtId.getBoundsInLocal()).getMinX(),
                    txtId.localToScreen(txtId.getBoundsInLocal()).getMinY() - 30);

            tooltipPassword.show(txtContraseñaLogin,
                    txtContraseñaLogin.localToScreen(txtContraseñaLogin.getBoundsInLocal()).getMinX(),
                    txtContraseñaLogin.localToScreen(txtContraseñaLogin.getBoundsInLocal()).getMinY() - 30);
        } else {
            // Ocultar tooltips
            tooltipId.hide();
            tooltipPassword.hide();
        }
    }
}