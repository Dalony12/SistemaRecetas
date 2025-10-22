package com.example.sistemarecetas.controller.MedicoApplication;

import com.example.sistemarecetas.Model.Medicamento;
import com.example.sistemarecetas.Model.Paciente;
import com.example.sistemarecetas.Model.Prescripcion;
import com.example.sistemarecetas.Model.Receta;
import com.example.sistemarecetas.logica.MedicamentoLogica;
import com.example.sistemarecetas.logica.PacienteLogica;
import com.example.sistemarecetas.logica.RecetaLogica;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrescripcionController {

    private static PrescripcionController instance;

    public static PrescripcionController getInstance() {
        if (instance == null) instance = new PrescripcionController();
        return instance;
    }

    @FXML private Button btnEliminarMedicamento;
    @FXML private DatePicker dtpFechaRetiroPres;
    @FXML private TextField txtNombrePacientePresc;
    @FXML private TableView<Prescripcion> tblMedicamentoReceta;
    @FXML private TableColumn<Prescripcion, String> colMedicamento;
    @FXML private TableColumn<Prescripcion, String> colPresentacion;
    @FXML private TableColumn<Prescripcion, Integer> colCantidad;
    @FXML private TableColumn<Prescripcion, String> colIndicaciones;
    @FXML private TableColumn<Prescripcion, Integer> colDuracion;

    @FXML private ProgressIndicator progressIndicator;

    private RecetaLogica recetasLogica;
    private PacienteLogica pacientesLogica;
    private MedicamentoLogica medicamentoLogica;
    private List<Prescripcion> listaMedicamentos = new ArrayList<>();
    private Receta receta = null;
    private Medicamento medicamento = null;
    private Paciente paciente = null;

    public PrescripcionController() { instance = this; }

    @FXML
    public void initialize() {
        try {
            // Inicializamos la lógica usando BD
            recetasLogica = new RecetaLogica();
            pacientesLogica = new PacienteLogica();
            medicamentoLogica = new MedicamentoLogica();

            tblMedicamentoReceta.setItems(FXCollections.observableArrayList(listaMedicamentos));

            // Configurar columnas
            colMedicamento.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMedicamento().getNombre()));
            colPresentacion.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMedicamento().getPresentacion()));
            colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
            colIndicaciones.setCellValueFactory(new PropertyValueFactory<>("indicaciones"));
            colDuracion.setCellValueFactory(new PropertyValueFactory<>("duracionDias"));

            tblMedicamentoReceta.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
                btnEliminarMedicamento.setDisable(newSel == null);
            });

            dtpFechaRetiroPres.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    if (date.isBefore(LocalDate.now())) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ffc0cb;");
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refrescarDatos() {
        listaMedicamentos.clear();
        tblMedicamentoReceta.getItems().clear();
        receta = null;
        medicamento = null;
        paciente = null;
        dtpFechaRetiroPres.setValue(null);
        txtNombrePacientePresc.clear();
    }

    @FXML
    public void abrirBuscarPaciente(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sistemarecetas/View/MedicoView/buscarPacienteTab.fxml"));
            Parent root = loader.load();

            BuscarPacienteController controller = loader.getController();
            controller.setListaPacientes(pacientesLogica.findAll()); // BD

            Stage stage = crearVentanaModal(root, "Buscar Paciente");
            stage.setOnHiding(event -> {
                Paciente seleccionado = controller.getPacienteSeleccionado();
                if (seleccionado != null) {
                    this.paciente = seleccionado;
                    txtNombrePacientePresc.setText(seleccionado.getNombre());
                }
            });
            stage.showAndWait();

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo abrir la ventana de búsqueda de pacientes.");
        }
    }

    @FXML
    public void abrirBuscarMedicamento(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sistemarecetas/View/MedicoView/buscarMedicamento.fxml"));
            Parent root = loader.load();

            BuscarMedicamentoController controller = loader.getController();
            controller.setListaMedicamentos(medicamentoLogica.findAll()); // BD

            Stage stage = crearVentanaModal(root, "Buscar Medicamento");
            stage.setOnHiding(event -> {
                Medicamento seleccionado = controller.getMedicamentoSeleccionado();
                if (seleccionado != null) {
                    this.medicamento = seleccionado;
                    abrirAsignacionDatos();
                }
            });
            stage.showAndWait();

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo abrir la ventana de búsqueda de medicamentos.");
        }
    }

    @FXML
    public void abrirAsignacionDatos() {
        if (medicamento == null) {
            mostrarAlerta("Advertencia", "Primero debe seleccionar un medicamento");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sistemarecetas/View/MedicoView/asignacionMedicamento.fxml"));
            Parent root = loader.load();

            AsignacionDatosController controller = loader.getController();
            Stage stage = crearVentanaModal(root, "Asignar Datos del Medicamento");

            stage.setOnHiding(event -> {
                if (controller.getCantidadMedicamento() > 0 &&
                        controller.getDuracionMedicamento() > 0 &&
                        !controller.getIndicacionesMedicamento().trim().isEmpty()) {

                    Prescripcion prescripcion = new Prescripcion(
                            medicamento,
                            controller.getCantidadMedicamento(),
                            controller.getIndicacionesMedicamento(),
                            controller.getDuracionMedicamento()
                    );
                    tblMedicamentoReceta.getItems().add(prescripcion);
                    listaMedicamentos.add(prescripcion);
                    medicamento = null;
                }
            });
            stage.showAndWait();

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo abrir la ventana de asignación.");
        }
    }


    @FXML
    public void guardarPrescripcion(ActionEvent actionEvent) {
        try {
            LocalDate fechaRetiro = dtpFechaRetiroPres.getValue();
            if (paciente == null || fechaRetiro == null || listaMedicamentos.isEmpty()) {
                mostrarAlerta("Campos incompletos", "Debe seleccionar un paciente, fecha y al menos un medicamento.");
                return;
            }

            if (receta == null) {
                receta = new Receta(null,paciente, listaMedicamentos, fechaRetiro);
                recetasLogica.create(receta); //BD
            } else {
                receta.setPaciente(paciente);
                receta.setMedicamentos(listaMedicamentos);
                receta.setFechaRetiro(fechaRetiro);
                recetasLogica.update(receta); // BD
            }

            mostrarAlerta("Éxito", "Receta guardada correctamente.");
            limpiarPrescripcion(actionEvent);

        } catch (Exception e) {
            mostrarAlerta("Error al guardar receta", e.getMessage());
        }
    }

    @FXML
    public void limpiarPrescripcion(ActionEvent actionEvent) {
        dtpFechaRetiroPres.setValue(null);
        txtNombrePacientePresc.clear();
        tblMedicamentoReceta.getItems().clear();
        listaMedicamentos.clear();
        receta = null;
        medicamento = null;
        paciente = null;
    }

    @FXML
    public void eliminarUnMedicamento(ActionEvent actionEvent) {
        Prescripcion seleccionada = tblMedicamentoReceta.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            tblMedicamentoReceta.getItems().remove(seleccionada);
            listaMedicamentos.remove(seleccionada);
            btnEliminarMedicamento.setDisable(true);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private Stage crearVentanaModal(Parent root, String titulo) {
        Stage stage = new Stage();
        stage.setTitle(titulo);
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        return stage;
    }
}
