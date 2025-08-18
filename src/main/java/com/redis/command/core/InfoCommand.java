package com.redis.command.core;

import com.redis.command.Command;
import com.redis.replication.ReplicationManager;
import com.redis.resp.parser.RespBuilder;
import com.redis.server.Server;
import com.redis.server.ServerContext;

public class InfoCommand implements Command {

    @Override
    public String execute(String[] args) {
        Server server = ServerContext.getServer();
        String result = "role:" + server.getRole() + "\n" + "master_replid:" + server.getMasterReplId() + "\n" + "master_repl_offset:" + server.getMasterReplOffset();
        return RespBuilder.bulkString(result);
    }
}
