package com.redis.resp.command;

import com.redis.resp.RespBuilder;
import org.jetbrains.annotations.Nullable;
import java.net.Socket;

public interface Command {
    default String execute(String[] args){
        return "";
    }

    default String execute(String[] args, Socket clientSocket) {
        return execute(args); // fallback to old behavior
    }
}
