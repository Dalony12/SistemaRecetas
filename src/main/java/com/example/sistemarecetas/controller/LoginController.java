package com.example.sistemarecetas.controller;

import com.example.sistemarecetas.Model.Usuario;
import com.example.sistemarecetas.domain.UsuarioActual;
import com.example.sistemarecetas.logica.FarmaceuticoLogica;
import com.example.sistemarecetas.logica.MedicoLogica;
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

    private FarmaceuticoLogica farmaceutasLogica;
    private MedicoLogica medicosLogica;

    @FXML
    private void initialize() {
        // Ocultar etiquetas de ayuda
        lblAyudaId.setVisible(false);
        lblAyudaPassword.setVisible(false);
        lblAyudaSoporte.setVisible(false);

        Tooltip tooltip = new Tooltip("Debe llenar los campos antes de iniciar sesión.");
        Tooltip.install(btnIniciarSesion, tooltip);

        // Reiniciar usuario actual
        UsuarioActual.getInstancia().cerrarSesion();

        btnIniciarSesion.setDisable(true);
        lblMensajeCampos.setVisible(true);

        ChangeListener<String> textListener = (obs, oldText, newText) -> {
            boolean habilitar = !txtId.getText().trim().isEmpty() &&
                    !txtContrasenaLogin.getText().trim().isEmpty();
            btnIniciarSesion.setDisable(!habilitar);
            lblMensajeCampos.setVisible(false);
        };

        txtId.textProperty().addListener(textListener);
        txtContrasenaLogin.textProperty().addListener(textListener);

        // Inicializar lógicas con base de datos
        farmaceutasLogica = new FarmaceuticoLogica();
        medicosLogica = new MedicoLogica();
    }

    @FXML
    private void clickIniciarSesion(ActionEvent event) {
        bloquearUI(true);

        String id = txtId.getText().trim();
        String password = txtContrasenaLogin.getText().trim();

        new Thread(() -> {
            try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

            Platform.runLater(() -> {
                bloquearUI(false);
                boolean encontrado = false;

                try {
                    // ADMIN
                    if (id.equals("admin") && password.equals("1234")) {
                        Usuario admin = new Usuario(0, id,"Administrador", password) {
                            public void mostrarInfo() { System.out.println("Usuario Administrador"); }
                        };
                        UsuarioActual.getInstancia().setUsuario(admin, "Admin");
                        cargarVista("/com/example/sistemarecetas/View/adminView/admin-view.fxml", "Pantalla de Inicio");
                        encontrado = true;
                    }

                    // MÉDICO
                    if (!encontrado) {
                        medicosLogica.findAll().stream()
                                .filter(m -> m.getIdentificacion().equals(id) && m.getPassword().equals(password))
                                .findFirst()
                                .ifPresent(m -> {
                                    UsuarioActual.getInstancia().setUsuario(m, "Medico");
                                    try { cargarVista("/com/example/sistemarecetas/View/MedicoView/Medico-view.fxml", "Pantalla de Inicio"); }
                                    catch (Exception e) { mostrarError("Error al cargar vista de médico: " + e.getMessage()); }
                                });
                        encontrado = UsuarioActual.getInstancia().getUsuario() != null;
                    }

                    // FARMACÉUTICO
                    if (!encontrado) {
                        farmaceutasLogica.findAll().stream()
                                .filter(f -> f.getIdentificacion().equals(id) && f.getPassword().equals(password))
                                .findFirst()
                                .ifPresent(f -> {
                                    UsuarioActual.getInstancia().setUsuario(f, "Farmaceutico");
                                    try { cargarVista("/com/example/sistemarecetas/View/farmaView/farma-view.fxml", "Pantalla de Inicio"); }
                                    catch (Exception e) { mostrarError("Error al cargar vista de farmaceuta: " + e.getMessage()); }
                                });
                        encontrado = UsuarioActual.getInstancia().getUsuario() != null;
                    }

                    // NO ENCONTRADO
                    if (!encontrado) {
                        mostrarInfo("Usuario o contraseña incorrectos.");
                        txtId.clear();
                        txtContrasenaLogin.clear();
                        lblMensajeCampos.setVisible(true);
                    }

                } catch (Exception e) {
                    mostrarError("No fue posible iniciar sesión: " + e.getMessage());
                    txtId.clear();
                    txtContrasenaLogin.clear();
                    lblMensajeCampos.setVisible(true);
                }
            });
        }).start();
    }

    private void bloquearUI(boolean bloquear) {
        btnIniciarSesion.setVisible(!bloquear);
        progressIndicator.setVisible(bloquear);
        txtId.setDisable(bloquear);
        txtContrasenaLogin.setDisable(bloquear);
        rbtAyuda.setDisable(bloquear);
    }

    private void cargarVista(String fxmlPath, String tituloVentana) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Stage stage = (Stage) txtId.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(tituloVentana);
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de sistema");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Autenticación");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void clickAyudaIniciarSesion(ActionEvent event) {
        boolean mostrarAyuda = rbtAyuda.isSelected();
        lblAyudaId.setVisible(mostrarAyuda);
        lblAyudaPassword.setVisible(mostrarAyuda);
        lblAyudaSoporte.setVisible(mostrarAyuda);
    }
}
