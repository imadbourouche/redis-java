package com.redis.server;

import com.redis.replication.ReplicationManager;
import com.redis.server.clientHandler.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Server {
    private int port = 6379;
    private boolean isReplica = false;
    private String masterHost;
    private int masterPort;
    private String role = "master";
    private String masterReplId;
    private int masterReplOffset = 0;

    private final ReplicationManager replicationManager;

    public Server() {
        this.replicationManager = new ReplicationManager();
    }

    // --- Getters ---
    public int getPort() { return port; }
    public String getRole() { return role; }
    public String getMasterReplId() { return masterReplId; }
    public int getMasterReplOffset() { return masterReplOffset; }
    public ReplicationManager getReplicationManager() { return replicationManager; }

    // --- Setters ---
    public void setPort(int port) { this.port = port; }
    public void setIsReplica(boolean isReplica) { this.isReplica = isReplica; }
    public void setMaster(String masterHost, int masterPort) {
        this.masterHost = masterHost;
        this.masterPort = masterPort;
    }
    public void setRole(String role) { this.role = role; }
    public void generateMasterReplId() { this.masterReplId = generateRandomId(); }
    public void setMasterReplOffset(int master_repl_offset) { this.masterReplOffset = master_repl_offset; }
    public void incrementOffset(long delta) { masterReplOffset += delta;}

    @SuppressWarnings("InfiniteLoopStatement")
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(this.port)) {
            serverSocket.setReuseAddress(true);
            System.out.println("[+] Starting server on port " + this.port);
            if (isReplica) {
                replicationManager.startReplicaHandshake(masterHost, masterPort);
            }

            while (true) {
                Socket clientSocket = serverSocket.accept();
                Thread.startVirtualThread(() -> ClientHandler.handleClientRequest(clientSocket));
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private String generateRandomId() {
        String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random RANDOM = new Random();
        StringBuilder sb = new StringBuilder(40);
        for (int i = 0; i < 40; i++) {
            int randomIndex = RANDOM.nextInt(ALPHANUMERIC.length());
            sb.append(ALPHANUMERIC.charAt(randomIndex));
        }
        return sb.toString();
    }
}
