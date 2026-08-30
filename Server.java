package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(9000)) {

            System.out.println("Server started on port 9000...");
            System.out.println("Waiting for clients...");

            while (true) {

                Socket socket = serverSocket.accept();

                System.out.println(
                        "New connection: "
                                + socket.getInetAddress().getHostAddress()
                );

                new org.example.Main.HandleClient(socket).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    static class HandleClient extends Thread {

        private final Socket socket;

        private final DataInputStream input;
        private final DataOutputStream output;

        private String userName;
        private String receiver;

        /*
         * username -> HandleClient
         *
         * Example:
         *
         * "Ali"  -> HandleClient(Ali)
         * "Sara" -> HandleClient(Sara)
         */

        private static final Map<String, org.example.Main.HandleClient> clients =
                Collections.synchronizedMap(new HashMap<>());

        public HandleClient(Socket socket) throws IOException {

            this.socket = socket;

            this.input =
                    new DataInputStream(
                            socket.getInputStream()
                    );

            this.output =
                    new DataOutputStream(
                            socket.getOutputStream()
                    );
        }


        @Override
        public void run() {

            try {

                /*
                 * The first message from the client
                 * must be its username.
                 */
                userName = input.readUTF();
                receiver = input.readUTF();

                /*
                 * Check whether username already exists.
                 */
                synchronized (clients) {

                    if (clients.containsKey(userName)) {

                        output.writeUTF(
                                "Username already exists."
                        );

                        output.flush();

                        return;
                    }

                    clients.put(userName, this);
                }

                System.out.println(
                        "Client connected: " + userName
                                + " - "
                                + socket.getInetAddress()
                                .getHostAddress()
                );

                /*
                 * Continuously receive messages.
                 */
                while (true) {

                    String message = input.readUTF();

                    /*
                     * Check message format.
                     */
                    if (message.isEmpty()) {
                        continue;
                    }



                    /*
                     * Don't allow empty values.
                     */
                    if (receiver.isEmpty()
                            || message.isEmpty()) {

                        output.writeUTF(
                                "Receiver and message cannot be empty."
                        );

                        output.flush();

                        continue;
                    }

                    /*
                     * Send private message.
                     */
                    sendPrivateMessage(
                            receiver,
                            message
                    );
                }


            }

            catch (IOException e) {

                System.out.println(
                        "Client disconnected: "
                                + userName
                );

            }
            finally {

                /*
                 * Remove client from map.
                 */
                if (userName != null) {

                    clients.remove(userName);

                    System.out.println(
                            "Removed client: "
                                    + userName
                    );
                }

                /*
                 * Close socket.
                 */
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
            }
        }


        private void sendPrivateMessage(String receiver, String message) {

            org.example.Main.HandleClient target;

            /*
             * Find the receiver.
             */
            synchronized (clients) {

                target = clients.get(receiver);
            }


            /*
             * Receiver doesn't exist.
             */
            if (target == null) {

                try {

                    output.writeUTF(
                            "User '" + receiver
                                    + "' is not online."
                    );

                    output.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return;
            }

            /*
             * Send message only to target.
             */
            try {

                target.output.writeUTF(
                        userName + ": " + message
                );

                target.output.flush();


            } catch (IOException e) {

                System.out.println(
                        "Could not send message to "
                                + receiver
                );
            }
        }
    }
}