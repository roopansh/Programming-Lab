package task3.teastall.server;

import task3.teastall.Constants;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdersProcessor extends Thread {
    List<OrderProcessor> orders;
    List<OrderProcessor> processingOrders;
    LocalDateTime timeStamp;

    OrdersProcessor() {
        super();
        orders = new ArrayList<>();
        processingOrders = new ArrayList<>();
        timeStamp = LocalDateTime.now();
    }

    @Override
    public void run() {
        while (true) {
            if (orders.size() > 0) {
                int count = 0;
                while (orders.size() > 0 && count < Constants.ORDERS_THRESHOLD) {
                    OrderProcessor order = orders.get(0);
                    orders.remove(0);
                    processingOrders.add(order);
                    order.start();
                    count++;
                }
                while (processingOrders.size() > 0) {
                    OrderProcessor order = processingOrders.get(0);
                    try {
                        order.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    timeStamp = order.sendResult(timeStamp);
                    processingOrders.remove(0);
                }
            } else {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
