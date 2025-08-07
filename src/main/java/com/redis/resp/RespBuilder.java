package com.redis.resp;

public final class RespBuilder {
    private RespBuilder() {}

    public static String simpleString(String message) {
        return "+" + message + "\r\n";
    }

    public static String error(String message) {
        return "-" + message + "\r\n";
    }

    public static String bulkString(String value) {
        if (value == null || value.isEmpty()) return "$-1\r\n";
        return "$" + value.length() + "\r\n" + value + "\r\n";
    }

    public static String nullBulkString() {
        return "$-1\r\n";
    }

    public static String integer(long number) {
        return ":" + number + "\r\n";
    }

    public static String array(String[] values) {
        if (values == null) return "*-1\r\n";
        StringBuilder sb = new StringBuilder();
        sb.append("*").append(values.length).append("\r\n");
        for (String v : values) {
            sb.append(bulkString(v));
        }
        return sb.toString();
    }
}
