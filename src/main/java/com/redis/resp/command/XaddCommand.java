package com.redis.resp.command;

import com.redis.exceptions.InvalidIdException;
import com.redis.resp.RespBuilder;
import com.redis.resp.storage.StreamDataStore;

public class XaddCommand implements Command{
    private final StreamDataStore streamDataStore;

    public XaddCommand(StreamDataStore streamDataStore) {
        this.streamDataStore = streamDataStore;
    }

    @Override
    public String execute(String[] args){
        if(args.length != 5){
            return RespBuilder.error("ERR usage: XADD <keyStream> <id> <key> <values>");
        }
        String streamKey = args[1];
        String id = args[2];
        String key = args[3];
        String value = args[4];
        try{
            return RespBuilder.bulkString(streamDataStore.set(streamKey, id, key, value));
        }catch (InvalidIdException e){
            return RespBuilder.error(e.getMessage());
        }catch (Exception e){
            System.out.println(e.getMessage());
            return RespBuilder.error(e.getMessage());
        }
    }


}
