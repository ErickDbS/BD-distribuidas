package com.mycompany.chat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Chat {
    private static final int PORT = 12345;
    private static List<PrintWriter> clients = new ArrayList<>();
    private static List<String> clientNames = new ArrayList<>();
    private static List<String> messageHistory = new ArrayList<>();
    private static Lock lock = new ReentrantLock();
    private static int activeClients = 0;
    private static final String HISTORY_DIRECTORY = "chat_history/";
    private static String historyFileName;

    public static void main(String[] args) {
        loadMessageHistory();  // Carga el historial de mensajes al iniciar el servidor

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

                // Lee el nombre del cliente
                Scanner scanner = new Scanner(clientSocket.getInputStream());
                String clientName = scanner.nextLine();

                lock.lock();
                try {
                    // Asegúrate de que el cliente no se haya agregado antes
                    if (!clientNames.contains(clientName)) {
                        clients.add(writer);
                        clientNames.add(clientName);
                        activeClients++;

                        // Envía los mensajes del historial después de que el cliente haya ingresado su nombre
                        for (String message : messageHistory) {
                            writer.println(message);
                        }

                        // Broadcast envía mensaje de bienvenida
                        String welcomeMessage = "Bienvenido al chat grupal, " + clientName + "! Escribe 'exit' para abandonar la sala.";
                        Chat.broadcast(welcomeMessage, writer, clientName);

                        new Thread(new ClientHandler(clientSocket, writer, clientName)).start();
                    } else {
                        // Si el cliente ya existe, cierra la conexión sin procesar más
                        writer.println("Ya hay un cliente con el mismo nombre. Conéctate con un nombre diferente.");
                        clientSocket.close();
                    }
                } finally {
                    lock.unlock();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcast(String message, PrintWriter sender, String senderName) {
        lock.lock();
        try {
            messageHistory.add(message);  // Añade mensajes al historial
            saveMessageHistory();  // Guarda el historial de mensajes

            for (PrintWriter writer : clients) {
                if (writer != sender) {
                    writer.println(message);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public static void removeClient(PrintWriter clientWriter, String clientName) {
        lock.lock();
        try {
            clients.remove(clientWriter);
            clientNames.remove(clientName);
            activeClients--;

            String leaveMessage = clientName + " abandonó la sala.";
            Chat.broadcast(leaveMessage, null, null);

            // Si no hay clientes activos, cierra el servidor
            if (activeClients == 0) {
                System.out.println("Cerrando el servidor...");
                System.exit(0);
            }
        } finally {
            lock.unlock();
        }
    }

    private static void saveMessageHistory() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(historyFileName, true))) {
            for (String message : messageHistory) {
                writer.println(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadMessageHistory() {
        createHistoryFileName();
        try (BufferedReader reader = new BufferedReader(new FileReader(historyFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                messageHistory.add(line);
            }
        } catch (IOException e) {
            // El archivo puede no existir, eso está bien
        }
    }

    private static void createHistoryFileName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());
        historyFileName = HISTORY_DIRECTORY + "chat_history_" + timestamp + ".txt";
    }
}




