package model.Cell;

import model.Geometry.Coordinate;
import model.Direction;
import model.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCell implements Cell, Serializable {

    private static final long serialVersionUID = 1L;

    protected Coordinate coordinate;
    private List<Player> players = new ArrayList<>();
    private Direction Direction;

    public AbstractCell(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    @Override
    public void addPlayer(Player player) {
        players.add(player);
    }

    @Override
    public void removePlayer(Player player) {
        players.remove(player);
    }

    @Override
    public List<Player> getPlayers() {
        return players;
    }

    @Override
    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setDirection(Direction Direction) {
        this.Direction = Direction;
    }

    public Direction getDirection() {
        return Direction;
    }
    
    @Override
    public void setCoordinate(Coordinate coordinate){ this.coordinate = coordinate; }

    // Implement other methods as abstract or concrete as needed
    public abstract String description();
}
