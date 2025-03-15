package model.Cell.Franchise.Restaurants;

import model.Geometry.Coordinate;
import java.util.Random;

import java.io.Serializable;

public class McOcean extends Restaurant implements Serializable {

    private static final long serialVersionUID = 1L;

    public McOcean(Coordinate coordinate) {
        super(coordinate, "McOcean", 2500, 900, "#144d29");
        this.moduloRound = 3;
    }

    @Override
    public void applyBonus(int nbRound){
        if(nbRound%moduloRound == 0){
            Random rand = new Random();
            getOwner().addMoney(getRent());
            int newRent = rand.nextInt((1000 - 900) + 1) + 900;
            setRent(newRent);
        }
    }

    @Override
    public boolean applyMalus(int nbRound) { return false;}
    public String description(){
        String s = "Prix : " + getPrice() + "$\nCe fast-food populaire dans le monde entier attire beaucoup de client dans la ville. Ce restaurant génère entre 900$ et 1000$ chaque 3 rounds.";
        return s;
    }

    @Override
    public String bonusDescription(){
        String s = getOwner().getName() + " a reçu : " + getRent() + "$!\n";
        return s;
    };

    @Override
    public String malusDescription() {
        return null;
    }

    public String getStyle(){return "-fx-background-color: #144d29; -fx-border-color: #FFCC00; -fx-border-width: 1px;";}
}
