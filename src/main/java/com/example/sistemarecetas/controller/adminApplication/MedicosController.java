package com.example.sistemarecetas.controller.adminApplication;

import com.example.sistemarecetas.Model.Medicamento;
import com.example.sistemarecetas.Model.Medico;
import com.example.sistemarecetas.Model.Paciente;
import com.example.sistemarecetas.controller.Async;
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

import java.sql.SQLException;
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

        txtIDMedico.textProperty().addListener((obs, oldVal, newVal) -> handleCodigoChange(newVal));
    }

    private void handleCodigoChange(String codigo) {
        if (currentMode.equals("guardar")) {
            setFieldEditable(txtNombreMedico, true);
            setFieldEditable(txtEspecialidadMedico, true);
            return;
        }

        try {
            Medico m = medicosLogica.findByIdentificacion(codigo);
            if (m != null) {
                txtNombreMedico.setText(m.getNombre());
                txtEspecialidadMedico.setText(m.getEspecialidad());

                boolean editable = currentMode.equals("modificar");
                setFieldEditable(txtNombreMedico, editable);
                setFieldEditable(txtEspecialidadMedico, editable);
            } else if (!currentMode.equals("buscar")) {
                limpiarCampos(false);
                setFieldEditable(txtNombreMedico, false);
            }

            if (currentMode.equals("buscar")) buscarMedico();

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


    private void toggleMode(String mode) {
        currentMode = mode;

        btnGuardarMedico.setSelected(mode.equals("guardar"));
        btnBorrarMedico.setSelected(mode.equals("borrar"));
        btnModificarMedico.setSelected(mode.equals("modificar"));
        btnBuscarMedico.setSelected(mode.equals("buscar"));

        txtIDMedico.setEditable(true);
        txtIDMedico.setStyle("");
        txtIDMedico.setTooltip(null);

        limpiarCampos();

        switch (mode) {
            case "guardar" -> {
                setFieldEditable(txtNombreMedico, true);
                setFieldEditable(txtEspecialidadMedico, true);
            }
            case "modificar", "borrar", "buscar" -> {
                setFieldEditable(txtNombreMedico, false);
                setFieldEditable(txtEspecialidadMedico, false);
            }
        }

        cargarMedicosAsync();
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

            Medico m;
            if (currentMode.equals("guardar")) {
                m = new Medico(identificacion, nombre, especialidad, identificacion);
                guardarClienteAsync(m);
            } else if (currentMode.equals("modificar")) {
                // Para modificar, si el password está vacío, mantenemos el actual
                Medico existente = medicosLogica.findByIdentificacion(identificacion);
                if (existente == null) {
                    mostrarAlerta("No encontrado", "No existe un médico con esa identificación: " + identificacion);
                    return;
                }
                m = new Medico(existente.getId(), identificacion, nombre, especialidad, identificacion);
                modificarMedicoAsync(m);
            } else if (currentMode.equals("borrar")) {
                eliminarMedicoAsync(identificacion);
            }

            limpiarCampos();
            cargarMedicosAsync();

        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    @FXML
    private void buscarMedico() {
        try {
            String codigo = txtIDMedico.getText().trim();

            List<Medico> resultado;
            if (codigo.isEmpty()) resultado = medicosLogica.findAll();
            else resultado = medicosLogica.searchByCodigo(codigo);

            listaObservable.setAll(resultado);

        } catch (SQLException e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    //METODOS CON HILOSSSSSS//
    public void cargarMedicosAsync(){
        progressBar.setVisible(true);
        Async.run(
                () -> {// Esta expresión representa el proceso principal
                    try {
                        // Sobre el proceso principal vamos a ejecutar un hilo con un proceso adicional
                        return medicosLogica.findAll();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                },
                listaClientes -> { // Este es el caso del onSuccess
                    tableMedicos.getItems().setAll(listaClientes);
                    progressBar.setVisible(false);
                },
                ex -> { // Este es el caso del onError
                    progressBar.setVisible(false);
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Error al cargar la lista de Medicos.");
                    a.setHeaderText(null);
                    a.setContentText(ex.getMessage());
                    a.showAndWait();
                }
        );
    }

    public void guardarClienteAsync(Medico c) {
        btnGuardarMedico.setDisable(true);
        progressBar.setVisible(true);

        Async.run(
                () -> { // Este es el proceso principal
                    try {
                        return medicosLogica.create(c);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                },
                guardado -> { // onSuccess
                    progressBar.setVisible(false);
                    btnGuardarMedico.setDisable(false);
                    tableMedicos.getItems().add(guardado);
                    new Alert(Alert.AlertType.INFORMATION, "Medico guardado").showAndWait();
                },
                ex -> { // onError
                    progressBar.setVisible(false);
                    btnGuardarMedico.setDisable(false);
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("No se pudo guardar el Medico");
                    a.setHeaderText(null);
                    a.setContentText(ex.getMessage());
                    a.showAndWait();
                }
        );
    }

    public void modificarMedicoAsync(Medico c) {
        btnModificarMedico.setDisable(true);
        progressBar.setVisible(true);

        Async.run(
                () -> { // Proceso principal (se ejecuta en segundo plano)
                    try {
                        return medicosLogica.update(c);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                },
                actualizado -> { // onSuccess
                    progressBar.setVisible(false);
                    btnModificarMedico.setDisable(false);

                    if (actualizado != null) {
                        // Buscar el paciente en la tabla y actualizarlo visualmente
                        for (int i = 0; i < tableMedicos.getItems().size(); i++) {
                            Medico existente = tableMedicos.getItems().get(i);
                            if (existente.getId() == actualizado.getId()) {
                                tableMedicos.getItems().set(i, actualizado);
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
                    btnModificarMedico.setDisable(false);

                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("Error al modificar el paciente");
                    a.setHeaderText(null);
                    a.setContentText(ex.getMessage());
                    a.showAndWait();
                }
        );
    }

    public void eliminarMedicoAsync(String identificacion) {
        btnBorrarMedico.setDisable(true);
        progressBar.setVisible(true);

        Async.run(
                () -> { // --- proceso en segundo plano
                    try {
                        return medicosLogica.deleteByIdentificacion(identificacion);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                },
                eliminado -> { // --- onSuccess
                    progressBar.setVisible(false);
                    btnBorrarMedico.setDisable(false);

                    if (eliminado) {
                        new Alert(Alert.AlertType.INFORMATION, "Paciente eliminado correctamente").showAndWait();
                        cargarMedicosAsync(); // recarga la tabla
                        limpiarCampos();
                    } else {
                        new Alert(Alert.AlertType.WARNING, "No se encontró ningún paciente con esa identificación").showAndWait();
                    }
                },
                ex -> { // --- onError
                    progressBar.setVisible(false);
                    btnBorrarMedico.setDisable(false);
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("Error al eliminar paciente");
                    a.setHeaderText(null);
                    a.setContentText(ex.getMessage());
                    a.showAndWait();
                }
        );
    }

    // ===== Animación de portada =====
    public void mostrarListaConAnimacion() {
        if (vBoxPortadaMedicos.isVisible()) {
            PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
            pause.setOnFinished(event -> {
                TranslateTransition translate = new TranslateTransition(Duration.seconds(1.5), vBoxPortadaMedicos);
                translate.setToX(-vBoxPortadaMedicos.getWidth() - 20);
                FadeTransition fade = new FadeTransition(Duration.seconds(1.5), vBoxPortadaMedicos);
                fade.setFromValue(1);
                fade.setToValue(0);
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


    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void limpiarCampos() { limpiarCampos(true); }

    private void limpiarCampos(boolean limpiarCodigo) {
        if (limpiarCodigo) txtIDMedico.clear();
        txtNombreMedico.clear();
        txtEspecialidadMedico.clear();

    }
}