package com.example.chatprojekt3semester2024.ChatServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String clientId;

    public ChatClient(String host, int port, String clientId) {
        try {
            this.socket = new Socket(host, port);
            this.clientId = clientId;

            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send klientens ID til serveren
            out.println(clientId);

            // Start en tråd til at læse beskeder fra serveren
            new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        System.out.println(message); // Vis beskeder modtaget fra serveren
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Send beskeder til serveren
    public void sendMessage(String message) {
        out.println(message);
    }
}