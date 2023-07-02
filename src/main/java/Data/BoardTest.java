package Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class BoardTest {
    public static Square[][] board;
    private static Tile[][] tiles;

    private static Board b;
    String[][] colors;
    ArrayList<Word> allWords;
    private Tile.Bag bag = null;


    public static class Square { //square is an object that contains tile and color
        public Tile tile;
        public final int col;
        public final int row;
        public int bunos;//1=Tripple word,2=tripple letter,3=double word, 4=double letter

        public Square(Tile t, int b, int r, int c) {
            this.tile = t;
            this.bunos = b;
            this.col = c;
            this.row = r;

        }

    }


    private BoardTest() {

        bag = Tile.Bag.getBag();
        board = new Square[15][15];//initialize the new board
        int i, j, k, l;
        int N = 15;
        int n = 15;
        for (i = 0; i < N; i++) {
            for (j = 0; j < N; j++) {//initialize al board to their index
                board[i][j] = new Square(null, 0, i, j);
            }
        }

        n /= 2;//initialize the triple word score board =1
        for (i = 0; i < N; i += n) {
            for (j = 0; j < N; j += n) {
                if (i == 7 && j == 7) {
                    board[i][j] = new Square(null, 3, i, j);
                } else {
                    board[i][j] = new Square(null, 1, i, j);
                }

            }

        }

        n = 15;// initialize the triple letter score board =2
        for (i = 1, j = 5; i < N - 1; i += 4, i += 4) {
            board[i][j].bunos = 2;
            board[i][(n - j) - 1].bunos = 2;
            board[j][i].bunos = 2;
            board[j][j].bunos = 2;
            board[j][n - j].bunos = 2;
            board[j][(n - i) - 1].bunos = 2;

        }

        for (i = 1; i < 5; i++)//initialize the double word score =3
        {
            board[i][i].bunos = 3;
            board[i][(n - i) - 1].bunos = 3;
            board[(n - i) - 1][i].bunos = 3;
            board[(n - i) - 1][(n - i) - 1].bunos = 3;
        }

        for (i = 0, j = 2, k = 3, l = 6; i < N - 1; i += 7, j *= 6, k += 8, l += 2)//initialize the double letter score=4
        {
            board[i][3].bunos = 4;
            board[i][n - 3 - 1].bunos = 4;
            board[j][6].bunos = 4;
            board[j][8].bunos = 4;
            board[k][0].bunos = 4;
            board[k][7].bunos = 4;
            board[k][n - 1].bunos = 4;
            board[l][2].bunos = 4;
            board[l][6].bunos = 4;
            board[l][8].bunos = 4;
            board[l][n - 2 - 1].bunos = 4;

        }
        tiles = getTiles();
    }

    private void initBoard() {
        // Allocate arrays.

        colors = new String[15][15];

        // Init color matrix
        for (int i = 0; i < 15; i++)
            for (int j = 0; j < 15; j++)
                colors[i][j] = "";


        colors[7][7] = "Star";

        colors[0][0] = "Red";
        colors[7][0] = "Red";
        colors[14][0] = "Red";
        colors[0][7] = "Red";
        colors[14][7] = "Red";
        colors[0][14] = "Red";
        colors[7][14] = "Red";
        colors[14][14] = "Red";

        colors[1][1] = "Yellow";
        colors[2][2] = "Yellow";
        colors[3][3] = "Yellow";
        colors[4][4] = "Yellow";
        colors[13][1] = "Yellow";
        colors[12][2] = "Yellow";
        colors[11][3] = "Yellow";
        colors[10][4] = "Yellow";
        colors[1][13] = "Yellow";
        colors[2][12] = "Yellow";
        colors[3][11] = "Yellow";
        colors[4][10] = "Yellow";
        colors[10][10] = "Yellow";
        colors[11][11] = "Yellow";
        colors[12][12] = "Yellow";
        colors[13][13] = "Yellow";

        colors[3][0] = "LightBlue";
        colors[11][0] = "LightBlue";
        colors[6][2] = "LightBlue";
        colors[8][2] = "LightBlue";
        colors[0][3] = "LightBlue";
        colors[7][3] = "LightBlue";
        colors[14][3] = "LightBlue";
        colors[2][6] = "LightBlue";
        colors[6][6] = "LightBlue";
        colors[8][6] = "LightBlue";
        colors[12][6] = "LightBlue";
        colors[3][7] = "LightBlue";
        colors[11][7] = "LightBlue";
        colors[2][8] = "LightBlue";
        colors[6][8] = "LightBlue";
        colors[8][8] = "LightBlue";
        colors[12][8] = "LightBlue";
        colors[0][11] = "LightBlue";
        colors[7][11] = "LightBlue";
        colors[14][11] = "LightBlue";
        colors[6][12] = "LightBlue";
        colors[8][12] = "LightBlue";
        colors[3][14] = "LightBlue";
        colors[11][14] = "LightBlue";

        colors[5][1] = "Blue";
        colors[9][1] = "Blue";
        colors[1][5] = "Blue";
        colors[5][5] = "Blue";
        colors[9][5] = "Blue";
        colors[13][5] = "Blue";
        colors[1][9] = "Blue";
        colors[5][9] = "Blue";
        colors[9][9] = "Blue";
        colors[13][9] = "Blue";
        colors[5][13] = "Blue";
        colors[9][13] = "Blue";
    }

    public Square getSquare(int r, int c) {
        return board[r][c];
    }

    public Tile.Bag getBag() {
        return this.bag;
    }

    public Tile[][] getTiles() { // gets a copy Tiles array of board
        Tile[][] tilenew = new Tile[15][15];
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (getSquare(i, j).tile == null) {
                    tilenew[i][j] = new Tile(' ', getSquare(i, j).bunos);
                } else {
                    tilenew[i][j] = getSquare(i, j).tile;
                }

            }
        }
        return tilenew;

    }


    // SingleTone Pattern
    public static Board getBoard() {
        if (b == null)
            b = new Board();
        return b;
    }


    public boolean dictionaryLegal(Word w) {
        return true;
    }


    // Check if word fit the size of the board
    private boolean wordInBoard(Word w) {
        return w.tiles.length < 16;
    }

    //method for board legal
    private boolean insideBoard(Word word) {//checks if the word is inside the board limits(method 1)
        if (word.getCol() > 14 || word.getLength() > 14 || word.getRow() < 0 || word.getCol() < 0 || word.getRow() > 14)//checks the common ability
        //of non-vertical a vertical words for out limits
        {
            return false;
        }
        if (word.isVertical()) {
            return word.getRow() + word.getLength() - 1 <= 14;

        } else if (word.getCol() + word.getLength() - 1 > 14) {
            return false;

        }
        return true;


    }

    private boolean nullTile(Word word) {
        int i = word.getRow(), j = word.getCol(), k = 0;
        if (word.isVertical()) {
            for (; k < word.getLength(); i++, k++) {
                if (word.tiles[k].letter != tiles[i][j].letter && board[i][j].tile != null) {
                    return false;
                }
            }
        } else {
            for (; k < word.getLength(); j++, k++) {
                if (word.tiles[k].letter != tiles[i][j].letter && board[i][j].tile != null) {
                    return false;
                }
            }

        }
        return true;
    }

    private boolean hofef(Word word) {//check if we have a tile that merge with the word (method2)
        int i = word.getRow(), j = word.getCol(), k = 0;
        if (!nullTile(word)) {
            return false;
        }
        if (word.isVertical()) {                //checks the vertical ways to fet false on word
            for (; k < word.getLength(); i++, k++) {
                if (board[i][j] != null) {
                    if (board[i][j].tile != word.tiles[k] && board[i][j].tile != null) {//check if the tiles are equals and don't need changes
                        return false;
                    }
                    return true;
                }

            }
            for (; k < word.getLength(); j++, k++) {//check the non-vertical ways to get false
                if (board[i][j] != null) if (board[i][j].tile != word.tiles[k]) {//checks if the tiles are not changing
                    return false;
                }

            }

        }
        return true;
    }


    public int getScore(Word word) {
        int score = 0, i = word.getRow(), j = word.getCol(), k = 0;
        Stack<Integer> stack = new Stack<Integer>();
        Word wordOnBoard = onBoard(word);
        if (word.isVertical()) {
            for (k = 0; k < word.getLength(); k++, i++) {
                if (word.tiles[k] != null) {
                    score += (scoreSwitch(word.tiles[k], i, j));
                    stack.push(scoreWord(word.tiles[k], i, j));
                } else {
                    if (word.tiles[k] == null && board[i][j].tile != null) {
                        score += board[i][j].tile.score;
                        stack.push(scoreWord(board[i][j].tile, i, j));

                    } else {
                        if (word.tiles[k] != null) {
                            score += word.tiles[k].score;
                            stack.push(scoreWord(word.tiles[k], i, j));
                        }

                    }
                }
            }
        } else {
            for (k = 0; k < word.getLength(); k++, j++) {
                if (word.tiles[k] != null) {
                    score += (scoreSwitch(word.tiles[k], i, j));
                    stack.push(scoreWord(word.tiles[k], i, j));
                } else {
                    if (board[i][j].tile == null && board[i][j].tile != null) {
                        score += board[i][j].tile.score;
                        stack.push(scoreWord(board[i][j].tile, i, j));

                    } else {
                        score += board[i][j].tile.score;
                        stack.push(scoreWord(word.tiles[k], i, j));
                    }
                }
            }

        }
        while (!stack.empty()) {
            score *= stack.pop();
        }
        return score;
    }

    private boolean leanWord(Word word) {
        if (word.isVertical()) {
            ArrayList<Word> leans;
            leans = checkOpeset(word);
            if (leans.size() != 0) {
                return true;
            }
            return word != verticalBoardStrting(word);
        } else {
            ArrayList<Word> leans = new ArrayList<Word>();
            leans = checkOpeset(word);
            if (leans.size() != 0) {
                return true;
            }
            return word != nonVBoardString(word);
        }
    }

    private boolean middleCheck(Word word) {//check if the word can be placed in the star middle

        if (word.isVertical()) {
            int j = word.getCol();
            for (int i = word.getRow(); i < word.getLength() + word.getRow(); i++) {
                if (i == 7 && j == 7) {
                    return true;

                }

            }

        } else {
            int j = word.getRow();
            for (int i = 0; i < word.getCol() + word.getLength(); i++) {
                if (i == 7 && j == 7) {
                    return true;
                }
            }

        }
        return false;
    }

    //ends of the method for boardLegal

    public boolean boardLegal(Word word) {//the main check for board legal
        boolean answer = insideBoard(word);//send for function that checks if the word is out of the
        // board boundaries
        if (!answer) {
            return false;
        }
        if (board[7][7].tile == null) {
            if (!middleCheck(word)) {
                return false;
            }
        }
        if (!leanWord(word)) {
            return false;
        }
        answer = hofef(word);//sending the word to function that's check if the word is merging with another word
        if (!answer) {
            return false;
        }
        return true;
    }

    public int scoreWord(Tile tile, int r, int c) {

        int j = c, k = 0;
        if (r == 7 && j == 7) {
            if (board[7][7].tile != null) {
                return 1;
            }
        }

        switch (board[r][j].bunos) {
            case 0: //don't have bonus
                return 1;
            case 1: //triple word score
                return 3;
            case 2: //triple letter score
                return 1;

            case 3: //double word score
                return 2;

            case 4: //double letter score
                return 1;
            default:
                break;
        }

        return 1;
    }


    public int scoreSwitch(Tile tile, int row, int col) {
        int score = 0;
        int i = row, j = col;
        switch (board[i][j].bunos) {
            case 0: //don't have bonus
                score += tile.score;
                break;
            case 1: //triple word score
                score += tile.score;
                break;
            case 2: //triple letter score
                score += (tile.score * 3);
                break;
            case 3: //double word score
                score += tile.score;

                break;
            case 4: //double letter score
                score += (tile.score * 2);
                break;
            default:
                break;
        }

        return score;
    }

    public int tryPlaceWord(Word word) {
        int score = 0, i;
        if (!boardLegal(word)) { //we check if the word is legal on the board
            return -1;
        }
        ArrayList<Word> words = new ArrayList<Word>();// here well put all the words that
        // well get from adding the new word

        words = getWords(word);      //sending the new word to the function that's shows ass all the
        // new words that will add to the board
        for (i = 0; i < words.size(); i++) {//checks if the words in the array is legal and removes the unlegal words

            if (!dictionaryLegal(words.get(i))) {
                words.remove(i);
            }

        }

        for (i = 0; i < words.size(); i++) { //here we sum all the scores that we get from every new word;
            score += getScore(words.get(i));
            addWord(words.get(i));
        }

        return score;

    }

    private boolean equals(Word word1, Word word2) {
        if (word1.getLength() != word2.getLength()) {
            return false;
        }
        int length = (word1.getLength() + word2.getLength()) / 2;
        for (int i = 0; i < length; i++) {
            if (word1.tiles[i] != word2.tiles[i]) {
                return false;
            }
        }
        return true;
    }

    private Word onBoard(Word word) {//show the word that we alredy have on the board from the new word

        int i = 0, r = word.getRow(), c = word.getCol();
        Word wordNew = new Word(word.tiles, word.getRow(), word.getCol(), word.isVertical());
        if (word.isVertical()) {
            for (; i < word.getLength(); i++, r++) {
                if (board[r][c].tile == null) {
                    wordNew.tiles[i] = null;
                }
            }

        } else {
            for (; i < word.getLength(); c++, i++) {
                if (board[r][c].tile == null) {
                    wordNew.tiles[i] = null;
                }
            }
        }
        return wordNew;

    }

    public void addWord(Word word) {
        int i = word.getRow(), j = word.getCol(), k = 0;
        if (word.isVertical()) {
            for (; k < word.getLength(); i++, k++) {
                if (board[i][j].tile == null && word.tiles[k].letter != ' ') {
                    board[i][j].tile = word.tiles[k];
                }
            }
        } else {
            for (; k < word.getLength(); j++, k++) {
                if (word.tiles[k].letter != ' ' && board[i][j].tile == null) {
                    board[i][j].tile = word.tiles[k];
                }
            }

        }

    }

    public ArrayList<Word> getWords(Word word) {//
        ArrayList<Word> arrayWords = new ArrayList<Word>();
        ArrayList<Word> addWords = new ArrayList<Word>();
        Word wordVer;
        if (word.isVertical()) {
            wordVer = verticalBoardStrting(word);
            if (!dictionaryLegal(wordVer)) {
                wordVer = null;
            }
        } else {
            wordVer = nonVBoardString(word);
            if (!dictionaryLegal(wordVer)) {
                wordVer = null;
            }
        }
        arrayWords = checkOpeset(word);
        if (wordVer != null) {
            arrayWords.add(wordVer);
        }
        for (int i = 0; i < arrayWords.size(); i++) {
            if (equals(arrayWords.get(i), onBoard(arrayWords.get(i)))) {
                arrayWords.remove(i);
            }
        }

        return arrayWords;

    }

    private Word verticalBoardStrting(Word word) {//this function takes a square  with new  vertical word and
        // checks the new word that this word is adding in the board in vertical ways

        ArrayList<Tile> tileArrayList = new ArrayList<Tile>(15);//iniatializ new array for the new word
        Stack<Tile> stackTiles = new Stack<Tile>();//making a stack for the upper tiles
        int i, j, sizeWord = 0;
        i = word.getRow() - 1;
        j = word.getCol();
        int endRow;
        endRow = word.getRow() + word.getLength() - 1;
        while (board[i][j].tile != null) {//pushes the upper tiles from the board to the stack
            stackTiles.push(board[i][j].tile);
            i--;
            sizeWord++;
        }
        int rowNew = i + 1;
        while (!stackTiles.empty()) { //adding the new tiles to the array
            tileArrayList.add(stackTiles.pop());

        }
        for (int k = 0; k < word.getLength(); sizeWord++, k++) {
            if (word.tiles[k] != null) {
                tileArrayList.add(sizeWord, word.tiles[k]);
            } else {
                tileArrayList.add(sizeWord, board[word.getRow() + k][word.getCol()].tile);
            }
        }
        int k = 1;
        while (board[endRow + k][j].tile != null) {//adding the tiles that string the new word from under
            tileArrayList.add(sizeWord, board[endRow + k][j].tile);
            k++;
        }

        Word wordNew = new Word(toTileArray(tileArrayList), rowNew, j, true);
        return wordNew;

    }

    public Word nonVBoardString(Word word) {

        ArrayList<Tile> tileArrayList = new ArrayList<Tile>(15);//initialize new array for the new word
        Stack<Tile> stackTiles = new Stack<Tile>();//making a stack for the left tiles
        int i = word.getRow();
        int j = word.getCol() - 1;
        int endCol = word.getCol() + word.getLength() - 1;
        while (board[i][j].tile != null) {//pushes the left tiles from the board to the stack
            stackTiles.push(board[i][j].tile);
            j--;
        }
        int colNew = j + 1;
        while (!stackTiles.empty()) { //adding the new tiles to the array
            tileArrayList.add(stackTiles.pop());
        }
        for (i = 0; i < word.getLength(); i++) {
            if (board[word.getRow()][word.getCol() + i].tile != null) {
                tileArrayList.add(board[word.getRow()][word.getCol() + i].tile);
            } else {
                tileArrayList.add(word.tiles[i]);//copy the tiles from the word array to the new tile array
            }

        }
        while (board[endCol + 1][j].tile != null && j < 15) {//adding the tiles that string the new word from under
            tileArrayList.add(board[endCol][j].tile);
            j++;

        }


        Word wordNew = new Word(toTileArray(tileArrayList), word.getRow(), colNew, false);
        return wordNew;

    }


    //this method check all the posibilities of the upest direction of word
    //for vertical words its check the non-vertical words, for non-vertical
    // its check the new vertical words on the board

    public ArrayList<Word> checkOpeset(Word word) {
        ArrayList<Word> wordsList = new ArrayList<Word>();
        Word wordNew;
        if (word.isVertical()) {// sends all the new words to the function that's check the strings
            for (int i = 0; i < word.getLength(); i++) {
                Tile[] newTile = new Tile[1];
                newTile[0] = word.tiles[i];
                wordNew = new Word(newTile, word.getRow() + i, word.getCol(), false);
                wordNew = (nonVBoardString(wordNew));
                if (!equals(wordNew, onBoard(wordNew))) {

                    if (dictionaryLegal(wordNew)) {
                        wordsList.add(wordNew);
                    }
                }

            }
        } else {
            for (int i = 0; i < word.getLength(); i++) {
                Tile[] newTile = new Tile[1];
                newTile[0] = word.tiles[i];
                wordNew = new Word(newTile, word.getRow(), word.getCol() + i, true);
                wordNew = (verticalBoardStrting(wordNew));
                if (!equals(wordNew, onBoard(wordNew))) {

                    if (dictionaryLegal(wordNew)) {
                        wordsList.add(wordNew);
                    }


                }
            }
        }

        return wordsList;

    }

    public Tile[] toTileArray(ArrayList<Tile> arrayList) {
        Tile[] tileNew = new Tile[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            tileNew[i] = arrayList.get(i);
        }
        return tileNew;

    }


    // Check if board is empty
    private boolean boardIsEmpty() {
        for (int i = 0; i < 15; i++)
            for (int j = 0; j < 15; j++)
                if (board[i][j] != null)
                    return false;
        return true;
    }

    //function to fill an array to 7 tiles  // according the bag tiles
    public List<Tile> setTilesPlayer(List<Tile> arr) {
        int i = arr.size();
        if (bag.qSize() != 0) {
            for (; i < 7; i++) {
                arr.add(bag.getRand());
            }

        }
        return arr;
    }


    public Tile[] toTileArray(List<Tile> arrayList) {
        Tile[] tileNew = new Tile[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            tileNew[i] = arrayList.get(i);
        }
        return tileNew;

    }

}
