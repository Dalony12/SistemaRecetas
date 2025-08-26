package com.example.sistemarecetas;

import Gestores.GestorMedicos;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class FarmaceuticoController implements Initializable {
    // Botones del formulario Medico
    @FXML private RadioButton btnGuardarFarmaceuta;
    @FXML private RadioButton btnBorrarFarmaceuta;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Listener para el botón de Guardar
        btnGuardarFarmaceuta.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // Si el botón de guardar se acaba de seleccionar
                btnBorrarFarmaceuta.setSelected(false); // Deselecciona el de Borrar
            }
        });

        // Listener para el botón de Borrar
        btnBorrarFarmaceuta.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // Si el botón de borrar se acaba de seleccionar
                btnGuardarFarmaceuta.setSelected(false); // Deselecciona el de Guardar
            }
        });
    }
}
