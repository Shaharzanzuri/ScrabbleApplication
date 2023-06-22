package Model;

import Data.Tile;
import ViewModel.ScrabbleViewModel;


import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Observable;


public class GuestModel extends Observable implements ScrabbleModelFacade {


    protected Socket server;
    private final String playerName;
    private boolean myTurn;
    private boolean gameOver;
    boolean gameStarted;
    private boolean disconnect;
    protected BufferedReader bufferedReader = null;
    protected BufferedWriter bufferedWriter = null;

    public GuestModel(String name, String ip, int port) throws IOException {
        this.playerName = name;
        gameStarted = false;
        disconnect = false;
        try {
            server = new Socket(ip, port);
        } catch (IOException e) {
            throw new IOException("ERROR in connecting to server. Check ip and port!");
        }
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
        bufferedReader = new BufferedReader(new InputStreamReader(server.getInputStream()));
        bufferedWriter.write("connect:" + name + "\n");
        bufferedWriter.flush();
        String st = bufferedReader.readLine();
        if (st.equals("game-full")) {
            server.close();
            throw new IOException("Game is Full!");
        }
        if (st.equals("name-not-valid")) {
            server.close();
            throw new IOException("Name is already chosen! Please enter a different name");
        }
        System.out.println(name + " Connected!");
        myTurn = false;
        gameOver = false;
        new Thread(this::waitForGameStart).start();
        // Connect to server with name and socket
    }


    private void waitForGameStart() {//Need to complete
        try {
            String res = bufferedReader.readLine();// Wait for game to start
            if (res == null || !res.equals("game-started")) {
                server.close();
                this.disconnectInvoked();
                return;
            }
            gameStarted = true;
            this.setChanged();
            this.notifyObservers();
            Thread.sleep(2000);
            this.waitForTurn();
        } catch (IOException | InterruptedException e) {
            System.out.println("Guest Disconnected Successfully!");
            throw new RuntimeException("Socket Closed!");
        }

    }


    private void waitForTurn() {
        System.out.println(this.playerName + " waiting for turn");
        try {
            StringBuilder sb = new StringBuilder();
            int character;
            while ((character = bufferedReader.read()) != -1) {
                if (character == '\n') {
                    String res = sb.toString();
                    sb.setLength(0); // Clear the StringBuilder for the next line
                    processResponse(res);
                } else {
                    sb.append((char) character);
                }
            }
            // Handle end of stream (server closed connection)
            this.disconnectInvoked();
            throw new RuntimeException("Server closed the connection");
        } catch (IOException e) {
            this.disconnectInvoked();
            throw new RuntimeException(e);
        }
    }

    private void processResponse(String response) {
        switch (response) {
            case "disconnect":
                proccesDisconect();
                break;
            case "update":
                proccesUpdate();
                break;
            case "game-over":
                proccesGameOver();
                break;
            case "my-turn":
                processPlayTurn();
                break;
            default:
                // Handle unrecognized response
                break;
        }
    }

    private void proccesDisconect() {
        try {
            this.server.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.disconnect = true;
        this.setChanged();
        this.notifyObservers();
    }

    private void proccesUpdate() {
        this.setChanged();
        this.notifyObservers();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void proccesGameOver() {
        gameOver = true;
        this.setChanged();
        this.notifyObservers();
        try {
            this.server.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processPlayTurn() {
        myTurn = true;
        System.out.println(this.playerName + " turn");
        this.setChanged();
        this.notifyObservers();
    }


    @Override
    public boolean submitWord(String word, int row, int col, boolean isVertical) throws IOException, ClassNotFoundException {
        // user
        try {
            bufferedWriter.write("submit:" + word + "," + row + "," + col + "," + isVertical + "\n");
            bufferedWriter.flush();
            String response;
            response = bufferedReader.readLine();
            return response.startsWith("true");
        } catch (IOException e) {
            this.disconnectInvoked();
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getScore() throws IOException {
        BufferedReader in = writeToHost("get-score: \n");
        String line = in.readLine();
        return line;
    }

    @Override
    public Tile[][] getBoard() throws IOException, ClassNotFoundException {
        BufferedReader in = writeToHost("get-board: \n");;
        return Tile.tilesFromString(in.readLine());

    }

    @Override
    public List<Tile> getNewPlayerTiles(int amount) throws IOException, ClassNotFoundException {
        BufferedReader in = writeToHost("get-new-tiles: \n");
        return Tile.stringToTilesList(in.readLine());
    }

    @Override
    public void nextTurn() throws IOException, InterruptedException {
        myTurn = false;
        writeToHost("next-turn");
        Thread t = new Thread(this::waitForTurn);
        t.start();
        this.setChanged();
        this.notifyObservers();
    }

    @Override
    public List<Tile> startGame() throws IOException, ClassNotFoundException {
        return getNewPlayerTiles(7);
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
        //need to complete

    }

    @Override
    public boolean isGameStarted() {
        return gameStarted;
    }

    public void disconnect() {
        try {
            server.close();
        } catch (IOException e) {
            this.disconnectInvoked();
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isDisconnected() {
        return disconnect;
    }


    public void disconnectInvoked() {
        this.disconnect = true;
        this.setChanged();
        this.notifyObservers();
    }


    private BufferedReader writeToHost(String data) {
        try {

            bufferedWriter.write(data);
            bufferedWriter.flush();
            Thread.sleep(1000);
            return bufferedReader;
        } catch (IOException | InterruptedException e) {
            this.disconnectInvoked();
            throw new RuntimeException(e);
        }
    }


    private boolean changeName(String name) throws IOException {
        BufferedReader in = writeToHost("name:" + name + "\n");
        if (in.readLine().startsWith("true")) {
            return true;
        } else return false;
    }

    private void makeMove(String move) {
        String moveGuest = "valid-move:" + move + "\n";
        writeToHost(moveGuest);
    }


    public boolean getGameOver() {
        return gameOver;
    }
}
