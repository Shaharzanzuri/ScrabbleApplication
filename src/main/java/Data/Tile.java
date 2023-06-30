package Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Tile {
    public final char letter;
    public final int score;

    Tile(char tav, int score) {
        this.letter = tav;
        this.score = score;
    }


    public static class Bag {

        private static Bag b = null;

        //bag have the quantity of the tiles while playing game
        private static Qtile[] bag;

        //tab is the score array of the tiles

        private static Tile[] tab;

        private int bagSize;

        //bagImplement have the real quantity og every tav//
        public final int[] bagImplemant;

        public static class Qtile {
            public char Tav;
            public int Quantitie;

            public Qtile(char tav, int Q) {
                this.Tav = tav;
                this.Quantitie = Q;
            }
        }

        public static Bag getBag() {
            if (Bag.b == null) {
                b = new Bag();
            }
            return b;
        }


        private Bag() {
            //constructor of tab score
            tab = new Tile[26];
            tab[0] = new Tile('A', 1);
            tab[1] = new Tile('B', 3);
            tab[2] = new Tile('C', 3);
            tab[3] = new Tile('D', 2);
            tab[4] = new Tile('E', 1);
            tab[5] = new Tile('F', 4);
            tab[6] = new Tile('G', 2);
            tab[7] = new Tile('H', 4);
            tab[8] = new Tile('I', 1);
            tab[9] = new Tile('J', 8);
            tab[10] = new Tile('K', 5);
            tab[11] = new Tile('L', 1);
            tab[12] = new Tile('M', 3);
            tab[13] = new Tile('N', 1);
            tab[14] = new Tile('O', 1);
            tab[15] = new Tile('P', 3);
            tab[16] = new Tile('Q', 10);
            tab[17] = new Tile('R', 1);
            tab[18] = new Tile('S', 1);
            tab[19] = new Tile('T', 1);
            tab[20] = new Tile('U', 1);
            tab[21] = new Tile('V', 4);
            tab[22] = new Tile('W', 4);
            tab[23] = new Tile('X', 8);
            tab[24] = new Tile('Y', 4);
            tab[25] = new Tile('Z', 10);

            // constructor of bag
            bag = new Qtile[26];
            bag[0] = new Qtile('A', 9);
            bag[1] = new Qtile('B', 2);
            bag[2] = new Qtile('C', 2);
            bag[3] = new Qtile('D', 4);
            bag[4] = new Qtile('E', 12);
            bag[5] = new Qtile('F', 2);
            bag[6] = new Qtile('G', 3);
            bag[7] = new Qtile('H', 2);
            bag[8] = new Qtile('I', 9);
            bag[9] = new Qtile('J', 1);
            bag[10] = new Qtile('K', 1);
            bag[11] = new Qtile('L', 4);
            bag[12] = new Qtile('M', 2);
            bag[13] = new Qtile('N', 6);
            bag[14] = new Qtile('O', 8);
            bag[15] = new Qtile('P', 2);
            bag[16] = new Qtile('Q', 1);
            bag[17] = new Qtile('R', 6);
            bag[18] = new Qtile('S', 4);
            bag[19] = new Qtile('T', 6);
            bag[20] = new Qtile('U', 4);
            bag[21] = new Qtile('V', 2);
            bag[22] = new Qtile('W', 2);
            bag[23] = new Qtile('X', 1);
            bag[24] = new Qtile('Y', 2);
            bag[25] = new Qtile('Z', 1);


            bagImplemant = new int[26];
            for (int i = 0; i < 26; i++) {
                bagImplemant[i] = bag[i].Quantitie;
            }

            bagSize = 98;


        }

        public int[] getQuantities() {

            int[] tabnew = new int[26];

            for (int i = 0; i < 26; i++) {
                tabnew[i] = bag[i].Quantitie;

            }
            return tabnew;

        }


        public Tile getRand() {
            Tile tile;
            if (qSize() == 0) {
                return null;
            }
            Random rand = new Random();
            int index = rand.nextInt(26);
            if (bag[index].Quantitie > 0) {
                bag[index].Quantitie--;
                tile = new Tile(tab[index].letter, tab[index].score);
                bagSize--;
                return tile;
            } else {
                for (int i = 0; i < 26; i++) {
                    if (i != index) {
                        if (bag[i].Quantitie > 0) {
                            bag[i].Quantitie--;
                            tile = new Tile(tab[i].letter, tab[i].score);
                            bagSize--;
                            return tile;
                        }

                    }
                    i++;
                }

            }
            return null;
        }


        public void put(Tile tile) {
            if (tile != null) {
                int index = tile.letter - 'A';
                if (index < 26 && index >= 0) {
                    int quantity = bag[index].Quantitie;
                    if (quantity + 1 <= bagImplemant[index]) {
                        bag[index].Quantitie++;
                        bagSize++;

                    }
                }
            }

        }

        public int getBagSize() {
            return bagSize;
        }

        public int qSize() {
            int size = 0;
            for (int i = 0; i < 26; i++) {
                if (bag[i].Quantitie != 0) {
                    size += bag[i].Quantitie;
                }
            }
            return size;
        }

        public Tile getTile(char tav) {
            int index = tav - 'A';
            if (index < 26 && index >= 0) {
                if (this.bag[index].Quantitie > 0 && this.bag[index].Quantitie - 1 < this.bagImplemant[index]) {
                    Tile tile = new Tile(tav, tab[index].score);
                    bagSize--;
                    return tile;
                }

            }
            return null;

        }


    }

    //convert a Tile to string
    @Override
    public String toString() {
        return letter + ";" + score;
    }

    public static String ListToString(List<Tile> tileList) // Convert List to String
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (Tile tile : tileList) {
            stringBuilder.append(tile.toString()).append(",");
        }
        String listAsString = stringBuilder.toString();
        listAsString = listAsString.substring(0, listAsString.length() - 1); // Remove trailing comma
        return listAsString;
    }

    public static List<Tile> stringToTilesList(String listAsString) { // Convert String back to List
        List<Tile> restoredTileList = new ArrayList<>();
        String[] tilePairs = listAsString.split(",");
        for (String tilePair : tilePairs) {
            String[] parts = tilePair.split(";");
            char letter = parts[0].charAt(0);
            int score = Integer.parseInt(parts[1]);
            restoredTileList.add(new Tile(letter, score));
        }
        return restoredTileList;
    }

    public static Tile[] stringToTilesArray(String string) {
        HashMap<Character, Integer> map = initiallizeScoreChar();
        Tile[] restoredTileList = new Tile[string.length()];
        char[] charlist = new char[string.length()];
        string.getChars(0, string.length(), charlist, 0);
        int i = 0;
        for (char tilePair : charlist) {
            int score = map.get(tilePair);
            restoredTileList[i] = (new Tile(tilePair, score));
            i++;
        }
        return restoredTileList;
    }


    // converts a Tiles array to string
    public static Tile[][] tilesFromString(String str) {
        String[] rows = str.split("row");
        int numRows = rows.length;
        int numCols = rows[0].split(",").length;
        Tile[][] tiles = new Tile[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            String[] tileData = rows[i].split(",");
            for (int j = 0; j < numCols; j++) {
                String[] tileParts = tileData[j].split(";");
                char character = tileParts[0].charAt(0);
                int score = Integer.parseInt(tileParts[1]);
                tiles[i][j] = new Tile(character, score);
            }
        }

        return tiles;
    }

    public static String tilesToString(Tile[][] tiles) {
        StringBuilder sb = new StringBuilder();

        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                sb.append((tile).toString()).append(",");
            }
            sb.append("row");
        }
        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }


    public static HashMap<Character, Integer> initiallizeScoreChar() {
        HashMap<Character, Integer> map = new HashMap<>();
        map.put('A', 1);
        map.put('B', 3);
        map.put('C', 3);
        map.put('D', 2);
        map.put('E', 1);
        map.put('F', 4);
        map.put('G', 2);
        map.put('H', 4);
        map.put('I', 1);
        map.put('J', 8);
        map.put('K', 5);
        map.put('L', 1);
        map.put('M', 3);
        map.put('N', 1);
        map.put('O', 1);
        map.put('P', 3);
        map.put('Q', 10);
        map.put('R', 1);
        map.put('S', 1);
        map.put('T', 1);
        map.put('U', 1);
        map.put('V', 4);
        map.put('W', 4);
        map.put('X', 8);
        map.put('Y', 4);
        map.put('Z', 10);
        map.put('_', 0);
        map.put(' ', 0);
        return map;
    }

}
