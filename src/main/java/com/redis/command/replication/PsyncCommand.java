package com.redis.command.replication;

import com.redis.command.Command;
import com.redis.resp.parser.RespBuilder;
import com.redis.server.Server;
import com.redis.server.ServerContext;
import com.redis.replication.ReplicationManager;

import java.net.Socket;

public class PsyncCommand implements Command {

    @Override
    public String execute(String[] args, Socket clientSocket) {
        Server server = ServerContext.getServer();
        ReplicationManager replManager = ServerContext.getServer().getReplicationManager();

        // Register this socket as a replica
        replManager.addReplica(clientSocket);

        String replId = server.getMasterReplId();
        long offset = server.getMasterReplOffset();

        return RespBuilder.simpleString("FULLRESYNC " + replId + " " + offset);
    }
}
