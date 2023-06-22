package Model;

import java.io.IOException;
import java.util.List;
import ViewModel.*;

import Data.*;

public interface ScrabbleModelFacade  {
    boolean submitWord(String word, int row, int col, boolean isVertical) throws IOException, ClassNotFoundException;
    String getScore() throws IOException;
    Tile[][] getBoard() throws IOException, ClassNotFoundException;
    List<Tile> getNewPlayerTiles(int amount) throws IOException, ClassNotFoundException;
    void nextTurn() throws IOException, InterruptedException;
    List<Tile> startGame() throws IOException, ClassNotFoundException;
    boolean isMyTurn();
    boolean isGameOver();
    void addObserver(ScrabbleViewModel vm);
    void endGame();
    boolean isGameStarted();
    void disconnect();
    boolean isDisconnected();
}
