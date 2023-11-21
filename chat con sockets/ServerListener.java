package com.mycompany.chatclient;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ServerListener implements Runnable {
    private Socket socket;

    public ServerListener(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            Scanner scanner = new Scanner(socket.getInputStream());

            while (scanner.hasNextLine()) {
                String message = scanner.nextLine();
                System.out.println(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}