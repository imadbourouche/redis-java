package com.redis.resp.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StreamDataStore {
    /*
        keyStream: {
            id: {
                key: value
            }
        }
     */
    private final Map<String, HashMap<String, HashMap<String, String>>> stream = new ConcurrentHashMap<>();

    public void set(String keyStream, String id, String key, String value) {
        HashMap<String, String> keyValue = new HashMap<>() {{put(key, value);}};
        stream.put(keyStream, new HashMap<>() {{put(id, keyValue);}});
    }

    public HashMap<String, HashMap<String, String>> getStream(String keyStream) {
        return stream.get(keyStream);
    }

    public HashMap<String, String> getStreamEntry(String keyStream, String id) {
        return stream.get(keyStream).get(id);
    }

    public boolean containsKey(String keyStream) {
        return stream.containsKey(keyStream);
    }

    public boolean containsId(String keyStream, String id) {
        if(stream.containsKey(keyStream)) {
            return stream.get(keyStream).containsKey(keyStream);
        }
        return false;
    }
}
