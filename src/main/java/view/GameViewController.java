package view;

import Data.Tile;
import ViewModel.ScrabbleViewModel;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;


import javafx.beans.property.*;
import javafx.collections.FXCollections;

import javafx.fxml.FXML;
import javafx.geometry.Insets;

import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.GridView;

import java.io.IOException;
import java.util.List;

public class GameViewController {


    ScrabbleViewModel vm = null;
    boolean host = false;
    Stage stage;


    // UI components\\

    //------------

    @FXML
    public SplitPane mainContainer;

    @FXML
    private GridPane tilesPlayerView;

    @FXML
    private ListView<String> scoreTableView;

    @FXML
    private GridPane board;

    @FXML
    private Button submitButton;

    @FXML
    private Button skipTurnButton;

    @FXML
    private Button exitButton;

    @FXML
    Button startGame;

    @FXML
    private Label nameGuest;

    //---------------

    private TileView selectedTile;

    private Label draggedTile; // Reference to the dragged tile label

    // binding variables for the vm \\

    BooleanProperty disconnect;
    ListProperty<Tile> bindingTiles; //the binding for the vm //
    ListProperty<String> bindingScoreTable;//for all players
    ObjectProperty<Tile[][]> bindingBoard;//for all players
    StringProperty nameBinding;

    private BooleanProperty myTurn;
    private BooleanProperty gameOver;


    //THE INITIALIZE COLORS VALUES
    private static final Color defoultTileBardColor = Color.WHITE;
    private static final Color defoultTileBagColor = Color.SANDYBROWN;
    private static final Color middleStarColor = Color.GOLD;
    private static final Color tripleWordScoreColor = Color.RED;
    private static final Color doubleWordScoreColor = Color.LIGHTYELLOW;
    private static final Color tripleLetterScoreColor = Color.BLUE;
    private static final Color doubleLetterScoreColor = Color.LIGHTBLUE;

    private static final String tripleWord="Triple Word";
    private static final String doubleWord="Double Word";
    private static final String doubleLetter="Double Letter";
    private static final String tripleLetter="Triple Letter";


    private static final int TILE_SIZE = 35;

    public GameViewController() {
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    public void setViewModel(ScrabbleViewModel vm) {
        this.vm = vm;
        disconnect = new SimpleBooleanProperty(false);
        disconnect.bindBidirectional(this.vm.getDisconnect());
        disconnect.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(() -> stage.close());
            }
        });
    }


    public void initWindow() {
        System.out.println("initWindow");
        initBinding();
        initSoreTable();
        initBoard();
        initPlayersTiles();
        initButtons();
        addListeners();
    }


    private void initBinding() {
        scoreTableView.getItems().clear();
        scoreTableView.itemsProperty().bind(vm.getScores());
        bindingTiles = new SimpleListProperty<>(FXCollections.observableArrayList());
        bindingTiles.bindBidirectional(vm.getTiles());
        bindingBoard = new SimpleObjectProperty<>();
        bindingBoard.bindBidirectional(vm.getBoard());
        myTurn = new SimpleBooleanProperty();
        myTurn.bindBidirectional(vm.myTurn);
        gameOver = new SimpleBooleanProperty();
        gameOver.bind(vm.getGameOver());
        disconnect = new SimpleBooleanProperty();
        disconnect.bindBidirectional(vm.getDisconnect());
        nameBinding.bindBidirectional(vm.getName());
    }

    private void initSoreTable() {
    }

    private void initBoard() {
        System.out.println("INIT BOARD:");
        Tile[][] boardPrev = vm.getPrevBoard();
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                TileView tileView = new TileView(boardPrev[i][j]);
                Cell cell = new Cell();
                cell.setTile(tileView);
                cell.setAlignment(Pos.CENTER);
                board.add(tileView, i, j);
            }
        }
        bindingBoard.set(vm.getPrevBoard());
    }

    private void initPlayersTiles() {
        System.out.println("INIT PLAYERS TILES");
        for (int i = 0; i < 7; i++) {
            TileView tile;
            if (!bindingTiles.isEmpty()) {
                tile = new TileView(bindingTiles.get(i));
            } else {
                tile = new TileView();
            }
            Cell c = new Cell();
            c.setDraggable(true);
            c.setTile(tile);
            tilesPlayerView.add(c,i,0);
        }
    }


    private void initButtons() {

        SimpleStringProperty playerName = new SimpleStringProperty();
        if(!myTurn.get()){
            submitButton.setDisable(true);
            skipTurnButton.setDisable(true);
            submitButton.setOpacity(0.5);
            skipTurnButton.setOpacity(0.5);
        }else{
            submitButton.setDisable(false);
            skipTurnButton.setDisable(false);
            submitButton.setOpacity(1);
            skipTurnButton.setOpacity(1);
        }
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
                bindingBoard.set(vm.getBoard().get());

            }
        });
    }

    private void setNameGuest(String name){
        this.nameGuest=new Label(nameBinding.get());
        this.nameGuest.setDisable(true);
    }


    public void skipTurn() throws IOException, InterruptedException {
        System.out.println("Skip Turn Clicked!");
        if (this.vm.myTurn.get()) {
            vm.skipTurn();
            this.myTurn.set(false);
        }
    }

    // an update of the playerTiles player bag\\

    @FXML
    private void submitButtonClicked(MouseEvent event) {
        System.out.println("submit button game clicked!");
        // Implement the submitButton event handler based on your requirements
    }

    public void setHost(boolean isHost) {
        this.host = isHost;
    }


    // helper classes \\

    //----------

    private class Cell extends StackPane {

        private TileView tile;

        private boolean draggable;


        public Cell() {
            this.tile = new TileView();
            tile.setStyle("-fx-background-color: transparent; -fx-font-size: 14px;");
            this.getChildren().add(tile);
            this.draggable = true;
            setPrefSize(TILE_SIZE, TILE_SIZE);
            setStyle("-fx-border-color: burlywood ;");
            initEvents();
        }

        private void initEvents() {
            // Set the drag and drop event handlers for the cell
            setOnDragOver(event -> {
                if (event.getGestureSource() != this && event.getDragboard().hasString()) {
                    // Allow for moving
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });

            setOnDragEntered(event -> {
                if (event.getGestureSource() != this && event.getDragboard().hasString()) {
                    setOpacity(0.7);
                }
            });

            setOnDragExited(event -> {
                if (event.getGestureSource() != this && event.getDragboard().hasString()) {
                    setOpacity(1.0);
                }
            });

            setOnDragDropped(event -> {
                if (event.getGestureSource() != this && event.getDragboard().hasString() && this.tile.letter == '\u0000') {
                    // Indicate that the drag operation was successful
                    event.setDropCompleted(true);

                    TileView sourceTile = (TileView) event.getGestureSource();
                    Cell sourceCell = (Cell) sourceTile.getParent();
                    sourceCell.setDraggable(true);
                    // Check if the dragged Tile belongs to the playerTiles or tiles
                    if (tilesPlayerView.getChildren().contains(sourceCell) || bindingTiles.contains(sourceCell.tile)) {
                        if (this == sourceCell) {
                            // The tile is being dragged onto the same cell, no action needed
                            return;
                        }
                        // Update the UI to reflect the change
                        System.out.println(sourceCell.tile.letter);
                        //playerBoardTiles.add(this);
                        if (this.tile.letter != '\u0000') {
                            System.out.println(this.tile.letter);
                            return;
                        }

                        TileView newTile = new TileView(sourceTile.getTileOriginal());

                        this.getChildren().clear();
                        this.tile.setTile(newTile.getTileOriginal());
                        this.getChildren().add(this.tile);
                        sourceCell.getChildren().clear();
                        sourceTile.setTile(new TileView().getTileOriginal());
                        this.setDraggable(true);
                        // Update the bindingBoard property
                        int row = GridPane.getRowIndex(this);
                        int col = GridPane.getColumnIndex(this);
                        Tile[][] currentBoard = bindingBoard.get();
                        currentBoard[row][col] = newTile.getTileOriginal();
                        bindingBoard.set(currentBoard);
                    }
                } else {
                    event.setDropCompleted(false);
                }
                event.consume();
            });


        }

        public void setTile(TileView tile) {
            this.tile = tile;
            getChildren().clear();
            getChildren().add(this.tile);
        }


        public void setDraggable(Boolean val) {
            tile.setDraggable(val);
            this.draggable = val;
        }

        public TileView getTile() {
            return tile;
        }

        public boolean isDraggable() {
            return draggable;
        }
    }


    public class TileView extends Label {

        private char letter;
        private int score;
        private Color color;
        private final DropShadow shadow = new DropShadow();
        private Cell targetCell;
        private Tile tileOriginal;

        private boolean draggable;

        public TileView(Tile tile) { // brown tiles
            super();
            tileOriginal = tile;
            this.letter = tile.letter;
            this.score = tile.score;
            this.color = defoultTileBagColor;
            draggable = true;
//            initValue();
            setText(getTileText());
            setPrefSize(TILE_SIZE, TILE_SIZE);
            String style="-fx-background-color:tan; -fx-font-size: 24px;";
            setAlignment(Pos.CENTER);

            this.initEvents();
        }

        private void initEvents() {
            setOnDragDetected(event -> {
                System.out.println(draggable);
                if (!draggable)
                    return;
                Dragboard db = startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(getText());
                db.setContent(content);
                selectedTile = this;

                // Add dragging effect
                setEffect(shadow);
                toFront();

                // Set the drag view
                SnapshotParameters parameters = new SnapshotParameters();
                parameters.setFill(Color.TRANSPARENT);
                Image snapshot = snapshot(parameters, null);
                db.setDragView(snapshot, snapshot.getWidth() / 2, snapshot.getHeight() / 2);

                // Set the drag position relative to the tile
                event.setDragDetect(true);
                event.consume();
            });

            setOnDragDone(event -> {
                if (event.getTransferMode() == TransferMode.MOVE && targetCell != null) {
                    selectedTile = null;
                    // Remove the tile from the source cell
                    Cell sourceCell = (Cell) getParent();
                    sourceCell.setTile(new TileView());
                    // Add the tile to the target cell
                    targetCell.setTile(this);

                    // Center the tile in the target cell
                    double cellCenterX = targetCell.getLayoutX() + targetCell.getWidth() / 2;
                    double cellCenterY = targetCell.getLayoutY() + targetCell.getHeight() / 2;
                    double tileCenterX = cellCenterX - getLayoutX() - TILE_SIZE / 2;
                    double tileCenterY = cellCenterY - getLayoutY() - TILE_SIZE / 2;

                    // Animate the tile movement to the center of the cell
                    TranslateTransition transition = new TranslateTransition(Duration.millis(200), this);
                    transition.setToX(tileCenterX);
                    transition.setToY(tileCenterY);
                    transition.play();
                }

                // Remove dragging effect
                setEffect(null);
                event.consume();
            });
        }

        public Tile getTileOriginal() {
            return this.tileOriginal;
        }

        public TileView() {
            super();
            this.letter = '\u0000';
            draggable = true;

//            initValue();

            setText(getTileText());

            setPrefSize(TILE_SIZE, TILE_SIZE);
            setStyle("-fx-background-color: lightblue; -fx-font-size: 14px;");
            setAlignment(Pos.CENTER);
            this.initEvents();
        }


        private void initValue() {
            switch (Character.toUpperCase(letter)) {
                case 'E', 'A', 'I', 'O', 'N', 'R', 'T', 'L', 'S', 'U' -> score = 1;
                case 'D', 'G' -> score = 2;
                case 'B', 'C', 'M', 'P' -> score = 3;
                case 'F', 'H', 'V', 'W', 'Y' -> score = 4;
                case 'K' -> score = 5;
                case 'J', 'X' -> score = 8;
                case 'Q', 'Z' -> score = 10;
                default -> score = 0; // Blank tiles or unsupported characters
            }
        }


        // setters and getters \\

        public void setTile(Tile tile) {
            if (tile != null) {
                this.letter = tile.letter;
                this.score = tile.score;
                this.setStyle("-fx-background-color: lightblue; -fx-font-size: 14px;");
            } else {
                this.letter = ' ';
            }
            setText(getTileText());
        }

        private String getTileText() {
            if (score == 0) {
                return String.valueOf(letter);
            } else {
                return letter + " (" + score + ")";
            }
        }


        public Character getLetter() {
            return letter;
        }

        public void setLetter(char letter) {
            this.letter = letter;
            setText(this.getTileText());
        }


        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public void setColor(Color color) {
            this.color = color;
            updateColor();
        }

        public Color getColor() {
            return color;
        }

        private void updateColor() {
            setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        }

        public TileView TileToTileView(Tile tile) {
            return new TileView(tile);

        }

        public void setDraggable(Boolean val) {
            draggable = val;
        }
    }

    //---------------------

    public ListView<TileView> tileListToTileViewList(List<Tile> tiles) {
        ListView<TileView> table = new ListView<>();
        for (
                Tile tile : tiles) {
            table.getItems().add(new TileView(tile));

        }
        return table;
    }


    //function to make a color array\\
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
}
