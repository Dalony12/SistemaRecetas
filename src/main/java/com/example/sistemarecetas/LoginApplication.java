package com.example.sistemarecetas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginApplication.class.getResource("View/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 516, 256);
        stage.setTitle("Sistema de Recetas");
        stage.setScene(scene);

        // Impide que la ventana sea redimensionable
        stage.setResizable(false);

        // Centrar la ventana en la pantalla
        stage.centerOnScreen();

        stage.show();
    }
}
