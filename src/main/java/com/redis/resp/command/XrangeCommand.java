package com.redis.resp.command;

import com.redis.resp.RespBuilder;
import com.redis.resp.storage.StreamDataStore;

import java.util.*;
import java.util.stream.Collectors;

public class XrangeCommand implements Command {
    private final StreamDataStore streamDataStore;

    public  XrangeCommand(StreamDataStore streamDataStore) {
        this.streamDataStore = streamDataStore;
    }

    @Override
    public String execute(String[] args){
        if(args.length != 4){
            return RespBuilder.error("ERR usage: XLRANGE <keyStream> <id1> <id2>");
        }
        String streamKey = args[1];
        String startId = args[2];
        String endId = args[3];
        if(!streamDataStore.containsKey(streamKey)) return RespBuilder.array(new ArrayList<>());

        HashMap<String, HashMap<String, String>> stream = streamDataStore.getStream(streamKey);
        if (stream.isEmpty()) {
            return RespBuilder.array(List.of(new String[0]));
        }

        // Resolve "-" and "+"
        if ("-".equals(startId)) {
            startId = Collections.min(stream.keySet(), this::compareIdsStrings);
        }
        if ("+".equals(endId)) {
            endId = Collections.max(stream.keySet(), this::compareIdsStrings);
        }
        // Sort keys to ensure XRANGE returns results in ascending order
        String finalStartId = startId;
        String finalEndId = endId;
        List<String> filteredIds = stream.keySet().stream()
                .filter(id -> isInRange(id, finalStartId, finalEndId))
                .sorted(this::compareIdsStrings)
                .toList();

        List<Object> result = new ArrayList<>();
        for (String id : filteredIds) {
            List<Object> entry = new ArrayList<>();
            entry.add(id); // first element is the ID
            List<Object> kvList = new ArrayList<>();
            stream.get(id).forEach((k, v) -> {
                kvList.add(k);
                kvList.add(v);
            });
            entry.add(kvList);
            result.add(entry);
        }

        return RespBuilder.array(result);
    }


    private boolean isInRange(String id, String idLeft, String idRight) {
        long[] left = parseId(idLeft);
        long[] right = parseId(idRight);
        long[] current = parseId(id);

        return compareIds(current, left) >= 0 && compareIds(current, right) <= 0;
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

    // Comparator for string IDs without re-parsing twice
    private int compareIdsStrings(String id1, String id2) {
        return compareIds(parseId(id1), parseId(id2));
    }

}
