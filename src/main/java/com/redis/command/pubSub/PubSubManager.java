package com.redis.command.pubSub;

import com.redis.resp.parser.RespBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PubSubManager {

    private static final Map<String, Set<Socket>> channels = new ConcurrentHashMap<>();

    public static int subscribe(List<String> channelsList, Socket clientSocket){
        for(String channel : channelsList){
            if (channels.containsKey(channel)) {
                if (channels.get(channel).contains(clientSocket)) {
                    return ClientContext.getNumberOfSubscriptions(clientSocket);
                }
            }
            channels.computeIfAbsent(channel, k -> ConcurrentHashMap.newKeySet()).add(clientSocket);
            ClientContext.setClientToSubscriptionMode(clientSocket);
            ClientContext.incrementNumberOfSubscriptions(clientSocket);
        }
        return ClientContext.getNumberOfSubscriptions(clientSocket);
    }

    public static int unsubscribe(String channel, Socket clientSocket) {
        Set<Socket> subscribers = channels.get(channel);
        if (subscribers != null) {
            subscribers.remove(clientSocket);
            if (subscribers.isEmpty()) {
                channels.remove(channel);
            }
            ClientContext.decrementNumberOfSubscriptions(clientSocket);
        }
        int numSubRemaining = ClientContext.getNumberOfSubscriptions(clientSocket);
        if (numSubRemaining <= 0) {
            ClientContext.removeFromSubscriptionMode(clientSocket);
        }
        return numSubRemaining;
    }


    public static Set<Socket> getSubscribers(String channel){
        return channels.getOrDefault(channel, Collections.emptySet());
    }

    public static int publish(String channel, String message){
        Set<Socket> subscribers = channels.get(channel);
        for(Socket subscriber : subscribers){
            try{
                OutputStream out = subscriber.getOutputStream();
                String response = RespBuilder.array(Arrays.asList("message", channel, message));
                out.write(response.getBytes());
                out.flush();
            }catch (IOException e){
                // do nothing
            }
       }
        return subscribers.size();
    }


    public static class ClientContext {
        private static final Map<Socket, Boolean> subscriptionMode = new ConcurrentHashMap<>();
        private static final Map<Socket, Integer> numberOfSubscriptions = new ConcurrentHashMap<>();

        public static void setClientToSubscriptionMode(Socket clientSocket){
            subscriptionMode.put(clientSocket, true);
        }

        public static boolean isClientInSubscriptionMode(Socket clientSocket) {
            if(subscriptionMode.containsKey(clientSocket)){
                return subscriptionMode.get(clientSocket);
            }
            return false;
        }

        public static void removeFromSubscriptionMode(Socket clientSocket) {
            subscriptionMode.remove(clientSocket);
        }

        public static int getNumberOfSubscriptions(Socket clientSocket) {
            return numberOfSubscriptions.getOrDefault(clientSocket, 0);
        }

        public static void incrementNumberOfSubscriptions(Socket clientSocket) {
            numberOfSubscriptions.put(clientSocket, numberOfSubscriptions.getOrDefault(clientSocket, 0) + 1) ;
        }

        public static void decrementNumberOfSubscriptions(Socket clientSocket) {
            if(numberOfSubscriptions.containsKey(clientSocket)){
                numberOfSubscriptions.put(clientSocket, numberOfSubscriptions.get(clientSocket) - 1) ;
            }
        }
    }


}
