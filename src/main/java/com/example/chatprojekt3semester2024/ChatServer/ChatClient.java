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
    public void sendFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            System.out.println("File does not exist.");

        }
        try (Socket fileSocket = new Socket("localhost", 5000);
             FileInputStream fileInputStream = new FileInputStream(file);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
             OutputStream outputStream = fileSocket.getOutputStream();
             PrintWriter printWriter = new PrintWriter(outputStream, true)) {

            // Send metadata (filnavn og filstørrelse)
            printWriter.println(file.getName());
            printWriter.println(file.length());

            // Send fildata
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            System.out.println("File has been sent.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}