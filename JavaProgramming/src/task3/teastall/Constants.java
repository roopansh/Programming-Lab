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
    public static final int ORDERS_THRESHHOLD = 10;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm 'on' cccc dd,LLLL ");

    public static Map<String, Integer> getInitialItems() {
        Map<String, Integer> items = new HashMap<>();
        items.put("Coffee", 100);
        items.put("Tea", 100);
        items.put("Snacks", 100);
        items.put("Cookies", 100);
        return items;
    }

    public static Map<String,Integer> getItemsPrice(){
        Map<String,Integer> items = new HashMap<>();
        items.put("Coffee", 6);
        items.put("Tea", 5);
        items.put("Snacks", 15);
        items.put("Cookies", 10);
        return items;
    }
    public static Map<String, Integer> getInitialItemsDelay() {
        Map<String, Integer> items = new HashMap<>();
        items.put("Coffee", 1);
        items.put("Tea", 1);
        items.put("Snacks", 0);
        items.put("Cookies", 0);
        return items;
    }
}
