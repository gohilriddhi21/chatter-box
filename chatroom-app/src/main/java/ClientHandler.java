
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * The `ClientHandler` class represents a thread responsible for handling communication
 * with an individual client in a chat room. It manages sending and receiving messages,
 * processing various types of messages, and handling disconnections.
 */
class ClientHandler implements Runnable {

  public Socket getSocket() {
    return socket;
  }

  /**
   * A list of all active client handlers.
   */
  public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

  private Socket socket;
  private BufferedReader bufferedReader;
  private BufferedWriter bufferedWriter;

  public BufferedReader getBufferedReader() {
    return bufferedReader;
  }

  public void setBufferedReader(BufferedReader bufferedReader) {
    this.bufferedReader = bufferedReader;
  }

  public BufferedWriter getBufferedWriter() {
    return bufferedWriter;
  }

  public void setBufferedWriter(BufferedWriter bufferedWriter) {
    this.bufferedWriter = bufferedWriter;
  }

  private String clientUserName;

  private Semaphore semaphore;

  /**
   * The left bracket used in message formatting.
   */
  public static String LEFT_BRACKET = "[";

  /**
   * The right bracket followed by a colon used in message formatting.
   */
  public static String RIGHT_BRACKET = "] : ";

  private ChatRoomProtocol chatRoomProtocol;

  /**
   * Constructs a new `ClientHandler` for the specified socket.
   *
   * @param socket The socket associated with the client.
   */
  public ClientHandler(Socket socket, Semaphore semaphore) {
    try {
      this.socket = socket;
      this.semaphore = semaphore;
      this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
      this.clientUserName = bufferedReader.readLine();
      chatRoomProtocol = new ChatRoomProtocol();
      clientHandlers.add(this);
      sendConnectionACK();
    } catch (Exception e){

    }
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public String toString() {
    return "ClientHandler{" +
        "socket=" + socket +
        ", bufferedReader=" + bufferedReader +
        ", bufferedWriter=" + bufferedWriter +
        ", clientUserName='" + clientUserName + '\'' +
        ", chatRoomProtocol=" + chatRoomProtocol +
        '}';
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }

  /**
   * Sends a connection acknowledgment message to the client upon successful connection.
   *
   * @throws IOException If an I/O error occurs.
   */
  public void sendConnectionACK() throws IOException {
    try {
      if (socket.isConnected()) {
        if(semaphore.availablePermits() == 0){
          this.bufferedWriter.write("MAX CLIENTS REACHED.");
          this.bufferedWriter.write("\n");
          this.bufferedWriter.flush();
        } else {
          String connectionData = bufferedReader.readLine();
          byte[] frame = ChatRoomProtocol.decodeFrame(connectionData);
          try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(frame);
              DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream)) {

            int messageType = dataInputStream.readInt();
            if (messageType == ChatRoomProtocol.CONNECT_MESSAGE) {
              String connectionMessage = "Connection established with Server. There are " +
                  clientHandlers.size() + " connected users.";
              sendDirectMessage(clientUserName, new String(chatRoomProtocol.encodeConnectResponse(true, connectionMessage), StandardCharsets.UTF_8));
              broadcastMessage("[Server] : " + clientUserName + " has entered the chat.");
            }
          } catch(Exception e){

          }
        }
      }
    } catch (Exception e){

    }
  }


  @Override
  public void run() {
    String messageFromClient;
    while (socket.isConnected()) {
      try {
        messageFromClient = bufferedReader.readLine();
        processInput(messageFromClient);
      } catch (IOException e) {
        break;
      }
    }
  }

  /**
   * Processes the input message received from the client.
   *
   * @param message The message received from the client.
   */
  public void processInput(String message) {
    if (message != null) {
      byte[] frame = ChatRoomProtocol.decodeFrame(message);
      try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(frame);
          DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream)) {

        int messageType = dataInputStream.readInt();
        switch (messageType) {
          case ChatRoomProtocol.BROADCAST_MESSAGE:
            dataInputStream.readNBytes(ChatRoomProtocol.FRAME_SEPARATOR.length());
            int senderUsernameSize = dataInputStream.readInt();
            dataInputStream.readNBytes(ChatRoomProtocol.FRAME_SEPARATOR.length());
            String senderUsername = new String(dataInputStream.readNBytes(senderUsernameSize),
                StandardCharsets.UTF_8);
            dataInputStream.readNBytes(ChatRoomProtocol.FRAME_SEPARATOR.length());
            int messageSize = dataInputStream.readInt();
            dataInputStream.readNBytes(ChatRoomProtocol.FRAME_SEPARATOR.length());
            byte[] messageBytes = new byte[messageSize];
            dataInputStream.readFully(messageBytes);
            String actualMessage = new String(messageBytes, StandardCharsets.UTF_8);
            broadcastMessage(LEFT_BRACKET + senderUsername + RIGHT_BRACKET + actualMessage);
            break;
          case ChatRoomProtocol.DIRECT_MESSAGE:
            dataInputStream.readNBytes(ChatRoomProtocol.FRAME_SEPARATOR.length());
            int senderUsernameSize2 = dataInputStream.readInt();
            dataInputStream.readNBytes(ChatRoomProtocol.FRAME_SEPARATOR.length());
            String senderUsername2 = new String(dataInputStream.readNBytes(senderUsernameSize2),
                StandardCharsets.UTF_8);
            dataInputStream.readNBytes(ChatRoomProtocol.FRAME_SEPARATOR.length());
            int recepientSize = dataInputStream.readInt();
            dataInputStream.readNBytes(ChatRoomProtocol.FRAME_SEPARATOR.length());
            String recepientName = new String(dataInputStream.readNBytes(recepientSize),
                StandardCharsets.UTF_8);
            dataInputStream.readNBytes(ChatRoomProtocol.FRAME_SEPARATOR.length());
            int messageSize2 = dataInputStream.readInt();
            dataInputStream.readNBytes(ChatRoomProtocol.FRAME_SEPARATOR.length());
            byte[] messageBytes2 = new byte[messageSize2];
            dataInputStream.readFully(messageBytes2);
            String actualMessage2 = new String(messageBytes2, StandardCharsets.UTF_8);
            sendDirectMessage(recepientName, LEFT_BRACKET + senderUsername2 + RIGHT_BRACKET + actualMessage2);
            break;
          case ChatRoomProtocol.DISCONNECT_MESSAGE:
            dataInputStream.readNBytes(ChatRoomProtocol.FRAME_SEPARATOR.length());
            senderUsernameSize = dataInputStream.readInt();
            dataInputStream.readNBytes(ChatRoomProtocol.FRAME_SEPARATOR.length());
            senderUsername = new String(dataInputStream.readNBytes(senderUsernameSize),
                StandardCharsets.UTF_8);
            System.out.println("\nUser " + senderUsername + " Disconnected.");
            sendDirectMessage(senderUsername,
                new String(chatRoomProtocol.encodeConnectResponse(true, Client.DISCONNECT_MESSAGE), StandardCharsets.UTF_8));

            closeEverything(socket, bufferedReader, bufferedWriter);
            break;
          case ChatRoomProtocol.QUERY_CONNECTED_USERS:
            sendDirectMessage(this.getClientUserName(), new String(chatRoomProtocol.encodeQueryResponse(clientHandlers), StandardCharsets.UTF_8));
            break;
          case ChatRoomProtocol.SEND_INSULT:
            dataInputStream.readNBytes(ChatRoomProtocol.FRAME_SEPARATOR.length());
            senderUsernameSize = dataInputStream.readInt();
            dataInputStream.readNBytes(ChatRoomProtocol.FRAME_SEPARATOR.length());
            senderUsername = new String(dataInputStream.readNBytes(senderUsernameSize), StandardCharsets.UTF_8);
            dataInputStream.readNBytes(ChatRoomProtocol.FRAME_SEPARATOR.length());
            recepientSize = dataInputStream.readInt();
            dataInputStream.readNBytes(ChatRoomProtocol.FRAME_SEPARATOR.length());
            recepientName = new String(dataInputStream.readNBytes(recepientSize), StandardCharsets.UTF_8);
            String insult = InsultGenerator.generateInsult();
            sendDirectMessage(senderUsername, insult);
            sendDirectMessage(recepientName, LEFT_BRACKET + senderUsername + RIGHT_BRACKET + insult);
            break;
        }
      } catch (Exception e) {

      }
    }
  }

  /**
   * Sends a direct message to the specified user.
   *
   * @param targetUser The username of the target user.
   * @param message    The message to be sent.
   */
  public void sendDirectMessage(String targetUser, String message) {
    for (ClientHandler clientHandler : clientHandlers) {
      if (clientHandler.clientUserName.equals(targetUser)) {
        try {
          clientHandler.bufferedWriter.write(message);
          clientHandler.bufferedWriter.newLine();
          clientHandler.bufferedWriter.flush();
        } catch (IOException e) {
        }
        return;
      }
    }
    try {
      bufferedWriter.write("[Server] : User '" + targetUser + "' not found.");
      bufferedWriter.newLine();
      bufferedWriter.flush();
    } catch (IOException e) {

    }
  }

  /**
   * Broadcasts a message to all connected clients except the sender.
   *
   * @param message The message to be broadcasted.
   */
  public void broadcastMessage(String message) {
    for (ClientHandler clientHandler : clientHandlers) {
      try {
        if (!clientHandler.clientUserName.equals(clientUserName)) {
          clientHandler.bufferedWriter.write(message);
          clientHandler.bufferedWriter.newLine();
          clientHandler.bufferedWriter.flush();
        }
      } catch (IOException e){

      }
    }
  }

  /**
   * Removes the current `ClientHandler` from the list of active handlers
   * and broadcasts a message about the user leaving the chat.
   */
  public void removeClientHandler() {
    clientHandlers.remove(this);
    this.semaphore.release();
    broadcastMessage("[Server] : " + clientUserName + " has left the chat");
  }

  /**
   * Closes the associated socket and streams, removing the client handler.
   *
   * @param socket         The socket to be closed.
   * @param bufferedReader The BufferedReader to be closed.
   * @param bufferedWriter The BufferedWriter to be closed.
   */
  public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
    removeClientHandler();
    try {
      if (bufferedReader != null) {
        bufferedReader.close();
      }

      if (bufferedWriter != null) {
        bufferedWriter.close();
      }

      if (socket != null) {
        socket.close();
      }
    } catch (IOException e) {
    }
  }

  /**
   * Gets the username of the associated client.
   *
   * @return The username of the client.
   */
  public String getClientUserName() {
    return clientUserName;
  }
}
