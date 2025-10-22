package com.example.sistemarecetas.controller.adminApplication;

import com.example.sistemarecetas.Model.Farmaceutico;
import com.example.sistemarecetas.Model.Medicamento;
import com.example.sistemarecetas.controller.Async;
import com.example.sistemarecetas.logica.MedicamentoLogica;
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
import java.util.Optional;

public class MedicamentosController {

    private static MedicamentosController instance;
    public MedicamentosController() { instance = this; }
    public static MedicamentosController getInstance() { return instance; }

    @FXML private VBox vBoxPortadaMedicamentos;
    @FXML private TableView<Medicamento> tableMedicamentos;
    @FXML private TableColumn<Medicamento, String> colCodigo;
    @FXML private TableColumn<Medicamento, String> colNombre;
    @FXML private TableColumn<Medicamento, String> colPresentacion;
    @FXML private TableColumn<Medicamento, String> colDescripcion;

    @FXML private RadioButton btnGuardarMedicamento;
    @FXML private RadioButton btnBorrarMedicamento;
    @FXML private RadioButton btnModificarMedicamento;
    @FXML private RadioButton btnBuscarMedicamento;

    @FXML private TextField txtCodigoMedicamento;
    @FXML private TextField txtNombreMedicamento;
    @FXML private TextField txtPresentacionMedicamento;
    @FXML private TextArea txtDescripcionMedicamento;

    @FXML private ProgressBar progressBar;

    private ObservableList<Medicamento> listaObservable;
    private MedicamentoLogica medicamentoLogica;
    private String currentMode = "guardar";

    @FXML
    public void initialize() {
        try {
            medicamentoLogica = new MedicamentoLogica();
            listaObservable = FXCollections.observableArrayList(medicamentoLogica.findAll());
            tableMedicamentos.setItems(listaObservable);

            colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            colPresentacion.setCellValueFactory(new PropertyValueFactory<>("presentacion"));
            colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

            toggleMode("guardar");
            configurarListeners();

        } catch (SQLException e) {
            mostrarAlerta("Error al inicializar", e.getMessage());
        }
    }

    private void configurarListeners() {
        btnGuardarMedicamento.setOnAction(e -> toggleMode("guardar"));
        btnBorrarMedicamento.setOnAction(e -> toggleMode("borrar"));
        btnModificarMedicamento.setOnAction(e -> toggleMode("modificar"));
        btnBuscarMedicamento.setOnAction(e -> toggleMode("buscar"));

        txtCodigoMedicamento.textProperty().addListener((obs, oldVal, newVal) -> handleCodigoChange(newVal));
    }

    private void handleCodigoChange(String codigo) {
        if (currentMode.equals("guardar")) {
            setFieldEditable(txtNombreMedicamento, true);
            setFieldEditable(txtPresentacionMedicamento, true);
            setFieldEditable(txtDescripcionMedicamento, true);
            return;
        }

        try {
            Medicamento m = medicamentoLogica.findByCodigo(codigo);
            if (m != null) {
                txtNombreMedicamento.setText(m.getNombre());
                txtPresentacionMedicamento.setText(m.getPresentacion());
                txtDescripcionMedicamento.setText(m.getDescripcion());

                boolean editable = currentMode.equals("modificar");
                setFieldEditable(txtNombreMedicamento, editable);
                setFieldEditable(txtPresentacionMedicamento, editable);
                setFieldEditable(txtDescripcionMedicamento, editable);
            } else if (!currentMode.equals("buscar")) {
                limpiarCampos(false);
                setFieldEditable(txtNombreMedicamento, false);
                setFieldEditable(txtPresentacionMedicamento, false);
                setFieldEditable(txtDescripcionMedicamento, false);
            }

            if (currentMode.equals("buscar")) buscarMedicamento();

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

        btnGuardarMedicamento.setSelected(mode.equals("guardar"));
        btnBorrarMedicamento.setSelected(mode.equals("borrar"));
        btnModificarMedicamento.setSelected(mode.equals("modificar"));
        btnBuscarMedicamento.setSelected(mode.equals("buscar"));

        txtCodigoMedicamento.setEditable(true);
        txtCodigoMedicamento.setStyle("");
        txtCodigoMedicamento.setTooltip(null);

        limpiarCampos();

        switch (mode) {
            case "guardar" -> {
                setFieldEditable(txtNombreMedicamento, true);
                setFieldEditable(txtPresentacionMedicamento, true);
                setFieldEditable(txtDescripcionMedicamento, true);
            }
            case "modificar", "borrar", "buscar" -> {
                setFieldEditable(txtNombreMedicamento, false);
                setFieldEditable(txtPresentacionMedicamento, false);
                setFieldEditable(txtDescripcionMedicamento, false);
            }
        }

        cargarMedicamentosAsync();
    }

    @FXML
    private void GuardarModificarEliminarMedicamento() {
        try {
            String codigo = txtCodigoMedicamento.getText().trim();
            String nombre = txtNombreMedicamento.getText().trim();
            String presentacion = txtPresentacionMedicamento.getText().trim();
            String descripcion = txtDescripcionMedicamento.getText().trim();

            if (codigo.isEmpty() || nombre.isEmpty() || presentacion.isEmpty() || descripcion.isEmpty()) {
                mostrarAlerta("Campos incompletos", "Debe llenar todos los campos del formulario");
                return;
            }

            Medicamento m;
            if (currentMode.equals("guardar")) {
                m = new Medicamento(codigo, nombre, presentacion, descripcion);
                guardarMedicamentoAsync(m);
            } else if (currentMode.equals("modificar")) {
                // Buscar el medicamento existente por código
                Medicamento existente = medicamentoLogica.findByCodigo(codigo);
                if (existente == null) {
                    mostrarAlerta("No encontrado", "No existe un medicamento con ese código: " + codigo);
                    return;
                }
                // Crear objeto con ID existente para actualizar
                m = new Medicamento(existente.getId(), codigo, nombre, presentacion, descripcion);
                modificarMedicamentoAsync(m);
            } else if (currentMode.equals("borrar")) {
                eliminarMedicamentoAsync(codigo);
            }

            limpiarCampos();
            cargarMedicamentosAsync();

        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    @FXML
    private void buscarMedicamento() {
        try {
            String codigo = txtCodigoMedicamento.getText().trim();

            List<Medicamento> resultado;
            if (codigo.isEmpty()) resultado = medicamentoLogica.findAll();
            else resultado = medicamentoLogica.searchByCodigo(codigo);

            listaObservable.setAll(resultado);

        } catch (SQLException e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    //METODOS CON HILOSSSSSS//

    public void cargarMedicamentosAsync(){
        progressBar.setVisible(true);
        Async.run(
                () -> {// Esta expresión representa el proceso principal
                    try {
                        // Sobre el proceso principal vamos a ejecutar un hilo con un proceso adicional
                        return medicamentoLogica.findAll();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                },
                listaClientes -> { // Este es el caso del onSuccess
                    tableMedicamentos.getItems().setAll(listaClientes);
                    progressBar.setVisible(false);
                },
                ex -> { // Este es el caso del onError
                    progressBar.setVisible(false);
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Error al cargar la lista de Medicamentos.");
                    a.setHeaderText(null);
                    a.setContentText(ex.getMessage());
                    a.showAndWait();
                }
        );
    }

    public void guardarMedicamentoAsync(Medicamento c) {
        btnGuardarMedicamento.setDisable(true);
        progressBar.setVisible(true);

        Async.run(
                () -> { // Este es el proceso principal
                    try {
                        return medicamentoLogica.create(c);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                },
                guardado -> { // onSuccess
                    progressBar.setVisible(false);
                    btnGuardarMedicamento.setDisable(false);
                    tableMedicamentos.getItems().add(guardado);
                    new Alert(Alert.AlertType.INFORMATION, "Medicamento guardado").showAndWait();
                },
                ex -> { // onError
                    progressBar.setVisible(false);
                    btnGuardarMedicamento.setDisable(false);
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("No se pudo guardar el Medicamento");
                    a.setHeaderText(null);
                    a.setContentText(ex.getMessage());
                    a.showAndWait();
                }
        );
    }

    public void modificarMedicamentoAsync(Medicamento f) {
        btnModificarMedicamento.setDisable(true);
        progressBar.setVisible(true);

        Async.run(
                () -> { // --- proceso principal (en segundo plano)
                    try {
                        return medicamentoLogica.update(f);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                },
                actualizado -> { // --- onSuccess
                    progressBar.setVisible(false);
                    btnModificarMedicamento.setDisable(false);

                    if (actualizado != null) {
                        new Alert(Alert.AlertType.INFORMATION, "Medicamento modificado correctamente").showAndWait();
                        try {
                            refrescarTabla();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        limpiarCampos();
                    } else {
                        new Alert(Alert.AlertType.WARNING, "No se pudo modificar el Medicamento.").showAndWait();
                    }
                },
                ex -> { // --- onError
                    progressBar.setVisible(false);
                    btnModificarMedicamento.setDisable(false);
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("Error al modificar Medicamento");
                    a.setHeaderText(null);
                    a.setContentText(ex.getMessage());
                    a.showAndWait();
                }
        );
    }

    public void eliminarMedicamentoAsync(String codigo) {
        btnBorrarMedicamento.setDisable(true);
        progressBar.setVisible(true);

        Async.run(
                () -> { // --- proceso en segundo plano
                    try {
                        return medicamentoLogica.deleteByCodigo(codigo); // devuelve boolean
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                },
                eliminado -> { // --- onSuccess
                    progressBar.setVisible(false);
                    btnBorrarMedicamento.setDisable(false);

                    if (eliminado) {
                        tableMedicamentos.getItems().remove(codigo);
                        new Alert(Alert.AlertType.INFORMATION, "Medicamento eliminado correctamente").showAndWait();
                        try {
                            refrescarTabla();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        new Alert(Alert.AlertType.WARNING, "No se pudo eliminar el medicamento.").showAndWait();
                    }
                },
                ex -> { // --- onError
                    progressBar.setVisible(false);
                    btnBorrarMedicamento.setDisable(false);
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("Error al eliminar medicamento");
                    a.setHeaderText(null);
                    a.setContentText(ex.getMessage());
                    a.showAndWait();
                }
        );
    }

    @FXML
    private void limpiarCampos() { limpiarCampos(true); }

    private void limpiarCampos(boolean limpiarCodigo) {
        if (limpiarCodigo) txtCodigoMedicamento.clear();
        txtNombreMedicamento.clear();
        txtPresentacionMedicamento.clear();
        txtDescripcionMedicamento.clear();
    }

    public void mostrarListaConAnimacion() {
        if (vBoxPortadaMedicamentos.isVisible()) {
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(event -> {
                TranslateTransition transition = new TranslateTransition(Duration.seconds(1.5), vBoxPortadaMedicamentos);
                transition.setToX(-vBoxPortadaMedicamentos.getWidth() - 20);
                transition.setOnFinished(e -> vBoxPortadaMedicamentos.setVisible(false));
                transition.play();
            });
            pause.play();
        }
    }

    public void refrescarTabla() throws SQLException {
        listaObservable.setAll(medicamentoLogica.findAll());
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}