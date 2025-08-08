package com.redis.resp.command;

import com.redis.resp.RespBuilder;
import com.redis.resp.storage.ListDataStore;

import java.util.ArrayList;

public class LrangeCommand implements Command {
    private final ListDataStore listDataStore;

    public LrangeCommand(ListDataStore store) {
        this.listDataStore = store;
    }

    @Override
    public String execute(String[] args) {
        if  (args.length < 3) {
            return RespBuilder.error("ERR usage: lrange <key> <start_index> <end_index>");
        }
        String key = args[1];
        int startIndex = Integer.parseInt(args[2]);
        int endIndex = Integer.parseInt(args[3]);

        if(!listDataStore.containsKey(key)) return RespBuilder.array(new ArrayList<>());
        ArrayList<String> list = listDataStore.getList(key);

        if (startIndex < 0) {
            startIndex = Math.abs(startIndex);
            if (startIndex > list.size()) startIndex = 0;
            else startIndex = list.size() - startIndex;
        }

        if (endIndex < 0) {
            endIndex = Math.abs(endIndex);
            if (endIndex > list.size()) endIndex = 0;
            else endIndex = list.size() - endIndex;
        }

        if ( startIndex > endIndex || startIndex >= list.size()) return RespBuilder.array(new ArrayList<>());
        if (endIndex >= list.size()) endIndex = list.size() - 1;
        ArrayList<String> result = new ArrayList<String>();

        for (int i = startIndex; i <= endIndex; i++) {
            result.add(list.get(i));
        }
        return RespBuilder.array(result);
    }
}
