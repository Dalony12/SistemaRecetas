package com.example.sistemarecetas;

import Backend.Medico;
import Gestores.GestorMedicos;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class MedicoController implements Initializable {
    // Botones del formulario Medico
    @FXML private RadioButton btnGuardarMedico;
    @FXML private RadioButton btnBorrarMedico;
    @FXML private RadioButton btnModificarMedico;
    @FXML private RadioButton btnEnviarMedico;
    @FXML private Label lblLimpiarMedico;
    @FXML private Label lblBusquedaMedico;

    //Controles del formulario Médico
    @FXML private TextField txtIDMedico;
    @FXML private TextField txtNombreMedico;
    @FXML private TextField txtEspecialidadMedico;

    //Gestor Medico y Medico
    private GestorMedicos gestorMedico;
    private Medico medico;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Listener para el botón de Guardar
        btnGuardarMedico.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // Si el botón de guardar se acaba de seleccionar
                btnBorrarMedico.setSelected(false); // Deselecciona el de Borrar
                btnModificarMedico.setSelected(false); // // Deselecciona el de Modificar
            }
        });

        // Listener para el botón de Borrar
        btnBorrarMedico.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // Si el botón de borrar se acaba de seleccionar
                btnGuardarMedico.setSelected(false); // Deselecciona el de Guardar
                btnModificarMedico.setSelected(false); // // Deselecciona el de Modificar
            }
        });

        btnModificarMedico.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // Si el botón de borrar se acaba de seleccionar
                btnModificarMedico.setSelected(false); // Deselecciona el de Guardar
                btnBorrarMedico.setSelected(false); // Deselecciona el de Borrar
            }
        });

        // Listener para el TextField de ID, rellena los campos dependiendo lo que escriba el usuario para que se llenen automaticamente
        txtIDMedico.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (newValue.isEmpty()) {
                    txtNombreMedico.clear();
                    txtEspecialidadMedico.clear();
                    return;
                }

                int id = Integer.parseInt(newValue);

                // Buscar médico por ID
                Medico encontrado = null;
                for (Medico m : gestorMedico.getMedicos()) {
                    if (m.getId() == id) {
                        encontrado = m;
                        break;
                    }
                }

                // Si lo encontró, rellena los campos
                if (encontrado != null) {
                    txtNombreMedico.setText(encontrado.getNombre());
                    txtEspecialidadMedico.setText(encontrado.getEspecialidad());
                } else {
                    // Si no existe, limpia los campos
                    txtNombreMedico.clear();
                    txtEspecialidadMedico.clear();
                }

            } catch (NumberFormatException e) {
                // Si el usuario escribe algo que no sea número
                txtNombreMedico.clear();
                txtEspecialidadMedico.clear();
            }
        });
    }

    @FXML
    private void GuardarMedico() {
        try {
            String nombre = txtNombreMedico.getText().trim();
            String especialidad = txtEspecialidadMedico.getText().trim();
            String identificacion = txtIDMedico.getText().trim();

            if(nombre.isEmpty() || especialidad.isEmpty() || identificacion.isEmpty()) {
                mostrarAlerta("Campos incompletos", "Debe llenar todos los campos del formulario");
                return;
            }

            // Solo si está seleccionado el botón de guardar
            if (btnGuardarMedico.isSelected()) {
                int id = Integer.parseInt(identificacion);
                Medico nuevo = new Medico(nombre, id, identificacion , especialidad );

                // Verificar que no se repita la identificación
                for (Medico m : gestorMedico.getMedicos()) {
                    if (m.getId() == (nuevo.getId())) {
                        mostrarAlerta("Médico ya existe",
                                "Ya existe un médico con esa identificación: " + nuevo.getId());
                        return;
                    }
                }

                gestorMedico.agregarMedico(nuevo);
            } else if (btnModificarMedico.isSelected()) {
                // Buscar el médico existente y modificar
                int id = Integer.parseInt(identificacion);
                Medico existente = gestorMedico.buscarPorId(id);
                if (existente == null) {
                    mostrarAlerta("No encontrado", "No existe un médico con ese ID");
                    return;
                }
                existente.setNombre(nombre);
                existente.setEspecialidad(especialidad);
            }

        } catch (Exception error){
            mostrarAlerta("Error al guardar los datos del médico", error.getMessage());
        }
    }


    private void mostrarAlerta(String titulo, String mensaje)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void limpiarCampos()
    {
        txtIDMedico.clear();
        txtNombreMedico.clear();
        txtEspecialidadMedico.clear();
    }

}
