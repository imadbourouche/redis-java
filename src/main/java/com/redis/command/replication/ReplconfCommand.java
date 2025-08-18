package com.redis.command.replication;

import com.redis.command.Command;
import com.redis.resp.parser.RespBuilder;
import com.redis.server.ServerContext;
import com.redis.replication.ReplicationManager;

import java.net.Socket;

public class ReplconfCommand implements Command {

    @Override
    public String execute(String[] args, Socket clientSocket) {
        if (args.length < 2) {
            return RespBuilder.error("ERR wrong number of arguments for 'REPLCONF' command");
        }

        String subCommand = args[1].toLowerCase();
        // ReplicationManager replManager = ServerContext.getServer().getReplicationManager();

        switch (subCommand) {
            case "listening-port":
                if (args.length >= 3) {
                    try {
                        int port = Integer.parseInt(args[2]);
                        System.out.println("[Master] Replica listening on port " + port);
                    } catch (NumberFormatException e) {
                        return RespBuilder.error("ERR invalid port for REPLCONF");
                    }
                }
                break;

            case "capa":
                if (args.length >= 3) {
                    String capability = args[2];
                    System.out.println("[Master] Replica capability: " + capability);
                }
                break;

            default:
                System.out.println("[Master] Ignoring REPLCONF " + subCommand);
                break;
        }

        return RespBuilder.simpleString("OK");
    }
}
