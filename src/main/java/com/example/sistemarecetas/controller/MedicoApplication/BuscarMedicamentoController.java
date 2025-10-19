package com.example.sistemarecetas.controller.MedicoApplication;

import com.example.sistemarecetas.Model.Medicamento;
import com.example.sistemarecetas.logica.MedicamentoLogica;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class BuscarMedicamentoController {

    @FXML private ComboBox<String> cmbFiltrarMedicamentoPresc;
    @FXML private TextField txtBuscarMedicamentoPresc;
    @FXML private Button btnCancelarMedicamento;

    @FXML private TableView<Medicamento> tblMedicamentoPresc;
    @FXML private TableColumn<Medicamento, String> colCodigo;
    @FXML private TableColumn<Medicamento, String> colNombre;
    @FXML private TableColumn<Medicamento, String> colPresentacion;

    private ObservableList<Medicamento> listaObservable;
    private MedicamentoLogica medicamentoLogica;

    private Medicamento medicamentoSeleccionado;

    @FXML
    public void initialize() {
        cmbFiltrarMedicamentoPresc.setItems(FXCollections.observableArrayList("ID", "Nombre"));
        cmbFiltrarMedicamentoPresc.setValue("Nombre");

        // Inicializamos la lógica de medicamentos con base de datos
        medicamentoLogica = new MedicamentoLogica();

        try {
            List<Medicamento> todosMedicamentos = medicamentoLogica.findAll();
            listaObservable = FXCollections.observableArrayList(todosMedicamentos);
            tblMedicamentoPresc.setItems(listaObservable);
        } catch (Exception e) {
            mostrarAlerta("Error al cargar medicamentos", e.getMessage());
        }

        // Configurar columnas
        colCodigo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCodigo()));
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        colPresentacion.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPresentacion()));

        // Listeners para búsqueda
        txtBuscarMedicamentoPresc.textProperty().addListener((obs, oldVal, newVal) -> filtrarMedicamentos());
        cmbFiltrarMedicamentoPresc.setOnAction(e -> filtrarMedicamentos());
    }

    public Medicamento getMedicamentoSeleccionado() {
        return medicamentoSeleccionado;
    }

    @FXML
    private void filtrarMedicamentos() {
        String filtro = cmbFiltrarMedicamentoPresc.getValue();
        String texto = txtBuscarMedicamentoPresc.getText().trim();

        try {
            List<Medicamento> filtrados;
            if (filtro.equals("ID")) {
                filtrados = medicamentoLogica.searchByCodigo(texto);
            } else {
                filtrados = medicamentoLogica.searchByNombre(texto);
            }

            listaObservable.setAll(filtrados);
            tblMedicamentoPresc.setItems(listaObservable);

        } catch (Exception e) {
            mostrarAlerta("Error al filtrar medicamentos", e.getMessage());
        }
    }

    @FXML
    private void guardarMedicamento() {
        medicamentoSeleccionado = tblMedicamentoPresc.getSelectionModel().getSelectedItem();
        if (medicamentoSeleccionado == null) {
            mostrarAlerta("Campos incompletos", "Debe seleccionar un medicamento.");
            return;
        }
        Stage stage = (Stage) txtBuscarMedicamentoPresc.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void cancelarBusqueda() {
        try {
            txtBuscarMedicamentoPresc.clear();
            listaObservable.setAll(medicamentoLogica.findAll());
            tblMedicamentoPresc.setItems(listaObservable);
        } catch (Exception e) {
            mostrarAlerta("Error al restaurar la lista", e.getMessage());
        }
        Stage stage = (Stage) btnCancelarMedicamento.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    public void setListaMedicamentos(List<Medicamento> medicamentos) {
        listaObservable.setAll(medicamentos);
    }
}