import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    @SuppressWarnings("InfiniteLoopStatement")
  public static void main(String[] args){
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");
    int port = 6379;
    try(ServerSocket serverSocket = new ServerSocket(port)){
      // Since the tester restarts your program quite often, setting SO_REUSEADDR
      // ensures that we don't run into 'Address already in use' errors
      serverSocket.setReuseAddress(true);
      // Wait for connection from client.
        while(true){
            Socket clientSocket = serverSocket.accept();
            Thread.startVirtualThread(() -> handleClientRequest(clientSocket));
        }
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }

  private static void handleClientRequest(Socket clientSocket) {
      try (clientSocket; InputStream inputStream = clientSocket.getInputStream();
           OutputStream outputStream = clientSocket.getOutputStream()) {
          try {
              byte[] buffer = new byte[1024];
              int read;
              while ((read = inputStream.read(buffer)) != -1) {
                  // read input from the client
                  String received = new String(buffer, 0, read).trim();
                  System.out.println(received);
                  // Respond to the client socket with PONG
                  outputStream.write(("+PONG\r\n").getBytes());
              }
          } catch (IOException e) {
              System.out.println("Client disconnected: " + e.getMessage());
          }
      } catch (IOException e) {
          System.out.println("IOException: " + e.getMessage());
      }
  }
}
