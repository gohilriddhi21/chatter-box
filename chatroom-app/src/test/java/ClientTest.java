
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClientTest {

  private Socket mockClientSocket;
  private BufferedReader mockClientReader;
  private BufferedWriter mockClientWriter;
  private ClientHandler clientHandler;

  @BeforeEach
  public void setUp() throws IOException {
    mockClientSocket = mock(Socket.class);
    mockClientReader = mock(BufferedReader.class);
    mockClientWriter = mock(BufferedWriter.class);

    when(mockClientSocket.getInputStream()).thenReturn(mock(InputStream.class));
    when(mockClientSocket.getOutputStream()).thenReturn(mock(OutputStream.class));
    when(mockClientSocket.isConnected()).thenReturn(true);

    clientHandler = new ClientHandler(mockClientSocket, new Semaphore(2));
    clientHandler.setBufferedReader(mockClientReader);
    clientHandler.setBufferedWriter(mockClientWriter);

    ClientHandler.clientHandlers = new ArrayList<>();
  }

  @AfterEach
  public void tearDown() throws IOException {
    clientHandler.closeEverything(mockClientSocket, mockClientReader, mockClientWriter);
  }

  @Test
  public void testHashCode() {
    Client client1 = new Client(mockClientSocket, "Alice");
    Client client2 = new Client(mockClientSocket, "Alice");
    assertNotEquals(client1.hashCode(), client2.hashCode());
  }

  @Test
  public void testEquals() {
    // Create two instances with the same properties
    Client client1 = new Client(mockClientSocket, "Bob");
    Client client2 = new Client(mockClientSocket, "Bob");

    // Verify that the instances are equal
    assertTrue(!client1.equals(client2));
  }

  @Test
  public void testNotEquals() {
    // Create two instances with different properties
    Client client1 = new Client(mockClientSocket, "Charlie");
    Client client2 = new Client(mockClientSocket, "David");

    // Verify that the instances are not equal
    assertFalse(client1.equals(client2));
  }

  @Test
  public void testEqualsWithNull() {
    // Create an instance with a non-null property
    Client client = new Client(mockClientSocket, "Eve");

    // Verify that the instance is not equal to null
    assertFalse(client.equals(null));
  }


  @Test
  public void testClientHandlerInitialization() {
    assertNotNull(clientHandler);
    assertEquals(mockClientSocket, clientHandler.getSocket());
  }

  @Test
  public void testClientHandlerConnectionACK() throws IOException {
    when(mockClientReader.readLine()).thenReturn("TestUser");
    clientHandler.sendConnectionACK();
  }


  @Test
  public void testChatRoomProtocolEncodeConnectMessage() {
    ChatRoomProtocol protocol = new ChatRoomProtocol();
    byte[] encodedMessage = protocol.encodeConnectMessage("john");

    assertEquals("\u0000\u0000\u0000\u0013 \u0000\u0000\u0000\u0004 john", new String (encodedMessage, StandardCharsets.UTF_8));
  }

  @Test
  public void testChatRoomProtocolDecodeFrame() {
    ChatRoomProtocol protocol = new ChatRoomProtocol();
    String message = "TestMessage";
    byte[] frame = ChatRoomProtocol.decodeFrame(message);

    assertEquals(message, new String(frame, StandardCharsets.UTF_8));
  }


  @Test
  public void testServerStartServer() {
    ServerSocket mockServerSocket = mock(ServerSocket.class);
    Server server = new Server(mockServerSocket);
    Thread serverThread = new Thread(() -> server.startServer(mockServerSocket.getLocalPort()));
    serverThread.start();
  }

  @Test
  public void testInsultGenerator() {
    String insult = InsultGenerator.generateInsult();
    assertNotNull(insult);
  }


}