package task3.teastall.server;

import java.util.ArrayList;
import java.util.List;

public class OrdersProcessor implements Runnable {
    List<OrderProcessor> Orders;

    OrdersProcessor() {
        super();
        Orders = new ArrayList<>();
    }

    @Override
    public void run() {
        while (Orders.size() > 0) {
            System.out.println("ABHI");
            OrderProcessor order = Orders.get(0);
            order.start();
            try {
                order.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            order.sendResult();
            Orders.remove(0);
        }
    }
}
