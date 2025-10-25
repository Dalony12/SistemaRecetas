package com.example.sistemarecetas.servicios;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class UsuarioChat {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public void conectar(String host, int port, String nombre /*Nombre cliente*/, Consumer<String> onMsg) throws IOException {
        socket = new  Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        //Comenzamos a escuchar los mensajes del servidor
        new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null)
                    onMsg.accept(line);
            } catch (IOException e) {
                onMsg.accept("[SISTEMA] Error, Desconectado del servidor: " + e.getMessage());
            }
        }).start();

        out.println(nombre);

    }

    public void enviarMensaje(String mensaje) {
        if (out != null) out.println(mensaje);
    }

    public void close() throws IOException {
        if (socket != null) socket.close();
    }
}
