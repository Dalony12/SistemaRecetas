package com.example.sistemarecetas.MedicoApplication;

import Gestores.GestorMedicamentos;
import Model.Medicamento;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;


public class BuscarMedicamentoController {
    @FXML private ComboBox<String> cmbFiltrarMedicamentoPresc;
    @FXML private TextField txtBuscarMedicamentoPresc;
    @FXML private Button btnCancelarMedicamento;


    //Tabla
    private Medicamento medicamento;
    @FXML private TableView<Medicamento> tblMedicamentoPresc;
    @FXML private TableColumn<Medicamento, String> colCodigo;
    @FXML private TableColumn<Medicamento, String> colNombre;
    @FXML private TableColumn<Medicamento, String> colPresentacion;

    // Lista observable + gestor
    private ObservableList<Medicamento> listaObservable;
    private  GestorMedicamentos gestorMedicamentos= GestorMedicamentos.getInstancia();


    @FXML
    public void initialize() {

        cmbFiltrarMedicamentoPresc.setItems(FXCollections.observableArrayList("ID", "Nombre"));
        cmbFiltrarMedicamentoPresc.setValue("Nombre");

        listaObservable = FXCollections.observableArrayList(gestorMedicamentos.getMedicamentos());
        tblMedicamentoPresc.setItems(listaObservable);

        // Configurar columnas
        colCodigo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCodigo()));
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        colPresentacion.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPresentacion()));

        // Listener para texto de búsqueda
        txtBuscarMedicamentoPresc.textProperty().addListener((obs, oldVal, newVal) -> filtrarMedicamentos());

        // BONUS: Listener para cambio de tipo de filtro
        cmbFiltrarMedicamentoPresc.setOnAction(e -> filtrarMedicamentos());

    }

    public Medicamento getMedicamento() {
        return medicamento;
    }

    @FXML
    private void filtrarMedicamentos() {
        String filtro = cmbFiltrarMedicamentoPresc.getValue();
        String texto = txtBuscarMedicamentoPresc.getText().trim().toLowerCase();

        ObservableList<Medicamento> filtrados = FXCollections.observableArrayList();

        for (Medicamento med : listaObservable) {
            if (filtro.equals("ID") && med.getCodigo().toLowerCase().contains(texto)) {
                filtrados.add(med);
            } else if (filtro.equals("Nombre") && med.getNombre().toLowerCase().contains(texto)) {
                filtrados.add(med);
            }
        }

        tblMedicamentoPresc.setItems(filtrados);
    }


    @FXML
    private void guardarMedicamento() {
        try {
            medicamento = tblMedicamentoPresc.getSelectionModel().getSelectedItem();
            if (medicamento == null) {
                mostrarAlerta("Campos incompletos", "Debe llenar todos los campos del formulario");
                return;
            }
            else {
                Stage stage = (Stage) txtBuscarMedicamentoPresc.getScene().getWindow();
                stage.close();
            }
        } catch (Exception e) {
            mostrarAlerta("Error al guardar los datos", e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    private void cancelarBusqueda() {
        txtBuscarMedicamentoPresc.clear();
        tblMedicamentoPresc.setItems(listaObservable); // Restaurar la lista completa

        // Cerrar la ventana actual
        Stage stage = (Stage) btnCancelarMedicamento.getScene().getWindow();
        stage.close();


    }

}
