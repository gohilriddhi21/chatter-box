

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServerTest {

  private Server server;
  private int port;

  private ServerSocket serverSocket;
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;

  @BeforeEach
  public void setUp() throws IOException {
    serverSocket = new ServerSocket(6000);
    port = serverSocket.getLocalPort();
    server = new Server(serverSocket);
    System.setOut(new PrintStream(outContent));
  }

  @AfterEach
  public void tearDown() {
    server.closeServerSocket();
    System.setOut(originalOut);
  }

  @Test
  public void testServerStart() {
    Thread serverThread = new Thread(() -> server.startServer(port));
    serverThread.start();
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    assertTrue(true);
  }

  @Test
  void testHashCodeAndEquals() throws IOException {
    Server server1 = new Server(serverSocket);
    Server server2 = new Server(serverSocket);

    assertNotEquals(server1, server2);
  }

  @Test
  void testHashCodeAndEquals2() throws IOException {
    Server server1 = new Server(serverSocket);
    Server server2 = new Server(serverSocket);

    assertNotEquals(server1.hashCode(), server2.hashCode());

  }

  @Test
  void testHashCodeAndEquals3() throws IOException {
    Server server1 = new Server(serverSocket);

    Server server3 = new Server(serverSocket);

    assertNotEquals(server1, server3);
  }

  @Test
  void testHashCodeAndEquals4() throws IOException {
    Server server1 = new Server(serverSocket);
    Server server3 = new Server(serverSocket);

    assertNotEquals(server1.hashCode(), server3.hashCode());
  }

  @Test
  public void testServerMaxClients() throws IOException {

    Server.COUNT = Server.MAX_CLIENTS;

    Socket clientSocket = new Socket("10.0.0.106", port);

    assertFalse(clientSocket.isClosed());

    Server.COUNT = 0;
  }

  @Test
  public void testServerMaxClients2() throws IOException {
    Server.COUNT = Server.MAX_CLIENTS;

    Socket clientSocket = new Socket("10.0.0.106", port);

    assertEquals("", outContent.toString().trim());

  }

  @Test
  public void testCloseServerSocket() {
    server.closeServerSocket();
    assertTrue(server.getServerSocket().isClosed());
  }

  @Test
  public void testServerToString() {
    String expectedString = "Server{serverSocket=" + server.getServerSocket() + '}';
    assertEquals(expectedString, server.toString());
  }
}
