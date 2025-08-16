package com.redis.resp.command;

import com.redis.exceptions.InvalidIdException;
import com.redis.resp.RespBuilder;
import com.redis.resp.storage.StreamDataStore;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class XreadCommand implements Command {
    private final StreamDataStore streamDataStore;
    private static final Map<String, Queue<Thread>> waiters = new ConcurrentHashMap<>();

    public XreadCommand(StreamDataStore streamDataStore) {
        this.streamDataStore = streamDataStore;
    }

    @Override
    public String execute(String[] args) {
        if (args.length < 4) {
            return RespBuilder.error("ERR usage: XREAD [block <ms>] streams <key1> <key2> ... <id1> <id2> ...");
        }

        try {
            if ("streams".equalsIgnoreCase(args[1])) {
                return RespBuilder.array(readStreams(parseStreamArgs(args, 2)));
            } else if ("block".equalsIgnoreCase(args[1])) {
                long timeout = Long.parseLong(args[2]);
                List<Object> result = blockingRead(parseStreamArgs(args, 4), timeout);
                if (result != null) {
                    return RespBuilder.array(result);
                }
                return RespBuilder.bulkString(null);
            } else {
                return RespBuilder.error("ERR invalid XREAD arguments");
            }
        } catch (NumberFormatException e) {
            return RespBuilder.error("ERR invalid timeout or ID format");
        }
    }

    /** Parses <key1> <key2> ... <id1> <id2> ... into a Map<streamKey, startId> */
    private Map<String, String> parseStreamArgs(String[] args, int streamsIndex) {
        Map<String, String> fields = new LinkedHashMap<>();
        int numStreamsKeys = (args.length - streamsIndex) / 2;
        for (int i = streamsIndex; i < streamsIndex + numStreamsKeys; i++) {
            fields.put(args[i], args[i + numStreamsKeys]);
        }
        return fields;
    }

    /** Non-blocking read logic */
    private List<Object> readStreams(Map<String, String> streams) {
        List<Object> result = new ArrayList<>();
        streams.forEach((streamKey, startId) -> {
            List<Object> streamData = readSingleStream(streamKey, startId);
            if (!streamData.isEmpty()) {
                result.add(streamData);
            }
        });
        return result;
    }

    /** Blocking read logic */
    private List<Object> blockingRead(Map<String, String> streams, long timeoutMs) {
        boolean waitForever = (timeoutMs == 0);
        long startTime = System.currentTimeMillis();

        for (Map.Entry<String, String> entry : streams.entrySet()) {
            String streamKey = entry.getKey();
            String startId = entry.getValue();
            Thread current = Thread.currentThread();
            waiters.computeIfAbsent(streamKey, k -> new LinkedList<>()).add(current);

            try {
                while (waitForever || (System.currentTimeMillis() - startTime < timeoutMs)) {
                    if (waiters.get(streamKey).peek() == current) {
                        if ("$".equals(startId)) {
                            startId = streamDataStore.getTopId(streamKey);
                        }
                        List<Object> data = readSingleStream(streamKey, startId);
                        if (!data.isEmpty()) {
                            waiters.get(streamKey).poll();
                            return List.of(data);
                        }
                    }
//                    System.out.printf("Thread[%d]: waiting for key in the stream %s%n", Thread.currentThread().threadId(), streamKey);
                    Thread.sleep(Duration.ofMillis(10));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                waiters.getOrDefault(streamKey, new LinkedList<>()).remove(current);
            }
        }
        return null;
    }

    /** Reads one stream from a given start ID */
    private List<Object> readSingleStream(String streamKey, String startId) {
        if (!streamDataStore.containsKey(streamKey)) return Collections.emptyList();

        HashMap<String, HashMap<String, String>> stream = streamDataStore.getStream(streamKey);
        if (stream.isEmpty()) return Collections.emptyList();

        String entryStartId = startId;
        List<String> filteredIds = stream.keySet().stream()
                .filter(id -> isGreaterExclusive(entryStartId, id))
                .sorted(this::compareIdsStrings)
                .toList();

        if (filteredIds.isEmpty()) return Collections.emptyList();

        List<Object> elements = new ArrayList<>();
        elements.add(streamKey);

        List<Object> entries = new ArrayList<>();
        for (String id : filteredIds) {
            List<Object> entry = new ArrayList<>();
            entry.add(id);
            List<Object> kvList = new ArrayList<>();
            stream.get(id).forEach((k, v) -> {
                kvList.add(k);
                kvList.add(v);
            });
            entry.add(kvList);
            entries.add(entry);
        }
        elements.add(entries);

        return elements;
    }

    private boolean isGreaterExclusive(String startId, String id) {
        long[] left = parseId(startId);
        long[] current = parseId(id);
        return compareIds(current, left) > 0;
    }

    private long[] parseId(String id) {
        String[] parts = id.split("-");
        long ms = Long.parseLong(parts[0]);
        long seq = parts.length > 1 ? Long.parseLong(parts[1]) : 0;
        return new long[]{ms, seq};
    }

    private int compareIds(long[] id1, long[] id2) {
        if (id1[0] != id2[0]) return Long.compare(id1[0], id2[0]);
        return Long.compare(id1[1], id2[1]);
    }

    private int compareIdsStrings(String id1, String id2) {
        return compareIds(parseId(id1), parseId(id2));
    }
}
