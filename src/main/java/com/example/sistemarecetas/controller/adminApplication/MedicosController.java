package com.example.sistemarecetas.controller.adminApplication;

import com.example.sistemarecetas.Model.Medico;
import com.example.sistemarecetas.logica.MedicoLogica;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.List;

public class MedicosController {

    private static MedicosController instance;
    public MedicosController() { instance = this; }
    public static MedicosController getInstance() { return instance; }

    @FXML private VBox vBoxPortadaMedicos;
    @FXML private TableView<Medico> tableMedicos;
    @FXML private TableColumn<Medico, String> colID;
    @FXML private TableColumn<Medico, String> colNombre;
    @FXML private TableColumn<Medico, String> colEspecialidad;

    @FXML private RadioButton btnGuardarMedico;
    @FXML private RadioButton btnBorrarMedico;
    @FXML private RadioButton btnModificarMedico;
    @FXML private RadioButton btnBuscarMedico;

    @FXML private TextField txtIDMedico;
    @FXML private TextField txtNombreMedico;
    @FXML private TextField txtEspecialidadMedico;

    @FXML private ProgressBar progressBar;

    private ObservableList<Medico> listaObservable;
    private MedicoLogica medicosLogica;

    private String currentMode = "guardar";

    @FXML
    public void initialize() {
        try {
            medicosLogica = new MedicoLogica();
            listaObservable = FXCollections.observableArrayList(medicosLogica.findAll());
            tableMedicos.setItems(listaObservable);

            colID.setCellValueFactory(new PropertyValueFactory<>("identificacion"));
            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            colEspecialidad.setCellValueFactory(new PropertyValueFactory<>("especialidad"));

            configurarListeners();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al inicializar", e.getMessage());
        }
    }

    private void configurarListeners() {
        btnGuardarMedico.setOnAction(e -> toggleMode("guardar"));
        btnBorrarMedico.setOnAction(e -> toggleMode("borrar"));
        btnModificarMedico.setOnAction(e -> toggleMode("modificar"));
        btnBuscarMedico.setOnAction(e -> toggleMode("buscar"));

        txtIDMedico.textProperty().addListener((obs, oldVal, newVal) -> {
            if (currentMode.equals("buscar")) buscarMedico();
            if (newVal.isEmpty()) limpiarCampos();
            else autocompletarMedico(newVal);
        });

        txtNombreMedico.textProperty().addListener((obs, oldVal, newVal) -> {
            if (currentMode.equals("buscar")) buscarMedico();
        });
    }

    private void autocompletarMedico(String identificacion) {
        try {
            Medico m = medicosLogica.findByIdentificacion(identificacion);
            if (m != null) {
                txtNombreMedico.setText(m.getNombre());
                txtEspecialidadMedico.setText(m.getEspecialidad());
            } else limpiarCampos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void GuardarModificarEliminarMedico() {
        try {
            String identificacion = txtIDMedico.getText().trim();
            String nombre = txtNombreMedico.getText().trim();
            String especialidad = txtEspecialidadMedico.getText().trim();

            if (identificacion.isEmpty() || nombre.isEmpty() || especialidad.isEmpty()) {
                mostrarAlerta("Campos incompletos", "Debe llenar todos los campos del formulario");
                return;
            }

            Medico m = new Medico(identificacion, nombre, especialidad);

            if (currentMode.equals("guardar")) {
                medicosLogica.create(m);
            } else if (currentMode.equals("modificar")) {
                medicosLogica.update(m);
            } else if (currentMode.equals("borrar")) {
                boolean eliminado = medicosLogica.deleteByIdentificacion(identificacion);
                if (!eliminado) mostrarAlerta("No encontrado", "No existe un médico con esa identificación: " + identificacion);
            }

            limpiarCampos();
            cargarMedicos();

        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    @FXML
    private void buscarMedico() {
        String identificacion = txtIDMedico.getText().trim();
        String nombre = txtNombreMedico.getText().trim();

        try {
            if (identificacion.isEmpty() && nombre.isEmpty()) {
                cargarMedicos();
                return;
            }

            List<Medico> resultado = medicosLogica.search(identificacion, nombre);
            listaObservable.setAll(resultado);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mostrarListaConAnimacion() {
        if (vBoxPortadaMedicos.isVisible()) {

            // Pequeña pausa antes de iniciar la animación
            PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
            pause.setOnFinished(event -> {

                // Animación de desplazamiento hacia la izquierda
                TranslateTransition translate = new TranslateTransition(Duration.seconds(1.5), vBoxPortadaMedicos);
                translate.setToX(-vBoxPortadaMedicos.getWidth() - 20);

                // Fade-out simultáneo
                FadeTransition fade = new FadeTransition(Duration.seconds(1.5), vBoxPortadaMedicos);
                fade.setFromValue(1);
                fade.setToValue(0);

                // Cuando termina la animación, ocultar el VBox y resetear posición
                translate.setOnFinished(e -> {
                    vBoxPortadaMedicos.setVisible(false);
                    vBoxPortadaMedicos.setTranslateX(0);
                    vBoxPortadaMedicos.setOpacity(1);
                });

                translate.play();
                fade.play();
            });

            pause.play();
        }
    }

    private void toggleMode(String mode) {
        currentMode = mode;

        btnGuardarMedico.setSelected(mode.equals("guardar"));
        btnBorrarMedico.setSelected(mode.equals("borrar"));
        btnModificarMedico.setSelected(mode.equals("modificar"));
        btnBuscarMedico.setSelected(mode.equals("buscar"));

        txtIDMedico.setEditable(true);
        txtNombreMedico.setEditable(!mode.equals("borrar"));
        txtEspecialidadMedico.setEditable(!mode.equals("borrar") && !mode.equals("buscar"));
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
        txtIDMedico.clear();
        txtNombreMedico.clear();
        txtEspecialidadMedico.clear();
    }

    public void cargarMedicos() {
        try {
            listaObservable.setAll(medicosLogica.findAll());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}