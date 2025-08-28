package com.example.sistemarecetas;

//OTRAS BIBLIOTECAS
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import java.net.URL;
import java.util.ResourceBundle;

//MEDICO
import Backend.Medico;
import Gestores.GestorMedicos;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

//FARMACEUTICO
import Backend.Farmaceutico;
import Gestores.GestorFarmaceuticos;
import javafx.scene.control.*;

public class AdminController implements Initializable {
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
        // Listener para el botón de Guardar MEDICO
        btnGuardarMedico.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // Si el botón de guardar se acaba de seleccionar
                btnBorrarMedico.setSelected(false); // Deselecciona el de Borrar
                btnModificarMedico.setSelected(false); // // Deselecciona el de Modificar
            }
        });

        // Listener para el botón de Borrar MEDICO
        btnBorrarMedico.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // Si el botón de borrar se acaba de seleccionar
                btnGuardarMedico.setSelected(false); // Deselecciona el de Guardar
                btnModificarMedico.setSelected(false); // // Deselecciona el de Modificar
            }
        });

        // Listener para el botón de Modificar MEDICO
        btnModificarMedico.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // Si el botón de borrar se acaba de seleccionar
                btnGuardarMedico.setSelected(false); // Deselecciona el de Guardar
                btnBorrarMedico.setSelected(false); // Deselecciona el de Borrar
            }
        });

        // Listener para el TextField de ID de MEDICO, rellena los campos dependiendo lo que escriba el usuario para que se llenen automaticamente
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


        // Listener para el botón de Guardar FARMACEUTA
        btnGuardarFarmaceuta.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // Si el botón de guardar se acaba de seleccionar
                btnBorrarFarmaceuta.setSelected(false); // Deselecciona el de Borrar
                btnModificarFarmaceutico.setSelected(false); // // Deselecciona el de Modificar
            }
        });

        // Listener para el botón de Borrar FARMACEUTA
        btnBorrarFarmaceuta.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // Si el botón de borrar se acaba de seleccionar
                btnGuardarFarmaceuta.setSelected(false); // Deselecciona el de Guardar
                btnModificarFarmaceutico.setSelected(false); // // Deselecciona el de Modificar
            }
        });

        // Listener para el botón de Modificar FARMACEUTA
        btnModificarFarmaceutico.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // Si el botón de borrar se acaba de seleccionar
                btnGuardarFarmaceuta.setSelected(false); // Deselecciona el de Guardar
                btnBorrarFarmaceuta.setSelected(false); // Deselecciona el de Borrar
            }
        });

        // Listener para el TextField de ID FARMACEUTA, rellena los campos dependiendo lo que escriba el usuario para que se llenen automaticamente
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

    //GUARDAR/MODIFICAR/ELIMINAR MEDICO
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
                        limpiarCamposMedico();
                        return;
                    }
                }

                gestorMedico.agregarMedico(nuevo);
                limpiarCamposMedico();

            } else if (btnModificarMedico.isSelected()) {
                // Buscar el médico existente y modificar
                Medico existente = gestorMedico.buscarPorId(identificacion);
                if (existente == null) {
                    mostrarAlerta("No encontrado", "No existe un médico con ese ID");
                    limpiarCamposMedico();
                    return;
                }
                existente.setNombre(nombre);
                existente.setEspecialidad(especialidad);
                limpiarCamposMedico();

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
                limpiarCamposMedico();
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

    //BUSCAR MEDICO
    @FXML
    private void buscarMedico() {

    }

    //LIMPIAR CAMPOS DE MEDICO
    @FXML
    private void limpiarCamposMedico()
    {
        txtIDMedico.clear();
        txtNombreMedico.clear();
        txtEspecialidadMedico.clear();
    }

    //GUARDAR/MODIFICAR/ELIMINAR FARMACEUTA
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
                        limpiarCamposFarmaceuta();
                        return;
                    }
                }

                gestorfarmaceuticos.agregarFarmaceuta(nuevo);
                limpiarCamposFarmaceuta();

                // Modificar
            } else if (btnModificarFarmaceutico.isSelected()) {
                Farmaceutico existente = gestorfarmaceuticos.buscarPorid(id);

                if (existente == null) {
                    mostrarAlerta("No encontrado", "No existe un farmacéutico con ese ID");
                    limpiarCamposFarmaceuta();
                    return;
                }

                existente.setNombre(nombre);
                limpiarCamposFarmaceuta();

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
                limpiarCamposFarmaceuta();
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

    //BUSCAR FARMACEUTA
    @FXML
    private void buscarFarmaceuta() {

    }

    //LIMPIAR ESPACIOS FARMACEUTA
    @FXML
    private void limpiarCamposFarmaceuta()
    {
        txtIDFarmaceuta.clear();
        txtNombreFarmaceuta.clear();
    }

    //MOSTRAR ALERTAS PARA TODOS LOS METODOS
    private void mostrarAlerta(String titulo, String mensaje)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
