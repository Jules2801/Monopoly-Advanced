package model.Cell.Franchise.Heritage;

import model.Geometry.Coordinate;

import java.io.Serializable;
import java.util.Random;

public class SaberTooth extends Heritage implements Serializable {

    private static final long serialVersionUID = 1L;

    public SaberTooth(Coordinate coordinate) {
        super("Saber", 4000, 1000, coordinate, 3, "#FFFEE2");
    }

    public String description(){
        String s = "Prix : " + getPrice() + "$\nLa statue représente un tigre au dent de sabre très connus pour transformer les pièces qu'il reçoit dans sa gueule en or dans les 100 prochaines années. Cette attraction touristique génère entre 1000$ et 1200$ chaque 3 rounds.";
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
    public String bonusDescription(){
        String s = getOwner().getName() + " a reçu : " + getRent() + "$!\n";
        return s;
    };
}
