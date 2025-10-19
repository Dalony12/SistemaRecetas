package com.example.sistemarecetas.controller.adminApplication;

import com.example.sistemarecetas.Model.Medico;
import com.example.sistemarecetas.Model.Paciente;
import com.example.sistemarecetas.controller.Async;
import com.example.sistemarecetas.logica.PacienteLogica;
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

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PacientesController {

    private static PacientesController instance;
    public PacientesController() { instance = this; }
    public static PacientesController getInstance() { return instance; }

    @FXML private VBox vBoxPortadaPaciente;
    @FXML private TableView<Paciente> tablePacientes;
    @FXML private TableColumn<Paciente, String> colID;
    @FXML private TableColumn<Paciente, String> colNombre;
    @FXML private TableColumn<Paciente, LocalDate> colFechaNacimiento;
    @FXML private TableColumn<Paciente, Integer> colTelefono;

    @FXML private RadioButton btnGuardarPaciente;
    @FXML private RadioButton btnBorrarPaciente;
    @FXML private RadioButton btnModificarPaciente;
    @FXML private RadioButton btnBuscarPaciente;

    @FXML private TextField txtIDPaciente;
    @FXML private TextField txtNombrePaciente;
    @FXML private DatePicker dtpFechaNacimiento;
    @FXML private TextField txtTelefonoPaciente;

    @FXML private ProgressBar progressBarPacientes;

    private ObservableList<Paciente> listaObservable;
    private PacienteLogica pacienteLogica;
    private String currentMode = "guardar";

    @FXML
    public void initialize() {
        try {
            pacienteLogica = new PacienteLogica();
            listaObservable = FXCollections.observableArrayList(pacienteLogica.findAll());
            tablePacientes.setItems(listaObservable);

            colID.setCellValueFactory(new PropertyValueFactory<>("identificacion"));
            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            colFechaNacimiento.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
            colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));

            configurarListeners();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al inicializar", e.getMessage());
        }
    }

    private void configurarListeners() {
        btnGuardarPaciente.setOnAction(e -> toggleMode("guardar"));
        btnBorrarPaciente.setOnAction(e -> toggleMode("borrar"));
        btnModificarPaciente.setOnAction(e -> toggleMode("modificar"));
        btnBuscarPaciente.setOnAction(e -> toggleMode("buscar"));

        txtIDPaciente.textProperty().addListener((obs, oldVal, newVal) -> {
            if (currentMode.equals("buscar")) buscarPaciente();
            if (newVal.isEmpty()) limpiarCampos();
            else autocompletarPaciente(newVal);
        });

        txtNombrePaciente.textProperty().addListener((obs, oldVal, newVal) -> {
            if (currentMode.equals("buscar")) buscarPaciente();
        });
    }

    private void autocompletarPaciente(String identificacion) {
        try {
            Paciente p = pacienteLogica.findByIdentificacion(identificacion);
            if (p != null) {
                txtNombrePaciente.setText(p.getNombre());
                txtTelefonoPaciente.setText(String.valueOf(p.getTelefono()));
                dtpFechaNacimiento.setValue(p.getFechaNacimiento());
            } else limpiarCampos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void GuardarModificarEliminarPaciente() {
        try {
            String identificacion = txtIDPaciente.getText().trim();
            String nombre = txtNombrePaciente.getText().trim();
            String telefonoStr = txtTelefonoPaciente.getText().trim();
            LocalDate fechaNacimiento = dtpFechaNacimiento.getValue();

            if (identificacion.isEmpty() || nombre.isEmpty() || telefonoStr.isEmpty() || fechaNacimiento == null) {
                mostrarAlerta("Campos incompletos", "Debe llenar todos los campos del formulario");
                return;
            }

            int telefono = Integer.parseInt(telefonoStr);
            Paciente p = new Paciente(identificacion, nombre, telefono, fechaNacimiento);

            if (currentMode.equals("guardar")) pacienteLogica.create(p);
            else if (currentMode.equals("modificar")) pacienteLogica.update(p);
            else if (currentMode.equals("borrar")) {
                boolean eliminado = pacienteLogica.deleteByIdentificacion(identificacion);
                if (!eliminado) mostrarAlerta("No encontrado", "No existe un paciente con esa identificación: " + identificacion);
            }

            limpiarCampos();
            cargarPacientes();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de formato", "El teléfono debe ser un número válido.");
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    @FXML
    private void buscarPaciente() {
        String identificacion = txtIDPaciente.getText().trim();
        String nombre = txtNombrePaciente.getText().trim();

        try {
            if (identificacion.isEmpty() && nombre.isEmpty()) {
                cargarPacientes();
                return;
            }

            List<Paciente> resultado = pacienteLogica.search(identificacion, nombre);
            listaObservable.setAll(resultado);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /////////HILOS///////
    public void cargarPacientesAsync(){
        progressBarPacientes.setVisible(true);
        Async.run(
                () -> {// Esta expresión representa el proceso principal
                    try {
                        // Sobre el proceso principal vamos a ejecutar un hilo con un proceso adicional
                        return pacienteLogica.findAll();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                },
                listaClientes -> { // Este es el caso del onSuccess
                    tablePacientes.getItems().setAll(listaClientes);
                    progressBarPacientes.setVisible(false);
                },
                ex -> { // Este es el caso del onError
                    progressBarPacientes.setVisible(false);
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Error al cargar la lista de clientes.");
                    a.setHeaderText(null);
                    a.setContentText(ex.getMessage());
                    a.showAndWait();
                }
        );
    }

    public void guardarPacienteAsync(Paciente c) {
        btnGuardarPaciente.setDisable(true);
        progressBarPacientes.setVisible(true);

        Async.run(
                () -> { // Este es el proceso principal
                    try {
                        return pacienteLogica.create(c);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                },
                guardado -> { // onSuccess
                    progressBarPacientes.setVisible(false);
                    btnGuardarPaciente.setDisable(false);
                    tablePacientes.getItems().add(guardado);
                    new Alert(Alert.AlertType.INFORMATION, "Cliente guardado").showAndWait();
                },
                ex -> { // onError
                    progressBarPacientes.setVisible(false);
                    btnGuardarPaciente.setDisable(false);
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("No se pudo guardar el cliente");
                    a.setHeaderText(null);
                    a.setContentText(ex.getMessage());
                    a.showAndWait();
                }
        );
    }

    public void mostrarListaConAnimacion() {
        if (vBoxPortadaPaciente.isVisible()) {

            PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
            pause.setOnFinished(event -> {

                TranslateTransition translate = new TranslateTransition(Duration.seconds(1.5), vBoxPortadaPaciente);
                translate.setToX(-vBoxPortadaPaciente.getWidth() - 20);

                FadeTransition fade = new FadeTransition(Duration.seconds(1.5), vBoxPortadaPaciente);
                fade.setFromValue(1);
                fade.setToValue(0);

                translate.setOnFinished(e -> {
                    vBoxPortadaPaciente.setVisible(false);
                    vBoxPortadaPaciente.setTranslateX(0);
                    vBoxPortadaPaciente.setOpacity(1);
                });

                translate.play();
                fade.play();
            });

            pause.play();
        }
    }

    private void toggleMode(String mode) {
        currentMode = mode;

        btnGuardarPaciente.setSelected(mode.equals("guardar"));
        btnBorrarPaciente.setSelected(mode.equals("borrar"));
        btnModificarPaciente.setSelected(mode.equals("modificar"));
        btnBuscarPaciente.setSelected(mode.equals("buscar"));

        txtIDPaciente.setEditable(true);
        txtNombrePaciente.setEditable(!mode.equals("borrar"));
        txtTelefonoPaciente.setEditable(!mode.equals("borrar") && !mode.equals("buscar"));
        dtpFechaNacimiento.setDisable(mode.equals("borrar") || mode.equals("buscar"));
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
        txtIDPaciente.clear();
        txtNombrePaciente.clear();
        txtTelefonoPaciente.clear();
        dtpFechaNacimiento.setValue(null);
    }

    public void cargarPacientes() {
        try {
            listaObservable.setAll(pacienteLogica.findAll());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
