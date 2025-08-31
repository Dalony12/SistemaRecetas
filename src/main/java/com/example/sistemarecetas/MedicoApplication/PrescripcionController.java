package com.example.sistemarecetas.MedicoApplication;

import Model.Medicamento;
import Model.Prescripcion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class PrescripcionController {

    @FXML
    private Button btnGuardarReceta;
    @FXML
    private DatePicker dtpFechaRetiroPres;
    @FXML
    private TextField txtNombrePacientePresc;
    @FXML
    private TableView<Medicamento> tblMedicamentoReceta;


    private Prescripcion prescripcion;
    private boolean modoEdicion = false;

    private Medicamento medicamento = null;

    private String indicaciones = "";
    private int cantidad = 0;
    private int duracion = 0;

    @FXML
    private void limpiarPrescripcion() {
        dtpFechaRetiroPres.setValue(null);
        txtNombrePacientePresc.setText("");
        tblMedicamentoReceta.getItems().clear();

        medicamento = null;
        indicaciones = "";
        cantidad = 0;
        duracion = 0;
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
                if (medicamento != null) {
                    this.medicamento = medicamentoSeleccionado;
                    tblMedicamentoReceta.getItems().add(medicamentoSeleccionado);

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

                    cantidad = controller.getCantidadMedicamento();
                    duracion = controller.getDuracionMedicamento();
                    indicaciones = controller.getIndicacionesMedicamento();
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
            String nombreCompletoPaciente = txtNombrePacientePresc.getText().trim();
            LocalDate fechaRetiro = dtpFechaRetiroPres.getValue();

            // Verificamos que el formulario este completo
            if (nombreCompletoPaciente.isEmpty() || fechaRetiro == null) {
                // Se va a lanzar un error (alert)
                mostrarAlerta("Campos incompletos", "Debe llenar todos los campos del formulario");
                return;
            }

            if (!modoEdicion) {
                prescripcion = new Prescripcion(medicamento, cantidad, indicaciones, duracion);
            } else {
                prescripcion.setMedicamento(medicamento);
                prescripcion.setIndicaciones(indicaciones);
                prescripcion.setCantidad(cantidad);
                prescripcion.setDuracionDias(duracion);
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
