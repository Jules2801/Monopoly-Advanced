package model.Cell.Franchise.Heritage;

import model.Geometry.Coordinate;

import java.io.Serializable;

public class EmptyHeritage extends Heritage implements Serializable {

    private static final long serialVersionUID = 1L;

    public EmptyHeritage(Coordinate coordinate) {
        super("EmptyH", 0, 0, coordinate, -1, "#FF00FF");
    }

    @Override
    public void applyBonus(int nbRound) {

    }

    @Override
    public String getStyle() {
        return "-fx-background-color: #FF00FF; -fx-border-color: #000000; -fx-border-width: 1px;";
    }

    @Override
    public String bonusDescription() {
        return null;
    }
}
