package Data;

public class Word {
    public final Tile[] tiles;
    private int row = 0;
    private int col = 0;

    final boolean vertical;
    private final int length;


    public Word(Tile[] tile, int r, int c, boolean ver) {//constructor for word


        tiles = new Tile[tile.length];
        for (int i = 0; i < tile.length; i++) {
            tiles[i] = tile[i];
        }
        this.length = tile.length;
        this.col = c;
        this.row = r;
        this.vertical = ver;

    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;

    }

    public boolean isVertical() {
        return this.vertical;
    }

    public int getLength() {
        return this.length;
    }

    public static Tile[] wordToTiles(Word word){
        Tile[] tilesNew=new Tile[word.getLength()];
        for(int i=0;i< word.getLength();i++){
            tilesNew[i]=word.tiles[i];
        }
        return tilesNew;
    }

    public Tile[] getTiles() {
        return tiles;
    }


    public static Word stringToWord(String word, int row, int col, boolean isVertical) {
        System.out.println(word);
        System.out.println(word.length());
        Tile[] t =Tile.stringToTilesArray(word);
        return new Word(t, row, col, isVertical);
    }

}
