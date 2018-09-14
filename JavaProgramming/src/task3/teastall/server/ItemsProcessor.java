package task3.teastall.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

class ItemsProcessor extends Thread {
    String item;
    List<ItemProcessor> itemOrders;
    ReentrantLock queueLock;

    ItemsProcessor(String item) {
        this.item = item;
        this.itemOrders = new ArrayList<>();
        queueLock = new ReentrantLock();
    }

    @Override
    public void run() {
        while (true) {
            if (itemOrders.size() > 0) {
                ItemProcessor itemProcessor = itemOrders.get(0);
                itemProcessor.start();
                itemOrders.remove(0);
                try {
                    itemProcessor.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
