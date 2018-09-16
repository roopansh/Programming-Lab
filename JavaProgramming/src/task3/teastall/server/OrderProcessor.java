package task3.teastall.server;

import javafx.util.Pair;
import task3.teastall.Constants;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/*
 * Thread for processing individual order.
 * Start the threads for processing individual item in the list
 * Check if all items are available and the order can be processed
 * */
public class OrderProcessor extends Thread {
    private List<List<String>> order;   // List of <Item, rate, quantity, price>
    private Server server;
    private int delay;  // Delay for processing the order
    private boolean available; // flag denoting the availability of an item
    private Socket socket;
    private LocalDateTime orderTime;
    private String customerName;

    /*
     * Constructor
     * */
    OrderProcessor(Server server, List<List<String>> order, LocalDateTime orderTime, String customerName, Socket socket) {
        this.order = order;
        this.server = server;
        delay = -1;
        available = true;
        this.socket = socket;
        this.orderTime = orderTime;
        this.customerName = customerName;
    }

    @Override
    public void run() {
        // Create thread for all the items and wait on them
        ItemProcessor[] itemProcessors = new ItemProcessor[order.size()];
        int count = 0;
        // counter of the countdownlatch is initialized to the number of items in the order and each item countdowns when it is processed
        CountDownLatch latch = new CountDownLatch(order.size());

        for (List<String> itemDetails : order) {
            String item = itemDetails.get(0);
            int quantity = Integer.parseInt(itemDetails.get(2));
            itemProcessors[count] = new ItemProcessor(server, item, quantity, latch);

            // acquire lock the on item list and then add to the queue
            ItemsProcessor itemsProcessor = server.getItemsProcessorMap().get(item);
            itemsProcessor.queueLock.lock();
            itemsProcessor.itemOrders.add(itemProcessors[count]);
            itemsProcessor.queueLock.unlock();

            count++;
        }

        try {
            latch.await();
            // waiting till all the items of the order are processed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // calculating delay after each item of the order is processed
        for (ItemProcessor itemProcessor : itemProcessors) {
            int d = itemProcessor.getDelay(); // delay time for an item of the order
            // check if the order can be fulfilled or not
            if (d < 0) available = false; // item cannot be processed due to stock unavailability
            if (delay < d) delay = d; // updating the delay time if an item requires much delay then the other items
        }
    }

    private int getDelay() {
        return delay;
    }

    private boolean isAvailable() {
        return available;
    }


    LocalDateTime sendResult(LocalDateTime timeStamp) {
        if (orderTime.isAfter(timeStamp)) {
            timeStamp = orderTime;
        }
        DataOutputStream dataOutputStream = null;
        try {
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (isAvailable()) {
            timeStamp = timeStamp.plusMinutes(getDelay());
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.writeUTF("The order will be delivered by " + Constants.DATE_TIME_FORMATTER.format(timeStamp));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            server.getOrdersRecords().put(new Pair(orderTime.format(Constants.DATE_TIME_FORMATTER), customerName), order);

        } else {
            try {
                assert dataOutputStream != null;
                dataOutputStream.writeUTF("Order can't be processed. Items unavailable");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return timeStamp;
    }
}
