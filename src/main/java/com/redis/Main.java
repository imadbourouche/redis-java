package com.redis;

import com.redis.server.Server;
import com.redis.server.ServerContext;

public class Main {
    public static void main(String[] args) {
        System.out.println("[+] Logs from your program will appear here!");
        var server = new Server();
        checkArguments(args, server);
        ServerContext.setServer(server);
        server.start();
    }

    private static void checkArguments(String[] args, Server server) {
        int i = 0;
        while(i < args.length) {
            String firstArg = args[i];
            String secondArg = args[i + 1];
            switch (firstArg) {
                case "--port":
                    try {
                        int portNumber = Integer.parseInt(secondArg);
                        if (portNumber < 1 || portNumber > 65535) {
                            throw new IllegalArgumentException("Port must be between 1 and 65535");
                        }
                        server.setPort(portNumber);
                    } catch (NumberFormatException e) {
                        System.err.println("Error: Invalid port number '" + secondArg + "'");
                        System.exit(1);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error: " + e.getMessage());
                        System.exit(1);
                    }
                    break;
                case "--replicaof":
                    String[] replicaOf = secondArg.trim().split(" ");
                    String masterHost = replicaOf[0];
                    int masterPort = Integer.parseInt(replicaOf[1]);
                    server.setIsReplica(true);
                    server.setMaster(masterHost, masterPort);
                    server.setRole("slave");
                    server.generateMasterReplId();
                    server.setMasterReplOffset(0);
                    break;
                default:
                    System.err.println("Error: Unknown argument '" + firstArg + "'");
                    System.err.println("Usage: [--port <port> --replicaof <MASTER_HOST> <MASTER_PORT>]");
                    System.exit(1);
            }
            i =  i + 2;
        }
    }
}
