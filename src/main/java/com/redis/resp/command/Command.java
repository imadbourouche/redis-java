package com.redis.resp.command;

public interface Command {
    String execute(String[] args);
}
