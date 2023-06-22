package Logic;

import java.util.HashMap;

public class DictionaryManager {
    HashMap<String, Dictionary> map;

    DictionaryManager() {
        map = new HashMap<>();
    }

    private static DictionaryManager myDic = null;

    // Return true or false if the last book of the book list given is in the Dictionary map,
    // if it is return true if not add it and return false.
    public boolean query(String... args) {
        addBook(args);
        String word = args[args.length - 1];
        boolean flag = false;
        for (int i = 0; i < args.length - 1; i++) {
            if (map.get(args[i]).query(word))
                flag = true;
        }
        return flag;
    }

    // helper method
    private void addBook(String... args) {
        for (int i = 0; i < args.length - 1; i++) {
            if (!map.containsKey(args[i]))
                map.put(args[i], new Dictionary(args[i]));
        }
    }

    // Check if the last book in book list is in the Dictionary map and return the answer
    public boolean challenge(String... args) {
        addBook(args);
        String word = args[args.length - 1];
        boolean flag = false;
        for (int i = 0; i < args.length - 1; i++) {
            if (map.get(args[i]).challenge(word))
                flag = true;
        }
        return flag;
    }

    // Return the Dictionary map size.
    public int getSize() {
        return map.size();
    }

    // Return the Dictionary map by Single-tone Design Pattern
    public static DictionaryManager get() {
        if (myDic == null)
            myDic = new DictionaryManager();
        return myDic;
    }
}
