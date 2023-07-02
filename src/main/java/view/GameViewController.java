package view;

import Data.Tile;
import ViewModel.ScrabbleViewModel;
import javafx.application.Platform;


import javafx.beans.property.*;
import javafx.collections.FXCollections;

import javafx.fxml.FXML;


import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

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
    ListProperty<String> bindingScoreTable=new SimpleListProperty<>();//for all players
    ObjectProperty<Tile[][]> bindingBoard;//for all players
    StringProperty nameBinding = new SimpleStringProperty();

    private BooleanProperty myTurn;
    private BooleanProperty gameOver;


    ObjectProperty<TileView[][]> tileViewBoard = new SimpleObjectProperty<>();

    //THE INITIALIZE COLORS VALUES
    private static final Color defoultTileBardColor = Color.WHITE;
    private static final Color defoultTileBagColor = Color.SANDYBROWN;
    private static final Color middleStarColor = Color.GOLD;
    private static final Color tripleWordScoreColor = Color.RED;
    private static final Color doubleWordScoreColor = Color.LIGHTYELLOW;
    private static final Color tripleLetterScoreColor = Color.BLUE;
    private static final Color doubleLetterScoreColor = Color.LIGHTBLUE;

    private static final String tripleWord = "Triple Word";
    private static final String doubleWord = "Double Word";
    private static final String doubleLetter = "Double Letter";
    private static final String tripleLetter = "Triple Letter";


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
        drawBoard();
//        initBoard();
//        initTileViewBoard();
        initPlayersTiles();
        initButtons();
        setNameGuest(nameBinding.get());
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
        for (int i = 0; i < 7; i++) {
            if (!bindingTiles.isEmpty()) {
                TileView tileView=new TileView(bindingTiles.get(i));
                Image tile =tileView.getImage();
                ImageView iv = new ImageView(tile);
                iv.setPreserveRatio(true);
                iv.setFitWidth(35);
                iv.setFitHeight(35);
                StackPane sp = new StackPane(iv);
                sp.setAlignment(Pos.CENTER);
                if (i < 5) {
                    tilesPlayerView.add(sp, i, 0);
                } else {
                    tilesPlayerView.add(sp, i, 1);
                }
            }


        }
    }


    public void drawBoard(){
        board.getChildren().clear();
        for(int i=0;i<15;i++){
            for(int j=0;j<15;j++){
                if(bindingBoard.get()[i][j].letter!=' '){
                    TileView tileView=new TileView(bindingBoard.get()[i][j]);
                    Image tile = tileView.getImage();
                    ImageView iv = new ImageView(tile);
                    iv.setPreserveRatio(true);
                    iv.setFitWidth(35);
                    iv.setFitHeight(35);
                    StackPane sp = new StackPane(iv);
                    sp.setAlignment(Pos.CENTER);
                    board.add(sp, j, i);
                }
            }
        }
    }


    private void initButtons() {

        SimpleStringProperty playerName = new SimpleStringProperty();
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
        this.nameGuest = new Label(nameBinding.get());
        this.nameGuest.setDisable(true);
        nameGuest.setDisable(false);
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



    public void initTileViewBoard() {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                TileView tileView = new TileView(bindingBoard.get()[i][j]);
                board.add(tileView, i, j);
            }
        }

    }


    public class TileView extends StackPane {

        private char letter;
        private int score;
        private final DropShadow shadow = new DropShadow();
        private Tile tileOriginal;
        private Image image=null;

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
           // this.initEvents();

        }

        private void initEvents() {
            setOnDragDetected(event -> {
                System.out.println(draggable);
                if (!draggable)
                    return;
                Dragboard db = startDragAndDrop(TransferMode.MOVE);
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

        }

        public Tile getTileOriginal() {
            return this.tileOriginal;
        }

        public TileView() {
            super();
            this.letter = ' ';
            draggable = true;


            initImageValues();
            this.setHeight(35.0);
            this.setPrefWidth(35.5);


            setAlignment(Pos.CENTER);
            this.initEvents();
        }

        public Image getImage(){
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


        private void initImageValues(){
            switch (Character.toLowerCase(letter)){
                case' '->setImage(null);
                case'a'->setImage("ui/image/general/bag/a.jpg");
                case'b'->setImage("ui/image/general/bag/b.jpg");
                case'c'->setImage("ui/image/general/bag/c.jpg");
                case'd'->setImage("ui/image/general/bag/d.jpg");
                case'e'->setImage("ui/image/general/bag/e.jpg");
                case'f'->setImage("ui/image/general/bag/f.jpg");
                case'g'->setImage("ui/image/general/bag/g.jpg");
                case'h'->setImage("ui/image/general/bag/h.jpg");
                case'i'->setImage("ui/image/general/bag/i.jpg");
                case'j'->setImage("ui/image/general/bag/j.jpg");
                case'k'->setImage("ui/image/general/bag/k.jpg");
                case'l'->setImage("ui/image/general/bag/l.jpg");
                case'm'->setImage("ui/image/general/bag/m.jpg");
                case'n'->setImage("ui/image/general/bag/n.jpg");
                case'o'->setImage("ui/image/general/bag/o.jpg");
                case'p'->setImage("ui/image/general/bag/p.jpg");
                case'q'->setImage("ui/image/general/bag/q.jpg");
                case'r'->setImage("ui/image/general/bag/r.jpg");
                case's'->setImage("ui/image/general/bag/s.jpg");
                case't'->setImage("ui/image/general/bag/t.jpg");
                case'u'->setImage("ui/image/general/bag/u.jpg");
                case'v'->setImage("ui/image/general/bag/v.jpg");
                case'w'->setImage("ui/image/general/bag/w.jpg");
                case'x'->setImage("ui/image/general/bag/x.jpg");
                case'y'->setImage("ui/image/general/bag/y.jpg");
                case'z'->setImage("ui/image/general/bag/z.jpg");
            }
        }

        private void setImage(String url){
            this.image=new Image(url);
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
