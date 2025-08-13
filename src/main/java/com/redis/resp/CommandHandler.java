package com.redis.resp;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.redis.resp.command.*;
import com.redis.resp.storage.ListDataStore;
import com.redis.resp.storage.MapDataStore;
import com.redis.resp.storage.StreamDataStore;

public class CommandHandler {
    private static final MapDataStore mapDataStore = new MapDataStore();
    private static final ListDataStore listDataStore = new ListDataStore();
    private static final StreamDataStore streamDataStore = new StreamDataStore();

    private static final Map<String, Command> commands;
    static {
        Map<String, Command> commandMap = new LinkedHashMap<>();
        commandMap.put("ping", new PingCommand());
        commandMap.put("echo", new EchoCommand());
        commandMap.put("set", new SetCommand(mapDataStore));
        commandMap.put("get", new GetCommand(mapDataStore));
        commandMap.put("rpush", new RpushCommand(listDataStore));
        commandMap.put("lrange", new LrangeCommand(listDataStore));
        commandMap.put("lpush", new LpushCommand(listDataStore));
        commandMap.put("llen", new LlenCommand(listDataStore));
        commandMap.put("lpop", new LpopCommand(listDataStore));
        commandMap.put("blpop", new BlpopCommand(listDataStore));
        commandMap.put("type", new TypeCommand(mapDataStore, streamDataStore));
        commandMap.put("xadd", new XaddCommand(streamDataStore));
        commandMap.put("xrange", new XrangeCommand(streamDataStore));
        commands = Collections.unmodifiableMap(commandMap);
    }
    public static String handle(String received) {
        String[] args = RespParser.parse(received);
        if (args.length == 0) return RespBuilder.error("ERR empty command");
        Command cmd = commands.get(args[0].toLowerCase(Locale.ROOT));
        if(cmd == null) return RespBuilder.error("ERR unknown command");
        return cmd.execute(args);
    }
}
