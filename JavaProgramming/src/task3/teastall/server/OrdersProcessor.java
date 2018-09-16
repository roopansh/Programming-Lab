package task3.teastall.server;

import task3.teastall.Constants;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
 * Thread for processing all the received order.
 * Start the threads for processing individual orders
 * */
public class OrdersProcessor extends Thread {
    List<OrderProcessor> orders; // list containing the threads of individual orders to be processed
    private List<OrderProcessor> processingOrders; // list containing the threads of individual orders added to the processing queue
    private LocalDateTime timeStamp;

    /*
     * Constructor
     * */
    OrdersProcessor() {
        super();
        orders = new ArrayList<>();
        processingOrders = new ArrayList<>();
        timeStamp = LocalDateTime.now();
    }

    /*
     * Starting the threads of the individual orders
     * joining the order threads to ensure First Come First Serve
     * */
    @Override
    public void run() {
        while (true) {
            if (orders.size() > 0) {
                int count = 0;
                while (orders.size() > 0 && count < Constants.ORDERS_THRESHOLD) {
                    OrderProcessor order = orders.get(0); // get the earliest received order
                    orders.remove(0);
                    processingOrders.add(order); // putting the order into processing queue
                    order.start(); // starting the order's thread
                    count++;
                }
                while (processingOrders.size() > 0) {
                    OrderProcessor order = processingOrders.get(0); // getting the earliest received order from the processing queue
                    try {
                        order.join(); // joining the threads of the processing orders in the order they received to ensure First Come First Serve
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // send the received time of the order and get the delivery time of that order
                    timeStamp = order.sendResult(timeStamp);
                    processingOrders.remove(0);
                }
            } else {
                try {
                    sleep(100); // Wait till no orders received
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
