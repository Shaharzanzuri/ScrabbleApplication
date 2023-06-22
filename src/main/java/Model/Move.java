package Model;

import Data.*;

public class Move {
    Tile tile;
    int row;
    int col;
    boolean vertical;

    int score = 0;

    public Move(Tile tile, int row, int col, boolean vertical) {
        this.tile = tile;
        this.row = row;
        this.col = col;
        this.vertical = vertical;
    }

    public Word MoveToWord() {
        Tile[] tile = new Tile[1];
        tile[0] = this.tile;
        Word word = new Word(tile, this.row, this.col, this.vertical);
        return word;
    }

    public static Move stringToMove(String str) {

        String[] parts = str.split(",");
        if (parts.length == 4) {
            Tile tile = Tile.Bag.getBag().getTile(parts[0].charAt(0));
            int row = Integer.parseInt(parts[1]);
            int col = Integer.parseInt(parts[2]);
            boolean vertical = Boolean.parseBoolean(parts[3]);
            return new Move(tile, row, col, vertical);
        }
        return null;
    }

    public static String moveToString(Move move) {
        //transfor from move to string
        return move.tile.toString() + "," + move.row + "," + move.col + "," + move.vertical;
    }


}
