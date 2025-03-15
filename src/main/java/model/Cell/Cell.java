package model.Cell;

import java.util.List;

import model.Player;
import model.Geometry.Coordinate;

public interface Cell {


    //décrit brièvement a quoi sert la case 
    public String description();
    public Coordinate getCoordinate();
    public void setCoordinate(Coordinate coordinate);
    void addPlayer(Player player);
    void removePlayer(Player player);
    List<Player> getPlayers();

    
} 
    

