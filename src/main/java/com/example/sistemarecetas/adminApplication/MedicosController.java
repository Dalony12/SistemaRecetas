package com.example.sistemarecetas.adminApplication;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class MedicosController {

    private static MedicosController instance; // Singleton

    public MedicosController() {
        instance = this; // Se asigna al crear la instancia desde FXMLLoader
    }

    public static MedicosController getInstance() {
        return instance;
    }

    @FXML
    private VBox vBoxPortadaMedicos;

    @FXML
    private TableView<?> tableMedicos;

    public void mostrarListaConAnimacion() {
        if (vBoxPortadaMedicos.isVisible()) {
            // Pausa de 1 segundo antes de iniciar la animación
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.seconds(1));
            pause.setOnFinished(event -> {
                TranslateTransition transition = new TranslateTransition(Duration.seconds(1.5), vBoxPortadaMedicos);
                transition.setToX(-vBoxPortadaMedicos.getWidth() - 20);
                transition.setOnFinished(e -> vBoxPortadaMedicos.setVisible(false));
                transition.play();
            });
            pause.play();
        }
    }

    @FXML
    public void CancelarMedico(ActionEvent actionEvent) { }

    @FXML
    public void EnviarMedico(ActionEvent actionEvent) { }

    @FXML
    public void GuardarMedico(ActionEvent actionEvent) { }

    @FXML
    public void borrarMedico(ActionEvent actionEvent) { }

    @FXML
    public void modificarMedico(ActionEvent actionEvent) { }
}
