package com.example.sistemarecetas.servicios;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.*;


public class ClinicaChatServer {
    private final int port;
    private final Map<String, UsuarioHandler> clients = new ConcurrentHashMap<>();
    private static final Logger LOGGER = Logger.getLogger(ClinicaChatServer.class.getName());

    public ClinicaChatServer(int port) {
        this.port = port;
        configureLogger();
    }

    public void configureLogger() {
        try {
            LOGGER.setUseParentHandlers(false);
            var fileHandler = new FileHandler("server.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            LOGGER.setLevel(Level.INFO);
        } catch (IOException e) {
            System.err.println("No se ha podido generar la bitacora del servidor");
        }
    }

    public void start() {
        LOGGER.info("Servidor del chat iniciado en el puerto: " + port);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
              while (true) {
                Socket socket = serverSocket.accept();
                UsuarioHandler usuarioHandler = new UsuarioHandler(socket, this);
                usuarioHandler.start();
                LOGGER.info("Nueva conexión aceptada desde " + socket.getRemoteSocketAddress());
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error en el servidor: " + e.getMessage(), e);
        }
    }

    public synchronized void registrar(String nombre, UsuarioHandler handler) {
        if (nombre == null || nombre.isEmpty()) return;
        clients.put(nombre, handler);
        LOGGER.info("Usuario conectado: " + nombre);
        broadcast("[SISTEMA] " + nombre + " se ha conectado.");
        notificarUsuarios();
    }

    public synchronized void remove(String nombre) {
        if (nombre == null) return;
        clients.remove(nombre);
        LOGGER.info("Usuario desconectado: " + nombre);
        broadcast("[SISTEMA] " + nombre + " ha salido del chat.");
        notificarUsuarios();
    }

    public synchronized void broadcast(String mensaje) {
        for (UsuarioHandler c : clients.values()) {
            c.send(mensaje);
        }
        LOGGER.info("Broadcast enviado: " + mensaje);
    }

    public void enviarPrivado(String remitente, String destinatario, String mensaje) {
        UsuarioHandler receptor = clients.get(destinatario);
        if (receptor != null) {
            receptor.send("[PRIVADO]" + remitente + ":" + mensaje);
            LOGGER.info("Mensaje privado de " + remitente + " a " + destinatario + ": " + mensaje);
        } else {
            // si no existe, notificar al remitente
            UsuarioHandler origen = clients.get(remitente);
            if (origen != null)
                origen.send("[SISTEMA] Usuario " + destinatario + " no está conectado.");
            LOGGER.warning("Intento de privado a usuario inexistente: " + destinatario);
        }
    }

    public synchronized void notificarUsuarios() {
        String lista = String.join(",", clients.keySet());
        for (UsuarioHandler c : clients.values()) {
            c.send("[USUARIOS]" + lista);
        }
        LOGGER.info("Lista de usuarios actualizada: " + lista);
    }

    public static void main(String[] args) {
        new ClinicaChatServer(6000).start();
    }

}
