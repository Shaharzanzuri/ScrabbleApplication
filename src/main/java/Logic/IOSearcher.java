package Logic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class IOSearcher {
    // Check if given book is in the  I/O Files. return the answer
    public static boolean search(String s, String... fileNames) {
        for (String fName : fileNames) {
            try (BufferedReader br = new BufferedReader(new FileReader(fName))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.contains(s))
                        return true;
                }
            } catch (IOException ignored) {
            }
        }
        return false;
    }
}
