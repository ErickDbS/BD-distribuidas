package com.mycompany.chatclient;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
 
public class ChatClient {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);

            // Hilo que maneja los mensajes del servidaor
            Thread serverListenerThread = new Thread(new ServerListener(socket));
            serverListenerThread.start();

         
            Scanner userInputScanner = new Scanner(System.in);

            // PrintWriter que envia los mensajes al servidor
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            System.out.print("Ingresa tu nombre: ");
            String name = userInputScanner.nextLine();
            writer.println(name);

            System.out.println("Bienvenido al chat grupal, " + name + "! Escribe 'exit' para abandonar la sala.");

            while (true) {
                String message = userInputScanner.nextLine();

                if ("exit".equalsIgnoreCase(message)) {
                    break;
                }

                writer.println(message);
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}