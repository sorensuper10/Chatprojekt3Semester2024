package com.example.chatprojekt3semester2024.ChatServer;

import java.io.*;
import java.net.*;

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
                if (message.equals("SEND_FILE")) {
                    receiveFile();

                }else {
                    System.out.println("Received from " + clientId + ": " + message);
                    handleMessage(message);
                }
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


    // Håndtering af modtaget besked fra klienten
    private void handleMessage(String message) {
        String[] messageParts = message.split("\\|");
        String senderId = messageParts[0];
        String timestamp = messageParts[1];
        String messageType = messageParts[2];
        String content = messageParts[3];

        switch (messageType) {
            case "TEXT":
                // Broadcast beskeden til alle andre klienter
                String formattedMessage = "[" + timestamp + "] " + senderId + ": " + content;
                ChatServer.broadcastMessage(formattedMessage, this);
                break;

            case "UNICAST":
                // Send beskeden til en specifik klient
                String recipientId = messageParts[4];
                ChatServer.unicastMessage("[" + timestamp + "] " + senderId + ": " + content, recipientId);
                break;

            default:
                System.out.println("Unknown message type: " + messageType);
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String getClientId() {
        return clientId;
    }
    private void receiveFile() {
        try (Socket fileSocket = new Socket(socket.getInetAddress(), 5000)) {
            InputStream inputStream = fileSocket.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String filename = bufferedReader.readLine();
            long fileSize = Long.parseLong(bufferedReader.readLine());

            String outputFilePath = "ServerFiles/" + filename;
            try (FileOutputStream fileOutputStream = new FileOutputStream(outputFilePath);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            long totalBytesRead = 0;

            while ((bytesRead = inputStream.read(buffer)) != -1);
            bufferedOutputStream.write(buffer, 0,bytesRead);
            totalBytesRead += bytesRead;

            if (totalBytesRead >= fileSize) {

            }
            bufferedOutputStream.flush();
                System.out.println("file received" + outputFilePath);

            }catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}