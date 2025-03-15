package model.Cell.Franchise.Heritage;

import model.Cell.Building;
import model.Cell.Franchise.Franchise;
import model.Geometry.Coordinate;

import java.io.Serializable;

public abstract class Heritage extends Franchise implements Serializable {

    private static final long serialVersionUID = 1L;

    public Heritage(String name, int price, int rent, Coordinate coordinate, int moduloRound, String color) {
        super(name, price, rent, false, coordinate, moduloRound, color);
    }

    public String description(){
        String s = "Patrimoine en cours de rénovation...";
        return s;
    }

    public abstract void applyBonus(int nbRound);

    public String getStyle() {
        return "-fx-background-color: " + color + "; -fx-border-style: dashed; -fx-border-width: 5px; -fx-border-color: black;";
    }
    public abstract String bonusDescription();
}
