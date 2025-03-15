package states;

import gui.App;
import gui.GameStates;
import gui.Style;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import model.Game;
import model.Player;
import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.util.ArrayList;

public class AddPlayers extends State{
    public static AddPlayers INSTANCE = new AddPlayers();
    public static AddPlayers getInstance(){return INSTANCE;}

    private ArrayList<Player> newPlayers = new ArrayList<>();
    private VBox leaderboardBox = makeLeaderboardBox();
    public int numberPlayers = 0;

    private Button playRandom = playRandomButton();

    public AddPlayers() {
        super("Add Players");
    }

    public void enter(){
        numberPlayers = 0;
        newPlayers.removeAll(newPlayers);
        leaderboardBox.getChildren().removeAll(leaderboardBox.getChildren());
        getPane().setCenter(makeMainBox());

        StackPane backgroundColor = new StackPane();
        backgroundColor.setBackground(skyBackground());

        backgroundColor.getChildren().add(getPane());

        getGameRoot().setCenter(backgroundColor);

    }

    private VBox makeMainBox(){
        VBox box = new VBox(50);
        box.setAlignment(Pos.CENTER);

        box.getChildren().addAll(makeAddPlayerAndLeaderboardBox(), makeButtonsBox());
        return box;
    }

    private HBox makeAddPlayerAndLeaderboardBox(){
        HBox box = new HBox(75);
        box.setAlignment(Pos.CENTER);


        box.getChildren().addAll(makeAddPlayerBox(), leaderboardBox);
        return box;
    }

    private VBox makeLeaderboardBox(){
        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);

        box.setPrefSize(200, 150);

        return box;
    }

    private VBox makeAddPlayerBox(){
        VBox box = new VBox(15);

        Label title = new Label("Ajouter un joueur :");
        title.setFont(Style.titleFont);

        box.getChildren().addAll(title, makePlayerFieldBox());
        return box;
    }

    private HBox makePlayerFieldBox(){
        HBox box = new HBox(25);
        box.setAlignment(Pos.CENTER);

        TextField textField = new TextField();
        textField.setPromptText("Donner le nom du joueur");
        textField.setFont(Style.paragraphFont);

        box.getChildren().addAll(textField, addPlayerButton(textField));
        return box;
    }

    private HBox makeButtonsBox(){
        HBox box = new HBox(75);
        box.setAlignment(Pos.CENTER);

        box.getChildren().addAll(playRandom);
        return box;
    }

    private HBox makeLeaderboardAndRemoveButtonBox(Player p){
        HBox box = new HBox(20);
        box.setAlignment(Pos.CENTER);

        Label playerName = new Label(p.getName());
        playerName.setFont(Style.paragraphFont);

        playerName.setPrefSize(120, 30);

        Button removeButton = removePlayerButton(p, box);
        removeButton.setDisable(true);
        removeButton.setDisable(false);

        box.getChildren().addAll(playerName, removeButton);
        return box;
    }

    private Button removePlayerButton(Player p, HBox box){
        Button removePlayer = new Button("X");
        Style.putListenerInButton(removePlayer);
        removePlayer.setFont(Style.lightFont);

        removePlayer.setOnAction(e -> {
            newPlayers.remove(p);
            leaderboardBox.getChildren().remove(box);
            numberPlayers--;
            stylePlayButton(playRandom);
        });

        Style.putListenerInButton(removePlayer);

        return removePlayer;
    }

    private Button playRandomButton(){
        Button playRandom = new Button("Jouer au jeu !");
        stylePlayButton(playRandom);
        Style.bigButtonSize(playRandom);
        playRandom.setFont(Style.titleFont);
        playRandom.setDisable(true);

        return playRandom;
    }

    private Button addPlayerButton(TextField textField){
        Button addPlayer = new Button("Ajouter");
        Style.putListenerInButton(addPlayer);
        addPlayer.setDisable(true);
        Style.littleButtonSize(addPlayer);
        addPlayer.setFont(Style.lightFont);

        makeTextFieldListener(textField, addPlayer);
        addPlayer.setOnAction(e -> {
            if(textField.getText().length() >= 3 && numberPlayers < 5){
                addPlayerToList(textField.getText());
                textField.setText("");
                addPlayer.setDisable(true);
            }
        });

        return addPlayer;
    }

    public void stylePlayButton(Button button){

        button.setOnAction(e -> {
            GameStates.ADDPLAYERS.changeState(GameStates.GAMEPLAY.getState());
        });

        if(numberPlayers < 2){
            button.setDisable(true);
            Style.styleButton(button, Style.DESACTIVATED);
        }
        else if(numberPlayers == 5){
            button.setDisable(false);
            Style.styleButton(button, Style.LIMITREACHED);
        }
        else{
            button.setDisable(false);
            Style.styleButton(button, Style.ACTIVATED);
        }
    }

    private void makeTextFieldListener(TextField textField, Button button){
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z-éïùèàâ]*")) {
                textField.setText(oldValue); // Ne prend en compte que les caractères alphabétiques
            }
            else if(newValue.length() > 10){
                textField.setText(oldValue);
            }
            else if(newValue.length() < 3) button.setDisable(true);
            else button.setDisable(false);
        });

        textField.setOnAction(e -> {
            if(textField.getText().length() >= 3 && numberPlayers < 5){
                addPlayerToList(textField.getText());
                textField.setText("");
                button.setDisable(true);
            }
        });
    }

    private void addPlayerToList(String name){
        Player p = new Player(name);
        newPlayers.add(p);
        numberPlayers++;

        leaderboardBox.getChildren().add(makeLeaderboardAndRemoveButtonBox(p));

        stylePlayButton(playRandom);
    }

    public void exit(){
        try {
            Game game = new Game(newPlayers);
            App.gameModel = game;
            numberPlayers = 0;
            playRandom.setDisable(true);
            stylePlayButton(playRandom);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
    }

    private Background skyBackground(){
        Stop[] stops = new Stop[] {
                new Stop(0, Color.web("#87CEEB")),
                new Stop(0.5, Color.web("#87CEEB")),
                new Stop(1, Color.web("#1E90FF"))
        };

        LinearGradient gradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
        BackgroundFill backgroundFill = new BackgroundFill(gradient, null, null);
        Background background = new Background(backgroundFill);

        return background;
    }

    private Background twilightBackground(){
        Stop[] stops = new Stop[] {
                new Stop(0, Color.web("#f6e05e")),
                new Stop(0.5, Color.web("#f6ad55")),
                new Stop(1, Color.web("#ed8936"))
        };

        LinearGradient gradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
        BackgroundFill backgroundFill = new BackgroundFill(gradient, null, null);
        Background background = new Background(backgroundFill);

        return background;
    }
}
