package Model;

import Logic.*;
import Data.*;

import java.io.*;
import java.util.*;
import java.net.Socket;

public class HostHandler implements ClientHandler {

    private boolean stop;
    private final HostModel model;


    public HostHandler(HostModel model) {
        this.model = model;
        stop = false;

    }

    String name;

    @Override
    public void handleClient(Socket client) {
        InputStream inFromClient = null;
        try {
            inFromClient = client.getInputStream();
            OutputStream outToClient = client.getOutputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inFromClient));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outToClient));
            String request;
            StringBuilder sb;
            while (!stop) {
                request = br.readLine();
                if (request == null) {
                    continue;
                }
                processClientRequest(client, br, bw, request);


            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void processClientRequest(Socket client, BufferedReader reader, BufferedWriter writer, String inputLine) throws IOException, ClassNotFoundException, InterruptedException {//process a client request
        String[] arr = inputLine.split(":");
        String update;
        if (arr.length == 1) {
            update = " ";
        } else {
            update = arr[1];
        }
        String action = arr[0];

        switch (action) {
            case "connect":
                proccesConnect(client, writer, update);
                break;
            case "name":
                proccesName(writer, update);//set new name
                break;
            case "submit":
                proccesSubmitMove(writer, update);//check the word on dictionary and get the score
                break;
            case "valid-move":
                proccesValidMove(writer, update);//place a tile on the board
                break;
            case "disconnect":
                processDisconectClient(client, writer, update);
                break;
            case "get-board":
                proccesGetBoard(writer, update);
                break;
            case "get-score":
                proccesGetScore(writer, update);
                break;
            case "get-new-tiles":
                proccesNewTiles(client, writer, update);
                break;
            case "next-turn":
                proccesPassTurn(writer, update);
                break;
            default:
                break;
        }

    }


    @Override
    public void close() {
        stop = true;
    }


    private void proccesConnect(Socket client, BufferedWriter bw, String update) throws IOException {
        System.out.println("process connecting guest to client:" + client.getLocalSocketAddress().toString());
        if (model.players.size() == 4) {
            bw.write("game-full" + "\n");
            bw.flush();
            return;
        }
        for (int i = 0; i < model.players.size(); i++) {
            if (update.equals(model.players.get(i).getName())) {
                bw.write("name-not-valid" + "\n");
                bw.flush();
                return;
            }
        }
        model.players.add(new Player(update, client, 0));
        model.update();
        bw.write("Ok" + "\n");
        bw.flush();
        System.out.println(update + "Connected!");

    }

    private void proccesName(BufferedWriter bw, String update) throws IOException {
        if (this.model.ValidName(update)) {
            bw.write("name-valid" + "\n");
            bw.flush();
        } else {
            bw.write("name-not-valid" + "\n");
            bw.flush();
        }
    }


    private void proccesNewTiles(Socket client, BufferedWriter bw, String update) throws IOException, ClassNotFoundException {
        List<Tile> playerTiles = model.getNewPlayerTiles(Integer.parseInt(update));
        String tilesAsString = Tile.ListToString(playerTiles);
        bw.write("answer:tiles-"+tilesAsString + "\n");
        bw.flush();
        System.out.println("tiles been send to client:" + client.getLocalSocketAddress().toString());
    }

    private void proccesPassTurn(BufferedWriter bw, String update) throws IOException, InterruptedException {
        model.nextTurn();
    }

    private void proccesGetScore(BufferedWriter bw, String update) throws IOException {
        bw.write("answer:score-"+model.getScore() + "\n");
        bw.flush();
        System.out.println(model.getScore() + " Score written");

    }

    private void proccesGetBoard(BufferedWriter bw, String update) throws IOException, ClassNotFoundException {
        System.out.println("host handler- process get board");
        Tile[][] tilesBoard = model.getBoard();
        String boardAsString = Tile.tilesToString(tilesBoard);
        synchronized (bw) {
            bw.write("answer:board-"+boardAsString + "\n");
            bw.flush();
        }
    }

    private void processDisconectClient(Socket client, BufferedWriter bw, String namePlayer) throws IOException {
        for (int i = 0; i < this.model.players.size(); i++) {
            if (this.model.players.get(i).getName().equals(model.name))
                continue;
            if (this.model.players.get(i).getName().equals(namePlayer)) {
                if (this.model.players.get(i).getSocket() != null) {
                    this.model.players.get(i).getSocket().close();
                }
                System.out.println(this.model.players.get(i).getName() + " Disconnected Successfully!");
                this.model.players.remove(this.model.players.get(i));
                this.model.update();
            }
        }
    }


    private void proccesValidMove(BufferedWriter bw, String update) throws IOException {

        //need to check it later and add the function
        bw.write("true");
        bw.flush();
    }


    private void proccesSubmitMove(BufferedWriter bw, String update) throws IOException, ClassNotFoundException {
        String[] parts = update.split(",");
        String word = parts[0];
        int row = Integer.parseInt(parts[1]);
        int col = Integer.parseInt(parts[2]);
        boolean vertical = Boolean.parseBoolean(parts[3]);
        String response = Boolean.toString(model.submitWord(word, row, col, vertical));
        bw.write("answer:submit-"+response + "\n");
        bw.flush();
    }

}
