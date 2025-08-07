package com.redis.server;

import com.redis.clientHandler.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private int port = 6379 ;

    public Server() {}
    public Server(int port) {
        this.port = port;
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void start () {
        try(ServerSocket serverSocket = new ServerSocket(this.port);){
            serverSocket.setReuseAddress(true);
            while(true){
                Socket clientSocket = serverSocket.accept();
                Thread.startVirtualThread(() -> ClientHandler.handleClientRequest(clientSocket));
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
