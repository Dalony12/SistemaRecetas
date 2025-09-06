package com.example.sistemarecetas.controller.FarmaceuticoApplication;

import com.example.sistemarecetas.Gestores.GestorRecetas;
import com.example.sistemarecetas.Model.Prescripcion;
import com.example.sistemarecetas.Model.Receta;
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

    public void initialize() {
        // Configurar columnas
        colNombre.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPaciente().getNombre()));
        colID.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPaciente().getId()));

        // Prescripciones con tooltip
        colPrescripciones.setCellFactory(tc -> new TableCell<Receta, String>() {
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

        colPrescripciones.setCellValueFactory(cellData -> {
            List<Prescripcion> lista = cellData.getValue().getMedicamentos();
            StringBuilder sb = new StringBuilder();
            for (Prescripcion p : lista) {
                sb.append(p.getMedicamento().getNombre())
                        .append(" (Cant = ")
                        .append(p.getCantidad())
                        .append(", Duración = ")
                        .append(p.getDuracionDias())
                        .append(", ")
                        .append(p.getIndicaciones())
                        .append("), ");
            }
            if (sb.length() > 0) sb.setLength(sb.length() - 2);
            return new SimpleStringProperty(sb.toString());
        });

        colFechaConfeccion.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFechaConfeccion().toString()));
        colFechaRetiro.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getFechaRetiro() != null ? cellData.getValue().getFechaRetiro().toString() : "")
        );
        colEstado.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEstado()));

        // Opciones ComboBox en columna Estado
        ObservableList<String> opciones = FXCollections.observableArrayList("En proceso", "Lista", "Entregado");
        colEstado.setCellFactory(ComboBoxTableCell.forTableColumn(opciones));

        // Habilitar edición
        tblBuscarPacientesDespacho.setEditable(true);
        colEstado.setEditable(true);

        // Evento de edición de estado
        colEstado.setOnEditCommit(event -> {
            Receta receta = event.getRowValue();
            receta.setEstado(event.getNewValue());
            cambiarEstado(receta);
        });

        // 🔹 Poblar tabla con recetas desde el GestorRecetas
        GestorRecetas gestor = GestorRecetas.getInstancia();
        tblBuscarPacientesDespacho.setItems(FXCollections.observableArrayList(gestor.getRecetas()));
    }

    // Método para validar y actualizar estado en el gestor
    private void cambiarEstado(Receta recetaSeleccionada) {
        if (recetaSeleccionada != null) {
            GestorRecetas gestorRecetas = GestorRecetas.getInstancia();
            boolean actualizado = gestorRecetas.actualizarEstadoReceta(recetaSeleccionada, recetaSeleccionada.getEstado());

            if (actualizado) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Actualización de Estado",
                        "El estado de la receta de " + recetaSeleccionada.getPaciente().getNombre() +
                                " ha sido actualizado a: " + recetaSeleccionada.getEstado() + " satdisfactoriamente.");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Estado",
                        "Ocurrió un error del sistema, no se pudo actualizar el estado." +
                                " Por favor, intente nuevamente.");
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
