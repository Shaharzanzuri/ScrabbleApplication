package view;

import Logic.BookScrabbleHandler;
import Logic.MyServer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class ScrabbleApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        MyServer s = new MyServer(8887, new BookScrabbleHandler());
        s.start();
        FXMLLoader fxmlLoader = null;
        String fxmlPath = "src/main/resources/ui/fxml/main_view.fxml";
        fxmlLoader = new FXMLLoader(new File(fxmlPath).toURL());
        Scene scene = new Scene(fxmlLoader.load(), 550, 550);
       scene.getStylesheets().add(getClass().getResource("/ui/css/hello-page.css").toExternalForm());
        MainViewController mp = fxmlLoader.getController();
        mp.setStage(stage);
//        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}