package task3.teastall;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final String MESSAGE_END = "END";
    public static final String SERVER_ADDRESS = "127.0.0.1";
    public static final int SERVER_PORT = 5000;
    public static final String GET_AVAILABLE_LIST = "GET_AVAILABLE_LIST";
    public static final String PLACE_ORDER = "PLACE_ORDER";

    public static Map<String, Integer> getInitialItems() {
        Map<String, Integer> items = new HashMap<>();
        items.put("Coffee", 100);
        items.put("Tea", 100);
        items.put("Snacks", 100);
        items.put("Chips", 100);
        return items;
    }

    public static Map<String, Integer> getInitialItemsDelay() {
        Map<String, Integer> items = new HashMap<>();
        items.put("Coffee", 1);
        items.put("Tea", 1);
        items.put("Snacks", 0);
        items.put("Chips", 0);
        return items;
    }
}
