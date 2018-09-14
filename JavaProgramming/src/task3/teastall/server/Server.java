package task3.teastall.server;

import javafx.util.Pair;
import task3.teastall.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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

    private Server() {

        Items = Constants.getInitialItems();
        ItemDelay = Constants.getInitialItemsDelay();
        itemsProcessorMap = new HashMap<>();
        ordersProcessor = new OrdersProcessor();
        for (String item : Items.keySet()) {
            itemsProcessorMap.put(item, new ItemsProcessor());
        }
        generateGui();
        start();
    }

    private void generateGui() {
        JFrame frame = new JFrame();
        JPanel p1 = new JPanel(new BorderLayout());
        JPanel p2 = new JPanel(new BorderLayout());
        JPanel p3 = new JPanel(new BorderLayout());
        JButton refresh = new JButton("Refresh"); //creating instance of JButton
        DefaultTableModel orderDetailsTable = new DefaultTableModel(new String[]{"S.No.", "Name", "Date", "Item", "Qty", "Rate", "Price"}, 0);
        DefaultTableModel stockDetailsTable = new DefaultTableModel(new String[]{"S.No.", "Item", "Stock Available"}, 0);
        DefaultTableModel purchaseListTable = new DefaultTableModel(new String[]{"S.No.", "Item"}, 0);
        
        JTable orderTable = new JTable(orderDetailsTable);
        JTable stockTable = new JTable(stockDetailsTable);
        JTable purchaseTable = new JTable(purchaseListTable);
        JScrollPane orderDetails = new JScrollPane(orderTable);
        JScrollPane stockDetails = new JScrollPane(stockTable);
        JScrollPane purchaseDetails = new JScrollPane(purchaseTable);

        JTabbedPane tp = new JTabbedPane();
        refresh.addActionListener(actionEvent -> refreshAction(orderDetailsTable, stockDetailsTable, purchaseListTable));
        refreshAction(orderDetailsTable, stockDetailsTable, purchaseListTable);

        frame.setLayout(new BorderLayout());
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        p1.add(orderDetails, BorderLayout.CENTER);
        p2.add(stockDetails, BorderLayout.CENTER);
        p3.add(purchaseDetails, BorderLayout.CENTER);
        tp.add("Orders", p1);
        tp.add("Stock", p2);
        tp.add("Purchase List", p3);
        frame.add(tp, BorderLayout.CENTER);
        frame.add(refresh, BorderLayout.PAGE_END);
        frame.setVisible(true);
    }

    private void refreshAction(DefaultTableModel orderDetailsTable, DefaultTableModel stockDetailsTable, DefaultTableModel purchaseListTable) {
        orderDetailsTable.setRowCount(0);
        AtomicInteger orderCount = new AtomicInteger();
        ordersRecords.forEach((date_name, items) -> {
            orderDetailsTable.addRow(new Object[]{orderCount.incrementAndGet(), date_name.getValue(), date_name.getKey(), "", "", "", ""});
            AtomicInteger totalPrice = new AtomicInteger();
            items.forEach(item -> {
                orderDetailsTable.addRow(new Object[]{"", "", "", item.get(0), item.get(2), item.get(1), item.get(3)});
                totalPrice.set(totalPrice.get() + Integer.parseInt(item.get(3)));
            });
            orderDetailsTable.addRow(new Object[]{"", "", "", "", "", "TOTAL", totalPrice});
            orderDetailsTable.addRow(new Object[]{"", "", "", "", "", "", ""});
        });
        stockDetailsTable.setRowCount(0);
        Items.forEach((item, stock) -> {
            if (!item.equals(Constants.TEA) && !item.equals(Constants.COFFEE)) {
                stockDetailsTable.addRow(new Object[]{stockDetailsTable.getRowCount() + 1, item, stock});
            }
        });

        purchaseListTable.setRowCount(0);
        Items.forEach((item, stock) -> {
            if (!item.equals(Constants.TEA) && !item.equals(Constants.COFFEE) && stock < Constants.getThresholdValues().get(item)) {
                purchaseListTable.addRow(new Object[]{purchaseListTable.getRowCount() + 1, item});
            }
        });
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
                            }
                            line = dataInputStream.readUTF();
                        } catch (IOException i) {
                            System.out.println(i.toString());
                        }
                    }

                    OrderProcessor orderProcessor = new OrderProcessor(this, order, LocalDateTime.now(), customerName, socket);
                    ordersProcessor.orders.add(orderProcessor);
                }
            }
        } catch (IOException i) {
            System.out.println(i.toString());
        }

    }

    Map<String, Integer> getItemDelay() {
        return ItemDelay;
    }

    Map<String, Integer> getItems() {
        return Items;
    }

    Map<String, ItemsProcessor> getItemsProcessorMap() {
        return itemsProcessorMap;
    }

    Map<Pair<String, String>, List<List<String>>> getOrdersRecords() {
        return ordersRecords;
    }

    public static void main(String[] args) {
        new Server();
    }
}
