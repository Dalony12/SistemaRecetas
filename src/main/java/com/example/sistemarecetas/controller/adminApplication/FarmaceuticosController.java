package com.example.sistemarecetas.controller.adminApplication;

import com.example.sistemarecetas.Model.Farmaceutico;
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

    private ObservableList<Farmaceutico> listaObservable;
    private FarmaceuticoLogica farmaceutaLogica;

    private String currentMode = "guardar"; // modo activo

    @FXML
    public void initialize() {
        try {
            // Inicializar la lógica usando DB
            farmaceutaLogica = new FarmaceuticoLogica();

            // Cargar lista desde la DB
            listaObservable = FXCollections.observableArrayList(farmaceutaLogica.findAll());
            tableFarmaceuticos.setItems(listaObservable);

            colIDFarma.setCellValueFactory(new PropertyValueFactory<>("id"));
            colNombreFarma.setCellValueFactory(new PropertyValueFactory<>("nombre"));

            // Inicializar modo por defecto
            toggleMode("guardar");

            // Eventos de botones
            btnGuardarFarmaceutico.setOnAction(e -> toggleMode("guardar"));
            btnBorrarFarmaceutico.setOnAction(e -> toggleMode("borrar"));
            btnModificarFarmaceutico.setOnAction(e -> toggleMode("modificar"));
            btnBuscarFarmaceutico.setOnAction(e -> toggleMode("buscar"));

            // Reacción de campos
            txtIDFarmaceuta.textProperty().addListener((obs, oldVal, newVal) -> {
                try {
                    handleIDChange(newVal);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            txtNombreFarmaceuta.textProperty().addListener((obs, oldVal, newVal) -> {
                if (currentMode.equals("buscar")) {
                    try {
                        buscarFarmaceuta();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

        } catch (Exception e) {
            mostrarAlerta("Error al inicializar", e.getMessage());
        }
    }

    public void cargarFarmaceuticos() {
        try {
            if (farmaceutaLogica != null) {
                listaObservable.setAll(farmaceutaLogica.findAll());
            }
        } catch (Exception e) {
            mostrarAlerta("Error al cargar farmacéuticos", e.getMessage());
        }
    }

    private void handleIDChange(String newVal) throws SQLException {
        String identificacion = newVal.startsWith("far-") ? newVal : "far-" + newVal;

        if (currentMode.equals("modificar") || currentMode.equals("buscar") || currentMode.equals("borrar")) {
            Optional<Farmaceutico> encontrado = farmaceutaLogica.findAll().stream()
                    .filter(f -> f.getIdentificacion().equalsIgnoreCase(identificacion))
                    .findFirst();

            if (encontrado.isPresent()) {
                txtNombreFarmaceuta.setText(encontrado.get().getNombre());
                txtNombreFarmaceuta.setEditable(currentMode.equals("modificar"));
            } else {
                if (!currentMode.equals("buscar")) txtNombreFarmaceuta.clear();
            }
        }

        if (currentMode.equals("buscar")) buscarFarmaceuta();
    }

    private void toggleMode(String mode) {
        currentMode = mode;

        btnGuardarFarmaceutico.setSelected(mode.equals("guardar"));
        btnBorrarFarmaceutico.setSelected(mode.equals("borrar"));
        btnModificarFarmaceutico.setSelected(mode.equals("modificar"));
        btnBuscarFarmaceutico.setSelected(mode.equals("buscar"));

        txtIDFarmaceuta.setEditable(true);
        txtNombreFarmaceuta.clear();
        txtIDFarmaceuta.setText("far-");

        switch (mode) {
            case "guardar":
                txtNombreFarmaceuta.setEditable(true);
                break;
            case "borrar":
            case "buscar":
                txtNombreFarmaceuta.setEditable(false);
                break;
            case "modificar":
                txtNombreFarmaceuta.setEditable(false); // se desbloquea al encontrar
                break;
        }
    }

    @FXML
    private void GuardarModificarEliminarFarmaceuta() {
        try {
            String identificacion = txtIDFarmaceuta.getText().trim();
            String nombre = txtNombreFarmaceuta.getText().trim();

            if (identificacion.isEmpty() || nombre.isEmpty()) {
                mostrarAlerta("Campos incompletos", "Debe llenar todos los campos del formulario");
                return;
            }

            Farmaceutico m;

            switch (currentMode) {
                case "guardar":
                    // Nuevo farmacéutico, password por defecto = "user"
                    m = new Farmaceutico(nombre, identificacion);
                    Farmaceutico creado = farmaceutaLogica.create(m); // Inserta en DB y retorna con ID generado
                    listaObservable.add(creado);
                    break;

                case "modificar":
                    // Para modificar, primero debemos obtener el ID real de DB
                    m = farmaceutaLogica.findByIdentificacion(identificacion)
                            .orElseThrow(() -> new RuntimeException("No se encontró farmacéutico con esa identificación"));
                    m.setNombre(nombre); // Actualizamos el nombre
                    farmaceutaLogica.update(m);
                    refrescarTabla();
                    break;

                case "borrar":
                    boolean eliminado = farmaceutaLogica.deleteByIdentificacion(identificacion);
                    if (eliminado) listaObservable.removeIf(x -> x.getIdentificacion().equalsIgnoreCase(identificacion));
                    else mostrarAlerta("No encontrado", "No existe un farmacéutico con esa identificación: " + identificacion);
                    break;
            }

            limpiarCampos();

        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    @FXML
    private void buscarFarmaceuta() throws SQLException {
        String identificacion = txtIDFarmaceuta.getText().trim().toLowerCase();
        String nombre = txtNombreFarmaceuta.getText().trim().toLowerCase();

        List<Farmaceutico> resultado = farmaceutaLogica.findAll().stream()
                .filter(f -> f.getNombre().toLowerCase().contains(nombre) ||
                        f.getIdentificacion().toLowerCase().contains(identificacion))
                .toList();

        listaObservable.setAll(resultado);
    }

    @FXML
    private void limpiarCampos() {
        txtIDFarmaceuta.setText("far-");
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