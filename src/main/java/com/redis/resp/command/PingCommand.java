package com.redis.resp.command;

import com.redis.resp.RespBuilder;

public class PingCommand implements Command {
    @Override
    public String execute(String[] args) {
        return RespBuilder.simpleString("PONG");
    }
}
