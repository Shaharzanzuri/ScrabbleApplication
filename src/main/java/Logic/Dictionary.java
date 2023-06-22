package Logic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Dictionary {
    CacheManager cacMan1;
    CacheManager cacMan2;
    BloomFilter bf;
    String[] fNames;

    // Constructor
    public Dictionary(String... fileNames) {
        cacMan1 = new CacheManager(400, new LRU()); // Exists
        cacMan2 = new CacheManager(100, new LFU());// Not exists
        fNames = fileNames;

        bf = new BloomFilter(256, "SHA1", "MD5");

        for (String fName : fileNames) {
            try (BufferedReader br = new BufferedReader(new FileReader(fName))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] lineWords = line.split(" ");
                    for (String word : lineWords)
                        if (!word.isEmpty())
                            bf.add(word);
                }
            } catch (IOException ignored) {
            }
        }
    }
    // return true or false if Cache contain book name.
    public boolean query(String s) {
        if (cacMan1.query(s))
            return true;
        if (cacMan2.query(s))
            return false;
        if (bf.contains(s)) {
            cacMan1.add(s);
            return true;
        }
        cacMan2.add(s);
        return false;
    }

    // return true or false if Files contain book name.
    public boolean challenge(String s) {
        boolean ans;
        ans = IOSearcher.search(s, fNames);
        if (ans)
            cacMan1.add(s);
        else
            cacMan2.add(s);

        return ans;
    }
}
