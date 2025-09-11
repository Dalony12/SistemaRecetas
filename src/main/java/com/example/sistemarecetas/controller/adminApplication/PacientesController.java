package com.example.sistemarecetas.controller.adminApplication;

import com.example.sistemarecetas.Model.Paciente;
import com.example.sistemarecetas.logica.farmaceutas.FarmaceutasLogica;
import com.example.sistemarecetas.logica.pacientes.PacientesLogica;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PacientesController {

    private static PacientesController instance;
    public PacientesController() { instance = this; }
    public static PacientesController getInstance() { return instance; }

    // Panel y tabla
    @FXML private VBox vBoxPortadaPaciente;
    @FXML private TableView<Paciente> tablePacientes;
    @FXML private TableColumn<Paciente, String> colID;
    @FXML private TableColumn<Paciente, String> colNombre;
    @FXML private TableColumn<Paciente, String> colFechaNacimiento;
    @FXML private TableColumn<Paciente, String> colTelefono;

    // Botones de acción
    @FXML private RadioButton btnGuardarPaciente;
    @FXML private RadioButton btnBorrarPaciente;
    @FXML private RadioButton btnModificarPaciente;
    @FXML private RadioButton btnBuscarPaciente;

    // Campos del formulario
    @FXML private TextField txtIDPaciente;
    @FXML private TextField txtNombrePaciente;
    @FXML private DatePicker dtpFechaNacimiento;
    @FXML private TextField txtTelefonoPaciente;

    private ObservableList<Paciente> listaObservable;
    private PacientesLogica pacienteLogica;

    @FXML
    public void initialize() {
        try {
            // Ruta dinámica al archivo XML
            String rutaXML = Paths.get(System.getProperty("user.dir"), "datos", "pacientes.xml").toString();
            File archivo = new File(rutaXML);

            // Crear archivo si no existe
            if (!archivo.exists()) {
                archivo.getParentFile().mkdirs();
                String contenidoInicial = """
                        <?xml version="1.0" encoding="UTF-8"?>
                        <pacientes>
                        </pacientes>
                        """;
                try (FileWriter writer = new FileWriter(archivo)) {
                    writer.write(contenidoInicial);
                    System.out.println("Archivo pacientes.xml creado en: " + rutaXML);
                }
            }
            // Inicializar lógica y tabla
            pacienteLogica = new PacientesLogica(rutaXML);
            listaObservable = FXCollections.observableArrayList(pacienteLogica.findAll());
            tablePacientes.setItems(listaObservable);

            colID.setCellValueFactory(new PropertyValueFactory<>("id"));
            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            colFechaNacimiento.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
            colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));

            btnGuardarPaciente.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) toggleMode("guardar");
            });
            btnBorrarPaciente.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) toggleMode("borrar");
            });
            btnModificarPaciente.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) toggleMode("modificar");
            });
            btnBuscarPaciente.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) toggleMode("buscar");
            });

            txtIDPaciente.textProperty().addListener((obs, oldVal, newVal) -> {
                if (btnBuscarPaciente.isSelected()) {
                    buscarPaciente();
                }
            });

            txtNombrePaciente.textProperty().addListener((obs, oldVal, newVal) -> {
                if (btnBuscarPaciente.isSelected()) {
                    buscarPaciente();
                }
            });

            txtIDPaciente.textProperty().addListener((obs, oldVal, newVal) -> {
                if (btnBuscarPaciente.isSelected()) buscarPaciente();
                if (newVal.isEmpty()) {
                    limpiarCampos();
                    return;
                }
                Optional<Paciente> encontrado = pacienteLogica.findByCodigo(newVal);
                if (encontrado.isPresent()) {
                    Paciente p = encontrado.get();
                    txtNombrePaciente.setText(p.getNombre());
                    txtTelefonoPaciente.setText(String.valueOf(p.getTelefono()));
                    dtpFechaNacimiento.setValue(p.getFechaNacimiento());
                } else {
                    txtNombrePaciente.clear();
                    txtTelefonoPaciente.clear();
                    dtpFechaNacimiento.setValue(null);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al inicializar", e.getMessage());
        }
    }

    @FXML
    private void GuardarModificarEliminarPaciente() {
        try {
            String id = txtIDPaciente.getText().trim();
            String nombre = txtNombrePaciente.getText().trim();
            String telefonoStr = txtTelefonoPaciente.getText().trim();
            LocalDate fechaNacimiento = dtpFechaNacimiento.getValue();

            if (id.isEmpty() || nombre.isEmpty() || telefonoStr.isEmpty() || fechaNacimiento == null) {
                mostrarAlerta("Campos incompletos", "Debe llenar todos los campos del formulario");
                return;
            }

            int telefono = Integer.parseInt(telefonoStr);
            Paciente p = new Paciente(id, nombre, telefono, fechaNacimiento);

            if (btnGuardarPaciente.isSelected()) {
                pacienteLogica.create(p);
                listaObservable.add(p);

            } else if (btnModificarPaciente.isSelected()) {
                pacienteLogica.update(p);
                refrescarTabla();

            } else if (btnBorrarPaciente.isSelected()) {
                boolean eliminado = pacienteLogica.deleteByCodigo(id);
                if (eliminado) {
                    listaObservable.removeIf(x -> x.getId().equalsIgnoreCase(id));
                } else {
                    mostrarAlerta("No encontrado", "No existe un paciente con ese código: " + id);
                }
            }

            limpiarCampos();
            refrescarTabla();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de formato", "El teléfono debe ser un número válido.");
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    @FXML
    private void buscarPaciente() {
        String id = txtIDPaciente.getText().trim();
        String nombre = txtNombrePaciente.getText().trim();

        if (id.isEmpty() && nombre.isEmpty()) {
            refrescarTabla();
            return;
        }

        List<Paciente> resultado = pacienteLogica.search(id, nombre);
        listaObservable.setAll(resultado);
    }

    private void toggleMode(String mode) {
        btnGuardarPaciente.setSelected(mode.equals("guardar"));
        btnBorrarPaciente.setSelected(mode.equals("borrar"));
        btnModificarPaciente.setSelected(mode.equals("modificar"));
        btnBuscarPaciente.setSelected(mode.equals("buscar"));

        boolean editable = !mode.equals("borrar");
        txtNombrePaciente.setEditable(editable);
        txtTelefonoPaciente.setEditable(editable);
        dtpFechaNacimiento.setDisable(!editable);
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

    public void refrescarTabla() {
        listaObservable.setAll(pacienteLogica.findAll());
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
}
