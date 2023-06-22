package Model;

import Logic.*;
import Data.*;

import java.io.*;
import java.util.*;
import java.net.Socket;

public class HostHandler implements ClientHandler {

    private boolean stop;
    private HostModel model;


    public HostHandler(HostModel model) {
        this.model = model;
        stop = false;
    }

    Socket guestSocket;

    String name;

    @Override
    public void handleClient(Socket client) {
        InputStream inFromClient = null;
        try {
            inFromClient = client.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inFromClient));
            String request;
            while (!stop) {
                request = br.readLine();
                if (request == null) {
                    continue;
                }
                processClientRequest(client, request);


            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void processClientRequest(Socket client, String inputLine) throws
            IOException, ClassNotFoundException, InterruptedException {//process a client request
        InputStream inFromClient = client.getInputStream();
        OutputStream outToClient = client.getOutputStream();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outToClient));
        String[] arr = inputLine.split(":");
        String action = arr[0];
        String update = arr[1];

        switch (action) {
            case "connect" -> proccesConnect(client, bw, update);
            case "name" -> proccesName(bw, update);//set new name
            case "submit" -> proccesSubmitMove(bw, update);//check the word on dictionary and get the score
            case "valid-move" -> proccesValidMove(bw, update);//place a tile on the board
            case "disconnect" -> processDisconectClient(bw, update);
            case "get-board" -> proccesGetBoard(bw, update);
            case "get-score" -> proccesGetScore(bw, update);
            case "get-new-tiles" -> proccesNewTiles(bw, update);
            case "next-turn" -> proccesPassTurn(bw, update);
        }

    }


    @Override
    public void close() {
        stop = true;
    }


    private void proccesConnect(Socket client, BufferedWriter bw, String update) throws IOException {
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
        //model.setChanged();
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


    private void proccesNewTiles(BufferedWriter bw, String update) throws IOException, ClassNotFoundException {
        List<Tile> playerTiles = model.getNewPlayerTiles(Integer.parseInt(update));
        String tilesAsString = Tile.ListToString(playerTiles);
        bw.write(tilesAsString + "\n");
        bw.flush();
    }

    private void proccesPassTurn(BufferedWriter bw, String update) throws IOException, InterruptedException {
        model.nextTurn();
    }

    private void proccesGetScore(BufferedWriter bw, String update) throws IOException {
        bw.write(model.getScore() + "\n");
        bw.flush();
        System.out.println(model.getScore() + " Score written");

    }

    private void proccesGetBoard(BufferedWriter bw, String update) throws IOException, ClassNotFoundException {
        Tile[][] tilesBoard = model.getBoard();
        String boardAsString = Tile.tilesToString(tilesBoard);
        bw.write(boardAsString + "\n");
        bw.flush();
    }

    private void processDisconectClient(BufferedWriter bw, String namePlayer) throws IOException {
        for (int i = 0; i < this.model.players.size(); i++) {
            if (this.model.players.get(i).getName().equals(model.name))
                continue;
            if (this.model.players.get(i).getName().equals(namePlayer)) {
                if (!this.model.players.get(i).socket.isClosed()) {
                    this.model.players.get(i).socket.close();
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
        bw.write(response + "\n");
        bw.flush();
    }

}
