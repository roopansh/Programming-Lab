package task3.teastall.client;

import task3.teastall.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.lang.System.out;

public class Client {
    private Map<String, Integer> OrderItemList;
    private List<String> Items;
    private Socket socket = null;
    private DataInputStream dataInputStream = null;
    private DataOutputStream dataOutputStream = null;
    private Integer totalPrice = 0;

    private Client() {
        OrderItemList = new HashMap<>();
        Items = new ArrayList<>();
        establishConnection();
        generateGui();
    }

    private void establishConnection() {
        // establish a connection
        try {
            socket = new Socket(Constants.SERVER_ADDRESS, Constants.SERVER_PORT);
            out.println("Connected to server");

            // takes input from terminal
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException i) {
            System.out.println(i.toString());
        }

        // string to read message from input
        String line = "";

        try {
            dataOutputStream.writeUTF(Constants.GET_AVAILABLE_LIST);
        } catch (IOException e) {
            System.out.println(e.toString());
        }

        // keep reading until "End" is input
        while (!line.equals(Constants.MESSAGE_END)) {
            try {
                this.Items.add(line);
                line = dataInputStream.readUTF();
            } catch (IOException i) {
                System.out.println(i.toString());
            }
        }
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    private void generateGui() {
        JFrame frame = new JFrame(); //creating instance of JFrame

        JLabel customerNameLabel = new JLabel("Enter Your Name");
        JTextField customerName = new JTextField();
        JLabel itemLabel = new JLabel("Select the item to order");
        JLabel quantityLabel = new JLabel("Select the quantity of item to order.");
        JButton orderButton = new JButton("Place Order");
        JButton addButton = new JButton("Add Selected Item");
        JButton clearButton = new JButton("Clear");
        JButton invoiceButton = new JButton("View Invoice");
        SpinnerModel spinnerNumberModel = new SpinnerNumberModel(1, 1, 100, 1);
        JSpinner quantitySpinner = new JSpinner(spinnerNumberModel);
        JList itemList = new JList(Items.toArray());
        DefaultTableModel orderDetailsTable = new DefaultTableModel(new String[]{"S.No.", "Item", "Qty"}, 0);
        JTable orderTable = new JTable(orderDetailsTable);
        JScrollPane orderDetails = new JScrollPane(orderTable);

        // x axis, y axis, width, height
        customerNameLabel.setBounds(50, 10, 250, 30);
        customerName.setBounds(350, 10, 100, 30);
        itemLabel.setBounds(50, 50, 250, 30);
        itemList.setBounds(350, 50, 100, 150);
        quantityLabel.setBounds(50, 250, 250, 30);
        quantitySpinner.setBounds(350, 250, 50, 30);
        addButton.setBounds(100, 400, 200, 40);
        clearButton.setBounds(300, 400, 200, 40);
        orderDetails.setBounds(0, 500, 600, 200);
        orderButton.setBounds(100, 700, 200, 40);
        invoiceButton.setBounds(300, 700, 200, 40);

        addButton.addActionListener(actionEvent -> {
            String item = Items.get(itemList.getSelectedIndex());
            Integer quantity = OrderItemList.getOrDefault(item, 0) + (Integer) quantitySpinner.getValue();
            OrderItemList.put(item, quantity);
            orderDetailsTable.setRowCount(0);
            OrderItemList.forEach((key, value) -> orderDetailsTable.addRow(new Object[]{orderDetailsTable.getRowCount() + 1, key, value}));
            if (OrderItemList.size() > 0) {
                orderButton.setEnabled(true);
                clearButton.setEnabled(true);
                invoiceButton.setEnabled(false);
            } else {
                clearButton.setEnabled(false);
                orderButton.setEnabled(false);
                invoiceButton.setEnabled(false);
            }
        });

        clearButton.addActionListener(actionEvent -> {
            OrderItemList.clear();
            orderDetailsTable.setRowCount(0);
            orderButton.setEnabled(false);
            invoiceButton.setEnabled(false);
            clearButton.setEnabled(false);
        });

        orderButton.addActionListener(actionEvent -> {
            spinnerNumberModel.setValue(1);
            String name = customerName.getText();
            String message = sendOrder(name);
            JOptionPane.showMessageDialog(frame, message);
            invoiceButton.setEnabled(true);
        });

        invoiceButton.addActionListener(actionEvent -> generateInvoice());

        clearButton.setEnabled(false);
        orderButton.setEnabled(false);
        invoiceButton.setEnabled(false);

        frame.add(customerNameLabel);
        frame.add(customerName);
        frame.add(itemLabel);
        frame.add(itemList);
        frame.add(quantityLabel);
        frame.add(quantitySpinner);
        frame.add(addButton);
        frame.add(clearButton);
        frame.add(orderDetails);
        frame.add(orderButton);
        frame.add(invoiceButton);

        frame.setSize(600, 800);//600 width and 800 height
        frame.setLayout(null);//using no layout managers
        frame.setVisible(true);//making the frame visible
    }

    private void generateInvoice() {
        JFrame frame = new JFrame();
        frame.setTitle("Customer Reciept");
        frame.setLayout(new BorderLayout());

        DefaultTableModel orderDetailsTable = new DefaultTableModel(new String[]{"S.No.", "Item", "Qty", "Rate", "Price"}, 0);
        JTable orderTable = new JTable(orderDetailsTable);
        JScrollPane orderDetails = new JScrollPane(orderTable);
        OrderItemList.forEach((key, value) -> orderDetailsTable.addRow(new Object[]{orderDetailsTable.getRowCount() + 1, key, value, Constants.getItemsPrice().get(key), Constants.getItemsPrice().get(key) * value}));

        for (Map.Entry<String, Integer> pair : OrderItemList.entrySet()) {
            totalPrice = totalPrice + (Constants.getItemsPrice().get(pair.getKey()) * Integer.parseInt(pair.getValue().toString()));
        }
        orderDetailsTable.addRow(new Object[]{"", "", "Total Price", "", totalPrice});

        frame.setSize(600, 800);
        frame.add(orderDetails, BorderLayout.CENTER);
        frame.setVisible(true);//making the frame visible
    }

    private String sendOrder(String customerName) {
        // establish a connection
        try {
            socket = new Socket(Constants.SERVER_ADDRESS, Constants.SERVER_PORT);
            out.println("Connected to server");

            dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException i) {
            System.out.println(i.toString());
        }

        try {
            dataOutputStream.writeUTF(Constants.PLACE_ORDER);
            dataOutputStream.writeUTF(customerName);
        } catch (IOException e) {
            System.out.println(e.toString());
        }

        // keep writing until "End"
        for (Map.Entry<String, Integer> entry : OrderItemList.entrySet()) {
            try {
                dataOutputStream.writeUTF(entry.getKey());
                dataOutputStream.writeUTF(Integer.toString(entry.getValue()));
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }
        try {
            dataOutputStream.writeUTF(Constants.MESSAGE_END);
        } catch (IOException e) {
            System.out.println(e.toString());
        }

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Read the response
        String line = "";
        try {
            line = dataInputStream.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(line);
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        return line;
    }

    public static void main(String[] args) {
        new Client();
    }
}
