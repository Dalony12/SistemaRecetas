package com.example.sistemarecetas.controller.FarmaceuticoApplication;

import com.example.sistemarecetas.Model.Prescripcion;
import com.example.sistemarecetas.Model.Receta;
import com.example.sistemarecetas.logica.recetas.RecetasLogica;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.Region;

import java.util.List;

public class DespachoController {

    @FXML
    private TableView<Receta> tblBuscarPacientesDespacho;
    @FXML
    private TableColumn<Receta, String> colNombre;
    @FXML
    private TableColumn<Receta, String> colID;
    @FXML
    private TableColumn<Receta, String> colPrescripciones;
    @FXML
    private TableColumn<Receta, String> colFechaConfeccion;
    @FXML
    private TableColumn<Receta, String> colFechaRetiro;
    @FXML
    private TableColumn<Receta, String> colEstado;
    @FXML
    private ComboBox<String> cmbFiltrarPaciencteDespacho;
    @FXML
    private TextField txtBuscarClienetDespacho;

    private ObservableList<Receta> listaObservable;
    private RecetasLogica recetasLogica;

    public void initialize() {
        recetasLogica = new RecetasLogica("datos/recetas.xml");

        // filtros
        cmbFiltrarPaciencteDespacho.setItems(FXCollections.observableArrayList("ID", "Nombre"));
        cmbFiltrarPaciencteDespacho.setValue("Nombre");

        // columnas
        colNombre.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getPaciente().getNombre()));
        colID.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getPaciente().getId()));

        colPrescripciones.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    setText(item.length() > 20 ? item.substring(0, 20) + "..." : item);
                    setTooltip(new Tooltip(item));
                }
            }
        });

        colPrescripciones.setCellValueFactory(c -> {
            List<Prescripcion> lista = c.getValue().getMedicamentos();
            StringBuilder sb = new StringBuilder();
            for (Prescripcion p : lista) {
                sb.append(p.getMedicamento().getNombre())
                        .append(" (Cant=")
                        .append(p.getCantidad())
                        .append(", Días=")
                        .append(p.getDuracionDias())
                        .append(", ")
                        .append(p.getIndicaciones())
                        .append("), ");
            }
            if (sb.length() > 0) sb.setLength(sb.length() - 2);
            return new SimpleStringProperty(sb.toString());
        });

        colFechaConfeccion.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getFechaConfeccion().toString()));
        colFechaRetiro.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getFechaRetiro() != null ? c.getValue().getFechaRetiro().toString() : "")
        );

        // estados válidos
        ObservableList<String> opciones = FXCollections.observableArrayList("En proceso", "Lista", "Entregada");
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstado()));
        colEstado.setCellFactory(ComboBoxTableCell.forTableColumn(opciones));

        tblBuscarPacientesDespacho.setEditable(true);
        colEstado.setEditable(true);

        // cambio de estado
        colEstado.setOnEditCommit(event -> {
            Receta receta = event.getRowValue();
            receta.setEstado(event.getNewValue());
            cambiarEstado(receta); // actualiza SOLO esa receta en XML
            tblBuscarPacientesDespacho.refresh(); // refresca solo la tabla
        });

        // cargar recetas
        listaObservable = FXCollections.observableArrayList(recetasLogica.findAll());
        tblBuscarPacientesDespacho.setItems(listaObservable);

        // filtros
        txtBuscarClienetDespacho.textProperty().addListener((obs, o, n) -> filtrarPacientes());
        cmbFiltrarPaciencteDespacho.setOnAction(e -> filtrarPacientes());
    }

    private void filtrarPacientes() {
        String filtro = cmbFiltrarPaciencteDespacho.getValue();
        String texto = txtBuscarClienetDespacho.getText().trim().toLowerCase();

        ObservableList<Receta> filtradas = FXCollections.observableArrayList();

        for (Receta receta : recetasLogica.findAll()) {
            String id = receta.getPaciente().getId().toLowerCase();
            String nombre = receta.getPaciente().getNombre().toLowerCase();

            if (filtro.equals("ID") && id.contains(texto)) {
                filtradas.add(receta);
            } else if (filtro.equals("Nombre") && nombre.contains(texto)) {
                filtradas.add(receta);
            }
        }

        tblBuscarPacientesDespacho.setItems(filtradas);
    }

    private void cambiarEstado(Receta recetaSeleccionada) {
        if (recetaSeleccionada != null) {
            try {
                recetasLogica.update(recetaSeleccionada); // 🔑 actualiza SOLO la receta editada

                mostrarAlerta(Alert.AlertType.INFORMATION, "Estado actualizado",
                        "La receta de " + recetaSeleccionada.getPaciente().getNombre() +
                                " cambió a: " + recetaSeleccionada.getEstado());
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error",
                        "No se pudo actualizar: " + e.getMessage());
            }
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }
}
