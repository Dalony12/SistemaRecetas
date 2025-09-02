package com.example.sistemarecetas.MedicoApplication;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Objects;

public class AsignacionDatosController {

    @FXML private Spinner<Integer> spnCantidadMedicamento;
    @FXML private Spinner<Integer> spnDuracionMedicamento;
    @FXML private TextArea txtAIndicacionesMedicamento;
    @FXML private Button btnGuardarAsignacion;
    @FXML private Button btnCancelarAsignacion;

    private int cantidadMedicamento = 0;
    private int duracionMedicamento = 0;
    private String indicacionesMedicamento = "";

    public int getCantidadMedicamento() {
        return cantidadMedicamento;
    }

    public int getDuracionMedicamento() {
        return duracionMedicamento;
    }

    public String getIndicacionesMedicamento() {
        return indicacionesMedicamento;
    }


    @FXML
    public void initialize() {
        // Configurar spinner de cantidad
        SpinnerValueFactory<Integer> cantidadFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        spnCantidadMedicamento.setValueFactory(cantidadFactory);

        // Configurar spinner de duración
        SpinnerValueFactory<Integer> duracionFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 365, 1);
        spnDuracionMedicamento.setValueFactory(duracionFactory);
    }


    @FXML
    private void guardarDatosMedicamento() {
        try {
            cantidadMedicamento = spnCantidadMedicamento.getValue();
            duracionMedicamento = spnDuracionMedicamento.getValue();
            indicacionesMedicamento = txtAIndicacionesMedicamento.getText();

            if (cantidadMedicamento == 0 || duracionMedicamento == 0 || Objects.equals(indicacionesMedicamento, "")) {
                mostrarAlerta("Campos incompletos", "Debe llenar todos los campos del formulario");
                return;
            }
            else {
                Stage stage = (Stage) btnGuardarAsignacion.getScene().getWindow();
                stage.close();
            }
        } catch (Exception e) {
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
    @FXML
    private void cancelarBusqueda() {
        spnDuracionMedicamento = null;
        spnCantidadMedicamento = null;
        txtAIndicacionesMedicamento = null;

        // Cerrar la ventana actual
        Stage stage = (Stage) btnCancelarAsignacion.getScene().getWindow();
        stage.close();

    }
}
