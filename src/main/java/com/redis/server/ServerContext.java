package com.redis.server;

public class ServerContext {
    private static Server server;

    public static void setServer(Server s) {
        server = s;
    }

    public static Server getServer() {
        return server;
    }
}