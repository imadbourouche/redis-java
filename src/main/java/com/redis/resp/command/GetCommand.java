package com.redis.resp.command;

import com.redis.resp.RespBuilder;
import com.redis.resp.storage.MapDataStore;

public class GetCommand implements Command {
    private final MapDataStore mapDataStore;

    public GetCommand(MapDataStore store) {
        this.mapDataStore = store;
    }

    @Override
    public String execute(String[] args) {
        if (args.length != 2) {
            return RespBuilder.error("ERR wrong number of arguments for 'get' command");
        }
        String value = mapDataStore.get(args[1]);
        return RespBuilder.bulkString(value);
    }
}
