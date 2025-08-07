package com.redis.resp;

import java.util.ArrayList;
import java.util.List;

public final class RespParser {
    private RespParser() {} // Utility class

    public static String[] parse(String input) {
        if (input == null || input.isEmpty()) return new String[0];
        String[] lines = input.split("\r\n");
        int index = 0;
        if (!lines[index].startsWith("*")) return new String[0];
        List<String> result = new ArrayList<>();
        int count = Integer.parseInt(lines[index++].substring(1));

        for (int i = 0; i < count && index < lines.length; i++) {
            if (!lines[index].startsWith("$")) break; // must be bulk string
            int len = Integer.parseInt(lines[index++].substring(1));
            if (index >= lines.length) break;
            String value = lines[index++];
            result.add(value);
        }

        return result.toArray(new String[0]);
    }
}
