package com.redis.resp.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ListDataStore {
    private final Map<String, ArrayList<String>> keyListMap = new HashMap<String, ArrayList<String>>();

    public boolean containsKey(String key) {
        return keyListMap.containsKey(key);
    }

    public ArrayList<String> getList(String key) {
        return keyListMap.get(key);
    }

    public void createList(String key) {
        keyListMap.put(key, new ArrayList<String>());
    }

    public int append(String key, String[] values) {
        ArrayList<String> list = keyListMap.get(key);
        Collections.addAll(list, values);
        return list.size();
    }

    public int addFirst(String key, String[] values) {
        ArrayList<String> list = keyListMap.get(key);
        for(String item : values) {
            list.addFirst(item);
        }
        return list.size();
    }

    public String pop(String key) {
        if (keyListMap.containsKey(key)){
            return keyListMap.get(key).isEmpty() ? null : keyListMap.get(key).removeFirst();
        }
        return null;
    }

    public ArrayList<String> popMultipleElements(String key, int numElements) {
        ArrayList<String> list = keyListMap.get(key);
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        int endIndex = Math.min(numElements, list.size());
        ArrayList<String> result = new ArrayList<>(list.subList(0, endIndex));
        list.subList(0, endIndex).clear();
        return result;
    }


    public int lengthList(String key) {
        return keyListMap.containsKey(key) ? keyListMap.get(key).size() : 0;
    }
}
