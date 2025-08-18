package com.redis.resp.parser;

import java.util.List;

public final class RespBuilder {
    private static final String DOLLAR = "$";
    private static final String CRLF = "\r\n";
    private static final String PLUS = "+";
    private static final String MINUS = "-";
    private static final String ASTRIC = "*";
    private  static final String TWOPOINTS = ":";

    private RespBuilder() {}

    public static String simpleString(String message) {
        return PLUS + message + CRLF;
    }

    public static String error(String message) {
        return MINUS + message + CRLF;
    }

    public static String bulkString(String value) {
        if (value == null) return DOLLAR + "-1" + CRLF;
        return DOLLAR + value.length() + CRLF + value + CRLF;
    }

    public static String integer(long number) {
        return TWOPOINTS + number + CRLF;
    }

    public static String array(List<?> values) {
        if (values == null) return ASTRIC + "-1" + CRLF;
        StringBuilder sb = new StringBuilder();
        sb.append(ASTRIC).append(values.size()).append(CRLF);
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
