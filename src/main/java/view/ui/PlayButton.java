package view.ui;

import gui.App;
import gui.GameStates;
import gui.Style;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;

public class PlayButton extends Label {
    //private final Image defaultImage;
    //private final Image highlightImage;

    public final double prefWidth = 163*2;
    public final double prefHeight = 81;
    private boolean randomMap;

    public PlayButton(boolean randomMap) {
        super();
        this.randomMap = randomMap;
        setStyle("-fx-background-color: #589ce4; -fx-background-radius: 8px; -fx-border-width: 5px; -fx-border-radius: 6px; -fx-border-color: #90d4fc; -fx-text-alignment: center;");

        double newWidth = App.primaryStage.getWidth();
        setPrefWidth((newWidth * prefWidth) / 1260);

        double newHeight = App.primaryStage.getHeight();
        setPrefHeight((newHeight * prefHeight) / 1080);

        setOnMouseEntered(event -> setStyle("-fx-background-color: #589ce4; -fx-background-radius: 8px; -fx-border-width: 5px; -fx-border-radius: 6px; -fx-border-color: #f4fcfc"));
        setOnMouseExited(event -> setStyle("-fx-background-color: #589ce4; -fx-background-radius: 8px; -fx-border-width: 5px; -fx-border-radius: 6px; -fx-border-color: #90d4fc;"));
        if (randomMap) {
            setText("JOUER AVEC MAP ALEATOIRE");
        } else {
            setText("JOUER");
        }
        setFont(Style.titleFont);
        setAlignment(Pos.CENTER);

        setOnMouseClicked(event -> GameStates.MENU.changeState(GameStates.ADDPLAYERS.getState()));
    }
}
