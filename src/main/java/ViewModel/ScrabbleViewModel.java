package ViewModel;

import Data.Tile;
import Model.ScrabbleModelFacade;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;

import java.io.IOException;
import java.util.Arrays;
import java.util.Observer;
import java.util.PropertyPermission;

public class ScrabbleViewModel implements Observer {

    private ObjectProperty<Tile[][]> boardProperty;// data binding
    private final ListProperty<Tile> tiles; // data binding
    private final ListProperty<String> scores; // data binding
    private final ScrabbleModelFacade model;
    public final BooleanProperty myTurn; // data binding
    public final BooleanProperty gameOver; // data binding
    private final BooleanProperty gameStarted;
    private Tile[][] prevBoard;
    private final BooleanProperty disconnect;

    public void startGame() {
        try {
            gameStarted.set(true);
            if (tiles.size() == 7)
                return;
            tiles.setAll(model.startGame());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public BooleanProperty getDisconnect() {
        return disconnect;
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

    public Tile[][] getPrevBoard(){
        try {
            this.prevBoard = this.model.getBoard();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return this.prevBoard;
    }


    public BooleanProperty getGameStartedProperty() {
        return this.gameStarted;
    }

    public BooleanProperty getGameOver() {
        return gameOver;
    }

    public ScrabbleViewModel(ScrabbleModelFacade m) {
        model = m;
        m.addObserver(this);
        scores = new SimpleListProperty<>(FXCollections.observableArrayList());
        try {
            tiles = new SimpleListProperty<>(FXCollections.observableArrayList(model.getNewPlayerTiles(7)));
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        boardProperty = new SimpleObjectProperty<>();
        myTurn = new SimpleBooleanProperty();
        gameOver = new SimpleBooleanProperty();
        gameStarted = new SimpleBooleanProperty(false);
        disconnect = new SimpleBooleanProperty(false);
        prevBoard = new Tile[15][15];

    }

    @Override
    public void update(java.util.Observable o, Object arg) {
        // Before startGame WaitForPlayers -> player connected -> update ScoreList in host
        // Guest -> startGame
        if (model.isDisconnected()) {
            this.disconnect.set(true);
            return;
        }
        if (!gameStarted.get() && !model.isGameStarted() && !model.isGameOver()) {
            //Host
            Platform.runLater(this::getScores);
            return;
        }
        if (!gameStarted.get() && model.isGameStarted()) {
            //Guest
            Platform.runLater(() -> {
                try {
                    boardProperty.set(model.getBoard());
                    prevBoard = boardProperty.get();
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                this.getScores();
            });
            this.startGame();
            return;
        }
        if (model.isGameOver()) {
            System.out.println("GAME OVER");
            gameOver.set(true);
            return;
        }
        Platform.runLater(() -> {
            try {
                prevBoard = boardProperty.get();
                boardProperty.set(model.getBoard());
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            this.getScores();
        });
        if (model.isMyTurn()) {
            myTurn.set(true);
        }

    }

    public void skipTurn() throws IOException, InterruptedException {
        model.nextTurn();

    }

    public ListProperty<String> getScores() {
        try {
            //Thread.sleep(1500);
            String score = model.getScore();
            System.out.println(score);
            String[] scoreSplit = score.split(";");
            scores.clear();
            scores.addAll(Arrays.asList(scoreSplit));
            return scores;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
