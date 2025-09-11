package com.example.sistemarecetas.controller.adminApplication;

import com.example.sistemarecetas.Model.Medicamento;
import com.example.sistemarecetas.logica.medicamentos.MedicamentoLogica;
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

    private ObservableList<Medicamento> listaObservable;
    private MedicamentoLogica medicamentoLogica;

    @FXML
    public void initialize() {
        try {
            // Ruta dinámica al archivo XML
            String rutaXML = Paths.get(System.getProperty("user.dir"), "datos", "medicamentos.xml").toString();
            File archivo = new File(rutaXML);

            // Crear archivo si no existe
            if (!archivo.exists()) {
                archivo.getParentFile().mkdirs();
                String contenidoInicial = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <medicamentos>
                    </medicamentos>
                    """;
                try (FileWriter writer = new FileWriter(archivo)) {
                    writer.write(contenidoInicial);
                    System.out.println("Archivo medicamentos.xml creado en: " + rutaXML);
                }
            }

            // Inicializar lógica y tabla
            medicamentoLogica = new MedicamentoLogica(rutaXML);
            listaObservable = FXCollections.observableArrayList(medicamentoLogica.findAll());
            tableMedicamentos.setItems(listaObservable);

            colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            colPresentacion.setCellValueFactory(new PropertyValueFactory<>("presentacion"));
            colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

            btnGuardarMedicamento.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) toggleMode("guardar");
            });
            btnBorrarMedicamento.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) toggleMode("borrar");
            });
            btnModificarMedicamento.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) toggleMode("modificar");
            });
            btnBuscarMedicamento.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) toggleMode("buscar");
            });

            txtPresentacionMedicamento.textProperty().addListener((obs, oldVal, newVal) -> {
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

            txtCodigoMedicamento.textProperty().addListener((obs, oldVal, newVal) -> {
                if (btnBuscarMedicamento.isSelected()) {
                    buscarMedicamento();
                }
            });

            txtCodigoMedicamento.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.isEmpty()) {
                    limpiarCampos();
                    return;
                }
                Optional<Medicamento> encontrado = medicamentoLogica.findByCodigo(newVal);
                if (encontrado.isPresent()) {
                    Medicamento m = encontrado.get();
                    txtNombreMedicamento.setText(m.getNombre());
                    txtPresentacionMedicamento.setText(m.getPresentacion());
                    txtDescripcionMedicamento.setText(m.getDescripcion());
                } else {
                    txtNombreMedicamento.clear();
                    txtPresentacionMedicamento.clear();
                    txtDescripcionMedicamento.clear();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al inicializar", e.getMessage());
        }
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

            if (btnGuardarMedicamento.isSelected()) {
                medicamentoLogica.create(m);
                listaObservable.add(m);

            } else if (btnModificarMedicamento.isSelected()) {
                medicamentoLogica.update(m);
                refrescarTabla();

            } else if (btnBorrarMedicamento.isSelected()) {
                boolean eliminado = medicamentoLogica.deleteByCodigo(codigo);
                if (eliminado) {
                    listaObservable.removeIf(x -> x.getCodigo().equalsIgnoreCase(codigo));
                } else {
                    mostrarAlerta("No encontrado", "No existe un medicamento con ese código: " + codigo);
                }
            }

            limpiarCampos();
            refrescarTabla();

        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    @FXML
    private void buscarMedicamento() {
        String query = String.join(" ",
                txtCodigoMedicamento.getText(),
                txtNombreMedicamento.getText(),
                txtPresentacionMedicamento.getText(),
                txtDescripcionMedicamento.getText()
        ).trim();

        if (query.isEmpty()) {
            refrescarTabla();
            return;
        }

        List<Medicamento> resultado = medicamentoLogica.search(query);
        listaObservable.setAll(resultado);
    }

    private void toggleMode(String mode) {
        btnGuardarMedicamento.setSelected(mode.equals("guardar"));
        btnBorrarMedicamento.setSelected(mode.equals("borrar"));
        btnModificarMedicamento.setSelected(mode.equals("modificar"));
        btnBuscarMedicamento.setSelected(mode.equals("buscar"));

        boolean editable = !mode.equals("borrar");
        txtDescripcionMedicamento.setEditable(editable);
        txtNombreMedicamento.setEditable(editable);
        txtPresentacionMedicamento.setEditable(editable);
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

    public void refrescarTabla() {
        listaObservable.setAll(medicamentoLogica.findAll());
    }

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