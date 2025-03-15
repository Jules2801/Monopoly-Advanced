package model;

import java.io.FileNotFoundException;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.Serializable;

import model.Cell.Building;
import model.Cell.Franchise.Heritage.Heritage;
import model.Cell.Franchise.Restaurants.Restaurant;
import model.Cell.AbstractCell;
import model.Cell.Cell;
import model.map.Map;

public class Game implements Serializable {


    private static final long serialVersionUID = 1L;

    private Map map; 
    private ArrayList<Player> players;
    private Dice dice;

    private static int numberPlayers = 0;
    private int currentPlayerIndex; 
    private GameState gameState;
    private int round = 1;
    private boolean isNewRound = true;
    private boolean eventActive = false;

    private int eventRound = 0;

    public boolean isEventActive() {
        return eventActive;
    }

    public void setEventActive(boolean eventActive) {
        this.eventActive = eventActive;
    }



    public int getEventRound() {
        return eventRound;
    }

    public void setEventRound(int eventRound) {
        this.eventRound = eventRound;
    }

    public Game(ArrayList<Player> players) throws FileNotFoundException {
        try {
            this.map = new Map(); // Tente de charger la carte
        } catch (FileNotFoundException e) {
            System.out.println("\n\n\nLa map n'a pas réussi à être chargée\n\n\n");
            e.printStackTrace();
            this.players = players;
        }
        this.players = players;
        initialize();
        this.dice = new Dice();
        this.currentPlayerIndex = 0;
        this.gameState = GameState.INITIAL; // Initialise l'état du jeu
    }

    public void initialize() {
        for (Player p : players) {
            AbstractCell startingCell = map.getCell(0, 0);
            p.setActualCell(startingCell);
            startingCell.addPlayer(p);
        }
    }

    // Ajoute un joueur au jeu
    public void addPlayer(Player player) {
        this.players.add(player);
        numberPlayers++;
    }

    public void playerLost(Player player){
        if(players.indexOf(player)<=currentPlayerIndex){
            currentPlayerIndex--;
        }
        this.players.remove(player);
        numberPlayers--;

    }

    // la liste des joueurs
    public ArrayList<Player> getPlayers() {
        return players;
    }

    // la map du jeu
    public Map getMap() {
        return map;
    }

     public Dice getDice() {
        return dice;
    }





    

    // Déplace un joueur sur le plateau
    public Direction nextPlayersDirection (Player player){
        //verifier ou il peut aller
        Direction nextDirection = map.nextDirection(player.getActualCell(), player.getNotAllowedDirection()).get(0);
        Cell nextCell = map.nextCell(nextDirection,player.getActualCell().getCoordinate());
        return nextDirection;
    }

    public void movePlayer(Player player,Direction direction){
        Cell nextCell = map.nextCell(direction,player.getActualCell().getCoordinate());
        player.setActualCell(nextCell);
        player.move(direction);
    }

    // états possibles du jeu
    public enum GameState {
        INITIAL, EN_COURS, TERMINE
    }

    public void nextTurn() {
        eventActive = false;
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

        //ON CHECK SI LE JOUEUR QUI JOUE N'A PAS PERDU

        while(getCurrentPlayer().lose()){
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }

        if (currentPlayerIndex == 0) {
            isNewRound = true;
            round++;
            eventRound++;
            if(eventRound==5){
                eventActive = true;
                eventRound = 0;
            }
        }
        else isNewRound = false;
    }

    public Event getRandomEvent(){
        Random r = new Random();
        Event[] events = Event.values();
        int disaster = r.nextInt(events.length);
        return events[disaster];
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public int getRound() {
        return round;
    }

    public ArrayList<Restaurant> getRestaurants(){return map.getAllRestaurants();}
    public ArrayList<Heritage> getHeritages(){return map.getAllHeritages();}

    public boolean isNewRound(){return isNewRound;}

    public Player getWinner(){
        return players.get(0);
    }

    //détruit certains buildings acheté par des joueurs
    public void destroyBuildings(){
        System.out.println("calculating");
        for(int y=0;y< map.getHeight();y++){
            for(int x=0;x< map.getWidth();x++){
                if(map.getCell(x,y) instanceof Building && ((Building) map.getCell(x,y)).getOwner()!=null){
                    System.out.println(map.getCell(x,y).getCoordinate());
                    Random random = new Random();
                    int destroy = random.nextInt(3);
                    if(destroy!=0){
                        ((Building) map.getCell(x,y)).destroy();
                    }
                }
            }
        }
    }

    public void kickLosers(){
        for(Player player : players){
            player.losing();
            }
    }

    public Player gameIsFinished(){
        int nbPlayerAlive = 0;
        ArrayList<Player> playersAlive = new ArrayList<>();
        for(Player p : players){
            if(!p.lose()){
                playersAlive.add(p);
            }
        }
        if(playersAlive.size() == 1) return playersAlive.get(0);
        return null;
    }

}

