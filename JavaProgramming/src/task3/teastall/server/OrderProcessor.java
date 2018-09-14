package task3.teastall.server;

import task3.teastall.Constants;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class OrderProcessor extends Thread {
    private List<List<String>> order;   // List of <Item, rate, quantity, price>
    private Server server;
    private int delay;
    private boolean available;
    private Socket socket;
    private LocalDateTime orderTime;

    OrderProcessor(Server server, List<List<String>> order, LocalDateTime orderTime, Socket socket) {
        this.order = order;
        this.server = server;
        delay = -1;
        available = true;
        this.socket = socket;
        this.orderTime = orderTime;
    }

    @Override
    public void run() {
        // Create thread for all the items and wait on them
        ItemProcessor[] itemProcessors = new ItemProcessor[order.size()];
        int count = 0;
        CountDownLatch latch = new CountDownLatch(order.size());

        for (List<String> itemDetails : order) {
            String item = itemDetails.get(0);
            int quantity = Integer.parseInt(itemDetails.get(2));
            itemProcessors[count] = new ItemProcessor(server, item, quantity, latch);
            // aquire lock the item list and then add to the queueu

            ItemsProcessor itemsProcessor = server.getItemsProcessorMap().get(item);
            itemsProcessor.queueLock.lock();
            itemsProcessor.itemOrders.add(itemProcessors[count]);
            itemsProcessor.queueLock.unlock();

            count++;
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (ItemProcessor itemProcessor : itemProcessors) {
            int d = itemProcessor.getDelay();
            // check if the order can be fulfilled or not
            if (d < 0) available = false;
            if (delay < d) delay = d;
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
                    dataOutputStream.writeUTF("The order will be delivered by " + timeStamp.format(Constants.DATE_TIME_FORMATTER));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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
