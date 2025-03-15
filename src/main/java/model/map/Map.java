package model.map;

import model.Cell.*;
import model.Cell.Bonus.Bonus;
import model.Cell.Bonus.Jail;
import model.Cell.Bonus.Teleport;
import model.Cell.Bonus.ToJail;
import model.Cell.Franchise.Heritage.Heritage;
import model.Cell.Franchise.Restaurants.Restaurant;
import model.Direction;
import model.Player;
import model.Geometry.Coordinate;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Predicate;
import java.io.Serializable;


public class Map implements Serializable {

    private static final long serialVersionUID = 1L;


    private final int HEIGHT = 11;
    private final int WIDTH = 11;

    private AbstractCell[][] cases;

    public Map() throws FileNotFoundException {
        cases = new AbstractCell[HEIGHT][WIDTH];
        MapLoader.randomGeneration(this);

        setDirecionWithAdjacentBuildings(); // met l'attribut direction de chaque building en fonction des batiments adjacents


    }

    public AbstractCell getCell(int x, int y) {
        return cases[y][x];
    }
    public AbstractCell getCell(Coordinate c){return getCell(c.getX(), c.getY());}

    public void setCell(int x, int y, AbstractCell c) {
        cases[y][x] = c;
    }
    public void setCell(Coordinate xy, AbstractCell c){setCell(xy.getX(), xy.getY(), c);}

    public int getHeight() {
        return HEIGHT;
    }

    public int getWidth() {
        return WIDTH;
    }

    public AbstractCell[][] getMap() {
        return cases;
    }

    public boolean isValid(int x, int y){
        return (x >= 0 && y >= 0) && (x < HEIGHT && y < WIDTH);
    }
    public boolean isValid(Coordinate c){
        return isValid(c.getX(), c.getY());
    }

    public Cell nextCell(Direction direction,Coordinate c){
        switch (direction){
            case EAST -> {
                return getCell(c.getX()+1,c.getY());
            }
            case SOUTH -> {
                return getCell(c.getX(),c.getY()+1);
            }
            case WEST -> {
                return getCell(c.getX()-1,c.getY());
            }
            case NORTH -> {
                return getCell(c.getX(),c.getY()-1);
            }
            default -> {
                return getCell(c.getX(),c.getY());
            }
        }
    }

    public ArrayList<Direction> nextDirection(Cell c, Direction prevDirection){
        Coordinate base = c.getCoordinate();
        ArrayList<Direction> directions = new ArrayList<>();

        //Vérification du Nord
        Coordinate north = new Coordinate(base.getX(), base.getY()-1);
        if(isValid(north) && north.getY()>=0 && !(getCell(north) instanceof Building) &&
                prevDirection != Direction.NORTH) directions.add(Direction.NORTH);

        //Vérification de l'Est
        Coordinate east = new Coordinate(base.getX()+1, base.getY());
        if(isValid(east) &&  east.getX()<WIDTH && !(getCell(east) instanceof Building) &&
                prevDirection != Direction.EAST) directions.add(Direction.EAST);

        //Vérification du Sud
        Coordinate south = new Coordinate(base.getX(), base.getY()+1);
        if(isValid(south) && south.getY()<HEIGHT && !(getCell(south) instanceof Building) &&
                prevDirection != Direction.SOUTH) directions.add(Direction.SOUTH);

        //Vérification de l'Ouest
        Coordinate west= new Coordinate(base.getX()-1, base.getY());
        if(isValid(west) && west.getX()>=0 && !(getCell(west) instanceof Building) &&
                prevDirection != Direction.WEST) directions.add(Direction.WEST);

        return directions;
    }

    public boolean isMultipleWay(Cell c, Direction prevDirection){
        ArrayList<Direction> ways = nextDirection(c, prevDirection);
        return ways.size() > 1;
    }

    // Vérifie si il y a plusieurs joueurs sur une case
    public boolean isMultiplePlayerOnCell(Cell c){
        return c.getPlayers().size() > 1;
    }

    /*
     * Vérifie si le joueur qui est sur la case c doit payer un loyer en renvoyant les cases sur lesquelles il doit payer
     * @param c : la case sur laquelle on veut vérifier si le joeur qui est dessus doit payer un loyer
     */
    public ArrayList<Building> getAdjacentCells(Cell c, Predicate<Building> condition) {
        Coordinate co = c.getCoordinate();
        ArrayList<Building> cells = new ArrayList<>();
        checkAndAdd(cells, co.getX(), co.getY() - 1, condition);
        checkAndAdd(cells, co.getX() + 1, co.getY(), condition);
        checkAndAdd(cells, co.getX(), co.getY() + 1, condition);
        checkAndAdd(cells, co.getX() - 1, co.getY(), condition);
        return cells;
    }

    public ArrayList<Restaurant> getAdjacentsRestaurantsCells(Cell c, Predicate<Restaurant> condition){
        Coordinate co = c.getCoordinate();
        ArrayList<Restaurant> cells = new ArrayList<>();
        checkandAddRestaurants(cells, co.getX(), co.getY() - 1, condition);
        checkandAddRestaurants(cells, co.getX() + 1, co.getY(), condition);
        checkandAddRestaurants(cells, co.getX(), co.getY() + 1, condition);
        checkandAddRestaurants(cells, co.getX() - 1, co.getY(), condition);
        return cells;
    }

    public ArrayList<Heritage> getAdjacentsHeritagesCells(Cell c, Predicate<Heritage> condition){
        Coordinate co = c.getCoordinate();
        ArrayList<Heritage> cells = new ArrayList<>();
        checkandAddHeritage(cells, co.getX(), co.getY() - 1, condition);
        checkandAddHeritage(cells, co.getX() + 1, co.getY(), condition);
        checkandAddHeritage(cells, co.getX(), co.getY() + 1, condition);
        checkandAddHeritage(cells, co.getX() - 1, co.getY(), condition);
        return cells;
    }

    public boolean intersection(int x,int y){
        return  cases[y][x] instanceof Road &&
                cases[y-1][x] instanceof Road &&
                cases[y+1][x] instanceof Road  &&
                cases[y][x-1] instanceof Road  &&
                cases[y][x+1] instanceof Road ;
    }

    public boolean unlinkRoad(int x,int y){
        return  cases[y][x] instanceof Road &&
                cases[y-1][x] instanceof Building &&
                cases[y+1][x] instanceof Building  &&
                cases[y][x-1] instanceof Building  &&
                        cases[y][x+1] instanceof Building ;
    }

    public boolean yIntersection(int x,int y){
        return cases[y][x] instanceof Road &&
                (cases[y+1][x] instanceof Road||cases[y-1][x] instanceof Road) ;
    }

    public boolean yPossibleImpass(int x,int y){
        return cases[y+1][x] instanceof Building || cases[y-1][x] instanceof Building;
    }

    public boolean topMoreThan(int param,int x,int y){
        if(y<param){
            return false;
        }else{
            for(int i=y-1;i>y-param-1;i--){
                if(!(cases [i][x] instanceof Building)){
                    return false;
                }
            }
            return true;
        }

    }

    public boolean nextToRoad(int x,int y){
        return cases[y][x] instanceof Building &&
                (cases[y-1][x] instanceof Road || cases[y+1][x] instanceof Road
                        || cases[y][x+1] instanceof Road  || cases[y][x-1] instanceof Road );
    }

    public boolean cross(int x,int y){
        if(x==0){
            if(y==0){
                return  cases[y][x] instanceof Road &&
                        cases[y+1][x] instanceof Road  &&
                        cases[y][x+1] instanceof Road ;
            }else if(y==HEIGHT-1){
                return cases[y][x] instanceof Road &&
                        cases[y-1][x] instanceof Road  &&
                        cases[y][x+1] instanceof Road ;
            }else{
                return cases[y][x] instanceof Road &&
                        cases[y-1][x] instanceof Road  &&
                        cases[y+1][x] instanceof Road &&
                        cases[y][x+1] instanceof Road ;
            }
        }
        else if(x==WIDTH-1){
            if(y==0){
                return  cases[y][x] instanceof Road &&
                        cases[y+1][x] instanceof Road  &&
                        cases[y][x-1] instanceof Road ;
            }else if(y==HEIGHT-1){
                return cases[y][x] instanceof Road &&
                        cases[y-1][x] instanceof Road  &&
                        cases[y][x-1] instanceof Road ;
            }else{
                return cases[y][x] instanceof Road &&
                        cases[y-1][x] instanceof Road  &&
                        cases[y+1][x] instanceof Road &&
                        cases[y][x-1] instanceof Road ;
            }
        }

        else if(y==0){
            return cases[y][x] instanceof Road &&
                    cases[y+1][x] instanceof Road  &&
                    cases[y][x-1] instanceof Road &&
                    cases[y][x+1] instanceof Road;
        }
        else if(y==HEIGHT-1){
            return cases[y][x] instanceof Road &&
                    cases[y-1][x] instanceof Road  &&
                    cases[y][x-1] instanceof Road &&
                    cases[y][x+1] instanceof Road;
        }
        else{
            return intersection(x,y);
        }
    }



    public boolean downMoreThan(int param,int x,int y){
        if(y+param>getHeight()-1){
            return false;
        }else{
            for(int i=y+1;i<y+param;i++){
                if(!(cases [i][x] instanceof Building)){
                    return false;
                }
            }
            return true;
        }

    }


    public boolean toWide(int x,int y){
        return (cases[y][x-1] instanceof Building  && cases[y][x+1] instanceof Building)
                &&(cases[y][x+2] instanceof Building || cases[y][x-2] instanceof Building);
    }

    public boolean toHigh(int x,int y){
        return (cases[y-1][x] instanceof Building  && cases[y+1][x] instanceof Building)
                &&(cases[y+2][x] instanceof Building || cases[y-2][x] instanceof Building);
    }

    private void checkAndAdd(ArrayList<Building> cells, int x, int y, Predicate<Building> condition) {
        if (isValid(x, y) && getCell(x, y) instanceof Building && !(getCell(x, y) instanceof Restaurant)) {
            Building b = (Building) getCell(x, y);
            if (condition.test(b)) {
                cells.add(b);
            }
        }
    }

    private void checkandAddRestaurants(ArrayList<Restaurant> cells, int x, int y, Predicate<Restaurant> condition) {
        if (isValid(x, y) && getCell(x, y) instanceof Restaurant) {
            Restaurant b = (Restaurant) getCell(x, y);
            if (condition.test(b)) {
                cells.add(b);
            }
        }
    }

    private void checkandAddHeritage(ArrayList<Heritage> cells, int x, int y, Predicate<Heritage> condition){
        if (isValid(x, y) && getCell(x, y) instanceof Heritage) {
            Heritage h = (Heritage) getCell(x, y);
            if (condition.test(h)) {
                cells.add(h);
            }
        }
    }

    public ArrayList<Building> getAdjacentEnemyBuildingsToPay(Cell c, Player joueur) {
        return getAdjacentCells(c, b -> b.getOwner() != joueur && b.getOwner() != null && !(b instanceof Restaurant || b instanceof Heritage));
    }

    public ArrayList<Building> getAdjacentPlayerBuildings(Cell c, Player player) {
        return getAdjacentCells(c, b -> b.getOwner() == player && !(b instanceof Restaurant || b instanceof Heritage) && b.getLevel() < 4 && b.getUpdatePrice() <= player.getMoney());
    }


    public ArrayList<Building> getAdjacentEmptyBuildings(Cell c, int money) {
        return getAdjacentCells(c, b -> b.getOwner() == null && !(b instanceof Restaurant || b instanceof Heritage) && b.getPrice() <= money);
    }

    public ArrayList<Restaurant> getAdjacentEmptyRestaurants(Cell c){
        return getAdjacentsRestaurantsCells(c, b -> b.getOwner() == null && b instanceof Restaurant);
    }

    public ArrayList<Heritage> getAdjacentEmptyHeritage(Cell c){
        return getAdjacentsHeritagesCells(c, b -> b.getOwner() == null && b instanceof Heritage);
    }

    public ArrayList<Restaurant> getAllRestaurants(){
        ArrayList<Restaurant> fastsFoods = new ArrayList<>();
        for(int x = 0; x < HEIGHT; x++){
            for(int y = 0; y < WIDTH; y++){
                if(cases[x][y] instanceof Restaurant)  fastsFoods.add((Restaurant) cases[x][y]);
            }
        }
        return fastsFoods;
    }

    public ArrayList<Heritage> getAllHeritages(){
        ArrayList<Heritage> heritages = new ArrayList<>();
        for(int x = 0; x < HEIGHT; x++){
            for(int y = 0; y < WIDTH; y++){
                if(cases[x][y] instanceof Heritage)  heritages.add((Heritage) cases[x][y]);
            }
        }
        return heritages;
    }


    public void setDirectionToAllBuildings(){
        for(int y = 0; y < HEIGHT; y++){
            for(int x = 0; x < WIDTH; x++){
                if(cases[y][x] instanceof Building){
                    ArrayList<Direction> directions = nextDirection(cases[y][x], null);
                    // get the first direction or null if there is no direction
                    Direction direction = directions.size() > 0 ? directions.get(0) : null;
                    ((Building) cases[y][x]).setDirection(direction);


                }
            }
        }
    }

    public void setDirecionWithAdjacentBuildings(){
        for(int y = 0; y < HEIGHT; y++){
            for(int x = 0; x < WIDTH; x++){
                if(cases[y][x] instanceof Building){
                    ArrayList<Building> adjacentBuildings = getAdjacentCells(cases[y][x], b -> true);
                    ArrayList<Direction> directions = nextDirection(cases[y][x], null);
                    ArrayList<Direction> directionsToAdjacentBuildings = new ArrayList<>();
                    for(Building b : adjacentBuildings){
                        directionsToAdjacentBuildings.add(((Building) b).getDirection());
                    }
                    ArrayList<Direction> intersection = intersectionDirections(directions, directionsToAdjacentBuildings);
                    // get the first direction or don't use intersection if there is no direction
                    if (intersection.size() > 0) {
                        ((Building) cases[y][x]).setDirection(intersection.get(0));
                    } else {
                        ((Building) cases[y][x]).setDirection(directions.size() > 0 ? directions.get(0) : null);
                    }
                }
            }
        }
    }

    public ArrayList<Direction> intersectionDirections(ArrayList<Direction> a, ArrayList<Direction> b){
        ArrayList<Direction> directions = new ArrayList<>();
        for(Direction d : a){
            if(b.contains(d)) directions.add(d);
        }
        return directions;
    }
 

    //Renvoie la liste des Cellules ou on peut aller avec le teleporteur
    public ArrayList<AbstractCell> possibleCellsTeleport(){
        ArrayList<AbstractCell> possibleCells = new ArrayList<>();
        for(AbstractCell[] lines : cases){
            for (AbstractCell cell : lines){
                if(!(cell instanceof  Building || cell instanceof Teleport || cell instanceof Garden || cell instanceof Jail || cell instanceof ToJail)){
                    possibleCells.add(cell);
                }
            }
        }
        return possibleCells;
    }

    //choisis au hasard une cellule sur laquelle on peut se teleporter

    public AbstractCell randomCellTeleport(){

        ArrayList<AbstractCell> possibleCells = possibleCellsTeleport();

        Random random = new Random();
        int randomCellInt = random.nextInt(possibleCells.size());
        return possibleCells.get(randomCellInt);
    }

    private boolean notPathCell(Cell cell){
        return !(cell instanceof Bonus || cell instanceof Road);
    }


    public boolean detectGarden(int x,int y){
        return notPathCell(cases[y+1][x])  &&
                notPathCell(cases[y-1][x]) &&
                notPathCell(cases[y][x+1]) &&
                notPathCell(cases[y][x-1]) ;
    }

    public void linkedGarden(Garden garden){
        int x = garden.getCoordinate().getX();
        int y = garden.getCoordinate().getY();

        if(cases[y-1][x] instanceof  Building){
            ((Building) cases[y-1][x]).setPossibleGarden(garden);
            garden.addNextTo((Building) cases[y-1][x]);
        }
        if(cases[y+1][x] instanceof  Building){
            ((Building) cases[y+1][x]).setPossibleGarden(garden);
            garden.addNextTo((Building) cases[y+1][x]);
        }
        if(cases[y][x-1] instanceof  Building){
            ((Building) cases[y][x-1]).setPossibleGarden(garden);
            garden.addNextTo((Building) cases[y][x-1]);
        }
        if(cases[y][x+1] instanceof  Building){
            ((Building) cases[y][x+1]).setPossibleGarden(garden);
            garden.addNextTo((Building) cases[y][x+1]);
        }


    }

    public ArrayList<Garden>gardenPlayerCanBuy(Player player){
        ArrayList<Building> buildings = player.getProperties();
        ArrayList<Garden> gardens = new ArrayList<>();

        for(Building building : buildings){
            if(building.getPossibleGarden()!=null && !(building.getPossibleGarden().isLinked())){
                gardens.add(building.getPossibleGarden());
            }
        }
        return gardens;
    }
}
