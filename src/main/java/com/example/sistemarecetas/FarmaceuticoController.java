package com.example.sistemarecetas;

import Backend.Farmaceutico;
import Gestores.GestorFarmaceuticos;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class FarmaceuticoController implements Initializable {
    // Botones del formulario Farmaceuta
    @FXML private RadioButton btnGuardarFarmaceuta;
    @FXML private RadioButton btnBorrarFarmaceuta;
    @FXML private RadioButton btnModificarFarmaceutico;

    //Controles del formulario Farmaceuta
    @FXML private TextField txtIDFarmaceuta;
    @FXML private TextField txtNombreFarmaceuta;

    //Gestor Farmaceutas y farmaceuta
    private GestorFarmaceuticos gestorfarmaceuticos = new GestorFarmaceuticos();
    private Farmaceutico farmaceutico;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Listener para el botón de Guardar
        btnGuardarFarmaceuta.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // Si el botón de guardar se acaba de seleccionar
                btnBorrarFarmaceuta.setSelected(false); // Deselecciona el de Borrar
                btnModificarFarmaceutico.setSelected(false); // // Deselecciona el de Modificar
            }
        });

        // Listener para el botón de Borrar
        btnBorrarFarmaceuta.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // Si el botón de borrar se acaba de seleccionar
                btnGuardarFarmaceuta.setSelected(false); // Deselecciona el de Guardar
                btnModificarFarmaceutico.setSelected(false); // // Deselecciona el de Modificar
            }
        });

        btnModificarFarmaceutico.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // Si el botón de borrar se acaba de seleccionar
                btnGuardarFarmaceuta.setSelected(false); // Deselecciona el de Guardar
                btnBorrarFarmaceuta.setSelected(false); // Deselecciona el de Borrar
            }
        });

        // Listener para el TextField de ID, rellena los campos dependiendo lo que escriba el usuario para que se llenen automaticamente
        txtIDFarmaceuta.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (newValue.isEmpty()) {
                    txtNombreFarmaceuta.clear();
                    return;
                }

                String id = newValue; // porque ya es un String

                // Buscar médico por ID
                Farmaceutico encontrado = null;
                for (Farmaceutico m : gestorfarmaceuticos.getFarmaceuticos()) {
                    if (m.getId().equals(id)) { // usar equals para comparar Strings
                        encontrado = m;
                        break;
                    }
                }

                // Si lo encontró, rellena los campos
                if (encontrado != null) {
                    txtNombreFarmaceuta.setText(encontrado.getNombre());
                } else {
                    // Si no existe, limpia los campos
                    txtNombreFarmaceuta.clear();
                }

            } catch (NumberFormatException e) {
                // Si el usuario escribe algo que no sea número
                txtNombreFarmaceuta.clear();
            }
        });
    }

    @FXML
    private void GuardarModificarEliminarFarmaceutico() {
        try {
            String id = txtIDFarmaceuta.getText().trim();
            String nombre = txtNombreFarmaceuta.getText().trim();

            if (id.isEmpty() || nombre.isEmpty()) {
                mostrarAlerta("Campos incompletos", "Debe llenar todos los campos del formulario");
                return;
            }

            // Guardar
            if (btnGuardarFarmaceuta.isSelected()) {
                Farmaceutico nuevo = new Farmaceutico(nombre, id, id);

                // Verificar que no se repita el ID
                for (Farmaceutico f : gestorfarmaceuticos.getFarmaceuticos()) {
                    if (f.getId().equals(id)) {
                        mostrarAlerta("Farmacéutico ya existe",
                                "Ya existe un farmacéutico con esa identificación: " + id);
                        limpiarCampos();
                        return;
                    }
                }

                gestorfarmaceuticos.agregarFarmaceuta(nuevo);
                limpiarCampos();

                // Modificar
            } else if (btnModificarFarmaceutico.isSelected()) {
                Farmaceutico existente = gestorfarmaceuticos.buscarPorid(id);

                if (existente == null) {
                    mostrarAlerta("No encontrado", "No existe un farmacéutico con ese ID");
                    limpiarCampos();
                    return;
                }

                existente.setNombre(nombre);
                limpiarCampos();

                // Eliminar
            } else if (btnBorrarFarmaceuta.isSelected()) {
                if (id.isEmpty()) {
                    mostrarAlerta("ID vacío", "Debe ingresar el ID del farmacéutico a eliminar");
                    return;
                }

                Farmaceutico aEliminar = gestorfarmaceuticos.buscarPorid(id);
                if (aEliminar == null) {
                    mostrarAlerta("No encontrado", "No existe un farmacéutico con ese ID: " + id);
                    return;
                }

                gestorfarmaceuticos.eliminarFarmaceutico(aEliminar);
                limpiarCampos();
            }

        } catch (Exception error) {
            String accion;

            if (btnGuardarFarmaceuta.isSelected()) {
                accion = "guardar los datos del farmacéutico";
            } else if (btnModificarFarmaceutico.isSelected()) {
                accion = "modificar los datos del farmacéutico";
            } else if (btnBorrarFarmaceuta.isSelected()) {
                accion = "eliminar el farmacéutico";
            } else {
                accion = "realizar la operación";
            }

            mostrarAlerta("Error al " + accion, error.getMessage());
        }
    }

    @FXML
    private void buscarFarmaceuta() {

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
        txtIDFarmaceuta.clear();
        txtNombreFarmaceuta.clear();
    }
}
