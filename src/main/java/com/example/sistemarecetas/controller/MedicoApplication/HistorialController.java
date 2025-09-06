package com.example.sistemarecetas.controller.MedicoApplication;

import com.example.sistemarecetas.Gestores.GestorRecetas;
import com.example.sistemarecetas.Model.Prescripcion;
import com.example.sistemarecetas.Model.Receta;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;


public class HistorialController {

    @FXML private TextField txtNombreHistorialRecetas;

    //Tabla de los datos
    @FXML private TableView<Receta> tableRecetas;
    @FXML private TableColumn<Receta, String> colPersona;
    @FXML private TableColumn<Receta, String> colMedicamentos;
    @FXML private TableColumn<Receta, String> colConfeccion;
    @FXML private TableColumn<Receta, String> colRetiro;
    @FXML private TableColumn<Receta, String> colCantidad;
    @FXML private TableColumn<Receta, String> colInidcaiones;
    @FXML private TableColumn<Receta, String> colCantidadDías;

    // Lista observable + gestor
    private ObservableList<Receta> listaObservable;
    private GestorRecetas gestorRecetas = GestorRecetas.getInstancia();

    @FXML
    public void initialize() {

        // Inicializar lista y tabla
        listaObservable = FXCollections.observableArrayList(gestorRecetas.getRecetas());
        tableRecetas.setItems(listaObservable);

        colPersona.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPaciente().getNombre()));
        colConfeccion.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFechaConfeccion().toString()));
        colRetiro.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getFechaRetiro() != null ? data.getValue().getFechaRetiro().toString() : "Pendiente"
        ));
        colMedicamentos.setCellValueFactory(data -> {
            List<Prescripcion> lista = data.getValue().getMedicamentos();
            String nombres = "";
            for (Prescripcion p : lista) {
                nombres += p.getMedicamento().getNombre() + ", ";
            }
            if (!nombres.isEmpty()) {
                nombres = nombres.substring(0, nombres.length() - 2);
            }
            return new SimpleStringProperty(nombres);
        });
        colCantidad.setCellValueFactory(data -> {
            List<Prescripcion> lista = data.getValue().getMedicamentos();
            String cantidades = "";
            for (Prescripcion p : lista) {
                cantidades += p.getCantidad() + ", ";
            }
            if (!cantidades.isEmpty()) {
                cantidades = cantidades.substring(0, cantidades.length() - 2);
            }
            return new SimpleStringProperty(cantidades);
        });
        colInidcaiones.setCellValueFactory(data -> {
            List<Prescripcion> lista = data.getValue().getMedicamentos();
            String indicaciones = "";
            for (Prescripcion p : lista) {
                indicaciones += p.getIndicaciones() + ", ";
            }
            if (!indicaciones.isEmpty()) {
                indicaciones = indicaciones.substring(0, indicaciones.length() - 2);
            }
            return new SimpleStringProperty(indicaciones);
        });
        colCantidadDías.setCellValueFactory(data -> {
            List<Prescripcion> lista = data.getValue().getMedicamentos();
            String dias = "";
            for (Prescripcion p : lista) {
                dias += p.getDuracionDias() + ", ";
            }
            if (!dias.isEmpty()) {
                dias = dias.substring(0, dias.length() - 2);
            }
            return new SimpleStringProperty(dias);
        });

        txtNombreHistorialRecetas.textProperty().addListener((obs, oldVal, newVal) -> {
            filtrarRecetas();
        });
    }

    private void filtrarRecetas() {
        String criterioNombre = txtNombreHistorialRecetas.getText().trim().toLowerCase();

        ObservableList<Receta> filtradas = FXCollections.observableArrayList();

        for (Receta r : gestorRecetas.getRecetas()) {
            String idPaciente = r.getPaciente().getId().toLowerCase();
            String nombrePaciente = r.getPaciente().getNombre().toLowerCase();

            boolean coincideNombre = criterioNombre.isEmpty() || nombrePaciente.contains(criterioNombre);

            if (coincideNombre) {
                filtradas.add(r);
            }
        }

        listaObservable.setAll(filtradas);
    }

}
