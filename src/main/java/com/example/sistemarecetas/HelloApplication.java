package com.example.sistemarecetas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("View/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 517, 255);
        stage.setTitle("Sistema de Recetas");
        stage.setScene(scene);
        stage.show();
    }
}
