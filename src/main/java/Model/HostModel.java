
package Model;

import Data.Board;
import Data.Player;
import Data.Tile;
import Data.Word;
import Logic.DictionaryCommunication;
import Logic.MyServer;
import ViewModel.ScrabbleViewModel;

import java.io.*;
import java.util.*;

public class HostModel extends Observable implements ScrabbleModelFacade {


    private MyServer guestServer;
    private HostHandler hostHandler;

    protected Board board;
    public List<Player> players;
    private String score;
    protected String name;
    private int round;
    private boolean bagIsEmpty;
    private boolean gameOver;
    private int turnCounter;
    private int numberOfPasses;
    protected boolean myTurn;
    private boolean disconnect;
    public boolean gameStarted;
    private final boolean isHostProp = true;


    private static final int MAX_GUESTS = 3;


    // constructor \\
    public HostModel(String name) {
        this.name = name;
        disconnect = false;
        bagIsEmpty = false;
        gameStarted = false;
        gameOver = false;
        hostHandler = new HostHandler(this);
        guestServer = new MyServer(5556, hostHandler);
        guestServer.start();
        players = new ArrayList<>();
        players.add(new Player(name, null, 0));
        board = Board.getBoard();
        turnCounter = 0;
        numberOfPasses = 0;
        round = 0;
    }


    @Override
    public boolean submitWord(String word, int row, int col, boolean isVertical) throws IOException, ClassNotFoundException {
        Word word1 = Word.stringToWord(word, row, col, isVertical);
        int score = board.tryPlaceWord(word1);
        if (score == -1) {
            return false;
        }
        // success - word has been placed on board
        Player p = this.players.get(this.turnCounter);
        p.addScore(score);
        // remove tiles from player somehow
        this.notifyAllPlayers();
        this.setChanged();
        this.notifyObservers();
        numberOfPasses = -1;
        return true;
    }

    private void notifyAllPlayers() throws IOException {

        for (Player player : this.players) {
            // SubmitWord will return true then viewModel should call getBoard, getScore and nextTurn
            if (this.players.get(this.turnCounter).getName().equals(player.getName()))
                continue;
            if (player.getName().equals(this.name)) {
                this.setChanged();
                this.notifyObservers();
                continue;
            }
            if (this.players.get(this.turnCounter) == player)
                continue;
            this.sendMessageToGuest(player, "update:");
        }
    }

    public void initScore() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.players.size(); i++) {
            if (i == this.players.size() - 1) {
                sb.append(this.players.get(i).getName()).append(";").append(this.players.get(i).getScore());
                break;
            }
            sb.append(this.players.get(i).getName()).append(";").append(this.players.get(i).getScore()).append(",");
        }
        this.score = sb.toString();
    }

    @Override
    public String getScore() throws IOException {
        initScore();
        return this.score;
    }

    @Override
    public Tile[][] getBoard() throws IOException, ClassNotFoundException {
        return board.getTiles();
    }


    @Override
    public String getName() {
        return this.name;
    }

    @Override
    // gets the List of Tiles for player \\
    public List<Tile> getNewPlayerTiles(int amount) throws IOException, ClassNotFoundException {
        if (bagIsEmpty)
            return null;
        List<Tile> list = new ArrayList<>(7 - amount);
        list = board.setTilesPlayer(list);
        return list;
    }

    @Override
    public boolean isHost() {
        return isHostProp;
    }

    @Override
    // passing the turn to the next player \\
    public void nextTurn() throws IOException, InterruptedException {
        numberOfPasses++;
        this.turnCounter++;
        if (this.turnCounter >= this.players.size()) {
            this.turnCounter = 0;
            this.round++;
        }
        if (round == 10 || numberOfPasses == this.players.size()) {//finish game
            this.endGame();
            return;
        }
        if (this.players.get(this.turnCounter).getName().equals(this.name)) {
            myTurn = true;
            this.setChanged();
            this.notifyObservers();
            return;
        }
        myTurn = false;
        this.sendMessageToGuest(this.players.get(this.turnCounter), "my-turn");
    }

    @Override
    public boolean startGame() throws IOException, ClassNotFoundException {
        board = Board.getBoard();
        Collections.shuffle(players);
        gameStarted = true;
        this.setChanged();
        this.notifyObservers();
        for (int i = 0; i < this.players.size(); i++) {
            if (players.get(i).getName().equals(this.name))
                continue;
            this.sendMessageToGuest(players.get(i), "game-started");
        }
        //Thread sleep
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (players.get(0).getName().equals(this.name)) { // if it's host turn
            myTurn = true;
            this.setChanged();
            this.notifyObservers();
        }
        this.sendMessageToGuest(players.get(0), "my-turn"); // if it's a guest turn
        this.setChanged();
        this.notifyObservers();
        return true;
    }

    //sends massage to guest \\
    private void sendMessageToGuest(Player player, String message) throws IOException {

        if (player.getSocket() != null) {
            synchronized (player.getSocket()) {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(player.getSocket().getOutputStream()));
                message = message + "\n";
                bw.write(message);
                bw.flush();
            }
        } else {
            System.out.println("the massage didnt send to player:" + player.getName());
        }
    }


    @Override
    public boolean isGameStarted() {
        return gameStarted;
    }


    @Override
    public boolean isMyTurn() {
        return myTurn;
    }

    @Override
    public boolean isGameOver() {
        return gameOver;
    }


    @Override
    public void addObserver(ScrabbleViewModel vm) {
        super.addObserver(vm);
    }

    @Override
    public void endGame() {
        for (int i = 0; i < this.players.size(); i++) {
            if (this.players.get(i).getName().equals(this.name)) {
                gameOver = true;
                this.setChanged();
                this.notifyObservers();
                continue;
            }
            try {
                this.sendMessageToGuest(this.players.get(i), "game-over");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        //finishGame
        this.closeClient(); // need to test

    }

    public void closeClient() {
        DictionaryCommunication dc = DictionaryCommunication.getInstance();
        dc.close();
        hostHandler.close();
        try {
            guestServer.close();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    public void disconnect() {
        disconnect = true;
        this.setChanged();
        this.notifyObservers();
        for (int i = 0; i < players.size(); i++) {
            try {
                this.sendMessageToGuest(this.players.get(i), "disconnect");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        //finishGame
        this.closeClient(); // need to test
    }

    @Override
    public boolean isDisconnected() {
        return disconnect;
    }


    public void update() {
        initScore();
        this.setChanged();
        this.notifyObservers();
    }


    public boolean ValidName(String name) { //checks valid name set
        for (Player player : players) {
            if (Objects.equals(player.getName(), name)) {
                return false;
            }
        }
        return true;
    }


    public static String mapToString(HashMap<String, Integer> hashMap) {// transform the Hashmap score to string
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
            stringBuilder.append(entry.getKey()).append(":").append(entry.getValue()).append(",");
        }
        String hashMapAsString = stringBuilder.toString();
        hashMapAsString = hashMapAsString.substring(0, hashMapAsString.length() - 1); // Remove trailing comma
        return hashMapAsString;
    }


}
