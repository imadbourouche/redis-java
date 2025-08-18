package com.redis.command.pubSub;

import com.redis.resp.parser.RespBuilder;
import com.redis.command.Command;

import java.net.Socket;

public class PublishCommand implements Command {

    @Override
    public String execute(String[] args, Socket socket) {
        if(args.length != 3){
            return RespBuilder.error("ERR usage: PUBLISH channel_name message_contents");
        }
        String channel = args[1];
        String content = args[2];
        int numSubs = PubSubManager.publish(channel, content);
        return RespBuilder.integer(numSubs);
    }
}
