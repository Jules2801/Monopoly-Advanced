package model.Cell.Bonus;

import model.Cell.AbstractCell;
import model.Cell.Cell;
import model.Geometry.Coordinate;
import model.Player;

import java.io.Serializable;

public abstract class Bonus extends AbstractCell implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    
    public Bonus(Coordinate coordinate, String name) {
        super(coordinate);
        this.name = name;
    }

    @Override
    public void setCoordinate(Coordinate coordinate) {
        super.setCoordinate(coordinate);
    }

    @Override
    public Coordinate getCoordinate() {
        return super.getCoordinate();
    }

    public String getName() {
        return name;
    }

    public abstract boolean action(Player player);// Action du Bonus


    // Méthode abstraite description() qu'on doit implémenter
    @Override
    public abstract String description();
    
}
