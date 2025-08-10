package com.redis.resp.command;

import com.redis.resp.RespBuilder;
import com.redis.resp.storage.ListDataStore;
import com.redis.resp.storage.MapDataStore;
import com.redis.resp.storage.StreamDataStore;

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
