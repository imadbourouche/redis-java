package com.redis.command.core;

import com.redis.command.Command;
import com.redis.resp.parser.RespBuilder;
import com.redis.storage.MapDataStore;
import com.redis.storage.StreamDataStore;

public class TypeCommand implements Command {
    private final MapDataStore mapDataStore;
    private final StreamDataStore streamDataStore;

    public TypeCommand(MapDataStore mapStore, StreamDataStore streamStore) {
        this.mapDataStore = mapStore;
        this.streamDataStore = streamStore;
    }

    @Override
    public String execute(String[] args) {
        if  (args.length != 2) {
            return RespBuilder.error("ERR usage: TYPE <key>");
        }
        String key = args[1];
        if(mapDataStore.containsKey(key)) {
            return RespBuilder.simpleString("string");
        }else if (streamDataStore.containsKey(key)) {
            return RespBuilder.simpleString("stream");
        }else{
            return RespBuilder.simpleString("none");
        }
    }
}
