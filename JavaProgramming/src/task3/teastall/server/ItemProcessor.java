package task3.teastall.server;

import task3.teastall.Constants;

import java.util.concurrent.CountDownLatch;

public class ItemProcessor extends Thread {
    private String item;
    private int quantity;
    private Server server;
    private int delay;
    private CountDownLatch latch;

    ItemProcessor(Server server, String item, int quantity, CountDownLatch latch) {
        super();
        this.item = item;
        this.server = server;
        this.quantity = quantity;
        this.latch = latch;
    }

    @Override
    public void run() {
        int stock = this.server.getItems().getOrDefault(item, 0);
        if (quantity > stock) {
            delay = -1;
        } else {
            delay = this.server.getItemDelay().getOrDefault(item, -1);
            delay = delay * quantity + Constants.DELIVERY_TIME;
            if (!item.equals(Constants.TEA) && !item.equals(Constants.COFFEE)) {
                this.server.getItems().put(item, stock - quantity);
            }
        }
        latch.countDown();
    }

    int getDelay() {
        return delay;
    }
}
