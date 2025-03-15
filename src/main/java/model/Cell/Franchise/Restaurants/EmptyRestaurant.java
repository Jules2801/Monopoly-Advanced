package model.Cell.Franchise.Restaurants;

import model.Geometry.Coordinate;

import java.io.Serializable;

public class EmptyRestaurant extends Restaurant implements Serializable {

    private static final long serialVersionUID = 1L;

    public EmptyRestaurant(Coordinate c){
        super(c, null, 0, 0, "FFFFFF");
    }
    @Override
    public void applyBonus(int nbRound) {
    }

    @Override
    public boolean applyMalus(int nbRound) {
        return false;
    }

    @Override
    public String bonusDescription(){
        return null;
    };

    @Override
    public String malusDescription() {
        return null;
    }

    @Override
    public String getStyle() {
        return "-fx-background-color: #FFFFFF; -fx-border-color: #000000; -fx-border-width: 1px;";
    }
}
