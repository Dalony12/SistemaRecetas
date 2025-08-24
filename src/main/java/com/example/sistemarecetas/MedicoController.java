package com.example.sistemarecetas;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;

import java.net.URL;
import java.util.ResourceBundle;

public class MedicoController implements Initializable {
    @FXML
    private RadioButton btnGuardarMedico;
    @FXML
    private RadioButton btnBorrarMedico;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Listener para el botón de Guardar
        btnGuardarMedico.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // Si el botón de guardar se acaba de seleccionar
                btnBorrarMedico.setSelected(false); // Deselecciona el de Borrar
            }
        });

        // Listener para el botón de Borrar
        btnBorrarMedico.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // Si el botón de borrar se acaba de seleccionar
                btnGuardarMedico.setSelected(false); // Deselecciona el de Guardar
            }
        });
    }
}
