package task3.teastall.server;

public class ItemProcessor extends Thread {
    private String item;
    private int quantity;
    private Server server;
    private int delay;

    ItemProcessor(Server server, String item, int quantity) {
        super();
        this.item = item;
        this.server = server;
        this.quantity = quantity;
    }

    @Override
    public void run() {
        if (quantity > this.server.getItems().getOrDefault(item, 0)) {
            delay = -1;
        } else {
            delay = this.server.getItemDelay().getOrDefault(item, -1);
            delay = delay * quantity + 2;
        }
    }

    int getDelay() {
        return delay;
    }
}
