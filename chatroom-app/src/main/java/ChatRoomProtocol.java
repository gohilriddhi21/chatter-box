

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * The `ChatRoomProtocol` class defines the protocol used for communication in a chat room.
 * It includes constants for message types, methods to encode different message types into byte arrays,
 * and a method to decode a message frame. The class uses a simple protocol with message types encoded as integers.
 */
public class ChatRoomProtocol {
  /**
   * Message indicating connect message
   */
  public static final int CONNECT_MESSAGE = 19;

  @Override
  public String toString() {
    return "ChatRoomProtocol{}";
  }

  /**
   * Response to a connection request.
   */
  public static final int CONNECT_RESPONSE = 20;

  /**
   * Message indicating a user disconnection.
   */
  public static final int DISCONNECT_MESSAGE = 21;

  /**
   * Request to query connected users.
   */
  public static final int QUERY_CONNECTED_USERS = 22;

  /**
   * Response to a query for connected users.
   */
  public static final int QUERY_USER_RESPONSE = 23;

  /**
   * Broadcast message to all connected users.
   */
  public static final int BROADCAST_MESSAGE = 24;

  /**
   * Direct message between two users.
   */
  public static final int DIRECT_MESSAGE = 25;

  /**
   * Message to send an insult to a specific user.
   */
  public static final int SEND_INSULT = 27;

  /**
   * Separator used for message framing.
   */
  public static final String FRAME_SEPARATOR = " ";

  /**
   * Encodes a broadcast message into a byte array.
   * @param sender The sender of the broadcast message.
   * @param message The content of the broadcast message.
   * @return Byte array representing the encoded broadcast message.
   */
  public byte[] encodeBroadcastMessage(String sender, String message) {
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {

      dataOutputStream.writeInt(BROADCAST_MESSAGE);
      encodeParameters(dataOutputStream, sender);
      encodeParameters(dataOutputStream, message);
      return byteArrayOutputStream.toByteArray();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Encodes a direct message into a byte array.
   * @param sender The sender of the direct message.
   * @param recipient The recipient of the direct message.
   * @param message The content of the direct message.
   * @return Byte array representing the encoded direct message.
   */
  public byte[] encodeDirectMessage(String sender, String recipient, String message) {
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {
      dataOutputStream.writeInt(DIRECT_MESSAGE);
      encodeParameters(dataOutputStream, sender);
      encodeParameters(dataOutputStream, recipient);
      encodeParameters(dataOutputStream, message);
      return byteArrayOutputStream.toByteArray();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Encodes a disconnect message into a byte array.
   * @param sender The sender requesting disconnection.
   * @return Byte array representing the encoded disconnect message.
   */
  public byte[] encodeDisconnectMessage(String sender) {
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {

      dataOutputStream.writeInt(DISCONNECT_MESSAGE);
      encodeParameters(dataOutputStream, sender);
      return byteArrayOutputStream.toByteArray();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }


  /**
   * Encodes a query for connected users into a byte array.
   * @param sender The sender initiating the query.
   * @return Byte array representing the encoded query for connected users.
   */
  public byte[] encodeQueryConnectedUsers(String sender) {
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {

      dataOutputStream.writeInt(QUERY_CONNECTED_USERS);
      encodeParameters(dataOutputStream, sender);
      return byteArrayOutputStream.toByteArray();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Encodes a connection message into a byte array.
   * @param sender The sender initiating the connection.
   * @return Byte array representing the encoded connection message.
   */
  public byte[] encodeConnectMessage(String sender) {
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {

      dataOutputStream.writeInt(CONNECT_MESSAGE);
      encodeParameters(dataOutputStream, sender);
      return byteArrayOutputStream.toByteArray();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Encodes a connection response into a byte array.
   * @param success Indicates whether the connection was successful.
   * @param message Additional message accompanying the response.
   * @return Byte array representing the encoded connection response.
   */
  public byte[] encodeConnectResponse(boolean success, String message) {
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {

      dataOutputStream.writeInt(CONNECT_RESPONSE);
      dataOutputStream.write(FRAME_SEPARATOR.getBytes(StandardCharsets.UTF_8));
      dataOutputStream.writeBoolean(success);
      encodeParameters(dataOutputStream, message);
      return byteArrayOutputStream.toByteArray();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Encodes an insult message into a byte array.
   * @param sender The sender of the insult.
   * @param recipient The recipient of the insult.
   * @return Byte array representing the encoded insult message.
   */
  public byte[] encodeSendInsult(String sender, String recipient) {
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {

      dataOutputStream.writeInt(SEND_INSULT);
      encodeParameters(dataOutputStream, sender);
      encodeParameters(dataOutputStream, recipient);
      return byteArrayOutputStream.toByteArray();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Encodes a query response into a byte array.
   * @param users List of connected users to be included in the response.
   * @return Byte array representing the encoded query response.
   */
  public byte[] encodeQueryResponse(ArrayList<ClientHandler> users) {
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {

      dataOutputStream.writeInt(QUERY_USER_RESPONSE);
      dataOutputStream.write(FRAME_SEPARATOR.getBytes(StandardCharsets.UTF_8));
      dataOutputStream.writeInt(users.size());
      for (ClientHandler user : users) {
        encodeParameters(dataOutputStream, user.getClientUserName());
      }
      return byteArrayOutputStream.toByteArray();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Decodes a message frame into a byte array.
   * @param message The input message string to be decoded.
   * @return Byte array representing the decoded message frame.
   */
  public static byte[] decodeFrame(String message) {
    return message.getBytes(StandardCharsets.UTF_8);
  }

  /**
   * Encodes a parameter and its length into the data output stream.
   * @param dataOutputStream The data output stream to write to.
   * @param param The parameter to be encoded.
   * @throws IOException If an I/O error occurs.
   */
  private void encodeParameters(DataOutputStream dataOutputStream, String param) throws IOException {
    dataOutputStream.write(FRAME_SEPARATOR.getBytes(StandardCharsets.UTF_8));
    dataOutputStream.writeInt(param.length());
    dataOutputStream.write(FRAME_SEPARATOR.getBytes(StandardCharsets.UTF_8));
    dataOutputStream.write(param.getBytes(StandardCharsets.UTF_8));
  }
}
