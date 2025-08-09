package com.redis.resp.command;

import com.redis.resp.RespBuilder;
import com.redis.resp.storage.ListDataStore;

import java.util.ArrayList;

public class LpopCommand implements Command {
    private final ListDataStore listDataStore;

    public LpopCommand(ListDataStore store) {
        this.listDataStore = store;
    }

    @Override
    public String execute(String[] args) {
        if  (args.length < 2) {
            return RespBuilder.error("ERR usage: lpop <key> <args>");
        }
        String listKey = args[1];
        if(args.length == 3){
            int numOfElemToRemove = Integer.parseInt(args[2]);
            ArrayList<String> itemsRemoved = listDataStore.popMultipleElements(listKey, numOfElemToRemove);
            return RespBuilder.array(itemsRemoved);
        }else{
            String firstItem = listDataStore.pop(args[1]);
            return RespBuilder.bulkString(firstItem);
        }
    }
}
