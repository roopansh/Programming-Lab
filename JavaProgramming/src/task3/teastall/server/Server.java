package task3.teastall.server;

import javafx.util.Pair;
import task3.teastall.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.*;



public class Server extends Thread {
    private Map<String, Integer> Items; // Item, Stock
    private Map<String, Integer> ItemDelay; // item, total delay
    private Map<String, Integer> ordersRecords = new HashMap<>();
    private OrdersProcessor ordersProcessor;
    private Map<String, ItemsProcessor> itemsProcessorMap;
    int Snacks = 0;
    int Cookies = 0;
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

    public void generateGui() {

        JFrame f = new JFrame();
        JLabel itemLabel = new JLabel("Select the item to order");
        JLabel stockLabel = new JLabel("Available Stock");
        JLabel purchaseLabel = new JLabel("Items required to purchase");
        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        JPanel p3 = new JPanel();
        JButton refresh = new JButton("Refresh");//creating instance of JButton
        DefaultTableModel orderDetailsTable = new DefaultTableModel(new String[]{"S.No.", "Item", "Qty"}, 0);
        DefaultTableModel stockDetailsTable = new DefaultTableModel(new String[]{"S.No.", "Item", "Qty"}, 0);
        DefaultTableModel purchaselistTable = new DefaultTableModel(new String[]{"S.No.", "Item"}, 0);

        JTable orderTable = new JTable(orderDetailsTable);
        JTable stockTable = new JTable(stockDetailsTable);
        JTable purchaseTable = new JTable(purchaselistTable);
        JScrollPane orderDetails = new JScrollPane(orderTable);
        JScrollPane stockDetails = new JScrollPane(stockTable);
        JScrollPane purchaseDetails = new JScrollPane(purchaseTable);
        orderDetailsTable.setRowCount(0);
        stockDetailsTable.setRowCount(0);

        ordersRecords.forEach((key, value) -> orderDetailsTable.addRow(new Object[]{orderDetailsTable.getRowCount() + 1, key, value}));
        stockDetailsTable.addRow(new Object[]{stockDetailsTable.getRowCount() + 1, "Snacks",Items.get("Snacks") - Snacks});
        stockDetailsTable.addRow(new Object[]{stockDetailsTable.getRowCount() + 1, "Cookies",Items.get("Cookies") - Cookies});

        p1.add(itemLabel);
        p1.add(orderDetails);
        p2.add(stockLabel);
        p2.add(stockDetails);
        p3.add(purchaseLabel);
        p3.add(purchaseDetails);

        JTabbedPane tp = new JTabbedPane();

        tp.setBounds(50, 50, 500, 600);
        tp.add("Orders", p1);
        tp.add("Stock", p2);
        tp.add("Purchase List", p3);
        f.add(tp);
        f.setSize(600, 800);
        orderDetails.setBounds(50, 400, 500, 200);
        purchaseDetails.setBounds(50, 400, 500, 200);
        stockDetails.setBounds(50, 400, 500, 200);
        refresh.setBounds(250, 650, 100, 40);

        refresh.addActionListener(actionEvent -> {
            orderDetailsTable.setRowCount(0);
            ordersRecords.forEach((key, value) -> orderDetailsTable.addRow(new Object[]{orderDetailsTable.getRowCount() + 1, key, value}));
            stockDetailsTable.setRowCount(0);
            stockDetailsTable.addRow(new Object[]{stockDetailsTable.getRowCount() + 1, "Snacks",Items.get("Snacks") - Snacks});
            stockDetailsTable.addRow(new Object[]{stockDetailsTable.getRowCount() + 1, "Cookies",Items.get("Cookies") - Cookies});
            purchaselistTable.setRowCount(0);
            if (Constants.getInitialItems().get("Snacks")-Snacks <= 10){
                purchaselistTable.addRow(new Object[]{purchaselistTable.getRowCount()+1,"Snacks"});
            }
            if (Constants.getInitialItems().get("Cookies")-Cookies <= 10){
                purchaselistTable.addRow(new Object[]{purchaselistTable.getRowCount()+1,"Cookies"});
            }
        });
        f.add(refresh);
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
                                if (!ordersRecords.containsKey(item)) {
                                    ordersRecords.put(item, quantity);
                                }else {
                                    ordersRecords.put(item,quantity + ordersRecords.get(item));
                                }
                                if (new String("Snacks").equals(item)){
                                    Snacks = Snacks + quantity;
                                }
                                else if(new String("Cookies").equals(item)){
                                    Cookies = Cookies + quantity;
                                }
                                //System.out.println("Snacks ordered = ");

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
