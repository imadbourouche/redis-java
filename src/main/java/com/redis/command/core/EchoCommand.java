package com.redis.command.core;

import com.redis.command.Command;
import com.redis.resp.parser.RespBuilder;

public class EchoCommand implements Command {
    @Override
    public String execute(String[] args) {
        if (args.length < 2) return RespBuilder.error("ERR wrong number of arguments for 'echo'");
        return RespBuilder.bulkString(args[1]);
    }
}
