package com.redis.command.pubSub;

import com.redis.resp.parser.RespBuilder;
import com.redis.command.Command;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SubscribeCommand implements Command {

    public SubscribeCommand() {}

    @Override
    public String execute(String[] args, Socket clientSocket){
        List<String> channels = new ArrayList<>(Arrays.asList(args).subList(1, args.length));
        int numSubscriptions = PubSubManager.subscribe(channels, clientSocket);
        if (channels.size() == 1) {
            return RespBuilder.array(Arrays.asList("subscribe", channels.getFirst(), numSubscriptions));
        }else{
            return RespBuilder.array(Arrays.asList("subscribe", channels, numSubscriptions));
        }
    }
}

