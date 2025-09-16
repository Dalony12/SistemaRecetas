package com.example.sistemarecetas;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("View/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 516, 256);
        stage.setTitle("Sistema de Recetas");
        stage.setScene(scene); stage.setResizable(false); stage.centerOnScreen();
        stage.getIcons().add(new Image(Application.class.getResourceAsStream("Images/icono.png")));
        stage.show();
    }
}