package model.Cell.Franchise.Restaurants;

import model.Cell.Building;
import model.Cell.Franchise.Franchise;
import model.Geometry.Coordinate;

import java.io.Serializable;

public abstract class Restaurant extends Franchise implements Serializable {

    private static final long serialVersionUID = 1L;

    public Restaurant(Coordinate coordinate, String name, int price, int royalties, String color) {
        super(name, price, royalties, false, coordinate, royalties, color);
    }

    @Override
    public String description() {
        if(getOwner() != null){
            if(isDestroy()){
                String s = "Restaurant en reconstruction...";
                return s;
            }else {
                String s = "Propriétaire :  " + getOwner().getName() + "\n" + "Revenus :  " + getRent() +"$\n" + "Rounds restants :  " + "aucun" +"€\n";
                return s;
            }

        }
        return "Fast food en construction...";
    }

    public abstract void applyBonus(int nbRound);
    public String bonusDescription(){
        String s = getOwner().getName() + " a reçu : " + getRent() + "$!\n";
        return s;
    };
    public abstract boolean applyMalus(int nbRound);
    public abstract String malusDescription();
    public abstract String getStyle();
}
