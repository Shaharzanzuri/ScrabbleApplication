package Model;

import Data.Tile;
import ViewModel.ScrabbleViewModel;


import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;


public class GuestModel extends Observable implements ScrabbleModelFacade {

    protected final Socket server;
    private final String playerName;
    private boolean myTurn;
    private boolean gameOver;
    boolean gameStarted;
    private boolean disconnect;
    private final boolean isHostProp = false;
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    ExecutorService executorUpdatesCheck = Executors.newFixedThreadPool(5);
    HashMap<String, Boolean> isUpdateMap = new HashMap<>();//map the concurrent updates of the variables
    HashMap<String, String> answersMap = new HashMap<>();//map between the variable to the actual update string from the hosr


    public GuestModel(String name, String ip, int port) throws IOException {
        this.playerName = name;
        gameStarted = false;
        disconnect = false;
        initMaps();//init the maps concurrent
        try {
            server = new Socket(ip, port);
        } catch (IOException e) {
            throw new IOException("ERROR in connecting to server. Check ip and port!");
        }
        BufferedWriter bw = null;
        bw = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
        BufferedReader br = new BufferedReader(new InputStreamReader(server.getInputStream()));
        bw.write("connect:" + name + "\n");
        bw.flush();
        String st = br.readLine();
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

        this.startListening();

        // Connect to server with name and socket
    }

    private void startListening() {//start listening for answers and updates from the server
        executorService.submit(() -> {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(server.getInputStream()));
                while (!myTurn || !gameOver) {
                    String res = null;
                    res = br.readLine();
                    if (res == null) {
                        continue;
                    }
                    processUpdates(res);
                }
            } catch (IOException e) {
                this.disconnectInvoked();
                throw new RuntimeException(e);
            }
        });

    }


    private synchronized void processUpdates(String inputLine) {
        String[] str = inputLine.split(":");
        String action = str[0];
        String update;
        if (str.length == 1) {
            update = " ";
        } else {
            update = str[1];
        }
        switch (action) {
            case "disconnect":
                proccesDisconect();
                return;
            case "update":
                proccesUpdate();
                break;
            case "game-over":
                proccesGameOver();
                return;
            case "my-turn":
                processPlayTurn();
                break;
            case "game-started":
                processGameStarted();
                break;
            case "answer":
                processAnswer(update);
                break;
            default:
                // Handle unrecognized response
                System.out.println("unrecognized res in processResponse\n");
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

    public void processGameStarted() {
        gameStarted = true;
        this.setChanged();
        this.notifyObservers();
    }

    private void processAnswer(String inputLine) {
        String[] str = inputLine.split("-");
        String action = str[0];
        String update = str[1];
        switch (action) {
            case "board":
                initUpdate("board", update);
                break;
            case "score":
                initUpdate("score", update);
                break;
            case "tiles":
                initUpdate("tiles", update);
                break;
            case "turn":
                initUpdate("turn", update);
                break;
            case "submit":
                initUpdate("submit", update);
                break;
            case "board-legal":
                initUpdate("boardLegal",update);
            default:
                System.out.println("unrecognized updates from host");
                break;
        }

    }


    @Override
    public boolean isHost() {
        return isHostProp;
    }

    @Override
    public boolean startGame() {
        processGameStarted();
        return true;
    }


    public void sendRequest(String request) {//sends requests to the host
        try {
            synchronized (server) {
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
                out.write(request + "\n");
                out.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public boolean submitWord(String word, int row, int col, boolean isVertical) throws IOException, ClassNotFoundException {
        String request = "submit:" + word + "," + row + "," + col + "," + isVertical;
        sendRequest(request);
        AtomicReference<Boolean> answer = new AtomicReference<>(false);
        executorUpdatesCheck.submit(() -> {
            synchronized (answer) {
                while (true) {
                    if (isUpdateMap.get("submit")) {
                        isUpdateMap.put("submit", false);
                        String ans = answersMap.get("submit");
                        if (ans.startsWith("true")) {
                            answer.set(true);
                            break;
                        } else {
                            answer.set(false);
                            break;
                        }
                    }
                }
            }
        });
        return answer.get();
    }

    public boolean boardLegal(Tile[][] boardLegal){
        String request="boardLegal:"+Tile.tilesToString(boardLegal);
        sendRequest(request);
        AtomicReference<String> answer = new AtomicReference<>("");
        Future<String> answerFuter=executorUpdatesCheck.submit(() -> {
            while (true) {
                if (isUpdateMap.get("boardLegal")) {
                    isUpdateMap.put("boardLegal", false);
                    answer.set(answersMap.get("boardLegal"));
                    answersMap.put("boardLegal", "");
                    break;
                }
            }

            return answer.get();
        });
        if(answer.get()=="true"){
            return true;
        }
        return false;

    }

    @Override
    public String getScore() throws IOException {
        String request = "get-score:";
        sendRequest(request);
        AtomicReference<String> answer = new AtomicReference<>("");
        Future<String> answerFuter=executorUpdatesCheck.submit(() -> {
            while (true) {
                if (isUpdateMap.get("score")) {
                    isUpdateMap.put("score", false);
                    answer.set(answersMap.get("score"));
                    answersMap.put("score", "");
                    break;
                }
            }

            return answer.get();
        });

        try {
            return answerFuter.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return this.playerName;
    }

    @Override
    public Tile[][] getBoard() throws IOException, ClassNotFoundException {
        String request = "get-board:\n";
        sendRequest(request);
        AtomicReference<String> answer = new AtomicReference<>("");
        Future<Tile[][]> boardFutre =
                executorUpdatesCheck.submit(() -> {
                    while (true) {
                        if (isUpdateMap.get("board")) {
                            isUpdateMap.put("board", false);
                            answer.set(answersMap.get("board"));
                            break;
                        }
                    }
                    return Tile.tilesFromString(answer.get());
                });
        try {
            Tile[][] boardTiles = boardFutre.get();
            return boardTiles;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }


    }


    @Override
    public List<Tile> getNewPlayerTiles(int amount) throws IOException, ClassNotFoundException {
        String request = "get-new-tiles:" + amount;

        sendRequest(request);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Future<List<Tile>> playersTileFutre = executorUpdatesCheck.submit(() -> {
            String answer = "";
            while (true) {
                if (isUpdateMap.get("tiles")) {
                    isUpdateMap.put("tiles", false);
                    answer = answersMap.get("tiles");
                    break;
                }
            }

            return Tile.stringToTilesList(answer);
        });
        try {
            return playersTileFutre.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void nextTurn() throws IOException, InterruptedException {
        myTurn = false;
        String request = "next-turn:";
        sendRequest(request);
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
        synchronized (server) {
            try {
                server.close();
            } catch (IOException e) {
                this.disconnectInvoked();
                throw new RuntimeException(e);
            }
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

    public boolean getGameOver() {
        return gameOver;
    }

    private void initUpdate(String name, String update) {
        answersMap.put(name, update);
        isUpdateMap.put(name, true);
    }

    private void initMaps() {

        // the updates current
        this.isUpdateMap.put("score", false);
        this.isUpdateMap.put("tiles", false);
        this.isUpdateMap.put("board", false);
        this.isUpdateMap.put("turn", false);
        this.isUpdateMap.put("submit", false);
        this.isUpdateMap.put("boardLegal",false);

        //the answers map
        this.answersMap.put("score", " ");
        this.answersMap.put("tiles", " ");
        this.answersMap.put("board", " ");
        this.answersMap.put("turn", " ");
        this.answersMap.put("submit", " ");
        this.answersMap.put("boardLegal"," ");
    }
}
