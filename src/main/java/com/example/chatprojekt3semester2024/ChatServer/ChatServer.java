package com.example.chatprojekt3semester2024.ChatServer;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChatServer {
    private static Set<ClientHandler> clientHandlers = new HashSet<>();

    public static void main(String[] args) {
        int port = 1234; // Porten som serveren lytter på
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Chat server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Broadcast til alle klienter
    public static void broadcastMessage(String message, ClientHandler excludeUser) {
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler != excludeUser) {
                clientHandler.sendMessage(message);
            }
        }
    }

    // Unicast til en specifik klient
    public static void unicastMessage(String message, String recipientId) {
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.getClientId().equals(recipientId)) {
                clientHandler.sendMessage(message);
            }
        }
    }

    // Fjern en klienthandler når de forlader chatten
    public static void removeClient(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
    }
}