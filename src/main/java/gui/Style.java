package gui;

import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import model.Direction;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Style {
    public static final int DESACTIVATED = 0;
    public static final int ACTIVATED = 1;
    public static final int LIMITREACHED = 2;

    public static final int bigNumberSize = 30;
    public static final int titleSize = 25;
    public static final int paragraphSize = 16;
    public static final int lightSize = 12;

    public static String emptyCaseMessage = "Sélectionner une case pour avoir des informations.";

    public static final Font bigNumberFont = Font.loadFont(Style.class.getResourceAsStream("/Fonts/FjallaOne.ttf"), bigNumberSize);
    public static final Font titleFont = Font.loadFont(Style.class.getResourceAsStream("/Fonts/FjallaOne.ttf"), titleSize);
    public static final Font paragraphFont = Font.loadFont(Style.class.getResourceAsStream("/Fonts/FjallaOne.ttf"), paragraphSize);
    public static final Font lightFont = Font.loadFont(Style.class.getResourceAsStream("/Fonts/FjallaOne.ttf"), lightSize);
    public static Image bluePlayer = loadImage("/Players/BluePlayer.png");
    public static Image greenPlayer = loadImage("/Players/GreenPlayer.png");
    public static Image orangePlayer = loadImage("/Players/OrangePlayer.png");
    public static Image purplePlayer = loadImage("/Players/PurplePlayer.png");
    public static Image redPlayer = loadImage("/Players/RedPlayer.png");

    public static Image northArrow = loadImage("/Map/DirectionArrows/UpArrow.png");
    public static Image southArrow = loadImage("/Map/DirectionArrows/DownArrow.png");
    public static Image eastArrow = loadImage("/Map/DirectionArrows/EastArrow.png");
    public static Image westArrow = loadImage("/Map/DirectionArrows/WestArrow.png");
    public static Image stopIcon = loadImage("/Map/DirectionArrows/StopIcon.png");


    public static Image startCell = loadImage("/Map/StartCell.png");
    public static Image taxCell = loadImage("/Map/TaxeIcon.png");
    public static Image teleport = loadImage("/Map/TeleportCell.png");

    public static Image lucky = loadImage("/Map/Lucky.png");
    public static Image toJail = loadImage("/Map/ToJail.png");
    public static Image jail = loadImage("/Map/Jail.png");

    public static Image backgroundImage = loadImage("/Menu/menuBG.png");
    public static Image monopolyManImage = loadImage("/Menu/monopoly-man.png");
    public static Image logoImage = loadImage("/Menu/monopoly-logo.png");
    public static Image[] level0Images = {loadImage("/Buildings/Level0.png"), loadImage("/Buildings/Level0Buy.png"), loadImage("/Buildings/Level0BuyHover.png")};
    public static Image[] level0bImages = {loadImage("/Buildings/Level0b.png"), loadImage("/Buildings/Level0bBuy.png"), loadImage("/Buildings/Level0bBuyHover.png")};
    public static Image volcano = loadImage("/Events/VolcanoGif.gif");
    public static Image meteor = loadImage("/Events/MeteorGif.gif");
    public static Image tsunami = loadImage("/Events/TsunamiGif.gif");

    //CHARGER LES BUILDINGS
    static String[] levels = {"L1","L2","L3","L4"};
    static String[] directions = {"North", "South", "East", "West"};

    public static Image[][] imagesBlue = loadBuidling("Blue");
    public static Image[][] imagesDestroy = loadBuidling("Destroy");
    public static Image[][] imagesGreen = loadBuidling("Green");
    public static Image[][] imagesPurple = loadBuidling("Purple");
    public static Image[][] imagesOrange = loadBuidling("Orange");
    public static Image[][] imagesRed = loadBuidling("Red");


    public static Image[][] loadBuidling(String color){
        Image[][] imageByDirection = new Image[levels.length][directions.length];
        // Parcourez chaque niveau
        for (int i = 0; i < levels.length; i++){
        String level = levels[i];
            // Parcourez chaque couleur
            for (int j = 0; j < directions.length; j++) {
                String direction = directions[j];
                String nomFichier = color+"/"+level + "_" + direction+"_"+ color + ".png";
                String cheminFichier = "/Buildings/"+ nomFichier;
                //Vérifi si le fichier existe
                Image image = loadImage(cheminFichier);
                imageByDirection[i][j] = image;
            }
        }
        return imageByDirection;
    }

    public static void addImageToBackground(Pane pane, Image image){
        BackgroundSize bgSize = new BackgroundSize( 1000, 1000, true, true, true, true);
        BackgroundImage bgImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, bgSize);
        Background bg = new Background(bgImage);
        pane.setBackground(bg);
    }

    public static int directionToInt(Direction direction){
        return switch (direction) {
            case NORTH -> 0;
            case SOUTH -> 1;
            case EAST -> 2;
            case WEST -> 3;
            default -> -1;
        };
    }


    public static void bigButtonSize(Button button){
        button.setPrefSize(326, 81);
    }

    public static void littleButtonSize(Button button){
        button.setPrefSize(70, 30);
    }
    /**
     * Permet de charger une image dans l'application.
     * @param imagePath Emplacement de l'image dans le package resources.
     */
    public static Image loadImage(String imagePath){
        try(InputStream inputStream = Style.class.getResourceAsStream(imagePath)){
            return new Image(inputStream);
        }
        catch (IOException e){
            System.out.println("C'EST RATE :( " + imagePath);
            e.printStackTrace();
            return null;
        }
    }



    public static void putListenerInButton(Button button){
        button.disabledProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) Style.styleButton(button, Style.DESACTIVATED);
            else Style.styleButton(button, Style.ACTIVATED);
        });
    }

    public static void putListenerAndStyleToButton(Button button, int value){
        Style.putListenerInButton(button);
        Style.styleButton(button, value);
        button.setFont(Style.paragraphFont);
    }

    public static Image getLevel0WithVariant(boolean isVariant, int pos){
        if(isVariant) return level0Images[pos];
        return level0bImages[pos];
    }

    public static void styleButton(Button button, int value){
        if(value == DESACTIVATED){
            button.setStyle("-fx-background-color: #B7B7B7; -fx-background-radius: 8px; -fx-border-width: 5px; -fx-border-radius: 6px; -fx-border-color: #DADADA;");

            button.setOnMouseEntered(e-> {
                button.setStyle("-fx-background-color: #B7B7B7; -fx-background-radius: 8px; -fx-border-width: 5px; -fx-border-radius: 6px; -fx-border-color: #DADADA;");
            });
            button.setOnMouseExited(e -> {
                button.setStyle("-fx-background-color: #B7B7B7; -fx-background-radius: 8px; -fx-border-width: 5px; -fx-border-radius: 6px; -fx-border-color: #DADADA;");
            });
        }
        else if(value == LIMITREACHED){
            button.setStyle("-fx-background-color: #FFD78B; -fx-background-radius: 8px; -fx-border-width: 5px; -fx-border-radius: 6px; -fx-border-color: #FFB72D;");

            button.setOnMouseEntered(e -> {
                button.setStyle("-fx-background-color: #FFD78B; -fx-background-radius: 8px; -fx-border-width: 5px; -fx-border-radius: 6px; -fx-border-color: #FFE7B8;");
            });
            button.setOnMouseExited(e -> {
                button.setStyle("-fx-background-color: #FFD78B; -fx-background-radius: 8px; -fx-border-width: 5px; -fx-border-radius: 6px; -fx-border-color: #FFB72D;");
            });
        }
        else{
            button.setStyle("-fx-background-color: #589ce4; -fx-background-radius: 8px; -fx-border-width: 5px; -fx-border-radius: 6px; -fx-border-color: #90d4fc;");

            button.setOnMouseEntered(e -> {
                button.setStyle("-fx-background-color: #589ce4; -fx-background-radius: 8px; -fx-border-width: 5px; -fx-border-radius: 6px; -fx-border-color: #f4fcfc;");
            });
            button.setOnMouseExited(e -> {
                button.setStyle("-fx-background-color: #589ce4; -fx-background-radius: 8px; -fx-border-width: 5px; -fx-border-radius: 6px; -fx-border-color: #90d4fc;");
            });
        }
    }

    public static Background skyBackground(){
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
}
