package com.example.sistemarecetas.MedicoApplication;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.util.Objects;

public class AsignacionDatosController {

    @FXML
    private Spinner<Integer> spnCantidadMedicamento;
    @FXML
    private Spinner<Integer> spnDuracionMedicamento;
    @FXML
    private TextArea txtAIndicacionesMedicamento;
    @FXML
    private Button btnGuardarAsignacion;

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
    private void guardarDatosMedicamento() {
        try {
            cantidadMedicamento = spnCantidadMedicamento.getValue();
            duracionMedicamento = spnDuracionMedicamento.getValue();
            indicacionesMedicamento = txtAIndicacionesMedicamento.getText();

            if (cantidadMedicamento == 0 || duracionMedicamento == 0 || Objects.equals(indicacionesMedicamento, "")) {
                // Se va a lanzar un error (alert)
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
}
