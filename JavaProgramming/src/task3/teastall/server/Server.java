package task3.teastall.server;

import task3.teastall.Constants;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Server extends Thread {
    private Map<String, Integer> Items; // Item, Stock
    private Map<String, Integer> ItemDelay; // item, total delay
    private OrdersProcessor ordersProcessor;
    private Map<String, ItemsProcessor> itemsProcessorMap;

    // constructor with port
    private Server() {
        Items = Constants.getInitialItems();
        ItemDelay = Constants.getInitialItemsDelay();
        itemsProcessorMap = new HashMap<>();
        ordersProcessor = new OrdersProcessor();
        for (String item : Items.keySet()) {
            itemsProcessorMap.put(item, new ItemsProcessor(item));
        }
        generateGui();
        start();
    }

    private void generateGui() {
        JFrame f = new JFrame();
        JTextArea ta = new JTextArea(200, 200);
        JLabel itemLabel = new JLabel("Select the item to order");
        JPanel p1 = new JPanel();
        p1.add(itemLabel);
        p1.add(ta);
        JPanel p2 = new JPanel();
        JPanel p3 = new JPanel();
        JTabbedPane tp = new JTabbedPane();
        tp.setBounds(50, 50, 200, 200);
        tp.add("Orders", p1);
        tp.add("Stock", p2);
        tp.add("help", p3);
        f.add(tp);
        f.setSize(400, 400);
        f.setLayout(null);
        f.setVisible(true);
    }

    @Override
    public void run() {
        ordersProcessor.start();
        for(ItemsProcessor itemsProcessor : itemsProcessorMap.values()){
            itemsProcessor.start();
        }

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
                    System.out.println("Receiving orders");
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
                    OrderProcessor orderProcessor = new OrderProcessor(this, order, LocalDateTime.now(), socket);
                    ordersProcessor.orders.add(orderProcessor);
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

    public Map<String, ItemsProcessor> getItemsProcessorMap() {
        return itemsProcessorMap;
    }

    public static void main(String[] args) {
        new Server();
    }
}
