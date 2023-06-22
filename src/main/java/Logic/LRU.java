package Logic;


import java.util.LinkedHashSet;

public class LRU implements CacheReplacementPolicy {
    LinkedHashSet<String> words;

    // Constructor
    public LRU() {
        words = new LinkedHashSet<>();
    }


    // Add a book to the Cache by the Last Recently Used Policy.
    @Override
    public void add(String s) {
        words.remove(s);
        words.add(s);
    }


    // Remove a book from the Cache by the Last Recently Used Policy.
    @Override
    public String remove() {
        String[] arr = new String[words.size()];
        arr = words.toArray(arr);
        words.remove(arr[0]);
        return arr[0];
    }
}
