package com.example.sistemarecetas.controller.FarmaceuticoApplication;

import com.example.sistemarecetas.Model.Prescripcion;
import com.example.sistemarecetas.Model.Receta;
import com.example.sistemarecetas.logica.RecetaLogica;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.Region;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class DespachoController {

    private static DespachoController instance;
    public DespachoController() { instance = this; }
    public static DespachoController getInstance() { return instance; }

    @FXML private TableView<Receta> tblBuscarPacientesDespacho;
    @FXML private TableColumn<Receta, String> colNombre;
    @FXML private TableColumn<Receta, String> colID;
    @FXML private TableColumn<Receta, String> colPrescripciones;
    @FXML private TableColumn<Receta, String> colFechaConfeccion;
    @FXML private TableColumn<Receta, String> colFechaRetiro;
    @FXML private TableColumn<Receta, String> colEstado;
    @FXML private ComboBox<String> cmbFiltrarPaciencteDespacho;
    @FXML private TextField txtBuscarClienetDespacho;

    private ObservableList<Receta> listaObservable;
    private RecetaLogica recetasLogica;

    @FXML
    public void initialize() {
        try {
            // Inicializa la lógica de recetas con BD
            recetasLogica = new RecetaLogica();

            // Configurar filtro
            if (cmbFiltrarPaciencteDespacho != null) {
                cmbFiltrarPaciencteDespacho.setItems(FXCollections.observableArrayList("Identificación", "Nombre"));
                cmbFiltrarPaciencteDespacho.setValue("Nombre");
            }

            configurarColumnas();
            configurarBindings();
            refrescarTabla();
        } catch (Exception ex) {
            logExceptionAndShowAlert("Error en initialize de DespachoController", ex);
        }
    }

    private void configurarColumnas() {
        try {
            if (colNombre == null || colID == null) return;

            colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPaciente().getNombre()));
            colID.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPaciente().getIdentificacion()));

            colPrescripciones.setCellValueFactory(c -> {
                List<Prescripcion> lista = c.getValue().getMedicamentos();
                StringBuilder sb = new StringBuilder();
                for (Prescripcion p : lista) {
                    sb.append(p.getMedicamento().getNombre())
                            .append(" (Cant=").append(p.getCantidad())
                            .append(", Días=").append(p.getDuracionDias())
                            .append(", ").append(p.getIndicaciones())
                            .append("), ");
                }
                if (sb.length() > 0) sb.setLength(sb.length() - 2);
                return new SimpleStringProperty(sb.toString());
            });

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

            colFechaConfeccion.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaConfeccion().toString()));
            colFechaRetiro.setCellValueFactory(c -> new SimpleStringProperty(
                    c.getValue().getFechaRetiro() != null ? c.getValue().getFechaRetiro().toString() : "")
            );

            ObservableList<String> opciones = FXCollections.observableArrayList("En proceso", "Lista", "Entregada");
            colEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstado()));
            colEstado.setCellFactory(ComboBoxTableCell.forTableColumn(opciones));

            if (tblBuscarPacientesDespacho != null) {
                tblBuscarPacientesDespacho.setEditable(true);
            }

            colEstado.setOnEditCommit(event -> {
                Receta receta = event.getRowValue();
                if (receta != null) {
                    receta.setEstado(event.getNewValue());
                    cambiarEstado(receta);
                    refrescarTabla();
                }
            });
        } catch (Exception ex) {
            logExceptionAndShowAlert("Error al configurar columnas en DespachoController", ex);
        }
    }

    private void configurarBindings() {
        try {
            if (txtBuscarClienetDespacho != null)
                txtBuscarClienetDespacho.textProperty().addListener((obs, o, n) -> filtrarPacientes());
            if (cmbFiltrarPaciencteDespacho != null)
                cmbFiltrarPaciencteDespacho.setOnAction(e -> filtrarPacientes());
        } catch (Exception ex) {
            logExceptionAndShowAlert("Error al configurar bindings en DespachoController", ex);
        }
    }

    public void refrescarTabla() {
        try {
            if (recetasLogica == null) recetasLogica = new RecetaLogica();
            List<Receta> todas = recetasLogica.findAll(); // obtiene todas las recetas desde BD
            listaObservable = FXCollections.observableArrayList(todas);
            if (tblBuscarPacientesDespacho != null) {
                tblBuscarPacientesDespacho.setItems(listaObservable);
                filtrarPacientes();
            }
        } catch (Exception ex) {
            logExceptionAndShowAlert("Error al refrescar tabla en DespachoController", ex);
        }
    }

    private void filtrarPacientes() {
        try {
            if (cmbFiltrarPaciencteDespacho == null || txtBuscarClienetDespacho == null || listaObservable == null) return;

            String filtro = cmbFiltrarPaciencteDespacho.getValue();
            String texto = txtBuscarClienetDespacho.getText().trim().toLowerCase();

            if (texto.isEmpty()) {
                tblBuscarPacientesDespacho.setItems(listaObservable);
                return;
            }

            ObservableList<Receta> filtradas = FXCollections.observableArrayList();

            for (Receta receta : listaObservable) {
                String id = receta.getPaciente().getIdentificacion().toLowerCase();
                String nombre = receta.getPaciente().getNombre().toLowerCase();

                if ((filtro.equals("Identificación") && id.contains(texto)) ||
                        (filtro.equals("Nombre") && nombre.contains(texto))) {
                    filtradas.add(receta);
                }
            }

            tblBuscarPacientesDespacho.setItems(filtradas);
        } catch (Exception ex) {
            logExceptionAndShowAlert("Error al filtrar en DespachoController", ex);
        }
    }

    private void cambiarEstado(Receta recetaSeleccionada) {
        if (recetaSeleccionada == null) return;
        try {
            recetasLogica.update(recetaSeleccionada); // actualiza estado en BD
            mostrarAlerta(Alert.AlertType.INFORMATION, "Estado actualizado",
                    "La receta de " + recetaSeleccionada.getPaciente().getNombre() +
                            " cambió a: " + recetaSeleccionada.getEstado());
        } catch (Exception e) {
            logExceptionAndShowAlert("Error al cambiar estado en DespachoController", e);
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        if (tipo == null) return;
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

    private void logExceptionAndShowAlert(String titulo, Exception ex) {
        ex.printStackTrace();
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        mostrarAlerta(Alert.AlertType.ERROR, titulo, ex.getMessage() != null ? ex.getMessage() : ex.toString());
    }
}
