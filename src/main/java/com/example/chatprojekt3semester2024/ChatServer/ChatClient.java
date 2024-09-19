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

    public ChatClient(String host, int port, String clientId) {
        try {
            this.socket = new Socket(host, port); // Forbind til serveren via IP-adresse
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

    // Send beskeder til serveren i det specificerede format
    public void sendMessage(String messageContent, String messageType) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        String formattedMessage = clientId + "|" + timestamp + "|" + messageType + "|" + messageContent;
        out.println(formattedMessage);
    }
}