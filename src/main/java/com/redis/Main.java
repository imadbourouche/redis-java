package com.redis;

import com.redis.server.Server;

public class Main {
  public static void main(String[] args){
    System.out.println("Logs from your program will appear here!");
    int port = 6379;
    Server server = new Server(port);
    server.start();
  }

}
