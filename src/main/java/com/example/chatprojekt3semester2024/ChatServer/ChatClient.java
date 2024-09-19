package com.example.chatprojekt3semester2024.ChatServer;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String clientId;

    // Constructor til at oprette forbindelse til serveren
    public ChatClient(String host, int port, String clientId) {
        try {
            // Opret en socket-forbindelse til serveren via den angivne IP-adresse og port
            this.socket = new Socket(host, port);
            this.clientId = clientId;

            // Åbn output- og input-streams
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send klientens ID til serveren ved oprettelse af forbindelse
            out.println(clientId);

            // Start en tråd til at læse beskeder fra serveren
            new Thread(() -> {
                try {
                    String message;
                    // Læs beskeder fra serveren og vis dem på klientens skærm
                    while ((message = in.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Metode til at sende beskeder til serveren
    public void sendMessage(String messageContent, String messageType) {
        // Formaterer beskeden i det specificerede format
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        String formattedMessage = clientId + "|" + timestamp + "|" + messageType + "|" + messageContent;
        out.println(formattedMessage);
    }

    // Main-metode til at starte klienten fra kommandolinjen
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Brug: java ChatClient <server-ip> <port> <client-id>");
            return;
        }

        // Læs argumenter fra kommandolinjen
        String serverIp = args[0];  // IP-adressen på serveren
        int port = Integer.parseInt(args[1]);  // Porten som serveren lytter på
        String clientId = args[2];  // Klientens ID (brugernavn)

        // Opret en ny instans af ChatClient
        ChatClient client = new ChatClient(serverIp, port, clientId);

        // Brug et simpelt loop til at sende beskeder via kommandolinjen
        try (BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))) {
            String userInput;
            while ((userInput = consoleInput.readLine()) != null) {
                // Send brugers indtastede besked til serveren som en "TEXT" besked
                client.sendMessage(userInput, "TEXT");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}