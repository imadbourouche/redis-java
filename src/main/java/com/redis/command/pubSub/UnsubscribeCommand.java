package com.redis.command.pubSub;

import com.redis.resp.parser.RespBuilder;
import com.redis.command.Command;

import java.net.Socket;
import java.util.Arrays;

public class UnsubscribeCommand implements Command {

    @Override
    public String execute(String[] args, Socket clientSocket) {
        if(args.length != 2){
            return RespBuilder.error("ERR usage: UNSUBSCRIBE <channel>");
        }
        String channel = args[1];
        int numSubscriptions = PubSubManager.unsubscribe(channel, clientSocket);
        return RespBuilder.array(Arrays.asList("unsubscribe", channel, numSubscriptions));
    }
}
