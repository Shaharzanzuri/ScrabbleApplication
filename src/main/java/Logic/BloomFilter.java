package Logic;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;

public class BloomFilter {
    BitSet arr;
    String[] hashArr;
    int bitSize;

    // Bloom Filter Constructor
    public BloomFilter(int size, String... algs) {
        arr = new BitSet();
        hashArr = algs;
        bitSize = size;
    }

    // Adding book to BF
    public void add(String s) {
        for (String hash : hashArr) {
            try {
                MessageDigest md = MessageDigest.getInstance(hash);
                byte[] bts = md.digest(s.getBytes());
                BigInteger result = new BigInteger(bts);
                int save = result.intValue() % bitSize;
                if (!arr.get(Math.abs(save)))
                    arr.flip(Math.abs(save));
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Check if book exist in BF
    public boolean contains(String s) {

        for (String hash : hashArr) {
            try {
                MessageDigest md = MessageDigest.getInstance(hash);
                byte[] bts = md.digest(s.getBytes());
                BigInteger result = new BigInteger(bts);
                int save = result.intValue() % bitSize;
                if (!arr.get(Math.abs(save)))
                    return false;
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    // Return a binary String of the word.
    @Override
    public String toString() {
        int savePos = 0;
        for (int i = 0; i < bitSize; i++) {
            if (arr.get(i))
                savePos = i;
        }

        int[] a = new int[savePos + 1];
        for (int i = 0; i < a.length; i++) {
            if (arr.get(i))
                a[i] = 1;
            else a[i] = 0;
        }

        StringBuilder res = new StringBuilder();

        for (int j : a) {
            res.append(j);
        }

        return res.toString();
    }
}
