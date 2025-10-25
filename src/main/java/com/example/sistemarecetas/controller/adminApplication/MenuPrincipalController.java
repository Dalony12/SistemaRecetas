package com.example.sistemarecetas.controller.adminApplication;

import com.example.sistemarecetas.Model.UsuarioActivo;
import com.example.sistemarecetas.controller.generalControllers.ChatController;
import com.example.sistemarecetas.domain.UsuarioActual;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class MenuPrincipalController {

    @FXML private TableView<UsuarioActivo> tblMensajes;
    @FXML private TableColumn<UsuarioActivo, String> colIDMensajes;
    @FXML private TableColumn<UsuarioActivo, Boolean> colMarcarMensaje;
    @FXML private Button btnSalir;
    @FXML private Button btnEnviar;

    @FXML
    public void initialize() {
        colIDMensajes.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        colMarcarMensaje.setCellValueFactory(cellData -> cellData.getValue().seleccionadoProperty());

        colMarcarMensaje.setCellFactory(column -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(event -> {
                    UsuarioActivo usuario = getTableView().getItems().get(getIndex());
                    usuario.setSeleccionado(checkBox.isSelected());
                });
            }

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    UsuarioActivo usuario = getTableView().getItems().get(getIndex());
                    checkBox.setSelected(usuario.isSeleccionado());
                    setGraphic(checkBox);
                }
            }
        });

        //ejemplo
        tblMensajes.setItems(FXCollections.observableArrayList(
                new UsuarioActivo("ADM-111"),
                new UsuarioActivo("MED-111"),
                new UsuarioActivo("MED-222")
        ));
    }

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

    @FXML
    public void enviar() {
        List<String> seleccionados = tblMensajes.getItems().stream()
                .filter(UsuarioActivo::isSeleccionado)
                .map(UsuarioActivo::getId)
                .toList();

        if (seleccionados.isEmpty()) {
            mostrarAlerta("Error: ", "No hay usuarios seleccionados");
            return;
        }
        abrirChat(seleccionados);
    }

    @FXML
    private void recibir() {
        List<String> seleccionados = tblMensajes.getItems().stream()
                .filter(UsuarioActivo::isSeleccionado)
                .map(UsuarioActivo::getId)
                .toList();

        if (seleccionados.isEmpty()) {
            mostrarAlerta("Error: ", "No hay usuarios seleccionados");
            return;
        }

        abrirChat(seleccionados);
    }

    @FXML
    private void abrirChat(List<String> seleccionados) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/sistemarecetas/View/TabGenerales/chat.fxml")
            );
            Parent root = loader.load();

            ChatController chatController = loader.getController();
            chatController.inicializarDestinatarios(seleccionados);

            Stage stage = new Stage();
            stage.setTitle("Chat de la Clínica");
            stage.setScene(new Scene(root));
            stage.initOwner(btnEnviar.getScene().getWindow());
            stage.setOnCloseRequest(event -> chatController.onClose());
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error al abrir chat", e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
