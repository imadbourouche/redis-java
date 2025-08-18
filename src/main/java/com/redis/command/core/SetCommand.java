package com.redis.command.core;

import com.redis.command.Command;
import com.redis.resp.parser.RespBuilder;
import com.redis.storage.MapDataStore;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SetCommand implements Command {
    private final MapDataStore mapDatastore;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public SetCommand(MapDataStore store) {
        this.mapDatastore = store;
    }

    @Override
    public String execute(String[] args) {
        if (args.length < 3) {
            return RespBuilder.error("ERR wrong number of arguments for 'set' command");
        }

        mapDatastore.set(args[1], args[2]);

        // Optional TTL handling
        if (args.length > 3) {
            String option = args[3].toLowerCase();
            if ("px".equals(option)) {
                if (args.length < 5) {
                    return RespBuilder.error("ERR PX option requires an argument");
                }
                try {
                    int ttl = Integer.parseInt(args[4]);
                    scheduler.schedule(() -> mapDatastore.remove(args[1]), ttl, TimeUnit.MILLISECONDS);
                } catch (NumberFormatException e) {
                    return RespBuilder.error("ERR PX value is not a valid integer");
                }
            } else {
                return RespBuilder.error("ERR unknown option for 'set'");
            }
        }

        return RespBuilder.simpleString("OK");
    }
}
