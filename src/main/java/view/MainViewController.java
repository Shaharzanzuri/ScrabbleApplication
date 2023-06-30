package view;

import Model.GuestModel;
import Model.HostModel;
import ViewModel.ScrabbleViewModel;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class MainViewController {


    ScrabbleViewModel vm;

    @FXML
    public BorderPane mainContainer;

    @FXML
    public HBox buttonHbox;

    @FXML
    TextField name;
    @FXML
    Button editButton;
    @FXML
    TextField ip;
    @FXML
    TextField port;
    @FXML
    DialogPane dialog;
    @FXML
    private TextField textField;
    @FXML
    public Button join;
    @FXML
    public Button hostGAME;

    boolean isHost = false;


    private BooleanProperty disconnect;

    Stage stage;


    private static MainViewController singleton_instace = null;

    public static MainViewController getMainPage() throws IOException {
        if (singleton_instace == null)
            singleton_instace = new MainViewController();
        return singleton_instace;
    }

    public Scene getScene() throws IOException {
        FXMLLoader fxmlLoader = null;
        String fxmlPath = "src/main/resources/ui/fxml/main-page.fxml";
        fxmlLoader = new FXMLLoader(new File(fxmlPath).toURL());
        Scene scene = new Scene(fxmlLoader.load());
//        scene.getStylesheets().add(getClass().getResource("/ui/css/main-page.css").toExternalForm());
        return scene;
    }

    public void swapePage() throws IOException {
        System.out.println("swapPage function");
        FXMLLoader fxmlLoader = null;
        String fxmlPath = "src/main/resources/ui/fxml/game_view.fxml";
        fxmlLoader = new FXMLLoader(new File(fxmlPath).toURL());
        Scene scene = new Scene(fxmlLoader.load());
       scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ui/css/game-page.css")).toExternalForm());
        GameViewController bc = fxmlLoader.getController();
        bc.setStage(stage);
        bc.setViewModel(vm);

        stage.setOnCloseRequest((WindowEvent event) -> {
            event.consume(); // Consume the event to prevent automatic window closure

            // Show a confirmation dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?");
            alert.showAndWait()
                    .filter(response -> response == ButtonType.OK) // Handle the user's choice
                    .ifPresent(response -> {
                        Platform.runLater(() -> {
                            System.out.println("Disconnected!");
                            vm.disconnect();
                            stage.close();
                        });
                    });
        });
        stage.setScene(scene);
        stage.show();
        bc.initWindow();
//        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
    }

    public Scene getScenePage() throws IOException {
        FXMLLoader loader = new FXMLLoader(new File("src/main/resources/ui/fxml/game_view.fxml").toURL());
        return new Scene(loader.load());
    }


    public void guestGame() throws IOException {
        System.out.println("Join clicked!");
        System.out.println(Integer.parseInt(port.getText()));
        GuestModel guest = null;
        try {
            guest = new GuestModel(name.getText(), ip.getText(), Integer.parseInt(port.getText()));
        } catch (IOException e) {
            e.printStackTrace();
            dialog.setContentText(e.getMessage());
        }
        if (guest != null) {
            vm = new ScrabbleViewModel(guest);
            this.swapePage();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void hostGame() throws IOException {
        System.out.println("Host clicked!");
        HostModel host = null;
        host = new HostModel(name.getText());
        vm = new ScrabbleViewModel(host);
        isHost = true;

        this.swapePage();
    }

    public void editParameters(ActionEvent actionEvent) {
        dialog.setContentText("");
        if (editButton.getText().equals("EDIT")) {
            System.out.println("Edit nickname clicked! " + name.getText());
            name.setEditable(true);
            ip.setEditable(true);
            port.setEditable(true);
            editButton.setText("SAVE");
        } else {
            System.out.println("Save nickname clicked! " + name.getText());
            name.setEditable(false);
            ip.setEditable(false);
            port.setEditable(false);
            editButton.setText("EDIT");
        }
    }


}
