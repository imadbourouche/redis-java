package com.redis.command.core;

import com.redis.command.Command;
import com.redis.resp.parser.RespBuilder;
import com.redis.command.pubSub.PubSubManager;

import java.net.Socket;
import java.util.Arrays;

public class PingCommand implements Command {
    @Override
    public String execute(String[] args, Socket clientSocket) {
        if(PubSubManager.ClientContext.isClientInSubscriptionMode(clientSocket)) {
            return RespBuilder.array(Arrays.asList("pong", ""));
        }else{
            return RespBuilder.simpleString("PONG");
        }
    }
}
