package com.redis.resp.storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapDataStore {
    private final Map<String, String> map = new ConcurrentHashMap<>();

    public void set(String key, String value) {
        map.put(key, value);
    }

    public String get(String key) {
        return map.get(key);
    }

    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    public void remove(String key) {
        map.remove(key);
    }
}
