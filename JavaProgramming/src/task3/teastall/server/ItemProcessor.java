package task3.teastall.server;

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
        if (quantity > this.server.getItems().getOrDefault(item, 0)) {
            delay = -1;
        } else {
            delay = this.server.getItemDelay().getOrDefault(item, -1);
            delay = delay * quantity + 2;
        }
        latch.countDown();
    }

    int getDelay() {
        return delay;
    }
}
