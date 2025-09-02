package com.example.sistemarecetas.FarmaceuticoApplication;

import Model.Medicamento;
import Model.Paciente;
import Model.Prescripcion;
import Model.Receta;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.ComboBoxTableCell;

import java.time.LocalDate;
import java.util.ArrayList;
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
    public void initialize() {
        colNombre.setCellValueFactory(cellData ->
                        new SimpleStringProperty(cellData.getValue().getPaciente().getNombre())
        );
        colID.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPaciente().getId())
        );
        colPrescripciones.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getMedicamentos().toString())
        );

        colPrescripciones.setCellFactory(tc -> {
            return new TableCell<Receta, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setTooltip(null);
                    } else {
                        // Muestra solo un pedacito si es largo
                        setText(item.length() > 20 ? item.substring(0, 20) + "..." : item);

                        // Tooltip con todo el texto
                        Tooltip tooltip = new Tooltip(item);
                        setTooltip(tooltip);
                    }
                }
            };
        });

        colPrescripciones.setCellValueFactory(cellData -> {
            List<Prescripcion> lista = cellData.getValue().getMedicamentos();
            StringBuilder sb = new StringBuilder();
            for (Prescripcion p : lista) {
                sb.append(p.getMedicamento().getNombre()) // nombre del medicamento
                        .append(" (Cant = ")
                        .append(p.getCantidad())
                        .append(", Duración = ")
                        .append(p.getDuracionDias())
                        .append(", ")
                        .append(p.getIndicaciones())
                        .append("), ");
            }
            // Elimina la última coma y espacio si hay algo
            if (sb.length() > 0) sb.setLength(sb.length() - 2);

            return new SimpleStringProperty(sb.toString());
        });

        colFechaConfeccion.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFechaConfeccion().toString())
        );
        colFechaRetiro.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFechaRetiro().toString())
        );
        colEstado.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEstado())
        );

        // Opciones para la columna Estado
        ObservableList<String> opciones = FXCollections.observableArrayList("En proceso", "Lista", "Entregado");

        // Cell factory para que se vea un ComboBox
        colEstado.setCellFactory(ComboBoxTableCell.forTableColumn(opciones));

        Prescripcion prescripcion = new Prescripcion(new Medicamento("1", "Acetaminofen", "Blanco", "Dolor de cabeza"), 2, "Dia y Noche", 3);
        Prescripcion prescripcion2 = new Prescripcion(new Medicamento("2", "Ibuprofeno", "Blanco", "Dolor de cabeza"), 3, "Dia y Noche", 4);
        List<Prescripcion> lista = new ArrayList<>();
        lista.add(prescripcion);
        lista.add(prescripcion2);
        tblBuscarPacientesDespacho.setItems(FXCollections.observableArrayList(
                new Receta(new Paciente("1", "Juan", 12, LocalDate.of(1995,2,21)), lista , LocalDate.of(2025, 9, 2), true)
        ));
    }
}
