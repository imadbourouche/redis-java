package com.redis.resp;

import java.net.Socket;
import java.util.*;

import com.redis.resp.command.*;
import com.redis.resp.command.pubSub.PubSubManager;
import com.redis.resp.command.pubSub.PublishCommand;
import com.redis.resp.command.pubSub.SubscribeCommand;
import com.redis.resp.command.pubSub.UnsubscribeCommand;
import com.redis.resp.storage.ListDataStore;
import com.redis.resp.storage.MapDataStore;
import com.redis.resp.storage.StreamDataStore;

public class CommandHandler {
    private static final MapDataStore mapDataStore = new MapDataStore();
    private static final ListDataStore listDataStore = new ListDataStore();
    private static final StreamDataStore streamDataStore = new StreamDataStore();
    private static final Set<String> ALLOWED_IN_SUB_MODE = Set.of(
            "subscribe", "psubscribe", "unsubscribe", "punsubscribe",
            "ping", "quit", "reset"
    );

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
        commandMap.put("xread", new XreadCommand(streamDataStore));
        commandMap.put("subscribe", new SubscribeCommand());
        commandMap.put("unsubscribe", new UnsubscribeCommand());
        commandMap.put("publish", new PublishCommand());
        commands = Collections.unmodifiableMap(commandMap);
    }

    public static String handle(String received, Socket clientSocket) {
        String[] args = RespParser.parse(received);
        if (args.length == 0) return RespBuilder.error("ERR empty command");
        String cmdName = args[0].toLowerCase(Locale.ROOT);
        boolean subMode = PubSubManager.ClientContext.isClientInSubscriptionMode(clientSocket);
        if (subMode && !ALLOWED_IN_SUB_MODE.contains(cmdName)) {
            return RespBuilder.error("ERR Can't execute '" + cmdName + "': only (P|S)SUBSCRIBE / (P|S)UNSUBSCRIBE / PING / QUIT / RESET are allowed in this context");
        }
        Command cmd = commands.get(cmdName);
        if(cmd == null) return RespBuilder.error("ERR unknown command");
        return cmd.execute(args, clientSocket);
    }
}
