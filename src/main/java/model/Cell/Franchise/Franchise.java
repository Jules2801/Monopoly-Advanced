package model.Cell.Franchise;

import model.Cell.Building;
import model.Geometry.Coordinate;

import java.io.Serializable;

public abstract class Franchise extends Building implements Serializable {

    private static final long serialVersionUID = 1L;

    public int moduloRound;
    public String color;
    public Franchise(String name, int price, int rent, boolean danger, Coordinate coordinate, int moduloRound, String color) {
        super(name, price, rent, danger, coordinate);
        this.moduloRound = moduloRound;
        this.color = color;
    }

    public String getOwnerDescription(int nbround){
        String s = "Propriétaire : " + getOwner().getName() + "\n Round(s) restant(s) : " + (moduloRound - (nbround%moduloRound)) + "\nRevenu : " + getRent() + "$";
        return s;
    }

    public abstract String description();
    public abstract String bonusDescription();
    public abstract void applyBonus(int nbRound);
    public abstract String getStyle();
}
