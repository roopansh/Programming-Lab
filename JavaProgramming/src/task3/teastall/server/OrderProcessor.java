package task3.teastall.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class OrderProcessor extends Thread {
    private Map<String, Integer> order;
    private Server server;
    private int delay;
    private boolean available;
    private Socket socket;

    OrderProcessor(Server server, Map<String, Integer> order, Socket socket) {
        this.order = order;
        this.server = server;
        delay = -1;
        available = true;
        this.socket = socket;
    }

    @Override
    public void run() {
        // Create thread for all the items and join on them
        ItemProcessor[] itemProcessors = new ItemProcessor[order.size()];
        int count = 0;
        for (Map.Entry<String, Integer> entry : order.entrySet()) {
            itemProcessors[count] = new ItemProcessor(server, entry.getKey(), entry.getValue());
            itemProcessors[count].start();
            count++;
        }
        for (ItemProcessor itemProcessor : itemProcessors) {
            try {
                itemProcessor.join();
                int d = itemProcessor.getDelay();

                // check if the order can be fulfilled or not
                if (d < 0) available = false;
                if (delay < d) delay = d;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private int getDelay() {
        return delay;
    }

    private boolean isAvailable() {
        return available;
    }

    void sendResult() {
        DataOutputStream dataOutputStream = null;
        try {
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (isAvailable()) {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.writeUTF("The order will be delivered in " + String.valueOf(getDelay()) + " minutes.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                assert dataOutputStream != null;
                dataOutputStream.writeUTF("Order can't be processed. Items unavailable");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
