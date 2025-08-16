package com.redis.resp;

import java.util.ArrayList;
import java.util.List;

public final class RespBuilder {
    private RespBuilder() {}

    public static String simpleString(String message) {
        return "+" + message + "\r\n";
    }

    public static String error(String message) {
        return "-" + message + "\r\n";
    }

    public static String bulkString(String value) {
        if (value == null) return "$-1\r\n";
        return "$" + value.length() + "\r\n" + value + "\r\n";
    }

    public static String integer(long number) {
        return ":" + number + "\r\n";
    }

    public static String array(List<?> values) {
        if (values == null) return "*-1\r\n";
        StringBuilder sb = new StringBuilder();
        sb.append("*").append(values.size()).append("\r\n");
        for (Object v : values) {
            if (v instanceof List<?>) {
                sb.append(array((List<?>) v)); // recursive for nested arrays
            }else if (v instanceof Integer) {
                sb.append(integer((Integer) v));
            }
            else {
                sb.append(bulkString(v.toString()));
            }
        }
        return sb.toString();
    }
}
