package com.example.sistemarecetas.controller.MedicoApplication;

import com.example.sistemarecetas.Gestores.GestorPacientes;
import com.example.sistemarecetas.Model.Paciente;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class buscarPacienteController {

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
    private GestorPacientes gestorPacientes = GestorPacientes.getInstancia();

    @FXML
    public void initialize() {
        cmbFiltrarPacientePresc.setItems(FXCollections.observableArrayList("ID", "Nombre"));
        cmbFiltrarPacientePresc.setValue("Nombre");

        listaObservable = FXCollections.observableArrayList(gestorPacientes.getPacientes());
        tblPacientePresc.setItems(listaObservable);

        colIDPaciente.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
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

        ObservableList<Paciente> filtrados = FXCollections.observableArrayList();

        for (Paciente p : listaObservable) {
            if (filtro.equals("ID") && p.getId().toLowerCase().contains(texto)) {
                filtrados.add(p);
            } else if (filtro.equals("Nombre") && p.getNombre().toLowerCase().contains(texto)) {
                filtrados.add(p);
            }
        }

        tblPacientePresc.setItems(filtrados);
    }

    @FXML
    private void guardarPaciente() {
        try {
            pacienteSeleccionado = tblPacientePresc.getSelectionModel().getSelectedItem();
            if (pacienteSeleccionado == null) {
                mostrarAlerta("Campos incompletos", "Debe seleccionar un paciente.");
                return;
            } else {
                Stage stage = (Stage) txtBuscarPacientePresc.getScene().getWindow();
                stage.close();
            }
        } catch (Exception e) {
            mostrarAlerta("Error al guardar los datos", e.getMessage());
        }
    }

    @FXML
    private void cancelarBusqueda() {
        txtBuscarPacientePresc.clear();
        tblPacientePresc.setItems(listaObservable);

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
}
