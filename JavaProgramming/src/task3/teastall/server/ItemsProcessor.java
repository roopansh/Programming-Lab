package task3.teastall.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/* Thread containing the queue of a particular item to be processed
 * Items added on first come first serve basis
 * Picks the first thread, processes it and then removes it
 * */
class ItemsProcessor extends Thread {
    List<ItemProcessor> itemOrders;
    ReentrantLock queueLock;

    ItemsProcessor() {
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
