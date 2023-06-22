package view;

import Data.Tile;
import ViewModel.ScrabbleViewModel;
import javafx.application.Platform;

import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;

import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class GameViewController {
    ScrabbleViewModel vm;
    boolean host = false;

    // Player variables
    private StringProperty playerName;
    private StringProperty turn;
    private ListProperty<TileView> tiles;
    private BooleanProperty disconnect;

    private Tile selectedTile;
    private Label draggedTile; // Reference to the dragged tile label

    //game variable
    ObjectProperty<String> scoreTable;
    ObjectProperty<TileView[][]> bindingBoard;


    Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    //THE INITIALIZE COLORS VALUES
    private static final Color defoultTileBardColor = Color.WHITE;
    private static final Color defoultTileBagColor = Color.SANDYBROWN;
    private static final Color middleStarColor = Color.GOLD;
    private static final Color tripleWordScoreColor = Color.RED;
    private static final Color doubleWordScoreColor = Color.LIGHTYELLOW;
    private static final Color tripleLetterScoreColor = Color.BLUE;
    private static final Color doubleLetterScoreColor = Color.LIGHTBLUE;

    private BooleanProperty myTurn;

    private BooleanProperty gameOver;

    // UI components

    @FXML
    public SplitPane mainContainer;

    @FXML
    private ListView<TileView> tilesTableView;

    @FXML
    private ListView<String> scoreTableView;

    @FXML
    private GridPane board;

    @FXML
    private Button submitButton;

    public GameViewController() {
    }

    @FXML
    private void exitGame() {
        // Implement the exitGame functionality
        System.exit(0);
    }


    @FXML
    private void homePage() {
        // Implement the homePage functionality
        // Code to navigate to the previous page
    }

    public void setViewModel(ScrabbleViewModel vm) {
        this.vm = vm;
        disconnect = new SimpleBooleanProperty(false);
        disconnect.bindBidirectional(this.vm.getDisconnect());
    }


    private void initBinding() {
        scoreTableView.itemsProperty().bind(vm.getScores());
        tiles = new SimpleListProperty<>(FXCollections.observableArrayList());
        tiles.bindBidirectional(TileView.tileListToTileViewList(vm.getTiles()).itemsProperty());
        bindingBoard = new SimpleObjectProperty<>();
        bindingBoard.bindBidirectional(fromTilesBoardToTilesViewBoard(vm.getBoard().get()));
        myTurn = new SimpleBooleanProperty();
        myTurn.bindBidirectional(vm.myTurn);
        gameOver = new SimpleBooleanProperty();
        gameOver.bind(vm.getGameOver());
        disconnect = new SimpleBooleanProperty();
        disconnect.bindBidirectional(vm.getDisconnect());
    }

    private void initBoard() {
        bindingBoard.set(fromTilesBoardToTilesViewBoard(vm.getBoard().get()).get());
    }

    private void initPlayersTiles() {
        for (int i = 0; i < 7; i++) {
            TileView tile;
            if (!tiles.isEmpty()) {
                tile = new TileView(tiles.get(i).letter, tiles.get(i).score, defoultTileBagColor);
            } else {
                tile = new TileView("A", 0, defoultTileBagColor);
            }
            TileView c = tile;
            c.draggable = true;
            tilesTableView.getItems().add(c);
        }
    }

    private void initButtons() {
        playerName = new SimpleStringProperty();

        // Create the tabs (Exit, Minimize, Back)
        Button exitTab = new Button("X");
        Button minimizeTab = new Button("-");
        Button backTab = new Button("<");

        // Configure the tabs
        exitTab.setOnAction(e -> exitGame());
        backTab.setOnAction(e -> homePage());

        // Add the tabs to the tabsBox
    }

    private void addListeners() {
        disconnect.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(() -> stage.close());
            }
        });
        // Add a ChangeListener to the bindingBoard property
        bindingBoard.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Clear the board and re-populate it with the new values
                board.getChildren().clear();
                bindingBoard.set(fromTilesBoardToTilesViewBoard(vm.getBoard().get()).get());

            }
        });
    }


    public void skipTurn() throws IOException, InterruptedException {
        System.out.println("Skip Turn Clicked!");
        if (this.vm.myTurn.get()) {
            vm.skipTurn();
            this.myTurn.set(false);
        }
    }

    // an update of the tiles player bag\\
    private void updateTilesTableView() {
        tiles.setAll(TileView.tileListToTileViewList(vm.getTiles().get()).getItems());
    }

    // an update of the score players \\
    private void updateScoreTableView() {
        scoreTable = null;
        scoreTableView.setItems(vm.getScores().get());
    }

    //helper method
    private ObjectProperty<TileView[][]> fromTilesBoardToTilesViewBoard(Tile[][] tilesBoard) {
        Color[][] tilesColorBoard;
        tilesBoard = vm.getPrevBoard();
        tilesColorBoard = getBoardColor();
        ObjectProperty<TileView[][]> boardView = new SimpleObjectProperty<>();
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                TileView tileView = new TileView(String.valueOf(tilesBoard[i][j].letter), tilesBoard[i][j].score, tilesColorBoard[i][j]);
//                boardView.get()[i][j] = tileView;
                board.add(tileView, i, j);
            }
        }
        return boardView;
    }


    @FXML
    private void submitButtonClicked(MouseEvent event) {
        System.out.println("submit button game clicked!");
        // Implement the submitButton event handler based on your requirements
    }

    public void setHost(boolean isHost) {
        this.host = isHost;
    }

    public void initWindow() {
        System.out.println("initWindow");
        initBinding();
        initBoard();
        initPlayersTiles();
        initButtons();
        addListeners();
    }


    public static class TileView extends Label{

        private String letter;
        private int score;
        private ObjectProperty<Color> color;
        private boolean draggable;

        public TileView(String letter, int score, Color color) {
            this.letter = letter;
            this.score = score;
            this.color = new SimpleObjectProperty<>(color);
            draggable = false;

            setPrefSize(40, 40);
            setText(letter);
            updateColor();
        }

        // Getters and setters for the properties

        public String getLetter() {
            return letter;
        }

        public void setLetter(String letter) {
            this.letter = letter;
            setText(letter);
        }


        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public void setColor(Color color) {
            this.color.set(color);
            updateColor();
        }

        public ObjectProperty<Color> colorProperty() {
            return color;
        }

        private void updateColor() {
            setBackground(new Background(new BackgroundFill(color.get(), CornerRadii.EMPTY, Insets.EMPTY)));
        }

        public static TileView TileToTileView(Tile tile) {
            TileView tileView = new TileView(String.valueOf(tile.letter), tile.score, defoultTileBagColor);
            return tileView;

        }

        public static ListView<TileView> tileListToTileViewList(List<Tile> tiles) {
            ListView<TileView> table = new ListView<>();
            for (
                    Tile tile : tiles) {
                table.getItems().add(new TileView(String.valueOf(tile.letter), tile.score, defoultTileBagColor));

            }
            return table;
        }

    }

    //function to make a color array
    private static Color[][] getBoardColor() {

        Color[][] colors = new Color[15][15];

        // Init color matrix
        for (int i = 0; i < 15; i++)
            for (int j = 0; j < 15; j++)
                colors[i][j] = Color.ANTIQUEWHITE;


        colors[7][7] = Color.BLACK;

        colors[0][0] = tripleWordScoreColor;
        colors[7][0] = tripleWordScoreColor;
        colors[14][0] = tripleWordScoreColor;
        colors[0][7] = tripleWordScoreColor;
        colors[14][7] = tripleWordScoreColor;
        colors[0][14] = tripleWordScoreColor;
        colors[7][14] = tripleWordScoreColor;
        colors[14][14] = tripleWordScoreColor;

        colors[1][1] = doubleWordScoreColor;
        colors[2][2] = doubleWordScoreColor;
        colors[3][3] = doubleWordScoreColor;
        colors[4][4] = doubleWordScoreColor;
        colors[13][1] = doubleWordScoreColor;
        colors[12][2] = doubleWordScoreColor;
        colors[11][3] = doubleWordScoreColor;
        colors[10][4] = doubleWordScoreColor;
        colors[1][13] = doubleWordScoreColor;
        colors[2][12] = doubleWordScoreColor;
        colors[3][11] = doubleWordScoreColor;
        colors[4][10] = doubleWordScoreColor;
        colors[10][10] = doubleWordScoreColor;
        colors[11][11] = doubleWordScoreColor;
        colors[12][12] = doubleWordScoreColor;
        colors[13][13] = doubleWordScoreColor;


        colors[3][0] = doubleLetterScoreColor;
        colors[11][0] = doubleLetterScoreColor;
        colors[6][2] = doubleLetterScoreColor;
        colors[8][2] = doubleLetterScoreColor;
        colors[0][3] = doubleLetterScoreColor;
        colors[7][3] = doubleLetterScoreColor;
        colors[14][3] = doubleLetterScoreColor;
        colors[2][6] = doubleLetterScoreColor;
        colors[6][6] = doubleLetterScoreColor;
        colors[8][6] = doubleLetterScoreColor;
        colors[12][6] = doubleLetterScoreColor;
        colors[3][7] = doubleLetterScoreColor;
        colors[11][7] = doubleLetterScoreColor;
        colors[2][8] = doubleLetterScoreColor;
        colors[6][8] = doubleLetterScoreColor;
        colors[8][8] = doubleLetterScoreColor;
        colors[12][8] = doubleLetterScoreColor;
        colors[0][11] = doubleLetterScoreColor;
        colors[7][11] = doubleLetterScoreColor;
        colors[14][11] = doubleLetterScoreColor;
        colors[6][12] = doubleLetterScoreColor;
        colors[8][12] = doubleLetterScoreColor;
        colors[3][14] = doubleLetterScoreColor;
        colors[11][14] = doubleLetterScoreColor;


        colors[5][1] = tripleLetterScoreColor;
        colors[9][1] = tripleLetterScoreColor;
        colors[1][5] = tripleLetterScoreColor;
        colors[5][5] = tripleLetterScoreColor;
        colors[9][5] = tripleLetterScoreColor;
        colors[13][5] = tripleLetterScoreColor;
        colors[1][9] = tripleLetterScoreColor;
        colors[5][9] = tripleLetterScoreColor;
        colors[9][9] = tripleLetterScoreColor;
        colors[13][9] = tripleLetterScoreColor;
        colors[5][13] = tripleLetterScoreColor;
        colors[9][13] = tripleLetterScoreColor;

        return colors;

    }
}
