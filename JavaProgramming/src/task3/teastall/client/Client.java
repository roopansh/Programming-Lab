package task3.teastall.client;

import task3.teastall.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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

    private Client() {
        OrderItemList = new HashMap<>();
        Items = new ArrayList<>();
        establishConnection(Constants.SERVER_ADDRESS, Constants.SERVER_PORT);
        generateGui();
    }

    private void establishConnection(String address, int port) {
        // establish a connection
        try {
            socket = new Socket(address, port);
            out.println("Connected to server");

            // takes input from terminal
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException i) {
            System.out.println(i);
        }

        // string to read message from input
        String line = "";

        try {
            dataOutputStream.writeUTF(Constants.GET_AVAILABLE_LIST);
        } catch (IOException e) {
            System.out.println(e);
        }

        // keep reading until "End" is input
        while (!line.equals(Constants.MESSAGE_END)) {
            try {
                this.Items.add(line);
                line = dataInputStream.readUTF();
            } catch (IOException i) {
                System.out.println(i);
            }
        }
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void generateGui() {
        JFrame frame = new JFrame(); //creating instance of JFrame

        JLabel itemLabel = new JLabel("Select the item to order");
        JLabel quantityLabel = new JLabel("Select the quantity of item to order.");
        JButton orderButton = new JButton("Order");//creating instance of JButton
        JButton addButton = new JButton("Add");//creating instance of JButton
        SpinnerModel spinnerNumberModel = new SpinnerNumberModel(1, //initial value
                1, //minimum value
                10, //maximum value
                1); //step
        JSpinner quantitySpinner = new JSpinner(spinnerNumberModel);
        JList itemList = new JList(Items.toArray());
        DefaultTableModel orderDetailsTable = new DefaultTableModel(new String[]{"S.No.", "Item", "Qty"}, 0);
        JTable orderTable = new JTable(orderDetailsTable);
        JScrollPane orderDetails = new JScrollPane(orderTable);


        // x axis, y axis, width, height
        itemLabel.setBounds(50, 50, 250, 30);
        itemList.setBounds(350, 50, 100, 150);
        quantityLabel.setBounds(50, 250, 250, 30);
        quantitySpinner.setBounds(350, 250, 50, 30);
        addButton.setBounds(250, 400, 100, 40);
        orderDetails.setBounds(50, 500, 500, 200);
        orderButton.setBounds(250, 750, 100, 40);

        addButton.addActionListener(actionEvent -> {
            String item = Items.get(itemList.getSelectedIndex());
            Integer quantity = OrderItemList.getOrDefault(item, 0) + (Integer) quantitySpinner.getValue();
            OrderItemList.put(item, quantity);
            orderDetailsTable.setRowCount(0);
            OrderItemList.forEach((key, value) -> orderDetailsTable.addRow(new Object[]{orderDetailsTable.getRowCount() + 1, key, value}));
            if (OrderItemList.size() > 0) {
                orderButton.setEnabled(true);
            } else {
                orderButton.setEnabled(false);
            }
        });

        orderButton.addActionListener(actionEvent -> {
            sendOrder();
        });

        orderButton.setEnabled(false);

        frame.add(itemLabel);
        frame.add(itemList);
        frame.add(quantityLabel);
        frame.add(quantitySpinner);
        frame.add(addButton);//adding addButton in JFrame
        frame.add(orderDetails);
        frame.add(orderButton);//adding orderButton in JFrame


        frame.setSize(600, 800);//600 width and 800 height

        frame.setLayout(null);//using no layout managers

        frame.setVisible(true);//making the frame visible
    }

    private void sendOrder() {
        // establish a connection
        try {
            socket = new Socket(Constants.SERVER_ADDRESS, Constants.SERVER_PORT);
            out.println("Connected to server");

            dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException i) {
            System.out.println(i);
        }

        try {
            dataOutputStream.writeUTF(Constants.PLACE_ORDER);
        } catch (IOException e) {
            System.out.println(e);
        }

        // keep writing until "End"
        for (Map.Entry<String, Integer> entry : OrderItemList.entrySet()) {
            try {
                dataOutputStream.writeUTF(entry.getKey());
                dataOutputStream.writeUTF(Integer.toString(entry.getValue()));
            } catch (IOException e) {
                System.out.println(e);
            }
        }
        try {
            dataOutputStream.writeUTF(Constants.MESSAGE_END);
        } catch (IOException e) {
            System.out.println(e);
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
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}
