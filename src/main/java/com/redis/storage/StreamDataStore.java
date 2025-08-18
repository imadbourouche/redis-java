package com.redis.storage;

import com.redis.exceptions.InvalidIdException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StreamDataStore {

    // keyStream: { id: { key: value } }
    private final Map<String, HashMap<String, HashMap<String, String>>> stream = new ConcurrentHashMap<>();
    private final Map<String, String> topIds = new ConcurrentHashMap<>();

    public String set(String keyStream, String id, Map<String, String> fields) {
        validateId(id);
        validateIdGreaterThanTop(keyStream, id);
        id = generateId(keyStream, id);
        stream.computeIfAbsent(keyStream, k -> new HashMap<>()).put(id, new  HashMap<>(fields));
        topIds.put(keyStream, id);
        return id;
    }

    public HashMap<String, HashMap<String, String>> getStream(String keyStream) {
        return stream.get(keyStream);
    }

    public boolean containsKey(String keyStream) {
        return stream.containsKey(keyStream);
    }

    public String getTopId(String keyStream) {
        return topIds.get(keyStream);
    }

    // private validation methods
    private void validateId(String id) {
        // Matches:
        // 1. number-number   (e.g. "123-456")
        // 2. number-*        (e.g. "123-*")
        // 3. *               (just an asterisk)
        if (!id.matches("([0-9]+-[0-9]+)|([0-9]+-\\*)|(\\*)")) {
            throw new InvalidIdException("ERR id not valid id");
        }
        if (id.equals("0-0")) {
            throw new InvalidIdException("ERR The ID specified in XADD must be greater than 0-0");
        }
    }

    private void validateIdGreaterThanTop(String keyStream, String newId) {
        if ("*".equals(newId) || newId.endsWith("-*")) {
            return;
        }
        String topId = topIds.get(keyStream);
        if (topId != null && compareIds(newId, topId) <= 0) {
            throw new InvalidIdException("ERR The ID specified in XADD is equal or smaller than the target stream top item");
        }
    }

    private int compareIds(String id1, String id2) {
        String[] parts1 = id1.split("-");
        String[] parts2 = id2.split("-");

        long millisecondsTime1 = Long.parseLong(parts1[0]);
        long millisecondsTime2 = Long.parseLong(parts2[0]);
        if (millisecondsTime1 != millisecondsTime2) {
            return Long.compare(millisecondsTime1, millisecondsTime2);
        }

        long sequenceNumber1 = Long.parseLong(parts1[1]);
        long sequenceNumber2 = Long.parseLong(parts2[1]);
        return Long.compare(sequenceNumber1, sequenceNumber2);
    }

    private String generateId(String keyStream, String id) {
        if ("*".equals(id)) {
            // Fully auto-generated ID
            return System.currentTimeMillis() + "-0";
        }
        if (id.endsWith("-*")) {
            // Partial auto-ID: use given ms, increment sequence from top
            String millisecondsTime = id.substring(0, id.length() - 2); // remove "-*"
            String topId = topIds.get(keyStream);
            long nextSeq = 0;
            if (millisecondsTime.equals("0")) nextSeq = 1;
            if (topId != null && topId.startsWith(millisecondsTime + "-")) {
                nextSeq = Long.parseLong(topId.split("-")[1]) + 1;
            }
            return millisecondsTime + "-" + nextSeq;
        }
        // Exact ID (already validated)
        return id;
    }
}
