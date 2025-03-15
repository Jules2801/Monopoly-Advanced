package model;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import model.Cards.Card;
import model.Cell.AbstractCell;
import model.Cell.Bonus.Start;
import model.Cell.Building;
import model.Cell.Cell;
import model.Geometry.Coordinate;
import model.Geometry.RealCoordinate;

import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean lost;

    private Coordinate coordinate;
    private RealCoordinate onScreenCoordinate;
    private Direction notAllowedDirection;
    private Direction direction;
    private boolean inJail;
    private ArrayList<Card> cards;
    private String name;
    private int money;
    private Cell actualCell;
    private boolean passOnStart;
    private boolean actuallyInBonus = false;
    private ArrayList<Building> properties;

    public Player(String name) {
        this.lost = false;
        this.name = name;
        this.money = 1000;
        this.properties = new ArrayList<>();
        this.cards = new ArrayList<Card>();
        this.inJail = false;
        this.coordinate = new Coordinate(0,0);
        this.onScreenCoordinate = new RealCoordinate(50,50);
        this.direction = Direction.EAST;
        this.notAllowedDirection = Direction.SOUTH;
        // Dans game on espace les joueurs en fonction de leurs nombre pour pas qu'ils se superposent
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public Direction getDirection() {
        return direction;
    }

    public RealCoordinate getOnScreenCoordinate() {
        return onScreenCoordinate;
    }

    // Ajouter de l'argent
    public void addMoney(int amount) {
        this.money += amount;
    }

    // Payer de l'argent
    public boolean pay(int amount) {
        System.out.println("J'ai: "+money+"$");
        if (money >= amount) {
            money -= amount;
            return true;
        } else {
            // Pas assez d'argent
            return false;
        }
    }

    // Payer de l'argent directement a un joueur
    public int payTo(int amount,Player player){
        if (money >= amount) {
            money -= amount;
            player.addMoney(amount);
            return 0;
        } else {
            player.addMoney(this.getMoney());
            int diff = amount - money;
            money = 0;
            return diff;
        }
    }

    public int payTaxes(int amount){
        if (money >= amount){
            money -= amount;
            return 0;
        }
        else{
            int diff = amount - money;
            money = 0;
            return diff;
        }
    }

    // Ajouter une propriété
    public void addBuilding(Building building) {
        properties.add(building);
    }

    // Ajouter un bonus
    public void addCard(Card card) {
        cards.add(card);
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public String getName() {
        return name;
    }

    public int getMoney() {
        return money;
    }
    public Direction getNotAllowedDirection(){return notAllowedDirection;}

    public ArrayList<Building> getProperties() {
        return properties;
    }

    public ArrayList<Building> getSafeBuildings(){
        ArrayList<Building> buildings = new ArrayList<>();
        for(Building b : properties){
            if(!b.isDestroy()) buildings.add(b);
        }
        return buildings;
    }

    public ArrayList<Card> getBonuses() {
        return cards;
    }

    public boolean isInJail() {
        return inJail;
    }

    public void toJail(){
        this.inJail = true;
    }
    public void outOfJail(){this.inJail = false;}

    public Cell getActualCell() {
        return actualCell;
    }

    public void setActualCell(Cell cell){
        actualCell = cell;
    }

    public void setPassOnStart(boolean passOnStart) {
        this.passOnStart = passOnStart;
    }

    public boolean isPassOnStart() {
        return passOnStart;
    }

    public boolean isActuallyInBonus(){
        return actuallyInBonus;
    }

    public void isInBonus(){
        actuallyInBonus = true;
    }
    public void notInBonusAnymore(){
        actuallyInBonus = false;
    }

    public void startAgain(){
        if(actualCell instanceof Start){
            this.addMoney(400);
        }
        else if(passOnStart){
            this.addMoney(200);
            this.passOnStart = false;
        }
    }

    public void repair(Building building){
        building.repair();
        pay(building.getReparationsPrice());
    }

    public void move(Direction direction){
        switch (direction){
            case NORTH -> {
                coordinate.setY(coordinate.getY()-1);
                notAllowedDirection = Direction.SOUTH;
            }
            case EAST -> {
                coordinate.setX(coordinate.getX()+1);
                notAllowedDirection = Direction.WEST;
            }
            case SOUTH -> {
                coordinate.setY(coordinate.getY()+1);
                notAllowedDirection = Direction.NORTH;
            }
            case WEST -> {
                coordinate.setX(coordinate.getX()-1);
                notAllowedDirection = Direction.EAST;
            }
        }
    }

    public ArrayList<Building> getDestroyBuildings(){
        ArrayList<Building> destroy = new ArrayList<>();
        for(Building building : properties){
            if(building.isDestroy()){
                destroy.add(building);
            }
        }
        return destroy;
    }

    public boolean allBuildingsMortgaged(){
        for(Building p : properties){
            if(!p.isMortgaged()) return false;
        }
        return true;
    }

    public boolean lose() {
        return lost;
    }

    public boolean losing(){

        if(money<=0 && (properties.isEmpty() || allBuildingsMortgaged())){
            this.lost = true;
            return true;
        }
        return false;
    }

    public void moveToCell(AbstractCell cell){
        setActualCell(cell);
        coordinate = new Coordinate(cell.getCoordinate().getX(),cell.getCoordinate().getY());
        notAllowedDirection = Direction.NONE;
    }

}

