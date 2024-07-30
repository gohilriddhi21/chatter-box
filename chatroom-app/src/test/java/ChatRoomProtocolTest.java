import java.util.ArrayList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChatRoomProtocolTest {

  @Test
  void encodeBroadcastMessage() {
    ChatRoomProtocol protocol = new ChatRoomProtocol();
    byte[] encodedMessage = protocol.encodeBroadcastMessage("yashvi", "Hello, everyone!");

    assertNotNull(encodedMessage);
  }

  @Test
  void encodeDirectMessage() {
    ChatRoomProtocol protocol = new ChatRoomProtocol();
    byte[] encodedMessage = protocol.encodeDirectMessage("yashvi", "riddhi", "Private message!");

    assertNotNull(encodedMessage);
  }

  @Test
  void encodeDisconnectMessage() {
    ChatRoomProtocol protocol = new ChatRoomProtocol();
    byte[] encodedMessage = protocol.encodeDisconnectMessage("yashvi");

    assertNotNull(encodedMessage);
  }

  @Test
  void encodeQueryConnectedUsers() {
    ChatRoomProtocol protocol = new ChatRoomProtocol();
    byte[] encodedMessage = protocol.encodeQueryConnectedUsers("yashvi");

    assertNotNull(encodedMessage);
  }

  @Test
  void encodeConnectMessage() {
    ChatRoomProtocol protocol = new ChatRoomProtocol();
    byte[] encodedMessage = protocol.encodeConnectMessage("yashvi");

    assertNotNull(encodedMessage);
  }

  @Test
  void encodeConnectResponse() {
    ChatRoomProtocol protocol = new ChatRoomProtocol();
    byte[] encodedMessage = protocol.encodeConnectResponse(true, "Connected successfully!");

    assertNotNull(encodedMessage);
  }

  @Test
  void encodeSendInsult() {
    ChatRoomProtocol protocol = new ChatRoomProtocol();
    byte[] encodedMessage = protocol.encodeSendInsult("yashvi", "riddhi");

    assertNotNull(encodedMessage);
  }

  @Test
  void encodeQueryResponse() {
    ChatRoomProtocol protocol = new ChatRoomProtocol();
    ArrayList<ClientHandler> users = new ArrayList<>();


    byte[] encodedMessage = protocol.encodeQueryResponse(users);
    assertNotNull(encodedMessage);
  }

  @Test
  void decodeFrame() {
    String message = "Hi all!";
    byte[] decodedBytes = ChatRoomProtocol.decodeFrame(message);

    assertNotNull(decodedBytes);

  }
}
