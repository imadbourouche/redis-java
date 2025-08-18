package com.redis.command.list;

import com.redis.command.Command;
import com.redis.resp.parser.RespBuilder;
import com.redis.storage.ListDataStore;

public class RpushCommand implements Command {
    private final ListDataStore  listDataStore;

    public RpushCommand(ListDataStore store) {
        this.listDataStore = store;
    }

    @Override
    public String execute(String[] args) {
        if  (args.length < 3) {
            return RespBuilder.error("ERR usage: rpush <key> <value>");
        }
        String key = args[1];
        if(!listDataStore.containsKey(key)) {
            listDataStore.createList(key);
        }
        String[] rpushArgs = new String[args.length - 2];
        System.arraycopy(args, 2, rpushArgs, 0, rpushArgs.length);
        int length = listDataStore.append(key , rpushArgs);
        return RespBuilder.integer(length);
    }
}
