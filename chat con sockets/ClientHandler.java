package com.mycompany.chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private PrintWriter writer;
    private String clientName;

    public ClientHandler(Socket clientSocket, PrintWriter writer, String clientName) {
        this.clientSocket = clientSocket;
        this.writer = writer;
        this.clientName = clientName;
    }

    @Override
    public void run() {
        try {
            Scanner scanner = new Scanner(clientSocket.getInputStream());

            while (scanner.hasNextLine()) {
                String message = scanner.nextLine();

                // Verifica si el mensaje enviado es exit
                if ("exit".equalsIgnoreCase(message)) {
                    break;
                }

                // Broadcast envia el mensaje a los otros clientes excepto al que lo envio
                Chat.broadcast(clientName + ": " + message, writer, clientName);



            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // elimina un cliente cuando se desconecta
            Chat.removeClient(writer, clientName);
        }
    }
}



