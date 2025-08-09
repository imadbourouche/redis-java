package com.redis.resp.command;

import com.redis.resp.RespBuilder;
import com.redis.resp.storage.ListDataStore;

public class LlenCommand implements Command {
    private final ListDataStore listDataStore;

    public LlenCommand(ListDataStore store) {
        this.listDataStore = store;
    }

    @Override
    public String execute(String[] args) {
        if  (args.length != 2) {
            return RespBuilder.error("ERR usage: llen <key>");
        }
        return RespBuilder.integer(listDataStore.lengthList(args[1]));
    }
}
