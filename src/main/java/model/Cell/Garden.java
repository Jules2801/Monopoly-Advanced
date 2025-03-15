package model.Cell;

import model.Geometry.Coordinate;
import model.Player;

import java.io.Serializable;
import java.util.ArrayList;

public class Garden extends AbstractCell implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int price;
    private int rent;
    private Building linkTo;
    private boolean linked;
    private ArrayList<Building> nextTo;

    public Building bestBuilding(Player player){

        Building best = null;

        for(Building building : nextTo){
            if(building.getOwner() != null && building.getOwner().equals(player)){
                if(best == null || best.getLevel() < building.getLevel()){
                    best = building;
                }
            }
        }
        return best;
    }

    public int getPrice() {
        return price;
    }

    public int getRent() {
        return rent;
    }

    public void setRent(int rent) {
        this.rent = rent;
    }

    public Building getLinkTo() {
        return linkTo;
    }

    public void setLinkTo(Building linkTo) {
        this.linkTo = linkTo;
    }

    public void setLinked(boolean linked) {
        this.linked = linked;
    }

    public ArrayList<Building> getNextTo() {
        return nextTo;
    }

    public void setNextTo(ArrayList<Building> nextTo) {
        this.nextTo = nextTo;
    }

    public Garden(Coordinate coordinate, int price) {
        super(coordinate);
        linked = false;
        nextTo = new ArrayList<>();
        this.price = price;
        this.rent = price/4;
    }

    public void linkTo(Building building){
        linkTo = building;
        linked = true;
        building.setOwnGarden(true);
    }

    public void addNextTo(Building building){
        nextTo.add(building);
    }

    public boolean isLinked() {
        return linked;
    }

    @Override
    public String description() {
        if(!linked){
            return "Ajoutez a votre immeuble un jardin";
        }
        else{
            return "Ce parc appartient à " + linkTo.getOwner().getName() + " et donne un bonus de " + getRent() + "$";
        }

    }
}
