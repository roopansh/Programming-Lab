package task3.teastall.server;

import task3.teastall.Constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends Thread {
    private Map<String, Integer> Items; // Item, Stock
    private Map<String, Integer> ItemDelay; // item, total delay
    private OrdersProcessor ordersProcessor;
    private ExecutorService ThreadPool;

    // constructor with port
    private Server() {
        Items = Constants.getInitialItems();
        ItemDelay = Constants.getInitialItemsDelay();
        ordersProcessor = new OrdersProcessor();
        ThreadPool = Executors.newSingleThreadExecutor();
        start();
    }

    @Override
    public void run() {
        // starts server and waits for a connection
        try {
            ServerSocket server = new ServerSocket(Constants.SERVER_PORT);
            System.out.println("Tea Stall Server started");
            while (true) {
                // initialize socket and input stream
                Socket socket = server.accept();

                // takes input from the client socket
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

                String line;

                // Receive first command
                line = dataInputStream.readUTF();
                if (line.equals(Constants.GET_AVAILABLE_LIST)) {
                    // Send message
                    System.out.println("Sending available items");
                    for (String item : Items.keySet()) {
                        dataOutputStream.writeUTF(item);
                    }
                    dataOutputStream.writeUTF(Constants.MESSAGE_END);
                    dataOutputStream.close();

                } else if (line.equals(Constants.PLACE_ORDER)) {
                    System.out.println("Receiving Orders");
                    // reads message from client until "END" is sent
                    String item;
                    int quantity;
                    Map<String, Integer> order = new HashMap<>();
                    line = dataInputStream.readUTF();
                    while (!line.equals(Constants.MESSAGE_END)) {
                        try {
                            item = line;
                            line = dataInputStream.readUTF();
                            quantity = Integer.parseInt(line);
                            if (Items.containsKey(item)) {
                                order.put(item, quantity);
                                System.out.println(item);
                                System.out.println(quantity);
                            }
                            line = dataInputStream.readUTF();
                        } catch (IOException i) {
                            System.out.println(i);
                        }
                    }
                    OrderProcessor orderProcessor = new OrderProcessor(this, order, socket);
                    ordersProcessor.Orders.add(orderProcessor);
                    ThreadPool.execute(ordersProcessor);
                }
            }
        } catch (IOException i) {
            System.out.println(i);
        }
    }

    Map<String, Integer> getItemDelay() {
        return ItemDelay;
    }

    Map<String, Integer> getItems() {
        return Items;
    }

    public static void main(String[] args) {
        new Server();
    }
}
