package com.redis.command.stream;

import com.redis.exceptions.InvalidIdException;
import com.redis.command.Command;
import com.redis.resp.parser.RespBuilder;
import com.redis.storage.StreamDataStore;

import java.util.HashMap;
import java.util.Map;

public class XaddCommand implements Command {
    private final StreamDataStore streamDataStore;

    public XaddCommand(StreamDataStore streamDataStore) {
        this.streamDataStore = streamDataStore;
    }

    @Override
    public String execute(String[] args){
        if (args.length < 5 || (args.length - 3) % 2 != 0) {
            return RespBuilder.error("ERR usage: XADD <keyStream> <id> <field> <value> [field value ...]");
        }
        String streamKey = args[1];
        String id = args[2];
        Map<String, String> fields = new HashMap<>();
        for (int i = 3; i < args.length; i += 2) {
            String field = args[i];
            String value = args[i + 1];
            fields.put(field, value);
        }
        try{
            return RespBuilder.bulkString(streamDataStore.set(streamKey, id, fields));
        }catch (InvalidIdException e){
            return RespBuilder.error(e.getMessage());
        }catch (Exception e){
            System.out.println(e.getMessage());
            return RespBuilder.error(e.getMessage());
        }
    }


}
