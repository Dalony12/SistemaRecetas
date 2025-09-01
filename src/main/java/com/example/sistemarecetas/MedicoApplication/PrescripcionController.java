package com.example.sistemarecetas.MedicoApplication;

import Gestores.GestorPacientes;
import Model.Medicamento;
import Model.Paciente;
import Model.Prescripcion;
import Model.Receta;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrescripcionController {

    @FXML
    private Button btnGuardarReceta;
    @FXML
    private DatePicker dtpFechaRetiroPres;
    @FXML
    private TextField txtNombrePacientePresc;
    @FXML
    private TableView<Medicamento> tblMedicamentoPresc;
    @FXML
    private TableView<Prescripcion> tblMedicamentoReceta;
    @FXML
    private TableColumn<Prescripcion, Medicamento> colMedicamento;
    @FXML
    private TableColumn<Prescripcion, String> colPresentacion;
    @FXML
    private TableColumn<Prescripcion, Integer> colCantidad;
    @FXML
    private TableColumn<Prescripcion, String> colIndicaciones;
    @FXML
    private TableColumn<Prescripcion, Integer> colDuracion;

    private GestorPacientes gestorPacientes;

    private List<Prescripcion> listaMedicamentos;
    private Receta receta = null;
    private Medicamento medicamento = null;


    @FXML
    public void initialize() {
        colMedicamento.setCellValueFactory(new PropertyValueFactory<>("medicamento"));
        colPresentacion.setCellValueFactory(new PropertyValueFactory<>("presentacion"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colIndicaciones.setCellValueFactory(new PropertyValueFactory<>("indicaciones"));
        colDuracion.setCellValueFactory(new PropertyValueFactory<>("duracionDias"));

        tblMedicamentoReceta.setItems(FXCollections.observableArrayList());

        listaMedicamentos = new ArrayList<>();
    }

    public void setGestorPacientes(GestorPacientes gestorPacientes) {
        this.gestorPacientes = gestorPacientes;
    }

    @FXML
    private void limpiarPrescripcion() {
        dtpFechaRetiroPres.setValue(null);
        txtNombrePacientePresc.setText("");
        tblMedicamentoReceta.getItems().clear();

        listaMedicamentos.clear();
        medicamento = null;
        receta = null;
    }

    @FXML
    private void abrirBuscarMedicamento() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("buscarMedicamento.fxml"));
            Parent root = loader.load();

            BuscarMedicamentoController controller = loader.getController();

            Stage stage = crearVentanaModal(root, "Buscar Medicamento");

            // cuando se cierre la ventana, recupero el medicamento
            stage.setOnHiding(event -> {
                Medicamento medicamentoSeleccionado = controller.getMedicamento();
                if (medicamentoSeleccionado != null) {
                    this.medicamento = medicamentoSeleccionado;
                    tblMedicamentoPresc.getItems().add(medicamentoSeleccionado);

                    // Se debe de abrir despues de seleccionar el medicamento
                    abrirAsignacionDatos();
                }
            });

            stage.showAndWait();
        }catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir la ventana de búsqueda: " + e.getMessage());
        }
    }

    @FXML
    private void abrirAsignacionDatos() {
        if (medicamento == null) {
            mostrarAlerta("Advertencia", "Primero debe seleccionar un medicamento");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("asignacionMedicamento.fxml"));
            Parent root = loader.load();

            AsignacionDatosController controller = loader.getController();

            Stage stage = crearVentanaModal(root, "Asignar Datos del Medicamento");

            stage.setOnHiding(event -> {
                if (controller.getCantidadMedicamento() > 0 &&
                        controller.getDuracionMedicamento() > 0 &&
                        !controller.getIndicacionesMedicamento().trim().isEmpty()) {

                    Prescripcion prescripcion = new Prescripcion(
                            medicamento,
                            controller.getCantidadMedicamento(),
                            controller.getIndicacionesMedicamento(),
                            controller.getDuracionMedicamento()
                    );
                    tblMedicamentoReceta.getItems().add(prescripcion);
                    listaMedicamentos.add(prescripcion);

                    medicamento = null;
                }
            });

            stage.showAndWait();
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir la ventana de asignación: " + e.getMessage());
        }
    }

    @FXML
    private void guardarPrescripcion() {
        try {
            if (gestorPacientes == null) {
                mostrarAlerta("Error de Sistema", "El gestor de pacientes no está disponible");
                return;
            }

            String nombreCompletoPaciente = txtNombrePacientePresc.getText().trim();
            LocalDate fechaRetiro = dtpFechaRetiroPres.getValue();

            // Verificamos que el formulario este completo
            if (nombreCompletoPaciente.isEmpty() || fechaRetiro == null) {
                mostrarAlerta("Campos incompletos", "Debe llenar todos los campos del formulario");
                return;
            }

            if (listaMedicamentos.isEmpty()) {
                mostrarAlerta("Receta vacia", "Debe seleccionar un medicamento");
                return;
            }

            Paciente paciente = gestorPacientes.buscarPorNombre(nombreCompletoPaciente);

            if (paciente == null) {
                mostrarAlerta("Paciente no encontrado", "No se encontró ningún paciente con el nombre " + nombreCompletoPaciente);
                return;
            }

            if (receta == null) {
                receta = new Receta(paciente, listaMedicamentos, fechaRetiro, true);
            }
            else {
                receta.setPaciente(paciente);
                receta.setMedicamentos(listaMedicamentos);
                receta.setFechaRetiro(fechaRetiro);
                receta.setConfeccionada(true);
            }
        }
        catch (Exception e) {
            mostrarAlerta("Error al guardar los datos", e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }


    private Stage crearVentanaModal(Parent root, String titulo) {
        Stage stage = new Stage();
        stage.setTitle(titulo);
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        return stage;
    }
}
