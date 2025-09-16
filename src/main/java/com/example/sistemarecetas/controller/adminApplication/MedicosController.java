package com.example.sistemarecetas.controller.adminApplication;

import com.example.sistemarecetas.Model.Medico;
import com.example.sistemarecetas.logica.medicos.MedicosLogica;
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

    private ObservableList<Medico> listaObservable;
    private MedicosLogica medicosLogica;
    private String rutaXML;

    @FXML
    public void initialize() {
        try {
            rutaXML = Paths.get(System.getProperty("user.dir"), "datos", "medicos.xml").toString();
            File archivo = new File(rutaXML);

            if (!archivo.exists()) {
                archivo.getParentFile().mkdirs();
                try (FileWriter writer = new FileWriter(archivo)) {
                    writer.write("""
                        <?xml version="1.0" encoding="UTF-8"?>
                        <medicos>
                        </medicos>
                        """);
                }
            }

            medicosLogica = new MedicosLogica(rutaXML);
            listaObservable = FXCollections.observableArrayList(medicosLogica.findAll());
            tableMedicos.setItems(listaObservable);

            colID.setCellValueFactory(new PropertyValueFactory<>("id"));
            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            colEspecialidad.setCellValueFactory(new PropertyValueFactory<>("especialidad"));

            configurarListeners();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al inicializar", e.getMessage());
        }
    }

    private void configurarListeners() {
        btnGuardarMedico.selectedProperty().addListener((obs, oldVal, newVal) -> { if (newVal) toggleMode("guardar"); });
        btnBorrarMedico.selectedProperty().addListener((obs, oldVal, newVal) -> { if (newVal) toggleMode("borrar"); });
        btnModificarMedico.selectedProperty().addListener((obs, oldVal, newVal) -> { if (newVal) toggleMode("modificar"); });
        btnBuscarMedico.selectedProperty().addListener((obs, oldVal, newVal) -> { if (newVal) toggleMode("buscar"); });

        txtIDMedico.textProperty().addListener((obs, oldVal, newVal) -> {
            if (btnBuscarMedico.isSelected()) buscarMedico();
            if (newVal.isEmpty()) limpiarCampos();
            else autocompletarMedico(newVal);
        });

        txtNombreMedico.textProperty().addListener((obs, oldVal, newVal) -> {
            if (btnBuscarMedico.isSelected()) buscarMedico();
        });
    }

    private void autocompletarMedico(String id) {
        Optional<Medico> encontrado = medicosLogica.findByCodigo(id);
        if (encontrado.isPresent()) {
            Medico m = encontrado.get();
            txtNombreMedico.setText(m.getNombre());
            txtEspecialidadMedico.setText(m.getEspecialidad());
        } else {
            txtNombreMedico.clear();
            txtEspecialidadMedico.clear();
        }
    }

    @FXML
    private void GuardarModificarEliminarMedico() {
        try {
            String id = txtIDMedico.getText().trim();
            String nombre = txtNombreMedico.getText().trim();
            String especialidad = txtEspecialidadMedico.getText().trim();

            if (id.isEmpty() || nombre.isEmpty() || especialidad.isEmpty()) {
                mostrarAlerta("Campos incompletos", "Debe llenar todos los campos del formulario");
                return;
            }

            Medico m = new Medico(id, nombre, id, especialidad);

            if (btnGuardarMedico.isSelected()) {
                medicosLogica.create(m);
            } else if (btnModificarMedico.isSelected()) {
                medicosLogica.update(m);
            } else if (btnBorrarMedico.isSelected()) {
                boolean eliminado = medicosLogica.deleteByCodigo(id);
                if (!eliminado) mostrarAlerta("No encontrado", "No existe un medico con ese ID: " + id);
            }

            limpiarCampos();
            cargarMedicos();

        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    @FXML
    private void buscarMedico() {
        String id = txtIDMedico.getText().trim();
        String nombre = txtNombreMedico.getText().trim();

        if (id.isEmpty() && nombre.isEmpty()) {
            cargarMedicos();
            return;
        }

        List<Medico> resultado = medicosLogica.search(id, nombre);
        listaObservable.setAll(resultado);
    }

    private void toggleMode(String mode) {
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
        listaObservable.setAll(medicosLogica.findAll());
    }

    public void mostrarListaConAnimacion() {
        if (vBoxPortadaMedicos.isVisible()) {
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(event -> {
                TranslateTransition transition = new TranslateTransition(Duration.seconds(1.5), vBoxPortadaMedicos);
                transition.setToX(-vBoxPortadaMedicos.getWidth() - 20);
                transition.setOnFinished(e -> vBoxPortadaMedicos.setVisible(false));
                transition.play();
            });
            pause.play();
        }
    }
}