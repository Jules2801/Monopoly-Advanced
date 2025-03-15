package model.Cell.Franchise.Restaurants;

import model.Geometry.Coordinate;

import java.io.Serializable;
import java.util.Random;

public class Antique extends Restaurant implements Serializable {

    private static final long serialVersionUID = 1L;

    public Antique(Coordinate coordinate){
        super(coordinate, "L'Antique", 2000, 700, "#C41230");
        this.moduloRound = 3;
    }

    @Override
    public void applyBonus(int nbRound) {
        if(nbRound % moduloRound == 0){
            Random rand = new Random();
            getOwner().pay(getRent());
            int newRent = rand.nextInt((800 - 700) + 1) + 700;
            setRent(newRent);
        }
    }

    public boolean applyMalus(int nbRound) {
        if(nbRound%3 == 0){
            Random rand = new Random();
            int prob = rand.nextInt(101);
            if(prob%5 == 0){
                //Trouver un moyen d'afficher un popup
                getOwner().addMoney(-(getRent()/2));
                return true;
            }
        }
        return false;
    }

    public String description(){
        String s = "Prix : " + getPrice() + "$\nCe restaurant à l'atmosphère convivial est très apprécié dans la ville. Ce restaurant génère entre 700$ et 800$ chaque 3 rounds.";
        return s;
    }

    @Override
    public String bonusDescription(){
        String s = getOwner().getName() + " a reçu : " + getRent() + "$!\n";
        return s;
    };

    @Override
    public String malusDescription() {
        String[] possibleWay = {
                "La friteuse est tombé en panne ! Il faut la réparer immédiatement !",
                "Un client est tombé sur un burger très pimené et vous l'ammadouer pour ne pas aller en procès.",
                "On a découvert une souris morte dans une friteuse, il faut appeler un dératiseur.",
                "La mafia vient réclamer sont dû. Le patron est dans des affaires vraiment louche..."
        };
        Random rand = new Random();
        String way = possibleWay[rand.nextInt(4)];
        System.out.println("Le texte qui sera affiché à l'écran est : " + way);
        return way;
    }

    public String getStyle(){
        return "-fx-background-color: #C41230; -fx-border-color: #F5D4B7; -fx-border-width: 1px;";
    }
}
