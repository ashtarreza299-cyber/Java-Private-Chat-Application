import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {


    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        try{

            Socket socket =  new Socket("localhost",8000);

            DataInputStream dis =
                    new DataInputStream(socket.getInputStream());

            DataOutputStream output =
                    new DataOutputStream(socket.getOutputStream());


            // Get user name and target name
            getUserName(output);

            getTargetName(output);

            sendMessage(output);

            // Main thread responsible to receive msg
            receiveMessage(socket, dis);

        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }

    }


    public static void getUserName(DataOutputStream output) throws IOException {
        System.out.println("Enter your user name: ");
        String name = scanner.nextLine().trim();

        while(name.isEmpty()) {

            System.out.println("User name can not be left empty, Enter your user name: ");
            name = scanner.nextLine().trim();
        }


        output.writeUTF(name);
        output.flush();
    }

    public static void getTargetName(DataOutputStream output) throws IOException {
        System.out.println("Who you want to chat with(Enter the user name): ");
        String target = scanner.nextLine().trim();

        while(target.isEmpty()) {

            System.out.println("User target can not be left empty, Enter your target user name: ");
            target = scanner.nextLine().trim();
        }


        output.writeUTF(target);
        output.flush();
    }

    public static void sendMessage(DataOutputStream output) throws IOException {
        // Thread to send Message
        Thread sender = new Thread(() -> {

            while(true) {

                try {
                    System.out.print(">");
                    String message =  scanner.nextLine();

                    // switch to another user to chat
                    // without restarting program
                    if(message.equals("/switch")){

                        getTargetName(output);
                    }

                    output.writeUTF(message);
                    output.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        sender.start();
    }

    public static void receiveMessage(Socket socket, DataInputStream dis) throws IOException {

        while(socket.isConnected()){

            String message =  dis.readUTF();
            System.out.println("\n" + message);
            System.out.println(">");
        }
    }

}