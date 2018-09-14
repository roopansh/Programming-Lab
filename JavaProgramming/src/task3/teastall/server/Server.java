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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class Server extends Thread {
    private Map<String, Integer> Items; // Item, Stock
    private Map<String, Integer> ItemDelay; // item, total delay

    // All orders Received
    /*
     * <Date, Name>  --->  List of <Item, rate, quantity, price>
     * */
    private Map<Pair<String, String>, List<List<String>>> ordersRecords = new HashMap<>();
    private OrdersProcessor ordersProcessor;
    private Map<String, ItemsProcessor> itemsProcessorMap;
    private int Snacks = 0;
    private int Cookies = 0;
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
        JLabel itemLabel = new JLabel("Select the item to order");
        JLabel stockLabel = new JLabel("Available Stock");
        JLabel purchaseLabel = new JLabel("Items required to purchase");
        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        JPanel p3 = new JPanel();
        JButton refresh = new JButton("Refresh");//creating instance of JButton
        DefaultTableModel orderDetailsTable = new DefaultTableModel(new String[]{"S.No.", "Name", "Date", "Item", "Qty", "Rate", "Price"}, 0);
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

        ordersRecords.forEach((date_name, items) -> {
            orderDetailsTable.addRow(new Object[]{orderDetailsTable.getRowCount() + 1, date_name.getValue(), date_name.getKey(), "", "", "", ""});
            items.forEach(item -> orderDetailsTable.addRow(new Object[]{"", "", "", item.get(0), item.get(2), item.get(1), item.get(3)}));
        });
        stockDetailsTable.addRow(new Object[]{stockDetailsTable.getRowCount() + 1, "Snacks", Items.get("Snacks") - Snacks});
        stockDetailsTable.addRow(new Object[]{stockDetailsTable.getRowCount() + 1, "Cookies", Items.get("Cookies") - Cookies});

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
            AtomicInteger orderCount = new AtomicInteger();
            ordersRecords.forEach((date_name, items) -> {
                orderDetailsTable.addRow(new Object[]{orderCount.incrementAndGet(), date_name.getValue(), date_name.getKey(), "", "", "", ""});
                AtomicInteger totalPrice = new AtomicInteger();
                items.forEach(item -> {
                    orderDetailsTable.addRow(new Object[]{"", "", "", item.get(0), item.get(2), item.get(1), item.get(3)});
                    totalPrice.set(totalPrice.get() + Integer.parseInt(item.get(3)));
                });
                orderDetailsTable.addRow(new Object[]{"", "", "", "", "", "", totalPrice});
                orderDetailsTable.addRow(new Object[]{"", "", "", "", "", "", ""});
            });
            stockDetailsTable.setRowCount(0);
            stockDetailsTable.addRow(new Object[]{stockDetailsTable.getRowCount() + 1, "Snacks", Items.get("Snacks") - Snacks});
            stockDetailsTable.addRow(new Object[]{stockDetailsTable.getRowCount() + 1, "Cookies", Items.get("Cookies") - Cookies});
            purchaselistTable.setRowCount(0);
            if (Constants.getInitialItems().get("Snacks") - Snacks <= 10) {
                purchaselistTable.addRow(new Object[]{purchaselistTable.getRowCount() + 1, "Snacks"});
            }
            if (Constants.getInitialItems().get("Cookies") - Cookies <= 10) {
                purchaselistTable.addRow(new Object[]{purchaselistTable.getRowCount() + 1, "Cookies"});
            }
        });
        f.add(refresh);
        f.setLayout(null);
        f.setVisible(true);

    }

    @Override
    public void run() {
        ordersProcessor.start();
        for (ItemsProcessor itemsProcessor : itemsProcessorMap.values()) {
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
                    String item, customerName = dataInputStream.readUTF();
                    Integer quantity, rate;
                    List<List<String>> order = new ArrayList<>();
                    line = dataInputStream.readUTF();
                    while (!line.equals(Constants.MESSAGE_END)) {
                        try {
                            item = line;
                            line = dataInputStream.readUTF();
                            quantity = Integer.parseInt(line);

                            if (Items.containsKey(item)) {
                                List<String> orderDetails = new ArrayList<>();
                                orderDetails.add(item);
                                rate = Constants.getItemsPrice().get(item);
                                orderDetails.add(rate.toString());
                                orderDetails.add(quantity.toString());
                                orderDetails.add(String.valueOf(quantity * rate));

                                order.add(orderDetails);

                                if (new String("Snacks").equals(item)) {
                                    Snacks = Snacks + quantity;
                                } else if (new String("Cookies").equals(item)) {
                                    Cookies = Cookies + quantity;
                                }
                            }
                            line = dataInputStream.readUTF();
                        } catch (IOException i) {
                            System.out.println(i);
                        }
                    }

                    ordersRecords.put(new Pair(LocalDateTime.now().toString(), customerName), order);

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
