package ViewModel;


import Data.Tile;
import Logic.BookScrabbleHandler;
import Logic.MyServer;
import Model.GuestModel;
import Model.HostModel;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import view.GameViewController;

import java.io.IOException;
import java.util.List;

public class TestViewModel {

    ScrabbleViewModel hostView;
    ScrabbleViewModel guestView;
    HostModel host;
    GuestModel guest;
    MyServer server;

    public TestViewModel() {
        server = new MyServer(8887, new BookScrabbleHandler());
        server.start();
    }

    public void closeTest() {
        try {
            server.close();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void initTest() {
        host = new HostModel("shahar");
        try {
            hostView = new ScrabbleViewModel(host);
        } catch (IOException e) {
            System.out.println("couldn't make a view host ");
            throw new RuntimeException(e);
        }
        try {
            guest = new GuestModel("shush", "localhost", 5556);
            guestView = new ScrabbleViewModel(guest);
        } catch (IOException e) {
            System.out.println("problem make guest-model");
            throw new RuntimeException(e);
        }

    }


    public void StartGameTest() throws InterruptedException {
        if (hostView.isHost())
            hostView.startGame();
        Thread.sleep(1000);
        if (!hostView.getGameStartedProperty().get()) {
            System.out.println("problem binding the start game");
        }
        if (guestView.getGameStartedProperty().get()) {
            System.out.println("the guest didn't get the update of game start");
        }

    }

    public void getTilesTest() {
        ListProperty<Tile> tilesHost = hostView.getTiles();
        if (tilesHost.size() != 7) {
            System.out.println("test get tiles host failed");
        }
        ListProperty<Tile> tilesGuest = guestView.getTiles();
        if (tilesGuest.size() != 7) {
            System.out.println("test get tiles guest failed");
        }
    }

    public void getBoardTest() {
        ObjectProperty<Tile[][]> boardHost;
        ObjectProperty<Tile[][]> boardGuest;
        hostView.initBoardProparty();
        guestView.initBoardProparty();
        boardHost = hostView.getBoard();
        boardGuest = guestView.getBoard();
        String stringBoardHost = Tile.tilesToString(boardGuest.get());
        String stringBoardGuest = Tile.tilesToString(boardHost.get());
        if (!stringBoardGuest.equals(stringBoardHost)) {
            System.out.println("boards test wrong");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        TestViewModel testViewModel = new TestViewModel();
        testViewModel.initTest();
        testViewModel.StartGameTest();
        testViewModel.getTilesTest();
        testViewModel.getBoardTest();
        testViewModel.hostView.disconnect();
        testViewModel.guestView.disconnect();
        System.out.println("done");
        testViewModel.closeTest();
    }
}
