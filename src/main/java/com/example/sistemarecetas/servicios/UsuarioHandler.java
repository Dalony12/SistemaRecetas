package com.example.sistemarecetas.servicios;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsuarioHandler extends Thread {
    private Socket socket;
    private ClinicaChatServer server;
    private BufferedReader in;
    private PrintWriter out;
    private String nombre;

    private static final Logger LOGGER = Logger.getLogger(UsuarioHandler.class.getName());

    public UsuarioHandler(Socket socket, ClinicaChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    public void send (String msg) {
        if (out != null) {
            out.println(msg);
        }
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Bienvenido al chat de la clínica. Escribe tu nombre para iniciar sesión");
            nombre = in.readLine();

            if (nombre == null || nombre.isBlank()) {
                out.println("[SISTEMA] Nombre inválido. Conexión cerrada.");
                socket.close();
                return;
            }

            server.registrar(nombre, this);

            String mensaje;
            while ((mensaje = in.readLine()) != null) {
                if (mensaje.startsWith("/privado")) {
                    String[] partes = mensaje.split(" ", 3);
                    if (partes.length == 3)
                        server.enviarPrivado(nombre, partes[1], partes[2]);
                } else {
                    server.broadcast(nombre + ": " + mensaje);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Conexion finalizada" + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Error cerrando el socket: " + e.getMessage());
            }
            server.remove(nombre);
        }
    }
}
