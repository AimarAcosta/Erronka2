package com.example.ElorServer2;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import sockets.HiloCliente; // Importamos tu clase de la otra carpeta
import java.net.ServerSocket;
import java.net.Socket;

@Component
public class SocketRunner implements CommandLineRunner {

    // Puerto para los Sockets (Distinto al 8080 de Spring)
    private static final int PUERTO_SOCKETS = 9000;

    @Override
    public void run(String... args) throws Exception {
        // Arrancamos el servidor de sockets en un HILO APARTE
        // Esto es vital para no congelar la aplicación Spring Boot
        new Thread(() -> {
            try {
                ServerSocket servidor = new ServerSocket(PUERTO_SOCKETS);
                System.out.println("=========================================");
                System.out.println("🚀 SERVIDOR SOCKETS ESCUCHANDO EN PUERTO: " + PUERTO_SOCKETS);
                System.out.println("=========================================");

                while (true) {
                    // Esperamos conexiones (bloqueante)
                    Socket cliente = servidor.accept();
                    System.out.println("⚡ Nuevo cliente socket conectado: " + cliente.getInetAddress());
                    
                    // Lanzamos tu HiloCliente para atenderle
                    new HiloCliente(cliente).start();
                }
            } catch (Exception e) {
                System.err.println("❌ Error en el servidor de Sockets: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }
}