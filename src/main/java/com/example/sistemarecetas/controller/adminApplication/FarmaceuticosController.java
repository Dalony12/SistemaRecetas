package com.example.sistemarecetas.controller.adminApplication;

import com.example.sistemarecetas.Model.Farmaceutico;
import com.example.sistemarecetas.Model.Medicamento;
import com.example.sistemarecetas.Model.Paciente;
import com.example.sistemarecetas.controller.Async;
import com.example.sistemarecetas.logica.FarmaceuticoLogica;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.beans.value.ChangeListener;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class FarmaceuticosController {

    private static FarmaceuticosController instance;
    public FarmaceuticosController() { instance = this; }
    public static FarmaceuticosController getInstance() { return instance; }

    @FXML private VBox vBoxPortadaFarmaceuta;
    @FXML private TableView<Farmaceutico> tableFarmaceuticos;
    @FXML private TableColumn<Farmaceutico, String> colIDFarma;
    @FXML private TableColumn<Farmaceutico, String> colNombreFarma;

    @FXML private RadioButton btnGuardarFarmaceutico;
    @FXML private RadioButton btnBorrarFarmaceutico;
    @FXML private RadioButton btnModificarFarmaceutico;
    @FXML private RadioButton btnBuscarFarmaceutico;

    @FXML private TextField txtIDFarmaceuta;
    @FXML private TextField txtNombreFarmaceuta;

    @FXML private ProgressBar progressBar;

    private ObservableList<Farmaceutico> listaObservable;
    private FarmaceuticoLogica farmaceutaLogica;

    private String currentMode = "guardar"; // modo activo
    @FXML
    public void initialize() {
        try {
            farmaceutaLogica = new FarmaceuticoLogica();
            listaObservable = FXCollections.observableArrayList(farmaceutaLogica.findAll());
            tableFarmaceuticos.setItems(listaObservable);

            colIDFarma.setCellValueFactory(new PropertyValueFactory<>("identificacion"));
            colNombreFarma.setCellValueFactory(new PropertyValueFactory<>("nombre"));

            toggleMode("guardar");
            configurarListeners();

        } catch (SQLException e) {
            mostrarAlerta("Error al inicializar", e.getMessage());
        }
    }

    private void configurarListeners() {
        btnGuardarFarmaceutico.setOnAction(e -> toggleMode("guardar"));
        btnBorrarFarmaceutico.setOnAction(e -> toggleMode("borrar"));
        btnModificarFarmaceutico.setOnAction(e -> toggleMode("modificar"));
        btnBuscarFarmaceutico.setOnAction(e -> toggleMode("buscar"));

        txtIDFarmaceuta.textProperty().addListener((obs, oldVal, newVal) -> handleIDChange(newVal));
    }

    private void handleIDChange(String codigo) {
        if (currentMode.equals("guardar")) {
            setFieldEditable(txtNombreFarmaceuta, true);
            return;
        }

        try {
            Farmaceutico m = farmaceutaLogica.findById(codigo);
            if (m != null) {
                txtNombreFarmaceuta.setText(m.getNombre());

                boolean editable = currentMode.equals("modificar");
                setFieldEditable(txtNombreFarmaceuta, editable);

            } else if (!currentMode.equals("buscar")) {
                limpiarCampos(false);
                setFieldEditable(txtNombreFarmaceuta, false);
            }

            if (currentMode.equals("buscar")) buscarFarmaceutico();

        } catch (SQLException e) {
            mostrarAlerta("Error al buscar medicamento", e.getMessage());
        }
    }

    private void setFieldEditable(TextInputControl field, boolean editable) {
        field.setEditable(editable);
        field.setStyle(editable ? "" : "-fx-control-inner-background: #f0f0f0;");
        if (!editable) field.setTooltip(new Tooltip("Campo bloqueado"));
        else field.setTooltip(null);
    }

    private void toggleMode(String mode) {
        currentMode = mode;

        btnGuardarFarmaceutico.setSelected(mode.equals("guardar"));
        btnBorrarFarmaceutico.setSelected(mode.equals("borrar"));
        btnModificarFarmaceutico.setSelected(mode.equals("modificar"));
        btnBuscarFarmaceutico.setSelected(mode.equals("buscar"));

        txtIDFarmaceuta.setEditable(true);
        txtIDFarmaceuta.setStyle("");
        txtIDFarmaceuta.setTooltip(null);

        limpiarCampos();

        switch (mode) {
            case "guardar" -> {
                setFieldEditable(txtNombreFarmaceuta, true);
            }
            case "modificar", "borrar", "buscar" -> {
                setFieldEditable(txtNombreFarmaceuta, false);
            }
        }

        cargarFarmaceuticosAsync();
    }

    @FXML
    private void GuardarModificarEliminarFarmaceuta() {
        try {
            String identificacion = txtIDFarmaceuta.getText().trim();
            String nombre = txtNombreFarmaceuta.getText().trim();

            switch (currentMode) {
                case "guardar" -> {
                    if (identificacion.isEmpty() || nombre.isEmpty()) {
                        mostrarAlerta("Campos incompletos", "Debe llenar ambos campos.");
                        return;
                    }
                    Farmaceutico nuevo = new Farmaceutico(nombre, identificacion);
                    guardarFarmaceuticosAsync(nuevo);
                }
                case "modificar" -> {
                    Optional<Farmaceutico> mod = farmaceutaLogica.findByIdentificacion(identificacion);
                    if (mod.isPresent()) {
                        mod.get().setNombre(nombre);
                        modificarFarmaceuticoAsync(mod.get());
                        refrescarTabla();
                    } else mostrarAlerta("No encontrado", "No existe farmacéutico con esa identificación.");
                }
                case "borrar" -> {
                    if (identificacion.isEmpty()) {
                        mostrarAlerta("Campo vacío", "Debe ingresar una identificación.");
                        return;
                    }
                    eliminarFarmaceuticoAsync(identificacion);
                }
            }

            limpiarCampos();

        } catch (Exception e) { mostrarAlerta("Error", e.getMessage()); }
    }

    @FXML
    private void buscarFarmaceutico() {
        try {
            String identificacion = txtIDFarmaceuta.getText().trim();

            List<Farmaceutico> resultado;
            if (identificacion.isEmpty()) resultado = farmaceutaLogica.findAll();
            else resultado = farmaceutaLogica.searchByCodigo(identificacion);

            listaObservable.setAll(resultado);

        } catch (SQLException e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    //METODOS CON HILOSSSSSS//
    public void cargarFarmaceuticosAsync(){
        progressBar.setVisible(true);
        Async.run(
                () -> {// Esta expresión representa el proceso principal
                    try {
                        // Sobre el proceso principal vamos a ejecutar un hilo con un proceso adicional
                        return farmaceutaLogica.findAll();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                },
                listaClientes -> { // Este es el caso del onSuccess
                    tableFarmaceuticos.getItems().setAll(listaClientes);
                    progressBar.setVisible(false);
                },
                ex -> { // Este es el caso del onError
                    progressBar.setVisible(false);
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Error al cargar la lista de Farmaceuticos.");
                    a.setHeaderText(null);
                    a.setContentText(ex.getMessage());
                    a.showAndWait();
                }
        );
    }

    public void guardarFarmaceuticosAsync(Farmaceutico c) {
        btnGuardarFarmaceutico.setDisable(true);
        progressBar.setVisible(true);

        Async.run(
                () -> { // Este es el proceso principal
                    try {
                        return farmaceutaLogica.create(c);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                },
                guardado -> { // onSuccess
                    progressBar.setVisible(false);
                    btnGuardarFarmaceutico.setDisable(false);
                    tableFarmaceuticos.getItems().add(guardado);
                    new Alert(Alert.AlertType.INFORMATION, "Farmaceutico guardado").showAndWait();
                },
                ex -> { // onError
                    progressBar.setVisible(false);
                    btnGuardarFarmaceutico.setDisable(false);
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("No se pudo guardar el Farmaceutico");
                    a.setHeaderText(null);
                    a.setContentText(ex.getMessage());
                    a.showAndWait();
                }
        );
    }

    public void modificarFarmaceuticoAsync(Farmaceutico f) {
        btnModificarFarmaceutico.setDisable(true);
        progressBar.setVisible(true);

        Async.run(
                () -> { // --- proceso principal (en segundo plano)
                    try {
                        return farmaceutaLogica.update(f);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                },
                actualizado -> { // --- onSuccess
                    progressBar.setVisible(false);
                    btnModificarFarmaceutico.setDisable(false);

                    if (actualizado != null) {
                        new Alert(Alert.AlertType.INFORMATION, "Farmacéutico modificado correctamente").showAndWait();
                        try {
                            refrescarTabla();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        limpiarCampos();
                    } else {
                        new Alert(Alert.AlertType.WARNING, "No se pudo modificar el farmacéutico.").showAndWait();
                    }
                },
                ex -> { // --- onError
                    progressBar.setVisible(false);
                    btnModificarFarmaceutico.setDisable(false);
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("Error al modificar farmacéutico");
                    a.setHeaderText(null);
                    a.setContentText(ex.getMessage());
                    a.showAndWait();
                }
        );
    }

    public void eliminarFarmaceuticoAsync(String identificacion) {
        btnBorrarFarmaceutico.setDisable(true);
        progressBar.setVisible(true);

        Async.run(
                () -> { // --- proceso en segundo plano
                    try {
                        return farmaceutaLogica.deleteByIdentificacion(identificacion);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                },
                eliminado -> { // --- onSuccess
                    progressBar.setVisible(false);
                    btnBorrarFarmaceutico.setDisable(false);

                    if (eliminado) {
                        listaObservable.removeIf(f -> f.getIdentificacion().equalsIgnoreCase(identificacion));
                        new Alert(Alert.AlertType.INFORMATION, "Farmacéutico eliminado correctamente").showAndWait();
                        try {
                            refrescarTabla();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        limpiarCampos();
                    } else {
                        new Alert(Alert.AlertType.WARNING, "No se encontró farmacéutico con esa identificación.").showAndWait();
                    }
                },
                ex -> { // --- onError
                    progressBar.setVisible(false);
                    btnBorrarFarmaceutico.setDisable(false);
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("Error al eliminar farmacéutico");
                    a.setHeaderText(null);
                    a.setContentText(ex.getMessage());
                    a.showAndWait();
                }
        );
    }


    @FXML
    private void limpiarCampos() { limpiarCampos(true); }

    private void limpiarCampos(boolean limpiarCodigo) {
        if (limpiarCodigo) txtIDFarmaceuta.clear();
        txtNombreFarmaceuta.clear();
    }

    public void refrescarTabla() throws SQLException {
        listaObservable.setAll(farmaceutaLogica.findAll());
    }

    public void mostrarListaConAnimacion() {
        if (vBoxPortadaFarmaceuta.isVisible()) {
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(event -> {
                TranslateTransition transition = new TranslateTransition(Duration.seconds(1.5), vBoxPortadaFarmaceuta);
                transition.setToX(-vBoxPortadaFarmaceuta.getWidth() - 20);
                transition.setOnFinished(e -> vBoxPortadaFarmaceuta.setVisible(false));
                transition.play();
            });
            pause.play();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}