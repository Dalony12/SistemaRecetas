package com.example.sistemarecetas.controller.generalControllers;

import com.example.sistemarecetas.domain.UsuarioActual;
import javafx.application.Platform;
import javafx.fxml.FXML;
import com.example.sistemarecetas.servicios.UsuarioChat;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.List;

public class ChatController {

    @FXML private TextArea txtMensajes;
    @FXML private TextField txtMensaje;

    private UsuarioChat chatClient;
    List<String> destinatarios;

    @FXML
    public void initialize() {
        try {
            String nombre = UsuarioActual.getInstancia().getUsuario().getIdentificacion();
            chatClient = new UsuarioChat();
            chatClient.conectar("localhost", 6000, nombre, msg ->
                    Platform.runLater(() -> txtMensajes.appendText(msg + "\n")));
        } catch (Exception e) {
            txtMensajes.appendText("[SISTEMA] Error al conectar: " + e.getMessage() + "\n");
        }
    }

    public void inicializarDestinatarios(List<String> destinatarios) {
        this.destinatarios = destinatarios;
        System.out.println("Chat abierto con: " + destinatarios);
    }

    @FXML
    public void onSend() {
        String msg = txtMensaje.getText().trim();
        if (msg.isEmpty()) return;
        chatClient.enviarMensaje(msg);
        txtMensaje.clear();
    }

    @FXML
    public void onClose() {
        try {
            chatClient.close();
        } catch (Exception ignored) {}
    }
}
