package task3.teastall.server;

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
                while (orders.size() > 0) {
                    OrderProcessor order = orders.get(0);
                    orders.remove(0);
                    processingOrders.add(order);
                    order.start();

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
