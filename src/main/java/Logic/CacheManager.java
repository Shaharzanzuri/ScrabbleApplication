package Logic;


import java.util.HashSet;

public class CacheManager {
    HashSet<String> words;
    CacheReplacementPolicy myCrp; // Policy
    int mySize;

    // Constructor
    public CacheManager(int size, CacheReplacementPolicy crp) {
        words = new HashSet<>();
        myCrp = crp;
        mySize = size;
    }

    // Ask if specific book is in the Cache
    public boolean query(String s) {
        return words.contains(s);
    }

    // Adding specific book to Cache
    public void add(String s) {
        myCrp.add(s);
        words.add(s);
        if (words.size() > mySize)
            words.remove(myCrp.remove());
    }
}
