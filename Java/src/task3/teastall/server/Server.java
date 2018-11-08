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
import java.time.ZoneId;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * server side of the Tea Stall
 * */
public class Server extends Thread {
    private Map<String, Integer> Items; // Item, Stock
    private Map<String, Integer> ItemDelay; // item, total delay

    // All orders Received
    /*
     * <Date, Name>  --->  List of <Item, rate, quantity, price>
     * */
    private Map<Pair<String, String>, List<List<String>>> ordersRecords = new HashMap<>(); // hashmap contains pair of date,name mapped to list of items included in his/her order
    private OrdersProcessor ordersProcessor;
    private Map<String, ItemsProcessor> itemsProcessorMap;

    private Server() {

        Items = Constants.getInitialItems(); // initial stock of each item
        ItemDelay = Constants.getInitialItemsDelay(); // processing time required for a particular item
        itemsProcessorMap = new HashMap<>();
        ordersProcessor = new OrdersProcessor(); // thread for processing the received orders
        for (String item : Items.keySet()) {
            itemsProcessorMap.put(item, new ItemsProcessor());
        }
        generateGui();
        start();
    }

    /*
     * Generate the GUI of the server side
     * */
    private void generateGui() {
        JFrame frame = new JFrame(); //creating instance of JFrame
        JPanel p1 = new JPanel(new BorderLayout());
        JPanel p2 = new JPanel(new BorderLayout());
        JPanel p3 = new JPanel(new BorderLayout());
        JLabel fromDateLabel = new JLabel(" From ");
        JLabel toDateLabel = new JLabel(" To ");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date from = cal.getTime();
        cal.add(Calendar.DATE, 2);
        Date to = cal.getTime();
        cal.add(Calendar.YEAR, -10);
        Date startDate = cal.getTime();
        cal.add(Calendar.YEAR, 20);
        Date endDate = cal.getTime();
        SpinnerModel fromModel = new SpinnerDateModel(from, startDate, endDate, Calendar.YEAR);
        SpinnerModel toModel = new SpinnerDateModel(to, startDate, endDate, Calendar.YEAR);
        JSpinner fromDateInput = new JSpinner(fromModel);
        JSpinner toDateInput = new JSpinner(toModel);
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        datePanel.add(fromDateLabel);
        datePanel.add(fromDateInput);
        datePanel.add(toDateLabel);
        datePanel.add(toDateInput);

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
        refresh.addActionListener(actionEvent -> refreshAction(orderDetailsTable, stockDetailsTable, purchaseListTable, fromDateInput, toDateInput));
        refreshAction(orderDetailsTable, stockDetailsTable, purchaseListTable, fromDateInput, toDateInput);

        frame.setLayout(new BorderLayout());
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        p1.add(orderDetails, BorderLayout.CENTER);
        p2.add(stockDetails, BorderLayout.CENTER);
        p3.add(purchaseDetails, BorderLayout.CENTER);
        tp.add("Orders", p1);
        tp.add("Stock", p2);
        tp.add("Purchase List", p3);
        frame.add(datePanel, BorderLayout.PAGE_START);
        frame.add(tp, BorderLayout.CENTER);
        frame.add(refresh, BorderLayout.PAGE_END);
        frame.setVisible(true); //making the frame visible
    }


    private void refreshAction(DefaultTableModel orderDetailsTable, DefaultTableModel stockDetailsTable, DefaultTableModel purchaseListTable, JSpinner fromDateInput, JSpinner toDateInput) {
        //clear the items added to order on clicking the refresh button
        orderDetailsTable.setRowCount(0);
        AtomicInteger orderCount = new AtomicInteger();
        final Date fromDate = (Date) fromDateInput.getValue();
        final Date toDate = (Date) toDateInput.getValue();

        // displaying all the orders received (fromdate to todate)
        ordersRecords.forEach((date_name, items) -> {
            LocalDateTime orderDateTime = LocalDateTime.parse(date_name.getKey(), Constants.DATE_TIME_FORMATTER);
            if (fromDate.toInstant().isBefore(orderDateTime.atZone(ZoneId.systemDefault()).toInstant()) && toDate.toInstant().isAfter(orderDateTime.atZone(ZoneId.systemDefault()).toInstant())) {
                orderDetailsTable.addRow(new Object[]{orderCount.incrementAndGet(), date_name.getValue(), date_name.getKey(), "", "", "", ""});
                AtomicInteger totalPrice = new AtomicInteger();
                items.forEach(item -> {
                    orderDetailsTable.addRow(new Object[]{"", "", "", item.get(0), item.get(2), item.get(1), item.get(3)});
                    totalPrice.set(totalPrice.get() + Integer.parseInt(item.get(3)));
                });
                orderDetailsTable.addRow(new Object[]{"", "", "", "", "", "TOTAL", totalPrice});
                orderDetailsTable.addRow(new Object[]{"", "", "", "", "", "", ""});
            }
        });
        //update the stock table showing the availability of the items on clicking the refresh button
        stockDetailsTable.setRowCount(0);
        Items.forEach((item, stock) -> {
            if (!item.equals(Constants.TEA) && !item.equals(Constants.COFFEE)) {
                stockDetailsTable.addRow(new Object[]{stockDetailsTable.getRowCount() + 1, item, stock});
            }
        });

        // update the list containing items required to purchase on clicking the refresh button
        purchaseListTable.setRowCount(0);
        Items.forEach((item, stock) -> {
            if (!item.equals(Constants.TEA) && !item.equals(Constants.COFFEE) && stock < Constants.getThresholdValues().get(item)) {
                purchaseListTable.addRow(new Object[]{purchaseListTable.getRowCount() + 1, item});
            }
        });
    }

    @Override
    public void run() {
        // start the thread to process the received orders
        ordersProcessor.start();

        // start the thread of each item's processing queue
        for (ItemsProcessor itemsProcessor : itemsProcessorMap.values()) {
            itemsProcessor.start();
        }

        // starts server and waits for a connection
        try {
            ServerSocket server = new ServerSocket(Constants.SERVER_PORT);
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
                    for (String item : Items.keySet()) {
                        dataOutputStream.writeUTF(item);
                    }
                    dataOutputStream.writeUTF(Constants.MESSAGE_END);
                    dataOutputStream.close();

                } else if (line.equals(Constants.PLACE_ORDER)) {
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
