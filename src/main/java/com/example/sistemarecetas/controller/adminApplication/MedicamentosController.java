package com.example.sistemarecetas.controller.adminApplication;

import com.example.sistemarecetas.Model.Medicamento;
import com.example.sistemarecetas.logica.medicamentos.MedicamentoLogica;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class MedicamentosController {

    private static MedicamentosController instance; // Singleton
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

    private ObservableList<Medicamento> listaObservable;
    private MedicamentoLogica medicamentoLogica;
    private String rutaXML;
    private String currentMode = "guardar"; // modo activo

    @FXML
    public void initialize() {
        try {
            rutaXML = Paths.get(System.getProperty("user.dir"), "datos", "medicamentos.xml").toString();
            File archivo = new File(rutaXML);

            if (!archivo.exists()) {
                archivo.getParentFile().mkdirs();
                try (FileWriter writer = new FileWriter(archivo)) {
                    writer.write("""
                        <?xml version="1.0" encoding="UTF-8"?>
                        <medicamentos>
                        </medicamentos>
                        """);
                }
            }

            medicamentoLogica = new MedicamentoLogica(rutaXML);
            listaObservable = FXCollections.observableArrayList(medicamentoLogica.findAll());
            tableMedicamentos.setItems(listaObservable);

            colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            colPresentacion.setCellValueFactory(new PropertyValueFactory<>("presentacion"));
            colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

            toggleMode("guardar"); // inicializamos modo por defecto
            configurarListeners();

        } catch (Exception e) {
            e.printStackTrace();
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
        // Siempre permitir editar todos los campos en modo guardar
        if (currentMode.equals("guardar")) {
            setFieldEditable(txtNombreMedicamento, true);
            setFieldEditable(txtPresentacionMedicamento, true);
            setFieldEditable(txtDescripcionMedicamento, true);
            return;
        }

        // Modo buscar, modificar o borrar
        Optional<Medicamento> encontrado = medicamentoLogica.findByCodigo(codigo);
        if (encontrado.isPresent()) {
            Medicamento m = encontrado.get();
            txtNombreMedicamento.setText(m.getNombre());
            txtPresentacionMedicamento.setText(m.getPresentacion());
            txtDescripcionMedicamento.setText(m.getDescripcion());

            boolean editable = currentMode.equals("modificar");
            setFieldEditable(txtNombreMedicamento, editable);
            setFieldEditable(txtPresentacionMedicamento, editable);
            setFieldEditable(txtDescripcionMedicamento, editable);
        } else {
            // Si no existe
            if (!currentMode.equals("buscar")) {
                limpiarCampos(false);
                setFieldEditable(txtNombreMedicamento, false);
                setFieldEditable(txtPresentacionMedicamento, false);
                setFieldEditable(txtDescripcionMedicamento, false);
            }
        }

        // En buscar, actualizar tabla
        if (currentMode.equals("buscar")) buscarMedicamento();
    }

    private void setFieldEditable(TextInputControl field, boolean editable) {
        field.setEditable(editable);
        if (!editable) {
            field.setStyle("-fx-control-inner-background: #f0f0f0;");
            field.setTooltip(new Tooltip("Campo bloqueado"));
        } else {
            field.setStyle("");
            field.setTooltip(null);
        }
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

        switch (mode) {
            case "guardar" -> {
                setFieldEditable(txtNombreMedicamento, true);
                setFieldEditable(txtPresentacionMedicamento, true);
                setFieldEditable(txtDescripcionMedicamento, true);
                cargarMedicamentos(); // restaurar lista completa
            }
            case "borrar", "modificar" -> {
                setFieldEditable(txtNombreMedicamento, false);
                setFieldEditable(txtPresentacionMedicamento, false);
                setFieldEditable(txtDescripcionMedicamento, false);
                cargarMedicamentos(); // restaurar lista completa
            }
            case "buscar" -> {
                setFieldEditable(txtNombreMedicamento, false);
                setFieldEditable(txtPresentacionMedicamento, false);
                setFieldEditable(txtDescripcionMedicamento, false);
            }
        }

        limpiarCampos();
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

            Medicamento m = new Medicamento(codigo, nombre, presentacion, descripcion);

            switch (currentMode) {
                case "guardar" -> medicamentoLogica.create(m);
                case "modificar" -> medicamentoLogica.update(m);
                case "borrar" -> {
                    boolean eliminado = medicamentoLogica.deleteByCodigo(codigo);
                    if (!eliminado) mostrarAlerta("No encontrado", "No existe un medicamento con ese código: " + codigo);
                }
            }

            limpiarCampos();
            cargarMedicamentos();

        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    @FXML
    private void buscarMedicamento() {
        String id = txtCodigoMedicamento.getText().trim();
        String nombre = txtNombreMedicamento.getText().trim();

        if (id.isEmpty() && nombre.isEmpty()) {
            cargarMedicamentos();
            return;
        }

        List<Medicamento> resultado = medicamentoLogica.search(id, nombre);
        listaObservable.setAll(resultado);
    }

    @FXML
    private void limpiarCampos() {
        limpiarCampos(true);
    }

    private void limpiarCampos(boolean limpiarCodigo) {
        if (limpiarCodigo) txtCodigoMedicamento.clear();
        txtNombreMedicamento.clear();
        txtPresentacionMedicamento.clear();
        txtDescripcionMedicamento.clear();
    }

    public void cargarMedicamentos() {
        listaObservable.setAll(medicamentoLogica.findAll());
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

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
