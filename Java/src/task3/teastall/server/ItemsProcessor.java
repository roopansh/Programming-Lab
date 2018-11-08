package task3.teastall.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/* Thread containing the queue of a particular item to be processed
 * Items added on first come first serve basis
 * Picks the first thread, processes it and then removes it
 * */
class ItemsProcessor extends Thread {
    List<ItemProcessor> itemOrders; // list containing the threads for the individual items in an order
    ReentrantLock queueLock;

    ItemsProcessor() {
        this.itemOrders = new ArrayList<>();
        queueLock = new ReentrantLock();
    }

    @Override
    public void run() {
        while (true) {
            if (itemOrders.size() > 0) {
                ItemProcessor itemProcessor = itemOrders.get(0); // getting the individual items of an order
                itemProcessor.start(); // starting the item's thread
                itemOrders.remove(0);
                try {
                    itemProcessor.join(); // joining the threads of each item of an order to ensure that order processing completes when each item of that order is processed
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
