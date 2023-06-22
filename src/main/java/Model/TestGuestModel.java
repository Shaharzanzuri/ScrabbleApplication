package Model;

import Data.Tile;
import Logic.BookScrabbleHandler;
import Logic.MyServer;

import java.io.IOException;
import java.util.Arrays;

public class TestGuestModel {
    HostModel host;
    GuestModel g1;

    static MyServer s;

    static BookScrabbleHandler bsh;

    public void init() throws InterruptedException, IOException, ClassNotFoundException {
        bsh = new BookScrabbleHandler();
        s = new MyServer(8887, bsh);
        s.start();
        Thread.sleep(1000);
        host = new HostModel("Host");
        host.startGame();
        // maybe guest needs a different ip ?
        g1 = new GuestModel("John","localhost", 5556);
        System.out.println("init successful");
    }


    public void testSubmitWord() throws IOException, InterruptedException, ClassNotFoundException {
        if(!g1.isMyTurn()) {
            host.nextTurn();
            Thread.sleep(2000);
        }
        if(!g1.isMyTurn()){
            System.out.println("Error in nextTurn");
        }
        if(!g1.submitWord("HORN",7,5, false))
            System.out.println("Error in submit word #1");
        if(host.submitWord("SDF",7,7, false))
            System.out.println("Error in submit word #2");
        if(!host.submitWord("FA_M",5,7, true))
            System.out.println("Error in submit word #3");
        if(!g1.submitWord("PASTE",9,5,false))
            System.out.println("Error in submit word #4");
        if(!host.submitWord("_OB",8,7, false))
            System.out.println("Error in submit word #5");
        if(!g1.submitWord("BIT",10,4, false))
            System.out.println("Error in submit word #6");

        System.out.println("---------------End of test submit word---------------");
    }

    public void testGetScore() throws IOException {
        if(!g1.getScore().equals("Host:0;John:88"))
            System.out.println("Error in test get score");
        System.out.println("---------------End of test get score---------------");
    }

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
        System.out.println(g1.getNewPlayerTiles(7).toString());
        System.out.println("---------------End of test get tiles---------------");
    }

    public void testNextTurn() throws IOException, InterruptedException {
        int countOfThreads = Thread.activeCount();
        if(g1.isMyTurn()){
            System.out.println("g1 turn");
            g1.nextTurn();
            Thread.sleep(1000);
            if(countOfThreads+1!=Thread.activeCount()){
                System.out.println("wait for turn - error");
            }
        }
        if(g1.isMyTurn())
            System.out.println("Error in test next turn");
        host.nextTurn();
        Thread.sleep(1000);
        if(countOfThreads!=Thread.activeCount()){
            System.out.println("wait for turn - error");
        }
        g1.nextTurn();
        Thread.sleep(3000);
        if(!g1.getGameOver())
            System.out.println("Error in game over");
        System.out.println("---------------End of test next turn---------------");
    }

    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        TestGuestModel test = new TestGuestModel();
        test.init();
        test.host.startGame();
        test.testGetScore();
        test.testGetNewPlayerTiles();
        test.testGetBoard();
        test.testSubmitWord();
        test.testNextTurn();
        test.host.closeClient();
        bsh.close();
        s.close();

    }

}
