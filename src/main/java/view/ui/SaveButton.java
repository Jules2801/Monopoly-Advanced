package view.ui;

import gui.App;
import gui.GameStates;
import gui.Style;
import javafx.geometry.Pos;
import javafx.scene.control.Button;

public class SaveButton extends Button {

    public final double prefWidth = 163*2;
    public final double prefHeight = 81;

    public SaveButton(){
        super();
        setStyle("-fx-background-color: #589ce4; -fx-background-radius: 8px; -fx-border-width: 5px; -fx-border-radius: 6px; -fx-border-color: #90d4fc; -fx-text-alignment: center;");
        setPrefSize(163, 81);

        setOnMouseEntered(event -> setStyle("-fx-background-color: #589ce4; -fx-background-radius: 8px; -fx-border-width: 5px; -fx-border-radius: 6px; -fx-border-color: #f4fcfc"));
        setOnMouseExited(event -> setStyle("-fx-background-color: #589ce4; -fx-background-radius: 8px; -fx-border-width: 5px; -fx-border-radius: 6px; -fx-border-color: #90d4fc;"));

        setText("REPRENDRE LA SAUVEGARDE");
        setAlignment(Pos.CENTER);
        setFont(Style.titleFont);

        double newWidth = App.primaryStage.getWidth();
        setPrefWidth((newWidth * prefWidth) / 1260);

        double newHeight = App.primaryStage.getHeight();
        setPrefHeight((newHeight * prefHeight) / 1080);

        Style.styleButton(this, Style.ACTIVATED);
        setOnMouseClicked(event -> GameStates.MENU.changeState(GameStates.SAVEMENU.getState()));
    }
}
