package com.example.chatprojekt3semester2024.ChatServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String clientId;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Modtag klientens ID (som kommer fra login-systemet)
            this.clientId = in.readLine();
            System.out.println(clientId + " joined the chat.");

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Received from " + clientId + ": " + message);
                ChatServer.broadcastMessage(clientId + ": " + message, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ChatServer.removeClient(this);
            System.out.println(clientId + " has left the chat.");
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}