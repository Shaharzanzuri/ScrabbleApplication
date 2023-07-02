package view;

import Data.Tile;
import ViewModel.ScrabbleViewModel;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;

import java.awt.*;
import java.io.File;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

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
    ListProperty<String> bindingScoreTable = new SimpleListProperty<>();//for all players
    ObjectProperty<Tile[][]> bindingBoard;//for all players
    StringProperty nameBinding = new SimpleStringProperty();

    private BooleanProperty myTurn;
    private BooleanProperty gameOver;


    ObjectProperty<TileView[][]> prevViewBoard = new SimpleObjectProperty<>();

    //THE INITIALIZE IMAGES VALUES
    private final Image TileBoardImage = getImageFromUrl("src/main/resources/ui/image/general/blank_tile.jpg");
    private final Image middleStarImage = getImageFromUrl("src/main/resources/ui/image/general/star.jpg");
    private final Image tripleWordScoreImage = getImageFromUrl("src/main/resources/ui/image/general/trippleWord.jpg");
    private final Image doubleWordScoreImage = getImageFromUrl("src/main/resources/ui/image/general/doubleWord.jpg");
    private final Image tripleLetterScoreImage = getImageFromUrl("src/main/resources/ui/image/general/trippleLetter.jpg");
    private final Image doubleLetterScoreImage = getImageFromUrl("src/main/resources/ui/image/general/doubleLetter.jpg");

    private static final String tripleWord = "Triple Word";
    private static final String doubleWord = "Double Word";
    private static final String doubleLetter = "Double Letter";
    private static final String tripleLetter = "Triple Letter";
    private static final String regularLetter = "Regular Letter";


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
        initPlayersTiles();
        drawBoard();
        initButtons();
        setNameGuest(nameBinding.get());
        addListeners();
    }


    private void initBinding() {
        bindingScoreTable = new SimpleListProperty<>(vm.getScores());
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
        setNameGuest(vm.getName().get());
    }

    private void initSoreTable() {
        scoreTableView.getItems().add(bindingScoreTable.getName());
    }

    private void initBoard() {
        System.out.println("INIT BOARD:");
        Tile[][] boardPrev = vm.getPrevBoard();
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                TileView tileView = new TileView(boardPrev[i][j]);
                board.add(tileView, i, j);
            }
        }
        bindingBoard.set(vm.getPrevBoard());
    }

    private void initPlayersTiles() {
        System.out.println("INIT PLAYERS TILES");
        tilesPlayerView.getChildren().clear();
        for (int i = 0; i < 7; i++) {
            if (!bindingTiles.isEmpty()) {
                TileView tileView = new TileView(bindingTiles.get(i));
                Image tile = tileView.getImage();
                ImageView iv = new ImageView(tile);
                iv.setPreserveRatio(true);
                iv.setFitWidth(40);
                iv.setFitHeight(40);
                iv.setStyle("-fx-background-color: light-gray; -fx-border-color: black; -fx-border-width: 2px;");
                StackPane sp = new StackPane(iv);
                sp.setAlignment(Pos.CENTER);
                StackPane.setMargin(sp, new Insets(5, 0, 10, 0));
                if (i < 4) {
                    tilesPlayerView.add(sp, i, 1);
                } else {
                    tilesPlayerView.add(sp, 7 - i, 2);
                }
                // Enable drag-and-drop functionality for the tile
                enableDragAndDrop(tileView, sp);
            }
        }
    }


    public void drawBoard() {
        board.getChildren().clear();
        ImageView[][] imageViewsBoard = getBoardImages();
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (bindingBoard.get()[i][j].letter != ' ') {
                    TileView tileView = new TileView(bindingBoard.get()[i][j]);
                    Image tile = tileView.getImage();
                    ImageView iv = new ImageView(tile);
                    iv.setPreserveRatio(true);
                    iv.setFitWidth(35);
                    iv.setFitHeight(35);
                    StackPane sp = new StackPane(iv);
                    sp.setAlignment(Pos.CENTER);
                    board.add(sp, j, i);
                } else {
                    TileView tileView=new TileView(bindingBoard.get()[i][j]);
                    ImageView iv = imageViewsBoard[i][j];
                    if (iv != null) {
                        iv.setPreserveRatio(true);
                        iv.setFitWidth(35);
                        iv.setFitHeight(35);
                        StackPane sp = new StackPane(iv);
                        sp.setAlignment(Pos.CENTER);
                        board.add(sp, j, i);
                        enableDrop(tileView,sp);

                    }
                }
            }

        }
        board.setGridLinesVisible(true);
    }


    private void initButtons() {
        System.out.println("INIT BUTTONS");
        exitButton.setDisable(false);
        exitButton.setOpacity(1);
        if (!myTurn.get()) {
            submitButton.setDisable(true);
            skipTurnButton.setDisable(true);
            submitButton.setOpacity(0.5);
            skipTurnButton.setOpacity(0.5);
        } else {
            submitButton.setDisable(false);
            skipTurnButton.setDisable(false);
            submitButton.setOpacity(1);
            skipTurnButton.setOpacity(1);
        }
    }

    private void addListeners() {
        System.out.println("INIT LISTENERS");
        myTurn.addListener((observable, oldValue, newValue) -> {
            initButtons();
        });
        gameOver.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(() -> {
                    exitGame();
                });
            }
        });
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

    private void setNameGuest(String name) {
        System.out.println("INIT NAME GUEST");
        this.nameGuest = new Label(nameBinding.get());
        this.nameGuest.setDisable(false);
        this.nameGuest.setBlendMode(BlendMode.SRC_ATOP);

    }

    @FXML
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

    @FXML
    private void exitGame() {
        vm.disconnect();
        System.exit(0);
    }


    public void setHost(boolean isHost) {
        this.host = isHost;
    }

    private void enableDrop(TileView tile,StackPane stackPane){

        // Set the drag and drop event handlers for the cell
        stackPane.setOnDragOver(event -> {
            if (event.getGestureSource() != this && event.getDragboard().hasString()) {
                // Allow for moving
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        stackPane.setOnDragEntered(event -> {
            if (event.getGestureSource() != this && event.getDragboard().hasString()) {
                stackPane.setOpacity(0.7);
            }
        });

        stackPane.setOnDragExited(event -> {
            if (event.getGestureSource() != this && event.getDragboard().hasString()) {
                stackPane.setOpacity(1.0);
            }
        });

        stackPane.setOnDragDropped(event -> {
            boolean success = false;
            if (event.getDragboard().hasString()) {
                // Remove the tile from the player's tiles
                bindingTiles.remove(selectedTile.getTileOriginal());

                // Add the tile to the board
                int columnIndex = GridPane.getColumnIndex(stackPane);
                int rowIndex = GridPane.getRowIndex(stackPane);
                bindingBoard.get()[rowIndex][columnIndex] = selectedTile.getTileOriginal();

                // Redraw the board and the player's tiles
                drawBoard();
                initPlayersTiles();

                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }



    private void enableDragAndDrop(TileView tileView, StackPane tilePane) {

        // Set the drag and drop event handlers for the cell
        tilePane.setOnDragOver(event -> {
            if (event.getGestureSource() != this && event.getDragboard().hasString()) {
                // Allow for moving
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });


        // Enable dragging the tile
        tilePane.setOnDragDetected(event -> {
            Dragboard dragboard = tilePane.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();

        // Set the drag view
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(javafx.scene.paint.Color.TRANSPARENT);
            content.putString("tile"); // Set a custom string as the content
            dragboard.setContent(content);
            selectedTile = tileView; // Store the selected tile
        event.setDragDetect(true);
            event.consume();
        });

        // Enable dropping the tile on the board
        board.setOnDragOver(event -> {
            if (event.getGestureSource() != board && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        // Handle dropping the tile on the board
        board.setOnDragDropped(event -> {
            boolean success = false;
            if (event.getDragboard().hasString()) {
                // Remove the tile from the player's tiles
                bindingTiles.remove(selectedTile.getTileOriginal());

                // Add the tile to the board
                int columnIndex = GridPane.getColumnIndex(tilePane);
                int rowIndex = GridPane.getRowIndex(tilePane);
                bindingBoard.get()[rowIndex][columnIndex] = selectedTile.getTileOriginal();

                // Redraw the board and the player's tiles
                drawBoard();
                initPlayersTiles();

                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }


    public class TileView extends StackPane {

        private char letter;
        private int score;
        private final DropShadow shadow = new DropShadow();
        private Tile tileOriginal;
        private Image image = null;
        private StackPane targetCell;

        private boolean draggable;

        public TileView(Tile tile) { // brown tiles
            super();
            tileOriginal = tile;
            this.letter = tile.letter;
            this.score = tile.score;
            draggable = true;
            setAlignment(Pos.CENTER);
            initValue();
            initImageValues();
            this.setVisible(true);

        }


        public TileView() {
            super();
            this.letter = ' ';
            draggable = true;


            initImageValues();
            this.setHeight(35.0);
            this.setPrefWidth(35.5);


            setAlignment(Pos.CENTER);

        }


        public Tile getTileOriginal() {
            return this.tileOriginal;
        }

        public Image getImage() {
            return this.image;
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


        private void initImageValues() {
            switch (Character.toLowerCase(letter)) {
                case ' ' -> setImage(null);
                case 'a' -> setImage("ui/image/bag/a.jpg");
                case 'b' -> setImage("ui/image/bag/b.jpg");
                case 'c' -> setImage("ui/image/bag/c.jpg");
                case 'd' -> setImage("ui/image/bag/d.jpg");
                case 'e' -> setImage("ui/image/bag/e.jpg");
                case 'f' -> setImage("ui/image/bag/f.jpg");
                case 'g' -> setImage("ui/image/bag/g.jpg");
                case 'h' -> setImage("ui/image/bag/h.jpg");
                case 'i' -> setImage("ui/image/bag/i.jpg");
                case 'j' -> setImage("ui/image/bag/j.jpg");
                case 'k' -> setImage("ui/image/bag/k.jpg");
                case 'l' -> setImage("ui/image/bag/l.jpg");
                case 'm' -> setImage("ui/image/bag/m.jpg");
                case 'n' -> setImage("ui/image/bag/n.jpg");
                case 'o' -> setImage("ui/image/bag/o.jpg");
                case 'p' -> setImage("ui/image/bag/p.jpg");
                case 'q' -> setImage("ui/image/bag/q.jpg");
                case 'r' -> setImage("ui/image/bag/r.jpg");
                case 's' -> setImage("ui/image/bag/s.jpg");
                case 't' -> setImage("ui/image/bag/t.jpg");
                case 'u' -> setImage("ui/image/bag/u.jpg");
                case 'v' -> setImage("ui/image/bag/v.jpg");
                case 'w' -> setImage("ui/image/bag/w.jpg");
                case 'x' -> setImage("ui/image/bag/x.jpg");
                case 'y' -> setImage("ui/image/bag/y.jpg");
                case 'z' -> setImage("ui/image/bag/z.jpg");
            }
        }

        private void setImage(String url) {
            String path = "src/main/resources/" + url;
            if (url == null) {
                System.out.println("url Image not exist");
            } else {
                File file = new File(path);
                if (file.exists()) {
                    try {
                        Image image = new Image(file.toURI().toString());
                        this.image = image;
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Handle error when loading image
                    }
                } else {
                    // Handle case when file does not exist
                    // Show an error message or handle it as desired
                }
            }
        }


        // setters and getters \\

        public void setTile(Tile tile) {
            if (tile != null) {
                this.letter = tile.letter;
                this.score = tile.score;
                this.initImageValues();
            } else {
                this.letter = ' ';
                this.initImageValues();
            }
        }


        public Character getLetter() {
            return letter;
        }

        public void setLetter(char letter) {
            this.letter = letter;
            this.initImageValues();
        }


        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
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


    //function to make a ImageView 2D array\\
    private ImageView[][] getBoardImages() {

        ImageView[][] ImageViewsArray = new ImageView[15][15];

        // Init color matrix
        for (int i = 0; i < 15; i++)
            for (int j = 0; j < 15; j++)
                if(bindingBoard.get()[i][j].letter==' '){
                    ImageViewsArray[i][j] = new ImageView();
                }


        ImageViewsArray[7][7] = new ImageView(middleStarImage);

        //triple word score
        ImageViewsArray[0][0] = new ImageView(tripleWordScoreImage);
        ImageViewsArray[7][0] = new ImageView(tripleWordScoreImage);
        ImageViewsArray[14][0] = new ImageView(tripleWordScoreImage);
        ImageViewsArray[0][7] = new ImageView(tripleWordScoreImage);
        ImageViewsArray[14][7] = new ImageView(tripleWordScoreImage);
        ImageViewsArray[0][14] = new ImageView(tripleWordScoreImage);
        ImageViewsArray[7][14] = new ImageView(tripleWordScoreImage);
        ImageViewsArray[14][14] = new ImageView(tripleWordScoreImage);

        //double word score
        ImageViewsArray[1][1] = new ImageView(doubleWordScoreImage);
        ImageViewsArray[2][2] = new ImageView(doubleWordScoreImage);
        ImageViewsArray[3][3] = new ImageView(doubleWordScoreImage);
        ImageViewsArray[4][4] = new ImageView(doubleWordScoreImage);
        ImageViewsArray[13][1] = new ImageView(doubleWordScoreImage);
        ImageViewsArray[12][2] = new ImageView(doubleWordScoreImage);
        ImageViewsArray[11][3] = new ImageView(doubleWordScoreImage);
        ImageViewsArray[10][4] = new ImageView(doubleWordScoreImage);
        ImageViewsArray[1][13] = new ImageView(doubleWordScoreImage);
        ImageViewsArray[2][12] = new ImageView(doubleWordScoreImage);
        ImageViewsArray[3][11] = new ImageView(doubleWordScoreImage);
        ImageViewsArray[4][10] = new ImageView(doubleWordScoreImage);
        ImageViewsArray[10][10] = new ImageView(doubleWordScoreImage);
        ImageViewsArray[11][11] = new ImageView(doubleWordScoreImage);
        ImageViewsArray[12][12] = new ImageView(doubleWordScoreImage);
        ImageViewsArray[13][13] = new ImageView(doubleWordScoreImage);


        //double letter score
        ImageViewsArray[3][0] = new ImageView(doubleLetterScoreImage);
        ImageViewsArray[11][0] = new ImageView(doubleLetterScoreImage);
        ImageViewsArray[6][2] = new ImageView(doubleLetterScoreImage);
        ImageViewsArray[8][2] = new ImageView(doubleLetterScoreImage);
        ImageViewsArray[0][3] = new ImageView(doubleLetterScoreImage);
        ImageViewsArray[7][3] = new ImageView(doubleLetterScoreImage);
        ImageViewsArray[14][3] = new ImageView(doubleLetterScoreImage);
        ImageViewsArray[2][6] = new ImageView(doubleLetterScoreImage);
        ImageViewsArray[6][6] = new ImageView(doubleLetterScoreImage);
        ImageViewsArray[8][6] = new ImageView(doubleLetterScoreImage);
        ImageViewsArray[12][6] = new ImageView(doubleLetterScoreImage);
        ImageViewsArray[3][7] = new ImageView(doubleLetterScoreImage);
        ImageViewsArray[11][7] = new ImageView(doubleLetterScoreImage);
        ImageViewsArray[2][8] = new ImageView(doubleLetterScoreImage);
        ImageViewsArray[6][8] = new ImageView(doubleLetterScoreImage);
        ImageViewsArray[8][8] = new ImageView(doubleLetterScoreImage);
        ImageViewsArray[12][8] = new ImageView(doubleLetterScoreImage);
        ImageViewsArray[0][11] = new ImageView(doubleLetterScoreImage);
        ImageViewsArray[7][11] = new ImageView(doubleLetterScoreImage);
        ImageViewsArray[14][11] = new ImageView(doubleLetterScoreImage);
        ImageViewsArray[6][12] = new ImageView(doubleLetterScoreImage);
        ImageViewsArray[8][12] = new ImageView(doubleLetterScoreImage);
        ImageViewsArray[3][14] = new ImageView(doubleLetterScoreImage);
        ImageViewsArray[11][14] = new ImageView(doubleLetterScoreImage);


        //triple letter score
        ImageViewsArray[5][1] = new ImageView(tripleLetterScoreImage);
        ImageViewsArray[9][1] = new ImageView(tripleLetterScoreImage);
        ImageViewsArray[1][5] = new ImageView(tripleLetterScoreImage);
        ImageViewsArray[5][5] = new ImageView(tripleLetterScoreImage);
        ImageViewsArray[9][5] = new ImageView(tripleLetterScoreImage);
        ImageViewsArray[13][5] = new ImageView(tripleLetterScoreImage);
        ImageViewsArray[1][9] = new ImageView(tripleLetterScoreImage);
        ImageViewsArray[5][9] = new ImageView(tripleLetterScoreImage);
        ImageViewsArray[9][9] = new ImageView(tripleLetterScoreImage);
        ImageViewsArray[13][9] = new ImageView(tripleLetterScoreImage);
        ImageViewsArray[5][13] = new ImageView(tripleLetterScoreImage);
        ImageViewsArray[9][13] = new ImageView(tripleLetterScoreImage);

        return ImageViewsArray;

    }

    private Image getImageFromUrl(String url) {
        Image image = null;
        if (url == null) {
            System.out.println("url Image not exist");
        } else {
            File file = new File(url);
            if (file.exists()) {
                try {
                    image = new Image(file.toURI().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    // Handle error when loading image
                }
            } else {
                // Handle case when file does not exist
                // Show an error message or handle it as desired
            }
        }
        return image;
    }

}
