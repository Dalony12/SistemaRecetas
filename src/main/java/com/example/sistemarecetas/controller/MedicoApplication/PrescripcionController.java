package com.example.sistemarecetas.controller.MedicoApplication;

import com.example.sistemarecetas.Model.Medicamento;
import com.example.sistemarecetas.Model.Paciente;
import com.example.sistemarecetas.Model.Prescripcion;
import com.example.sistemarecetas.Model.Receta;
import com.example.sistemarecetas.logica.medicamentos.MedicamentoLogica;
import com.example.sistemarecetas.logica.pacientes.PacientesLogica;
import com.example.sistemarecetas.logica.recetas.RecetasLogica;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrescripcionController {

    @FXML
    private Button btnEliminarMedicamento;
    @FXML
    private DatePicker dtpFechaRetiroPres;
    @FXML
    private TextField txtNombrePacientePresc;
    @FXML
    private TableView<Prescripcion> tblMedicamentoReceta;
    @FXML
    private TableColumn<Prescripcion, String> colMedicamento;
    @FXML
    private TableColumn<Prescripcion, String> colPresentacion;
    @FXML
    private TableColumn<Prescripcion, Integer> colCantidad;
    @FXML
    private TableColumn<Prescripcion, String> colIndicaciones;
    @FXML
    private TableColumn<Prescripcion, Integer> colDuracion;

    private RecetasLogica recetasLogica;
    private PacientesLogica pacientesLogica;
    private MedicamentoLogica medicamentoLogica;
    private List<Prescripcion> listaMedicamentos;
    private Receta receta = null;
    private Medicamento medicamento = null;
    private Paciente paciente = null;


    @FXML
    public void initialize() {
        try {
            // Ruta dinámica al archivo XML
            String rutaXML = Paths.get(System.getProperty("user.dir"), "datos", "recetas.xml").toString();
            File archivo = new File(rutaXML);

            // Crear archivo si no existe
            if (!archivo.exists()) {
                archivo.getParentFile().mkdirs();
                String contenidoInicial = """
                        <?xml version="1.0" encoding="UTF-8"?>
                        <recetas>
                        </recetas>
                        """;
                try (FileWriter writer = new FileWriter(archivo)) {
                    writer.write(contenidoInicial);
                    System.out.println("Archivo recetas.xml creado en: " + rutaXML);
                }
            }

            String rutaPacientesXML = Paths.get(System.getProperty("user.dir"), "datos", "pacientes.xml").toString();
            pacientesLogica = new PacientesLogica(rutaPacientesXML);

            String rutaPacientesXMLMedicamentos = Paths.get(System.getProperty("user.dir"), "datos", "medicamentos.xml").toString();
            medicamentoLogica = new MedicamentoLogica(rutaPacientesXMLMedicamentos);

            // Inicializar lógica y tabla
            recetasLogica = new RecetasLogica(rutaXML);
            listaMedicamentos = new ArrayList<>();
            tblMedicamentoReceta.setItems(FXCollections.observableArrayList());


            colMedicamento.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getMedicamento().getNombre())
            );

            colPresentacion.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getMedicamento().getPresentacion())
            );

            colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
            colIndicaciones.setCellValueFactory(new PropertyValueFactory<>("indicaciones"));
            colDuracion.setCellValueFactory(new PropertyValueFactory<>("duracionDias"));

            tblMedicamentoReceta.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                btnEliminarMedicamento.setDisable(newSelection == null);
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
        }  catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al inicializar", e.getMessage());
        }
    }

    @FXML
    private void limpiarPrescripcion() {
        dtpFechaRetiroPres.setValue(null);
        txtNombrePacientePresc.setText("");
        tblMedicamentoReceta.getItems().clear();

        listaMedicamentos.clear();
        medicamento = null;
        receta = null;
    }

    @FXML
    private void abrirBuscarMedicamento() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sistemarecetas/View/MedicoView/buscarMedicamento.fxml"));
            Parent root = loader.load();

            BuscarMedicamentoController controller = loader.getController();

            // Aquí le pasás la lista de pacientes desde tu lógica modular
            controller.setListaMedicamentos(medicamentoLogica.findAll());

            Stage stage = crearVentanaModal(root, "Buscar Medicamento");

            // cuando se cierre la ventana, recupero el medicamento
            stage.setOnHiding(event -> {
                Medicamento medicamentoSeleccionado = controller.getMedicamento();
                if (medicamentoSeleccionado != null) {
                    this.medicamento = medicamentoSeleccionado;

                    // Se debe de abrir despues de seleccionar el medicamento
                    abrirAsignacionDatos();
                }
            });

            stage.showAndWait();
        }catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir la ventana de búsqueda: " + e.getMessage());
        }
    }

    @FXML
    private void abrirBuscarPaciente() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sistemarecetas/View/MedicoView/buscarPacienteTab.fxml"));
            Parent root = loader.load();

            buscarPacienteController controller = loader.getController();

            // Aquí le pasás la lista de pacientes desde tu lógica modular
            controller.setListaPacientes(pacientesLogica.findAll());


            Stage stage = crearVentanaModal(root, "Buscar Paciente");

            stage.setOnHiding(event -> {
                Paciente seleccionado = controller.getPacienteSeleccionado();
                if (seleccionado != null) {
                    this.paciente = seleccionado;
                    txtNombrePacientePresc.setText(seleccionado.getNombre());
                }
            });

            stage.showAndWait();
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir la ventana de búsqueda de pacientes.");
        }
    }


    @FXML
    private void abrirAsignacionDatos() {
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
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir la ventana de asignación: " + e.getMessage());
        }
    }

    @FXML
    private void guardarPrescripcion() {
        try {
            LocalDate fechaRetiro = dtpFechaRetiroPres.getValue();

            if (paciente == null || fechaRetiro == null) {
                mostrarAlerta("Campos incompletos", "Debe seleccionar un paciente y una fecha de retiro.");
                return;
            }

            if (listaMedicamentos.isEmpty()) {
                mostrarAlerta("Receta vacía", "Debe agregar al menos un medicamento.");
                return;
            }

            if (fechaRetiro.isBefore(LocalDate.now())) {
                mostrarAlerta("Fecha inválida", "No puede seleccionar una fecha anterior a hoy.");
                return;
            }

            if (receta == null) {
                receta = new Receta(paciente, listaMedicamentos, fechaRetiro);
                recetasLogica.create(receta);
            } else {
                receta.setPaciente(paciente);
                receta.setMedicamentos(listaMedicamentos);
                receta.setFechaRetiro(fechaRetiro);
                recetasLogica.create(receta);
            }

            mostrarAlerta("Receta guardada", "La receta fue registrada correctamente.");
            limpiarPrescripcion(); // Opcional: limpiar campos después de guardar

        }
        catch (Exception e) {
            mostrarAlerta("Error al guardar los datos", e.getMessage());
        }
    }

    @FXML
    private void eliminarUnMedicamento() {
        Prescripcion seleccionada = tblMedicamentoReceta.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarAlerta("Sin selección", "Debe seleccionar un medicamento para eliminar.");
            return;
        }

        tblMedicamentoReceta.getItems().remove(seleccionada);
        listaMedicamentos.remove(seleccionada);

        //Desactivar el botón después de eliminar
        btnEliminarMedicamento.setDisable(true);
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
