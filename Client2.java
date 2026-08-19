import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client2 {

    public static void main(String[] args) {

        try{
            Socket socket = new Socket("localhost", 9000);

            DataInputStream dis = new DataInputStream(socket.getInputStream());

            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter your user Name: ");
            String userName = scanner.nextLine();

            dos.writeUTF(userName);
            dos.flush();

            // Thread to send Message
            Thread sender = new Thread(() -> {

                while(true) {

                    try {
                        System.out.print(">");
                        String message =  scanner.nextLine();
                        dos.writeUTF(message);
                        dos.flush();
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

        catch(IOException e) {
            System.out.println(e.getMessage());
        }

    }

}