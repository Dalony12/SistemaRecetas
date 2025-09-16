package com.example.sistemarecetas.controller.generalControllers;

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

    private static HistorialController instance;

    @FXML private TextField txtNombreHistorialRecetas;
    @FXML private TableView<Receta> tableRecetas;
    @FXML private TableColumn<Receta, String> colPersona;
    @FXML private TableColumn<Receta, String> colMedicamentos;
    @FXML private TableColumn<Receta, String> colConfeccion;
    @FXML private TableColumn<Receta, String> colRetiro;
    @FXML private TableColumn<Receta, String> colCantidad;
    @FXML private TableColumn<Receta, String> colIndicaciones;
    @FXML private TableColumn<Receta, String> colCantidadDias;

    private ObservableList<Receta> listaObservable;
    private RecetasLogica recetasLogica;

    public HistorialController() {
        instance = this;
    }

    public static HistorialController getInstance() {
        return instance;
    }

    @FXML
    public void initialize() {
        try {
            String rutaRecetas = Paths.get(System.getProperty("user.dir"), "datos", "recetas.xml").toString();
            String rutaPacientes = Paths.get(System.getProperty("user.dir"), "datos", "pacientes.xml").toString();
            String rutaMedicamentos = Paths.get(System.getProperty("user.dir"), "datos", "medicamentos.xml").toString();

            recetasLogica = new RecetasLogica(rutaRecetas, rutaPacientes, rutaMedicamentos);

            listaObservable = FXCollections.observableArrayList(recetasLogica.findAll());
            tableRecetas.setItems(listaObservable);

            configurarColumnas();

            txtNombreHistorialRecetas.textProperty().addListener((obs, oldVal, newVal) -> filtrarRecetas());

        } catch (Exception e) {
            mostrarAlerta("Error al cargar historial", e.getMessage());
        }
    }

    /** 🔑 Permite recargar todas las recetas en la tabla desde la lógica */
    public void cargarHistorial() {
        if (recetasLogica != null) {
            listaObservable.setAll(recetasLogica.findAll());
        }
    }

    private void configurarColumnas() {
        colPersona.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPaciente().getNombre()));

        colConfeccion.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFechaConfeccion().toString()));

        colRetiro.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getFechaRetiro() != null ? data.getValue().getFechaRetiro().toString() : "Pendiente"
        ));

        colMedicamentos.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getMedicamentos().stream()
                        .map(p -> p.getMedicamento().getNombre())
                        .collect(Collectors.joining(", "))
        ));

        colCantidad.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getMedicamentos().stream()
                        .map(p -> String.valueOf(p.getCantidad()))
                        .collect(Collectors.joining(", "))
        ));

        colIndicaciones.setCellFactory(tc -> new TableCell<Receta, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    setText(item.length() > 25 ? item.substring(0, 25) + "..." : item);
                    setTooltip(new Tooltip(item));
                }
            }
        });

        colIndicaciones.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getMedicamentos().stream()
                        .map(Prescripcion::getIndicaciones)
                        .collect(Collectors.joining(", "))
        ));

        colCantidadDias.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getMedicamentos().stream()
                        .map(p -> String.valueOf(p.getDuracionDias()))
                        .collect(Collectors.joining(", "))
        ));
    }

    private void filtrarRecetas() {
        String criterio = txtNombreHistorialRecetas.getText().trim().toLowerCase();
        List<Receta> filtradas = recetasLogica.findAll().stream()
                .filter(r -> r.getPaciente().getNombre().toLowerCase().contains(criterio))
                .collect(Collectors.toList());
        listaObservable.setAll(filtradas);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
