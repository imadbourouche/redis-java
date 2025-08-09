package com.redis.resp.command;

import com.redis.resp.RespBuilder;
import com.redis.resp.storage.ListDataStore;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BlpopCommand implements Command {
    private final ListDataStore listDataStore;

    // One waiting queue per key (FIFO)
    private static final Map<String, Queue<Thread>> waiters = new ConcurrentHashMap<>();

    public BlpopCommand(ListDataStore store) {
        this.listDataStore = store;
    }

    @Override
    public String execute(String[] args) {
        if (args.length != 3) {
            return RespBuilder.error("ERR usage: BLPOP <key> <time>");
        }

        String listKey = args[1];
        float timeout;
        try {
            timeout = Float.parseFloat(args[2]) * 1000; // seconds -> ms
        } catch (NumberFormatException e) {
            return RespBuilder.error("ERR timeout must be an integer or float");
        }

        boolean waitForever = (timeout == 0);

        Thread current = Thread.currentThread();
        waiters.computeIfAbsent(listKey, k -> new LinkedList<>()).add(current);

        long start = System.currentTimeMillis();
        try {
            while (waitForever || (System.currentTimeMillis() - start < timeout)) {
                // Only first thread in queue may pop
                if (waiters.get(listKey).peek() == current) {
                    if (listDataStore.containsKey(listKey)) {
                        String elem = listDataStore.pop(listKey);
                        if (elem != null) {
                            waiters.get(listKey).poll(); // remove from queue
                            return RespBuilder.array(new ArrayList<>(Arrays.asList(listKey, elem)));
                        }
                    }
                }
                 System.out.printf("Thread[%d]: waiting for key in the list %s%n", Thread.currentThread().threadId(), listKey);
                Thread.sleep(Duration.ofMillis(100));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // Ensure cleanup if timed out or interrupted
            waiters.get(listKey).remove(current);
        }

        return RespBuilder.bulkString(null);
    }
}
