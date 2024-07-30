import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * The `Client` class represents a client in a simple chat room application.
 * It establishes a connection to a server, sends and receives messages, and
 * provides various commands for interacting with the chat room.
 */
public class Client {

  private Socket socket;
  private BufferedReader bufferedReader;
  private BufferedWriter bufferedWriter;
  private String userName;
  private ChatRoomProtocol chatRoomProtocol;

  /**
   * The entry input message prompting the user to enter their username.
   */
  public static String ENTRY_INPUT = "Enter your Username: ";

  /**
   * The index of the host argument in the command-line arguments array.
   */
  public static int HOST_ARGUMENT_INDEX = 0;

  /**
   * The abnormal exit code used when the program terminates unexpectedly.
   */
  public static int ABNORMAL_EXIT_CODE = 0;

  /**
   * The format for providing command-line arguments.
   */
  public static String ARGUMENT_INPUT_FORMAT = "Usage: java <host> <port>";

  /**
   * The index of the port argument in the command-line arguments array.
   */
  public static int PORT_ARGUMENT_INDEX = 1;

  /**
   * The minimum number of command-line arguments required.
   */
  public static int MINIMUM_ARGUMENT_REQUIRED = 2;

  /**
   * The index representing the second part of a message in command parsing.
   */
  public static int MESSAGE_PART_INDEX = 1;

  /**
   * The index representing the target username part in command parsing.
   */
  public static int TARGET_USERNAME_PART_INDEX = 0;

  /**
   * Constant index value 0.
   */
  public static int INDEX_0 = 0;

  /**
   * Constant index value 1.
   */
  public static int INDEX_1 = 1;

  /**
   * Constant index value 2.
   */
  public static int INDEX_2 = 2;

  /**
   * Introduction message for available commands.
   */
  public static String COMMANDS_INTRODUCTION = "Available Commands:";

  /**
   * The string separator used for parsing commands.
   */
  public static String INPUT_STRING_SEPARATOR = " ";

  /**
   * Usage message for the logoff command.
   */
  public static String COMMAND_LOGOFF_USAGE = "- logoff: sends a DISCONNECT_MESSAGE to the server";

  /**
   * Usage message for the who command.
   */
  public static String COMMAND_WHO_USAGE = "- who: sends a QUERY_CONNECTED_USERS to the server";

  /**
   * Usage message for the user command.
   */
  public static String COMMAND_USER_USAGE = "- @user: sends a DIRECT_MESSAGE to the specified user to the server";

  /**
   * Usage message for the all command.
   */
  public static String COMMAND_ALL_USAGE = "- @all: sends a BROADCAST_MESSAGE to the server, to be sent to all users connected";

  /**
   * Usage message for the help command.
   */
  public static String COMMAND_HELP_USAGE = "- ?: prints all the commands available";

  /**
   * Usage message for the insult command.
   */
  public static String COMMAND_INSULT_USAGE = "- !user: sends a SEND_INSULT message to the server, to be sent to the specified user";

  /**
   * The logoff command string.
   */
  public static String COMMAND_LOGOFF = "logoff";

  /**
   * The who command string.
   */
  public static String COMMAND_WHO = "who";

  /**
   * The user command string.
   */
  public static String COMMAND_USER = "@";

  /**
   * The all command string.
   */
  public static String COMMAND_ALL = "@all";

  /**
   * The insult command string.
   */
  public static String COMMAND_INSULT = "!";

  /**
   * The help command string.
   */
  public static String COMMAND_HELP = "?";

  /**
   * The message indicating disconnection from the server.
   */
  public static String DISCONNECT_MESSAGE = "You are no longer connected.";

  /**
   * The message indicating connection refusal.
   */
  public static String CONNECTION_REFUSED_MESSAGE = "Connection refused.";

  /**
   * The introduction message for connected clients.
   */
  public static String CONNECTED_CLIENTS_INTRODUCTION_MESSAGE = "[Server] : Connected Clients are:";

  /**
   * Displays all available commands to the user.
   */
  private void displayAllCommands() {
    System.out.println(COMMANDS_INTRODUCTION);
    System.out.println(COMMAND_LOGOFF_USAGE);
    System.out.println(COMMAND_WHO_USAGE);
    System.out.println(COMMAND_USER_USAGE);
    System.out.println(COMMAND_ALL_USAGE);
    System.out.println(COMMAND_INSULT_USAGE);
    System.out.println(COMMAND_HELP_USAGE);
  }

  /**
   * Constructs a new `Client` instance with the specified socket and username.
   *
   * @param socket   The socket used for communication with the server.
   * @param userName The username of the client.
   */
  public Client(Socket socket, String userName) {
    try {
      this.socket = socket;
      this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
      this.userName = userName;
      chatRoomProtocol = new ChatRoomProtocol();
      bufferedWriter.write(userName);
      bufferedWriter.newLine();
      bufferedWriter.flush();
      sendEncodedMessage(chatRoomProtocol.encodeConnectMessage(this.userName));
    } catch (IOException e) {
      closeEverything(socket, bufferedReader, bufferedWriter);
      System.out.println(CONNECTION_REFUSED_MESSAGE);
    }
  }

  /**
   * Sends an encoded message to the server based on user input.
   */
  public void sendMessage() {
    try {
      bufferedWriter.write(userName);
      bufferedWriter.newLine();
      bufferedWriter.flush();

      Scanner scanner = new Scanner(System.in);
      while (socket.isConnected()) {
        String message = scanner.nextLine();

        if (message.equals(COMMAND_LOGOFF)) {
          sendEncodedMessage(chatRoomProtocol.encodeDisconnectMessage(this.userName));
        } else if (message.equals(COMMAND_WHO)) {
          sendEncodedMessage(chatRoomProtocol.encodeQueryConnectedUsers(this.userName));
        } else if (message.startsWith(COMMAND_ALL)) {
          message = message.split(COMMAND_ALL + INPUT_STRING_SEPARATOR)[MESSAGE_PART_INDEX];
          sendEncodedMessage(chatRoomProtocol.encodeBroadcastMessage(this.userName, message));
        } else if (message.startsWith(COMMAND_USER)) {
          String[] parts = message.split(INPUT_STRING_SEPARATOR, INDEX_2);
          String targetUser;
          if (parts.length <= INDEX_1 || parts[INDEX_1] == null || parts[INDEX_1].isEmpty()) {
            parts = new String[]{parts[INDEX_0], INPUT_STRING_SEPARATOR};
          }
          targetUser = parts[TARGET_USERNAME_PART_INDEX].substring(INDEX_1);
          sendEncodedMessage(chatRoomProtocol.encodeDirectMessage(this.userName, targetUser, parts[INDEX_1]));
        } else if (message.startsWith(COMMAND_INSULT)) {
          String[] parts = message.split(INPUT_STRING_SEPARATOR, INDEX_2);
          String targetUser = parts[TARGET_USERNAME_PART_INDEX].substring(INDEX_1);
          sendEncodedMessage(chatRoomProtocol.encodeSendInsult(this.userName, targetUser));
        } else if (message.equals(COMMAND_HELP)) {
          displayAllCommands();
        } else {
          sendEncodedMessage(chatRoomProtocol.encodeBroadcastMessage(this.userName, message));
        }
      }
    } catch (IOException e) {
      closeEverything(socket, bufferedReader, bufferedWriter);
      return;
    }
  }

  /**
   * Listens for incoming messages from the server and processes them accordingly.
   */
  public void listenForMessage() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        String msgFromGrpChat;

        while (socket.isConnected()) {
          try {
            msgFromGrpChat = bufferedReader.readLine();
            processOutput(msgFromGrpChat);
          } catch (IOException e) {
            break;
          }
        }
      }
    }).start();
  }

  /**
   * Checks if the user has disconnected based on the received message.
   *
   * @param message The message received from the server.
   * @throws IOException If an I/O error occurs.
   */
  private void checkIfUserHasDisconnected(String message) throws IOException {
    if (message.equals(DISCONNECT_MESSAGE)) {
      this.bufferedWriter.write(DISCONNECT_MESSAGE);
      System.exit(0);
    }
  }

  /**
   * Processes the output received from the server.
   *
   * @param msgFromGrpChat The message received from the server.
   */
  public void processOutput(String msgFromGrpChat) {
    if(msgFromGrpChat.equals("MAX CLIENTS REACHED.")){
      System.out.println(msgFromGrpChat);
      System.exit(0);
    }
    if (msgFromGrpChat != null) {
      byte[] frame = ChatRoomProtocol.decodeFrame(msgFromGrpChat);
      try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(frame);
          DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream)) {
        int messageType = dataInputStream.readInt();
        if (messageType == ChatRoomProtocol.CONNECT_RESPONSE) {
          dataInputStream.readNBytes(ChatRoomProtocol.FRAME_SEPARATOR.length());
          dataInputStream.readBoolean();
          dataInputStream.readNBytes(ChatRoomProtocol.FRAME_SEPARATOR.length());
          int messageSize = dataInputStream.readInt();
          dataInputStream.readNBytes(ChatRoomProtocol.FRAME_SEPARATOR.length());
          byte[] messageBytes = new byte[messageSize];
          dataInputStream.readFully(messageBytes);
          String actualMessage = new String(messageBytes, StandardCharsets.UTF_8);
          System.out.println(actualMessage);
          checkIfUserHasDisconnected(actualMessage);
        } else if (messageType == ChatRoomProtocol.QUERY_USER_RESPONSE) {
          System.out.println(CONNECTED_CLIENTS_INTRODUCTION_MESSAGE);
          dataInputStream.readNBytes(ChatRoomProtocol.FRAME_SEPARATOR.length());
          int numUsers = dataInputStream.readInt();
          for (int i = INDEX_0; i < numUsers; i++) {
            dataInputStream.readNBytes(ChatRoomProtocol.FRAME_SEPARATOR.length());
            int userNameSize = dataInputStream.readInt();
            dataInputStream.readNBytes(ChatRoomProtocol.FRAME_SEPARATOR.length());
            byte[] userNameBytes = new byte[userNameSize];
            dataInputStream.readFully(userNameBytes);
            String actualMessage = new String(userNameBytes, StandardCharsets.UTF_8);
            System.out.println(COMMAND_USER + actualMessage);
          }
        } else {
          System.out.println(msgFromGrpChat);
        }
      } catch (Exception e) {
      }
    }
  }

  /**
   * Sends an encoded message to the server.
   *
   * @param frame The encoded message frame.
   * @throws IOException If an I/O error occurs.
   */
  private void sendEncodedMessage(byte[] frame) throws IOException {
    bufferedWriter.write(new String(frame, StandardCharsets.UTF_8));
    bufferedWriter.newLine();
    bufferedWriter.flush();
  }

  /**
   * Closes the socket and associated streams.
   *
   * @param socket         The socket to be closed.
   * @param bufferedReader The BufferedReader to be closed.
   * @param bufferedWriter The BufferedWriter to be closed.
   */
  public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
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

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }

  /**
   * The main entry point of the application.
   * Parses command-line arguments, establishes a connection to the server, and starts the client.
   *
   * @param args The command-line arguments, expected to be the host and port.
   */
  public static void main(String[] args) {
    if (args.length != MINIMUM_ARGUMENT_REQUIRED) {
      System.out.println(ARGUMENT_INPUT_FORMAT);
      System.exit(ABNORMAL_EXIT_CODE);
    }
    String host = args[HOST_ARGUMENT_INDEX];
    int port = Integer.parseInt(args[PORT_ARGUMENT_INDEX]);
    try {
      Scanner scanner = new Scanner(System.in);
      System.out.println(ENTRY_INPUT);
      String username = scanner.nextLine();
      Socket socket = new Socket(host, port);
      Client client = new Client(socket, username);
      client.listenForMessage();
      client.sendMessage();
    } catch (Exception e) {
    }
  }
}
