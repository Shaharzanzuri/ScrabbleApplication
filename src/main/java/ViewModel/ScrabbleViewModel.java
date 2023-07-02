package ViewModel;

import Data.Tile;
import Model.ScrabbleModelFacade;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Observer;


public class ScrabbleViewModel implements Observer {

    private ObjectProperty<Tile[][]> boardProperty;// data binding
    private final ListProperty<Tile> tiles; // data binding
    private ListProperty<String> scores; // data binding
    private final ScrabbleModelFacade model;
    public final BooleanProperty myTurn; // data binding
    public final BooleanProperty gameOver; // data binding
    private BooleanProperty gameStarted;
    private Tile[][] prevBoard;
    private final BooleanProperty disconnect;

    private StringProperty name;

    public void startGame() {
        this.gameStarted.set(true);
    }

    public BooleanProperty getDisconnect() {
        return disconnect;
    }

    public boolean isHost() {
        return model.isHost();
    }

    public void disconnect() {
        this.model.disconnect();
    }

    public ListProperty<Tile> getTiles() {
        return tiles;
    }

    public ObjectProperty<Tile[][]> getBoard() {
        return boardProperty;
    }

    public Tile[][] getPrevBoard() {
        try {
            this.prevBoard = this.model.getBoard();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return this.prevBoard;
    }

    public boolean boardlegal(String word, int row, int col, boolean vertical) {
        try {
            return this.model.submitWord(word, row, col, vertical);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public BooleanProperty getGameStartedProperty() {
        return this.gameStarted;
    }

    public BooleanProperty getGameOver() {
        return gameOver;
    }

    public ScrabbleViewModel(ScrabbleModelFacade m) throws IOException {
        model = m;
        m.addObserver(this);
        scores = new SimpleListProperty<>(FXCollections.observableArrayList());
        try {
            tiles = new SimpleListProperty<>(FXCollections.observableArrayList(model.getNewPlayerTiles(7)));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            boardProperty = new SimpleObjectProperty<>(model.getBoard());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        myTurn = new SimpleBooleanProperty(model.isMyTurn());
        gameOver = new SimpleBooleanProperty();
        gameStarted = new SimpleBooleanProperty(model.isGameStarted());
        disconnect = new SimpleBooleanProperty(model.isDisconnected());
        prevBoard = new Tile[15][15];
        try {
            prevBoard = model.getBoard();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        name = new SimpleStringProperty(model.getName());

    }

    @Override
    public void update(java.util.Observable o, Object arg) {
        if (model.isDisconnected()) {
            this.disconnect.set(true);
            return;
        }
        if (!gameStarted.get() && model.isGameStarted()) {
            //Guest
            this.startGame();
            this.gameStarted.set(true);
            return;
        }
        if (model.isGameOver()) {
            System.out.println("GAME OVER");
            gameOver.set(true);
            return;
        }
        if (model.isMyTurn()) {
            myTurn.set(true);
        }
        try {
            name = new SimpleStringProperty(model.getName());
            prevBoard = model.getBoard();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        initBoardProparty();

    }

    public void initTiles() {
        List<Tile> tileList = null;
        try {
            tileList = model.getNewPlayerTiles(7);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        tiles.addAll(tileList);
    }

    public void initBoardProparty() {
        try {
            boardProperty.bindBidirectional(new SimpleObjectProperty<>(model.getBoard()));
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public StringProperty getName() {
        return name;
    }

    public void skipTurn() throws IOException, InterruptedException {
        model.nextTurn();

    }

    public ListProperty<String> getScores() {
        try {
            //Thread.sleep(1500);
            String score = model.getScore();
            System.out.println(score);
            String[] scoreSplit = score.split(",");
            String[] newScore = new String[scoreSplit.length];
            int i = 0;
            for (String s : scoreSplit) {
                String[] str = s.split(";");
                s = str[0] + ":" + str[1];
                newScore[i] = s;
                i++;
            }
            scores.clear();
            scores.addAll(Arrays.asList(newScore));
            return scores;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean submitWord(String word, int row, int col, boolean vertical) {
        try {
            return this.model.submitWord(word, row, col, vertical);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
