package com.redis.server.clientHandler;

import com.redis.command.CommandHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientHandler {

    public static void handleClientRequest(Socket clientSocket) {
        try (clientSocket; InputStream inputStream = clientSocket.getInputStream();
             OutputStream outputStream = clientSocket.getOutputStream()) {
            try {
                byte[] buffer = new byte[1024];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    String received = new String(buffer, 0, read).trim();
                    String response = CommandHandler.handle(received, clientSocket);
                    outputStream.write((response).getBytes());
                    outputStream.flush();
                }
            } catch (IOException e) {
                System.out.println("Client disconnected: " + e.getMessage());
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
