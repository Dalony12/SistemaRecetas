package com.example.sistemarecetas.MedicoApplication;

import Model.Medicamento;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class BuscarMedicamentoController {

    private Medicamento medicamento;
    @FXML
    private TableView<Medicamento> tblMedicamentoPresc;

    public Medicamento getMedicamento() {
        return medicamento;
    }

    @FXML
    private void guardarMedicamento() {
        try {
            medicamento = tblMedicamentoPresc.getSelectionModel().getSelectedItem();
            if (medicamento == null) {
                // Se va a lanzar un error (alert)
                mostrarAlerta("Campos incompletos", "Debe llenar todos los campos del formulario");
                return;
            }
            else {
                Stage stage = (Stage) tblMedicamentoPresc.getScene().getWindow();
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
