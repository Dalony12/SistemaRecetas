package com.example.sistemarecetas.controller.adminApplication;

import com.example.sistemarecetas.domain.UsuarioActual;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuPrincipalController {

    public void clickCerrarSesion(ActionEvent actionEvent) {
        try {
            // 1. Limpiar el usuario actual
            UsuarioActual.getInstancia().cerrarSesion();

            // 2. Cargar la vista de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sistemarecetas/View/login-view.fxml"));
            Parent root = loader.load();

            // 3. Obtener la ventana actual
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            // 4. Cambiar la escena a login
            stage.setScene(new Scene(root));
            stage.setTitle("Login - Sistema de Recetas");

        } catch (IOException e) {
            // Mostrar alerta en caso de error
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de sistema");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo cerrar sesión: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    public void clickContactarSoporte(MouseEvent mouseEvent) {
    }
}
