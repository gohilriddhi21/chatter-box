import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * The Server class represents a simple server that accepts incoming client connections.
 * It uses a ServerSocket to listen for client connections and delegates handling to {@link ClientHandler}.
 */
public class Server {


  /**
   * @return server socket
   */
  public ServerSocket getServerSocket() {
    return serverSocket;
  }

  /**
   * The maximum number of clients the server can handle.
   */
  public static final int MAX_CLIENTS = 11;

  private ExecutorService executorService;

  private static Semaphore semaphore;

  /**
   * The format for providing input arguments when running the server.
   */
  public static final String SERVER_INPUT_FORMAT = "Usage: java <port>";

  /**
   * Message indicating a new client connection.
   */
  public static final String NEW_CLIENT_INTRODUCTION_MESSAGE = "\nA new Client connected.";

  /**
   * Message indicating that the maximum number of clients has been reached, and new connections are refused.
   */
  public static final String MAX_CLIENT_REACHED_MESSAGE = "\nConnection refused. Maximum clients reached.";

  /**
   * The minimum number of arguments required when starting the server.
   */
  public static final int MINIMUM_ARGUMENT_REQUIRED = 1;

  /**
   * Abnormal exit code used when the required number of arguments is not provided.
   */
  public static final int ABNORMAL_EXIT_CODE = 0;

  /**
   * The index of the port argument in the command line arguments.
   */
  public static final int ARGUMENT_PORT_INDEX = 0;

  /**
   * A counter to keep track of the number of connected clients.
   */
  public static int COUNT = 0;

  /**
   * The {@link ServerSocket} used by the server to accept client connections.
   */
  private ServerSocket serverSocket;

  /**
   * Constructs a new {@code Server} with the specified {@link ServerSocket}.
   *
   * @param serverSocket the ServerSocket to be used by the server.
   */
  public Server(ServerSocket serverSocket) {
    this.serverSocket = serverSocket;
    this.semaphore = new Semaphore(MAX_CLIENTS);
  }

  /**
   * Starts the server, listening for incoming client connections.
   *
   * @param port the port on which the server should listen for connections.
   */
  public void startServer(int port) {
    try {
      System.out.format("Server Started. Listening to port: %s ", port);
      executorService = Executors.newCachedThreadPool();
      while (!serverSocket.isClosed()) {
        if (semaphore.availablePermits() != 0) {
          semaphore.acquire();
          Socket socket = serverSocket.accept();
          System.out.println(NEW_CLIENT_INTRODUCTION_MESSAGE);
          executorService.execute(new ClientHandler(socket, semaphore));
        } else {
          System.out.println(MAX_CLIENT_REACHED_MESSAGE);
          break;
        }
      }
    } catch (IOException | InterruptedException e) {

    }
  }

  /**
   * Closes the server socket.
   */
  public void closeServerSocket() {
    try {
      if (serverSocket != null && !serverSocket.isClosed()) {
        serverSocket.close();
      }
    } catch(IOException e) {

    }
  }

  /**
   * Returns a string representation of the {@code Server} object.
   *
   * @return a string representation of the object.
   */
  @Override
  public String toString() {
    return "Server{" +
        "serverSocket=" + serverSocket +
        '}';
  }

  /**
   * Returns the hash code value for the {@code Server} object.
   *
   * @return a hash code value for this object.
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param obj the reference object with which to compare.
   * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }

  /**
   * The main method that starts the server.
   *
   * @param args the command-line arguments. Expects a single argument - the port number.
   */
  public static void main(String[] args) {
    if (args.length != MINIMUM_ARGUMENT_REQUIRED) {
      System.out.println(SERVER_INPUT_FORMAT);
      System.exit(ABNORMAL_EXIT_CODE);
    }
    try {
      int port = Integer.parseInt(args[ARGUMENT_PORT_INDEX]);
      ServerSocket serverSocket = new ServerSocket(port);
      Server server = new Server(serverSocket);
      server.startServer(port);
    } catch (Exception e) {

    }
  }
}
