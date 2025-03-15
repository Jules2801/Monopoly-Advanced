package model.Cell.Franchise.Heritage;

import model.Geometry.Coordinate;

import java.io.Serializable;
import java.util.Random;

public class Tartaros extends Heritage implements Serializable {

    private static final long serialVersionUID = 1L;


    public Tartaros(Coordinate coordinate) {
        super("Tartaros", 4000, 1000, coordinate, 3, "#7B0000");
    }

    public String description(){
        String s = "Prix : " + getPrice() + "$\nLe musée regroupe toutes les preuves, répliques, reliques et reproductions sur les démons insulaire et les créatures mystiques. Cette attraction touristique génère entre 1000$ et 1200$ chaque 3 rounds.";
        return s;
    }

    @Override
    public void applyBonus(int nbRound) {
        if(nbRound%moduloRound == 0){
            Random rand = new Random();
            getOwner().pay(getRent());
            int newRent = rand.nextInt((1200 - 1000) + 1) + 1000;
            setRent(newRent);
        }
    }

    @Override
    public String bonusDescription() {
        return "Le joueur " + getOwner().getName() + " reçoit " + getRent() + "$ du musée\n";
    }
}
