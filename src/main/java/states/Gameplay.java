package states;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import gui.Style;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import controller.GameplayController;
import javafx.animation.*;
import gui.App;
import javafx.event.ActionEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import model.*;
import model.Cards.*;
import model.Cell.*;
import model.Cell.Franchise.Heritage.Heritage;
import model.Cell.Franchise.Restaurants.*;
import model.Geometry.Coordinate;
import javafx.scene.shape.*;
import model.Tools.Observer;

public class Gameplay extends State {


    public List<Rectangle> playerRectangles = new ArrayList<>();
    public Image[] playerImages = {Style.bluePlayer,Style.greenPlayer,Style.orangePlayer,Style.redPlayer,Style.purplePlayer};
    public Color[] colors = {Color.BLUE,Color.GREEN,Color.ORANGE,Color.RED,Color.MEDIUMPURPLE};


    //Attribut nécessaire pour l'initalisation de l'état
    public static Gameplay INSTANCE = new Gameplay();
    public final GameplayController controller = new GameplayController(this);


    //Les Panes nécessaires pour l'affichage du jeu
    BorderPane mainPane = new BorderPane();
    public BorderPane centerPane = new BorderPane();
    public VBox rightBox = new VBox(30);
    public VBox leftBox = new VBox(30);
    public StackPane overlayPane = new StackPane();

    //Les listes nécessaires pour l'accessibilité des cases du jeux.
    public BorderPane[][] cellOnGraphics;
    public BorderPane[][] informationOnGraphics;
    public ArrayList<Card> allBonus = controller.setAllBonus();

    //Tous les attributs et boutons nécessaires pour le bon fonctionnement du jeu
    public Button skip = new Button("Passer au joueur suivant");
    public Button playableCardButton = new Button("Cartes à jouer");
    public Button testEvent = new Button("Lancer une catastrophe");

    public Timeline timeRemain = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            timeRemaining.setText(seconds + " secondes");
            seconds--;
        }
    }));
    public Button testSave = new Button("Enregistrer la partie");

    //Toutes les fonctionnalités non visibles important pour le jeu
    public List<Label> playerPriceLabel;
    public int[] price = {1, 1000, 1000000, 1000000000};
    public int seconds;
    public String[] modPrice = {"", "k", "M", "B"};
    public Label gameMessageLabel = new Label();
    private Text roundLabel = new Text("Round: 1");
    private VBox leaderboard = new VBox(15);
    public Text stepsLabel = new Text("Aucun pas restant.");
    public Text timeRemaining = new Text("Aucun");
    public Label descriptionLabel = new Label("Sélectionner une case pour avoir des informations.");


    //Méthodes permettant d'initialiser l'état

    public Gameplay(){
        super("Gameplay");
    }

    public static Gameplay getInstance(){return INSTANCE;}

    public void enter() {
        Style.putListenerInButton(skip);
        skip.setFont(Style.paragraphFont);

        Style.putListenerInButton(playableCardButton);
        playableCardButton.setFont(Style.paragraphFont);

        Style.putListenerInButton(testSave);
        testSave.setFont(Style.paragraphFont);

        controller.setGameModel();

        BorderPane rightPane = new BorderPane();
        rightPane.setPrefWidth(App.root.getWidth() * 0.2);

        descriptionLabel.setWrapText(true);
        descriptionLabel.setStyle("-fx-text-alignment: center; -fx-padding: 30 30 30 30px;");
        descriptionLabel.setFont(Style.paragraphFont);
        descriptionLabel.setPrefSize(300, 200);

        VBox information = new VBox(15);
        information.setAlignment(Pos.CENTER);
        descriptionLabel.setAlignment(Pos.CENTER);
        descriptionLabel.setPadding(new Insets(10, 10, 10, 10));
        VBox timeRemainingBox = new VBox(15);
        timeRemainingBox.setAlignment(Pos.CENTER);

        Text timeRemainingText = new Text("Temps restants : ");
        timeRemainingText.setFont(Style.titleFont);
        timeRemainingText.setTextAlignment(TextAlignment.CENTER);

        timeRemaining.setFont(Style.paragraphFont);
        timeRemainingBox.getChildren().addAll(timeRemainingText, timeRemaining);

        Label informationLabel = new Label("Information : ");
        informationLabel.setFont(Style.titleFont);

        information.getChildren().addAll(informationLabel, descriptionLabel);

        VBox rightContent = new VBox(15);
        rightContent.setAlignment(Pos.CENTER);


        gameMessageLabel.setStyle("-fx-font-size: 16px; -fx-padding: 10;");

        roundLabel.setText("Round " + controller.getRound());
        roundLabel.setTextAlignment(TextAlignment.CENTER);

        roundLabel.setStyle("-fx-text-alignment: center;");
        roundLabel.setFont(Style.titleFont);

        controller.setMapOnGraphics(centerPane);
        setPlayerGraphic(centerPane);

        rightContent.getChildren().add(0, roundLabel);


        rightBox.setAlignment(Pos.CENTER);

        BorderPane test = new BorderPane();
        rightPane.setBottom(test);
        test.setBottom(skip);
        //test.setTop(testEvent);
        test.setCenter(testSave);
        rightBox.getChildren().addAll(roundLabel, information, timeRemainingBox, skip/*, test*/);

        rightPane.setCenter(rightBox);
        rightPane.setBackground(Style.skyBackground());

        skip.setDisable(true);
        playableCardButton.setDisable(true);

        /*testEvent.setOnAction((event -> {
            activateEvent();
        }));*/

        testSave.setOnAction((event-> {
            controller.saveGame();
        }));



        BorderPane leftPane = new BorderPane();
        leftPane.setPrefWidth(App.root.getWidth() * 0.2);

        leftBox.setAlignment(Pos.CENTER);


        leaderboard.setAlignment(Pos.CENTER);
        leaderboard.setStyle("-fx-padding: 15 0 0 0;");

        Label leaderboardLabel = new Label("Classement : ");
        leaderboardLabel.setFont(Style.titleFont);

        leaderboard.getChildren().add(leaderboardLabel);
        leaderboard.getChildren().addAll(controller.getPlayerHBox());

        VBox stepsRemainingBox = new VBox(25);
        Text stepsRemainingTitle = new Text("Nombre de pas restant :");

        stepsRemainingTitle.setStyle("-fx-text-alignment: center;");
        stepsRemainingTitle.setFont(Style.titleFont);
        stepsRemainingBox.setAlignment(Pos.CENTER);

        stepsRemainingBox.getChildren().addAll(stepsRemainingTitle, stepsLabel);

        stepsLabel.setStyle("-fx-text-alignment: center;");
        stepsLabel.setFont(Style.paragraphFont);

        playableCardButton.setOnAction((event) -> controller.setPlayableCardButtonAction());

        leftBox.getChildren().addAll(leaderboard, stepsRemainingBox, playableCardButton);

        leftPane.setCenter(leftBox);
        leftPane.setBackground(Style.skyBackground());


        centerPane.setStyle("-fx-border-width: 5px; -fx-border-color: black;");


        mainPane.setCenter(centerPane);
        mainPane.setRight(rightPane);
        mainPane.setLeft(leftPane);

        overlayPane.getChildren().addAll(mainPane);
        getPane().setCenter(overlayPane);

        getGameRoot().setCenter(getPane());

        controller.setNewRound();
        controller.updateLeaderboard();
        controller.setPriceLabel();

        controller.updateLeaderboard();

        App.primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            double newWidth = newVal.doubleValue();
            double sideWidth = newWidth * 0.2;

            leftPane.setPrefWidth(sideWidth);
            rightPane.setPrefWidth(sideWidth);
            centerPane.setPrefWidth(newWidth * 0.6);
        });
    }

    public void setPopupBoxSize(VBox box){
        box.setMinWidth(150);
        box.setMinHeight(100);

        box.setPrefWidth(200);
        box.setPrefHeight(150);

        box.setMaxWidth(250);
        box.setMaxHeight(200);
    }

    public void setCardBoxSize(VBox box){
        box.setMinWidth(300);
        box.setMinHeight(400);

        box.setPrefWidth(350);
        box.setPrefHeight(450);

        box.setMaxWidth(400);
        box.setMaxHeight(500);
    }

    public void setImageBoxSize(VBox box){
        box.setMinWidth(200);
        box.setMinHeight(80);

        box.setPrefWidth(250);
        box.setPrefHeight(130);

        box.setMaxWidth(300);
        box.setMaxHeight(180);
    }



    public HBox makePlayerCardBox(Player player, BorderPane pane){
        HBox playableCardDisplay = new HBox(70);
        playableCardDisplay.setAlignment(Pos.CENTER);

        if(!player.getCards().isEmpty()) playableCardDisplay.getChildren().add(makeCardBox(player, player.getCards().get(0), pane));
        else playableCardDisplay.getChildren().add(makeCardBox(player, null, pane));

        if(!player.getCards().isEmpty() && player.getCards().size() >= 2) playableCardDisplay.getChildren().add(makeCardBox(player, player.getCards().get(1), pane));
        else playableCardDisplay.getChildren().add(makeCardBox(player, null, pane));

        if(!player.getCards().isEmpty() && player.getCards().size() >= 3) playableCardDisplay.getChildren().add(makeCardBox(player, player.getCards().get(2), pane));
        else playableCardDisplay.getChildren().add(makeCardBox(player, null, pane));

        return playableCardDisplay;
    }

    public HBox makeBonusCardBox(Player player, BorderPane pane){
        Random r = new Random();
        HBox bonusCardDisplay = new HBox(70);
        bonusCardDisplay.setAlignment(Pos.CENTER);

        ArrayList<Observer> observers = new ArrayList<>();

        bonusCardDisplay.getChildren().add(makeBonusBox(player, allBonus.get(r.nextInt(100)%allBonus.size()), pane, observers));

        bonusCardDisplay.getChildren().add(makeBonusBox(player, allBonus.get(r.nextInt(100)%allBonus.size()), pane, observers));

        bonusCardDisplay.getChildren().add(makeBonusBox(player, allBonus.get(r.nextInt(100)%allBonus.size()), pane, observers));

        return bonusCardDisplay;
    }

    public VBox makeCardBox(Player player, Card card, BorderPane pane){
        VBox cardBox = new VBox(20);
        cardBox.setAlignment(Pos.CENTER);
        setCardBoxSize(cardBox);
        if(card == null) {
            cardBox.setStyle("-fx-border-style: dashed; -fx-border-color: white; -fx-border-width: 5px");
            Label emptyCase = new Label("Case vide");
            emptyCase.setStyle("-fx-text-fill: white;");
            emptyCase.setFont(Style.titleFont);
            cardBox.getChildren().add(emptyCase);
        }
        else{
            String color = card.getColor();
            if(!card.isPlayable) color = "grey";
            cardBox.setStyle("-fx-border-color: " + color + "; -fx-border-width: 5px; -fx-background-color: white;");

            Label caseTitleLabel = new Label(card.getNom());
            caseTitleLabel.setStyle("-fx-text-fill: black;");
            caseTitleLabel.setFont(Style.titleFont);

            VBox caseImage = new VBox(0);
            setImageBoxSize(caseImage);
            caseImage.setStyle("-fx-background-color: " + color + ";");

            Label caseDescriptionLabel = new Label(card.getDescription());
            caseDescriptionLabel.setWrapText(true);
            caseDescriptionLabel.setStyle("-fx-text-fill: black; -fx-text-alignment: center; -fx-padding: 0 20px");
            caseDescriptionLabel.setFont(Style.paragraphFont);

            cardBox.getChildren().addAll(caseTitleLabel, caseImage, caseDescriptionLabel);

            if(card.isPlayable) {
                cardBox.setOnMouseClicked((event) -> {
                    controller.applyEffect(player, card);
                    removePopupInStage(pane);
                });
            }
        }
        return cardBox;
    }

    public VBox makeBonusBox(Player player, Card card, BorderPane pane, List<Observer> observers){
        VBox cardBox = new VBox(20);
        cardBox.setAlignment(Pos.CENTER);
        setCardBoxSize(cardBox);
        cardBox.setStyle("-fx-border-color: grey; -fx-border-width: 5px; -fx-background-color: white;");

        Label caseTitleLabel = new Label("???");
        caseTitleLabel.setStyle("-fx-text-fill: black;");
        caseTitleLabel.setFont(Style.titleFont);

        VBox caseImage = new VBox(0);
        setImageBoxSize(caseImage);

        caseImage.setStyle("-fx-background-color: grey;");
        cardBox.setOnMouseEntered(e -> {
            cardBox.setStyle("-fx-border-color: #3d3d3d; -fx-border-width: 5px; -fx-background-color: white;");
            caseImage.setStyle("-fx-background-color: #3d3d3d;");
        });
        cardBox.setOnMouseExited(e -> {
            cardBox.setStyle("-fx-border-color: grey; -fx-border-width: 5px; -fx-background-color: white;");
            caseImage.setStyle("-fx-background-color: grey;");
        });

        Label caseDescriptionLabel = new Label("");
        caseDescriptionLabel.setWrapText(true);
        caseDescriptionLabel.setStyle("-fx-text-fill: black; -fx-text-alignment: center; -fx-padding: 0 20px");
        caseDescriptionLabel.setFont(Style.paragraphFont);

        cardBox.getChildren().addAll(caseTitleLabel, caseImage, caseDescriptionLabel);

        Observer obs = new Observer() {
            @Override
            public void update() {
                cardBox.setOnMouseEntered(null);
                cardBox.setOnMouseExited(null);
                cardBox.setOnMouseClicked(null);
                caseTitleLabel.setText(card.getNom());
                caseDescriptionLabel.setText(card.getDescription());
            }
        };
        observers.add(obs);

        cardBox.setOnMouseClicked(e -> {
            controller.updateObservers(observers);
            cardBox.setStyle("-fx-border-color:" + card.getColor() +"; -fx-border-width: 5px; -fx-background-color: white;");
            caseTitleLabel.setText(card.getNom());
            caseDescriptionLabel.setText(card.getDescription());
            caseImage.setStyle("-fx-background-color: " + card.getColor() + ";");
            Timeline removeBox = new Timeline(
                    new KeyFrame(Duration.seconds(3), new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            removePopupInStage(pane);
                        }
                    })
            );
            removeBox.setCycleCount(1);
            removeBox.setOnFinished(e1 -> {
                player.addCard(card);
                controller.payBeforePlay();
            });
            removeBox.play();
        });
        return cardBox;
    }

    public void exit(){
        rightBox = new VBox(30);
        leftBox = new VBox(30);

        playerRectangles = new ArrayList<>();
        playerImages = new Image[]{Style.bluePlayer, Style.greenPlayer, Style.orangePlayer, Style.redPlayer, Style.purplePlayer};
        colors = new Color[]{Color.BLUE, Color.GREEN, Color.ORANGE, Color.CYAN, Color.MEDIUMPURPLE};

        //Attribut nécessaire pour l'initalisation de l'état
        INSTANCE = new Gameplay();
        GameplayController controller = new GameplayController(this);


        //Attribut nécessaire pour la logique de l'état
        centerPane = new BorderPane();
        overlayPane = new StackPane();
        mainPane = new BorderPane();
        allBonus = controller.setAllBonus();


        skip = new Button("Passer au joueur suivant");
        playableCardButton = new Button("Cartes à jouer");


        //testEvent = new Button("Lancer une catastrophe");

        testSave = new Button("Sauvegarder la partie");



        price = new int[]{1, 1000, 1000000, 1000000000};
        modPrice = new String[]{"", "k", "M", "B"};
        gameMessageLabel = new Label();
        roundLabel = new Text("Round: 1");
        leaderboard = new VBox(15);
        stepsLabel = new Text("Aucun pas restant.");
        timeRemaining = new Text("Aucun");

        descriptionLabel = new Label("Sélectionner une case pour avoir des informations.");
    }


    public HBox playerBox(Label money) {
        HBox playerBox = new HBox(2);
        playerBox.setAlignment(Pos.CENTER);

        playerBox.getChildren().addAll(money);

        return playerBox;
    }

    /**
     * Permet d'initialiser le classement des joueurs dans le Leaderboard.
     */

    /**
     * Permet d'avoir un affichage propre de la monnaie.
     * @param m Entier correspondant à la monnaie..
     */
    public String getModuloMoney(int m){
        int i = 0;
        while(m >= price[i+1] && i <= price.length - 2) i++;
        if(i >= 4) i = 3;
        if (i > 0 && m % price[i] != 0) {
            double money = (double) m / price[i];
            return String.format("%.1f%s€", money, modPrice[i]);
        } else {
            return m/price[i] + modPrice[i] + "€";
        }
    }

    public BorderPane makeQuickBox(String colorBackground, String title, String colorTitle, Button actionButton, AbstractCell building){
        BorderPane finalBox = new BorderPane();
        finalBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.0)");
        VBox box = new VBox(40);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #ffffff; -fx-border-color: black; -fx-border-width: 2px; -fx-background-color:" + colorBackground);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill:" + colorTitle +";");
        titleLabel.setFont(Style.titleFont);

        HBox buttonBox = getButtonsBox(actionButton, controller.getLeaveButton(finalBox, building));

        box.getChildren().addAll(titleLabel, buttonBox);
        setPopupBoxSize(box);
        finalBox.setCenter(box);
        return finalBox;
    }


    public VBox getEventBox(ImageView gif) {
        VBox eventBox = new VBox(40);
        eventBox.setAlignment(Pos.CENTER);
        eventBox.setStyle("-fx-border-width: 2px; -fx-border-color: #ffffff; -fx-background-color: #f36043;");

        gif.setPreserveRatio(true);
        gif.setFitWidth(1280);

        eventBox.getChildren().add(gif);

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(3000), evt -> {
            gif.setVisible(false);
        }));
        timeline.setCycleCount(1);
        timeline.play();

        return eventBox;
    }


    public VBox getPrisonDiceBox(Dice dice){
        VBox diceBox = new VBox(20);
        diceBox.setAlignment(Pos.CENTER);
        diceBox.setStyle("-fx-border-width: 2px; -fx-border-color: black; -fx-background-color: white;");


        Label number = new Label("?");
        Random r = new Random();
        number.setStyle("-fx-text-fill: red;");
        number.setFont(Style.bigNumberFont);
        Timeline t = new Timeline(new KeyFrame(Duration.millis(100), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                number.setText((r.nextInt(5) + 1) + " + " + (r.nextInt(5) + 1));
            }
        }));
        t.setCycleCount(Animation.INDEFINITE);
        t.play();

        Label boxText = new Label("Lancer le dès :");
        boxText.setStyle("-fx-text-fill: black;");
        boxText.setFont(Style.titleFont);

        Button throwDice = getPrisonThrowDice(number, t, dice);

        diceBox.getChildren().addAll(number, boxText, throwDice);

        setPopupBoxSize(diceBox);

        return diceBox;
    }

    public VBox getBonusDiceBox(Dice dice){
        VBox diceBox = new VBox(20);
        diceBox.setAlignment(Pos.CENTER);
        diceBox.setStyle("-fx-border-width: 2px; -fx-border-color: black; -fx-background-color: white;");


        Label number = new Label("?");
        Random r = new Random();
        number.setStyle("-fx-text-fill: red;");
        number.setFont(Style.bigNumberFont);
        Timeline t = new Timeline(new KeyFrame(Duration.millis(100), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                number.setText(allBonus.get(r.nextInt(allBonus.size())).getNom());
            }
        }));
        t.setCycleCount(Animation.INDEFINITE);
        t.play();

        Label boxText = new Label("Lancer le dès :");
        boxText.setStyle("-fx-text-fill: black;");
        boxText.setFont(Style.titleFont);

        Button throwDice = getBonusThrowDice(number, t, dice);

        diceBox.getChildren().addAll(number, boxText, throwDice);

        return diceBox;
    }

    public VBox getDiceBox(){
        VBox diceBox = new VBox(20);
        diceBox.setAlignment(Pos.CENTER);
        diceBox.setStyle("-fx-border-width: 2px; -fx-border-color: black; -fx-background-color: white;");


        Label number = new Label("?");
        Random r = new Random();
        number.setStyle("-fx-text-fill: red;");
        number.setFont(Style.bigNumberFont);
        Timeline t = new Timeline(new KeyFrame(Duration.millis(100), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                number.setText((r.nextInt(11) + 1) + "");
            }
        }));
        t.setCycleCount(Animation.INDEFINITE);
        t.play();

        Label boxText = new Label("Lancer le dès :");
        boxText.setStyle("-fx-text-fill: black;");
        boxText.setFont(Style.titleFont);

        Button throwDice = getThrowDice(number, t);

        diceBox.getChildren().addAll(number, boxText, throwDice);

        setPopupBoxSize(diceBox);

        return diceBox;
    }

    public String convertColorToString(Color s){
        String res = s.toString();
        return res.substring(2);
    }

    public HBox getButtonsBox(Button firstButton, Button secondButton){
        HBox buttonsBox = new HBox(5);
        buttonsBox.getChildren().addAll(firstButton, secondButton);
        buttonsBox.setAlignment(Pos.CENTER);
        return buttonsBox;
    }

    public BorderPane getBuyBox(Building building, Player player, Observer observers){
        BorderPane[] box = new BorderPane[1];
        Button buyButton = getBuyButton(building, player, box, observers);

        BorderPane buyBox = makeQuickBox("cyan", ("A vendre : " + building.getPrice() + "€"), "red", buyButton, building);

        box[0] = buyBox;
        return buyBox;
    }

    public BorderPane getTakeableBox(Building building, Player player, Observer obs){
        BorderPane[] box = new BorderPane[1];
        Button takeButton = controller.getTakeButton(building, player, box, obs);

        BorderPane takeableBox = makeQuickBox("#C1FFBD", "Prendre ce bien ?", "black", takeButton, building);

        box[0] = takeableBox;
        return takeableBox;
    }

    public BorderPane getRepareBox(Building building, Player player){
        BorderPane[] box = new BorderPane[1];
        Button repareButton = controller.getRepareButton(building, player, box);

        BorderPane repareBox = makeQuickBox("#efd003", ("Réparations: " + building.getReparationsPrice()+"€"), "#ff5900", repareButton, building);

        box[0] = repareBox;
        return repareBox;
    }

    public BorderPane getUpgradeBox(Building building, Player player){
        BorderPane[] box = new BorderPane[1];
        Button upgradeButton = controller.getUpgradeButton(building, player, box);

        BorderPane upgradeBox = makeQuickBox("pink", ("Amélioration : " + building.getUpdatePrice() + "$"), "red", upgradeButton, building);

        box[0] = upgradeBox;
        return upgradeBox;
    }

    public BorderPane makeGardenBox(Garden garden, Player player, Observer observers){
        BorderPane[] box = new BorderPane[1];
        Button buyButton = getBuyGardenButton(garden, player, box, observers);

        BorderPane buyGardenBox = makeQuickBox("green", ("Prix du Jardin : " + garden.getPrice() + "$"), "white", buyButton, garden);
        box[0] = buyGardenBox;
        return buyGardenBox;

    }

    public Button getBuyGardenButton(Garden garden, Player player, BorderPane[] box, Observer obs){
        Button button = new Button("Acheter");
        Style.putListenerInButton(button);
        button.setFont(Style.paragraphFont);
        button.setDisable(true);
        if(garden.getPrice() <= player.getMoney()) button.setDisable(false);

        button.setOnAction((event->{
            timeRemain.setCycleCount(seconds+1);
            timeRemain.play();
            player.pay(garden.getPrice());
            controller.updateLeaderboard();
            button.setDisable(true);

            Coordinate c = garden.getCoordinate();

            Building b = garden.bestBuilding(player);
            garden.linkTo(b);

            informationOnGraphics[c.getY()][c.getX()].setOnMouseEntered(e -> descriptionLabel.setText(garden.description()));
            informationOnGraphics[c.getY()][c.getX()].setOnMouseExited(e -> descriptionLabel.setText(Style.emptyCaseMessage));

            obs.update();
            removePopupInStage(box[0]);
        }));
        return button;
    }


    public Button getBuyButton(Building building, Player player, BorderPane[] box, Observer observers){
        Button button = new Button("Acheter");
        Style.putListenerAndStyleToButton(button, Style.ACTIVATED);
        if(player.getMoney() < building.getPrice()) button.setDisable(true);

        button.setOnAction((event -> {
            timeRemain.setCycleCount(seconds+1);
            timeRemain.play();
            button.setDisable(true);
            Coordinate c = building.getCoordinate();

            building.addOwner(player);
            controller.updateLeaderboard();


            Image[][] buildingImage = controller.addBuildingImagesToPlayer();

            Style.addImageToBackground(informationOnGraphics[c.getY()][c.getX()], buildingImage[building.getLevel()-1][Style.directionToInt(building.getDirection())]);
            informationOnGraphics[c.getY()][c.getX()].setOnMouseEntered(e -> {
                descriptionLabel.setText(building.description());
            });
            informationOnGraphics[c.getY()][c.getX()].setOnMouseExited(e -> descriptionLabel.setText("Sélectionner une case pour avoir des informations"));

            buildingOwned(building,player,cellOnGraphics[c.getY()][c.getX()],informationOnGraphics[c.getY()][c.getX()]);
            observers.update();


            informationOnGraphics[c.getY()][c.getX()].setOnMouseClicked(null);
            informationOnGraphics[c.getY()][c.getX()].setDisable(false);
            removePopupInStage(box[0]);
        }));
        return button;
    }

    public void buildingOwned(Building building,Player player,BorderPane pane,BorderPane infoPane){
        Coordinate c = building.getCoordinate();

        Image[][] buildingImage = controller.addBuildingImagesToPlayer(player);

        pane.setStyle("-fx-background-color: #3b3b3b;");
        Style.addImageToBackground(infoPane, buildingImage[building.getLevel()-1][Style.directionToInt(building.getDirection())]);
        infoPane.setOnMouseEntered(e -> {
            descriptionLabel.setText(building.description());
        });
        infoPane.setOnMouseExited(e -> descriptionLabel.setText("Sélectionner une case pour avoir des informations"));

    }

    public void setGardensBox(Garden garden, Player player, Observer observers){
        overlayPane.getChildren().add(makeGardenBox(garden,player,observers));
    }

    public BorderPane getTeleportChoiceBox(Player player) {
        BorderPane finalBox = new BorderPane();
        finalBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.0)");
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-border-width: 2px; -fx-border-color: black; -fx-background-color: cyan;");

        Label label = new Label("Téléportez vous !");
        label.setStyle("-fx-text-fill: black;");
        label.setFont(Style.titleFont);

        Button payButton = new Button("200$");
        Style.putListenerAndStyleToButton(payButton, Style.DESACTIVATED);
        payButton.setDisable(true);
        if(player.getMoney()>=200){
            payButton.setDisable(false);
        }
        Button randomButton = new Button("Gratuit");
        Style.putListenerAndStyleToButton(randomButton, Style.ACTIVATED);
        HBox buttonsBox = getButtonsBox(payButton, randomButton);

        setPopupBoxSize(box);

        payButton.setOnAction(event -> {
            if (player.pay(200)) {
                controller.updateLeaderboard();
                overlayPane.getChildren().remove(finalBox);
                controller.makeSelectionableCellVisible();
            }
        });


        randomButton.setOnAction(event -> {
            overlayPane.getChildren().remove(finalBox);
            controller.randomTeleport();
        });

        box.getChildren().addAll(label, buttonsBox);
        finalBox.setCenter(box);
        return finalBox;
    }

    public String getBuildingStyle(Player player){
        String color = "-fx-background-color: #" + convertColorToString(colors[controller.getPlayerList().indexOf(player)]) + ";" + "-fx-border-width: 2px; -fx-border-color: black; -fx-border-style: dotted;";
        return color;
    }

    public void setBuyBox(Building building, Player player, Observer observers){
        overlayPane.getChildren().add(getBuyBox(building, player, observers));
    }

    public void setTakeableBox(Building building, Player player, Observer obs){
        overlayPane.getChildren().add(getTakeableBox(building, player, obs));
    }

    public void setRepareBox(Building building,Player player, List<Observer> observers){
        overlayPane.getChildren().add(getRepareBox(building,player));
    }

    public void setUpgradeBox(Building building, Player player){
        overlayPane.getChildren().add(getUpgradeBox(building, player));
    }

    public void setTeleportChoiceBox(Player player) {
        BorderPane teleportChoiceBox = getTeleportChoiceBox(player);
        overlayPane.getChildren().add(teleportChoiceBox);
    }


    public Observer makeSelectionableCellObserver(ArrayList<AbstractCell> possibleCellsTeleport){
        Observer obs = new Observer() {
            @Override
            public void update() {
                for(AbstractCell cell : possibleCellsTeleport){
                    Coordinate c = cell.getCoordinate();
                    BorderPane infoCell = informationOnGraphics[c.getY()][c.getX()];
                    infoCell.setOpacity(1);
                    infoCell.setStyle("");

                    infoCell.setOnMouseEntered(e -> {
                        descriptionLabel.setText(cell.description());
                    });
                    infoCell.setOnMouseExited(e -> {
                        descriptionLabel.setText("Sélectionner une case pour avoir des informations.");
                    });
                    infoCell.setOnMouseClicked(null);
                    controller.payBeforePlay();
                }
            }
        };
        return obs;
    }

    public void selectionableCellVisible(Coordinate c, String cellDescription){
        BorderPane infoCell = informationOnGraphics[c.getY()][c.getX()];
        infoCell.setOpacity(0);
        infoCell.setStyle("-fx-background-color: lightgreen");
        infoCell.setOnMouseEntered(event -> {
            descriptionLabel.setText(cellDescription);
            infoCell.setOpacity(0.5);
        });
        infoCell.setOnMouseExited(event -> {
            infoCell.setOpacity(0);
        });
    }

    public void makeBuyableBuildingVisible(List<Building> list){
        for(Building b : list){
            Coordinate c = b.getCoordinate();
            BorderPane cell = cellOnGraphics[c.getY()][c.getX()];
            BorderPane infoCell = informationOnGraphics[c.getY()][c.getX()];
            //cell.setStyle("-fx-border-color: lightgreen; -fx-border-width: 2px; -fx-background-color: #3b3b3b;");
            Style.addImageToBackground(infoCell, Style.getLevel0WithVariant(b.isVariant(),1));
            infoCell.setOnMouseEntered(event -> {
                descriptionLabel.setText(b.description());
                Style.addImageToBackground(infoCell, Style.getLevel0WithVariant(b.isVariant(),2));
            });
            infoCell.setOnMouseExited(event -> {
                //cell.setStyle("-fx-border-color: lightgreen; -fx-border-width: 2px; -fx-background-color: #3b3b3b;");
                Style.addImageToBackground(infoCell, Style.getLevel0WithVariant(b.isVariant(),1));
            });
        }
    }

    public void makeNearRestaurantVisible(List<Restaurant> list){
        for(Restaurant f : list){
            Coordinate c = f.getCoordinate();
            BorderPane cell = cellOnGraphics[c.getY()][c.getX()];
            BorderPane infoCell = informationOnGraphics[c.getY()][c.getX()];

            cell.setStyle("-fx-border-color: lightgreen; -fx-background-color: " + f.color);
            infoCell.setOnMouseEntered(event -> {
                descriptionLabel.setText(f.description());
                cell.setStyle("-fx-border-color: #499f56");
            });
            infoCell.setOnMouseExited(event -> {
                cell.setStyle("-fx-border-color: lightgreen;");
            });
        }
    }

    public void makeNearHeritageVisible(List<Heritage> list){
        for(Heritage f : list){
            Coordinate c = f.getCoordinate();
            BorderPane cell = cellOnGraphics[c.getY()][c.getX()];
            BorderPane infoCell = informationOnGraphics[c.getY()][c.getX()];

            cell.setStyle("-fx-border-color: lightgreen; -fx-background-color: " + f.color + ";");
            infoCell.setOnMouseEntered(event -> {
                descriptionLabel.setText(f.description());
                cell.setStyle("-fx-border-color: #499f56; -fx-background-color: " + f.color + ";");
            });
            infoCell.setOnMouseExited(event -> {
                cell.setStyle("-fx-border-color: lightgreen; -fx-background-color: " + f.color + ";");
            });
        }
    }

    public void makeUpgradableBuildingVisible(List<Building> list){
        for(Building b : list){
            Coordinate c = b.getCoordinate();
            BorderPane cell = cellOnGraphics[c.getY()][c.getX()];
            BorderPane infoCell = informationOnGraphics[c.getY()][c.getX()];

            cell.setStyle("-fx-border-width: 2px; -fx-border-color: lightgreen; -fx-background-color: #3b3b3b;");
            infoCell.setOnMouseEntered(event -> {
                descriptionLabel.setText(b.description());
                cell.setStyle("-fx-border-width: 2px; -fx-border-color: #499f56; -fx-background-color: #3b3b3b;");
            });
            infoCell.setOnMouseExited(event -> {
                cell.setStyle("-fx-border-width: 2px; -fx-border-color: lightgreen; -fx-background-color: #3b3b3b;");
            });
        }
    }

    public void makeMortagedBuildingVisible(ArrayList<Building> list){
        for(Building b : list){
            Coordinate c = b.getCoordinate();
            BorderPane cell = cellOnGraphics[c.getY()][c.getX()];
            BorderPane infoCell = informationOnGraphics[c.getY()][c.getX()];

            cell.setStyle("-fx-border-width: 2px; -fx-border-color: red; -fx-background-color: #3b3b3b;");
            infoCell.setOnMouseEntered(event -> {
                descriptionLabel.setText(b.description());
                cell.setStyle("-fx-border-width: 2px; -fx-border-color: #a20505; -fx-background-color: #3b3b3b;");
            });
            infoCell.setOnMouseExited(event -> {
                cell.setStyle("-fx-border-width: 2px; -fx-border-color: red; -fx-background-color: #3b3b3b;");
            });
        }
    }

    public void removeDirection(){
        for(int y=0;y<App.gameModel.getMap().getWidth();y++){
            for(int x=0;x<App.gameModel.getMap().getHeight();x++){
                if(informationOnGraphics[y][x].getCenter() instanceof ImageView){
                    informationOnGraphics[y][x].setCenter(null);
                }
            }
        }
    }

    public void showDestroy() {
        for (int y = 0; y < App.gameModel.getMap().getHeight(); y++) {
            for (int x = 0; x < App.gameModel.getMap().getWidth(); x++) {
                Cell cell = App.gameModel.getMap().getCell(x, y);
                cellDestroy(cell,x,y,cellOnGraphics[y][x],informationOnGraphics[y][x]);
                }
            }


        }

    public void cellDestroy(Cell cell,int x,int y,BorderPane pane,BorderPane infoPane) {
        if (cell instanceof Building && ((Building) cell).isDestroy()) {
            Building b = (Building) cell;
            //String playerColor = convertColorToString(colors[controller.getPlayerList().indexOf(((Building) cell).getOwner())]);
            String color = "-fx-border-width: 2px; -fx-border-color: #3b3b3b; -fx-border-style: dotted;";
            pane.setStyle("-fx-background-color: #3b3b3b;");
            infoPane.setOnMouseEntered(e -> {
                descriptionLabel.setText(cell.description());
            });
            Style.addImageToBackground(infoPane, Style.imagesDestroy[b.getLevel()-1][Style.directionToInt(b.getDirection())]);
        }

    }


    public void showBuyableGarden(ArrayList<Garden> gardens){
        for(Garden garden : gardens){
            int x = garden.getCoordinate().getX();
            int y = garden.getCoordinate().getY();
            Pane c = cellOnGraphics[y][x];
            Pane infocell = informationOnGraphics[y][x];
            c.setStyle("-fx-background-color: #2e9531;-fx-border-color: lightgreen");
            infocell.setOnMouseEntered(event -> {
                c.setStyle("-fx-background-color: #65bb67;-fx-border-color: lightgreen");
            });
            infocell.setOnMouseExited(event -> {
                c.setStyle("-fx-background-color: #2e9531;-fx-border-color: lightgreen");
            });

        }
    }

    public void showDirection(List<Direction>directions,Player current,int remainingMoves,Observer obs, AtomicInteger transitionsCompleted, int currentPlayerIndex){
        Coordinate coordinate  = current.getCoordinate();

        Random random = new Random();
        int chanceOfStopNorth = random.nextInt(100 + 1) + 1;
        int chanceOfStopSouth = random.nextInt(100 + 1) + 1;
        int chanceOfStopWest = random.nextInt(100 + 1) + 1;
        int chanceOfStopEast = random.nextInt(100 + 1) + 1;

        int nbDirections = directions.size();
        int nbStops = 0;

        for(Direction d : directions){

            switch (d) {
                case NORTH:
                    if (chanceOfStopNorth > 30 || (nbDirections - nbStops) == 1) {
                        ImageView imageView = new ImageView(Style.northArrow);
                        imageView.setFitHeight(50);
                        imageView.setFitWidth(50);
                        imageView.setImage(Style.northArrow);
                        informationOnGraphics[coordinate.getY()-1][coordinate.getX()].setCenter(imageView);
                        imageView.setOnMouseClicked(event -> {
                            removeDirection();
                            controller.movePlayer(current, remainingMoves, obs, transitionsCompleted, currentPlayerIndex, Direction.NORTH);
                        });
                    }else{
                        nbStops += 1;
                        ImageView imageView = new ImageView(Style.stopIcon);
                        imageView.setFitHeight(50);
                        imageView.setFitWidth(50);
                        imageView.setImage(Style.stopIcon);
                        informationOnGraphics[coordinate.getY()-1][coordinate.getX()].setCenter(imageView);
                    }
                    break;

                case SOUTH:
                    if (chanceOfStopSouth > 20 || (nbDirections - nbStops) == 1) {
                        ImageView simageView = new ImageView(Style.southArrow);
                        simageView.setFitHeight(50);
                        simageView.setFitWidth(50);
                        simageView.setImage(Style.southArrow);
                        informationOnGraphics[coordinate.getY()+1][coordinate.getX()].setCenter(simageView);
                        simageView.setOnMouseClicked(event -> {
                            removeDirection();
                            controller.movePlayer(current, remainingMoves, obs, transitionsCompleted, currentPlayerIndex, Direction.SOUTH);
                        });
                    }else{
                        nbStops += 1;
                        ImageView simageView = new ImageView(Style.stopIcon);
                        simageView.setFitHeight(50);
                        simageView.setFitWidth(50);
                        simageView.setImage(Style.stopIcon);
                        informationOnGraphics[coordinate.getY()+1][coordinate.getX()].setCenter(simageView);
                    }
                    break;

                case WEST:
                    if (chanceOfStopWest > 35 || (nbDirections - nbStops) == 1) {
                        ImageView wimageView = new ImageView(Style.westArrow);
                        wimageView.setFitHeight(50);
                        wimageView.setFitWidth(50);
                        wimageView.setImage(Style.westArrow);
                        informationOnGraphics[coordinate.getY()][coordinate.getX() - 1].setCenter(wimageView);
                        wimageView.setOnMouseClicked(event -> {
                            removeDirection();
                            controller.movePlayer(current, remainingMoves, obs, transitionsCompleted, currentPlayerIndex, Direction.WEST);
                        });
                    }else{
                        nbStops += 1;
                        ImageView wimageView = new ImageView(Style.stopIcon);
                        wimageView.setFitHeight(50);
                        wimageView.setFitWidth(50);
                        informationOnGraphics[coordinate.getY()][coordinate.getX() - 1].setCenter(wimageView);

                    }
                    break;

                case EAST:
                    if (chanceOfStopEast > 15 || (nbDirections - nbStops) == 1) {
                        ImageView eimageView = new ImageView(Style.eastArrow);
                        eimageView.setFitHeight(50);
                        eimageView.setFitWidth(50);
                        eimageView.setImage(Style.eastArrow);
                        informationOnGraphics[coordinate.getY()][coordinate.getX()+1].setCenter(eimageView);
                        eimageView.setOnMouseClicked(event -> {
                            removeDirection();
                            controller.movePlayer(current, remainingMoves, obs, transitionsCompleted, currentPlayerIndex, Direction.EAST);
                        });
                    }else{
                        nbStops += 1;
                        ImageView eimageView = new ImageView(Style.stopIcon);
                        eimageView.setFitHeight(50);
                        eimageView.setFitWidth(50);
                        informationOnGraphics[coordinate.getY()][coordinate.getX()+1].setCenter(eimageView);
                    }
                    break;
            }
        }
    }



    private Button getPrisonThrowDice(Label number, Timeline timeline, Dice prisonDice){
        Button throwDice = new Button("Lancer !");
        Style.putListenerAndStyleToButton(throwDice, Style.ACTIVATED);

        throwDice.setOnAction((event -> {
            throwDice.setDisable(true);

            boolean diceResult = prisonDice.isPerfect();
            timeline.stop();
            number.setText(prisonDice.getValue1() + " + " + prisonDice.getValue2());
            number.setStyle("-fx-text-fill: red;");
            number.setFont(Style.bigNumberFont);
            if(diceResult) number.setStyle("-fx-text-fill: green;");
        }));
        return throwDice;
    }

    private Button getBonusThrowDice(Label number, Timeline timeline, Dice bonusDice){
        Button throwDice = new Button("Lancer !");
        Style.putListenerAndStyleToButton(throwDice, Style.ACTIVATED);

        throwDice.setOnAction((event -> {
            throwDice.setDisable(true);

            bonusDice.roll();
            timeline.stop();
            number.setText(allBonus.get(bonusDice.getSum()%allBonus.size()).getNom());
            number.setStyle("-fx-text-fill: green;");
        }));
        return throwDice;
    }

    private Button getThrowDice(Label number, Timeline timeline) {
        Button throwDice = new Button("Lancer !");
        Style.putListenerAndStyleToButton(throwDice, Style.ACTIVATED);

        throwDice.setOnAction((event -> {
            throwDice.setDisable(true);

            int diceResult = controller.getDiceResult();
            timeline.stop();
            number.setText(diceResult + "");
            number.setStyle("-fx-text-fill: green;");

            roundLabel.setText("Round: " + controller.getRound());
            controller.moving(diceResult);

        }));
        return throwDice;
    }

    public void activateEvent(ImageView gif){

        VBox eventBox = getEventBox(gif);
        setPopupInStage(eventBox);

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> {
            removePopupInStage(eventBox);
            controller.setNewRound();
        });
        delay.play();
        controller.destroyBuildings();
        showDestroy();

    }

    public void setPlayerGraphic(Pane centerPane){
        for(int i=0;i<App.gameModel.getPlayers().size();i++){
            Player player = App.gameModel.getPlayers().get(i);
            ImageView image = new ImageView(playerImages[i]);
            image.setFitWidth(70);
            image.setFitHeight(70);
            ImagePattern patternImage = new ImagePattern(image.getImage());

            Rectangle playerRect =
                    new Rectangle(  (player.getCoordinate().getX()*69)+(i*10),
                            (player.getCoordinate().getY()*64.5)+(i*10),35,35);
            playerRect.setFill(patternImage);
            //playerRect.setFill(colors[i]);
            centerPane.getChildren().add(playerRect);
            playerRectangles.add(playerRect);
        }
    }

    public void updatePlayerGraphics() {
        for (int i = playerRectangles.size() - 1; i >= 0; i--) {
            Rectangle playerRect = playerRectangles.get(i);
            Player player = App.gameModel.getPlayers().get(i);
            if (player.lose()) {
                centerPane.getChildren().remove(playerRect);
            }
        }
    }



    public void setPopupInStage(Pane popup){
        if(!overlayPane.getChildren().contains(popup)) overlayPane.getChildren().add(popup);
    }

    public void removePopupInStage(Pane popup){
        overlayPane.getChildren().remove(popup);
    }

}
