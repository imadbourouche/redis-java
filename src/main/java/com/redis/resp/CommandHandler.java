package com.redis.resp;

import java.util.HashMap;
import java.util.Locale;

public class CommandHandler {
    public static HashMap<String, String> map = new HashMap<String, String>();

    public static String handle(String received) {
        String[] arguments = RespParser.parse(received);
        if (arguments.length == 0) {
            return RespBuilder.error("ERR empty command");
        }

        final String ping = "ping";
        final String echo = "echo";
        final String set = "set";
        final String get = "get";
        String command = arguments[0].toLowerCase(Locale.ROOT);

        switch (command) {
            case ping:
                return RespBuilder.simpleString("PONG");
            case echo:
                if (arguments.length > 1) {
                    return RespBuilder.bulkString(arguments[1]);
                } else {
                    return RespBuilder.error("ERR wrong number of arguments for 'echo' command");
                }
            case set:
                if (arguments.length == 3) {
                    map.put(arguments[1], arguments[2]);
                    return RespBuilder.simpleString("OK");
                }else{
                    return RespBuilder.error("Too many arguments for command '" + command + "'");
                }
                case get:
                    if (arguments.length > 1) {
                        if(map.containsKey(arguments[1])){
                            return RespBuilder.bulkString(map.get(arguments[1]));
                        }else{
                            return RespBuilder.nullBulkString();
                        }
                    }else{
                        return RespBuilder.nullBulkString();
                    }
            default:
                return RespBuilder.error("ERR unknown command '" + command + "'");
        }
    }
}
