package com.example.sistemarecetas.controller.MedicoApplication;

import com.example.sistemarecetas.Model.Medicamento;
import com.example.sistemarecetas.Model.Paciente;
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


    //Tabla
    private Medicamento medicamento;
    @FXML private TableView<Medicamento> tblMedicamentoPresc;
    @FXML private TableColumn<Medicamento, String> colCodigo;
    @FXML private TableColumn<Medicamento, String> colNombre;
    @FXML private TableColumn<Medicamento, String> colPresentacion;

    private ObservableList<Medicamento> listaObservable;

    @FXML
    public void initialize() {

        cmbFiltrarMedicamentoPresc.setItems(FXCollections.observableArrayList("ID", "Nombre"));
        cmbFiltrarMedicamentoPresc.setValue("Nombre");

        // Aquí asumimos que listaObservable ya fue cargada desde otro módulo
        listaObservable = FXCollections.observableArrayList(); // Se puede setear externamente
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

    public void setListaMedicamentos(List<Medicamento> medicamentos) {
        listaObservable.setAll(medicamentos);
    }

}
