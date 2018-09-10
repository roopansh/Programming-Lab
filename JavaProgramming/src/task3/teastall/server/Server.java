package task3.teastall.server;

import task3.teastall.Constants;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {
    private Map<String, Integer> Items; // Item, Stock
    private List<Map<String, Integer>> OrdersRecieved;

    // constructor with port
    public Server(int port) {
        Items = Constants.getInitialItems();
        OrdersRecieved = new ArrayList<>();

        // starts server and waits for a connection
        try {
            ServerSocket server = new ServerSocket(port);
            System.out.println("Tea Stall Server started");

            // initialize socket and input stream
            Socket socket = server.accept();

            // takes input from the client socket
            DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            String line = "";

            // Send message
            System.out.println("Sending available items");
            for (String item : Items.keySet()) {
                dataOutputStream.writeUTF(item);
            }
            dataOutputStream.writeUTF(Constants.MESSAGE_END);
            dataOutputStream.close();

            System.out.println("Receiving Orders");
            // reads message from client until "END" is sent
            while (!line.equals(Constants.MESSAGE_END)) {
                try {
                    line = dataInputStream.readUTF();
                    System.out.println(line);
                } catch (IOException i) {
                    System.out.println(i);
                }
            }
            // close connection
            socket.close();
            dataInputStream.close();
        } catch (IOException i) {
            System.out.println(i);
        }
    }

    public static void main(String[] args) {
        new Server(Constants.SERVER_PORT);
    }
}
