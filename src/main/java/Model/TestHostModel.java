package Model;

import Data.Tile;
import Logic.BookScrabbleHandler;
import Logic.MyServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestHostModel  {

    HostModel host;
    MyServer s;
    BookScrabbleHandler bsh;


    public void init()  throws IOException, InterruptedException, ClassNotFoundException {
        bsh = new BookScrabbleHandler();
        s = new MyServer(8887,bsh);
        s.start();
        host = new HostModel("Host");
    }

    public void testStartGame() throws IOException, ClassNotFoundException {

        if(!host.isMyTurn() ||  host.getBoard() ==null)
            System.out.println("Error in start game");
    }

    public void submitWord() throws IOException, ClassNotFoundException {
        if(!host.submitWord("HORN",7,5, false) && host.players.get(0).getScore() == 14)
            System.out.println("Error in submit word");
        if(host.submitWord("SDF",7,7, false))
            System.out.println("Error in submit word #2");
        if(!host.submitWord("FA_M",5,7, true) && host.players.get(0).getScore() == 23)
            System.out.println("Error in submit word #3");
        if(!host.submitWord("PASTE",9,5,false) && host.players.get(0).getScore() == 48)
            System.out.println("Error in submit word #3");
        if(!host.submitWord("_OB",8,7, false) && host.players.get(0).getScore() == 66)
            System.out.println("Error in submit word #3");
        if(!host.submitWord("BIT",10,4, false) && host.players.get(0).getScore() == 88)
            System.out.println("Error in submit word #3");
        System.out.println(host.players.get(0).getScore());

    }

    public void testGetScore() throws IOException, ClassNotFoundException {
        if(!host.getScore().equals("Host:88"))
            System.out.println("Error in get score #1");
        System.out.println("---------------End of test get score---------------");
    }

    // '\u0000' = null in char language
    public void testGetBoard() throws IOException, ClassNotFoundException {
        Tile[][] board = host.getBoard();
        String stringBoard=Tile.tilesToString(board);
        System.out.println("tilesToString result:");
        System.out.println(stringBoard);
        System.out.println("board tiles as the Tile[][] board :");
        System.out.println(Arrays.deepToString(board));
        System.out.println("---------------End of test get board---------------");
    }

    public void testGetNewPlayerTiles() throws IOException, ClassNotFoundException {
        List<Tile> list = new ArrayList<>();
        for(int i=0; i<12; i++){
            list = host.getNewPlayerTiles(7);
            if(list.size() != 7){
                System.out.println("Error in getNewPlayerTiles");
                break;
            }
        }
        list = host.getNewPlayerTiles(5);
        if(list.size() != 7)
            System.out.println("Error in getNewPlayerTiles");
        list = host.getNewPlayerTiles(7);
        if(list.size() != 7)
            System.out.println("Error in getNewPlayerTiles");

        System.out.println("---------------End of test get tiles---------------");
    }

    public void testNextTurn() throws IOException, InterruptedException {
        host.nextTurn();
        host.nextTurn();
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        TestHostModel test = new TestHostModel();
        test.init();
        test.testStartGame();
        test.submitWord();
        test.testGetScore();
        test.testGetBoard();
        test.testGetNewPlayerTiles();
        test.testNextTurn();
        test.bsh.close();
        test.host.closeClient();
        test.s.close();
    }
}
