package com.redis.replication;

import com.redis.server.Server;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ReplicationManager {
    private final List<Socket> replicas = new CopyOnWriteArrayList<>();


    // Called when server starts, and it's configured as replica
    public void startReplicaHandshake(String masterHost, int masterPort) {
        System.out.println("[Replication] Starting handshake with master " + masterHost + ":" + masterPort);
        ReplicaReplicationHandler replicaHandler = new ReplicaReplicationHandler(masterHost, masterPort);
        Thread.ofVirtual().start(replicaHandler);
    }

    public void addReplica(Socket replica) {
        replicas.add(replica);
        System.out.println("[+] Registered new replica: " + replica);
    }

    public void removeReplica(Socket replica) {
        replicas.remove(replica);
        System.out.println("[-] Replica removed: " + replica);
    }

    public List<Socket> getReplicas() {
        return replicas;
    }

    // Called after every write command to propagate to replicas
    public void broadcast(String command) {
        System.out.printf("[+] Broadcasting %s to all replicas%n", command);
        for (Socket replica : replicas) {
            try {
                OutputStream out = replica.getOutputStream();
                out.write(command.getBytes());
                out.flush();
            } catch (IOException e) {
                removeReplica(replica);
            }
        }
    }
}

