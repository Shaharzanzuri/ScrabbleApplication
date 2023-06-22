package Logic;


import java.util.LinkedHashMap;
import java.util.Map;

public class LFU implements CacheReplacementPolicy {
    LinkedHashMap<String, Integer> words;
    String minStr;

    // Constructor
    public LFU() {
        words = new LinkedHashMap<>();
        minStr = null;
    }

    // Add a book to the Cache by the Last Frequency Used Policy.
    @Override
    public void add(String s) {
        if (words.containsKey(s))
            words.put(s, words.get(s) + 1);
        else if (words.isEmpty()) {
            words.put(s, 1);
            minStr = s;
        } else
            words.put(s, 1);
    }

    // Remove a book from the Cache by the Last Frequency Used Policy.
    @Override
    public String remove() {
        int minCount = words.get(minStr);
        for (Map.Entry<String, Integer> word : words.entrySet()) {
            if (minCount > word.getValue()) {
                minCount = word.getValue();
                minStr = word.getKey();
            }
        }
        return minStr;
    }
}