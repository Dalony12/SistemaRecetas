package com.example.sistemarecetas.adminApplication;

import Gestores.GestorMedicamentos;
import Model.Medicamento;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class MedicamentosController {

    private static MedicamentosController instance; // Singleton
    public MedicamentosController() { instance = this; }
    public static MedicamentosController getInstance() { return instance; }

    // Panel y tabla
    @FXML private VBox vBoxPortadaMedicamentos;
    @FXML private TableView<Medicamento> tableMedicamentos;
    @FXML private TableColumn<Medicamento, String> colCodigo;
    @FXML private TableColumn<Medicamento, String> colNombre;
    @FXML private TableColumn<Medicamento, String> colPresentacion;
    @FXML private TableColumn<Medicamento, String> colDescripcion;

    // Botones de acción (pueden ser RadioButton o ToggleButton)
    @FXML private RadioButton btnGuardarMedicamento;
    @FXML private RadioButton btnBorrarMedicamento;
    @FXML private RadioButton btnModificarMedicamento;
    @FXML private RadioButton btnBuscarMedicamento;

    // Campos del formulario
    @FXML private TextField txtCodigoMedicamento;
    @FXML private TextField txtNombreMedicamento;
    @FXML private TextField txtPresentacionMedicamento;
    @FXML private TextArea txtDescripcionMedicamento;

    // Lista observable + gestor
    private ObservableList<Medicamento> listaObservable;
    private GestorMedicamentos gestorMedicamento = GestorMedicamentos.getInstancia();

    @FXML
    public void initialize() {
        // Inicializar lista y tabla
        listaObservable = FXCollections.observableArrayList(gestorMedicamento.getMedicamentos());
        tableMedicamentos.setItems(listaObservable);

        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPresentacion.setCellValueFactory(new PropertyValueFactory<>("presentacion"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        // Listeners para que los RadioButton se excluyan mutuamente
        btnGuardarMedicamento.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) { btnBorrarMedicamento.setSelected(false); btnModificarMedicamento.setSelected(false); btnBuscarMedicamento.setSelected(false); }
        });
        btnBorrarMedicamento.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) { btnGuardarMedicamento.setSelected(false); btnModificarMedicamento.setSelected(false); btnBuscarMedicamento.setSelected(false); }
        });
        btnModificarMedicamento.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) { btnGuardarMedicamento.setSelected(false); btnBorrarMedicamento.setSelected(false); btnBuscarMedicamento.setSelected(false); }
        });

        btnBuscarMedicamento.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) { btnGuardarMedicamento.setSelected(false); btnBorrarMedicamento.setSelected(false); btnModificarMedicamento.setSelected(false); }
        });

        txtCodigoMedicamento.textProperty().addListener((obs, oldVal, newVal) -> {
            if (btnBuscarMedicamento.isSelected()) {
                buscarMedicamento();
            }
        });

        txtNombreMedicamento.textProperty().addListener((obs, oldVal, newVal) -> {
            if (btnBuscarMedicamento.isSelected()) {
                buscarMedicamento();
            }
        });

        txtDescripcionMedicamento.textProperty().addListener((obs, oldVal, newVal) -> {
            if (btnBuscarMedicamento.isSelected()) {
                buscarMedicamento();
            }
        });

        txtPresentacionMedicamento.textProperty().addListener((obs, oldVal, newVal) -> {
            if (btnBuscarMedicamento.isSelected()) {
                buscarMedicamento();
            }
        });


        // Listener para autocompletar campos cuando escriben el Codigo
        txtCodigoMedicamento.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                txtNombreMedicamento.clear();
                txtDescripcionMedicamento.clear();
                txtPresentacionMedicamento.clear();
                return;
            }
            Medicamento encontrado = gestorMedicamento.buscarPorCodigo(newVal);
            if (encontrado != null) {
                txtNombreMedicamento.setText(encontrado.getNombre());
                txtPresentacionMedicamento.setText(encontrado.getPresentacion());
                txtDescripcionMedicamento.setText(encontrado.getDescripcion());
            } else {
                txtNombreMedicamento.clear();
                txtDescripcionMedicamento.clear();
                txtPresentacionMedicamento.clear();
            }
        });
    }

    // ---------------- Lógica CRUD ----------------

    @FXML
    private void GuardarModificarEliminarMedicamento() {
        try {
            String nombre = txtNombreMedicamento.getText().trim();
            String presentacion = txtPresentacionMedicamento.getText().trim();
            String identificacion = txtCodigoMedicamento.getText().trim();
            String descripcion = txtDescripcionMedicamento.getText().trim();

            if (nombre.isEmpty() || presentacion.isEmpty() || identificacion.isEmpty() || descripcion.isEmpty()) {
                mostrarAlerta("Campos incompletos", "Debe llenar todos los campos del formulario");
                return;
            }

            if (btnGuardarMedicamento.isSelected()) {
                // Crear nuevo Medicamento
                Medicamento nuevo = new Medicamento(identificacion,nombre, presentacion, descripcion);

                for (Medicamento m : gestorMedicamento.getMedicamentos()) {
                    if (m.getCodigo().equals(identificacion)) {
                        mostrarAlerta("Médico ya existe", "Ya existe un medicamento con esa identificación: " + nuevo.getCodigo());
                        limpiarCampos();
                        return;
                    }
                }
                gestorMedicamento.agregarMedicamento(nuevo);
                listaObservable.add(nuevo);

            } else if (btnModificarMedicamento.isSelected()) {
                Medicamento existente = gestorMedicamento.buscarPorCodigo(identificacion);
                if (existente == null) {
                    mostrarAlerta("No encontrado", "No existe un medicamento con ese ID");
                    limpiarCampos();
                    return;
                }
                existente.setNombre(nombre);
                existente.setPresentacion(presentacion);
                existente.setDescripcion(descripcion);

                tableMedicamentos.refresh();

            } else if (btnBorrarMedicamento.isSelected()) {
                Medicamento aEliminar = gestorMedicamento.buscarPorCodigo(identificacion);
                if (aEliminar == null) {
                    mostrarAlerta("No encontrado", "No existe un medicamento con ese ID: " + identificacion);
                    return;
                }
                gestorMedicamento.eliminarMedicamento(aEliminar);
                listaObservable.remove(aEliminar);
            }

            limpiarCampos();
            refrescarTabla();

        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    @FXML
    private void buscarMedicamento() {
        String criterioID = txtCodigoMedicamento.getText().trim().toLowerCase();
        String criterioNombre = txtNombreMedicamento.getText().trim().toLowerCase();
        String criterioPresentacion = txtPresentacionMedicamento.getText().trim().toLowerCase();
        String criterioDescripcion = txtDescripcionMedicamento.getText().trim().toLowerCase();

        ObservableList<Medicamento> filtrados = FXCollections.observableArrayList();

        for (Medicamento m : gestorMedicamento.getMedicamentos()) {
            boolean coincideID = criterioID.isEmpty() || m.getCodigo().toLowerCase().contains(criterioID);
            boolean coincideNombre = criterioNombre.isEmpty() || m.getNombre().toLowerCase().contains(criterioNombre);
            boolean coincidePresentacion= criterioPresentacion.isEmpty() || m.getPresentacion().toLowerCase().contains(criterioPresentacion);
            boolean coincideDescripcion= criterioDescripcion.isEmpty() || m.getDescripcion().toLowerCase().contains(criterioDescripcion);

            if (coincideID && coincideNombre && coincidePresentacion && coincideDescripcion) {
                filtrados.add(m);
            }
        }

        listaObservable.setAll(filtrados);
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
        txtCodigoMedicamento.clear();
        txtNombreMedicamento.clear();
        txtPresentacionMedicamento.clear();
        txtDescripcionMedicamento.clear();
    }

    // ---------------- Tabla ----------------
    public void refrescarTabla() {
        listaObservable.setAll(gestorMedicamento.getMedicamentos());
    }

    // ---------------- Animación ----------------
    public void mostrarListaConAnimacion() {
        if (vBoxPortadaMedicamentos.isVisible()) {
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.seconds(1));
            pause.setOnFinished(event -> {
                TranslateTransition transition = new TranslateTransition(Duration.seconds(1.5), vBoxPortadaMedicamentos);
                transition.setToX(-vBoxPortadaMedicamentos.getWidth() - 20);
                transition.setOnFinished(e -> vBoxPortadaMedicamentos.setVisible(false));
                transition.play();
            });
            pause.play();
        }
    }
}


