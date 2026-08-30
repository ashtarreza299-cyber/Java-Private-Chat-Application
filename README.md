# Java Private Chat Application

A simple multi-client private chat application built with Java TCP sockets.

The project demonstrates how multiple clients can connect to a central server and send private messages to specific users instead of broadcasting messages to everyone.

## Features

* Multiple clients can connect to the server simultaneously.
* Each client has a unique username.
* Clients can send private messages to specific users.
* The server keeps track of connected users.
* Messages are routed directly to the intended recipient.
* Disconnected clients are removed from the active client list.
* Invalid message formats are handled by the server.

## Technologies Used

* Java
* TCP/IP Socket Programming
* `ServerSocket`
* `Socket`
* Multithreading
* `DataInputStream`
* `DataOutputStream`
* `HashMap`
* Java Collections

## Architecture

The application follows a simple client-server architecture:

```text
                ┌─────────────────────┐
                │       Server        │
                │                     │
                │  Connected Clients  │
                │                     │
                │  Ali  → Socket A    │
                │  Sara → Socket B    │
                │  John → Socket C    │
                └──────────┬──────────┘
                           │
             ┌─────────────┼─────────────┐
             │             │             │
             ▼             ▼             ▼
          Client A      Client B      Client C
            Ali           Sara          John
```

When a client sends a private message, the server finds the target user's connection and sends the message only to that client.

For example:

```text
Ali → Sara: Hello Sara!
```

The server looks up Sara:

```java
clients.get("Sara");
```

and sends the message through Sara's socket.

## Message Format

The client uses the following format for private messages:

```text
message
```

Example:

```text
Hello! How are you?
```

The server separates the receiver and message:

```text
Receiver: Sara
Message: Hello! How are you?
```

The server then sends the message to Sara.

## How It Works

### 1. Server

The server creates a `ServerSocket` on port `9000`:

```java
ServerSocket serverSocket = new ServerSocket(9000);
```

It continuously waits for clients:

```java
Socket socket = serverSocket.accept();
```

For every new connection, the server creates a new `HandleClient` thread.

```java
new HandleClient(socket).start();
```

This allows multiple clients to communicate with the server concurrently.

### 2. Client Registration

When a client connects, it first sends its username.

For example:

```text
Ali
```

The server stores the client in a `HashMap`:

```text
username → HandleClient
```

Example:

```text
Ali  → HandleClient
Sara → HandleClient
John → HandleClient
```

This allows the server to quickly find a specific user.

### 3. Sending a Private Message

Suppose Ali sends:

```text
Sara:Hello Sara!
```

The server parses the message and finds Sara:

```java
HandleClient target = clients.get("Sara");
```

Then it sends the message through Sara's output stream:

```java
target.output.writeUTF(message);
```

Only Sara receives the message.

If during the conversation the client wants to chat with 
other friend, the client can use the following command:

```text
/switch
```

implemented by the following code:

```java
// switch to another user to chat
// without restarting program
if(message.equals("/switch")){
    getTargetName(output);
}
```


## Project Structure

```text
JavaPrivateChat/
│
├── src/
│   ├── Server.java
│   ├── Client.java
│   └── ...
│
├── README.md
└── ...
```

The exact structure may vary depending on the IDE or build system used.

## How to Run

### 1. Start the Server

Run the server first.

The server listens on:

```text
localhost:9000
```

You should see:

```text
Server started on port 9000...
Waiting for clients...
```

### 2. Start the Clients

Run the client application multiple times.

For example:

```text
Client 1 → Ali
Client 2 → Sara
```

### 3. Enter the client user name

From Ali's client:

```text
ALi
```

From Sara's client:

```text
Sara
```

### 4. Who do you want to chat with (Enter the user name)

If Ali wants to chat with Sara:

```text
Sara
```

If Sara wants to chat with Ali:

```text
Ali
```

### 5. Send Message

Ali writes to Sara:

```text
Hello Sara i am Ali
```



## Concepts Learned

This project helped me practice several important Java and computer networking concepts:

* Client-server architecture
* TCP communication
* Socket programming
* Server-side connection management
* Java threads
* Concurrent clients
* Input/output streams
* Network communication
* Collections and `HashMap`
* Managing connected users
* Private message routing
* Handling client disconnections

## Future Improvements

Possible improvements for future versions include:

* [ ] Graphical user interface using Swing
* [ ] User authentication
* [ ] Password-based login
* [ ] Online users list
* [ ] Group chats
* [ ] Message timestamps
* [ ] Chat history
* [ ] Database integration
* [ ] File transfer
* [ ] End-to-end encryption
* [ ] Better message protocol using custom message objects
* [ ] Improved exception and connection handling

## What I Learned

The main goal of this project was to gain practical experience with Java networking.

Instead of using a high-level framework, I implemented the communication using Java's built-in socket APIs. This helped me understand how clients establish TCP connections, how the server manages multiple connections, and how data can be transferred between different processes.

## Author

**Sayed Ashtar Reza Entezar**

This project was created as part of my journey to improve my Java, networking, and backend development skills.
