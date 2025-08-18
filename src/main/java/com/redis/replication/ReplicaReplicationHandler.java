package com.redis.replication;

import com.redis.server.ServerContext;

import java.io.*;
import java.net.Socket;

public class ReplicaReplicationHandler implements Runnable {
    private final String masterHost;
    private final int masterPort;
    private ReplicationState state = ReplicationState.INIT;

    public ReplicaReplicationHandler(String host, int port) {
        this.masterHost = host;
        this.masterPort = port;
    }

    @Override
    public void run() {
        int port = ServerContext.getServer().getPort();
        try (Socket socket = new Socket(masterHost, masterPort);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            // 1. Send PING
            out.print("*1\r\n$4\r\nPING\r\n");
            out.flush();
            state = ReplicationState.PING_SENT;
            System.out.println("[Replica] Sent PING");
            System.out.println("[Replica] Received: " + in.readLine());

            // 2. REPLCONF listening-port
            out.printf("*3\r\n$8\r\nREPLCONF\r\n$14\r\nlistening-port\r\n$%d\r\n%d\r\n", String.valueOf(port).length(), port);
            out.flush();
            state = ReplicationState.REPLCONF_SENT;
            System.out.println("[Replica] Sent REPLCONF listening-port");
            System.out.println("[Replica] Received: " + in.readLine());

            // 3. REPLCONF capa psync2
            out.print("*3\r\n$8\r\nREPLCONF\r\n$4\r\ncapa\r\n$6\r\npsync2\r\n");
            out.flush();
            System.out.println("[Replica] Sent REPLCONF capa psync2");
            System.out.println("[Replica] Received: " + in.readLine());

            // 4. PSYNC
            out.print("*3\r\n$5\r\nPSYNC\r\n$1\r\n?\r\n$2\r\n-1\r\n");
            out.flush();
            state = ReplicationState.PSYNC_SENT;
            System.out.println("[Replica] Sent PSYNC");
            System.out.println("[Replica] Received: " + in.readLine());

            state = ReplicationState.SYNCED;
        } catch (IOException e) {
            throw new RuntimeException("Replication handshake failed", e);
        }
    }
}
