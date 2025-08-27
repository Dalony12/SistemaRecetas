package com.example.sistemarecetas;

import Backend.Medico;
import Gestores.GestorMedicos;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class MedicoController implements Initializable {
    // Botones del formulario Medico
    @FXML private RadioButton btnGuardarMedico;
    @FXML private RadioButton btnBorrarMedico;
    @FXML private RadioButton btnModificarMedico;

    //Controles del formulario Médico
    @FXML private TextField txtIDMedico;
    @FXML private TextField txtNombreMedico;
    @FXML private TextField txtEspecialidadMedico;

    //Gestor Medico y Medico
    private GestorMedicos gestorMedico = new GestorMedicos();
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
                btnGuardarMedico.setSelected(false); // Deselecciona el de Guardar
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

                String id = newValue; // porque ya es un String

                // Buscar médico por ID
                Medico encontrado = null;
                for (Medico m : gestorMedico.getMedicos()) {
                    if (m.getId().equals(id)) { // usar equals para comparar Strings
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
    private void GuardarModificarEliminarMedico() {
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
                Medico nuevo = new Medico(nombre, identificacion, identificacion , especialidad );

                // Verificar que no se repita la identificación
                for (Medico m : gestorMedico.getMedicos()) {
                    if (m.getId().equals(identificacion)) {
                        mostrarAlerta("Médico ya existe",
                                "Ya existe un médico con esa identificación: " + nuevo.getId());
                        limpiarCampos();
                        return;
                    }
                }

                gestorMedico.agregarMedico(nuevo);
                limpiarCampos();

            } else if (btnModificarMedico.isSelected()) {
                // Buscar el médico existente y modificar
                Medico existente = gestorMedico.buscarPorId(identificacion);
                if (existente == null) {
                    mostrarAlerta("No encontrado", "No existe un médico con ese ID");
                    limpiarCampos();
                    return;
                }
                existente.setNombre(nombre);
                existente.setEspecialidad(especialidad);
                limpiarCampos();

            } else if(btnBorrarMedico.isSelected()) {
                String iden = txtIDMedico.getText().trim();

                if (iden.isEmpty()) {
                    mostrarAlerta("ID vacío", "Debe ingresar el ID del médico a eliminar");
                    return;
                }

                // Buscar el médico por ID
                Medico aEliminar = gestorMedico.buscarPorId(iden);
                if (aEliminar == null) {
                    mostrarAlerta("No encontrado", "No existe un médico con ese ID: " + iden);
                    return;
                }

                // Eliminar del gestor
                gestorMedico.eliminarMedico(aEliminar);
                limpiarCampos();
            }

        } catch (Exception error){
            String accion;

            if (btnGuardarMedico.isSelected()) {
                accion = "guardar los datos del médico";
            } else if (btnModificarMedico.isSelected()) {
                accion = "modificar los datos del médico";
            } else if (btnBorrarMedico.isSelected()) {
                accion = "eliminar el médico";
            } else {
                accion = "realizar la operación";
            }

            mostrarAlerta("Error al " + accion, error.getMessage());
        }
    }

    @FXML
    private void bucarMedico() {

    }


    private void mostrarAlerta(String titulo, String mensaje)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void limpiarCampos()
    {
        txtIDMedico.clear();
        txtNombreMedico.clear();
        txtEspecialidadMedico.clear();
    }

}
