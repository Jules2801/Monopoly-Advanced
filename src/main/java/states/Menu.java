package states;

import gui.App;
import gui.GameStates;
import gui.Style;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import view.ui.PlayButton;
import view.ui.QuitButton;
import view.ui.SaveButton;

import java.time.LocalTime;

public class Menu extends State{
    public static Menu INSTANCE = new Menu();
    protected ImageView logo;
    protected ImageView monopolyMan;

    public Menu(){
        super("Menu");
    }

    public static Menu getInstance(){return INSTANCE;}

    public void enter(){
        getPane().getChildren().add(new Label("Menu écran"));

        Image backgroundImage = Style.backgroundImage;

        Image logoImage = Style.logoImage;
            logo = new ImageView(logoImage);
            logo.setPreserveRatio(true);
            logo.setSmooth(true);

        Image monopolyManImage = Style.monopolyManImage;
            monopolyMan = new ImageView(monopolyManImage);
            monopolyMan.setFitWidth(App.primaryStage.getWidth());
            monopolyMan.setFitHeight(App.primaryStage.getHeight());

        BackgroundSize bgSize = new BackgroundSize( 100, 100, true, true, true, true);
        BackgroundImage bgImage = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, bgSize);
        Background bg = new Background(bgImage);

        BorderPane leftPane = new BorderPane();

        GridPane centerPane = new GridPane();

                ColumnConstraints column1 = new ColumnConstraints();
                column1.setPercentWidth(100);
                centerPane.getColumnConstraints().add(column1);

                RowConstraints row1 = new RowConstraints();
                row1.setPercentHeight(50);
                RowConstraints row2 = new RowConstraints();
                row2.setPercentHeight(50);
                centerPane.getRowConstraints().addAll(row1, row2);

                BorderPane logoPane = new BorderPane();
                    logoPane.setCenter(logo);

                BorderPane buttonPane = new BorderPane();
                    VBox buttonBox = new VBox(10);
                    buttonBox.setAlignment(Pos.CENTER);
                        PlayButton playButton = new PlayButton(false);
                        SaveButton settingButton = new SaveButton();
                        QuitButton quitButton = new QuitButton();

                        buttonBox.getChildren().addAll(playButton ,settingButton, quitButton);

                    buttonPane.setCenter(buttonBox);

            centerPane.add(logoPane, 0, 0);
            centerPane.add(buttonPane, 0, 1);


        BorderPane rightPane = new BorderPane();

        Button gameplayButton = new Button("Go to Gameplay :");
        gameplayButton.setOnAction(e -> GameStates.MENU.changeState(GameStates.GAMEPLAY.getState()));

        StackPane backgroundColor = new StackPane();
        LocalTime currentTime = LocalTime.now();

        backgroundColor.setBackground(skyBackground());
        if(currentTime.isAfter(LocalTime.of(17, 0))) backgroundColor.setBackground(twilightBackground());

        backgroundColor.getChildren().add(getPane());
        getPane().setBackground(bg);


        getPane().setLeft(leftPane);
        getPane().setCenter(centerPane);
        getPane().setRight(rightPane);

        double nWidth = App.primaryStage.getWidth();
        double sWidth = nWidth * 0.2;
        leftPane.setPrefWidth(sWidth);
        rightPane.setPrefWidth(sWidth);
        centerPane.setPrefWidth(nWidth*0.6);

        double nHeight = App.primaryStage.getHeight();

        logoPane.setPrefHeight(nHeight / 2);
        logo.setFitWidth(nHeight * 0.5);
        buttonPane.setPrefHeight(nHeight / 2);


        getGameRoot().setCenter(backgroundColor);
        // Ajout des évènements permettant de changer la taille des components dynamiquement
        App.primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            double newWidth = newVal.doubleValue();
            double sideWidth = newWidth * 0.2;

            leftPane.setPrefWidth(sideWidth);
            rightPane.setPrefWidth(sideWidth);
            centerPane.setPrefWidth(newWidth * 0.6);

            playButton.setPrefWidth((newWidth * playButton.prefWidth / 1260));
            settingButton.setPrefWidth((newWidth * settingButton.prefWidth / 1260));
            quitButton.setPrefWidth((newWidth * quitButton.prefWidth / 1260));
        });

        App.primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            double newHeight = newVal.doubleValue();

            logoPane.setPrefHeight(newHeight / 2);
            logo.setFitWidth(newHeight * 0.5);
            buttonPane.setPrefHeight(newHeight / 2);

            playButton.setPrefHeight((newHeight * playButton.prefHeight / 1080));
            settingButton.setPrefHeight((newHeight * settingButton.prefHeight / 1080));
            quitButton.setPrefHeight((newHeight * quitButton.prefHeight / 1080));
        });
    }

    public void exit(){
    }
    /**
     * Permet de changer la couleur de l'arrière plan en jour.
     */
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

    /**
     * Permet de changer la couleur de l'arrière plan en crépuscule.
     */
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

    public void transitionTo(State s){System.out.println("Transitioning from Menu to " + s.showState());}
}
