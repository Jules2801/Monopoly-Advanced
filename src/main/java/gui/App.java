package gui;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.Game;
import states.State;

import java.io.IOException;

public class App extends Application {
    public static final BorderPane root = new BorderPane();
    public static State currentInstance = GameStates.MENU.getState();
    public static Stage primaryStage;
    public static Game gameModel;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        // Création de la scène
        Scene scene = new Scene(root, 1280, 720);
        primaryStage.setResizable(false);

        // Assignation de la scène à la stage
        stage.setTitle("Monopoly : Remastered Edition");
        stage.setScene(scene);

        // Affichage de la stage
        currentInstance.enter();
        stage.show();
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - primaryStage.getHeight()) / 2);
    }

    public static void main(String[] args) {
        launch();
    }
}
