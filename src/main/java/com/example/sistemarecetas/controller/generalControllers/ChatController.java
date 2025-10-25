package com.example.sistemarecetas.controller.generalControllers;

import com.example.sistemarecetas.domain.UsuarioActual;
import javafx.application.Platform;
import javafx.fxml.FXML;
import com.example.sistemarecetas.servicios.UsuarioChat;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.List;

public class ChatController {

    @FXML private TextArea txtMensajes;
    @FXML private TextField txtMensaje;

    private UsuarioChat usuarioChat;
    List<String> destinatarios;

    public void setUsuarioChat(UsuarioChat usuarioChat) {
        this.usuarioChat = usuarioChat;
    }

    public void inicializarDestinatarios(List<String> destinatarios) {
        this.destinatarios = destinatarios;
        System.out.println("Chat abierto con: " + destinatarios);
    }

    @FXML
    public void onSend() {
        String msg = txtMensaje.getText().trim();
        if (msg.isEmpty()) return;
        usuarioChat.enviarMensaje(msg);
        txtMensaje.clear();
    }

    @FXML
    public void onClose() {
        try {
            usuarioChat.close();
        } catch (Exception ignored) {}
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
