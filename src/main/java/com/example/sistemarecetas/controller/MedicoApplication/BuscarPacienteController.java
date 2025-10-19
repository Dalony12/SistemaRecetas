package com.example.sistemarecetas.controller.MedicoApplication;

import com.example.sistemarecetas.Model.Paciente;
import com.example.sistemarecetas.logica.PacienteLogica;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class BuscarPacienteController {

    @FXML private ComboBox<String> cmbFiltrarPacientePresc;
    @FXML private TextField txtBuscarPacientePresc;
    @FXML private Button btnCancelarPaciente;

    @FXML private TableView<Paciente> tblPacientePresc;
    @FXML private TableColumn<Paciente, String> colIDPaciente;
    @FXML private TableColumn<Paciente, String> colNombrePaciente;
    @FXML private TableColumn<Paciente, String> colTelefonoPaciente;
    @FXML private TableColumn<Paciente, String> colFechaNacPaciente;

    private Paciente pacienteSeleccionado;
    private ObservableList<Paciente> listaObservable;
    private PacienteLogica pacientesLogica;

    @FXML
    public void initialize() {
        cmbFiltrarPacientePresc.setItems(FXCollections.observableArrayList("ID", "Nombre"));
        cmbFiltrarPacientePresc.setValue("Nombre");

        // Inicializar lógica con base de datos
        pacientesLogica = new PacienteLogica(); // ya no pasa ruta XML

        try {
            listaObservable = FXCollections.observableArrayList(pacientesLogica.findAll());
            tblPacientePresc.setItems(listaObservable);
        } catch (Exception e) {
            mostrarAlerta("Error al cargar pacientes", e.getMessage());
        }

        colIDPaciente.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIdentificacion()));
        colNombrePaciente.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        colTelefonoPaciente.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getTelefono())));
        colFechaNacPaciente.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFechaNacimiento().toString()));

        txtBuscarPacientePresc.textProperty().addListener((obs, oldVal, newVal) -> filtrarPacientes());
        cmbFiltrarPacientePresc.setOnAction(e -> filtrarPacientes());
    }

    public Paciente getPacienteSeleccionado() {
        return pacienteSeleccionado;
    }

    @FXML
    private void filtrarPacientes() {
        String filtro = cmbFiltrarPacientePresc.getValue();
        String texto = txtBuscarPacientePresc.getText().trim().toLowerCase();

        try {
            List<Paciente> pacientesFiltrados;
            if (filtro.equals("ID")) {
                pacientesFiltrados = pacientesLogica.search(texto, ""); // buscar por ID en BD
            } else { // Nombre
                pacientesFiltrados = pacientesLogica.search("", texto); // buscar por nombre en BD
            }

            listaObservable.setAll(pacientesFiltrados);
        } catch (Exception e) {
            mostrarAlerta("Error al filtrar pacientes", e.getMessage());
        }
    }

    @FXML
    private void guardarPaciente() {
        try {
            pacienteSeleccionado = tblPacientePresc.getSelectionModel().getSelectedItem();
            if (pacienteSeleccionado == null) {
                mostrarAlerta("Campos incompletos", "Debe seleccionar un paciente.");
                return;
            }
            Stage stage = (Stage) txtBuscarPacientePresc.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            mostrarAlerta("Error al guardar los datos", e.getMessage());
        }
    }

    @FXML
    private void cancelarBusqueda() {
        try {
            txtBuscarPacientePresc.clear();
            listaObservable.setAll(pacientesLogica.findAll());
        } catch (Exception e) {
            mostrarAlerta("Error al cargar pacientes", e.getMessage());
        }

        Stage stage = (Stage) btnCancelarPaciente.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    public void setListaPacientes(List<Paciente> pacientes) {
        listaObservable.setAll(pacientes);
    }
}