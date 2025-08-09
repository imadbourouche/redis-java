package com.redis.resp;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.redis.resp.command.*;
import com.redis.resp.storage.ListDataStore;
import com.redis.resp.storage.MapDataStore;

public class CommandHandler {
    private static final MapDataStore mapDataStore = new MapDataStore();
    private static final ListDataStore listDataStore = new ListDataStore();

    private static final Map<String, Command> commands = Map.of(
            "ping", new PingCommand(),
            "echo", new EchoCommand(),
            "set", new SetCommand(mapDataStore),
            "get", new GetCommand(mapDataStore),
            "rpush", new RpushCommand(listDataStore),
            "lrange", new LrangeCommand(listDataStore),
            "lpush", new LpushCommand(listDataStore),
            "llen", new LlenCommand(listDataStore),
            "lpop", new LpopCommand(listDataStore)
    );

    public static Map<String, String> map = new ConcurrentHashMap<>();

    public static String handle(String received) {
        String[] args = RespParser.parse(received);
        if (args.length == 0) return RespBuilder.error("ERR empty command");
        Command cmd = commands.get(args[0].toLowerCase(Locale.ROOT));
        if(cmd == null) return RespBuilder.error("ERR unknown command");
        return cmd.execute(args);
    }
}
