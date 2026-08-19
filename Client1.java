import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {

        try{

            Socket socket =  new Socket("localhost",9000);

            DataInputStream dis =
                    new DataInputStream(socket.getInputStream());

            DataOutputStream output =
                    new DataOutputStream(socket.getOutputStream());


            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter your user name: ");
            String name = scanner.nextLine();
            output.writeUTF(name);
            output.flush();

            // Thread to send Message
            Thread sender = new Thread(() -> {

                while(true) {

                    try {
                        System.out.print(">");
                        String message =  scanner.nextLine();
                        output.writeUTF(message);
                        output.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            sender.start();

            // Main thread responsible to recieve msg
            while(socket.isConnected()){

                String message =  dis.readUTF();
                System.out.println("\n" + message);
                System.out.println(">");
            }

        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

}