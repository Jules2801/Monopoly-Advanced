package controller;

import gui.App;
import gui.GameStates;
import gui.Style;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import model.*;
import model.Cards.*;
import model.Cell.*;
import model.Cell.Bonus.*;
import model.Cell.Franchise.Heritage.EmptyHeritage;
import model.Cell.Franchise.Heritage.Heritage;
import model.Cell.Franchise.Heritage.SaberTooth;
import model.Cell.Franchise.Heritage.Tartaros;
import model.Cell.Franchise.Restaurants.*;
import model.Cell.Franchise.Restaurants.Antique;
import model.Geometry.Coordinate;
import model.Save.SaveGame;
import model.Tools.Observer;
import states.Gameplay;
import model.map.Map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GameplayController {


    public Gameplay view;
    public Game game;

    public GameplayController(Gameplay view) {
        this.view = view;
    }

    public void setGameModel(){
        this.game = App.gameModel;
        view.playerPriceLabel = new ArrayList<>();
    }

    public List<Player> getPlayerList() {
        return game.getPlayers();
    }

    public List<HBox> getPlayerHBox() {
        List<HBox> playerBoxes = new ArrayList<>();
        for (int i = 0; i < getPlayerList().size(); i++) {
            Player player = getPlayerList().get(i);
            Label moneyLabel = new Label(player.getMoney() + "$");
            moneyLabel.setFont(Style.paragraphFont);
            view.playerPriceLabel.add(moneyLabel);
            playerBoxes.add(view.playerBox(moneyLabel));
        }
        return playerBoxes;
    }

    /**
     * Met à jour l'argent d'un joueur dans l'interface utilisateur.
     */
    public void setPriceLabel(){
        for(int i = 0; i < getPlayerList().size(); i++){
            Player p = game.getPlayers().get(i);
            view.playerPriceLabel.get(i).setText(p.getName() + " : " + view.getModuloMoney(p.getMoney()));
            view.playerPriceLabel.get(i).setFont(Style.paragraphFont);
        }
    }

    public void updatePriceLabel(){
        Player p = game.getCurrentPlayer();
        for(int i = 0; i < getPlayerList().size(); i++){
            if(getPlayerList().get(i) == p){
                if(!getPlayerList().get(i).lose()) {
                    view.playerPriceLabel.get(i).setText(p.getName() + " : " + view.getModuloMoney(p.getMoney()));
                    view.playerPriceLabel.get(i).setFont(Style.paragraphFont);
                }else{
                    view.playerPriceLabel.get(i).setText(p.getName() + " : " + "Éliminé");
                }

            }
        }
    }

    public int getRound(){
        return game.getRound();
    }

    public void setPlayableCardButtonAction(){
        setAllyBuildingCardPlayable(game.getCurrentPlayer());
        setEnnemyBuildingCardPlayable(game.getCurrentPlayer());
        setPrisonCardPlayable(game.getCurrentPlayer());
        makePlayableCardBox();
        view.timeRemain.stop();
    }

    public int getCurrentPlayerIndex(){
        return game.getCurrentPlayerIndex();
    }

    /**
     * Méthode appelé à chaque fois que le Leaderboard à besoin d'être mis à jour.
     */
    public void updateLeaderboard() {
        for(int i = 0; i < game.getPlayers().size(); i++){
            if(game.getPlayers().get(i).lose()){
                view.playerPriceLabel.get(i).setStyle("-fx-text-fill: grey;");
                view.playerPriceLabel.get(i).setFont(Style.paragraphFont);
            }else{
                view.playerPriceLabel.get(i).setStyle("-fx-text-fill: black;");
                view.playerPriceLabel.get(i).setFont(Style.paragraphFont);
            }
            updatePriceLabel();
        }
        String color = view.convertColorToString(view.colors[game.getCurrentPlayerIndex()]);
        view.playerPriceLabel.get(game.getCurrentPlayerIndex()).setStyle("-fx-text-fill: #" + color + ";");
        view.playerPriceLabel.get(game.getCurrentPlayerIndex()).setFont(Style.titleFont);
    }

    public void makeSelectionableCellVisible(){
        ArrayList<AbstractCell> possibleCellsTeleport = game.getMap().possibleCellsTeleport();
        Observer obs = view.makeSelectionableCellObserver(possibleCellsTeleport);
        for(AbstractCell cell : possibleCellsTeleport){
            Coordinate c = cell.getCoordinate();
            BorderPane infoCell = view.informationOnGraphics[c.getY()][c.getX()];
            view.selectionableCellVisible(c, cell.description());
            infoCell.setOnMouseClicked(event -> {
                infoCell.setOpacity(0.9);
                Teleport(cell);
                obs.update();
                checkMovePlayer();
            });
        }
    }

    public void updateObservers(List<Observer> observers){
        for(Observer o : observers){
            o.update();
        }
    }

    public int getDiceResult(){
        game.getDice().roll();
        int sum = game.getDice().getSum();
        game.getDice().resetValues();
        return sum;
    }

    public void setNewRound(){
        view.testSave.setDisable(true);
        updateLeaderboard();
        if(gameIsLose() != null){
            goToWinRound();
        }

        updateLeaderboard();
        view.centerPane.setStyle("-fx-border-width: 5px; -fx-border-color: #" + view.convertColorToString(view.colors[getCurrentPlayerIndex()]));

        if(game.isNewRound()){
            for(Restaurant f: game.getRestaurants()){
                f.applyBonus(getRound());
                updateLeaderboard();
                if(f.applyMalus(getRound())){
                    f.malusDescription();
                };
                updateLeaderboard();
            }
            for(Heritage h: game.getHeritages()){
                h.applyBonus(getRound());
                updateLeaderboard();
            }
        }

        if(!game.getCurrentPlayer().isInJail()) {
            VBox diceBox = view.getDiceBox();
            Dice dice = game.getDice();
            view.setPopupInStage(diceBox);
            Observer diceObserver = new Observer() {

                public void update() {
                    Timeline removeBox = new Timeline(
                            new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    view.removePopupInStage(diceBox);
                                }
                            })
                    );
                    removeBox.setCycleCount(1);
                    removeBox.play();
                    removeBox.setOnFinished(e -> {
                        dice.removeObserver(this);
                    });
                }
            };
            dice.addObserver(diceObserver);
        }
        else{
            setPrisonBoxRound(game.getCurrentPlayer());
        }
    }

    public void payBeforePlay(){
        Player player = game.getCurrentPlayer();
        ArrayList<Building> c = game.getMap().getAdjacentEnemyBuildingsToPay(player.getActualCell(), player);
        Building payThisBuilding = null;
        if (!c.isEmpty()) {
            for (Building b : c) {
                if(payThisBuilding == null || (payThisBuilding.getRent() < b.getRent())) payThisBuilding = b;
            }
            int diff = player.payTo(payThisBuilding.getRent(), payThisBuilding.getOwner());
            if(diff != 0){
                ArrayList<Building> list = player.getSafeBuildings();
                ArrayList<Building> mortagedList = new ArrayList<>();

                if(!player.getSafeBuildings().isEmpty()) {
                    Observer obs = makeMortagedBuildingObserver(player, list, payThisBuilding, diff);
                    view.makeMortagedBuildingVisible(list);
                    setActionWithMortagedBuilding(list, player, obs, mortagedList, diff);
                }
                else{
                    updateLeaderboard();
                    Observer obs = makeNewRoundObserver(player);
                    obs.update();
                }
            }
            else{
                updateLeaderboard();
                Observer obs = makeNewRoundObserver(player);
                obs.update();
            }

        }
        else if(player.getActualCell() instanceof Taxes){
            Taxes taxes = (Taxes) player.getActualCell();
            int diff = player.payTaxes(taxes.getPrice());
            if(diff != 0){
                ArrayList<Building> list = player.getSafeBuildings();
                ArrayList<Building> mortagedList = new ArrayList<>();

                if(!player.getSafeBuildings().isEmpty()) {
                    Observer obs = makeMortagedBuildingObserver(player, list, null, diff);
                    view.makeMortagedBuildingVisible(list);
                    setActionWithMortagedBuilding(list, player, obs, mortagedList, diff);
                }
                else{
                    updateLeaderboard();
                    Observer obs = makeNewRoundObserver(player);
                    obs.update();
                }
            }
            else{
                updateLeaderboard();
                Observer obs = makeNewRoundObserver(player);
                obs.update();
            }
        }
        else{
            Observer obs = makeNewRoundObserver(player);
            obs.update();
        }
    }

    public Observer makeNewRoundObserver(Player player){
        Observer obs = new Observer() {
            @Override
            public void update() {
                ArrayList<Building> buyableBuilding = game.getMap().getAdjacentEmptyBuildings(player.getActualCell(), player.getMoney());
                ArrayList<Building> actualPlayerBuilding = game.getMap().getAdjacentPlayerBuildings(player.getActualCell(), player);
                ArrayList<Restaurant> restaurants = game.getMap().getAdjacentEmptyRestaurants(player.getActualCell());
                ArrayList<Heritage> heritages = game.getMap().getAdjacentEmptyHeritage(player.getActualCell());
                ArrayList<Garden> gardens = game.getMap().gardenPlayerCanBuy(player);

                ArrayList<Building> destroyBuildings = game.getCurrentPlayer().getDestroyBuildings();

                ArrayList<Observer> observers = new ArrayList<>();

                view.makeBuyableBuildingVisible(buyableBuilding);
                view.showBuyableGarden(gardens);
                view.makeUpgradableBuildingVisible(actualPlayerBuilding);
                view.makeNearRestaurantVisible(restaurants);
                view.makeNearHeritageVisible(heritages);

                view.skip.setDisable(false);
                view.playableCardButton.setDisable(false);
                view.seconds = 30;
                view.timeRemain.setCycleCount(view.seconds+1);
                view.timeRemain.play();

                Observer buyableObserver = makeBuyableBuildingObserver(buyableBuilding);
                observers.add(buyableObserver);

                Observer upgradableObserver = makeUpgradableBuildingObserver(actualPlayerBuilding);
                observers.add(upgradableObserver);

                Observer buyableRestaurant = makeBuyableRestaurantObserver(restaurants);
                observers.add(buyableRestaurant);

                Observer buyableHeritage = makeBuyableHeritageObserver(heritages);
                observers.add(buyableHeritage);

                view.skip.setOnAction((event -> {
                    view.skip.setDisable(true);
                    Style.styleButton(view.skip, Style.DESACTIVATED);
                    view.playableCardButton.setDisable(true);
                    view.timeRemain.stop();
                    view.timeRemaining.setText("Aucun");

                    updateObservers(observers);

                    game.kickLosers();
                    view.updatePlayerGraphics();

                    game.nextTurn();
                    checkEventTurn();

                    updateLeaderboard();
                }));

                view.timeRemain.setOnFinished(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        view.timeRemaining.setText("Aucun");
                        view.timeRemain.stop();
                        updateObservers(observers);
                        view.skip.setDisable(true);
                        view.playableCardButton.setDisable(true);

                        game.nextTurn();
                        checkEventTurn();
                        updateLeaderboard();
                    }
                });

                setActionWithBuyableBuiding(buyableBuilding, player, observers);
                setActionWithUpgradableBuilding(actualPlayerBuilding, player, observers);
                setActionWithReparation(destroyBuildings, player, observers);
                setActionWithBuyableRestaurant(restaurants, player, observers);
                setActionWithBuyableHeritage(heritages, player, observers);
                setActionWithBuyableGarden(gardens,player,observers);
            }
        };
        return obs;
    }

    public Observer makeMortagedBuildingObserver(Player player, ArrayList<Building> list, Building building, int amount){
        Observer obs = new Observer() {
            @Override
            public void update() {
                for(Building b : list){
                    Coordinate c = b.getCoordinate();
                    BorderPane cell = view.cellOnGraphics[c.getY()][c.getX()];
                    BorderPane infoCell = view.informationOnGraphics[c.getY()][c.getX()];

                    if(b.getOwner() == player) cell.setStyle("-fx-background-color: #3b3b3b;");
                    else{
                        player.getProperties().remove(b);
                        cell.setStyle("-fx-background-color: #3b3b3b; -fx-border-width: 2px; -fx-border-color: yellow; -fx-border-style: dashed;");
                    }
                    infoCell.setOnMouseEntered((event) -> view.descriptionLabel.setText(b.description()));
                    infoCell.setOnMouseExited((event) -> view.descriptionLabel.setText("Sélectionner une case pour avoir des informations."));
                    infoCell.setOnMouseClicked(null);
                }
                int diff;
                if(building != null) diff = player.payTo(amount, building.getOwner());
                else diff = player.payTaxes(amount);
                updateLeaderboard();
                if(diff > 0){
                    Observer main = makeNewRoundObserver(player);
                    main.update();
                }
                else{
                    game.nextTurn();
                    checkEventTurn();
                }
            }
        };
        return obs;
    }

    public Observer makeUpgradableBuildingObserver(List<Building> list){
        Observer obs = new Observer() {
            public void update(){

                for(Building b : list){
                    Coordinate c = b.getCoordinate();
                    BorderPane cell = view.cellOnGraphics[c.getY()][c.getX()];
                    BorderPane infoCell = view.informationOnGraphics[c.getY()][c.getX()];

                    cell.setStyle("-fx-background-color: #3b3b3b");
                    infoCell.setOnMouseEntered((event) -> view.descriptionLabel.setText(b.description()));
                    infoCell.setOnMouseExited((event) -> view.descriptionLabel.setText("Sélectionner une case pour avoir des informations."));
                    infoCell.setOnMouseClicked(null);
                }
            }
        };
        return obs;
    }

    public Observer makeBuyableRestaurantObserver(List<Restaurant> list){
        Observer obs = new Observer() {
            @Override
            public void update() {
                for(Restaurant f: list){
                    if(f.getOwner() == null){
                        Coordinate c = f.getCoordinate();
                        BorderPane infoCell = view.informationOnGraphics[c.getY()][c.getX()];
                        BorderPane graphicsCell = view.cellOnGraphics[c.getY()][c.getX()];

                        graphicsCell.setStyle(f.getStyle());

                        infoCell.setOnMouseEntered((event) -> view.descriptionLabel.setText(f.description()));
                        infoCell.setOnMouseExited((event) -> view.descriptionLabel.setText("Sélectionner une case pour avoir des informations."));
                        infoCell.setOnMouseClicked(null);
                    }
                }
            }
        };
        return obs;
    }

    public Observer makeBuyableHeritageObserver(List<Heritage> list){
        Observer obs = new Observer() {
            @Override
            public void update() {
                for(Heritage f: list){
                    if(f.getOwner() == null){
                        Coordinate c = f.getCoordinate();
                        BorderPane infoCell = view.informationOnGraphics[c.getY()][c.getX()];
                        BorderPane graphicsCell = view.cellOnGraphics[c.getY()][c.getX()];

                        graphicsCell.setStyle(f.getStyle());

                        infoCell.setOnMouseEntered((event) -> view.descriptionLabel.setText(f.description()));
                        infoCell.setOnMouseExited((event) -> view.descriptionLabel.setText("Sélectionner une case pour avoir des informations."));
                        infoCell.setOnMouseClicked(null);
                    }
                }
            }
        };
        return obs;
    }

    public Observer makeBuyableBuildingObserver(List<Building> list){
        Observer obs = new Observer() {
            @Override
            public void update() {
                for(Building b : list){
                    Coordinate c = b.getCoordinate();
                    BorderPane cell = view.cellOnGraphics[c.getY()][c.getX()];
                    BorderPane infoCell = view.informationOnGraphics[c.getY()][c.getX()];
                    if(b.getOwner() == null){
                        cell.setStyle("-fx-background-color: #3b3b3b;");
                        Style.addImageToBackground(infoCell, Style.getLevel0WithVariant(b.isVariant(),0));
                        infoCell.setOnMouseExited(null);
                        infoCell.setOnMouseEntered(null);
                        infoCell.setOnMouseClicked(null);
                    }
                    else{
                        Image[][] buildingImage = addBuildingImagesToPlayer();
                        Style.addImageToBackground(view.informationOnGraphics[c.getY()][c.getX()], buildingImage[b.getLevel()-1][Style.directionToInt(b.getDirection())]);
                        cell.setStyle("-fx-background-color: #3b3b3b;");
                    }
                }
            }
        };
        return obs;
    }

    public Observer makeSimpleTurnBackObserver(List<Building> list){
        Observer obs = new Observer() {
            @Override
            public void update() {
                for(Building b : list){
                    Coordinate c = b.getCoordinate();
                    BorderPane cell = view.cellOnGraphics[c.getY()][c.getX()];
                    BorderPane infoCell = view.informationOnGraphics[c.getY()][c.getX()];
                    cell.setStyle("-fx-background-color: #3b3b3b;");
                    infoCell.setOnMouseClicked(null);
                    infoCell.setOnMouseEntered((event) -> {
                        view.descriptionLabel.setText(b.description());
                    });
                    infoCell.setOnMouseExited(null);
                }
            }
        };
        return obs;
    }

    public void setActionWithTakeableBuilding(List<Building> list, Player player){
        Observer obs = makeSimpleTurnBackObserver(list);
        for(Building b : list){
            Coordinate c = b.getCoordinate();
            BorderPane cell = view.cellOnGraphics[c.getY()][c.getX()];
            BorderPane infoCell = view.informationOnGraphics[c.getY()][c.getX()];

            cell.setStyle("-fx-background-color: #3b3b3b; -fx-border-color: gold;");

            infoCell.setOnMouseEntered(event -> {
                cell.setStyle("-fx-background-color: #3b3b3b; -fx-border-color: #bd9e04");
            });
            infoCell.setOnMouseExited(event -> {
                cell.setStyle("-fx-background-color: #3b3b3b; -fx-border-color: gold;");
            });

            infoCell.setOnMouseClicked(event -> {
                view.timeRemain.stop();
                view.setTakeableBox(b, player, obs);
            });
        }
    }

    public Button getTakeButton(Building building, Player player, BorderPane[] box, Observer obs){
        Button button = new Button("Oui");
        Style.putListenerAndStyleToButton(button, Style.ACTIVATED);

        button.setOnAction(event -> {
            view.timeRemain.setCycleCount(view.seconds+1);
            view.timeRemain.play();
            button.setDisable(true);

            view.skip.setDisable(false);

            obs.update();

            building.changeOwner(player);

            Coordinate c = building.getCoordinate();
            BorderPane cell = view.cellOnGraphics[c.getY()][c.getX()];
            BorderPane infoCell = view.informationOnGraphics[c.getY()][c.getX()];

            Image[][] buildingImage = addBuildingImagesToPlayer();

            Style.addImageToBackground(view.informationOnGraphics[c.getY()][c.getX()], buildingImage[building.getLevel()-1][Style.directionToInt(building.getDirection())]);

            cell.setStyle("-fx-background-color: #3b3b3b;");

            if(building.getOwnGarden()){
                Coordinate gc = building.getPossibleGarden().getCoordinate();
                String playerColor = view.convertColorToString(view.colors[game.getCurrentPlayerIndex()]);
                String gcolor = "-fx-background-color: #2e9531;" + "-fx-border-width: 2px; -fx-border-color: "+ "#" + playerColor + ";"+"; -fx-border-style: dotted;";
                view.cellOnGraphics[gc.getY()][gc.getX()].setStyle(gcolor);
            }

            infoCell.setOnMouseEntered(e -> {
                view.descriptionLabel.setText(building.description());
            });
            infoCell.setOnMouseClicked(null);
            view.removePopupInStage(box[0]);
        });
        return button;
    }

    public Button getRepareButton(Building building, Player player, BorderPane[] box){
        Button button = new Button("Réparer");
        Style.putListenerAndStyleToButton(button, Style.ACTIVATED);
        if(player.getMoney() < building.getReparationsPrice()) button.setDisable(true);

        button.setOnAction((event -> {
            view.timeRemain.setCycleCount(view.seconds+1);
            view.timeRemain.play();
            button.setDisable(true);
            player.repair(building);
            view.controller.updateLeaderboard();
            Coordinate c = building.getCoordinate();

            view.cellOnGraphics[c.getY()][c.getX()].setStyle("-fx-background-color: #3b3b3b;");

            Image[][] buildingImage = addBuildingImagesToPlayer();

            Style.addImageToBackground(view.informationOnGraphics[c.getY()][c.getX()], buildingImage[building.getLevel()-1][Style.directionToInt(building.getDirection())]);
            view.informationOnGraphics[c.getY()][c.getX()].setOnMouseEntered(e -> {
                view.descriptionLabel.setText(building.description());
            });
            view.informationOnGraphics[c.getY()][c.getX()].setOnMouseExited(e -> view.descriptionLabel.setText(Style.emptyCaseMessage));
            view.informationOnGraphics[c.getY()][c.getX()].setOnMouseClicked(null);
            view.removePopupInStage(box[0]);
        }));
        return button;
    }

    public Button getUpgradeButton(Building building, Player player, BorderPane[] box){
        Button button = new Button("Améliorer");
        Style.putListenerAndStyleToButton(button, Style.ACTIVATED);

        button.setOnAction((event -> {
            view.timeRemain.setCycleCount(view.seconds+1);
            view.timeRemain.play();
            button.setDisable(true);

            building.upgrade(player);

            updateLeaderboard();


            Coordinate c = building.getCoordinate();

            Image[][] buildingImage = addBuildingImagesToPlayer();

            Style.addImageToBackground(view.informationOnGraphics[c.getY()][c.getX()], buildingImage[building.getLevel()-1][Style.directionToInt(building.getDirection())]);
            view.cellOnGraphics[c.getY()][c.getX()].setStyle("-fx-background-color: #3b3b3b");

            view.informationOnGraphics[c.getY()][c.getX()].setOnMouseEntered(e -> {
                view.descriptionLabel.setText(building.description());
            });
            view.informationOnGraphics[c.getY()][c.getX()].setOnMouseExited(null);
            view.informationOnGraphics[c.getY()][c.getX()].setOnMouseClicked(null);
            view.removePopupInStage(box[0]);
        }));

        return button;
    }

    public Button getLeaveButton(BorderPane box, AbstractCell building){
        Button button = new Button("Quitter");
        Style.putListenerAndStyleToButton(button, Style.ACTIVATED);

        button.setOnAction((event -> {
            Coordinate c = building.getCoordinate();
            view.informationOnGraphics[c.getY()][c.getX()].setDisable(false);
            box.setDisable(false);
            view.timeRemain.setCycleCount(view.seconds+1);
            view.timeRemain.play();
            button.setDisable(true);
            view.removePopupInStage(box);
        }));
        return button;
    }

    public VBox makeRestaurantBox(Player player, Restaurant restaurant, BorderPane pane, Restaurant oldRestaurant){
        VBox cardBox = new VBox(20);
        cardBox.setAlignment(Pos.CENTER);
        view.setCardBoxSize(cardBox);

        String color = restaurant.color;
        cardBox.setStyle("-fx-border-color: " + color + "; -fx-border-width: 5px; -fx-background-color: white");

        Label caseTitleLabel = new Label(restaurant.getName());
        caseTitleLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
        caseTitleLabel.setFont(Style.titleFont);

        VBox caseImage = new VBox(0);
        view.setImageBoxSize(caseImage);
        caseImage.setStyle("-fx-background-color: " + color + ";");

        Label caseDescriptionLabel = new Label(restaurant.description());
        caseDescriptionLabel.setWrapText(true);
        caseDescriptionLabel.setStyle("-fx-text-fill: black; -fx-text-alignment: center; -fx-padding: 0 20px");
        caseDescriptionLabel.setFont(Style.paragraphFont);

        Button acceptButton = new Button("Accepter");
        Style.putListenerAndStyleToButton(acceptButton, Style.ACTIVATED);

        if(player.getMoney() < restaurant.getPrice()) acceptButton.setDisable(true);

        cardBox.getChildren().addAll(caseTitleLabel, caseImage, caseDescriptionLabel, acceptButton);
        acceptButton.setOnMouseClicked(event -> {
            view.timeRemain.setCycleCount(view.seconds+1);
            view.timeRemain.play();
            restaurant.setOwner(player);
            replaceRestaurant(restaurant);
            oldRestaurant.setOwner(player);

            Coordinate c = restaurant.getCoordinate();
            BorderPane graphicCell = view.cellOnGraphics[c.getY()][c.getX()];
            BorderPane infoCell = view.informationOnGraphics[c.getY()][c.getX()];

            graphicCell.setStyle(restaurant.getStyle());
            graphicCell.setCenter(new Text(restaurant.getName()));
            infoCell.setOnMouseEntered(event1 -> {
                view.descriptionLabel.setText(restaurant.getOwnerDescription(getRound()));
            });
            infoCell.setOnMouseExited(event1 ->{
                view.descriptionLabel.setText("");
            });
            infoCell.setOnMouseClicked(null);

            view.removePopupInStage(pane);
        });
        return cardBox;
    }

    public VBox makeHeritageBox(Player player, Heritage heritage, BorderPane pane, Heritage oldHeritage){
        VBox cardBox = new VBox(20);
        cardBox.setAlignment(Pos.CENTER);
        view.setCardBoxSize(cardBox);

        String color = heritage.color;
        cardBox.setStyle("-fx-border-color: " + color + "; -fx-border-width: 5px; -fx-background-color: white; -fx-border-style: dashed;");

        Label caseTitleLabel = new Label(heritage.getName());
        caseTitleLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
        caseTitleLabel.setFont(Style.titleFont);

        VBox caseImage = new VBox(0);
        view.setImageBoxSize(caseImage);
        caseImage.setStyle("-fx-background-color: " + color + ";");

        Label caseDescriptionLabel = new Label(heritage.description());
        caseDescriptionLabel.setWrapText(true);
        caseDescriptionLabel.setStyle("-fx-text-fill: black; -fx-text-alignment: center; -fx-padding: 0 20px");
        caseDescriptionLabel.setFont(Style.paragraphFont);

        Button acceptButton = new Button("Accepter");
        Style.putListenerAndStyleToButton(acceptButton, Style.ACTIVATED);

        if(player.getMoney() < heritage.getPrice()) acceptButton.setDisable(true);

        cardBox.getChildren().addAll(caseTitleLabel, caseImage, caseDescriptionLabel, acceptButton);
        acceptButton.setOnMouseClicked(event -> {
            view.timeRemain.setCycleCount(view.seconds+1);
            view.timeRemain.play();
            heritage.setOwner(player);
            replaceHeritage(heritage);
            oldHeritage.setOwner(player);

            Coordinate c = heritage.getCoordinate();
            BorderPane graphicCell = view.cellOnGraphics[c.getY()][c.getX()];
            BorderPane infoCell = view.informationOnGraphics[c.getY()][c.getX()];

            graphicCell.setStyle(heritage.getStyle());
            graphicCell.setCenter(new Text(heritage.getName()));
            infoCell.setOnMouseEntered(event1 -> {
                view.descriptionLabel.setText(heritage.getOwnerDescription(getRound()));
            });
            infoCell.setOnMouseExited(event1 ->{
                view.descriptionLabel.setText("");
            });
            infoCell.setOnMouseClicked(null);

            view.removePopupInStage(pane);
        });
        return cardBox;
    }

    public void setActionWithBuyableGarden(List<Garden> list, Player player,List<Observer> observers){
        Observer actionListener = makeActionGardenObserver(list);
        observers.add(actionListener);
        for(Garden g : list){
            Coordinate c = g.getCoordinate();
            view.informationOnGraphics[c.getY()][c.getX()].setOnMouseClicked((event -> {
                view.timeRemain.stop();
                view.setGardensBox(g, player , actionListener);
            }));
        }
    }

    public void setActionWithBuyableBuiding(List<Building> list, Player player, List<Observer> observers){
        Observer actionListenerObserver = makeActionBuyableObserver(list);
        //observers.add(actionListenerObserver);
        for(Building b : list){
            Coordinate c = b.getCoordinate();
            view.informationOnGraphics[c.getY()][c.getX()].setOnMouseClicked((event -> {
                view.timeRemain.stop();
                view.setBuyBox(b, player, actionListenerObserver);
            }));

            view.timeRemain.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    view.timeRemaining.setText("Aucun");
                    view.timeRemain.stop();
                    updateObservers(observers);
                    view.skip.setDisable(true);
                    view.playableCardButton.setDisable(true);
                    game.nextTurn();
                    checkEventTurn();
                    updateLeaderboard();
                }
            });
        }
    }

    public void setActionWithReparation(List<Building> list,Player player, List<Observer> observers) {
        Observer actionListenerObserver = makeActionReparableObserver(list);
        observers.add(actionListenerObserver);
        for(Building b : list){
            Coordinate c = b.getCoordinate();
            view.cellOnGraphics[c.getY()][c.getX()].setStyle("-fx-background-color: #3b3b3b; -fx-border-color: red; -fx-border-width: 1px; -fx-border-style: dashed;");
            view.informationOnGraphics[c.getY()][c.getX()].setOnMouseEntered(e -> {
                view.cellOnGraphics[c.getY()][c.getX()].setStyle("-fx-background-color: #3b3b3b; -fx-border-color: #8a0000; -fx-border-width: 1px; -fx-border-style: dashed;");
                view.descriptionLabel.setText(b.description());
            });
            view.informationOnGraphics[c.getY()][c.getX()].setOnMouseExited(e -> {
                view.cellOnGraphics[c.getY()][c.getX()].setStyle("-fx-background-color: #3b3b3b; -fx-border-color: red; -fx-border-width: 1px; -fx-border-style: dashed;");
                view.descriptionLabel.setText(Style.emptyCaseMessage);
            });
            view.informationOnGraphics[c.getY()][c.getX()].setOnMouseClicked((event -> {
                view.timeRemain.stop();
                view.setRepareBox(b, player, observers);
            }));

            view.timeRemain.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    view.timeRemaining.setText("Aucun");
                    view.timeRemain.stop();
                    updateObservers(observers);
                    view.skip.setDisable(true);
                    view.playableCardButton.setDisable(true);
                    game.nextTurn();
                    checkEventTurn();
                    updateLeaderboard();
                }
            });
        }
    }



    public void setActionWithBuyableRestaurant(List<Restaurant> list, Player player, List<Observer> observers){
        for(Restaurant f : list){
            Coordinate c = f.getCoordinate();

            view.informationOnGraphics[c.getY()][c.getX()].setOnMouseClicked(event -> {
                view.timeRemain.stop();
                setRestaurantBoxRound(f, player);
            });

            view.timeRemain.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    view.timeRemaining.setText("Aucun");
                    view.timeRemain.stop();
                    updateObservers(observers);
                    view.skip.setDisable(true);
                    view.playableCardButton.setDisable(true);
                    game.nextTurn();
                    checkEventTurn();
                    updateLeaderboard();
                }
            });
        }
    }

    public int getAllPrice(Player currentPlayer, ArrayList<Building> list, Button button, Observer obs, int moneyRemaining, Pane container){
        int res = 0;
        for(Building b : list){
            res += b.getRent()*2;
        }
        int finalPrice = res;
        if(res >= moneyRemaining || list.containsAll(currentPlayer.getSafeBuildings())){
            button.setOnAction(e -> {
                for(Building b : list){
                    b.setOwner(null);
                    b.setMortgaged(true);
                    currentPlayer.addMoney(finalPrice);
                    b.setPrice((int) (b.getRent()*2.5));
                    view.leftBox.getChildren().remove(container);
                }
                obs.update();
            });
            button.setDisable(false);
        }
        else{
            button.setDisable(true);
        }
        return res;
    }


    public ImageView EventGif(Event event){
        switch (event){
            case METEOR -> {
                return new ImageView(Style.meteor);
            }
            case TSUNAMI -> {
                return new ImageView(Style.tsunami);
            }
            default-> {
                return new ImageView(Style.volcano);
            }

        }
    }

    public void setActionWithMortagedBuilding(List<Building> list, Player player, Observer obs, ArrayList<Building> allMortagedBuilding, int moneyRemaining){
        VBox box = new VBox(10);
            box.setAlignment(Pos.CENTER);
        Label l = new Label("Prix total : " + "0" + "$");
            l.setFont(Style.titleFont);
        Button button = new Button("Vendre");
            Style.putListenerInButton(button);
            button.setDisable(true);
        box.getChildren().addAll(l, button);
        view.leftBox.getChildren().add(box);

        for(Building b : list){
            Coordinate c = b.getCoordinate();

            view.informationOnGraphics[c.getY()][c.getX()].setOnMouseClicked(e -> {
                if(allMortagedBuilding.contains(b)){
                    view.cellOnGraphics[c.getY()][c.getX()].setStyle("-fx-border-width: 2px; -fx-border-color: red; -fx-background-color: #3b3b3b;");
                    view.informationOnGraphics[c.getY()][c.getX()].setOnMouseEntered(event -> {
                        view.descriptionLabel.setText(b.description());
                        view.cellOnGraphics[c.getY()][c.getX()].setStyle("-fx-border-width: 2px; -fx-border-color: #a20505; -fx-background-color: #3b3b3b;");
                    });
                    view.informationOnGraphics[c.getY()][c.getX()].setOnMouseExited(event -> {
                        view.cellOnGraphics[c.getY()][c.getX()].setStyle("-fx-border-width: 2px; -fx-border-color: red; -fx-background-color: #3b3b3b;");
                    });
                    allMortagedBuilding.remove(b);
                    l.setText("Prix total : " + getAllPrice(player, allMortagedBuilding, button, obs, moneyRemaining, box) + "$");
                }
                else{
                    view.cellOnGraphics[c.getY()][c.getX()].setStyle("-fx-border-width: 2px; -fx-border-color: #0dcb0d; -fx-background-color: #3b3b3b;");
                    view.informationOnGraphics[c.getY()][c.getX()].setOnMouseEntered(event -> {
                        view.descriptionLabel.setText(b.description());
                        view.cellOnGraphics[c.getY()][c.getX()].setStyle("-fx-border-width: 2px; -fx-border-color: green; -fx-background-color: #3b3b3b;");
                    });
                    view.informationOnGraphics[c.getY()][c.getX()].setOnMouseExited(event -> {
                        view.cellOnGraphics[c.getY()][c.getX()].setStyle("-fx-border-width: 2px; -fx-border-color: #0dcb0d; -fx-background-color: #3b3b3b;");
                    });
                    allMortagedBuilding.add(b);
                    l.setText("Prix total : " + getAllPrice(player, allMortagedBuilding, button, obs, moneyRemaining, box) + "$");
                }
            });
        }
    }

    public void setActionWithBuyableHeritage(List<Heritage> list, Player player, List<Observer> observers){
        for(Heritage h : list){
            Coordinate c = h.getCoordinate();

            view.informationOnGraphics[c.getY()][c.getX()].setOnMouseClicked(event -> {
                view.timeRemain.stop();
                setHeritageBoxRound(h, player);
            });

            view.timeRemain.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    view.timeRemaining.setText("Aucun");
                    view.timeRemain.stop();
                    updateObservers(observers);
                    view.skip.setDisable(true);
                    view.playableCardButton.setDisable(true);
                    game.nextTurn();
                    checkEventTurn();
                    updateLeaderboard();
                }
            });
        }
    }

    public void setActionWithUpgradableBuilding(List<Building> list, Player player, List<Observer> observers){
        for(Building b : list){
            Coordinate c = b.getCoordinate();
            view.informationOnGraphics[c.getY()][c.getX()].setOnMouseClicked((event -> {
                view.timeRemain.stop();
                view.setUpgradeBox(b, player);
            }));

            view.timeRemain.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    view.timeRemaining.setText("Aucun");
                    view.timeRemain.stop();
                    updateObservers(observers);
                    view.skip.setDisable(true);
                    view.playableCardButton.setDisable(true);
                    game.nextTurn();
                    checkEventTurn();
                    updateLeaderboard();
                }
            });
        }
    }

    public Observer makeActionReparableObserver(List<Building> list){
        Observer obs = new Observer() {
            @Override
            public void update() {
                for(Building b : list){
                    Coordinate c = b.getCoordinate();
                    view.informationOnGraphics[c.getY()][c.getX()].setOnMouseClicked(null);
                    view.informationOnGraphics[c.getY()][c.getX()].setOnMouseEntered(e -> view.descriptionLabel.setText(b.description()));
                    view.informationOnGraphics[c.getY()][c.getX()].setOnMouseExited(e -> view.descriptionLabel.setText(Style.emptyCaseMessage));
                    view.cellOnGraphics[c.getY()][c.getX()].setStyle("-fx-background-color: #3b3b3b;");
                }
            }
        };
        return obs;
    }

    public Observer makeActionBuyableObserver(List<Building> list){
        Observer obs = new Observer() {
            @Override
            public void update() {
                for(Building b : list){
                    Coordinate c = b.getCoordinate();
                    BorderPane cell = view.cellOnGraphics[c.getY()][c.getX()];
                    BorderPane infoCell = view.informationOnGraphics[c.getY()][c.getX()];
                    infoCell.setOnMouseClicked(null);
                    if(b.getOwner() == null){
                        cell.setStyle("-fx-background-color: #3b3b3b;");
                        Style.addImageToBackground(infoCell, Style.getLevel0WithVariant(b.isVariant(), 0));
                        infoCell.setOnMouseExited(null);
                        infoCell.setOnMouseEntered(null);
                        infoCell.setOnMouseClicked(null);
                    }
                    else{
                        infoCell.setOnMouseEntered(e -> view.descriptionLabel.setText(b.description()));
                        Image[][] buildingImage = addBuildingImagesToPlayer();
                        Style.addImageToBackground(view.informationOnGraphics[c.getY()][c.getX()], buildingImage[b.getLevel()-1][Style.directionToInt(b.getDirection())]);
                        cell.setStyle("-fx-background-color: #3b3b3b;");
                    }
                }
            }
        };
        return obs;
    }

    public Observer makeActionGardenObserver(List<Garden> list){
        Observer obs = new Observer() {
            @Override
            public void update() {
                for(Garden garden : list){
                    Coordinate c = garden.getCoordinate();
                    view.informationOnGraphics[c.getY()][c.getX()].setOnMouseClicked(null);
                    view.informationOnGraphics[c.getY()][c.getX()].setOnMouseEntered(e -> view.descriptionLabel.setText(garden.description()));
                    view.informationOnGraphics[c.getY()][c.getX()].setOnMouseExited(e -> view.descriptionLabel.setText(Style.emptyCaseMessage));
                    String color = "-fx-border-width: 0; -fx-background-color: #2e9531";
                    String playerColor = view.convertColorToString(view.colors[game.getCurrentPlayerIndex()]);
                    if(garden.getLinkTo() != null && garden.getLinkTo().getOwner() != null){
                        color = "-fx-background-color: #2e9531;" + "-fx-border-width: 2px; -fx-border-color: "+ "#" + playerColor+ ";"+"; -fx-border-style: dotted;";
                        view.informationOnGraphics[c.getY()][c.getX()].setOnMouseEntered(e -> view.descriptionLabel.setText(garden.description()));
                        view.informationOnGraphics[c.getY()][c.getX()].setOnMouseExited(e -> view.descriptionLabel.setText(Style.emptyCaseMessage));
                    }
                    view.cellOnGraphics[c.getY()][c.getX()].setStyle(color);
                }
            }
        };
        return obs;
    }

    public void makePlayableCardBox(){
        BorderPane cardDisplay = new BorderPane();
            VBox showCard = new VBox(45);

            Label t = new Label("Quel cartes souhaitez vous jouez ?");
            t.setStyle("-fx-text-fill: white;");
            t.setFont(Style.titleFont);

            Button quitButton = new Button("Quitter");
            Style.putListenerAndStyleToButton(quitButton, Style.ACTIVATED);


            quitButton.setOnAction((event) -> {
                view.overlayPane.getChildren().remove(cardDisplay);
                view.timeRemain.setCycleCount(view.seconds+1);
                view.timeRemain.play();
            });

            showCard.getChildren().addAll(t, view.makePlayerCardBox(game.getCurrentPlayer(), cardDisplay), quitButton);
            showCard.setAlignment(Pos.CENTER);

            cardDisplay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9);");
            cardDisplay.setCenter(showCard);
            view.overlayPane.getChildren().add(cardDisplay);
    }

    public void makeBonusCardBox(){
        //view.stepsLabel.setStyle("-fx-font-size: 16px; -fx-text-alignment: center;");
        BorderPane cardDisplay = new BorderPane();
        VBox showCard = new VBox(45);

        Label t = new Label("Selectionner une carte :");
        t.setStyle("-fx-text-fill: white;");
        t.setFont(Style.titleFont);

        showCard.getChildren().addAll(t, view.makeBonusCardBox(game.getCurrentPlayer(), cardDisplay));
        showCard.setAlignment(Pos.CENTER);

        cardDisplay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9);");
        cardDisplay.setCenter(showCard);
        view.overlayPane.getChildren().add(cardDisplay);
    }

    public void setRestaurantBoxRound(Restaurant restaurant, Player player){
        //view.stepsLabel.setStyle("-fx-font-size: 16px; -fx-text-alignment: center");
        BorderPane restaurantBoxDisplay = new BorderPane();
        VBox restaurantBox = new VBox(60);
            HBox propositions = new HBox(60);
            propositions.setAlignment(Pos.CENTER);
                propositions.getChildren().addAll(makeRestaurantBox(player, new McOcean(restaurant.getCoordinate()), restaurantBoxDisplay, restaurant), makeRestaurantBox(player, new Bartenders(restaurant.getCoordinate()), restaurantBoxDisplay, restaurant), makeRestaurantBox(player, new Antique(restaurant.getCoordinate()), restaurantBoxDisplay, restaurant));

            Button leaveButton = new Button("Quitter");
            Style.putListenerAndStyleToButton(leaveButton, Style.ACTIVATED);


            leaveButton.setOnAction(event -> {
                view.timeRemain.setCycleCount(view.seconds+1);
                view.timeRemain.play();
                view.removePopupInStage(restaurantBoxDisplay);
            });

            restaurantBoxDisplay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9);");
            restaurantBox.setAlignment(Pos.CENTER);
            restaurantBox.getChildren().addAll(propositions, leaveButton);

            restaurantBoxDisplay.setCenter(restaurantBox);
            view.overlayPane.getChildren().add(restaurantBoxDisplay);
    }

    public void setHeritageBoxRound(Heritage heritage, Player player){
        //view.stepsLabel.setStyle("-fx-font-size: 16px; -fx-text-alignment: center");
        BorderPane heritageBoxDisplay = new BorderPane();
        VBox heritageBox = new VBox(60);
        HBox propositions = new HBox(60);
        propositions.setAlignment(Pos.CENTER);
        propositions.getChildren().addAll(makeHeritageBox(player, new SaberTooth(heritage.getCoordinate()), heritageBoxDisplay, heritage), makeHeritageBox(player, new Tartaros(heritage.getCoordinate()), heritageBoxDisplay, heritage));

        Button leaveButton = new Button("Quitter");
        Style.putListenerAndStyleToButton(leaveButton, Style.ACTIVATED);

        leaveButton.setOnAction(event -> {
            view.timeRemain.setCycleCount(view.seconds+1);
            view.timeRemain.play();
            view.removePopupInStage(heritageBoxDisplay);
        });

        heritageBoxDisplay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9);");
        heritageBox.setAlignment(Pos.CENTER);
        heritageBox.getChildren().addAll(propositions, leaveButton);

        heritageBoxDisplay.setCenter(heritageBox);
        view.overlayPane.getChildren().add(heritageBoxDisplay);
    }

    public ArrayList<Card> setAllBonus(){
        ArrayList<Card> l = new ArrayList<>();

        for(int i = 0; i < 3; i++){
            l.add(new Shield());
        }
        for(int i = 0; i < 2; i++){
            l.add(new OutOfPrison());
            l.add(new Inflation());
            l.add(new FamousBuilding());
        }
        l.add(new PeaceTreaty());
        return l;
    }

    public void setBonusBoxRound(Player player){
        view.skip.setDisable(true);
        view.timeRemain.stop();

        VBox mainBox = new VBox(20);
        mainBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9);");



        Dice dice = new Dice();
        VBox bonusDiceBox = view.getBonusDiceBox(dice);
        view.setPopupBoxSize(bonusDiceBox);
        view.setPopupInStage(bonusDiceBox);

        Observer bonusDiceObserver = new Observer() {
            @Override
            public void update() {
                Timeline removeBox = new Timeline(
                        new KeyFrame(Duration.seconds(3), new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                view.removePopupInStage(bonusDiceBox);
                            }
                        })
                );
                removeBox.setCycleCount(1);
                removeBox.setOnFinished(e -> {
                    player.addCard(view.allBonus.get(dice.getSum()%view.allBonus.size()));
                    Observer obs = makeNewRoundObserver(game.getCurrentPlayer());
                    obs.update();
                });
                removeBox.play();
            }
        };
        dice.addObserver(bonusDiceObserver);
    }



    public void setPrisonBoxRound(Player player){
        //view.stepsLabel.setStyle("-fx-font-size: 16px; -fx-text-alignment: center;");
        BorderPane prisonBoxDisplay = new BorderPane();
            VBox prisonBox = new VBox(60);
                Label t = new Label("Vous êtes en prison.\n Que souhaitez-vous faire ?");
                t.setStyle("-fx-text-fill: white;");
                t.setFont(Style.titleFont);

                HBox prisonButtonBox = new HBox(20);
                    Card prisonCard = null;
                    Button throwPrisonDice = new Button("Lancer le dès");
                    Style.putListenerAndStyleToButton(throwPrisonDice, Style.ACTIVATED);

                        throwPrisonDice.setOnAction((event) -> {
                            view.overlayPane.getChildren().remove(prisonBoxDisplay);
                            Dice prisonDice = new Dice();
                            VBox prisonDiceBox = view.getPrisonDiceBox(prisonDice);
                            view.setPopupInStage(prisonDiceBox);

                            Observer prisonDiceObserver = new Observer() {
                                @Override
                                public void update() {
                                    Timeline removeBox = new Timeline(
                                            new KeyFrame(Duration.seconds(3), new EventHandler<ActionEvent>() {
                                                @Override
                                                public void handle(ActionEvent event) {
                                                    view.removePopupInStage(prisonDiceBox);
                                                }
                                            })
                                    );
                                    removeBox.setCycleCount(1);
                                    removeBox.setOnFinished(e -> {
                                        prisonDice.removeObserver(this);
                                        if(!prisonDice.isEqual()){
                                            game.nextTurn();
                                            checkEventTurn();
                                        }
                                        else{
                                            game.getCurrentPlayer().outOfJail();
                                            setNewRound();
                                        }
                                    });
                                    removeBox.play();
                                }
                            };
                            prisonDice.addObserver(prisonDiceObserver);

                        });
                    Button playPrisonCard = new Button("Jouer la carte sortie de prison");
                    Style.putListenerAndStyleToButton(playPrisonCard, Style.DESACTIVATED);
                        playPrisonCard.setDisable(true);
                    for(Card c : player.getCards()) if(c instanceof OutOfPrison){
                        playPrisonCard.setDisable(false);
                        prisonCard = c;
                    }
                    Card finalPrisonCard = prisonCard;
                    playPrisonCard.setOnAction((event) -> {
                        applyEffect(player, finalPrisonCard);
                        view.overlayPane.getChildren().remove(prisonBoxDisplay);
                        setNewRound();
                    });
                prisonButtonBox.getChildren().addAll(throwPrisonDice, playPrisonCard);
                prisonButtonBox.setAlignment(Pos.CENTER);

            prisonBox.getChildren().addAll(t, prisonButtonBox);
            prisonBox.setAlignment(Pos.CENTER);

        prisonBoxDisplay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9);");
        prisonBoxDisplay.setCenter(prisonBox);
        view.overlayPane.getChildren().add(prisonBoxDisplay);
    }
    
    public void destroyBuildings(){
        game.destroyBuildings();
    }

    public void moveActualToCell(AbstractCell cell){

        Coordinate currentCoordinate = game.getCurrentPlayer().getCoordinate();
        Coordinate targetCoordinate = cell.getCoordinate();
        game.getCurrentPlayer().moveToCell(cell);
        double movesY = (targetCoordinate.getY() - currentCoordinate.getY()) * 64.5;
        double movesX = (targetCoordinate.getX() - currentCoordinate.getX()) * 69;
        TranslateTransition transition =
                new TranslateTransition(Duration.seconds(0.5), view.playerRectangles.get(game.getCurrentPlayerIndex()));
        transition.setByY(movesY);
        transition.setByX(movesX);
        transition.play();
    }

    public void randomTeleport(){
        moveActualToCell(game.getMap().randomCellTeleport());
        payBeforePlay();
    }

    public void Teleport(AbstractCell cell){
        moveActualToCell(cell);
    }

    public void moving(int diceResult) {
        int current = game.getCurrentPlayerIndex();
        Player currentPlayer = game.getPlayers().get(current);
        Observer obs = makeNewRoundObserver(currentPlayer);
        AtomicInteger transitionsCompleted = new AtomicInteger(0);

        checkMovePlayer(currentPlayer, diceResult, transitionsCompleted, current);
    }


    public void saveGame(){
        SaveGame.save(game,"game.sav");
    }

    public void checkEventTurn(){
        if(game.isEventActive()) {
            ImageView event = EventGif(game.getRandomEvent());
            view.activateEvent(event);
        }
        else{
            setNewRound();
        }
    }

    public void checkMovePlayer(){
        checkMovePlayer(game.getCurrentPlayer(), 0, null, game.getCurrentPlayerIndex());
    }
    public void checkMovePlayer(Player currentPlayer, int remainingMoves, AtomicInteger transitionsCompleted, int currentPlayerIndex) {
        Coordinate startCell = new Coordinate(0,0);
        if (remainingMoves <= 0) {
            view.stepsLabel.setText("Aucun pas restant");
            if(currentPlayer.getCoordinate().equals(startCell)){
                currentPlayer.addMoney(500);//500$ supplementaire si joueur tombe parfaitement sur la case depart
                updateLeaderboard();
            }
            if(game.getCurrentPlayer().getActualCell() instanceof Jail && !game.getCurrentPlayer().isInJail()){
                game.getCurrentPlayer().toJail();
                game.nextTurn();
                checkEventTurn();
                return;
            }
            else if(game.getCurrentPlayer().getActualCell() instanceof Luck){
                view.skip.setDisable(true);
                view.timeRemain.stop();
                view.playableCardButton.setDisable(true);

                game.getCurrentPlayer().isInBonus();
                makeBonusCardBox();
            }
            else if(currentPlayer.getActualCell() instanceof Teleport){
                view.setTeleportChoiceBox(game.getCurrentPlayer());
            }
            else {
                payBeforePlay();
            }
            view.testSave.setDisable(false);
            return;
        }



        if (game.getMap().isMultipleWay(currentPlayer.getActualCell(), currentPlayer.getNotAllowedDirection())) {
            Observer obs = makeNewRoundObserver(currentPlayer);
            ArrayList<Direction> directions = game.getMap().nextDirection(currentPlayer.getActualCell(), currentPlayer.getNotAllowedDirection());
            view.showDirection(directions,currentPlayer,remainingMoves,obs,transitionsCompleted,currentPlayerIndex);
            return;
        }
        Observer obs = makeNewRoundObserver(currentPlayer);
        Direction direction = game.nextPlayersDirection(currentPlayer);
        movePlayer(currentPlayer,remainingMoves,obs,transitionsCompleted,currentPlayerIndex,direction);
    }


    //TODO:
    /*
    if choix on fait la translation
    if next direction != actual direction on fait la transition
    * */

    public void movePlayer(Player currentPlayer, int remainingMoves, Observer obs, AtomicInteger transitionsCompleted, int currentPlayerIndex, Direction direction) {
        game.movePlayer(currentPlayer,direction);
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished(e -> {


            double byY = 0;
            double byX = 0;

            switch (direction) {
                case NORTH:
                    byY = -64.5;
                    break;
                case SOUTH:
                    byY = 64.5;
                    break;
                case EAST:
                    byX = 69;
                    break;
                case WEST:
                    byX = -69;
                    break;
            }
            Coordinate startCell = new Coordinate(0,0);
            if(currentPlayer.getCoordinate().equals(startCell)){
                currentPlayer.addMoney(500);//Ajoute 500$ si il passe par la case depart
                updateLeaderboard();
            }
            movePlayerTransition(view.playerRectangles.get(currentPlayerIndex), byY, byX, obs, transitionsCompleted, remainingMoves - 1, currentPlayerIndex);
        });
        pause.play();
    }

    private void movePlayerTransition(Rectangle playerRectangle, double byY, double byX, Observer obs, AtomicInteger transitionsCompleted, int remainingMoves, int currentPlayerIndex) {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), playerRectangle);
        transition.setByY(byY);
        transition.setByX(byX);
        view.stepsLabel.setText(remainingMoves + " pas restant(s)");
        transition.setOnFinished(e -> {
            transitionsCompleted.incrementAndGet();
            checkMovePlayer(game.getPlayers().get(currentPlayerIndex), remainingMoves, transitionsCompleted, currentPlayerIndex);
        });
        transition.play();
    }

    public void applyEffect(Player player, Card card){
        player.getCards().remove(card);

        switch(card.getEffect()){
            case "famousbuilding": {
                famousBuilding(player);
                break;
            }
            case "outofprison":{
                outOfPrison(player);
                break;
            }
            case "peacetreaty":{
                peaceTreaty(player);
                break;
            }
            case "inflation":{
                inflation(player);
                break;
            }
            case "shield":{

                break;
            }
        }
    }

    /*
Méthode permettant d'appliquer les effets du Monopoly
 */
    public void famousBuilding(Player player){
        ArrayList<Building> playerBuildings = player.getProperties();
        Observer obs = makeSimpleTurnBackObserver(playerBuildings);

        view.skip.setDisable(true);

        for(Building b : playerBuildings){
            Coordinate c = b.getCoordinate();
            BorderPane cell = view.cellOnGraphics[c.getY()][c.getX()];
            BorderPane infoCell = view.informationOnGraphics[c.getY()][c.getX()];

            cell.setStyle("-fx-border-color: lightgreen; -fx-background-color: #3b3b3b;");

            infoCell.setOnMouseEntered((event) -> {
                view.descriptionLabel.setText(b.description());
                cell.setStyle("-fx-border-color: darkgreen; -fx-background-color: #3b3b3b;");
            });

            infoCell.setOnMouseExited((event) -> {
                cell.setStyle("-fx-border-color: lightgreen; -fx-background-color: #3b3b3b;");
            });

            infoCell.setOnMouseClicked((event) -> {
                b.olympicUpdatePrice();
                obs.update();
                view.skip.setDisable(false);
                view.timeRemain.setCycleCount(view.seconds+1);
                view.timeRemain.play();
            });
        }
    }

    public void outOfPrison(Player player){
        player.outOfJail();
        setNewRound();
    }

    public void peaceTreaty(Player player){
        ArrayList<Building> buildings = new ArrayList<>();
        view.skip.setDisable(true);
        for(Player p : getPlayerList()){
            if(p != player) buildings.addAll(p.getProperties());
        }
        setActionWithTakeableBuilding(buildings, player);
    }

    public void inflation(Player player){
        for(Building b : player.getProperties()){
            b.inflationPrice();
        }
    }

    public void setAllyBuildingCardPlayable(Player player){
        for(Card c : player.getCards()){
            if(!player.getProperties().isEmpty()){
            if(c instanceof FamousBuilding) c.isPlayable = true;
            if(c instanceof Inflation) c.isPlayable = true;
            if(c instanceof Shield) c.isPlayable = true;
            }
        }
    }
    public void setEnnemyBuildingCardPlayable(Player player){
        if(existEnemyBuilding(player)) {
            for (Card c : player.getCards()) {
                if (c instanceof PeaceTreaty) c.isPlayable = true;
            }
        }
    }
    public void setPrisonCardPlayable(Player player){
        if(player.isInJail()){
            for(Card c : player.getCards()) {
                if (c instanceof OutOfPrison) c.isPlayable = true;
            }
        }
    }

    public boolean existEnemyBuilding(Player player){
        for(Player p : getPlayerList()){
            if(p != player && !p.getProperties().isEmpty()) return true;
        }
        return false;
    }

    public void replaceRestaurant(Restaurant restaurant){
        game.getMap().setCell(restaurant.getCoordinate(), restaurant);
    }

    public void replaceHeritage(Heritage heritage){
        game.getMap().setCell(heritage.getCoordinate(), heritage);
    }

    public Player gameIsLose(){
        return game.gameIsFinished();
    }

    public void goToWinRound(){
        GameStates.GAMEPLAY.changeState(GameStates.WINROUND.getState());
    }

    public Image[][] addBuildingImagesToPlayer() {
        int i = game.getCurrentPlayerIndex();
        if(view.colors[i] == Color.BLUE) return Style.imagesBlue;
        else if(view.colors[i] == Color.GREEN) return Style.imagesGreen;
        else if(view.colors[i] == Color.ORANGE) return Style.imagesOrange;
        else if(view.colors[i] == Color.CYAN) return Style.imagesRed;
        else if(view.colors[i] == Color.MEDIUMPURPLE) return Style.imagesPurple;
        return null;
    }

    public Image[][] addBuildingImagesToPlayer(Player player) {
        int i = game.getPlayers().indexOf(player);
        if(view.colors[i] == Color.BLUE) return Style.imagesBlue;
        else if(view.colors[i] == Color.GREEN) return Style.imagesGreen;
        else if(view.colors[i] == Color.ORANGE) return Style.imagesOrange;
        else if(view.colors[i] == Color.CYAN) return Style.imagesRed;
        else if(view.colors[i] == Color.MEDIUMPURPLE) return Style.imagesPurple;
        return null;
    }

    /**
     * Permet de relier la map à l'interface graphique
     * @param showPane Pane contenant la map du jeu dans l'interface graphique.
     * */
    public void setMapOnGraphics(BorderPane showPane){
        StackPane mainStackPane = new StackPane();

        Map map = game.getMap();
        int width = map.getWidth();
        int height = map.getHeight();

        GridPane mapGraphics = new GridPane();
        GridPane informationGraphics = new GridPane();
        for (int i = 0; i < height; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight(100.0 / height); // Chaque ligne a une hauteur égale

            mapGraphics.getRowConstraints().add(rowConstraints);
            informationGraphics.getRowConstraints().add(rowConstraints);
        }

        for (int i = 0; i < width; i++) {
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setPercentWidth(100.0 / width); // Chaque colonne a une largeur égale
            mapGraphics.getColumnConstraints().add(colConstraints);
            informationGraphics.getColumnConstraints().add(colConstraints);
        }

        view.cellOnGraphics = new BorderPane[height][width];
        view.informationOnGraphics = new BorderPane[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                BorderPane pane = new BorderPane();
                BorderPane infoPane = new BorderPane();


                Cell cell = map.getCell(col, row);
                if(cell instanceof Road) pane.setStyle("-fx-background-color: grey;");
                if(cell instanceof Garden){
                    if(((Garden) cell).isLinked()){
                        infoPane.setOnMouseEntered(e -> view.descriptionLabel.setText(cell.description()));
                        infoPane.setOnMouseExited(e -> view.descriptionLabel.setText(Style.emptyCaseMessage));
                    }
                    pane.setStyle("-fx-background-color: #2e9531;");
                }
                if(cell instanceof Building && !(cell instanceof Heritage || cell instanceof Restaurant)){
                    if(((Building) cell).isDestroy()){
                        view.cellDestroy(cell,col,row,pane,infoPane);
                    }
                    else if (((Building) cell).getOwner()!=null){
                        view.buildingOwned((Building) cell,((Building) cell).getOwner(),pane,infoPane);
                    }
                    else{
                        Building b = (Building) cell;
                        pane.setStyle("-fx-background-color: #3a3a3a;");
                        Style.addImageToBackground(infoPane, Style.getLevel0WithVariant(b.isVariant(),0));
                    }
                }
                if(cell instanceof Start){
                    ImageView imageView = new ImageView(Style.startCell);
                    imageView.setFitHeight(64.5);
                    imageView.setFitWidth(69);
                    imageView.setImage(Style.startCell);
                    pane.setCenter(imageView);
                };
                if(cell instanceof Jail){
                    ImageView imageView = new ImageView(Style.jail);
                    imageView.setFitHeight(64.5);
                    imageView.setFitWidth(69);
                    imageView.setImage(Style.jail);

                    pane.setStyle("-fx-background-color: black;");
                    pane.setCenter(imageView);
                }
                if(cell instanceof Teleport){
                    ImageView imageView = new ImageView(Style.teleport);
                    imageView.setFitHeight(64.5);
                    imageView.setFitWidth(69);
                    imageView.setImage(Style.teleport);

                    pane.setCenter(imageView);
                };
                if(cell instanceof ToJail) {
                    ImageView imageView = new ImageView(Style.toJail);
                    imageView.setFitHeight(64.5);
                    imageView.setFitWidth(69);
                    imageView.setImage(Style.toJail);

                    pane.setCenter(imageView);
                }
                if(cell instanceof Luck){
                    ImageView imageView = new ImageView(Style.lucky);
                    imageView.setFitHeight(64.5);
                    imageView.setFitWidth(69);
                    imageView.setImage(Style.lucky);

                    pane.setCenter(imageView);
                }
                if(cell instanceof McOcean){
                    pane.setStyle("-fx-background-color: #144d29; -fx-border-color: #FFCC00; -fx-border-width: 1px;");
                    pane.setCenter(new Text("McOcean"));
                }
                if(cell instanceof Bartenders){
                    pane.setStyle("-fx-background-color: #D62700; -fx-border-color: #FF8733; -fx-border-width: 1px;");
                    pane.setCenter(new Text("Burger Queen"));
                }
                if(cell instanceof Antique){
                    pane.setStyle("-fx-background-color: #C41230; -fx-border-color: #F5D4B7; -fx-border-width: 1px;");
                    pane.setCenter(new Text("SDFC"));
                }
                if(cell instanceof EmptyRestaurant){
                    pane.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #000000; -fx-border-width: 1px;");
                    pane.setCenter(new Text("Empty"));
                }
                if(cell instanceof Taxes ){
                    ImageView imageView = new ImageView(Style.taxCell);
                    imageView.setFitHeight(64.5);
                    imageView.setFitWidth(69);
                    imageView.setImage(Style.taxCell);

                    pane.setCenter(imageView);
                }

                if(cell instanceof EmptyHeritage){
                    pane.setStyle("-fx-background-color: #FF00FF; -fx-border-color: #000000; -fx-border-width: 1px;");
                }
                else if(cell instanceof Heritage){
                    pane.setStyle(((Heritage) cell).getStyle());
                    pane.setCenter(new Text(((Heritage) cell).getName()));
                    infoPane.setOnMouseEntered(event1 -> {
                        view.descriptionLabel.setText(((Heritage) cell).getOwnerDescription(getRound()));
                    });
                    infoPane.setOnMouseExited(event1 ->{
                        view.descriptionLabel.setText("");
                    });
                    infoPane.setOnMouseClicked(null);
                }
                else if(cell == null) pane.setStyle("-fx-background-color: pink; -fx-border-color: purple; -fx-border-width: 1px;");


                infoPane.setOnMouseEntered(mouseEvent -> {
                    view.descriptionLabel.setText(cell.description());
                });
                infoPane.setOnMouseExited(mouseEvent -> {
                    view.descriptionLabel.setText("Sélectionner une case pour avoir des informations.");
                } );
                view.cellOnGraphics[row][col] = pane;
                view.informationOnGraphics[row][col] = infoPane;
                mapGraphics.add(pane, col, row);
                informationGraphics.add(infoPane, col, row);
            }
        }
        mainStackPane.getChildren().addAll(mapGraphics, informationGraphics);
        showPane.setCenter(mainStackPane);

    }

}
