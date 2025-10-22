package com.example.sistemarecetas.controller.adminApplication;

import com.example.sistemarecetas.Model.Medicamento;
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
import javafx.beans.value.ChangeListener;
import javafx.util.Duration;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    @FXML private ProgressBar progressBar;

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

            toggleMode("guardar");
            configurarListeners();

        } catch (SQLException e) {
            mostrarAlerta("Error al inicializar", e.getMessage());
        }
    }

    private void configurarListeners() {
        btnGuardarPaciente.setOnAction(e -> toggleMode("guardar"));
        btnBorrarPaciente.setOnAction(e -> toggleMode("borrar"));
        btnModificarPaciente.setOnAction(e -> toggleMode("modificar"));
        btnBuscarPaciente.setOnAction(e -> toggleMode("buscar"));

        txtIDPaciente.textProperty().addListener((obs, oldVal, newVal) -> handleCodigoChange(newVal));
    }

    private void handleCodigoChange(String codigo) {
        if (currentMode.equals("guardar")) {
            setFieldEditable(txtNombrePaciente, true);
            setFieldEditable(txtTelefonoPaciente, true);
            setFieldEditable(dtpFechaNacimiento, true);
            return;
        }

        try {
            Paciente m = pacienteLogica.findByIdentificacion(codigo);
            if (m != null) {
                txtNombrePaciente.setText(m.getNombre());
                txtTelefonoPaciente.setText(String.valueOf(m.getTelefono()));
                dtpFechaNacimiento.setValue(m.getFechaNacimiento());

                boolean editable = currentMode.equals("modificar");
                setFieldEditable(txtNombrePaciente, editable);
                setFieldEditable(txtTelefonoPaciente, editable);
                setFieldEditable(dtpFechaNacimiento, editable);
            } else if (!currentMode.equals("buscar")) {
                limpiarCampos(false);
                setFieldEditable(txtNombrePaciente, false);
                setFieldEditable(txtTelefonoPaciente, false);
                setFieldEditable(dtpFechaNacimiento, false);
            }

            if (currentMode.equals("buscar")) buscarPaciente();

        } catch (SQLException e) {
            mostrarAlerta("Error al buscar medicamento", e.getMessage());
        }
    }


    private void setFieldEditable(TextInputControl field, boolean editable) {
        field.setEditable(editable);
        field.setStyle(editable ? "" : "-fx-control-inner-background: #f0f0f0;");
        if (!editable) field.setTooltip(new Tooltip("Campo bloqueado"));
        else field.setTooltip(null);
    }
    private void setFieldEditable(DatePicker picker, boolean editable) {
        picker.setDisable(!editable); // Se usa setDisable en lugar de setEditable
        picker.setStyle(editable ? "" : "-fx-opacity: 0.8; -fx-control-inner-background: #f0f0f0;");
        if (!editable) picker.setTooltip(new Tooltip("Campo bloqueado"));
        else picker.setTooltip(null);
    }

    private void toggleMode(String mode) {
        currentMode = mode;

        btnGuardarPaciente.setSelected(mode.equals("guardar"));
        btnBorrarPaciente.setSelected(mode.equals("borrar"));
        btnModificarPaciente.setSelected(mode.equals("modificar"));
        btnBuscarPaciente.setSelected(mode.equals("buscar"));

        txtIDPaciente.setEditable(true);
        txtIDPaciente.setStyle("");
        txtIDPaciente.setTooltip(null);

        limpiarCampos();

        switch (mode) {
            case "guardar" -> {
                setFieldEditable(txtNombrePaciente, true);
                setFieldEditable(txtTelefonoPaciente, true);
                setFieldEditable(dtpFechaNacimiento, true);
            }
            case "modificar", "borrar", "buscar" -> {
                setFieldEditable(txtNombrePaciente, false);
                setFieldEditable(txtTelefonoPaciente, false);
                setFieldEditable(dtpFechaNacimiento, false);
            }
        }

        cargarPacientesAsync();
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
            Paciente p;

            if (currentMode.equals("guardar")) {
                p = new Paciente(identificacion, nombre, telefono, fechaNacimiento);
                guardarPacienteAsync(p);

            } else if (currentMode.equals("modificar")) {
                // Buscar paciente existente
                Paciente existente = pacienteLogica.findByIdentificacion(identificacion);
                if (existente == null) {
                    mostrarAlerta("No encontrado", "No existe un paciente con esa identificación: " + identificacion);
                    return;
                }
                // Crear objeto con ID existente para actualizar
                p = new Paciente(existente.getId(), identificacion, nombre, telefono, fechaNacimiento);
                modificarPacienteAsync(p);

            } else if (currentMode.equals("borrar")) {
                eliminarPacienteAsync(identificacion);
            }

            limpiarCampos();
            cargarPacientesAsync();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de formato", "El teléfono debe ser un número válido.");
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    @FXML
    private void buscarPaciente() {
        try {
            String codigo = txtIDPaciente.getText().trim();


            List<Paciente> resultado;
            if (codigo.isEmpty()) resultado = pacienteLogica.findAll();
            else resultado = pacienteLogica.searchByCodigo(codigo);

            listaObservable.setAll(resultado);

        } catch (SQLException e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    //METODOS CON HILOSSSSSS//
    public void cargarPacientesAsync(){
        progressBar.setVisible(true);
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
                    progressBar.setVisible(false);
                },
                ex -> { // Este es el caso del onError
                    progressBar.setVisible(false);
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Error al cargar la lista de pacientes.");
                    a.setHeaderText(null);
                    a.setContentText(ex.getMessage());
                    a.showAndWait();
                }
        );
    }

    public void guardarPacienteAsync(Paciente c) {
        btnGuardarPaciente.setDisable(true);
        progressBar.setVisible(true);

        Async.run(
                () -> { // Este es el proceso principal
                    try {
                        return pacienteLogica.create(c);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                },
                guardado -> { // onSuccess
                    progressBar.setVisible(false);
                    btnGuardarPaciente.setDisable(false);
                    tablePacientes.getItems().add(guardado);
                    new Alert(Alert.AlertType.INFORMATION, "Paciente guardado").showAndWait();
                },
                ex -> { // onError
                    progressBar.setVisible(false);
                    btnGuardarPaciente.setDisable(false);
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("No se pudo guardar el Paciente");
                    a.setHeaderText(null);
                    a.setContentText(ex.getMessage());
                    a.showAndWait();
                }
        );
    }

    public void modificarPacienteAsync(Paciente c) {
        btnModificarPaciente.setDisable(true);
        progressBar.setVisible(true);

        Async.run(
                () -> { // Proceso principal (se ejecuta en segundo plano)
                    try {
                        return pacienteLogica.update(c);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                },
                actualizado -> { // onSuccess
                    progressBar.setVisible(false);
                    btnModificarPaciente.setDisable(false);

                    if (actualizado != null) {
                        // Buscar el paciente en la tabla y actualizarlo visualmente
                        for (int i = 0; i < tablePacientes.getItems().size(); i++) {
                            Paciente existente = tablePacientes.getItems().get(i);
                            if (existente.getId() == actualizado.getId()) {
                                tablePacientes.getItems().set(i, actualizado);
                                break;
                            }
                        }

                        new Alert(Alert.AlertType.INFORMATION, "Paciente modificado correctamente").showAndWait();
                    } else {
                        new Alert(Alert.AlertType.WARNING, "No se pudo modificar el paciente.").showAndWait();
                    }
                },
                ex -> { // onError
                    progressBar.setVisible(false);
                    btnModificarPaciente.setDisable(false);

                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("Error al modificar el paciente");
                    a.setHeaderText(null);
                    a.setContentText(ex.getMessage());
                    a.showAndWait();
                }
        );
    }

    public void eliminarPacienteAsync(String identificacion) {
        btnBorrarPaciente.setDisable(true);
        progressBar.setVisible(true);

        Async.run(
                () -> { // --- proceso en segundo plano
                    try {
                        return pacienteLogica.deleteByIdentificacion(identificacion);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                },
                eliminado -> { // --- onSuccess
                    progressBar.setVisible(false);
                    btnBorrarPaciente.setDisable(false);

                    if (eliminado) {
                        new Alert(Alert.AlertType.INFORMATION, "Paciente eliminado correctamente").showAndWait();
                        cargarPacientesAsync(); // recarga la tabla
                        limpiarCampos();
                    } else {
                        new Alert(Alert.AlertType.WARNING, "No se encontró ningún paciente con esa identificación").showAndWait();
                    }
                },
                ex -> { // --- onError
                    progressBar.setVisible(false);
                    btnBorrarPaciente.setDisable(false);
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("Error al eliminar paciente");
                    a.setHeaderText(null);
                    a.setContentText(ex.getMessage());
                    a.showAndWait();
                }
        );
    }


    @FXML
    private void limpiarCampos() { limpiarCampos(true); }


    private void limpiarCampos(boolean limpiarCodigo) {
        if (limpiarCodigo) txtIDPaciente.clear();
        txtNombrePaciente.clear();
        txtTelefonoPaciente.clear();
        dtpFechaNacimiento.setValue(null);
    }

    public void mostrarListaConAnimacion() {
        if (vBoxPortadaPaciente.isVisible()) {
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(event -> {
                TranslateTransition transition = new TranslateTransition(Duration.seconds(1.5), vBoxPortadaPaciente);
                transition.setToX(-vBoxPortadaPaciente.getWidth() - 20);
                transition.setOnFinished(e -> vBoxPortadaPaciente.setVisible(false));
                transition.play();
            });
            pause.play();
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
