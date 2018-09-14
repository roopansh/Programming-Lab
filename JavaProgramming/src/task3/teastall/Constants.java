package task3.teastall;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final String MESSAGE_END = "END";
    public static final String SERVER_ADDRESS = "127.0.0.1";
    public static final int SERVER_PORT = 5000;
    public static final String GET_AVAILABLE_LIST = "GET_AVAILABLE_LIST";
    public static final String PLACE_ORDER = "PLACE_ORDER";
    public static final int ORDERS_THRESHOLD = 10;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy HH:mm:ss a");
    public static final int DELIVERY_TIME = 2;
    public static final String COFFEE = "Coffee";
    public static final String TEA = "Tea";
    private static final String SNACKS = "Snacks";
    private static final String COOKIES = "Cookies";

    public static Map<String, Integer> getInitialItems() {
        Map<String, Integer> items = new HashMap<>();
        items.put(COFFEE, 0);
        items.put(TEA, 0);
        items.put(SNACKS, 100);
        items.put(COOKIES, 100);
        return items;
    }

    public static Map<String, Integer> getItemsPrice() {
        Map<String, Integer> items = new HashMap<>();
        items.put(COFFEE, 6);
        items.put(TEA, 5);
        items.put(SNACKS, 15);
        items.put(COOKIES, 10);
        return items;
    }

    public static Map<String, Integer> getInitialItemsDelay() {
        Map<String, Integer> items = new HashMap<>();
        items.put(COFFEE, 1);
        items.put(TEA, 1);
        items.put(SNACKS, 0);
        items.put(COOKIES, 0);
        return items;
    }

    public static Map<String, Integer> getThresholdValues() {
        Map<String, Integer> items = new HashMap<>();
        items.put(SNACKS, 10);
        items.put(COOKIES, 10);
        return items;
    }
}
