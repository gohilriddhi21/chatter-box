# Chat Room Application

This is a simple chat room application implemented in Java. The application consists of a server and a client that communicate over a network to enable users to send and receive messages in a chat room.

## How to Run

### Server

1. Run the server: Modify Run arguments
    ```bash
    <port>
    ```
   Replace `<port>` with the desired port number (e.g., 1234).

### Client

1. Run the client: Modify Run Arguments

    ```bash
    <host> <port>
    ```

   Replace `<host>` with the server's hostname or IP address and `<port>` with the server's port number.
   For example, host = localhost port = 1234

## Entry Point

The entry point for the program is the `main` method in the `Server` class for the server and the `Client` class for the client.

## Key Classes/Methods

### Server

- `Server`: Represents the chat room server. The `main` method initializes the server and listens for incoming connections.
- The Server class represents a simple server that accepts incoming client connections.
- It uses a ServerSocket to listen for client connections and delegates handling to {@link ClientHandler}.

### Client

- `Client`: Represents a client in the chat room. The `main` method initializes the client, establishes a connection to the server, and starts the client. The `sendMessage` method allows users to send messages, and the `listenForMessage` method listens for incoming messages.
- It establishes a connection to a server, sends and receives messages, and provides various commands for interacting with the chat room.

### ChatRoomProtocol

- `ChatRoomProtocol`: Represents the Protocol using which the client and server encode and decode messages. 

### ClientHandler

- `ClientHandler`: Represents the  handler which handles all clients messages.
- The `ClientHandler` class represents a thread responsible for handling communication with an individual client in a chat room. It manages sending and receiving messages, processing various types of messages, and handling disconnections.

## Assumptions

- The server and client are intended to run on the same machine for local testing.
- The Client doesn't terminate through the terminal.
- Exception handling is implemented to gracefully handle errors.

## How to Run in IntelliJ UI

1. Open the project in IntelliJ IDEA.
2. Right-click on the `Server` class, select "Run Server.main()".
3. Right-click on the `Client` class, select "Run Client.main()".



