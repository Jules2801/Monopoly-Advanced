package view.ui;

import gui.App;
import gui.Style;
import javafx.geometry.Pos;
import javafx.scene.control.Label;

public class QuitButton extends Label {

    public final double prefWidth = 163*2;
    public final double prefHeight = 81;

    public QuitButton(){
        super();
        setStyle("-fx-background-color: #589ce4; -fx-background-radius: 8px; -fx-border-width: 5px; -fx-border-radius: 6px; -fx-border-color: #90d4fc; -fx-text-alignment: center;");
        setPrefSize(163, 81);

        setOnMouseEntered(event -> setStyle("-fx-background-color: #589ce4; -fx-background-radius: 8px; -fx-border-width: 5px; -fx-border-radius: 6px; -fx-border-color: #f4fcfc"));
        setOnMouseExited(event -> setStyle("-fx-background-color: #589ce4; -fx-background-radius: 8px; -fx-border-width: 5px; -fx-border-radius: 6px; -fx-border-color: #90d4fc;"));

        setText("QUITTER");
        setAlignment(Pos.CENTER);
        setFont(Style.titleFont);

        double newWidth = App.primaryStage.getWidth();
        setPrefWidth((newWidth * prefWidth) / 1260);

        double newHeight = App.primaryStage.getHeight();
        setPrefHeight((newHeight * prefHeight) / 1080);

        setOnMouseClicked(event -> System.exit(0));
    }
}
