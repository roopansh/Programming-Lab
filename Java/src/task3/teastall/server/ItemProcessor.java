package task3.teastall.server;

import task3.teastall.Constants;

import java.util.concurrent.CountDownLatch;

/*
 * Thread for processing individual item of an order
 * */
public class ItemProcessor extends Thread {
    private String item;    // Item it is processing
    private int quantity;   // quantity of the item needed to be ordered
    private Server server;  // Main Server class
    private int delay;  // Delay that will be occurred in fulfilling this order
    private CountDownLatch latch;   // Countdown lathc to indicate the order thread waiting on it

    /*
     * Constructor
     * */
    ItemProcessor(Server server, String item, int quantity, CountDownLatch latch) {
        super();
        this.item = item;
        this.server = server;
        this.quantity = quantity;
        this.latch = latch;
    }

    /*
     * Check if the item is available
     * Update the delay accordingly
     * */
    @Override
    public void run() {
        int stock = this.server.getItems().getOrDefault(item, 0);   // Current stock of the item to be ordered
        if (!item.equals(Constants.TEA) && !item.equals(Constants.COFFEE) && quantity > stock) { // if stock is not available
            delay = -1; // set the delay to -1 to indicate that stock is not available
        } else {    // If stock is available
            delay = this.server.getItemDelay().getOrDefault(item, 0);  // get the delay in preparing the item
            delay = delay * quantity + Constants.DELIVERY_TIME; // Delivery time delay
            this.server.getItems().put(item, stock - quantity); // update the quantity of the stock available
        }
        latch.countDown();  // For the order thread waiting on this item
    }

    /*
     * return the delay after the thread has run
     * */
    int getDelay() {
        return delay;
    }
}
