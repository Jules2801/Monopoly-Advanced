package states;

import controller.WinRoundController;
import gui.App;
import gui.Style;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import model.Player;

public class WinRound extends State{
    public static WinRound INSTANCE = new WinRound();
    public final WinRoundController controller = new WinRoundController(this);
    public WinRound() {
        super("WinRound");
    }

    public static WinRound getInstance(){return INSTANCE;}

    public void enter(){
        getPane().setCenter(getWinVBox());
        getPane().setBackground(Style.skyBackground());
        getGameRoot().setCenter(getPane());
    }

    public VBox getWinVBox(){
        VBox box = new VBox(60);
        box.setAlignment(Pos.CENTER);

        box.getChildren().addAll(getTitle(), getButtons());

        return box;
    }

    public VBox getButtons(){
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);

        box.getChildren().addAll(getReplayButton(), getQuitButton());

        return box;
    }

    public HBox getTitle(){
        HBox box = new HBox(3);
        box.setAlignment(Pos.CENTER);

        Label playerName = new Label();
            setPlayerStyle(playerName, App.gameModel.gameIsFinished());

        Label title1 = new Label("Le joueur ");
            setTitleStyle(title1);

        Label title2 = new Label(" à gagné la partie !");
            setTitleStyle(title2);

        box.getChildren().addAll(title1, playerName, title2);
        return box;
    }

    public Button getReplayButton(){
        Button b = new Button("Revenir au menu principal");
        b.setFont(Style.titleFont);
        setButtonStyle(b);
        b.setOnAction(e -> controller.replay());

        return b;
    }

    public Button getQuitButton(){
        Button b = new Button("Quitter");
        b.setOnAction(e -> controller.quit());
        b.setFont(Style.titleFont);
        setButtonStyle(b);
        return b;
    }

    public void setTitleStyle(Label title){
        title.setStyle("-fx-font-size: 50px; -fx-text-fill: red;");
    }

    public void setPlayerStyle(Label playerName, Player player){
        playerName.setText(player.getName());
        playerName.setStyle("-fx-font-size: 50px; -fx-text-fill: #C4F9D0; -fx-font-weight: bolder;");
    }

    public void setButtonStyle(Button button){
        button.setStyle("-fx-background-color: #589ce4; -fx-background-radius: 8px; -fx-border-width: 5px; -fx-border-radius: 6px; -fx-border-color: #90d4fc; -fx-text-alignment: center;");
        button.setPrefSize(163*2, 81);
        button.setOnMouseEntered(event -> button.setStyle("-fx-background-color: #589ce4; -fx-background-radius: 8px; -fx-border-width: 5px; -fx-border-radius: 6px; -fx-border-color: #8EB7E3"));
        button.setOnMouseExited(event -> button.setStyle("-fx-background-color: #589ce4; -fx-background-radius: 8px; -fx-border-width: 5px; -fx-border-radius: 6px; -fx-border-color: #90d4fc;"));

    }

    public void transitionTo(State s){System.out.println("Transitioning from Gameplay to " + s.showState());}
}
