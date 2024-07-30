import java.util.concurrent.Semaphore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ClientHandlerTest {

  private ClientHandler clientHandler;
  private Socket testSocket;
  private BufferedReader testBufferedReader;
  private BufferedWriter testBufferedWriter;

  @BeforeEach
  void setUp() throws IOException {
    testSocket = new Socket();
    testBufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream("testUser\n".getBytes())));
    testBufferedWriter = new BufferedWriter(new OutputStreamWriter(new ByteArrayOutputStream()));

    clientHandler = new ClientHandler(testSocket, new Semaphore(2));
    clientHandler.setBufferedReader(testBufferedReader);
    clientHandler.setBufferedWriter(testBufferedWriter);
  }

  @Test
  void testConstructor() {
    assertNotNull(clientHandler);
  }

  @Test
  void testRun() {
    clientHandler.run();
  }

  @Test
  void testProcessInput() {
    clientHandler.processInput("TestMessage");
  }
  @Test
  void testBroadcastMessage() throws IOException {
    clientHandler.broadcastMessage("BroadcastMessage");

    testBufferedWriter.flush();
    assert(true);
  }

}
