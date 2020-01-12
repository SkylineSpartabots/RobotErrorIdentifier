package client.extras;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * An oject that can store messages and timestamps of a certain filter type.
 */
public class LogList {
    public ArrayList<String> messages;
    public ArrayList<String> timeStamps;
    public HashMap<String, List<String>> values;

    public LogList() {
        messages = new ArrayList<>();
        timeStamps = new ArrayList<>();
        values = new HashMap<>();
    }

    public LogList(ArrayList<String> messages, ArrayList<String> timeStamps, HashMap<String, List<String>> values) {
        this.messages = messages;
        this.timeStamps = timeStamps;
        this.values = values;
    }
}