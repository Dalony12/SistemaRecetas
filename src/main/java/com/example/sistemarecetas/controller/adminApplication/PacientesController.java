package com.example.sistemarecetas.controller.adminApplication;

import com.example.sistemarecetas.Gestores.GestorPacientes;
import com.example.sistemarecetas.Model.Paciente;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.time.LocalDate;

public class PacientesController {

    private static PacientesController instance; // Singleton
    public PacientesController() { instance = this; }
    public static PacientesController getInstance() { return instance; }

    // Panel y tabla
    @FXML private VBox vBoxPortadaPaciente;
    @FXML private TableView<Paciente> tablePacientes;
    @FXML private TableColumn<Paciente, String> colID;
    @FXML private TableColumn<Paciente, String> colNombre;
    @FXML private TableColumn<Paciente, String> colFechaNacimiento;
    @FXML private TableColumn<Paciente, String> colTelefono;

    // Botones de acción (pueden ser RadioButton o ToggleButton)
    @FXML private RadioButton btnGuardarPaciente;
    @FXML private RadioButton btnBorrarPaciente;
    @FXML private RadioButton btnModificarPaciente;
    @FXML private RadioButton btnBuscarPaciente;

    // Campos del formulario
    @FXML private TextField txtIDPaciente;
    @FXML private TextField txtNombrePaciente;
    @FXML private DatePicker dtpFechaNacimiento;
    @FXML private TextField txtTelefonoPaciente;

    // Lista observable + gestor
    private ObservableList<Paciente> listaObservable;
    private GestorPacientes gestorPaciente = GestorPacientes.getInstancia();

    @FXML
    public void initialize() {
        // Inicializar lista y tabla
        listaObservable = FXCollections.observableArrayList(gestorPaciente.getPacientes());
        tablePacientes.setItems(listaObservable);

        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colFechaNacimiento.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        // Listeners para que los RadioButton se excluyan mutuamente
        btnGuardarPaciente.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) { btnBorrarPaciente.setSelected(false); btnModificarPaciente.setSelected(false); btnBuscarPaciente.setSelected(false); txtTelefonoPaciente.setEditable(true); dtpFechaNacimiento.setDisable(false); txtNombrePaciente.setEditable(true);}
        });
        btnBorrarPaciente.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) { btnGuardarPaciente.setSelected(false); btnModificarPaciente.setSelected(false); btnBuscarPaciente.setSelected(false); txtTelefonoPaciente.setEditable(false); dtpFechaNacimiento.setDisable(true); txtNombrePaciente.setEditable(false);}
        });
        btnModificarPaciente.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) { btnGuardarPaciente.setSelected(false); btnBorrarPaciente.setSelected(false); btnBuscarPaciente.setSelected(false); txtTelefonoPaciente.setEditable(true); dtpFechaNacimiento.setDisable(false); txtNombrePaciente.setEditable(true);}
        });

        btnBuscarPaciente.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) { btnGuardarPaciente.setSelected(false); btnBorrarPaciente.setSelected(false); btnModificarPaciente.setSelected(false); txtTelefonoPaciente.setEditable(false); dtpFechaNacimiento.setDisable(true); txtNombrePaciente.setEditable(true); }
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


        // Listener para autocompletar campos cuando escriben el ID
        txtIDPaciente.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                txtNombrePaciente.clear();
                txtTelefonoPaciente.clear();
                dtpFechaNacimiento.setValue(null);
                return;
            }
            Paciente encontrado = gestorPaciente.buscarPorId(newVal);
            if (encontrado != null) {
                txtNombrePaciente.setText(encontrado.getNombre());
                dtpFechaNacimiento.setValue(encontrado.getFechaNacimiento());
                txtTelefonoPaciente.setText(String.valueOf(encontrado.getTelefono()));

            } else {
                txtNombrePaciente.clear();
                dtpFechaNacimiento.setValue(null);
                txtTelefonoPaciente.clear();
            }
        });
    }

    // ---------------- Lógica CRUD ----------------

    @FXML
    private void GuardarModificarEliminarPaciente() {
        try {
            String nombre = txtNombrePaciente.getText().trim();
            String identificacion = txtIDPaciente.getText().trim();
            LocalDate fechaNacimiento = dtpFechaNacimiento.getValue();
            String telefonoTexto = txtTelefonoPaciente.getText().trim();

            if (nombre.isEmpty() || identificacion.isEmpty() || fechaNacimiento == null || telefonoTexto.isEmpty()) {
                mostrarAlerta("Campos incompletos", "Debe llenar todos los campos del formulario");
                return;
            }

            int telefono = Integer.parseInt(telefonoTexto);

            if (btnGuardarPaciente.isSelected()) {
                // Crear nuevo médico
                Paciente nuevo = new Paciente(identificacion,nombre, telefono, fechaNacimiento);

                for (Paciente p : gestorPaciente.getPacientes()) {
                    if (p.getId().equals(identificacion)) {
                        mostrarAlerta("Paciente ya existe", "Ya existe un Paciente con esa identificación: " + nuevo.getId());
                        limpiarCampos();
                        return;
                    }
                }
                gestorPaciente.agregarPaciente(nuevo);
                listaObservable.add(nuevo);

            } else if (btnModificarPaciente.isSelected()) {
                Paciente existente = gestorPaciente.buscarPorId(identificacion);
                if (existente == null) {
                    mostrarAlerta("No encontrado", "No existe un Paciente con ese ID");
                    limpiarCampos();
                    return;
                }
                existente.setNombre(nombre);
                existente.setFechaNacimiento(fechaNacimiento);
                existente.setTelefono(telefono);

                tablePacientes.refresh();

            } else if (btnBorrarPaciente.isSelected()) {
                    Paciente aEliminar = gestorPaciente.buscarPorId(identificacion);
                if (aEliminar == null) {
                    mostrarAlerta("No encontrado", "No existe un Paciente con ese ID: " + identificacion);
                    return;
                }
                gestorPaciente.eliminarPaciente(aEliminar);
                listaObservable.remove(aEliminar);
            }

            limpiarCampos();
            refrescarTabla();

        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    @FXML
    private void buscarPaciente() {
        String criterioID = txtIDPaciente.getText().trim().toLowerCase();
        String criterioNombre = txtNombrePaciente.getText().trim().toLowerCase();
        LocalDate criterioFechaNacimiento = dtpFechaNacimiento.getValue();
        String criterioTelefono = txtTelefonoPaciente.getText().trim().toLowerCase();

        ObservableList<Paciente> filtrados = FXCollections.observableArrayList();

        for (Paciente p : gestorPaciente.getPacientes()) {
            boolean coincideID = criterioID.isEmpty() || p.getId().toLowerCase().contains(criterioID.toLowerCase());
            boolean coincideNombre = criterioNombre.isEmpty() || p.getNombre().toLowerCase().contains(criterioNombre.toLowerCase());
            boolean coincideFechaNacimiento = criterioFechaNacimiento == null || p.getFechaNacimiento().toString().equals(criterioFechaNacimiento);
            boolean coincideTelefono = criterioTelefono.isEmpty() || p.getTelefono() == Integer.parseInt(criterioTelefono);


            if (coincideID && coincideNombre && coincideFechaNacimiento && coincideTelefono) {
                filtrados.add(p);
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
        txtIDPaciente.clear();
        txtNombrePaciente.clear();
        dtpFechaNacimiento.setValue(null);
        txtTelefonoPaciente.clear();
    }

    // ---------------- Tabla ----------------
    public void refrescarTabla() {
        listaObservable.setAll(gestorPaciente.getPacientes());
    }

    // ---------------- Animación ----------------
    public void mostrarListaConAnimacion() {
        if (vBoxPortadaPaciente.isVisible()) {
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.seconds(1));
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
