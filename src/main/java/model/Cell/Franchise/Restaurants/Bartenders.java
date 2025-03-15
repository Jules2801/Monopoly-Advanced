package model.Cell.Franchise.Restaurants;

import model.Geometry.Coordinate;

import java.io.Serializable;
import java.util.Random;

public class Bartenders extends Restaurant implements Serializable {

    private static final long serialVersionUID = 1L;

    public Bartenders(Coordinate coordinate){
        super(coordinate, "Bartender's", 2100, 750, "#D62700");
        this.moduloRound = 3;
    }

    @Override
    public void applyBonus(int nbRound) {
        if(nbRound%moduloRound == 0){
            Random rand = new Random();
            getOwner().addMoney(getRent());
            int newRent = rand.nextInt((850 - 750) + 1) + 750;
            setRent(newRent);
        }
    }

    @Override
    public boolean applyMalus(int nbRound) {
        if(nbRound%2 == 0){
            Random rand = new Random();
            int prob = rand.nextInt(101);
            if(prob%4 == 0){
                //Trouver un moyen d'afficher un popup
                getOwner().addMoney(-(getRent()/2));
                return true;
            }
        }
        return false;
    }

    public String description(){
        String s = "Prix : " + getPrice() + "$\nCe restaurant fait un carton en ville récement. Ce restaurant génère entre 750$ et 850$ chaque 3 rounds.";
        return s;
    }

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
        return "-fx-background-color: #D62700; -fx-border-color: #FF8733; -fx-border-width: 1px;";
    }
}
