package com.example.sistemarecetas.controller;

import com.example.sistemarecetas.Model.Farmaceutico;
import com.example.sistemarecetas.Model.Medico;
import com.example.sistemarecetas.Model.Usuario;
import com.example.sistemarecetas.domain.UsuarioActual;
import com.example.sistemarecetas.logica.farmaceutas.FarmaceutasLogica;
import com.example.sistemarecetas.logica.medicos.MedicosLogica;
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

    // ---- NUEVO: lógica ----
    private FarmaceutasLogica farmaceutasLogica;
    private MedicosLogica medicosLogica;

    @FXML
    private void initialize() {
        // Ocultar inicialmente etiquetas de ayuda y mensaje
        lblAyudaId.setVisible(false);
        lblAyudaPassword.setVisible(false);
        lblAyudaSoporte.setVisible(false);

        Tooltip tooltip = new Tooltip("Debe llenar los campos antes de iniciar sesión.");
        Tooltip.install(btnIniciarSesion, tooltip);

        // Reiniciar usuario actual
        UsuarioActual.getInstancia().cerrarSesion();

        // Inicialmente deshabilitar botón
        btnIniciarSesion.setDisable(true);
        lblMensajeCampos.setVisible(true);

        // Listener para habilitar/deshabilitar botón según campos
        ChangeListener<String> textListener = (obs, oldText, newText) -> {
            boolean habilitar = !txtId.getText().trim().isEmpty() && !txtContrasenaLogin.getText().trim().isEmpty();
            btnIniciarSesion.setDisable(!habilitar);
            lblMensajeCampos.setVisible(false);
        };

        txtId.textProperty().addListener(textListener);
        txtContrasenaLogin.textProperty().addListener(textListener);

        // ---- Inicializar lógica con rutas a XML ----
        farmaceutasLogica = new FarmaceutasLogica("datos/farmaceutas.xml");
        medicosLogica = new MedicosLogica("datos/medicos.xml");
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

                try {
                    // ----- ADMIN -----
                    if (id.equals("admin") && password.equals("1234")) {
                        Usuario admin = new Usuario(id, "Administrador", password) {
                            @Override
                            public void mostrarInfo() {
                                System.out.println("Usuario Administrador");
                            }
                        };
                        UsuarioActual.getInstancia().setUsuario(admin, "Admin");
                        cargarVista("/com/example/sistemarecetas/View/adminView/admin-view.fxml", "Pantalla de Inicio");
                        encontrado = true;
                    }

                    // ----- MÉDICO -----
                    if (!encontrado) {
                        medicosLogica.findAll().stream()
                                .filter(m -> m.getId().equals(id) && m.getPassword().equals(password))
                                .findFirst()
                                .ifPresent(m -> {
                                    UsuarioActual.getInstancia().setUsuario(m, "Medico");
                                    try {
                                        cargarVista("/com/example/sistemarecetas/View/MedicoView/Medico-view.fxml", "Pantalla de Inicio");
                                    } catch (Exception e) {
                                        mostrarError("Error al cargar vista de médico: " + e.getMessage());
                                    }
                                });
                        encontrado = UsuarioActual.getInstancia().getUsuario() != null;
                    }

                    // ----- FARMACEUTA -----
                    if (!encontrado) {
                        farmaceutasLogica.findAll().stream()
                                .filter(f -> f.getId().equals(id) && f.getPassword().equals(password))
                                .findFirst()
                                .ifPresent(f -> {
                                    UsuarioActual.getInstancia().setUsuario(f, "Farmaceutico");
                                    try {
                                        cargarVista("/com/example/sistemarecetas/View/farmaView/farma-view.fxml", "Pantalla de Inicio");
                                    } catch (Exception e) {
                                        mostrarError("Error al cargar vista de farmaceuta: " + e.getMessage());
                                    }
                                });
                        encontrado = UsuarioActual.getInstancia().getUsuario() != null;
                    }

                    // ----- NO ENCONTRADO -----
                    if (!encontrado) {
                        mostrarInfo("Usuario o contraseña incorrectos.");
                        txtId.clear();
                        txtContrasenaLogin.clear();
                        lblMensajeCampos.setVisible(true);
                    }

                } catch (Exception e) {
                    mostrarError("No fue posible iniciar sesión debido a un error: " + e.getMessage());
                    txtId.clear();
                    txtContrasenaLogin.clear();
                    lblMensajeCampos.setVisible(true);
                }
            });
        }).start();
    }

    // Método de utilidad para cargar vistas
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
