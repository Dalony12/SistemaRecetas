package com.example.sistemarecetas.controller.MedicoApplication;

import com.example.sistemarecetas.Gestores.GestorRecetas;
import com.example.sistemarecetas.Model.Prescripcion;
import com.example.sistemarecetas.Model.Receta;
import com.example.sistemarecetas.logica.recetas.RecetasLogica;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;


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

    @FXML
    public void initialize() {

        try {
            String rutaXML = Paths.get(System.getProperty("user.dir"), "datos", "recetas.xml").toString();
            RecetasLogica recetasLogica = new RecetasLogica(rutaXML);
            List<Receta> recetas = recetasLogica.findAll();

            listaObservable = FXCollections.observableArrayList(recetas);
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
        colInidcaiones.setCellFactory(tc -> new TableCell<Receta, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    setText(item); // sin recorte
                    setTooltip(new Tooltip(item));
                }
            }
        });

            colInidcaiones.setCellValueFactory(data -> {
                String indicaciones = data.getValue().getMedicamentos().stream()
                        .map(Prescripcion::getIndicaciones)
                        .collect(Collectors.joining(", "));
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

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al cargar historial", e.getMessage());
        }

    }

    private void filtrarRecetas() {
        String criterio = txtNombreHistorialRecetas.getText().trim().toLowerCase();
        List<Receta> filtradas = listaObservable.stream()
                .filter(r -> r.getPaciente().getNombre().toLowerCase().contains(criterio))
                .collect(Collectors.toList());
        tableRecetas.setItems(FXCollections.observableArrayList(filtradas));
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}

